package com.kuaidao.manageweb.controller.client;

import com.kuaidao.callcenter.dto.sobotClient.*;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.client.SobotClientFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
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
@RequestMapping("/client/sobotClient")
public class SobotClientController {

    @Autowired
    OrganizationFeignClient organizationFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private SobotClientFeignClient sobotClientFeignClient;
    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    public static final String CLIENT_NO = "clientNo";
    public static final String OKCC_ACCOUNT = "okccAccount";
    public static final String OKCC_PASSWORD = "okccPassword";

    //当前坐席登录方式（1-网络电话；2-sip话机；3-手机）',
    private static final String CALL_WAY_1 = "1";


    /**
     *  跳转赤晨页面
     */
    @RequiresPermissions("callcenter:sobotClient:view")
    @RequestMapping("/toSobotClientPage")
    public String toSobotClientPage(HttpServletRequest request) {
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
                log.error("跳转智齿坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/sobotClientPage";
    }


    @RequiresPermissions("callcenter:sobotClient:view")
    @PostMapping("/listSobotClientPage")
    @ResponseBody
    public JSONResult<PageBean<SobotClientRespDTO>> listSobotClientPage(@RequestBody SobotClientQueryDTO queryClientDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            queryClientDTO.setOrgId(orgId);
        }
        return sobotClientFeignClient.listSobotClientPage(queryClientDTO);
    }


    @RequiresPermissions("callcenter:sobotClient:add")
    @PostMapping("/saveSobotClient")
    @ResponseBody
    public JSONResult<Boolean> saveSobotClient(@Valid @RequestBody AddOrUpdateSobotClientDTO reqDTO, BindingResult result) throws Exception {
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
        reqDTO.setCallWay(CALL_WAY_1);
        JSONResult<Boolean> booleanJSONResult = sobotClientFeignClient.saveSobotClient(reqDTO);
        return booleanJSONResult;
    }


    @RequiresPermissions("callcenter:sobotClient:edit")
    @PostMapping("/updateSobotClient")
    @ResponseBody
    public JSONResult<Boolean> updateSobotClient(@Valid @RequestBody AddOrUpdateSobotClientDTO reqDTO,BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = reqDTO.getId();
        if (id == null) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        Long userId = reqDTO.getUserId();
        if(null != userId){
            JSONResult jsonResult = assignmentOrg(reqDTO, userId);
            if(!JSONResult.SUCCESS.equals(jsonResult.getCode())){
                return jsonResult;
            }
        }
        return sobotClientFeignClient.updateSobotClient(reqDTO);
    }

    @PostMapping("/querySobotClientById")
    @ResponseBody
    public JSONResult<SobotClientRespDTO> querySobotClientById(@RequestBody IdEntity idEntity)throws Exception {
        String id = idEntity.getId();
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            return new JSONResult<SobotClientRespDTO>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        JSONResult<SobotClientRespDTO> sobotClientRespDTOJSONResult = sobotClientFeignClient.querySobotClientById(idEntity);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            if(JSONResult.SUCCESS.equals(sobotClientRespDTOJSONResult.getCode()) && sobotClientRespDTOJSONResult.getData()!=null){
                SobotClientRespDTO sobotClient = sobotClientRespDTOJSONResult.getData();
                sobotClient.setIsDxzj(true);
            }
        }
        return sobotClientRespDTOJSONResult;
    }

    @RequiresPermissions("callcenter:sobotClient:delete")
    @PostMapping("/deleteSobotClient")
    @ResponseBody
    public JSONResult<Boolean> deleteSobotClient(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        log.info("userId{{}},delete sobot client,idList{{}}", curLoginUser.getId(), idListReq);

        return sobotClientFeignClient.deleteSobotClient(idListReq);
    }


    @RequiresPermissions("callcenter:sobotClient:import")
    @PostMapping("/uploadSobotClient")
    @ResponseBody
    public JSONResult uploadSobotClient(@RequestParam("file") MultipartFile file) throws Exception {
        // 获取当前的用户信息
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();

        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        log.info("userid{{}} sobot_client upload size:{{}}", userId, excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 5000) {
            log.error("上传智齿坐席,大于5000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),"导入数据过多，已超过5000条！");
        }

        // 存放合法的数据
        List<ImportSobotClientDTO> dataList = new ArrayList<ImportSobotClientDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ImportSobotClientDTO rowDto = new ImportSobotClientDTO();
            for (int j = 0; j < rowList.size(); j++) {
                String value = (String) rowList.get(j);
                if (j == 0) {
                    rowDto.setName(value);
                } else if (j == 1) {
                    // 登录坐席号
                    rowDto.setLoginClient(value);
                } else if (j == 2) {
                    // 坐席编号
                    rowDto.setClientNo(value);
                } else if (j == 3) {
                    // 号码所属公司
                    rowDto.setNumberAttributionCompany(value);
                }else if (j == 4) {
                    // 坐席归属地
                    rowDto.setAttribution(value);
                }else if (j == 5) {
                    // 呼叫中心应用id
                    rowDto.setClientId(value);
                }else if (j == 6) {
                    // 应用密钥
                    rowDto.setClientSecret(value);
                }else if (j == 7) {
                    // 公司id
                    rowDto.setCompanyId(value);
                }
            }
            dataList.add(rowDto);
        }
        redisTemplate.opsForValue().set(Constants.SOBOT_CLIENT_KEY + userId, dataList, 10 * 60,TimeUnit.SECONDS);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("num", dataList.size());
        resMap.put("data", dataList);
        return new JSONResult<>().success(resMap);
    }

    @RequiresPermissions("callcenter:sobotClient:import")
    @ResponseBody
    @PostMapping("/submitSobotClientData")
    public JSONResult<List<ImportSobotClientDTO>> submitSobotClientData() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();
        List<ImportSobotClientDTO> dataList = (List<ImportSobotClientDTO>) redisTemplate.opsForValue().get(Constants.SOBOT_CLIENT_KEY + userId);
        UploadSobotClientDataDTO<ImportSobotClientDTO> reqClientDataDTO = new UploadSobotClientDataDTO<ImportSobotClientDTO>();
        reqClientDataDTO.setCreateUser(userId);
        reqClientDataDTO.setList(dataList);
        // remove redis 临时数据
        redisTemplate.delete(Constants.SOBOT_CLIENT_KEY + userId);

        JSONResult<List<ImportSobotClientDTO>> uploadSobotClientData = sobotClientFeignClient.uploadSobotClientData(reqClientDataDTO);
        return uploadSobotClientData;
    }

    @PostMapping("/sobotLogin")
    @ResponseBody
    public JSONResult sobotLogin(@RequestBody SobotLoginReqDTO reqDTO ) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long userId = curLoginUser.getId();
        if(StringUtils.isBlank(reqDTO.getLoginClient())){
            return new JSONResult<>().fail(SysErrorCodeEnum.LOGIN_CLIENT_NOT_EXIT.getCode(),SysErrorCodeEnum.LOGIN_CLIENT_NOT_EXIT.getMessage());
        }
        SobotClientQueryDTO sobotClientQueryDTO = new SobotClientQueryDTO();
        sobotClientQueryDTO.setLoginClient(reqDTO.getLoginClient());
        sobotClientQueryDTO.setUserId(userId);
        JSONResult<SobotClientRespDTO> sobotClientReq = sobotClientFeignClient.querySobotClient(sobotClientQueryDTO);

        if(JSONResult.SUCCESS.equals(sobotClientReq.getCode())) {
            SobotClientRespDTO sobotClientRespDTO = sobotClientReq.getData();
            if(null == sobotClientRespDTO) {
                return new JSONResult<>().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该坐席号不属于您");
            }
            if(StringUtils.isBlank(sobotClientRespDTO.getClientNo())){
                return new JSONResult<>().fail(SysErrorCodeEnum.CLIENT_NO_NOT_EXIT.getCode(),SysErrorCodeEnum.CLIENT_NO_NOT_EXIT.getMessage());
            }
            return new JSONResult<>().success(true);
        }
        return new JSONResult<>().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }

    @PostMapping("/sobotLogout")
    @ResponseBody
    public JSONResult sobotLogout(HttpServletRequest request) {
        Session session = SecurityUtils.getSubject().getSession();
        if(StringUtils.isBlank((String)session.getAttribute(CLIENT_NO))) {
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_UNLOGINCLIENT_FAIL.getCode(),SysErrorCodeEnum.ERR_UNLOGINCLIENT_FAIL.getMessage());
        }
        return new JSONResult<>().success(true);
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
            log.error("获取当前 orgId所在的组织,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }

    /**
     * 赋值org
     */
    private JSONResult assignmentOrg(AddOrUpdateSobotClientDTO reqDTO,Long userId){
        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(userId);
        JSONResult<UserInfoDTO> userInfoJr = userInfoFeignClient.get(idEntityLong);
        String code = userInfoJr.getCode();
        if(!JSONResult.SUCCESS.equals(code)){
            log.error("智齿赋值org失败，失败原因{}",userInfoJr.getMsg());
            return new JSONResult<Boolean>().fail(code,userInfoJr.getMsg());
        }
        UserInfoDTO userInfo = userInfoJr.getData();
        reqDTO.setOrgId(userInfo.getOrgId());
        return new JSONResult().success(reqDTO);
    }
}
