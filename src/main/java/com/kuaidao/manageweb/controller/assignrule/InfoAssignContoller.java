package com.kuaidao.manageweb.controller.assignrule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.assignrule.InfoAssignDTO;
import com.kuaidao.aggregation.dto.assignrule.InfoAssignQueryDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;

@Controller
@RequestMapping("/assignrule/infoAssign")
public class InfoAssignContoller {

	@Autowired
	private InfoAssignFeignClient infoAssignFeignClient;

	/***
	 * 初始化信息流分配页面
	 * 
	 * @return
	 */
	@RequestMapping("/initinfoAssign")
	public String initinfoAssign() {
		return "assignrule/";
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
		return infoAssignFeignClient.querInfoAssignPage(queryDTO);
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
