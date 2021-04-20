package com.kuaidao.manageweb.controller.client;

import com.kuaidao.callcenter.dto.OkccClient.*;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.client.OkccClientFeignClient;
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
@RequestMapping("/client/okccClient")
public class OkccClientController {

    @Autowired
    OrganizationFeignClient organizationFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private OkccClientFeignClient okccClientFeignClient;
    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    public static final String CLIENT_NO = "clientNo";
    public static final String OKCC_ACCOUNT = "okccAccount";
    public static final String OKCC_PASSWORD = "okccPassword";


    /**
     *  跳转赤晨页面
     */
    @RequiresPermissions("callcenter:okccClient:view")
    @RequestMapping("/toOkccClientPage")
    public String toOkccClientPage(HttpServletRequest request) {
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
                log.error("跳转赤晨坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/okccClientPage";
    }


    @RequiresPermissions("callcenter:OkccClient:view")
    @PostMapping("/listOkccClientPage")
    @ResponseBody
    public JSONResult<PageBean<OkccClientRespDTO>> listOkccClientPage(@RequestBody OkccClientQueryDTO queryClientDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            queryClientDTO.setOrgId(orgId);
        }
        return okccClientFeignClient.listOkccClientPage(queryClientDTO);
    }


    @RequiresPermissions("callcenter:okccClient:add")
    @PostMapping("/saveOkccClient")
    @ResponseBody
    public JSONResult<Boolean> saveOkccClient(@Valid @RequestBody AddOrUpdateOkccClientDTO reqDTO, BindingResult result) throws Exception {
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
        JSONResult<Boolean> booleanJSONResult = okccClientFeignClient.saveOkccClient(reqDTO);
        return booleanJSONResult;
    }


    @RequiresPermissions("callcenter:okccClient:edit")
    @PostMapping("/updateOkccClient")
    @ResponseBody
    public JSONResult<Boolean> updateOkccClient(@Valid @RequestBody AddOrUpdateOkccClientDTO reqDTO,BindingResult result) throws Exception {
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
        return okccClientFeignClient.updateOkccClient(reqDTO);
    }

    @PostMapping("/queryOkccClientById")
    @ResponseBody
    public JSONResult<OkccClientRespDTO> queryOkccClientById(@RequestBody IdEntity idEntity)throws Exception {
        String id = idEntity.getId();
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            return new JSONResult<OkccClientRespDTO>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        JSONResult<OkccClientRespDTO> okccClientRespDTOJSONResult = okccClientFeignClient.queryOkccClientById(idEntity);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            if(JSONResult.SUCCESS.equals(okccClientRespDTOJSONResult.getCode()) && okccClientRespDTOJSONResult.getData()!=null){
                OkccClientRespDTO okccClient = okccClientRespDTOJSONResult.getData();
                okccClient.setIsDxzj(true);
            }
        }
        return okccClientRespDTOJSONResult;
    }

    @RequiresPermissions("callcenter:okccClient:delete")
    @PostMapping("/deleteOkccClient")
    @ResponseBody
    public JSONResult<Boolean> deleteOkccClient(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        log.info("userId{{}},delete okcc client,idList{{}}", curLoginUser.getId(), idListReq);

        return okccClientFeignClient.deleteOkccClient(idListReq);
    }


    @RequiresPermissions("callcenter:okccClient:import")
    @PostMapping("/uploadOkccClient")
    @ResponseBody
    public JSONResult uploadOkccClient(@RequestParam("file") MultipartFile file) throws Exception {
        // 获取当前的用户信息
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();

        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        log.info("userid{{}} okcc_client upload size:{{}}", userId, excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 5000) {
            log.error("上传赤晨坐席,大于5000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),"导入数据过多，已超过5000条！");
        }

        // 存放合法的数据
        List<ImportOkccClientDTO> dataList = new ArrayList<ImportOkccClientDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ImportOkccClientDTO rowDto = new ImportOkccClientDTO();
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
                    // 赤晨外呼开户账号
                    rowDto.setOkccAccount(value);
                }else if (j == 6) {
                    // 赤晨外呼账号密码
                    rowDto.setOkccPassword(value);
                }
            }
            dataList.add(rowDto);
        }
        redisTemplate.opsForValue().set(Constants.OKCC_CLIENT_KEY + userId, dataList, 10 * 60,TimeUnit.SECONDS);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("num", dataList.size());
        resMap.put("data", dataList);
        return new JSONResult<>().success(resMap);
    }

    @RequiresPermissions("callcenter:okccClient:import")
    @ResponseBody
    @PostMapping("/submitOkccClientData")
    public JSONResult<List<ImportOkccClientDTO>> submitOkccClientData() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();
        List<ImportOkccClientDTO> dataList = (List<ImportOkccClientDTO>) redisTemplate.opsForValue().get(Constants.OKCC_CLIENT_KEY + userId);
        UploadOkccClientDataDTO<ImportOkccClientDTO> reqClientDataDTO = new UploadOkccClientDataDTO<ImportOkccClientDTO>();
        reqClientDataDTO.setCreateUser(userId);
        reqClientDataDTO.setList(dataList);
        // remove redis 临时数据
        redisTemplate.delete(Constants.OKCC_CLIENT_KEY + userId);

        JSONResult<List<ImportOkccClientDTO>> uploadOkccClientData = okccClientFeignClient.uploadOkccClientData(reqClientDataDTO);
        return uploadOkccClientData;
    }

    @PostMapping("/okccLogin")
    @ResponseBody
    public JSONResult okccLogin(@RequestBody OkccLoginReqDTO reqDTO ) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long userId = curLoginUser.getId();
        if(StringUtils.isBlank(reqDTO.getLoginClient())){
            return new JSONResult<>().fail(SysErrorCodeEnum.LOGIN_CLIENT_NOT_EXIT.getCode(),SysErrorCodeEnum.LOGIN_CLIENT_NOT_EXIT.getMessage());
        }
        OkccClientQueryDTO okccClientQueryDTO = new OkccClientQueryDTO();
        okccClientQueryDTO.setLoginClient(reqDTO.getLoginClient());
        okccClientQueryDTO.setUserId(userId);
        JSONResult<OkccClientRespDTO> okccClientReq = okccClientFeignClient.queryOkccClient(okccClientQueryDTO);

        if(JSONResult.SUCCESS.equals(okccClientReq.getCode())) {
            OkccClientRespDTO okccClientRespDTO = okccClientReq.getData();
            if(null == okccClientRespDTO) {
                return new JSONResult<>().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该坐席号不属于您");
            }
            if(StringUtils.isBlank(okccClientRespDTO.getClientNo())){
                return new JSONResult<>().fail(SysErrorCodeEnum.CLIENT_NO_NOT_EXIT.getCode(),SysErrorCodeEnum.CLIENT_NO_NOT_EXIT.getMessage());
            }
            if(StringUtils.isBlank(okccClientRespDTO.getOkccAccount())){
                return new JSONResult<>().fail(SysErrorCodeEnum.LOGIN_OKCC_ACCOUNT_NOT_EXIT.getCode(),SysErrorCodeEnum.LOGIN_OKCC_ACCOUNT_NOT_EXIT.getMessage());
            }
            if(StringUtils.isBlank(okccClientRespDTO.getOkccPassword())){
                return new JSONResult<>().fail(SysErrorCodeEnum.LOGIN_OKCC_PASSWORD_NOT_EXIT.getCode(),SysErrorCodeEnum.LOGIN_OKCC_PASSWORD_NOT_EXIT.getMessage());
            }
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute(CLIENT_NO,okccClientRespDTO.getClientNo());
            session.setAttribute(OKCC_ACCOUNT,okccClientRespDTO.getOkccAccount());
            session.setAttribute(OKCC_PASSWORD,okccClientRespDTO.getOkccPassword());
            return new JSONResult<>().success(true);
        }
        return new JSONResult<>().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }

    @PostMapping("/okccLogout")
    @ResponseBody
    public JSONResult okccLogout(HttpServletRequest request) {
        Session session = SecurityUtils.getSubject().getSession();
        if(StringUtils.isBlank((String)session.getAttribute(CLIENT_NO))) {
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_UNLOGINCLIENT_FAIL.getCode(),SysErrorCodeEnum.ERR_UNLOGINCLIENT_FAIL.getMessage());
        }
        session.removeAttribute(CLIENT_NO);
        session.removeAttribute(OKCC_ACCOUNT);
        session.removeAttribute(OKCC_PASSWORD);
        return new JSONResult<>().success(true);
    }

    /**
     * 赤晨 外呼
     */
    @PostMapping("/okccOutboundCall")
    @ResponseBody
    public JSONResult okccOutboundCall(@RequestBody OkccClientOutboundDTO  callDTO){
        String customerPhoneNumber = callDTO.getCustomerPhone();
        if(StringUtils.isBlank(customerPhoneNumber)) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),"客户手机号为null");
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Session session = SecurityUtils.getSubject().getSession();
        if(null == session.getAttribute(CLIENT_NO) || null == session.getAttribute(OKCC_ACCOUNT) || null == session.getAttribute(OKCC_PASSWORD)){
            return new JSONResult().fail(SysErrorCodeEnum.CALL_NOT_EXIT.getCode(),SysErrorCodeEnum.CALL_NOT_EXIT.getMessage());
        }
        callDTO.setClientNo(String.valueOf(session.getAttribute(CLIENT_NO)));
        callDTO.setCustomerPhone(customerPhoneNumber);
        callDTO.setAccountId(curLoginUser.getId());
        callDTO.setOrgId(curLoginUser.getOrgId());
        callDTO.setOkccAccount(String.valueOf(session.getAttribute(OKCC_ACCOUNT)));
        callDTO.setOkccPassword(String.valueOf(session.getAttribute(OKCC_PASSWORD)));
        return okccClientFeignClient.okccOutbound(callDTO);
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
    private JSONResult assignmentOrg(AddOrUpdateOkccClientDTO reqDTO,Long userId){
        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(userId);
        JSONResult<UserInfoDTO> userInfoJr = userInfoFeignClient.get(idEntityLong);
        String code = userInfoJr.getCode();
        if(!JSONResult.SUCCESS.equals(code)){
            log.error("赤晨赋值org失败，失败原因{}",userInfoJr.getMsg());
            return new JSONResult<Boolean>().fail(code,userInfoJr.getMsg());
        }
        UserInfoDTO userInfo = userInfoJr.getData();
        reqDTO.setOrgId(userInfo.getOrgId());
        return new JSONResult().success(reqDTO);
    }
}
