package com.kuaidao.manageweb.controller.clue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.user.UserDataAuthReq;
import org.apache.commons.collections.CollectionUtils;
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
public class TelCreatePhoneLogController {
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private TelCreatePhoneLogFeignClient telCreatePhoneLogFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
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
        String roleCode = user.getRoleList().get(0).getRoleCode();
        //监察按照业务线查询，其他推广角色按照配置的业务线查询
        if (RoleCodeEnum.JC.name().equals(roleCode)) {
            reqDTO.setBusinessLine(user.getBusinessLine());
        }else{
            List<Integer> businessLineList = getBusinessLineList(user.getId());
            reqDTO.setBusinessLineList(businessLineList);
        }

        JSONResult<PageBean<TelCreatePhoneLogDto>> jsonResult = telCreatePhoneLogFeignClient.queryPageList(reqDTO);
        return jsonResult;
    }
    /**
     * @Description: 根据用户id查询用户权限关系然后构建业务线集合
     *
     * @Param: [userId]
     * @return: java.util.List<java.lang.Integer>
     * @author: fanjd
     * @date: 2020/6/23  17:12
     * @version: V1.0
     */
    private List<Integer> getBusinessLineList(Long userId) {
        List<Integer> businessLineList = new ArrayList<>();
        UserDataAuthReq req = new UserDataAuthReq();
        req.setUserId(userId);
        JSONResult<List<UserDataAuthReq>> jsonResult =  userInfoFeignClient.findUserDataAuthByParam(req);
        if (JSONResult.SUCCESS.equals(jsonResult.getCode()) && CollectionUtils.isNotEmpty(jsonResult.getData())) {
            List<UserDataAuthReq> list =  jsonResult.getData();
            for (UserDataAuthReq userDataAuthReq : list) {
                businessLineList.add(userDataAuthReq.getBusinessLine());
            }
        }
        return  businessLineList;
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
