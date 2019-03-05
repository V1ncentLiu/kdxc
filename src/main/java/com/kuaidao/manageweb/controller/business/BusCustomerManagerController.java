/**
 * 
 */
package com.kuaidao.manageweb.controller.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.BusCustomerDTO;
import com.kuaidao.aggregation.dto.clue.BusCustomerPageParam;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.clue.BusCustomerFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/business/busCustomerManager")
public class BusCustomerManagerController {
    private static Logger logger = LoggerFactory.getLogger(BusCustomerManagerController.class);
    @Autowired
    private BusCustomerFeignClient busCustomerFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysRegionFeignClient sysRegionFeignClient;

    /***
     * 商务客户管理页
     * 
     * @return
     */
    @RequestMapping("/initCustomerManager")
    @RequiresPermissions("business:busCustomerManager:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 查询所有电销组
        List<OrganizationRespDTO> teleSaleGroupList = getSaleGroupList(null, OrgTypeConstant.DXZ);
        request.setAttribute("teleSaleGroupList", teleSaleGroupList);
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
            // 管理员 可以选择所有商务组 商务总监

            // 查询所有商务组
            List<OrganizationRespDTO> busSaleGroupList =
                    getSaleGroupList(null, OrgTypeConstant.SWZ);
            request.setAttribute("busSaleGroupList", busSaleGroupList);
            // 查询所有商务总监
            List<UserInfoDTO> busDirectorList = getUserList(null, RoleCodeEnum.SWZJ.name());
            request.setAttribute("busDirectorList", busDirectorList);
        } else if (roleList != null
                && RoleCodeEnum.SWDQZJ.name().equals(roleList.get(0).getRoleCode())) {
            // 商务大区总监 可以选择本区下的商务组 商务总监

            // 查询所有商务组
            List<OrganizationRespDTO> busSaleGroupList =
                    getSaleGroupList(user.getOrgId(), OrgTypeConstant.SWZ);
            request.setAttribute("busSaleGroupList", busSaleGroupList);
            // 查询本区商务总监
            List<UserInfoDTO> busDirectorList =
                    getUserList(user.getOrgId(), RoleCodeEnum.SWZJ.name());
            request.setAttribute("busDirectorList", busDirectorList);
        } else if (roleList != null
                && RoleCodeEnum.SWZJ.name().equals(roleList.get(0).getRoleCode())) {
            // 商务总监 可以选择本商务组下的商务经理

            // 查询本区商务总监
            List<UserInfoDTO> busDirectorList =
                    getUserList(user.getOrgId(), RoleCodeEnum.SWZJ.name());
            request.setAttribute("searchBusSaleList", busDirectorList);
        }
        // 查询所有商务经理
        List<Map<String, Object>> allSaleList = getAllSaleList();
        request.setAttribute("allSaleList", allSaleList);
        // 查询组织下商务经理
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setRoleCode(RoleCodeEnum.SWJL.name());
        userOrgRoleReq.setOrgId(user.getOrgId());
        JSONResult<List<UserInfoDTO>> saleList =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        request.setAttribute("busSaleList", saleList.getData());
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> listNoPage =
                projectInfoFeignClient.listNoPage(new ProjectInfoPageParam());
        request.setAttribute("projectList", listNoPage.getData());
        SysRegionDTO queryDTO = new SysRegionDTO();
        // 查询所有省
        queryDTO.setType(1);
        JSONResult<List<SysRegionDTO>> querySysRegionByParam =
                sysRegionFeignClient.querySysRegionByParam(queryDTO);
        request.setAttribute("provinceList", querySysRegionByParam.getData());

        // 查询字典选址情况集合
        request.setAttribute("optionAddressList", getDictionaryByCode(Constants.OPTION_ADDRESS));
        // 查询字典合伙人集合
        request.setAttribute("partnerList", getDictionaryByCode(Constants.PARTNER));
        // 查询字典餐饮经验集合
        request.setAttribute("cateringExperienceList",
                getDictionaryByCode(Constants.CATERING_EXPERIENCE));
        // 查询字典餐饮经验集合
        request.setAttribute("shopTyleList", getDictionaryByCode(Constants.VISTIT_STORE_TYPE));
        return "business/busCustomerManagerPage";
    }

    /***
     * 商务客户管理列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("business:busCustomerManager:view")
    public JSONResult<PageBean<BusCustomerDTO>> list(@RequestBody BusCustomerPageParam pageParam,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }

        JSONResult<PageBean<BusCustomerDTO>> busCustomerList =
                busCustomerFeignClient.busCustomerList(pageParam);

        return busCustomerList;
    }

    /***
     * 下属商务经理列表
     * 
     * @return
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    @RequiresPermissions("aggregation:appiontmentManager:view")
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq,
            HttpServletRequest request) {
        userOrgRoleReq.setRoleCode(RoleCodeEnum.SWJL.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
    }

    /**
     * 获取当前登录账号
     * 
     * @param orgDTO
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 获取所有组织组
     * 
     * @param orgDTO
     * @return
     */
    private List<OrganizationRespDTO> getSaleGroupList(Long parentId, Integer type) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setParentId(parentId);
        queryDTO.setOrgType(type);
        // 查询所有组织
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> data = queryOrgByParam.getData();
        return data;
    }

    /**
     * 获取所有商务经理（组织名-大区名）
     * 
     * @param orgDTO
     * @return
     */
    private List<Map<String, Object>> getAllSaleList() {
        // 查询所有商务组
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> groupList = queryOrgByParam.getData();
        // 查询所有商务大区
        queryDTO.setOrgType(OrgTypeConstant.SWDQ);
        JSONResult<List<OrganizationRespDTO>> busArea =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> busAreaLsit = busArea.getData();
        // 查询所有商务经理
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.SWJL.name());

        Map<Long, OrganizationRespDTO> orgMap = new HashMap<Long, OrganizationRespDTO>();
        // 生成<机构id，机构>map
        for (OrganizationRespDTO org : groupList) {
            orgMap.put(org.getId(), org);
        }
        for (OrganizationRespDTO org : busAreaLsit) {
            orgMap.put(org.getId(), org);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 生成结果集，匹配电销组以及电销总监
        for (UserInfoDTO user : userList) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            OrganizationRespDTO group = orgMap.get(user.getOrgId());
            OrganizationRespDTO area = orgMap.get(group.getParentId());

            resultMap.put("id", user.getId());
            resultMap.put("name",
                    user.getName() + "(" + area.getName() + "--" + group.getName() + ")");
            result.add(resultMap);
        }
        return result;
    }

    /**
     * 根据机构和角色类型获取用户
     * 
     * @param orgDTO
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 查询字典表
     * 
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }

}
