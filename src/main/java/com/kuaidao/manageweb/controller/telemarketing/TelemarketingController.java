package com.kuaidao.manageweb.controller.telemarketing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.manageweb.util.DownFile;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      电销管理
 */

@Controller
@RequestMapping("/telemarketing")
public class TelemarketingController {

    private static Logger logger = LoggerFactory.getLogger(TelemarketingController.class);
    @Autowired
    TelemarketingLayoutFeignClient telemarketingLayoutFeignClient;

    /**
     * 电销布局列表
     * 
     * @return
     */
    @RequestMapping("/telemarketingLayoutList")
    public String inviteAreaList(HttpServletRequest request) {
        return "telemarketing/telemarketingLayoutList";
    }
    /**
     * 电销布局列表
     * 
     * @return
     */
    @RequestMapping("/getTelemarketingLayoutList")
    @ResponseBody
    public JSONResult<PageBean<TelemarketingLayoutDTO>> getTelemarketingLayoutList(HttpServletRequest request,@RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
    	return telemarketingLayoutFeignClient.getTelemarketingLayoutList(telemarketingLayoutDTO);
    }
    /**
     * 添加电销布局
     * 
     * @return
     */
    @RequestMapping("/addTelemarketingLayout")
    @LogRecord(description = "添加电销布局",operationType = LogRecord.OperationType.INSERT,menuName = MenuEnum.TELEMARKTINGLAYOUT)
    @ResponseBody
    public JSONResult addTelemarketingLayout(@RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
    	return telemarketingLayoutFeignClient.addOrUpdateTelemarketingLayout(telemarketingLayoutDTO);
    }
    
    /**
     * 修改电销布局
     * 
     * @return
     */
    @RequestMapping("/updateTelemarketingLayout")
    @LogRecord(description = "修改电销布局",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.TELEMARKTINGLAYOUT)
    @ResponseBody
    public JSONResult updateTelemarketingLayout(@RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
    	return telemarketingLayoutFeignClient.addOrUpdateTelemarketingLayout(telemarketingLayoutDTO);
    }
    /**
     * 删除电销布局
     * 
     * @return
     */
    @RequestMapping("/deleTelemarketingLayout")
    @LogRecord(description = "删除电销布局",operationType = LogRecord.OperationType.DELETE,menuName = MenuEnum.TELEMARKTINGLAYOUT)
    @ResponseBody
    public JSONResult deleTelemarketingLayout(@RequestBody TelemarketingLayoutDTO telemarketingLayoutDTO) {
    	return telemarketingLayoutFeignClient.deleTelemarketingLayout(telemarketingLayoutDTO);
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
