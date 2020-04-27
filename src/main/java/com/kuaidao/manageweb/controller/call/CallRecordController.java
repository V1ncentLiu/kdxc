package com.kuaidao.manageweb.controller.call;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.kuaidao.common.constant.*;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.kuaidao.aggregation.dto.call.CallRecordCountDTO;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.call.QueryPhoneLocaleDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.config.BusinessCallrecordLimit;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/call/callRecord")
public class CallRecordController {

    private static Logger logger = LoggerFactory.getLogger(CallRecordController.class);

    @Autowired
    CallRecordFeign callRecordFeign;
    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;
    
    @Autowired
    BusinessCallrecordLimit businessCallrecordLimit;
    @Autowired
    RedisTemplate redisTemplate;
    
    @Value("${missedCall.business}")
    private String missedCallBusiness;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    /**
     * 记录拨打时间
     */
    @PostMapping("/recodeCallTime")
    @ResponseBody
    public void recodeCallTime(@RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        logger.info("firstCall {{}}",myCallRecordReqDTO);
        callRecordFeign.recodeCallTime(myCallRecordReqDTO);
    }

    /**
     * 我的通话记录
     *
     * @return
     */
    @RequiresPermissions("aggregation:myCallRecord:view")
    @RequestMapping("/myCallRecord")
    public String myCallRecord(HttpServletRequest request) {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        List<RoleInfoDTO> roleList = user.getRoleList();
        request.setAttribute("userId", user.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", user.getOrgId().toString());
        return "call/myCallRecord";
    }


    /**
     * 电销顾问通话记录
     *
     * @return
     */
    @RequiresPermissions("aggregation:telCallRecord:view")
    @RequestMapping("/telCallRecord")
    public String telCallRecord(HttpServletRequest request) {
        logger.info("businessCallrecordLimit {{}}",businessCallrecordLimit);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String ownOrgId = "";
        //商学院处理
        boolean isBusinessAcademy = false;
        Long curOrgId = curLoginUser.getOrgId();
        Long qhdBusOrgId = businessCallrecordLimit.getQhdBusOrgId();
        if(curOrgId.equals(qhdBusOrgId) || curOrgId.equals(businessCallrecordLimit.getSjhzTjBusOrgId()) || curOrgId.equals(businessCallrecordLimit.getQdtzBusOrgId())) {
            Integer businessLine = curLoginUser.getBusinessLine();
            isBusinessAcademy  = true;
            request.setAttribute("teleGroupList", getTeleGroupByBusinessLine(businessLine));
            request.setAttribute("teleDeptList",getTeleDeptByBusinessLine(businessLine));
        }
       //郑州商学院
        if(!isBusinessAcademy) {
            String zzBusOrgId = businessCallrecordLimit.getZzBusOrgId();
            if (StringUtils.isNotBlank(zzBusOrgId) &&  containsOrgId(zzBusOrgId,orgId+"")) {
                isBusinessAcademy  = true;
                List<String> orgIdList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(zzBusOrgId);
                long paramOrgId = Long.parseLong(orgIdList.get(1));
                request.setAttribute("teleGroupList", getDescenDantTeleGroupByOrgId(paramOrgId));
                request.setAttribute("teleDeptList",getTeleDeptByOrgId(paramOrgId).getData());
            }
        }
        
        
        //石家庄商学院
        if(!isBusinessAcademy) {
            String sjzBusOrgId = businessCallrecordLimit.getSjzBusOrgId();
            if (StringUtils.isNotBlank(sjzBusOrgId) && containsOrgId(sjzBusOrgId,curOrgId+"")) {
                isBusinessAcademy  = true;
                List<String> orgIdList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(sjzBusOrgId);
                long paramOrgId = Long.parseLong(orgIdList.get(1));
                request.setAttribute("teleGroupList",  getDescenDantTeleGroupByOrgId(paramOrgId));
                request.setAttribute("teleDeptList",getTeleDeptByOrgId(paramOrgId).getData());
            }
        }
       
    
        //合肥商学院 
        if(!isBusinessAcademy) {
            String hfBusOrgId = businessCallrecordLimit.getHfBusOrgId();
            if (StringUtils.isNotBlank(hfBusOrgId)  && containsOrgId(hfBusOrgId,curOrgId+"")) {
                isBusinessAcademy  = true;
                List<String> orgIdList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(hfBusOrgId);
                long paramOrgId = Long.parseLong(orgIdList.get(1));
                request.setAttribute("teleGroupList",getDescenDantTeleGroupByOrgId(paramOrgId));
                request.setAttribute("teleDeptList",getTeleDeptByOrgId(paramOrgId).getData());
            }
        }
         
        if(!isBusinessAcademy) {
            //非商学院
            if (RoleCodeEnum.DXZJ.name().equals(roleCode)) {
                ownOrgId =  String.valueOf(curLoginUser.getOrgId());
                request.setAttribute("teleGroupList", getCurTeleGroupList(orgId));
                request.setAttribute("ownOrgId", ownOrgId);
            } else {
                request.setAttribute("teleGroupList", getTeleGroupByRoleCode(curLoginUser));
            }
        }
        //总裁办文员-查询所有小物种的电销组
        if(RoleCodeEnum.ZCBWY.name().equals(roleCode)){
            request.setAttribute("teleGroupList",getTeleGroupByBusinessLine(BusinessLineConstant.XIAOWUZHONG));
            request.setAttribute("teleDeptList",getTeleDeptByBusinessLine(BusinessLineConstant.XIAOWUZHONG));
        }
        //监察-查询业务线对应下的电销组
        if(RoleCodeEnum.JC.name().equals(roleCode)){
            request.setAttribute("teleGroupList",getTeleGroupByBusinessLine(curLoginUser.getBusinessLine()));
            request.setAttribute("teleDeptList",getTeleDeptByBusinessLine(curLoginUser.getBusinessLine()));
        }
        //电销事业部
        getTeleDeptList(request,roleCode,orgId,curLoginUser.getBusinessLine());

    // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("aggregation:telCallRecord");
        queryFieldByRoleAndMenuReq.setId(curLoginUser.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(curLoginUser.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(curLoginUser.getId());
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:telCallRecord");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        request.setAttribute("userId", curLoginUser.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", curLoginUser.getOrgId().toString());

        return "call/telCallRecord";
    }

    /**
     * 根据组织机构查询电销事业部
     * @param orgId
     */
    private  JSONResult<List<OrganizationDTO>> getTeleDeptByOrgId(Long orgId){
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(orgId);
        organizationQueryDTO.setOrgType(OrgTypeConstant.DZSYB);
        return  organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
    }
    /**
     * 查询电销事业部
     * @param request
     * @param roleCode
     */
    private void getTeleDeptList(HttpServletRequest request, String roleCode,Long orgId,Integer businessLine) {
        if(RoleCodeEnum.GLY.name().equals(roleCode)){
            OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
            busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            busGroupReqDTO.setOrgType(OrgTypeConstant.DZSYB);
            request.setAttribute("teleDeptList",organizationFeignClient.queryOrgByParam(busGroupReqDTO).getData());
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode)){
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setId(orgId);
            queryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            JSONResult<List<OrganizationDTO>> orgJr = organizationFeignClient.listParentsUntilOrg(queryDTO);
            List<OrganizationDTO> orgList = orgJr.getData();
            List<OrganizationDTO> teleDeptIdList = new ArrayList<>();
            for (OrganizationDTO organizationDTO : orgList) {
                if(OrgTypeConstant.DZSYB.equals(organizationDTO.getOrgType())){
                    teleDeptIdList.add(organizationDTO);
                    request.setAttribute("curDeptId",String.valueOf(organizationDTO.getId()));
                    break;
                }
            }
            request.setAttribute("teleDeptList",teleDeptIdList);

        }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
            IdEntity idEntity = new IdEntity(String.valueOf(orgId));
            JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
            if (!JSONResult.SUCCESS.equals(orgJr.getCode()) ||  orgJr.getData() == null) {
                logger.error("organizationFeignClient.queryOrgById(),req{{}},res{{}}",idEntity,orgJr);
            }
            List<OrganizationDTO> orgList = new ArrayList<>();
            orgList.add(orgJr.getData());
            request.setAttribute("teleDeptList",orgList);
            request.setAttribute("curDeptId",String.valueOf(orgId));
        }else if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            queryDTO.setParentId(orgId);
            JSONResult<List<OrganizationRespDTO>> orgJr = organizationFeignClient.queryOrgByParam(queryDTO);
            if (JSONResult.SUCCESS.equals(orgJr.getCode()) &&  orgJr.getData() != null &&  orgJr.getData().size()>0) {
                request.setAttribute("teleDeptList",orgJr.getData());
            }
        }
    }

    public List<OrganizationDTO> getDescenDantTeleGroupByOrgId(Long parentOrgId){

        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(parentOrgId);
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        // 查询下级电销组(查询使用)
        JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
            organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
        List<OrganizationDTO> data = listDescenDantByParentId.getData();
        return data;
    }

    /**
     * 获取该业务下 的所有电销组
     *
     * @param businessLine
     * @return
     */
    private List<OrganizationRespDTO> getTeleGroupByBusinessLine(Integer businessLine) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setBusinessLine(businessLine);
        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> orgGroupJr =
                organizationFeignClient.queryOrgByParam(queryDTO);
        if (!JSONResult.SUCCESS.equals(orgGroupJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}", queryDTO, orgGroupJr);
            return new ArrayList<>();
        }
        return orgGroupJr.getData();
    }


    /**
     * 获取该业务下 的所有电销事业部
     *
     * @param businessLine
     * @return
     */
    private List<OrganizationRespDTO> getTeleDeptByBusinessLine(Integer businessLine) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setBusinessLine(businessLine);
        queryDTO.setOrgType(OrgTypeConstant.DZSYB);
        JSONResult<List<OrganizationRespDTO>> orgGroupJr =
                organizationFeignClient.queryOrgByParam(queryDTO);
        if (!JSONResult.SUCCESS.equals(orgGroupJr.getCode())) {
            logger.error("getTeleDeptByBusinessLine,param{{}},res{{}}", queryDTO, orgGroupJr);
            return new ArrayList<>();
        }
        return orgGroupJr.getData();
    }

    private List<OrganizationDTO> getTeleGroupByRoleCode(UserInfoDTO user) {
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null
            && RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) {
            // 如果是电销副总展现事业部下所有组
            Long orgId = user.getOrgId();
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(orgId);
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            // 查询下级电销组(查询使用)
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            List<OrganizationDTO> data = listDescenDantByParentId.getData();
            return data;
        } else if (roleList != null
            && RoleCodeEnum.DXZJL.name().equals(roleList.get(0).getRoleCode())) {
            // 如果是电销副总展现事业部下所有组
            Long orgId = user.getOrgId();
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(orgId);
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            // 查询下级电销组(查询使用)
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            List<OrganizationDTO> data = listDescenDantByParentId.getData();
            return data;
        }
        return  null;
    }
    private List<OrganizationDTO> getCurTeleGroupList(Long orgId) {
        OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(String.valueOf(orgId));
        List<OrganizationDTO> teleGroupIdList = new ArrayList<>();
        teleGroupIdList.add(curOrgGroupByOrgId);
        return teleGroupIdList;
    }

    /**
     * 获取当前 orgId所在的组织
     *
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId);
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if (!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}", idEntity, orgJr);
            return null;
        }
        return orgJr.getData();
    }


    /**
     * 电销顾问总时长统计
     *
     * @return
     */
    @RequiresPermissions("aggregation:tmTalkTimeCallRecord:view")
    @RequestMapping("/tmTalkTimeCallRecord")
    public String tmTalkTimeCallRecord(HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String ownOrgId = "";
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            request.setAttribute("teleGroupList",getCurTeleGroupList(orgId));
            ownOrgId =  String.valueOf(curLoginUser.getOrgId());
            request.setAttribute("ownOrgId", ownOrgId);
        }else {
            request.setAttribute("teleGroupList",getTeleGroupByRoleCode(curLoginUser));
        }
        return "call/tmTalkTimeCallRecord";
    }

    /**
     * 获取我的通话记录 分页展示 ，参数模糊匹配
     *
     * @return
     */
    @RequiresPermissions("aggregation:myCallRecord:view")
    @PostMapping("/listMyCallRecord")
    @ResponseBody
    public JSONResult<Map<String, Object>> listMyCallRecord(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getId();
        List<Long> accountIdList = new ArrayList<Long>();
        accountIdList.add(id);
        myCallRecordReqDTO.setAccountIdList(accountIdList);
        return callRecordFeign.listMyCallRecord(myCallRecordReqDTO);
    }

    /**
     * 我的通话记录 统计总时长
     *
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/countMyCallRecordTalkTime")
    @ResponseBody
    public JSONResult<Integer> countMyCallRecordTalkTime(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getId();
        List<Long> accountIdList = new ArrayList<Long>();
        accountIdList.add(id);
        myCallRecordReqDTO.setAccountIdList(accountIdList);
        return callRecordFeign.countMyCallRecordTalkTime(myCallRecordReqDTO);
    }

    /**
     * 电销通话记录 分页展示 ，参数模糊匹配
     *
     * @return
     */
    @RequiresPermissions("aggregation:telCallRecord:view")
    @PostMapping("/listAllTmCallRecord")
    @ResponseBody
    public JSONResult<Map<String, Object>> listAllTmCallRecord(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        logger.info("callrecord limit {{}}",businessCallrecordLimit);
        // 根据角色查询 下属顾问
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        // 电销顾问
        List<Long> accountIdList = myCallRecordReqDTO.getAccountIdList();
        logger.info("callrecord curLoginUser {{}}",curLoginUser);
        if (CollectionUtils.isEmpty(accountIdList)) {
            //9期 商学院处理
           Integer businessLine = curLoginUser.getBusinessLine();
           Map<String,Object> busMap =  setBusAccountIdList(myCallRecordReqDTO,orgId,businessLine);
            logger.info("callrecord busMap {{}}",busMap);
           Boolean isBusLimit =  (Boolean)busMap.get("isBusinessAcademy");

           if (isBusLimit &&  busMap.get("result")!=null) {
               logger.info("callrecord busMap111111 {{}}",busMap);
              return (JSONResult)busMap.get("result");
            }
            logger.info("callrecord busMap333333 {{}}",busMap);
           if (isBusLimit) {
               //商学院组织机构
               Long selectTeleGroupId = myCallRecordReqDTO.getTeleGroupId();
               Long selectedTeleDeptId = myCallRecordReqDTO.getTeleDeptId();
               selectTeleGroupId = selectTeleGroupId != null ? selectTeleGroupId : selectedTeleDeptId;
               if (selectTeleGroupId!=null) {
                   logger.info("callrecord userList {{}}",selectTeleGroupId);
                   List<UserInfoDTO> userList = getTeleSaleByOrgId(selectTeleGroupId);
                   logger.info("callrecord userListsss {{}}",userList);
                   if (CollectionUtils.isEmpty(userList)) {
                       return new JSONResult<Map<String, Object>>().success(null);
                   }
                   List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user -> user.getId())
                           .collect(Collectors.toList());
                   myCallRecordReqDTO.setAccountIdList(idList);
               }
           }
            logger.info("callrecord busMap1 {{}}",busMap);
           
           if (!isBusLimit) {
               if (RoleCodeEnum.DXZJ.name().equals(roleCode)) {
                   // 电销总监
                   List<UserInfoDTO> userList = getTeleSaleByOrgId(orgId);
                   if (CollectionUtils.isEmpty(userList)) {
                       return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                               "该电销总监下无顾问");
                   }
                   List<Long> idList = userList.parallelStream().filter(user -> user.getStatus() == 1 || user.getStatus() == 3).map(user -> user.getId())
                           .collect(Collectors.toList());
                   idList.add(curLoginUser.getId());
                   myCallRecordReqDTO.setAccountIdList(idList);
               }else if(RoleCodeEnum.ZCBWY.name().equals(roleCode)){
                   //总裁办文员
                   UserOrgRoleReq req = new UserOrgRoleReq();
                   req.setRoleCode(RoleCodeEnum.DXCYGW.name());
                   req.setBusinessLine(BusinessLineConstant.XIAOWUZHONG);
                   req.setOrgId(myCallRecordReqDTO.getTeleGroupId());
                   JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                   List<Long> idList=new ArrayList<>();
                   if(userJr.getData()!=null && !userJr.getData().isEmpty()){
                        idList = userJr.getData().parallelStream().map(user -> user.getId())
                               .collect(Collectors.toList());
                   }
                   myCallRecordReqDTO.setAccountIdList(idList);
               }else if(RoleCodeEnum.JC.name().equals(roleCode)){
                   //监察角色查看该业务线下所有的通话记录
                   //总裁办文员
                   UserOrgRoleReq req = new UserOrgRoleReq();
                   req.setRoleCode(RoleCodeEnum.DXCYGW.name());
                   req.setBusinessLine(curLoginUser.getBusinessLine());
                   JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                   List<Long> idList=new ArrayList<>();
                   if(userJr.getData()!=null && !userJr.getData().isEmpty()){
                       idList = userJr.getData().parallelStream().map(user -> user.getId())
                               .collect(Collectors.toList());
                   }
                   myCallRecordReqDTO.setAccountIdList(idList);
               }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
                   Long teleGroupId = myCallRecordReqDTO.getTeleGroupId();
                   Long reqGroupId = teleGroupId == null ? orgId : teleGroupId;
                   UserOrgRoleReq req = new UserOrgRoleReq();
                   req.setRoleCode(RoleCodeEnum.DXCYGW.name());
                   req.setOrgId(reqGroupId);
                   JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                   if(!JSONResult.SUCCESS.equals(userJr.getCode())){
                       logger.error("查询电销通话记录-查询创业顾问 param{{}},res{{}}",req,userJr);
                       return new JSONResult<Map<String, Object>>().fail(userJr.getCode(),userJr.getMsg());
                   }
                   List<UserInfoDTO> userData = userJr.getData();
                   if(CollectionUtils.isEmpty(userData)){
                       return new JSONResult<Map<String, Object>>().success(null);
                   }
                   List<Long> idList= userData.parallelStream().map(user -> user.getId()).collect(Collectors.toList());
                   myCallRecordReqDTO.setAccountIdList(idList);

               }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
                   Long teleGroupId = myCallRecordReqDTO.getTeleGroupId();
                   Long teleDeptId = myCallRecordReqDTO.getTeleDeptId();
                   Long reqOrgId = teleGroupId != null ? teleGroupId : teleDeptId != null ? teleDeptId : null;
                   List<UserInfoDTO> userList = getTeleSaleByOrgId(reqOrgId);
                   if (CollectionUtils.isEmpty(userList)) {
                       logger.warn("顾问通话记录-管理员查询所有的电销顾问，返回数据为null，param[{}] ",reqOrgId);
                       return new JSONResult<Map<String, Object>>().success(null);
                   }
                   List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user -> user.getId())
                           .collect(Collectors.toList());
                   myCallRecordReqDTO.setAccountIdList(idList);

               }else if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
                   Long teleGroupId = myCallRecordReqDTO.getTeleGroupId();
                   Long teleDeptId = myCallRecordReqDTO.getTeleDeptId();
                   Long reqOrgId = teleGroupId != null ? teleGroupId : teleDeptId != null ? teleDeptId : curLoginUser.getOrgId();
                   List<UserInfoDTO> userList = getTeleSaleByOrgId(reqOrgId);
                   if (CollectionUtils.isEmpty(userList)) {
                       logger.warn("顾问通话记录-管理员查询所有的电销顾问，返回数据为null，param[{}] ",reqOrgId);
                       return new JSONResult<Map<String, Object>>().success(null);
                   }
                   List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user -> user.getId())
                           .collect(Collectors.toList());
                   myCallRecordReqDTO.setAccountIdList(idList);
               } else {
                   // 其他角色
                   Long teleGroupId = myCallRecordReqDTO.getTeleGroupId();
                   if (teleGroupId != null) {
                       List<UserInfoDTO> userList = getTeleSaleByOrgId(teleGroupId);
                       if (CollectionUtils.isEmpty(userList)) {
                           return new JSONResult<Map<String, Object>>().success(null);
                       }
                       List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user -> user.getId())
                               .collect(Collectors.toList());
                       myCallRecordReqDTO.setAccountIdList(idList);
                   } else {
                       List<UserInfoDTO> userInfoList = getTeleSaleByOrgId(curLoginUser.getOrgId());
                       if (CollectionUtils.isEmpty(userInfoList)) {
                           return new JSONResult<Map<String, Object>>().success(null);
                       }
                       List<Long> idList = userInfoList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user -> user.getId())
                               .collect(Collectors.toList());
                       myCallRecordReqDTO.setAccountIdList(idList);
                   }
               }
           } 
            
        }
        logger.info("callrecord myCallRecordReqDTO {{}}",myCallRecordReqDTO);
        JSONResult<Map<String,Object>> mapss = callRecordFeign.listAllTmCallRecord(myCallRecordReqDTO);
        logger.info("callrecord mapss {{}}",mapss);
        return mapss;

    }

    /**
     * 检查当前组织机构是否在指定的文件中
     * @param sourceStr
     * @param orgId
     * @return
     */
    private boolean containsOrgId(String sourceStr,String orgId){
        String[] idArr = sourceStr.split(",");
        if(idArr[0].equals(orgId)){
            return true;
        }
        return false;
    }
    
    /**
     * 商学院逻辑处理
    * @param myCallRecordReqDTO
    * @param curOrgId
    * @return
     */
    private Map<String, Object>  setBusAccountIdList(CallRecordReqDTO myCallRecordReqDTO,Long curOrgId,Integer businessLine) {
        Map<String, Object> resMap = new HashMap<>();
        //判断是否 秦皇岛商学院听业务线下所有； 商机盒子商学院 听业务线下所有  渠道拓展 听业务线下所有
        List<UserInfoDTO>  userInfoList = new ArrayList<>();
        boolean isBusinessAcademy = false   ;
        Long qhdBusOrgId = businessCallrecordLimit.getQhdBusOrgId();
        if(curOrgId.equals(qhdBusOrgId) || curOrgId.equals(businessCallrecordLimit.getSjhzTjBusOrgId()) || curOrgId.equals(businessCallrecordLimit.getQdtzBusOrgId())) {
              userInfoList  = getTeleSaleByBusinessLine(businessLine);
              isBusinessAcademy = true;
        }
        logger.info("callrecord isBusinessAcademy1 {{}}",isBusinessAcademy);
        logger.info("callrecord userInfoList {{}}",userInfoList);
       //郑州商学院 听郑州
        if (!isBusinessAcademy) {
            String zzBusOrgId = businessCallrecordLimit.getZzBusOrgId();
            if (StringUtils.isNotBlank(zzBusOrgId) && containsOrgId(zzBusOrgId,curOrgId+"")) {
                List<String> orgIdList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(zzBusOrgId);
                userInfoList = getTeleSaleByOrgId(Long.parseLong(orgIdList.get(1)));
                isBusinessAcademy = true;
            }
        }
        
        if (!isBusinessAcademy) {
            //石家庄商学院 听石家庄
            String sjzBusOrgId = businessCallrecordLimit.getSjzBusOrgId();
            if (StringUtils.isNotBlank(sjzBusOrgId) && containsOrgId(sjzBusOrgId,curOrgId+"")) {
                List<String> orgIdList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(sjzBusOrgId);
                userInfoList = getTeleSaleByOrgId(Long.parseLong(orgIdList.get(1)));
                isBusinessAcademy = true;
            }
        }
        
        if (!isBusinessAcademy) {
            //合肥商学院 听合肥的
             String hfBusOrgId = businessCallrecordLimit.getHfBusOrgId();
            if (StringUtils.isNotBlank(hfBusOrgId) &&  containsOrgId(hfBusOrgId,curOrgId+"")) {
                List<String> orgIdList = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(hfBusOrgId);
                userInfoList = getTeleSaleByOrgId(Long.parseLong(orgIdList.get(1)));
                isBusinessAcademy = true;
            }
        }
        resMap.put("isBusinessAcademy", isBusinessAcademy);
        if (!isBusinessAcademy) {
           return resMap; 
        }
        if (CollectionUtils.isEmpty(userInfoList)) {
            resMap.put("result",new JSONResult<Map<String, Object>>().success(null));
            return resMap;
        }
        List<Long> idList = userInfoList.parallelStream().map(user->user.getId()).collect(Collectors.toList());
        myCallRecordReqDTO.setAccountIdList(idList);
        logger.info("callrecord resMap222 {{}}",resMap);
        return resMap;
    }

    /**
     * 根据businessLine 获取创业顾问
    * @param businessLine
    * @return
     */
    private List<UserInfoDTO> getTeleSaleByBusinessLine(Integer businessLine) {
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setBusinessLine(businessLine);
        req.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error(
                    "查询电销通话记录-根据业务线获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    req, userJr);
            return null;
        }
        return userJr.getData();
    }

    /**
     * 根据orgId 获取创业顾问
     *
     * @param orgId
     * @return
     */
    private List<UserInfoDTO> getTeleSaleByOrgId(Long orgId) {
        logger.info("callrecord orgId {{}}",orgId);
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setOrgId(orgId);
        req.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        logger.info("callrecord userJr {{}}",userJr);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error(
                    "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    orgId, userJr);
            return null;
        }
        return userJr.getData();
    }


    /***
     * 电销通话时长统计 分页
     *
     * @param myCallRecordReqDTO
     * @return
     */
    @RequiresPermissions("aggregation:tmTalkTimeCallRecord:view")
    @PostMapping("/listAllTmCallTalkTime")
    @ResponseBody
    public JSONResult<Map<String, Object>> listAllTmCallTalkTime(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {

        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        //电销顾问
        List<Long> accountIdList = myCallRecordReqDTO.getAccountIdList();
        Long teleGroupId = myCallRecordReqDTO.getTeleGroupId();
        if(CollectionUtils.isNotEmpty(accountIdList)){

        }else if(teleGroupId != null){
            List<UserInfoDTO> userList = getTeleSaleByOrgId(teleGroupId);
            if(CollectionUtils.isEmpty(userList)) {
                return new JSONResult<Map<String,Object>>().success(null);
            }
            List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
            myCallRecordReqDTO.setAccountIdList(idList);
        }else {
            if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
                //电销总监
                List<UserInfoDTO> userList = getTeleSaleByOrgId(orgId);
                if(CollectionUtils.isEmpty(userList)) {
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该电销总监下无顾问");
                }
                List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                idList.add(curLoginUser.getId());
                myCallRecordReqDTO.setAccountIdList(idList);

            }else {
                List<UserInfoDTO>  userInfoList  = getTeleSaleByOrgId(curLoginUser.getOrgId());
                if (CollectionUtils.isEmpty(userInfoList)) {
                    return new JSONResult<Map<String,Object>>().success(null);
                }
                List<Long> idList = userInfoList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user->user.getId()).collect(Collectors.toList());
                myCallRecordReqDTO.setAccountIdList(idList);
            }
        }
        return callRecordFeign.listAllTmCallTalkTime(myCallRecordReqDTO);
    }


    /**
     * 根据clueId 或 customerPhone 查询 通话记录
     *
     * @param myCallRecordReqDTO 参数 clueid 或 customerPhone
     * @return
     */
    @PostMapping("/listTmCallReacordByParams")
    @ResponseBody
    public JSONResult<PageBean<CallRecordRespDTO>> listTmCallReacordByParams(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        return callRecordFeign.listTmCallReacordByParams(myCallRecordReqDTO);
    }

    /**
     * 根据clueId 或 customerPhone 查询 通话记录
     *
     * @param myCallRecordReqDTO 参数 clueid 或 customerPhone
     * @return
     */
    @PostMapping("/listTmCallReacordByParamsNoPage")
    @ResponseBody
    JSONResult<List<CallRecordRespDTO>> listTmCallReacordByParamsNoPage(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        return callRecordFeign.listTmCallReacordByParamsNoPage(myCallRecordReqDTO);
    }

    /**
     *  获取天润通话记录地址 根据 记录Id
     * @param idEntity
     * @return
     */
    @PostMapping("/getRecordFile")
    @ResponseBody
    public JSONResult<String> getRecordFile(@RequestBody IdEntity idEntity) {
        return callRecordFeign.getRecordFile(idEntity);
    }

    /**
     * 根据clueId List 分组统计 拨打次数
     *
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/countCallRecordTotalByClueIdList")
    @ResponseBody
    public JSONResult<List<CallRecordCountDTO>> countCallRecordTotalByClueIdList(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        return callRecordFeign.countCallRecordTotalByClueIdList(myCallRecordReqDTO);
    }



    /**
     * 统计 通话时长
     *
     * @param teleConsoleReqDTO
     * @return
     */
    @PostMapping("/countTodayTalkTime")
    @ResponseBody
    public JSONResult<Integer> countTodayTalkTime(
            @RequestBody TeleConsoleReqDTO teleConsoleReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getId();
        teleConsoleReqDTO.setTeleSaleId(id);
        teleConsoleReqDTO.setStartTime(DateUtil.getTodayStartTime());
        teleConsoleReqDTO.setEndTime(new Date());
        return callRecordFeign.countTodayTalkTime(teleConsoleReqDTO);
    }


    /**
     * 查询手机号码归属地
     *
     * @param queryPhoneLocaleDTO
     * @return
     */
    @PostMapping("/queryPhoneLocale")
    @ResponseBody
    public JSONResult<JSONObject> queryPhoneLocale(
            @RequestBody QueryPhoneLocaleDTO queryPhoneLocaleDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryPhoneLocaleDTO.setOrgId(curLoginUser.getOrgId());
        return callRecordFeign. queryPhoneLocale(queryPhoneLocaleDTO);
    }
    
    /**
     * 查询手机号未接次数及禁止拨打时间
     * @return
     */
    @PostMapping("/missedCalPhone")
    @ResponseBody
    public String missedCalPhone(@RequestParam(value = "phone") String phone) {
    	UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
    	String str = "";
    	List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        List<Long> idList = new ArrayList<Long>();
    	if(curLoginUser.getBusinessLine() !=null && missedCallBusiness.contains(","+curLoginUser.getBusinessLine()+",") && (roleList.get(0).getRoleCode().equals(RoleCodeEnum.DXZJ.name()) || roleList.get(0).getRoleCode().equals(RoleCodeEnum.DXCYGW.name()))) {
    		String timie = (String)redisTemplate.opsForValue().get(RedisConstant.MISSEDCALLS_PHONE+phone);
    		if(StringUtils.isNotBlank(timie)) {
    			str = "禁止呼叫此号码，请"+timie+"后再试";
    		}
    	}
    	return str;
    }
}
