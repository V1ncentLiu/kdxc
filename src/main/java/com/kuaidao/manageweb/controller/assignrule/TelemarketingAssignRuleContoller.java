package com.kuaidao.manageweb.controller.assignrule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.assignrule.InfoAssignDTO;
import com.kuaidao.aggregation.dto.assignrule.TeleAssignRuleQueryDTO;
import com.kuaidao.aggregation.dto.assignrule.TelemarketingAssignRuleDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.assignrule.TelemarketingAssignRuleFeignClient;

@Controller
@RequestMapping("/assignrule/teleAssignRule")
public class TelemarketingAssignRuleContoller {

	@Autowired
	private TelemarketingAssignRuleFeignClient telemarketingAssignRuleFeignClient;

	@RequestMapping("/initteleAssignRule")
	public String initinfoAssign() {
		return "assignrule/";
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
		return telemarketingAssignRuleFeignClient.queryTeleAssignRulePage(queryDTO);
	}

	/***
	 * 保存电销分配规则
	 * 
	 * @return
	 */
	@RequestMapping("/saveTeleAssignRule")
	@ResponseBody
	public JSONResult<String> saveTeleAssignRule(@RequestBody TelemarketingAssignRuleDTO dto,
			HttpServletRequest request, HttpServletResponse response) {
		return telemarketingAssignRuleFeignClient.saveTeleAssignRule(dto);
	}

	/***
	 * 修改电销分配规则
	 * 
	 * @return
	 */
	@RequestMapping("/updateTeleAssignRule")
	@ResponseBody
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
}
