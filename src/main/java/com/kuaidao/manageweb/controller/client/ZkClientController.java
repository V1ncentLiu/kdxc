package com.kuaidao.manageweb.controller.client;

import com.kuaidao.callcenter.dto.ZkClient.*;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.client.ZkClientFeignClient;
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
@RequestMapping("/client/zkClient")
public class ZkClientController {

    @Autowired
    OrganizationFeignClient organizationFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private ZkClientFeignClient zkClientFeignClient;
    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    public static final String CLIENT_NO = "clientNo";
    public static final String REQUEST_TYPE = "requestType";

    public static final String SIP = "2";
    public static final String SIP_NAME = "sip外呼";
    public static final String MOBILE = "1";
    public static final String MOBILE_NAME = "绑定手机外呼";


    /**
     *  跳转中科页面
     */
//    @RequiresPermissions("callcenter:zkClient:view")
    @RequestMapping("/toZkClientPage")
    public String toZkClientPage(HttpServletRequest request) {
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
                log.error("跳转中科坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/zkClientPage";
    }


//    @RequiresPermissions("callcenter:zkClient:view")
    @PostMapping("/listZkClientPage")
    @ResponseBody
    public JSONResult<PageBean<ZkClientRespDTO>> listZkClientPage(@RequestBody ZkClientQueryDTO queryClientDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            queryClientDTO.setOrgId(orgId);
        }
        return zkClientFeignClient.listZkClientPage(queryClientDTO);
    }


//    @RequiresPermissions("callcenter:zkClient:add")
    @PostMapping("/saveZkClient")
    @ResponseBody
    public JSONResult<Boolean> saveZkClient(@Valid @RequestBody AddOrUpdateZkClientDTO reqDTO,BindingResult result) throws Exception {
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
        JSONResult<Boolean> booleanJSONResult = zkClientFeignClient.saveZkClient(reqDTO);
        return booleanJSONResult;
    }


//    @RequiresPermissions("callcenter:zkClient:edit")
    @PostMapping("/updateZkClient")
    @ResponseBody
    public JSONResult<Boolean> updateZkClient(@Valid @RequestBody AddOrUpdateZkClientDTO reqDTO,BindingResult result) throws Exception {
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
        return zkClientFeignClient.updateZkClient(reqDTO);
    }

    @PostMapping("/queryZkClientById")
    @ResponseBody
    public JSONResult<ZkClientRespDTO> queryZkClientById(@RequestBody IdEntity idEntity)throws Exception {
        String id = idEntity.getId();
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            return new JSONResult<ZkClientRespDTO>().fail(
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        JSONResult<ZkClientRespDTO> zkClientRespDTOJSONResult = zkClientFeignClient.queryZkClientById(idEntity);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            if(JSONResult.SUCCESS.equals(zkClientRespDTOJSONResult.getCode()) && zkClientRespDTOJSONResult.getData()!=null){
                ZkClientRespDTO zkClient = zkClientRespDTOJSONResult.getData();
                zkClient.setIsDxzj(true);
            }
        }
        return zkClientRespDTOJSONResult;
    }

//    @RequiresPermissions("callcenter:zkClient:delete")
    @PostMapping("/deleteZkClient")
    @ResponseBody
    public JSONResult<Boolean> deleteZkClient(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        log.info("userId{{}},delete zk client,idList{{}}", curLoginUser.getId(), idListReq);

        return zkClientFeignClient.deleteZkClient(idListReq);
    }


//    @RequiresPermissions("callcenter:zkClient:import")
    @PostMapping("/uploadZkClient")
    @ResponseBody
    public JSONResult uploadZkClient(@RequestParam("file") MultipartFile file) throws Exception {
        // 获取当前的用户信息
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();

        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        log.info("userid{{}} zk_client upload size:{{}}", userId, excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),
                    SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 5000) {
            log.error("上传中科坐席,大于5000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),
                    "导入数据过多，已超过5000条！");
        }

        // 存放合法的数据
        List<ImportZkClientDTO> dataList = new ArrayList<ImportZkClientDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ImportZkClientDTO rowDto = new ImportZkClientDTO();
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
                    rowDto.setRequestType(value);
                    // 坐席外呼方式 1:绑定手机号 2:sip
                    if(value.equals(SIP)){
                        rowDto.setRequestTypeName(SIP_NAME);
                    }
                    if(value.equals(MOBILE)){
                        rowDto.setRequestTypeName(MOBILE_NAME);
                    }
                }else if (j == 4) {
                    // 号码所属公司
                    rowDto.setNumberAttributionCompany(value);
                }
            }
            dataList.add(rowDto);
        }
        redisTemplate.opsForValue().set(Constants.ZK_CLIENT_KEY + userId, dataList, 10 * 60,
                TimeUnit.SECONDS);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("num", dataList.size());
        resMap.put("data", dataList);
        return new JSONResult<>().success(resMap);
    }

//    @RequiresPermissions("callcenter:zkClient:import")
    @ResponseBody
    @PostMapping("/submitZkClientData")
    public JSONResult<List<ImportZkClientDTO>> submitZkClientData() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        long userId = curLoginUser.getId();
        List<ImportZkClientDTO> dataList = (List<ImportZkClientDTO>) redisTemplate.opsForValue().get(Constants.ZK_CLIENT_KEY + userId);
        UploadZkClientDataDTO<ImportZkClientDTO> reqClientDataDTO = new UploadZkClientDataDTO<ImportZkClientDTO>();
        reqClientDataDTO.setCreateUser(userId);
        reqClientDataDTO.setList(dataList);
        // remove redis 临时数据
        redisTemplate.delete(Constants.ZK_CLIENT_KEY + userId);

        JSONResult<List<ImportZkClientDTO>> uploadZkClientData = zkClientFeignClient.uploadZkClientData(reqClientDataDTO);
        return uploadZkClientData;
    }

    @PostMapping("/zkLogin")
    @ResponseBody
    public JSONResult zkLogin(@RequestBody ZkLoginReqDTO reqDTO ) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long userId = curLoginUser.getId();
        if(StringUtils.isBlank(reqDTO.getLoginClient())){
            return new JSONResult<>().fail(SysErrorCodeEnum.LOGIN_CLIENT_NOT_EXIT.getCode(),SysErrorCodeEnum.LOGIN_CLIENT_NOT_EXIT.getMessage());
        }
        ZkClientQueryDTO zkClientQueryDTO = new ZkClientQueryDTO();
        zkClientQueryDTO.setLoginClient(reqDTO.getLoginClient());
        zkClientQueryDTO.setUserId(userId);
        JSONResult<ZkClientRespDTO> zkClientReq = zkClientFeignClient.queryZkClient(zkClientQueryDTO);

        if(JSONResult.SUCCESS.equals(zkClientReq.getCode())) {
            ZkClientRespDTO zkClientRespDTO = zkClientReq.getData();
            if(null == zkClientRespDTO) {
                return new JSONResult<>().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该坐席号不属于您");
            }
            if(StringUtils.isBlank(zkClientRespDTO.getClientNo())){
                return new JSONResult<>().fail(SysErrorCodeEnum.CLIENT_NO_NOT_EXIT.getCode(),SysErrorCodeEnum.CLIENT_NO_NOT_EXIT.getMessage());
            }
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute(CLIENT_NO,zkClientRespDTO.getClientNo());
            session.setAttribute(REQUEST_TYPE,zkClientRespDTO.getRequestType());
            return new JSONResult<>().success(true);
        }
        return new JSONResult<>().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }

    @PostMapping("/zkLogout")
    @ResponseBody
    public JSONResult zkLogout(HttpServletRequest request) {
        Session session = SecurityUtils.getSubject().getSession();
        if(StringUtils.isBlank((String)session.getAttribute(CLIENT_NO))) {
            return  new JSONResult<>().fail(SysErrorCodeEnum.ERR_UNLOGINCLIENT_FAIL.getCode(),SysErrorCodeEnum.ERR_UNLOGINCLIENT_FAIL.getMessage());
        }
        session.removeAttribute(CLIENT_NO);
        session.removeAttribute(REQUEST_TYPE);
        return new JSONResult<>().success(true);
    }

    /**
     * 中科 外呼
     */
    @PostMapping("/zkOutboundCall")
    @ResponseBody
    public JSONResult zkOutboundCall(@RequestBody ZkClientOutboundDTO  callDTO){
        String customerPhoneNumber = callDTO.getCustomerPhone();
        if(StringUtils.isBlank(customerPhoneNumber)) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),"客户手机号为null");
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Session session = SecurityUtils.getSubject().getSession();
        if(null == session.getAttribute(CLIENT_NO)){
            return new JSONResult().fail(SysErrorCodeEnum.CALL_NOT_EXIT.getCode(),SysErrorCodeEnum.CALL_NOT_EXIT.getMessage());
        }
        callDTO.setClientNo(String.valueOf(session.getAttribute(CLIENT_NO)));
        callDTO.setCustomerPhone(customerPhoneNumber);
        callDTO.setAccountId(curLoginUser.getId());
        callDTO.setOrgId(curLoginUser.getOrgId());
        return zkClientFeignClient.zkOutbound(callDTO);
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
    private JSONResult assignmentOrg(AddOrUpdateZkClientDTO reqDTO,Long userId){
        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(userId);
        JSONResult<UserInfoDTO> userInfoJr = userInfoFeignClient.get(idEntityLong);
        String code = userInfoJr.getCode();
        if(!JSONResult.SUCCESS.equals(code)){
            log.error("中科赋值org失败，失败原因{}",userInfoJr.getMsg());
            return new JSONResult<Boolean>().fail(code,userInfoJr.getMsg());
        }
        UserInfoDTO userInfo = userInfoJr.getData();
        reqDTO.setOrgId(userInfo.getOrgId());
        return new JSONResult().success(reqDTO);
    }
}
