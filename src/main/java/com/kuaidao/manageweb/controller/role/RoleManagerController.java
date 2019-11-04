/**
 * 
 */
package com.kuaidao.manageweb.controller.role;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.module.ModuleManagerFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldRespDTO;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
import com.kuaidao.sys.dto.module.ModuleInfoDTO;
import com.kuaidao.sys.dto.module.OperationInfoDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;

@Controller
@RequestMapping("/role/roleManager")
public class RoleManagerController {

	private static Logger logger = LoggerFactory.getLogger(RoleManagerController.class);

	@Autowired
	private RoleManagerFeignClient roleManagerFeignClient;

	@Autowired
	private ModuleManagerFeignClient moduleManagerFeignClient;

	@Autowired
	private UserInfoFeignClient userInfoFeignClient;

	@Autowired
	private CustomFieldFeignClient customFieldFeignClient;

	/***
	 * 
	 * @return
	 */
	@RequestMapping("/initRoleInfo")
	public String initRoleInfo() {
		return "role/roleManagePage";
	}

	/**
	 * 查询用户集合
	 * 
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/queryUserList")
	@ResponseBody
	public JSONResult<PageBean<UserInfoDTO>> queryUserList(@RequestBody UserInfoPageParam param) {
		return userInfoFeignClient.list(param);

	}

	/***
	 * 添加角色
	 * 
	 * @return
	 */
	@RequestMapping("/addRolePre")
	public String addRolePre(HttpServletRequest request) {

		JSONResult<List<IndexModuleDTO>> treeJsonRes = moduleManagerFeignClient
				.queryModuleShow(SystemCodeConstant.HUI_JU);
		if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode()) && treeJsonRes.getData() != null) {
			List<IndexModuleDTO> moduledtoList = treeJsonRes.getData();

			if (null != moduledtoList && moduledtoList.size() > 0) {

				for (IndexModuleDTO indexMoudel : moduledtoList) {

					List<ModuleInfoDTO> moduleList = indexMoudel.getSubList();

					for (ModuleInfoDTO module : moduleList) {

						List<String> checkedCities = new ArrayList<String>();

						module.setCheckedCities(checkedCities);
					}
				}
			}

			request.setAttribute("moduleData", treeJsonRes.getData());
		} else {
			logger.error("query module tree,res{{}}", treeJsonRes);
		}
		return "role/addRolePage";
	}

	/***
	 * 修改角色
	 * 
	 * @return
	 */
	@RequestMapping("/updateRolePre")
	public String updateRolePre(HttpServletRequest request, Model model) {
		String roleId = request.getParameter("roleId");
		RoleQueryDTO dto = new RoleQueryDTO();
		dto.setId(new Long(roleId));
		dto.setRoleName(roleId);
		JSONResult<RoleInfoDTO> roleJsonRes = roleManagerFeignClient.qeuryRoleById(dto);
		RoleInfoDTO roledto = null;
		if (roleJsonRes != null && JSONResult.SUCCESS.equals(roleJsonRes.getCode()) && roleJsonRes.getData() != null) {
			roledto = roleJsonRes.getData();
			if (null != roledto) {

				model.addAttribute("ipListTable", roledto.getIpPackages());

				model.addAttribute("roleInfo", roledto);
			}
		}
		JSONResult<List<IndexModuleDTO>> treeJsonRes = moduleManagerFeignClient
				.queryModuleShow(SystemCodeConstant.HUI_JU);

		if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode()) && treeJsonRes.getData() != null) {

			List<IndexModuleDTO> moduledtoList = treeJsonRes.getData();

			if (null != moduledtoList && moduledtoList.size() > 0) {

				for (IndexModuleDTO indexMoudel : moduledtoList) {

					List<ModuleInfoDTO> moduleList = indexMoudel.getSubList();

					for (ModuleInfoDTO module : moduleList) {

						List<String> checkedCities = new ArrayList<String>();
						List<OperationInfoDTO> operationInfos = module.getOperationInfos();
						for (OperationInfoDTO operation : operationInfos) {
							if (null != roledto) {
								List<OperationInfoDTO> roleOperations = roledto.getOperations();
								for (OperationInfoDTO roleOpe : roleOperations) {

									if (roleOpe.getId().equals(operation.getId())) {

										checkedCities.add(operation.getId() + "");
										module.setCheckAll(true);
										break;
									}

								}

							}

						}
						module.setCheckedCities(checkedCities);
					}
				}
			}

			request.setAttribute("moduleData", treeJsonRes.getData());
		} else {
			logger.error("query module tree,res{{}}", treeJsonRes);
		}
		return "role/updateRolePage";
	}

	/**
	 * 查询角色列表
	 * 
	 * @param dto
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/queryRoleList")
	@ResponseBody
	public JSONResult<List<RoleInfoDTO>> queryRoleList(@RequestBody RoleQueryDTO dto, HttpServletRequest request,
			HttpServletResponse response) {
		return roleManagerFeignClient.queryRoleList(dto);
	}

	/**
	 * 查询角色列表
	 * 
	 * @param dto
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/queryCustomField")
	@ResponseBody
	public JSONResult<List<CustomFieldRespDTO>> queryCustomField(@RequestBody CustomFieldQueryDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return customFieldFeignClient.queryCustomField(dto.getMenuCode());
	}

	/**
	 * 保存角色数据
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/saveRoleInfo")
	@LogRecord(description = "角色新增", operationType = LogRecord.OperationType.INSERT, menuName = MenuEnum.ROLE_MANAGEMENT)
	@ResponseBody
	public JSONResult<String> saveRoleInfo(@RequestBody RoleInfoDTO dto) {
		dto.setSystemCode(SystemCodeConstant.HUI_JU);
		JSONResult<String> pageJson = roleManagerFeignClient.saveRoleInfo(dto);
		return pageJson;
	}

	/**
	 * 修改角色数据
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/updateRoleInfo")
	@LogRecord(description = "角色修改", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.ROLE_MANAGEMENT)
	@ResponseBody
	public JSONResult<String> updateRoleInfo(@RequestBody RoleInfoDTO dto) {
		dto.setSystemCode(SystemCodeConstant.HUI_JU);
		JSONResult<String> pageJson = roleManagerFeignClient.updateRoleInfo(dto);
		return pageJson;
	}

	/**
	 * 删除角色数据
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/deleteRoleInfo")
	@LogRecord(description = "角色删除", operationType = LogRecord.OperationType.DELETE, menuName = MenuEnum.ROLE_MANAGEMENT)
	@ResponseBody
	public JSONResult<String> deleteRoleInfo(@RequestBody RoleInfoDTO dto) {
		JSONResult<String> pageJson = roleManagerFeignClient.deleteRoleInfo(dto);
		return pageJson;
	}

	@PostMapping("/qeuryRoleByName")
	@ResponseBody
	public JSONResult<List<RoleInfoDTO>> qeuryRoleByName(@RequestBody RoleQueryDTO roleDTO) {
		return roleManagerFeignClient.qeuryRoleByName(roleDTO);
	}

	/**
	 * 删除角色时判断是否有用户关联
	 * 
	 * @param roleDTO
	 * @return
	 */

	@PostMapping("/qeuryRoleByUserId")
	@ResponseBody
	public JSONResult<List<RoleInfoDTO>> qeuryRoleByUserId(@RequestBody RoleQueryDTO roleDTO) {
		return roleManagerFeignClient.qeuryRoleByUserId(roleDTO);
	}

}
