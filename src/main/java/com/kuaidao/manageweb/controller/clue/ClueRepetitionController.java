package com.kuaidao.manageweb.controller.clue;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kuaidao.aggregation.dto.clue.ClueRepetitionDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.clue.ClueRepetitionFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;

/**
* 重单管理
* Created  on 2019-2-18 16:15:59
*/
@Controller
@RequestMapping("/clue/cluerepetition")
public class ClueRepetitionController {
	private static Logger logger = LoggerFactory.getLogger(ClueRepetitionController.class);
	
	@Autowired
	ClueRepetitionFeignClient clueRepetitionFeignClient;
	 /**
     *  重单列表页面
     * 
     * @return
     */
    @RequestMapping("/queryRepeatPage")
    public String queryRepeatPage(HttpServletRequest request) {
		return "clue/repetition/customerrePetitionList";
    }
    
    /**
     * 重单列表
     * 
     * @return
     */
    @RequestMapping("/queryRepeatList")
    @ResponseBody
    public JSONResult<PageBean<ClueRepetitionDTO>> queryRepeatList(HttpServletRequest request,@RequestBody ClueRepetitionDTO clueRepetitionDTO) {
    	JSONResult<PageBean<ClueRepetitionDTO>> list = clueRepetitionFeignClient.queryRepeatList(clueRepetitionDTO);
    	return list;
    }
    
    /**
     * 重单详情
     * 
     * @return
     */
    @RequestMapping("/queryRepeatById")
    @ResponseBody
    public JSONResult<PageBean<ClueRepetitionDTO>> queryRepeatById(HttpServletRequest request,@RequestBody ClueRepetitionDTO clueRepetitionDTO) {
    	JSONResult<PageBean<ClueRepetitionDTO>> list = clueRepetitionFeignClient.queryRepeatList(clueRepetitionDTO);
    	return list;
    }
    
    /**
     * 撤销重单申请
     * 
     * @return
     */
    @RequestMapping("/delRepeatByIds")
    @ResponseBody
    public JSONResult<PageBean<ClueRepetitionDTO>> delRepeatByIds(HttpServletRequest request,@RequestBody ClueRepetitionDTO clueRepetitionDTO) {
    	JSONResult<PageBean<ClueRepetitionDTO>> list = clueRepetitionFeignClient.queryRepeatList(clueRepetitionDTO);
    	return list;
    }
    
    
    /**
     *  重单处理列表页面
     * 
     * @return
     */
    @RequestMapping("/dealPetitionListPage")
    public String dealPetitionListPage(HttpServletRequest request) {
		return "clue/repetition/dealPetitionList";
    } 
    
    /**
     * 重单处理列表
     * 
     * @return
     */
    @RequestMapping("/dealPetitionList")
    @ResponseBody
    public JSONResult<PageBean<ClueRepetitionDTO>> dealPetitionList(HttpServletRequest request,@RequestBody ClueRepetitionDTO clueRepetitionDTO) {
    	JSONResult<PageBean<ClueRepetitionDTO>> list = clueRepetitionFeignClient.queryRepeatList(clueRepetitionDTO);
    	return list;
    }
    
    /**
     * 处理重单
     * 
     * @return
     */
    @RequestMapping("/dealPetitionById")
    @ResponseBody
    public JSONResult<PageBean<ClueRepetitionDTO>> dealPetitionById(HttpServletRequest request,@RequestBody ClueRepetitionDTO clueRepetitionDTO) {
    	JSONResult<PageBean<ClueRepetitionDTO>> list = clueRepetitionFeignClient.queryRepeatList(clueRepetitionDTO);
    	return list;
    }
}
