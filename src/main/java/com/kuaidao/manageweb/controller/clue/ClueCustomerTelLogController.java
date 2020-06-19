package com.kuaidao.manageweb.controller.clue;

import com.kuaidao.aggregation.dto.clue.ClueCustomerTelLogDto;
import com.kuaidao.aggregation.dto.clue.ClueCustomerTelLogReqDto;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.clue.ClueCustomerTelLogFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 新建手机号查询
 * @author fanjd
 */
@RestController
@RequestMapping(value = "/clueCustomerTelLog")
public class ClueCustomerTelLogController {
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ClueCustomerTelLogFeignClient clueCustomerTelLogFeignClient;
    /***
     * 初始化列表
     *
     * @return
     */
    @RequestMapping("/initPage")
    public String initPage(HttpServletRequest request) {
        // 电销组
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(organizationQueryDTO);
        request.setAttribute("teleGroupList", queryOrgByParam.getData());
        return "searchNewPhone/searchNewPhone";
    }
    /**
     * 新建手机号查询列表
     */
    @PostMapping("/queryPageList")
    public JSONResult<PageBean<ClueCustomerTelLogDto>> queryPageList(@RequestBody ClueCustomerTelLogReqDto reqDTO) {
        UserInfoDTO user = getUser();
        reqDTO.setBusinessLine(user.getBusinessLine());
        JSONResult<PageBean<ClueCustomerTelLogDto>> jsonResult = clueCustomerTelLogFeignClient.queryPageList(reqDTO);
        return jsonResult;
    }
    /**
     * 获取当前登录账号
     *
     * @param
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

}
