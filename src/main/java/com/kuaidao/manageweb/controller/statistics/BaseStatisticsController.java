package com.kuaidao.manageweb.controller.statistics;

import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author: guhuitao
 * @create: 2019-08-22 14:21
 **/
@Controller
public class BaseStatisticsController {


    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    /**
     * 根据商务组id和角色查询 用户
     * @param userOrgRoleReq
     * @return
     */
    @RequestMapping("/base/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq) {
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
    }

    /**
     * 根据参数查询组织机构
     * @param dto
     * @return
     */
    @RequestMapping("/base/getGroupList")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> getGroupList(@RequestBody OrganizationQueryDTO dto) {
        JSONResult<List<OrganizationRespDTO>> list =
                organizationFeignClient.queryOrgByParam(dto);
        return list;
    }

}
