package com.kuaidao.manageweb.controller.inviteArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.util.DownFile;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      y邀约区域
 */

@Controller
@RequestMapping("/invitearea")
public class InviteAreaController {

    private static Logger logger = LoggerFactory.getLogger(InviteAreaController.class);
    @Autowired
    InviteareaFeignClient inviteareaFeignClient;
    /**
     * 邀约记录列表
     * 
     * @return
     */
    @RequestMapping("/inviteAreaList")
    public String inviteAreaList(HttpServletRequest request) {
        return "inviteArea/inviteAreaList";
    }
    
    /**
     * 邀约记录列表
     * 
     * @return
     */
    @RequestMapping("/inviteAreaListPage")
    @ResponseBody
    public JSONResult<PageBean<InviteAreaDTO>> inviteAreaListPage(HttpServletRequest request,@RequestBody InviteAreaDTO inviteAreaDTO) {
    	return inviteareaFeignClient.inviteAreaListPage(inviteAreaDTO);
    }
    
    /**
     * 添加邀约区域
     * 
     * @return
     */
    @RequestMapping("/addInviteArea")
    public String addInviteArea(HttpServletRequest request) {
        return "inviteArea/addInviteArea";
    }
    /**
     * 删除邀约区域
     * 
     * @return
     */
    @RequestMapping("/deleInviteArea")
    @LogRecord(description = "删除邀约区域",operationType = LogRecord.OperationType.DELETE,menuName = MenuEnum.INVITEAREA)
    @ResponseBody
    public JSONResult deleInviteArea(@RequestBody InviteAreaDTO inviteAreaDTO) {
    	return inviteareaFeignClient.deleInviteArea(inviteAreaDTO);
    }
    
    /**
     * 模板下载
     * @param request
     * @param response
     * @throws Exception
     */
	@RequestMapping(value = "/download")
	public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {

		DownFile df = new DownFile();
		// 文件模板在%TOMCAT_HOME%\\webapps\\YourWebProject\\WEB-INF\\model\\文件夹中
		String filePath = request.getSession().getServletContext().getRealPath("model");
		String fileName = "area-division.xlsx";

		try {
			df.downFile(filePath + File.separator + fileName, fileName, request, response);
		} catch (IOException e) {
			logger.error("邀约区域下载模板失败：", e);
		} catch (Exception e) {
			logger.error("邀约区域下载模板失败：", e);
		}
	}
}
