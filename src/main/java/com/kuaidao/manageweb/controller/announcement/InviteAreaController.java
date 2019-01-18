package com.kuaidao.manageweb.controller.announcement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String deleInviteArea(HttpServletRequest request) {
    	String id = request.getParameter("id");
        return "inviteArea/addInviteArea";
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
