package com.kuaidao.manageweb.controller.client;

import com.kuaidao.callcenter.dto.LcClient.*;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.client.LcClientFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/client/lcClient")
public class LcClientController {

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private LcClientFeignClient lcClientFeignClient;

    /**
     *  跳转乐创页面
     */
//    @RequiresPermissions("callcenter:lcClient:view")
    @RequestMapping("/toLcClientPage")
    public String toLcClientPage(HttpServletRequest request) {
        List<OrganizationDTO> orgList = new ArrayList<>();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(String.valueOf(curLoginUser.getOrgId()));
            if(curOrgGroupByOrgId!=null) {
                orgList.add(curOrgGroupByOrgId);
            }
            request.setAttribute("orgList", orgList);
        } else {
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            JSONResult<List<OrganizationRespDTO>> orgListJr = organizationFeignClient.queryOrgByParam(queryDTO);
            if (orgListJr == null || !JSONResult.SUCCESS.equals(orgListJr.getCode())) {
                log.error("跳转乐创坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/lcClientPage";
    }


//    @RequiresPermissions("callcenter:lcClient:view")
    @PostMapping("/listLcClientPage")
    @ResponseBody
    public JSONResult<PageBean<LcClientRespDTO>> listLcClientPage(
            @RequestBody LcClientQueryDTO queryClientDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            queryClientDTO.setOrgId(orgId);
        }
        return lcClientFeignClient.listLcClientPage(queryClientDTO);
    }


//    @RequiresPermissions("callcenter:lcClient:add")
    @PostMapping("/saveLcClient")
    @ResponseBody
    public JSONResult<Boolean> saveLcClient(@Valid @RequestBody AddOrUpdateLcClientDTO reqDTO,
                                              BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        Long userId = reqDTO.getUserId();
        if(null != userId){
            JSONResult jsonResult = assignmentOrg(reqDTO, userId);
            if(!JSONResult.SUCCESS.equals(jsonResult.getCode())){
                return jsonResult;
            }
        }
        JSONResult<Boolean> booleanJSONResult = lcClientFeignClient.saveLcClient(reqDTO);
        return booleanJSONResult;
    }


//    @RequiresPermissions("callcenter:lcClient:edit")
    @PostMapping("/updateLcClient")
    @ResponseBody
    public JSONResult<Boolean> updateLcClient(@Valid @RequestBody AddOrUpdateLcClientDTO reqDTO,
                                                BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = reqDTO.getId();
        if (id == null) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        Long userId = reqDTO.getUserId();
        if(null != userId){
            JSONResult jsonResult = assignmentOrg(reqDTO, userId);
            if(!JSONResult.SUCCESS.equals(jsonResult.getCode())){
                return jsonResult;
            }
        }
        return lcClientFeignClient.updateLcClient(reqDTO);
    }

    @PostMapping("/queryLcClientById")
    @ResponseBody
    public JSONResult<LcClientRespDTO> queryLcClientById(@RequestBody IdEntity idEntity)
            throws Exception {
        String id = idEntity.getId();
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            return new JSONResult<LcClientRespDTO>().fail(
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        JSONResult<LcClientRespDTO> lcClientRespDTOJSONResult = lcClientFeignClient.queryLcClientById(idEntity);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            if(JSONResult.SUCCESS.equals(lcClientRespDTOJSONResult.getCode()) && lcClientRespDTOJSONResult.getData()!=null){
                LcClientRespDTO lcClient = lcClientRespDTOJSONResult.getData();
                lcClient.setIsDxzj(true);
            }
        }
        return lcClientRespDTOJSONResult;
    }

//    @RequiresPermissions("callcenter:lcClient:delete")
    @PostMapping("/deleteLcClient")
    @ResponseBody
    public JSONResult<Boolean> deleteLcClient(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        log.info("userId{{}},delete lc client,idList{{}}", curLoginUser.getId(), idListReq);

        return lcClientFeignClient.deleteLcClient(idListReq);
    }


//    @RequiresPermissions("callcenter:lcClient:import")
    @PostMapping("/uploadLcClient")
    @ResponseBody
    public JSONResult uploadLcClient(@RequestParam("file") MultipartFile file) throws Exception {
        // 获取当前的用户信息
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();

        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        log.info("userid{{}} lc_client upload size:{{}}", userId, excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),
                    SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 5000) {
            log.error("上传乐创坐席,大于5000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),
                    "导入数据过多，已超过5000条！");
        }

        // 存放合法的数据
        List<ImportLcClientDTO> dataList = new ArrayList<ImportLcClientDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ImportLcClientDTO rowDto = new ImportLcClientDTO();
            for (int j = 0; j < rowList.size(); j++) {
                String value = (String) rowList.get(j);
                if (j == 0) {
                    rowDto.setName(value);
                } else if (j == 1) {
                    // 主叫手机号
                    rowDto.setCaller(value);
                } else if (j == 2) {
                    // 坐席编号
                    rowDto.setCallKey(value);
                }else if (j == 3) {
                    // 号码所属公司
                    rowDto.setNumberAttributionCompany(value);
                }
            }
            dataList.add(rowDto);
        }
        redisTemplate.opsForValue().set(Constants.LC_CLIENT_KEY + userId, dataList, 10 * 60,
                TimeUnit.SECONDS);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("num", dataList.size());
        resMap.put("data", dataList);
        return new JSONResult<>().success(resMap);
    }

//    @RequiresPermissions("callcenter:lcClient:import")
    @ResponseBody
    @PostMapping("/submitLcClientData")
    public JSONResult<List<ImportLcClientDTO>> submitLcClientData() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();
        List<ImportLcClientDTO> dataList = (List<ImportLcClientDTO>) redisTemplate.opsForValue().get(Constants.LC_CLIENT_KEY + userId);
        UploadLcClientDataDTO<ImportLcClientDTO> reqClientDataDTO = new UploadLcClientDataDTO<ImportLcClientDTO>();
        reqClientDataDTO.setCreateUser(userId);
        reqClientDataDTO.setList(dataList);
        // remove redis 临时数据
        redisTemplate.delete(Constants.LC_CLIENT_KEY + userId);

        JSONResult<List<ImportLcClientDTO>> uploadTrClientData = lcClientFeignClient.uploadLcClientData(reqClientDataDTO);
        return uploadTrClientData;
    }

    /**
     * 七陌 外呼
     */
    @PostMapping("/lcOutboundCall")
    @ResponseBody
    public JSONResult lcOutboundCall(@RequestBody LcClientOutboundDTO  callDTO){
        return null;
    }

    /**
     * 获取当前 orgId所在的组织
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(String.valueOf(orgId));
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            log.error("getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }

    /**
     * 赋值org
     */
    private JSONResult assignmentOrg(AddOrUpdateLcClientDTO reqDTO,Long userId){
        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(userId);
        JSONResult<UserInfoDTO> userInfoJr = userInfoFeignClient.get(idEntityLong);
        String code = userInfoJr.getCode();
        if(!JSONResult.SUCCESS.equals(code)){
            return new JSONResult<Boolean>().fail(code,userInfoJr.getMsg());
        }
        UserInfoDTO userInfo = userInfoJr.getData();
        reqDTO.setOrgId(userInfo.getOrgId());
        return new JSONResult().success(reqDTO);
    }
}
