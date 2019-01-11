package com.kuaidao.manageweb.controller.module;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.manageweb.feign.module.ModuleManagerFeignClient;
import com.kuaidao.sys.dto.module.ModuleInfoDTO;
import com.kuaidao.sys.dto.module.ModuleQueryDTO;

@Controller
@RequestMapping("/module/roleManager")
public class ModuleManagerController {
	private static Logger logger = LoggerFactory.getLogger(ModuleManagerController.class);

	@Autowired
	private ModuleManagerFeignClient moduleManagerFeignClient;

	/***
	 * 初始化菜单管理页面
	 * 
	 * @return
	 */
	@RequestMapping("/initModuleInfo")
	public String initModuleInfo(HttpServletRequest request) {
		ModuleQueryDTO dto = new ModuleQueryDTO();
		dto.setSystemCode("huiju");
		JSONResult<List<TreeData>> treeJsonRes = moduleManagerFeignClient.queryModuleTree(dto);
		if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode()) && treeJsonRes.getData() != null) {
			request.setAttribute("moduleData", treeJsonRes.getData());
		} else {
			logger.error("query module tree,res{{}}", treeJsonRes);
		}
		return "module/moduleManager";
	}

	/**
	 * 分页 查询菜单信息
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/queryModuleDataByPage")
	@ResponseBody
	public JSONResult<PageBean<ModuleInfoDTO>> queryModuleDataByPage(ModuleQueryDTO dto) {
		JSONResult<PageBean<ModuleInfoDTO>> pageJson = moduleManagerFeignClient.queryModulePageList(dto);
		return pageJson;
	}

	/**
	 * 保存菜单数据
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/saveModuleInfo")
	@ResponseBody
	public JSONResult<String> saveModuleInfo(ModuleInfoDTO dto) {
		JSONResult<String> pageJson = moduleManagerFeignClient.saveModuleInfo(dto);
		return pageJson;
	}

	/**
	 * 修改菜单数据
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/updateModuleInfo")
	@ResponseBody
	public JSONResult<String> updateModuleInfo(ModuleInfoDTO dto) {
		JSONResult<String> pageJson = moduleManagerFeignClient.updateModuleInfo(dto);
		return pageJson;
	}

	/**
	 * 删除菜单数据
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/deleteModuleInfo")
	@ResponseBody
	public JSONResult<String> deleteModuleInfo(ModuleInfoDTO dto) {
		JSONResult<String> pageJson = moduleManagerFeignClient.deleteModuleInfo(dto);
		return pageJson;
	}

	/**
	 * 查询修改数据
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/queryModuleById")
	@ResponseBody
	public JSONResult<ModuleInfoDTO> queryModuleById(ModuleQueryDTO dto) {
		JSONResult<ModuleInfoDTO> pageJson = moduleManagerFeignClient.queryModuleById(dto);
		return pageJson;
	}

}
