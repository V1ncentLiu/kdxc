package com.kuaidao.manageweb.controller.sign;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kuaidao.aggregation.dto.clue.ClueRepetitionDTO;
import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.aggregation.dto.sign.BusinessSignDTO;
import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.sign.BusinessSignFeignClient;
import com.kuaidao.manageweb.util.DownFile;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      签约记录
 */

@Controller
@RequestMapping("/businesssign")
public class BusinessSignController {

    private static Logger logger = LoggerFactory.getLogger(BusinessSignController.class);
    @Autowired
    InviteareaFeignClient inviteareaFeignClient;
    @Autowired
    BusinessSignFeignClient businessSignFeignClient;
    @Autowired
    SysRegionFeignClient sysRegionFeignClient;
    @Autowired
	private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    /**
     *  有效性签约单确认列表页面
     * 
     * @return
     */
    @RequestMapping("/businessSignValidPage")
    public String businessSignValidPage(HttpServletRequest request) {
    	OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
		orgDto.setOrgType(OrgTypeConstant.SWZ);
		orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
		//商务小组
		JSONResult<List<OrganizationRespDTO>> swList = organizationFeignClient.queryOrgByParam(orgDto);
		orgDto.setOrgType(OrgTypeConstant.DXZ);
		//电销小组
		JSONResult<List<OrganizationRespDTO>> dxList = organizationFeignClient.queryOrgByParam(orgDto);
		
		// 查询项目列表
        JSONResult<List<ProjectInfoDTO>> listNoPage =
                projectInfoFeignClient.listNoPage(new ProjectInfoPageParam());
      //获取省份
    	List<SysRegionDTO> proviceslist = sysRegionFeignClient.getproviceList().getData();
    	
    	request.setAttribute("swList", swList.getData());
		request.setAttribute("dxList", dxList.getData());
		request.setAttribute("projectList", listNoPage.getData());
		request.setAttribute("provinceList", proviceslist);
		return "business/businessSignValidPage";
    } 
    
    /**
     * 有效性签约单确认列表
     * 
     * @return
     */
    @RequestMapping("/businessSignValidList")
    @ResponseBody
    public JSONResult<PageBean<BusinessSignDTO>> businessSignValidList(HttpServletRequest request,@RequestBody BusinessSignDTO businessSignDTO) {
    	JSONResult<PageBean<BusinessSignDTO>> list = businessSignFeignClient.businessSignValidList(businessSignDTO);
    	return list;
    }
    /**
     * 签约有效性判断
     * 
     * @return
     */
    @RequestMapping("/updateBusinessSignDTOValidByIds")
    @LogRecord(description = "签约有效性修改",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.BUSINESSSIGNVALID)
    @ResponseBody
    public JSONResult addTelemarketingLayout(@RequestBody BusinessSignDTO businessSignDTO) {
    	return businessSignFeignClient.updateBusinessSignDTOValidByIds(businessSignDTO);
    }
}
