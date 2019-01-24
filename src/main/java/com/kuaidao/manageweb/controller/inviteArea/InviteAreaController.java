package com.kuaidao.manageweb.controller.inviteArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.DownFile;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    
    @Autowired
    SysRegionFeignClient sysRegionFeignClient;
    @Autowired
	private OrganizationFeignClient organizationFeignClient;
    /**
     * 邀约记录列表页面
     * 
     * @return
     */
    @RequestMapping("/inviteAreaList")
    public String inviteAreaList(HttpServletRequest request) {
    	OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
		orgDto.setOrgType(OrgTypeConstant.SWZ);
		//商务小组
		JSONResult<List<OrganizationRespDTO>> swList = organizationFeignClient.queryOrgByParam(orgDto);
		orgDto.setOrgType(OrgTypeConstant.DXZ);
		//电销小组
		JSONResult<List<OrganizationRespDTO>> dxList = organizationFeignClient.queryOrgByParam(orgDto);
		request.setAttribute("swList", swList.getData());
		request.setAttribute("dxList", dxList.getData());
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
     * 添加邀约区域页面
     * 
     * @return
     */
    @RequestMapping("/addInviteAreaPage")
    public String addInviteArea(HttpServletRequest request) {
    	List<SysRegionDTO> list = sysRegionFeignClient.getproviceList().getData();
        return "inviteArea/addInviteAreaPage";
    }
    /**
     * 添加邀约区域
     * 
     * @return
     */
    @RequestMapping("/addInviteArea")
    @LogRecord(description = "添加邀约区域",operationType = LogRecord.OperationType.INSERT,menuName = MenuEnum.INVITEAREA)
    @ResponseBody
    public JSONResult addInviteAreaMes(@RequestBody InviteAreaDTO inviteAreaDTO) {
    	return inviteareaFeignClient.addOrUpdateInviteArea(inviteAreaDTO);
    }
    
    /**
     * 修改邀约区域
     * 
     * @return
     */
    @RequestMapping("/updateInviteArea")
    @LogRecord(description = "修改邀约区域",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.INVITEAREA)
    @ResponseBody
    public JSONResult updateInviteAreaMes(@RequestBody InviteAreaDTO inviteAreaDTO) {
    	return inviteareaFeignClient.addOrUpdateInviteArea(inviteAreaDTO);
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
