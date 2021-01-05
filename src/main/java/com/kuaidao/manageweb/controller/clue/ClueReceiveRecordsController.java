package com.kuaidao.manageweb.controller.clue;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.ClueReceiveRecordsDTO;
import com.kuaidao.businessconfig.constant.BusinessConfigErrorCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.clue.ClueReceiveRecordsFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/clue/cluereceiverecords")
public class ClueReceiveRecordsController {

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private ClueReceiveRecordsFeignClient clueReceiveRecordsFeignClient;

    /**
     * 领取
     * 
     * @return
     */
    @RequiresPermissions("PublicCustomer:receive")
    @RequestMapping("/receiveClueByClueIds")
    @ResponseBody
    @LogRecord(description = "公有池领取", operationType = OperationType.RECEIVE,
            menuName = MenuEnum.TEL_CENTER_PUBLICCUSTOMER)
    public JSONResult<ClueReceiveRecordsDTO> receiveClueByClueIds(
            @RequestBody ClueReceiveRecordsDTO clueReceiveRecordsDTO) {
        UserInfoDTO user = getUser();
        clueReceiveRecordsDTO.setBusinessLine(user.getBusinessLine());
        List<RoleInfoDTO> roleList = user.getRoleList();
        List<Long> idList = new ArrayList<Long>();
        if (roleList != null && !RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())
                && !RoleCodeEnum.DXCYGW.name().equals(roleList.get(0).getRoleCode())) {
            return new JSONResult().fail(BusinessConfigErrorCodeEnum.PUBLICCLUERECEIVERLOE.getCode(),
                    BusinessConfigErrorCodeEnum.PUBLICCLUERECEIVERLOE.getMessage());
        } else {
            clueReceiveRecordsDTO.setReceiveUser(user.getId());
            clueReceiveRecordsDTO.setTeleGroupId(user.getOrgId());
            clueReceiveRecordsDTO.setLoginUserRole(roleList.get(0).getRoleCode());

            // 查询用户的上级
            OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
            orgDto.setId(clueReceiveRecordsDTO.getTeleGroupId());
            orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
            JSONResult<List<OrganizationDTO>> orgJson =
                    organizationFeignClient.listParentsUntilOrg(orgDto);
            if (orgJson != null && JSONResult.SUCCESS.equals(orgJson.getCode())
                    && orgJson.getData() != null && orgJson.getData().size() > 0) {
                for (OrganizationDTO org : orgJson.getData()) {

                    if (null != org.getOrgType()
                            && org.getOrgType().equals(OrgTypeConstant.DZSYB)) {
                        clueReceiveRecordsDTO.setTeleDeptId(org.getId());
                        UserOrgRoleReq userRoleInfo = new UserOrgRoleReq();
                        userRoleInfo.setRoleCode(RoleCodeEnum.DXFZ.name());
                        userRoleInfo.setOrgId(org.getId());
                        JSONResult<List<UserInfoDTO>> ceoUserInfoJson =
                                userInfoFeignClient.listByOrgAndRole(userRoleInfo);
                        if (ceoUserInfoJson.getCode().equals(JSONResult.SUCCESS)
                                && null != ceoUserInfoJson.getData()
                                && ceoUserInfoJson.getData().size() > 0) {
                            // 电销副总
                            clueReceiveRecordsDTO
                                    .setTeleCeoId(ceoUserInfoJson.getData().get(0).getId());
                        }
                        if (!RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
                            userRoleInfo.setRoleCode(RoleCodeEnum.DXZJ.name());
                            userRoleInfo.setOrgId(user.getOrgId());
                            List<Integer> statusList = new ArrayList();
                            statusList.add(1);
                            userRoleInfo.setStatusList(statusList);
                            JSONResult<List<UserInfoDTO>> ceoUserInfoJsons =
                                    userInfoFeignClient.listByOrgAndRole(userRoleInfo);
                            if (ceoUserInfoJsons.getCode().equals(JSONResult.SUCCESS)
                                    && null != ceoUserInfoJsons.getData()
                                    && ceoUserInfoJsons.getData().size() > 0) {
                                // 电销总监
                                clueReceiveRecordsDTO.setTeleDirectorId(
                                        ceoUserInfoJsons.getData().get(0).getId());
                            }
                        } else {
                            clueReceiveRecordsDTO.setTeleDirectorId(user.getId());
                        }
                    }
                    if (null != org.getOrgType()
                            && org.getOrgType().equals(OrgTypeConstant.DXFGS)) {

                        UserOrgRoleReq userRoleInfo = new UserOrgRoleReq();
                        userRoleInfo.setRoleCode(RoleCodeEnum.DXZJL.name());
                        userRoleInfo.setOrgId(org.getId());
                        JSONResult<List<UserInfoDTO>> ceoUserInfoJson =
                                userInfoFeignClient.listByOrgAndRole(userRoleInfo);
                        if (ceoUserInfoJson.getCode().equals(JSONResult.SUCCESS)
                                && null != ceoUserInfoJson.getData()
                                && ceoUserInfoJson.getData().size() > 0) {
                            // 电销总经理
                            clueReceiveRecordsDTO
                                    .setTeleManagerId(ceoUserInfoJson.getData().get(0).getId());
                        }
                        clueReceiveRecordsDTO.setTeleCompanyId(org.getId());
                    }
                    if (null != org.getOrgType()
                            && org.getOrgType().equals(OrgTypeConstant.DXFGS)) {

                        UserOrgRoleReq userRoleInfo = new UserOrgRoleReq();
                        userRoleInfo.setRoleCode(RoleCodeEnum.DXZJL.name());
                        userRoleInfo.setOrgId(org.getId());
                        JSONResult<List<UserInfoDTO>> ceoUserInfoJson =
                                userInfoFeignClient.listByOrgAndRole(userRoleInfo);
                        if (ceoUserInfoJson.getCode().equals(JSONResult.SUCCESS)
                                && null != ceoUserInfoJson.getData()
                                && ceoUserInfoJson.getData().size() > 0) {
                            // 电销总经理
                            clueReceiveRecordsDTO
                                    .setTeleManagerId(ceoUserInfoJson.getData().get(0).getId());
                        }
                        clueReceiveRecordsDTO.setTeleCompanyId(org.getId());
                    }
                }
            }
            clueReceiveRecordsDTO.setRoleId(roleList.get(0).getId());
            return clueReceiveRecordsFeignClient.receiveClueByClueIds(clueReceiveRecordsDTO);
        }

    }

    /**
     * 获取当前登录账号
     * 
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

}
