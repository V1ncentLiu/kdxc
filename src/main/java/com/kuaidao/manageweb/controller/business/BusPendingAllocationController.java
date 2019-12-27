/**
 * 
 */
package com.kuaidao.manageweb.controller.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.AppiontmentCancelDTO;
import com.kuaidao.aggregation.dto.clue.BusAllocationClueReq;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationDTO;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationPageParam;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.clue.AppiontmentFeignClient;
import com.kuaidao.manageweb.feign.clue.PendingVisitFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
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
@RequestMapping("/business/busAllocation")
public class BusPendingAllocationController {
    private static Logger logger = LoggerFactory.getLogger(BusPendingAllocationController.class);
    @Autowired
    private PendingVisitFeignClient pendingVisitFeignClient;
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
    @Autowired
    private AppiontmentFeignClient appiontmentFeignClient;
    private List<UserInfoDTO> saleList;

    /***
     * 待分配资源列表页
     * 
     * @return
     */
    @RequestMapping("/initAppiontmentList")
    @RequiresPermissions("business:busAllocationManager:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 查询所有电销组
        List<OrganizationRespDTO> saleGroupList = getSaleGroupList();
        request.setAttribute("saleGroupList", saleGroupList);
        // 查询所有签约项目
        ProjectInfoPageParam param=new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.queryBySign(param);
        request.setAttribute("teleProjectList", allProject.getData());
        //查询可签约的项目
        param.setIsNotSign(AggregationConstant.NO);
        JSONResult<List<ProjectInfoDTO>> project = projectInfoFeignClient.queryBySign(param);
        request.setAttribute("projectList", project.getData());
        // 查询所有商务经理
        List<Map<String, Object>> allSaleList = getAllSaleList();
        request.setAttribute("allSaleList", allSaleList);

        Integer businessLine = null;
        if (user.getBusinessLine() != null) {
            businessLine = user.getBusinessLine();
        }
        // 查询组织下商务经理
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> saleList =
                getUserList(user.getOrgId(), RoleCodeEnum.SWJL.name(), statusList, businessLine);
        request.setAttribute("busSaleList", saleList);
        // 查询所有省
        JSONResult<List<SysRegionDTO>> getproviceList = sysRegionFeignClient.getproviceList();
        request.setAttribute("provinceList", getproviceList.getData());

        // 查询字典选址情况集合
        request.setAttribute("optionAddressList", getDictionaryByCode(Constants.OPTION_ADDRESS));
        // 查询字典合伙人集合
        request.setAttribute("partnerList", getDictionaryByCode(Constants.PARTNER));
        // 查询字典餐饮经验集合
        request.setAttribute("cateringExperienceList",
                getDictionaryByCode(Constants.CATERING_EXPERIENCE));
        // 查询字典店铺面积集合
        request.setAttribute("storefrontAreaList", getDictionaryByCode(Constants.STOREFRONT_AREA));
        // 查询字典投资金额集合
        request.setAttribute("ussmList", getDictionaryByCode(Constants.USSM));
        // 意向品类
        request.setAttribute("purTypeList", getDictionaryByCode(Constants.PROJECT_CATEGORY));
        return "business/busAllocationManagerPage";
    }

    /***
     * 待分配来访客户列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("business:busAllocationManager:view")
    public JSONResult<PageBean<BusPendingAllocationDTO>> list(
            @RequestBody BusPendingAllocationPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setOrgId(user.getOrgId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }

        JSONResult<PageBean<BusPendingAllocationDTO>> pendingAllocationList =
                pendingVisitFeignClient.pendingVisitList(pageParam);

        return pendingAllocationList;
    }


    /**
     * 分配资源
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/busAllocationClue")
    @ResponseBody
    @RequiresPermissions("business:busAllocationManager:allocation")
    @LogRecord(description = "分配资源", operationType = OperationType.DISTRIBUTION,
            menuName = MenuEnum.BUS_ALLOCATION_CLUE)
    public JSONResult busAllocationClue(
            @Valid @RequestBody BusAllocationClueReq busAllocationClueReq, BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        busAllocationClueReq.setUserId(user.getId());
        busAllocationClueReq.setOrg(user.getOrgId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            busAllocationClueReq.setRoleId(roleList.get(0).getId());
            busAllocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }

        return pendingVisitFeignClient.busAllocationClue(busAllocationClueReq);
    }

    /***
     * 取消预约来访数据
     * 
     * @return
     */
    @RequestMapping("/cancelAppiontment")
    @ResponseBody
    @RequiresPermissions("business:busAllocationManager:cancel")
    @LogRecord(description = "取消预约来访数据", operationType = OperationType.UPDATE,
            menuName = MenuEnum.BUS_ALLOCATION_CLUE)
    public JSONResult<String> cancelAppiontment(@RequestBody AppiontmentCancelDTO dto,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        String roleCode = roleList.get(0).getRoleCode();
        if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            dto.setIsSwzj(1);
        }else {
            dto.setIsSwzj(0);
        }
        // 取消邀约来访数据
        return appiontmentFeignClient.cancelAppiontment(dto);
    }


    /***
     * 下属电销员工列表
     * 
     * @return
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq,
            HttpServletRequest request) {
        userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
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
     * 获取电销组
     * 
     * @param orgDTO
     * @return
     */
    private List<OrganizationRespDTO> getSaleGroupList() {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        // 查询下级电销组
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
        UserInfoDTO userInfo = getUser();
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
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        Integer businessLine = null;
        if (userInfo.getBusinessLine() != null) {
            businessLine = userInfo.getBusinessLine();
        }
        List<UserInfoDTO> userList =
                getUserList(null, RoleCodeEnum.SWJL.name(), statusList, businessLine);

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
            if (group != null) {
                OrganizationRespDTO area = orgMap.get(group.getParentId());
                resultMap.put("id", user.getId().toString());
                if (area != null) {
                    resultMap.put("name",
                            user.getName() + "(" + area.getName() + "--" + group.getName() + ")");
                } else {
                    resultMap.put("name", user.getName() + "(" + group.getName() + ")");

                }
                result.add(resultMap);
            }
        }
        return result;
    }

    /**
     * 根据机构和角色类型获取用户
     * 
     * @param orgDTO
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList,
            Integer businessLine) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        if (businessLine != null) {
            userOrgRoleReq.setBusinessLine(businessLine);
        }
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
