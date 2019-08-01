package com.kuaidao.manageweb.controller.phonetraffic;

import com.alibaba.fastjson.JSONObject;
import com.kuaidao.aggregation.dto.call.CallRecordCountDTO;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.QueryPhoneLocaleDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/traffic/callRecord")
public class TrafficCallRecordController {

    private static Logger logger = LoggerFactory.getLogger(TrafficCallRecordController.class);

    @Autowired
    CallRecordFeign callRecordFeign;
    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    /**
     * 话务通话记录页面初始化
     *
     * @return
     */
    @RequiresPermissions("aggregation:trafficCallRecord:view")
    @RequestMapping("/trafficCallRecord")
    public String trafficCallRecord(HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        request.setAttribute("userId", curLoginUser.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", curLoginUser.getOrgId().toString());
        if (RoleCodeEnum.HWZG.name().equals(roleCode)) {
            UserOrgRoleReq userRole = new UserOrgRoleReq();
            userRole.setOrgId(orgId);
            userRole.setRoleCode(RoleCodeEnum.HWY.name());
            JSONResult<List<UserInfoDTO>> hwUserList =  userInfoFeignClient.listByOrgAndRole(userRole);
            request.setAttribute("hwzyList", hwUserList.getData());
        }
        return "phonetraffic/trafficCallRecord";
    }
    /**
     * 话务通话记录(所有)页面初始化
     *
     * @return
     */
    @RequiresPermissions("aggregation:trafficAllCallRecord:view")
    @RequestMapping("/trafficAllCallRecord")
    public String trafficAllCallRecord(HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        request.setAttribute("userId", curLoginUser.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", curLoginUser.getOrgId().toString());
        if (RoleCodeEnum.HWJL.name().equals(roleCode)) {
            List<OrganizationDTO> hwGroupList =  getHwGroupByRoleCode(curLoginUser);
            request.setAttribute("hwGroupList", hwGroupList);
        }
        return "phonetraffic/trafficAllCallRecord";
    }
    /**
     * 话务通话记录 分页展示 ，参数模糊匹配
     *
     * @return
     */
    @RequiresPermissions("aggregation:trafficCallRecord:view")
    @PostMapping("/listHwCallRecord")
    @ResponseBody
    public JSONResult<Map<String, Object>> listHwCallRecord(
        @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        // 根据角色查询下属话务专员
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        // 话务专员
        List<Long> accountIdList = myCallRecordReqDTO.getAccountIdList();
        if (CollectionUtils.isEmpty(accountIdList)) {
            if (RoleCodeEnum.HWZG.name().equals(roleCode)) {
                List<UserInfoDTO> userList = getPhoneTrafficByOrgId(orgId);
                if (CollectionUtils.isEmpty(userList)) {
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                        "该话务主管下没有话务专员");
                }
                List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user -> user.getId())
                    .collect(Collectors.toList());
                idList.add(curLoginUser.getId());
                myCallRecordReqDTO.setAccountIdList(idList);

            }

        }

        return callRecordFeign.listAllTmCallRecord(myCallRecordReqDTO);

    }
    /**
     * 话务通话记录(所有) 分页展示 ，参数模糊匹配
     *
     * @return
     */
    @RequiresPermissions("aggregation:trafficAllCallRecord:view")
    @PostMapping("/listAllHwCallRecord")
    @ResponseBody
    public JSONResult<Map<String, Object>> listAllHwCallRecord(
        @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        // 根据角色查询下属话务专员
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        String roleCode = CommUtil.getRoleCode(curLoginUser);
        // 话务专员
        List<Long> accountIdList = myCallRecordReqDTO.getAccountIdList();
        if (CollectionUtils.isEmpty(accountIdList)) {
            if(RoleCodeEnum.HWJL.name().equals(roleCode)) {
                // 话务经理
                Long teleGroupId = myCallRecordReqDTO.getTeleGroupId();
                if (teleGroupId != null) {
                    List<UserInfoDTO> userList = getPhoneTrafficByOrgId(teleGroupId);
                    if (CollectionUtils.isEmpty(userList)) {
                        return new JSONResult<Map<String, Object>>().success(null);
                    }
                    List<Long> idList = userList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user -> user.getId())
                        .collect(Collectors.toList());
                    myCallRecordReqDTO.setAccountIdList(idList);
                } else {
                    List<UserInfoDTO> userInfoList = getPhoneTrafficByOrgId(curLoginUser.getOrgId());
                    if (CollectionUtils.isEmpty(userInfoList)) {
                        return new JSONResult<Map<String, Object>>().success(null);
                    }
                    List<Long> idList = userInfoList.parallelStream().filter(user->user.getStatus() ==1 || user.getStatus() ==3).map(user -> user.getId())
                        .collect(Collectors.toList());
                    myCallRecordReqDTO.setAccountIdList(idList);
                }
            }

        }

        return callRecordFeign.listAllTmCallRecord(myCallRecordReqDTO);

    }
    private List<OrganizationDTO> getHwGroupByRoleCode(UserInfoDTO user) {
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null
            && RoleCodeEnum.HWJL.name().equals(roleList.get(0).getRoleCode())) {
            // 如果是电销副总展现事业部下所有组
            Long orgId = user.getOrgId();
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(orgId);
            organizationQueryDTO.setOrgType(OrgTypeConstant.HWZ);
            // 查询下级电销组(查询使用)
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            List<OrganizationDTO> data = listDescenDantByParentId.getData();
            return data;
        }
        return  null;
    }
    /**
     * 根据orgId 获取话务专员
     *
     * @param orgId
     * @return
     */
    private List<UserInfoDTO> getPhoneTrafficByOrgId(Long orgId) {
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setOrgId(orgId);
        req.setRoleCode(RoleCodeEnum.HWY.name());
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error(
                    "查询话务通话记录-获取话务专员-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    orgId, userJr);
            return null;
        }
        return userJr.getData();
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
}
