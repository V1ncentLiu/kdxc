package com.kuaidao.manageweb.controller.assignrule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.businessconfig.dto.assignrule.TeleAssignRuleQueryDTO;
import com.kuaidao.businessconfig.dto.assignrule.TelemarketingAssignRuleDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.assignrule.TelemarketingAssignRuleFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/assignrule/teleAssignRule")
public class TelemarketingAssignRuleContoller {

    @Autowired
    private TelemarketingAssignRuleFeignClient telemarketingAssignRuleFeignClient;

    @Autowired
    private RoleManagerFeignClient roleManagerFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @RequestMapping("/initteleAssignRule")
    @RequiresPermissions("teleAssignRule:view")
    public String initinfoAssign(HttpServletRequest request, Model model) {

        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        // 查询电销分公司
        orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXFGS);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationRespDTO>> orgComJson =
                organizationFeignClient.queryOrgByParam(orgDto);
        if (orgComJson.getCode().equals(JSONResult.SUCCESS)) {
            model.addAttribute("orgCompany", orgComJson.getData());
        }
        // 电销事业部
        orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DZSYB);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationRespDTO>> orgDeptJson =
                organizationFeignClient.queryOrgByParam(orgDto);
        if (orgDeptJson.getCode().equals(JSONResult.SUCCESS)) {
            model.addAttribute("orgDept", orgDeptJson.getData());
        }
        // 查询电销组
        orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationRespDTO>> orgJson =
                organizationFeignClient.queryOrgByParam(orgDto);
        if (orgJson.getCode().equals(JSONResult.SUCCESS)) {
            model.addAttribute("orgSelect", orgJson.getData());
        }
        return "assignrule/telemarketingAllotRule";
    }

    /***
     * 展现电销分配规则页面
     * 
     * @return
     */
    @RequestMapping("/queryTeleAssignRuleList")
    @ResponseBody
    public JSONResult<PageBean<TelemarketingAssignRuleDTO>> queryTeleAssignRuleList(
            @RequestBody TeleAssignRuleQueryDTO queryDTO, HttpServletRequest request,
            HttpServletResponse response) {
        // 获取当前登录用户的机构信息//
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        List<Long> orgList = new ArrayList<>();
        JSONResult<PageBean<TelemarketingAssignRuleDTO>> json = new JSONResult<>();
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            String roleCode = user.getRoleList().get(0).getRoleCode();
            if (null != roleCode) {
                // 管理员查看所有
                if (roleCode.equals(RoleCodeEnum.GLY.name())) {
                    orgList = getTelList(queryDTO);
                    if (queryDTO.getTelemarketingId() != null || queryDTO.getTeleDepart() != null
                            || queryDTO.getTeleCompany() != null) {
                        queryDTO.setFieldList(orgList);
                    } else {
                        json = telemarketingAssignRuleFeignClient.queryTeleAssignRulePage(queryDTO);
                        return json;
                    }

                } else if (roleCode.equals(RoleCodeEnum.DXZC.name())
                        || roleCode.equals(RoleCodeEnum.DXZJL.name())
                        || roleCode.equals(RoleCodeEnum.DXFZ.name())) {
                    // 电销总裁、电销总经理、电销副总、查看所有下级电销的数据
                    List<Long> loginOrgList = this.queryTeleOrgInfoList(user.getOrgId());
                    if (queryDTO.getTelemarketingId() != null || queryDTO.getTeleDepart() != null
                            || queryDTO.getTeleCompany() != null) {
                        orgList = getTelList(queryDTO);
                        loginOrgList.retainAll(orgList);
                    }

                    queryDTO.setFieldList(loginOrgList);

                } else if (roleCode.equals(RoleCodeEnum.DXZJ.name())) {
                    List<Long> orgLists = new ArrayList<>();
                    orgLists.add(user.getOrgId());
                    if (queryDTO.getTelemarketingId() != null || queryDTO.getTeleDepart() != null
                            || queryDTO.getTeleCompany() != null) {
                        orgList = getTelList(queryDTO);
                        orgLists.retainAll(orgList);
                        queryDTO.setFieldList(orgLists);
                    } else {
                        // 电销总监查看自己创建的
                        queryDTO.setFieldList(orgLists);
                    }

                } else {
                    queryDTO.setOther("1!=1");
                }
            }

        }

        if (queryDTO.getFieldList() != null && queryDTO.getFieldList().size() > 0) {
            json = telemarketingAssignRuleFeignClient.queryTeleAssignRulePage(queryDTO);
        } else {
            // 包装分页pageBean
            PageBean<TelemarketingAssignRuleDTO> pageInfoDto =
                    new PageBean<TelemarketingAssignRuleDTO>();
            pageInfoDto.setCurrentPage(1);
            pageInfoDto.setPageSize(20);
            pageInfoDto.setTotal(0);
            pageInfoDto.setPageSizes(0);
            pageInfoDto.setData(null);
            json = new JSONResult<PageBean<TelemarketingAssignRuleDTO>>().success(pageInfoDto);
        }
        return json;
    }

    public List<Long> getTelList(TeleAssignRuleQueryDTO queryDTO) {
        List<Long> orgList = new ArrayList<>();
        if (queryDTO.getTelemarketingId() != null) {
            orgList.add(queryDTO.getTelemarketingId());
        }
        List<Long> departOrgList = new ArrayList<>();
        if (queryDTO.getTeleDepart() != null) {
            // 根据选择的电销事业部查询下面所有电销组
            String teleDeptStr =
                    this.queryTeleOrgInfoStr(queryDTO.getTeleDepart(), OrgTypeConstant.DXZ);
            if (org.apache.commons.lang.StringUtils.isNotBlank(teleDeptStr)) {
                for (String str : teleDeptStr.split(",")) {
                    departOrgList.add(Long.parseLong(str));
                }
            }
            if (queryDTO.getTelemarketingId() != null) {
                if (orgList.size() > 0) {
                    orgList.retainAll(departOrgList);
                }
            } else {
                orgList = departOrgList;
            }


        }
        List<Long> companyorgList = new ArrayList<>();
        if (queryDTO.getTeleCompany() != null) {
            String teleComStr =
                    this.queryTeleOrgInfoStr(queryDTO.getTeleCompany(), OrgTypeConstant.DZSYB);
            if (org.apache.commons.lang.StringUtils.isNotBlank(teleComStr)) {
                for (String str : teleComStr.split(",")) {
                    companyorgList.add(Long.parseLong(str));
                }
            }
            if (queryDTO.getTelemarketingId() != null || queryDTO.getTeleDepart() != null) {
                if (orgList.size() > 0) {
                    orgList.retainAll(companyorgList);
                }
            } else {
                orgList = companyorgList;
            }
        }
        return orgList;
    }

    /**
     * 查询机构下的所有电销组
     * 
     * @param parentId
     * @return
     */

    private String queryTeleOrgInfoStr(Long parentId, int orgType) {

        // 电销分公司
        if (null != parentId) {
            OrganizationQueryDTO dto = new OrganizationQueryDTO();

            dto.setParentId(parentId);

            dto.setOrgType(orgType);
            dto.setSystemCode(SystemCodeConstant.HUI_JU);

            JSONResult<List<OrganizationDTO>> orgJson =
                    organizationFeignClient.listDescenDantByParentId(dto);

            List<Long> idList = new ArrayList<Long>();

            if (orgJson.getCode().equals(JSONResult.SUCCESS)) {

                List<OrganizationDTO> orgList = orgJson.getData();

                if (null != orgList && orgList.size() > 0) {

                    for (OrganizationDTO org : orgList) {
                        idList.add(org.getId());
                    }
                }
            }
            if (null != idList && idList.size() > 0) {

                return StringUtils.collectionToDelimitedString(idList, ",");
            }
        }
        return null;
    }

    /**
     * 查询机构下的所有电销组
     * 
     * @param parentId
     * @return
     */

    private List<Long> queryTeleOrgInfoList(Long parentId) {

        if (null != parentId) {
            OrganizationQueryDTO dto = new OrganizationQueryDTO();

            dto.setParentId(parentId);

            dto.setOrgType(OrgTypeConstant.DXZ);

            JSONResult<List<OrganizationDTO>> orgJson =
                    organizationFeignClient.listDescenDantByParentId(dto);

            List<Long> idList = new ArrayList<Long>();

            if (orgJson.getCode().equals(JSONResult.SUCCESS)) {

                List<OrganizationDTO> orgList = orgJson.getData();

                if (null != orgList && orgList.size() > 0) {

                    for (OrganizationDTO org : orgList) {
                        idList.add(org.getId());
                    }
                }
            }
            if (null != idList && idList.size() > 0) {

                return idList;
            }
        }
        return null;
    }

    /***
     * 新增打开页面
     * 
     * @return
     */
    @RequestMapping("/preSaveTeleAssignRule")
    @RequiresPermissions("teleAssignRule:add")
    public String preSaveTeleAssignRule(HttpServletRequest request, Model model) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        // 查询组织下电销顾问
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> saleList =
                getUserList(user.getOrgId(), RoleCodeEnum.DXCYGW.name(), statusList);
        // “经纪人”人员
        List<UserInfoDTO> jjSaleList =
                getUserList(user.getOrgId(), RoleCodeEnum.JJJL.name(), statusList);
        if( null == saleList ){

            saleList = Collections.emptyList();
        }
        if( null == jjSaleList ){

            jjSaleList = Collections.emptyList();
        }
        saleList.addAll(jjSaleList);
        model.addAttribute("orgUserList", saleList);
        return "assignrule/addtelemarketingRule";
    }

    /***
     * 修改打开页面
     * 
     * @return
     */
    @RequestMapping("/preUpdateTeleAssignRule")
    @RequiresPermissions("teleAssignRule:edit")
    public String preUpdateTeleAssignRule(HttpServletRequest request, Model model) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        // 查询组织下电销顾问
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> saleList =
                getUserList(user.getOrgId(), RoleCodeEnum.DXCYGW.name(), statusList);
        model.addAttribute("orgUserList", saleList);
        String ruleId = request.getParameter("ruleId");
        TeleAssignRuleQueryDTO dto = new TeleAssignRuleQueryDTO();
        dto.setId(new Long(ruleId));
        JSONResult<TelemarketingAssignRuleDTO> ruleDtoJson =
                telemarketingAssignRuleFeignClient.queryTeleAssignRuleById(dto);
        if (ruleDtoJson.getCode().equals(JSONResult.SUCCESS)) {

            model.addAttribute("updateRule", ruleDtoJson.getData());

        }

        return "assignrule/updatetelemarketingRule";
    }

    /***
     * 保存电销分配规则
     * 
     * @return
     */
    @RequestMapping("/saveTeleAssignRule")
    @ResponseBody
    @LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.ASSIGNRULE_TELE)
    public JSONResult<String> saveTeleAssignRule(@RequestBody TelemarketingAssignRuleDTO dto,
            HttpServletRequest request, HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        dto.setCreateUser(user.getId());
        dto.setTelemarketingId(user.getOrgId());
        return telemarketingAssignRuleFeignClient.saveTeleAssignRule(dto);
    }

    /***
     * 修改电销分配规则
     * 
     * @return
     */
    @RequestMapping("/updateTeleAssignRule")
    @ResponseBody
    @LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.ASSIGNRULE_TELE)
    public JSONResult<String> updateTeleAssignRule(@RequestBody TelemarketingAssignRuleDTO dto,
            HttpServletRequest request, HttpServletResponse response) {
        return telemarketingAssignRuleFeignClient.updateTeleAssignRule(dto);
    }

    /***
     * 删除电销分配规则
     * 
     * @return
     */
    @RequestMapping("/deleteTeleAssignRule")
    @RequiresPermissions("teleAssignRule:delete")
    @LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.ASSIGNRULE_TELE)
    @ResponseBody
    public JSONResult<String> deleteTeleAssignRule(@RequestBody TelemarketingAssignRuleDTO dto,
            HttpServletRequest request, HttpServletResponse response) {
        return telemarketingAssignRuleFeignClient.deleteTeleAssignRule(dto);
    }

    /***
     * 修改电销分配规则状态
     * 
     * @return
     */
    @RequestMapping("/updateTeleAssignRuleStatus")
    @ResponseBody
    @LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.ASSIGNRULE_TELE)
    public JSONResult<String> updateTeleAssignRuleStatus(
            @RequestBody TelemarketingAssignRuleDTO dto, HttpServletRequest request,
            HttpServletResponse response) {
        return telemarketingAssignRuleFeignClient.updateTeleAssignRuleStatus(dto);
    }

    /***
     * 修改电销分配规则状态
     * 
     * @return
     */
    @RequestMapping("/queryTeleAssignRuleById")
    @ResponseBody
    public JSONResult<TelemarketingAssignRuleDTO> queryTeleAssignRuleById(
            @RequestBody TeleAssignRuleQueryDTO dto, HttpServletRequest request,
            HttpServletResponse response) {
        return telemarketingAssignRuleFeignClient.queryTeleAssignRuleById(dto);
    }

    /***
     * 修改电销分配规则状态
     * 
     * @return
     */
    @RequestMapping("/queryTeleAssignRuleByName")
    @ResponseBody
    @LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.DISABLE,
            menuName = MenuEnum.ASSIGNRULE_TELE)
    public JSONResult<List<TelemarketingAssignRuleDTO>> queryTeleAssignRuleByName(
            @RequestBody TeleAssignRuleQueryDTO dto, HttpServletRequest request,
            HttpServletResponse response) {
        return telemarketingAssignRuleFeignClient.queryTeleAssignRuleByName(dto);
    }

    /**
     * 根据机构和角色类型获取用户
     * 
     * @param orgDTO
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
}
