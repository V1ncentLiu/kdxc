package com.kuaidao.manageweb.controller.assignrule;

import java.util.ArrayList;
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

import com.kuaidao.aggregation.dto.assignrule.TeleAssignRuleQueryDTO;
import com.kuaidao.aggregation.dto.assignrule.TelemarketingAssignRuleDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.assignrule.TelemarketingAssignRuleFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;

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
		orgDto.setOrgType(OrgTypeConstant.ZSZX);
		JSONResult<List<OrganizationRespDTO>> orgComJson = organizationFeignClient.queryOrgByParam(orgDto);
		if (orgComJson.getCode().equals(JSONResult.SUCCESS)) {
			model.addAttribute("orgCompany", orgComJson.getData());
		}
		// 电销事业部
		orgDto = new OrganizationQueryDTO();
		orgDto.setOrgType(OrgTypeConstant.DZSYB);
		JSONResult<List<OrganizationRespDTO>> orgDeptJson = organizationFeignClient.queryOrgByParam(orgDto);
		if (orgDeptJson.getCode().equals(JSONResult.SUCCESS)) {
			model.addAttribute("orgDept", orgDeptJson.getData());
		}
		// 查询电销组
		orgDto = new OrganizationQueryDTO();
		orgDto.setOrgType(OrgTypeConstant.DXZ);
		JSONResult<List<OrganizationRespDTO>> orgJson = organizationFeignClient.queryOrgByParam(orgDto);
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
			@RequestBody TeleAssignRuleQueryDTO queryDTO, HttpServletRequest request, HttpServletResponse response) {
		// 获取当前登录用户的机构信息//
		Subject subject = SecurityUtils.getSubject();
		UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
		if (null != user.getRoleList() && user.getRoleList().size() > 0) {
			String roleCode = user.getRoleList().get(0).getRoleCode();
			if (null != roleCode) {
				if (roleCode.equals(RoleCodeEnum.GLY.name())) {
					// 管理员查看所有
				} else if (roleCode.equals(RoleCodeEnum.DXZC.name()) || roleCode.equals(RoleCodeEnum.DXZJL.name())
						|| roleCode.equals(RoleCodeEnum.DXFZ.name())) {
					// 电销总裁、电销总经理、电销副总、查看所有下级电销的数据
					List<Long> orgList = this.queryTeleOrgInfoList(user.getOrgId());
					queryDTO.setFieldList(orgList);

				} else if (roleCode.equals(RoleCodeEnum.DXZJ.name())) {
					// 电销总监查看自己创建的
					queryDTO.setCreateUser(user.getId());
				} else {
					queryDTO.setOther("1!=1");
				}
			}

			// 根据选择的电销公司查询下面所有电销组
			String teleComStr = this.queryTeleOrgInfoStr(queryDTO.getTeleCompany());

			queryDTO.setTeleCompanyStr(teleComStr);

			// 根据选择的电销事业部查询下面所有电销组
			String teleDeptStr = this.queryTeleOrgInfoStr(queryDTO.getTeleDepart());

			queryDTO.setTeleDepartStr(teleDeptStr);
		}

		return telemarketingAssignRuleFeignClient.queryTeleAssignRulePage(queryDTO);
	}

	/**
	 * 查询机构下的所有电销组
	 * 
	 * @param parentId
	 * @return
	 */

	private String queryTeleOrgInfoStr(Long parentId) {

		// 电销分公司
		if (null != parentId) {
			OrganizationQueryDTO dto = new OrganizationQueryDTO();

			dto.setParentId(parentId);

			dto.setOrgType(OrgTypeConstant.DXZ);

			JSONResult<List<OrganizationDTO>> orgJson = organizationFeignClient.listDescenDantByParentId(dto);

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

			JSONResult<List<OrganizationDTO>> orgJson = organizationFeignClient.listDescenDantByParentId(dto);

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
		RoleQueryDTO query = new RoleQueryDTO();
		query.setRoleCode(RoleCodeEnum.DXCYGW.name());
		Subject subject = SecurityUtils.getSubject();
		UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
		JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
		if (roleJson.getCode().equals(JSONResult.SUCCESS)) {
			List<RoleInfoDTO> roleList = roleJson.getData();
			if (null != roleList && roleList.size() > 0) {
				RoleInfoDTO roleDto = roleList.get(0);
				UserInfoPageParam param = new UserInfoPageParam();
				param.setRoleId(roleDto.getId());
				param.setOrgId(user.getOrgId());
				param.setPageSize(10000);
				param.setPageNum(1);
				JSONResult<PageBean<UserInfoDTO>> userListJson = userInfoFeignClient.list(param);
				if (userListJson.getCode().equals(JSONResult.SUCCESS)) {
					PageBean<UserInfoDTO> pageList = userListJson.getData();
					List<UserInfoDTO> userList = pageList.getData();
					model.addAttribute("orgUserList", userList);

				}
			}
		}
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
		RoleQueryDTO query = new RoleQueryDTO();
		query.setRoleCode(RoleCodeEnum.DXCYGW.name());
		Subject subject = SecurityUtils.getSubject();
		UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
		JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
		if (roleJson.getCode().equals(JSONResult.SUCCESS)) {
			List<RoleInfoDTO> roleList = roleJson.getData();
			if (null != roleList && roleList.size() > 0) {
				RoleInfoDTO roleDto = roleList.get(0);
				UserInfoPageParam param = new UserInfoPageParam();
				param.setRoleId(roleDto.getId());
				param.setOrgId(user.getOrgId());
				param.setPageSize(10000);
				param.setPageNum(1);
				JSONResult<PageBean<UserInfoDTO>> userListJson = userInfoFeignClient.list(param);
				if (userListJson.getCode().equals(JSONResult.SUCCESS)) {
					PageBean<UserInfoDTO> pageList = userListJson.getData();
					List<UserInfoDTO> userList = pageList.getData();
					model.addAttribute("orgUserList", userList);

				}
			}
		}
		String ruleId = request.getParameter("ruleId");
		TeleAssignRuleQueryDTO dto = new TeleAssignRuleQueryDTO();
		dto.setId(new Long(ruleId));
		JSONResult<TelemarketingAssignRuleDTO> ruleDtoJson = telemarketingAssignRuleFeignClient
				.queryTeleAssignRuleById(dto);
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
	@LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.INSERT, menuName = MenuEnum.ASSIGNRULE_TELE)
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
	@LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.ASSIGNRULE_TELE)
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
	@LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.DELETE, menuName = MenuEnum.ASSIGNRULE_TELE)
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
	@LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.ASSIGNRULE_TELE)
	public JSONResult<String> updateTeleAssignRuleStatus(@RequestBody TelemarketingAssignRuleDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return telemarketingAssignRuleFeignClient.updateTeleAssignRuleStatus(dto);
	}

	/***
	 * 修改电销分配规则状态
	 * 
	 * @return
	 */
	@RequestMapping("/queryTeleAssignRuleById")
	@ResponseBody
	public JSONResult<TelemarketingAssignRuleDTO> queryTeleAssignRuleById(@RequestBody TeleAssignRuleQueryDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return telemarketingAssignRuleFeignClient.queryTeleAssignRuleById(dto);
	}

	/***
	 * 修改电销分配规则状态
	 * 
	 * @return
	 */
	@RequestMapping("/queryTeleAssignRuleByName")
	@ResponseBody
	@LogRecord(description = "电销分配规则", operationType = LogRecord.OperationType.DISABLE, menuName = MenuEnum.ASSIGNRULE_TELE)
	public JSONResult<List<TelemarketingAssignRuleDTO>> queryTeleAssignRuleByName(
			@RequestBody TeleAssignRuleQueryDTO dto, HttpServletRequest request, HttpServletResponse response) {
		return telemarketingAssignRuleFeignClient.queryTeleAssignRuleByName(dto);
	}
}
