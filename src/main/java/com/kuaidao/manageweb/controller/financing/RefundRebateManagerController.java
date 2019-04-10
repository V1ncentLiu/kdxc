/**
 * 
 */
package com.kuaidao.manageweb.controller.financing;

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
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.financing.ApplyRefundRebateReq;
import com.kuaidao.aggregation.dto.financing.RefundRebateListDTO;
import com.kuaidao.aggregation.dto.financing.RefundRebatePageParam;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.financing.RefundRebateFeignClient;
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
@RequestMapping("/financing/refundRebate")
public class RefundRebateManagerController {
    private static Logger logger = LoggerFactory.getLogger(RefundRebateManagerController.class);
    @Autowired
    private RefundRebateFeignClient refundRebateFeignClient;
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
     * 餐饮公司退返款页
     * 
     * @return
     */
    @RequestMapping("/initRefundRebateManager")
    @RequiresPermissions("financing:refundRebateManager:view")
    public String initRefundRebateManager(HttpServletRequest request) {
        // 查询所有商务大区
        List<OrganizationRespDTO> busAreaList = getOrgList(null, OrgTypeConstant.SWDQ);
        request.setAttribute("busAreaList", busAreaList);
        // 查询所有商务组
        List<OrganizationRespDTO> busGroupList = getOrgList(null, OrgTypeConstant.SWZ);
        request.setAttribute("busGroupList", busGroupList);
        // 查询所有电销事业部
        List<OrganizationRespDTO> teleDeptList = getOrgList(null, OrgTypeConstant.DZSYB);
        request.setAttribute("teleDeptList", teleDeptList);
        // 查询所有电销组
        List<OrganizationRespDTO> teleGroupList = getOrgList(null, OrgTypeConstant.DXZ);
        request.setAttribute("teleGroupList", teleGroupList);
        // 查询所有商务经理
        List<UserInfoDTO> busSaleList = getUserList(null, RoleCodeEnum.SWJL.name(), null);
        request.setAttribute("busSaleList", busSaleList);
        // 查询所有电销创业顾问
        List<UserInfoDTO> teleSaleList = getUserList(null, RoleCodeEnum.DXCYGW.name(), null);
        request.setAttribute("teleSaleList", teleSaleList);

        // 查询所有省
        SysRegionDTO queryDTO = new SysRegionDTO();
        queryDTO.setType(0);
        JSONResult<List<SysRegionDTO>> querySysRegionByParam =
                sysRegionFeignClient.querySysRegionByParam(queryDTO);
        request.setAttribute("provinceList", querySysRegionByParam.getData());

        // 查询签约店型集合
        request.setAttribute("vistitStoreTypeList",
                getDictionaryByCode(DicCodeEnum.VISITSTORETYPE.getCode()));

        return "financing/refundRebateManagerPage";
    }

    /***
     * 餐饮公司退返款列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("financing:refundRebateManager:view")
    public JSONResult<PageBean<RefundRebateListDTO>> list(
            @RequestBody RefundRebatePageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }

        JSONResult<PageBean<RefundRebateListDTO>> list = refundRebateFeignClient.list(pageParam);
        return list;
    }

    /***
     * 餐饮公司退款申请
     * 
     * @return
     */
    @PostMapping("/applyRefund")
    @ResponseBody
    @RequiresPermissions("financing:refundRebateManager:applyRefund")
    @LogRecord(description = "申请退款", operationType = OperationType.INSERT,
            menuName = MenuEnum.REFUNDREBATE_MANAGER)
    public JSONResult<Long> applyRefund(@RequestBody ApplyRefundRebateReq req,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入申请人id
        req.setCreateUser(user.getId());
        req.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        JSONResult<Long> applyRefund = refundRebateFeignClient.applyRefundRebate(req);
        return applyRefund;
    }

    /***
     * 餐饮公司返款申请
     * 
     * @return
     */
    @PostMapping("/applyRebate")
    @ResponseBody
    @RequiresPermissions("financing:refundRebateManager:applyRebate")
    @LogRecord(description = "申请返款", operationType = OperationType.INSERT,
            menuName = MenuEnum.REFUNDREBATE_MANAGER)
    public JSONResult<Long> applyRebate(@RequestBody ApplyRefundRebateReq req,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入申请人id
        req.setCreateUser(user.getId());
        req.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        JSONResult<Long> applyRebate = refundRebateFeignClient.applyRefundRebate(req);
        return applyRebate;
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
    private List<OrganizationRespDTO> getOrgList(Long parentId, Integer type) {
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
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.SWJL.name(), statusList);

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
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
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
