package com.kuaidao.manageweb.controller.assignrule;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.assignrule.InfoAssignDTO;
import com.kuaidao.aggregation.dto.assignrule.InfoAssignQueryDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;

@Controller
@RequestMapping("/assignrule/infoAssign")
public class InfoAssignContoller {

	@Autowired
	private InfoAssignFeignClient infoAssignFeignClient;

	@Autowired
	private OrganizationFeignClient organizationFeignClient;

	@Autowired
	private ProjectInfoFeignClient projectInfoFeignClient;

	@Autowired
	private RoleManagerFeignClient roleManagerFeignClient;

	@Autowired
	private UserInfoFeignClient userInfoFeignClient;

	/***
	 * 初始化信息流分配页面
	 * 
	 * @return
	 */
	@RequestMapping("/initinfoAssign")
	public String initinfoAssign(HttpServletRequest request, Model model) {

		OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
		orgDto.setOrgType(OrgTypeConstant.DXZ);

		JSONResult<List<OrganizationRespDTO>> orgJson = organizationFeignClient.queryOrgByParam(orgDto);
		// 电销组
		if (orgJson.getCode().equals(JSONResult.SUCCESS)) {
			model.addAttribute("orgSelect", orgJson.getData());
		}

		ProjectInfoPageParam param = new ProjectInfoPageParam();
		// 项目
		JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
		if (proJson.getCode().equals(JSONResult.SUCCESS)) {
			model.addAttribute("proSelect", proJson.getData());
		}

		return "assignrule/infoAssignRule";
	}

	/***
	 * 展现信息流分配页面
	 * 
	 * @return
	 */
	@RequestMapping("/queryInfoAssignList")
	@ResponseBody
	public JSONResult<PageBean<InfoAssignDTO>> queryInfoAssignList(@RequestBody InfoAssignQueryDTO queryDTO,
			HttpServletRequest request, HttpServletResponse response) {
		return infoAssignFeignClient.queryInfoAssignPage(queryDTO);
	}

	/***
	 * 查询所有电销组
	 * 
	 * @return
	 */
	@RequestMapping("/querySelectOrg")
	@ResponseBody
	public JSONResult<List<OrganizationRespDTO>> querySelectOrg(HttpServletRequest request,
			HttpServletResponse response) {

		OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
		orgDto.setOrgType(OrgTypeConstant.DXZ);
		// 电销组
		return organizationFeignClient.queryOrgByParam(orgDto);

	}

	/***
	 * 查询所有项目
	 * 
	 * @return
	 */
	@RequestMapping("/querySelecProject")
	@ResponseBody
	public JSONResult<List<ProjectInfoDTO>> querySelecProject(HttpServletRequest request,
			HttpServletResponse response) {

		ProjectInfoPageParam param = new ProjectInfoPageParam();
		// 电销组
		return projectInfoFeignClient.listNoPage(param);

	}

	/***
	 * 保存信息流分配规则
	 * 
	 * @return
	 */
	@RequestMapping("/saveInfoAssign")
	@ResponseBody
	public JSONResult<String> saveInfoAssign(@RequestBody InfoAssignDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		Long orgId = dto.getTelemarketingId();
		if (null != orgId) {
			RoleQueryDTO query = new RoleQueryDTO();
			query.setRoleCode(RoleCodeEnum.DXZJ.name());
			JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
			if (roleJson.getCode().equals(JSONResult.SUCCESS)) {
				List<RoleInfoDTO> roleList = roleJson.getData();
				if (null != roleList && roleList.size() > 0) {
					RoleInfoDTO roleDto = roleList.get(0);
					UserInfoPageParam param = new UserInfoPageParam();
					param.setRoleId(roleDto.getId());
					param.setOrgId(orgId);
					param.setPageSize(10000);
					param.setPageNum(1);
					JSONResult<PageBean<UserInfoDTO>> userListJson = userInfoFeignClient.list(param);
					if (userListJson.getCode().equals(JSONResult.SUCCESS)) {
						PageBean<UserInfoDTO> pageList = userListJson.getData();
						List<UserInfoDTO> userList = pageList.getData();
						if (null != userList && userList.size() > 0) {
							dto.setTelemarketingDirectorId(userList.get(0).getId());

						}

					}
				}
			}
		}

		return infoAssignFeignClient.saveInfoAssign(dto);
	}

	/***
	 * 修改信息流分配规则
	 * 
	 * @return
	 */
	@RequestMapping("/updateInfoAssign")
	@ResponseBody
	public JSONResult<String> updateInfoAssign(@RequestBody InfoAssignDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		return infoAssignFeignClient.saveInfoAssign(dto);
	}

	/***
	 * 删除保存信息流分配规则
	 * 
	 * @return
	 */
	@RequestMapping("/deleteInfoAssign")
	@ResponseBody
	public JSONResult<String> deleteInfoAssign(@RequestBody InfoAssignDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		return infoAssignFeignClient.delteInfoAssign(dto);
	}

	/***
	 * 根据主键查询信息流分配规则
	 * 
	 * @return
	 */
	@RequestMapping("/queryInfoAssignById")
	@ResponseBody
	public JSONResult<InfoAssignDTO> queryInfoAssignById(@RequestBody InfoAssignQueryDTO queryDto,
			HttpServletRequest request, HttpServletResponse response) {
		return infoAssignFeignClient.queryInfoAssignById(queryDto);
	}

}
