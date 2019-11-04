package com.kuaidao.manageweb.controller.module;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.module.ModuleManagerFeignClient;
import com.kuaidao.sys.dto.module.ModuleInfoDTO;
import com.kuaidao.sys.dto.module.ModuleQueryDTO;
import com.kuaidao.sys.dto.module.OperationInfoDTO;

@Controller
@RequestMapping("/module/moduleManager")
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
		dto.setSystemCode(SystemCodeConstant.HUI_JU);
		String moduleId= request.getParameter("moduleId");
		JSONResult<List<TreeData>> treeJsonRes = moduleManagerFeignClient.queryModuleTree(dto);
		if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode()) && treeJsonRes.getData() != null) {
			request.setAttribute("moduleData", treeJsonRes.getData());
			if(null!=moduleId){
				ModuleQueryDTO queryDto = new ModuleQueryDTO();
				queryDto.setId(new Long(moduleId));
				JSONResult<ModuleInfoDTO> dtoJson = moduleManagerFeignClient.queryModuleById(queryDto);
				if (dtoJson != null && JSONResult.SUCCESS.equals(dtoJson.getCode()) && dtoJson.getData() != null) {
					ModuleInfoDTO  info=dtoJson.getData();
					TreeData tree=new TreeData();
					dtoJson.getData();
					tree.setLevel(info.getLevel());
					tree.setId(info.getId());
					tree.setLabel(info.getName());
					request.setAttribute("selectModule", tree);	
				}
			}
		
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
	public JSONResult<PageBean<ModuleInfoDTO>> queryModuleDataByPage(@RequestBody ModuleQueryDTO dto) {
		dto.setSystemCode(SystemCodeConstant.HUI_JU);
		JSONResult<PageBean<ModuleInfoDTO>> pageJson = moduleManagerFeignClient.queryModulePageList(dto);
		return pageJson;
	}

	/***
	 * 添加下级菜单
	 * 
	 * @return
	 */
	@RequestMapping("/addModulePre")
	public String addModulePre(HttpServletRequest request, Model model) {
		String moduleId = request.getParameter("moduleId");
		String levle = request.getParameter("level");
		model.addAttribute("moduleId", moduleId);
		model.addAttribute("levle", levle);
		return "module/addModulePage";
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
	@LogRecord(description = "菜单新增",operationType = LogRecord.OperationType.INSERT,menuName =MenuEnum.MODULE_MANAGEMENT)
	@ResponseBody
	public JSONResult<String> saveModuleInfo(@RequestBody ModuleInfoDTO dto) {
		dto.setSystemCode(SystemCodeConstant.HUI_JU);
		JSONResult<String> pageJson = moduleManagerFeignClient.saveModuleInfo(dto);
		return pageJson;
	}

	/**
	 * 修改菜单数据
	 * 
	 * @param pageN1um
	 * @param pageSize
	 * @param queryDTO
	 * @return
	 */
	@PostMapping("/updateModuleInfo")
	@LogRecord(description = "菜单修改",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.MODULE_MANAGEMENT)
	@ResponseBody
	public JSONResult<String> updateModuleInfo(@RequestBody ModuleInfoDTO dto) {
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
	@LogRecord(description = "菜单删除",operationType = LogRecord.OperationType.DELETE,menuName = MenuEnum.MODULE_MANAGEMENT)
	@ResponseBody
	public JSONResult<String> deleteModuleInfo(@RequestBody ModuleInfoDTO dto) {
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
	@RequestMapping("/queryModuleById")
	public String queryModuleById(HttpServletRequest request, Model model) {
		String moduleId = request.getParameter("moduleId");
		ModuleQueryDTO dto = new ModuleQueryDTO();
		dto.setId(new Long(moduleId));
		JSONResult<ModuleInfoDTO> dtoJson = moduleManagerFeignClient.queryModuleById(dto);
 
		if (dtoJson.getCode().equals(JSONResult.SUCCESS)) {
			ModuleInfoDTO moduleDto = dtoJson.getData();
			List<String> checkModuleId = new ArrayList<String>();
			List<String> checkOptions = new ArrayList<String>();
			checkOptions.add("增(add)");
			checkOptions.add("删(delete)");
			checkOptions.add("改(edit)");
			checkOptions.add("查(view)");
			if (null != moduleDto.getOperationInfos() && moduleDto.getOperationInfos().size() > 0) {
				for (OperationInfoDTO opt : moduleDto.getOperationInfos()) {
					checkModuleId.add(opt.getName() + "(" + opt.getCode() + ")");
					String dataInfo=opt.getName() + "(" + opt.getCode() + ")";
					if(!checkOptions.contains(dataInfo)){
						checkOptions.add(opt.getName() + "(" + opt.getCode() + ")");
					}
				}
			}

			moduleDto.setType(checkModuleId);

			model.addAttribute("checkOptions", checkOptions);

			model.addAttribute("dtoJson", moduleDto);
		}

		return "module/updateModulePage";
	}
	
	@PostMapping("/queryModuleByParam")
	@ResponseBody
	public JSONResult<List<ModuleInfoDTO>> queryModuleByParam(@RequestBody ModuleQueryDTO dto){
		return moduleManagerFeignClient.queryModuleByParam(dto);
	}
}
