package com.kuaidao.manageweb.controller.invalidcustomer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.invalidcustomer.ClueQueryParamDTO;
import com.kuaidao.aggregation.dto.invalidcustomer.InvalidCustomerResourcesReqDTO;
import com.kuaidao.aggregation.dto.invalidcustomer.InvalidCustomerResourcesRespDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.InvalidCustomer.InvalidCustomerFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author yangbiao
 * @Date: 2019/2/11 15:13
 * @Description: 无效客户资源
 */
@Controller
@RequestMapping("/aggregation/invalidcustomer")
public class InvalidCustomerResources {


    private static Logger logger = LoggerFactory.getLogger(InvalidCustomerResources.class);

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    RoleManagerFeignClient roleManagerFeignClient;

    @Autowired
    InvalidCustomerFeignClient invalidCustomerFeignClient;

    @PostMapping("/resourceReduction")
    @ResponseBody
    public JSONResult<Boolean> resourceReduction(@RequestBody InvalidCustomerResourcesReqDTO dto) {
        Long id = CommUtil.getCurLoginUser().getId();
        dto.setUpdateTime(new Date());
        dto.setUpdateUserId(id);
        return invalidCustomerFeignClient.resourceReduction(dto);
    }

    /**
     * 分页查询
     */
    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {
        logger.info("------------ 无效客户资源列表 ---------------");
        // 电销组
        List dxzList = new ArrayList();
        List<Long> dxzIdsList = new ArrayList();
        List dxcygwList = new ArrayList();
        List dxzjsList = new ArrayList();

        // 权限相关
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.get(0) != null) {
            if (RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
                // 管理员:查看全部电销组
                OrganizationQueryDTO dto = new OrganizationQueryDTO();
                dto.setSystemCode(SystemCodeConstant.HUI_JU);
                dto.setOrgType(OrgTypeConstant.DXZ);
                JSONResult<List<OrganizationRespDTO>> dzList =
                        organizationFeignClient.queryOrgByParam(dto);
                dxzList = dzList.getData();
                for (OrganizationRespDTO organizationRespDTO : dzList.getData()) {
                    dxzIdsList.add(organizationRespDTO.getId());
                }
                dxcygwList = dxcygws(dxzIdsList);
                dxzjsList = dxzjs(dxzIdsList);
            } else if (RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) {
                // 电销副总:查看事业部下的全部电销组
                OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
                orgDto.setParentId(user.getOrgId());
                orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
                orgDto.setOrgType(OrgTypeConstant.DXZ);
                JSONResult<List<OrganizationDTO>> dzList =
                        organizationFeignClient.listDescenDantByParentId(orgDto);
                dxzList = dzList.getData();
                for (OrganizationDTO organizationDTO : dzList.getData()) {
                    dxzIdsList.add(organizationDTO.getId());
                }
                dxcygwList = dxcygws(dxzIdsList);
                dxzjsList = dxzjs(dxzIdsList);
            } else if (RoleCodeEnum.DXZJL.name().equals(roleList.get(0).getRoleCode())) {
                // 电销副总:查看分公司下的全部电销组
                OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
                orgDto.setParentId(user.getOrgId());
                orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
                orgDto.setOrgType(OrgTypeConstant.DXZ);
                JSONResult<List<OrganizationDTO>> dzList =
                        organizationFeignClient.listDescenDantByParentId(orgDto);
                dxzList = dzList.getData();
                for (OrganizationDTO organizationDTO : dzList.getData()) {
                    dxzIdsList.add(organizationDTO.getId());
                }
                dxcygwList = dxcygws(dxzIdsList);
                dxzjsList = dxzjs(dxzIdsList);
            } else if (RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
                // 电销总监:查看所在电销组
                // IdEntity idEntity = new IdEntity();
                // idEntity.setId(String.valueOf(user.getOrgId()));
                // JSONResult<OrganizationDTO> orgRes =
                // organizationFeignClient.queryOrgById(idEntity);

                OrganizationQueryDTO dto = new OrganizationQueryDTO();
                dto.setSystemCode(SystemCodeConstant.HUI_JU);
                dto.setOrgType(OrgTypeConstant.DXZ);
                dto.setId(user.getOrgId());
                JSONResult<List<OrganizationRespDTO>> dzList =
                        organizationFeignClient.queryOrgByParam(dto);
                dxzList = dzList.getData();
                dxzIdsList.add(user.getOrgId());
                dxcygwList = dxcygws(dxzIdsList);
                // dxzjsList = dxzjs(dxzIdsList);
            }
            // 添加一个对电销创业顾问的筛选
        }

        request.setAttribute("dzList", dxzList);
        request.setAttribute("dxgwList", dxcygwList);
        request.setAttribute("dxzjList", dxzjsList);

        return "invalidcustomer/invalidCustomer";
    }

    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<InvalidCustomerResourcesRespDTO>> queryListPage(
            @RequestBody ClueQueryParamDTO dto) {
        Date date1 = dto.getCreateTime1();
        Date date2 = dto.getCreateTime2();
        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                return new JSONResult().fail("-1", "创建时间，开始时间大于结束时间!");
            }
        }

        Date date3 = dto.getReleaseTime1();
        Date date4 = dto.getReleaseTime2();
        if (date3 != null && date4 != null) {
            if (date3.getTime() > date4.getTime()) {
                return new JSONResult().fail("-1", "释放时间，开始时间大于结束时间!");
            }
        }



        // 权限相关
        List<Long> dxzList = new ArrayList<Long>();
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.get(0) != null) {
            if (RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
                // 管理员:查看全部电销组
                OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
                orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
                orgDto.setOrgType(OrgTypeConstant.DXZ);
                JSONResult<List<OrganizationRespDTO>> dzList =
                        organizationFeignClient.queryOrgByParam(orgDto);
                List<OrganizationRespDTO> datas = dzList.getData();
                for (OrganizationRespDTO organizationRespDTO : datas) {
                    dxzList.add(organizationRespDTO.getId());
                }

            } else if (RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) {
                // 电销副总:查看事业部下的全部电销组
                OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
                orgDto.setParentId(user.getOrgId());
                orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
                orgDto.setOrgType(OrgTypeConstant.DXZ);
                JSONResult<List<OrganizationDTO>> dzList =
                        organizationFeignClient.listDescenDantByParentId(orgDto);
                List<OrganizationDTO> datas = dzList.getData();
                for (OrganizationDTO organizationDTO : datas) {
                    dxzList.add(organizationDTO.getId());
                }
            } else if (RoleCodeEnum.DXZJL.name().equals(roleList.get(0).getRoleCode())) {
                // 电销总经理:查看分公司下的全部电销组
                OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
                orgDto.setParentId(user.getOrgId());
                orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
                orgDto.setOrgType(OrgTypeConstant.DXZ);
                JSONResult<List<OrganizationDTO>> dzList =
                        organizationFeignClient.listDescenDantByParentId(orgDto);
                List<OrganizationDTO> datas = dzList.getData();
                for (OrganizationDTO organizationDTO : datas) {
                    dxzList.add(organizationDTO.getId());
                }
            } else if (RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
                // 电销总监:查看所在电销组
                dxzList.add(user.getOrgId());
            } else {
                dxzList.add(user.getOrgId());
            }
        }
        dto.setOrgids(dxzList);

        return invalidCustomerFeignClient.queryListPage(dto);
    }



    /**
     * 查询的是当前组织下电销顾问。
     * 
     * @return
     */
    private List dxcygws(List<Long> orgList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgIdList(orgList);
        userOrgRoleReq.setRoleCode(RoleCodeEnum.DXZJ.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    private List dxzjs(List<Long> orgList) {
        // 这个查询是不对的啊
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgIdList(orgList);
        userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }
}
