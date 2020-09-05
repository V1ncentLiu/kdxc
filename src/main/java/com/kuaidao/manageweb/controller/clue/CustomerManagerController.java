package com.kuaidao.manageweb.controller.clue;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.clue.CustomerManagerDTO;
import com.kuaidao.aggregation.dto.clue.CustomerManagerQueryDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.CustomerManagerFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/tele/customerManager")
public class CustomerManagerController {
    private static Logger logger = LoggerFactory.getLogger(PendingAllocationController.class);
    @Autowired
    private CustomerManagerFeignClient customerManagerFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;

    @Autowired
    private ClueBasicFeignClient clueBasicFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    @RequiresPermissions("customerManager:view")
    @RequestMapping("/initcustomerManager")
    public String initmyCustomer(HttpServletRequest request, Model model) {

        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        String ownOrgId = "";
        if (roleList != null && RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
            List<OrganizationDTO> dataList = new ArrayList<OrganizationDTO>();
            // 如果当前登录的为电销总监,查询所有下属电销员工
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            statusList.add(SysConstant.USER_STATUS_LOCK);
            List<UserInfoDTO> userList =
                    getUserList(user.getOrgId(), RoleCodeEnum.DXCYGW.name(), statusList);
            request.setAttribute("saleList", userList);
            ownOrgId = String.valueOf(user.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(ownOrgId);
            if (curOrgGroupByOrgId != null) {
                dataList.add(curOrgGroupByOrgId);
            }
            // 查询同事业部下的电销组
            Long orgId = user.getOrgId();
            JSONResult<OrganizationDTO> queryOrgById =
                    organizationFeignClient.queryOrgById(new IdEntity(orgId.toString()));
            List<Map<String, Object>> saleGroupList =
                    getSaleGroupListByBusinessLine(user.getBusinessLine());
            request.setAttribute("saleGroupList", saleGroupList);
            request.setAttribute("ownOrgId", ownOrgId);
            request.setAttribute("queryOrg", dataList);

        } else if (roleList != null
                && RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) {
            List<Map<String, Object>> saleGroupList = getSaleGroupList(user.getOrgId());
            request.setAttribute("saleGroupList", saleGroupList);
            // 如果是电销副总展现事业部下所有组
            Long orgId = user.getOrgId();
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(orgId);
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            // 查询下级电销组(查询使用)
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            List<OrganizationDTO> data = listDescenDantByParentId.getData();
            request.setAttribute("queryOrg", data);
        } else if (roleList != null
                && RoleCodeEnum.DXZJL.name().equals(roleList.get(0).getRoleCode())) {
            List<Map<String, Object>> saleGroupList = getSaleGroupList(user.getOrgId());
            request.setAttribute("saleGroupList", saleGroupList);
            // 如果是电销副总展现事业部下所有组
            Long orgId = user.getOrgId();
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(orgId);
            organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
            // 查询下级电销组(查询使用)
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            List<OrganizationDTO> data = listDescenDantByParentId.getData();
            request.setAttribute("queryOrg", data);
        }

        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("customerManager");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("customerManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        request.setAttribute("roleCode", user.getRoleList().get(0).getRoleCode());
        request.setAttribute("businessLine", user.getBusinessLine());
        Boolean canCopy = handleCanCopy(user.getBusinessLine());
        request.setAttribute("canCopy", canCopy);
        return "clue/customManagement";
    }

    @RequestMapping("/findcustomerManagerPage")
    @ResponseBody
    public JSONResult<PageBean<CustomerManagerDTO>> findTeleClueInfo(HttpServletRequest request,
                                                                     @RequestBody CustomerManagerQueryDTO dto) {
        java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (null != dto && null != dto.getDayTel()) {
            // 当日拨打电话
            dto.setTelTime(formatter.format(new Date()));
        }
        if (null != dto && null != dto.getTrackingDay() && dto.getTrackingDay().intValue() == 1) {
            // 当日跟进
            dto.setTrackingTime(formatter.format(new Date()));
        }

        // 数据权限处理
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            String roleCode = user.getRoleList().get(0).getRoleCode();
            if (null != roleCode) {
                if (roleCode.equals(RoleCodeEnum.GLY.name())) {
                    // 管理员查看所有

                } else if (roleCode.equals(RoleCodeEnum.DXFZ.name())) {
                    // 查看事业部下所有数据
                    dto.setTeleDept(user.getOrgId());

                } else if (roleCode.equals(RoleCodeEnum.DXZJL.name())) {

                    // 查看分公司下所有数据
                    dto.setTeleCompany(user.getOrgId());

                } else if (roleCode.equals(RoleCodeEnum.DXZJ.name())) {

                    // 查看电销组下所有数据
                    dto.setTeleGorup(user.getOrgId());

                }
            }
        }
        dto.setOrgId(user.getOrgId());
        JSONResult<PageBean<CustomerManagerDTO>> jsonResult =
                customerManagerFeignClient.findcustomerPage(dto);
        return jsonResult;
    }

    @RequestMapping("/listOrgInfo")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> listByOrgAndRole(HttpServletRequest request) {
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        return organizationFeignClient.queryOrgByParam(orgDto);

    }

    /**
     * 获取当前登录账号
     *
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 根据机构和角色类型获取用户
     *
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 根据机构和角色类型获取用户
     *
     * @return
     */
    private List<UserInfoDTO> getUserListByBusliness(Integer businessLine, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        userOrgRoleReq.setBusinessLine(businessLine);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }
    /**
     * 获取电销组
     *
     * @param orgId
     * @return
     */
    private List<Map<String, Object>> getSaleGroupList(Long orgId) {
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(orgId);
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        // 查询下级电销组
        JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
        List<OrganizationDTO> data = listDescenDantByParentId.getData();
        // 查询所有电销总监
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.DXZJ.name(), statusList);
        Map<Long, UserInfoDTO> userMap = new HashMap<Long, UserInfoDTO>();
        // 生成<机构id，用户>map
        for (UserInfoDTO userInfoDTO : userList) {
            userMap.put(userInfoDTO.getOrgId(), userInfoDTO);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        // 生成结果集，匹配电销组以及电销总监
        for (OrganizationDTO organizationDTO : data) {
            Map<String, Object> orgMap = new HashMap<String, Object>();
            UserInfoDTO user = userMap.get(organizationDTO.getId());
            orgMap.put("orgId", organizationDTO.getId());
            orgMap.put("orgName", organizationDTO.getName());
            if (null != user) {
                orgMap.put("userId", user.getId());
                orgMap.put("userName", user.getName());
                orgMap.put("id", organizationDTO.getId() + "," + user.getId());
                orgMap.put("name", organizationDTO.getName() + "(" + user.getName() + ")");

            }
            result.add(orgMap);
        }
        return result;
    }
    /**
     * 获取电销组
     *
     * @param
     * @return
     */
    private List<Map<String, Object>> getSaleGroupListByBusinessLine(Integer businessLine) {

        // 查询所有电销总监
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserListByBusliness(businessLine, RoleCodeEnum.DXZJ.name(), statusList);
        Map<Long, UserInfoDTO> userMap = new HashMap<Long, UserInfoDTO>();
        // 生成<机构id，用户>map
        for (UserInfoDTO userInfoDTO : userList) {
            userMap.put(userInfoDTO.getOrgId(), userInfoDTO);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        // 生成结果集，匹配电销组以及电销总监
        for (UserInfoDTO user : userList) {
            Map<String, Object> orgMap = new HashMap<String, Object>();
            orgMap.put("orgId", user.getOrgId());
            orgMap.put("userId", user.getId());
            orgMap.put("userName", user.getName());
            orgMap.put("id", user.getOrgId() + "," + user.getId());
            orgMap.put("name", user.getOrgName() + "(" + user.getName() + ")");
            result.add(orgMap);
        }
        return result;
    }

    /**
     * 转移资源
     *
     * @param allocationClueReq
     * @return
     */
    @PostMapping("/transferClue")
    @ResponseBody
    @RequiresPermissions("customerManager:transferclue")
    @LogRecord(description = "转移资源", operationType = OperationType.TRANSFER,
            menuName = MenuEnum.TELE_CUSTOMER_MANAGER)
    public JSONResult transferClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
                                   BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        allocationClueReq.setOrg(user.getOrgId());
        return clueBasicFeignClient.transferClue(allocationClueReq);
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
        idEntity.setId(orgId + "");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if (!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}", idEntity, orgJr);
            return null;
        }
        return orgJr.getData();
    }
    /**
     * @Description:处理当前登录业务线是否能复制
     * @Param:
     * @return:
     * @author: fanjd
     * @date: 2020/8/12 14:32
     * @version: V1.0
     */
    private Boolean handleCanCopy(Integer businessLine) {
        // 获取外包业务线
        String wbBusinessLine = getSysSetting(SysConstant.WB_BUSINESSLINE);
        if (StringUtils.isBlank(wbBusinessLine)) {
            return  false;
        }
        List<String> wbBusinessLineList = Arrays.asList(wbBusinessLine.split(","));
        if (wbBusinessLineList.contains(String.valueOf(businessLine))) {
            return true;
        }
        return false;
    }
    /**
     * 查询系统参数
     */
    private String getSysSetting(String code) {
        SysSettingReq sysSettingReq = new SysSettingReq();
        sysSettingReq.setCode(code);
        JSONResult<SysSettingDTO> byCode = sysSettingFeignClient.getByCode(sysSettingReq);
        if (byCode != null && JSONResult.SUCCESS.equals(byCode.getCode())) {
            return byCode.getData().getValue();
        }
        return null;
    }
}
