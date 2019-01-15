/**
 * 
 */
package com.kuaidao.manageweb.controller.role;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.module.ModuleManagerFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
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
	private   ModuleManagerFeignClient   moduleManagerFeignClient;
	
	@Autowired
	private  UserInfoFeignClient userInfoFeignClient;

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
    public JSONResult<PageBean<UserInfoDTO>> queryUserList(@RequestBody UserInfoPageParam param){
    	return userInfoFeignClient.list(param);
    	
    }

	/***
	 * 添加角色
	 * 
	 * @return
	 */
	@RequestMapping("/addRolePre")
	public String addRolePre(HttpServletRequest request) {
	 
		JSONResult<List<IndexModuleDTO>> treeJsonRes = moduleManagerFeignClient.queryModuleShow("huiju");
		if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode()) && treeJsonRes.getData() != null) {
			request.setAttribute("moduleData", treeJsonRes.getData());
		} else {
			logger.error("query module tree,res{{}}", treeJsonRes);
		}
		return "role/addRolePage";
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
	 * 保存角色数据
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/saveRoleInfo")
	@ResponseBody
	public JSONResult<String> saveRoleInfo(@RequestBody RoleInfoDTO dto) {
		dto.setSystemCode("huiju");
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
	@ResponseBody
	public JSONResult<String> updateRoleInfo(@RequestBody RoleInfoDTO dto) {
		JSONResult<String> pageJson = roleManagerFeignClient.updateRoleInfo(dto);
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
	@PostMapping("/deleteRoleInfo")
	@ResponseBody
	public JSONResult<String> deleteRoleInfo(@RequestBody RoleInfoDTO dto) {
		JSONResult<String> pageJson = roleManagerFeignClient.deleteRoleInfo(dto);
		return pageJson;
	}
	
 

}
