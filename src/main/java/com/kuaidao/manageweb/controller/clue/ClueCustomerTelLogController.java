package com.kuaidao.manageweb.controller.clue;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.clue.TelCreatePhoneLogDto;
import com.kuaidao.aggregation.dto.clue.TelCreatePhoneLogReqDto;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.clue.TelCreatePhoneLogFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * 新建手机号查询
 * @author fanjd
 */
@Controller
@RequestMapping(value = "/telCreatePhoneLog")
public class ClueCustomerTelLogController {
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private TelCreatePhoneLogFeignClient telCreatePhoneLogFeignClient;

    /***
     * 初始化列表
     * @return
     */
    @RequestMapping("/initPage")
    public String initPage(HttpServletRequest request) {
        // 电销组
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam = organizationFeignClient.queryOrgByParam(organizationQueryDTO);
        request.setAttribute("teleGroupList", queryOrgByParam.getData());
        return "searchNewPhone/searchNewPhone";
    }

    /**
     * 新建手机号查询列表
     */
    @ResponseBody
    @PostMapping("/queryPageList")
    public JSONResult<PageBean<TelCreatePhoneLogDto>> queryPageList(@RequestBody TelCreatePhoneLogReqDto reqDTO) {
        UserInfoDTO user = getUser();
        reqDTO.setBusinessLine(user.getBusinessLine());
        JSONResult<PageBean<TelCreatePhoneLogDto>> jsonResult = telCreatePhoneLogFeignClient.queryPageList(reqDTO);
        return jsonResult;
    }

    /**
     * 获取当前登录账号
     * @param
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

}
