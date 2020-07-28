package com.kuaidao.manageweb.controller.paydetail;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.kuaidao.aggregation.constant.PayDetailConstant;
import com.kuaidao.aggregation.dto.paydetail.PayDetailInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailListDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailPageParam;
import com.kuaidao.aggregation.dto.sign.BusSignInsertOrUpdateDTO;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.paydetail.PayDetailFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.sign.BusinessSignFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author yangbiao
 * @Date: 2019/1/2 15:14
 * @Description: 付款明细
 */

@Controller
@RequestMapping("/payDetail")
public class PayDetaiController {

    private static Logger logger = LoggerFactory.getLogger(PayDetaiController.class);
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
    @Autowired
    PayDetailFeignClient payDetailFeignClient;
    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    UserInfoFeignClient userInfoFeignClient;


    /**
     * 新增
     */
    @RequestMapping("/insert")
    @ResponseBody
    public JSONResult<Boolean> saveVisitRecord(@Valid @RequestBody PayDetailInsertOrUpdateDTO dto,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateUser(user.getId());
        if ("4".equals(dto.getPayType())) { // 尾款
            dto.setMakeUpTime(null);
            dto.setAmountBalance(null);
        }
        return payDetailFeignClient.savePayDedail(dto);
    }

    /**
     * 更新签约单和付款明细
     */
    @ResponseBody
    @RequestMapping("/payOrSignUpdate")
    @RequiresPermissions("business:busCustomerManager:editPayAndSign")
    @LogRecord(operationType = LogRecord.OperationType.UPDATE, description = "编辑付款明细",
            menuName = MenuEnum.PAYANDSIGN_CHANGE)
    public JSONResult<Boolean> payOrSignUpdate(@RequestBody BusSignInsertOrUpdateDTO dto)
            throws Exception {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setUpdateUser(user.getId());
        dto.setRoleCode(user.getRoleList().get(0).getRoleCode());
        // 全款
        if (PayDetailConstant.PayType.FULL_PAYMENT.getCode().equals(dto.getSignType())) {
            dto.setMakeUpTime(null);
            dto.setAmountBalance(null);
        }
        if (user.getBusinessLine() != null) {
            dto.setBusinessLine(user.getBusinessLine());
        }
        return payDetailFeignClient.payOrSignUpdate(dto);
    }

    /***
     * 付款信息列表页
     * 
     * @return
     */
    @RequestMapping("/initPayDetailList")
    @RequiresPermissions("aggregation:payDetailManager:view")
    public String initPayDetailList(HttpServletRequest request,@RequestParam(required = false) Integer type) {
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        String ownOrgId = "";
        // 如果当前登录的为商务总监,查询所有下属商务经理
        if (roleList != null && RoleCodeEnum.SWZJ.name().equals(roleList.get(0).getRoleCode())) {
            List<OrganizationDTO> orgList = new ArrayList<OrganizationDTO>();
            UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
            userOrgRoleReq.setOrgId(user.getOrgId());
            userOrgRoleReq.setRoleCode(RoleCodeEnum.SWJL.name());
            JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                    userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
            request.setAttribute("busSaleList", listByOrgAndRole.getData());
            ownOrgId = String.valueOf(user.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(ownOrgId);
            if (curOrgGroupByOrgId != null) {
                orgList.add(curOrgGroupByOrgId);
            }
            request.setAttribute("busGroupList", orgList);
            request.setAttribute("ownOrgId", ownOrgId);
        } else if (roleList != null
                && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
            // 管理员查询所有商务组
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setOrgType(OrgTypeConstant.SWZ);
            JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                    organizationFeignClient.queryOrgByParam(queryDTO);
            request.setAttribute("busGroupList", queryOrgByParam.getData());
        } else {
            // 其他角色,查询所有下属商务组
            OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
            organizationQueryDTO.setParentId(user.getOrgId());
            organizationQueryDTO.setOrgType(OrgTypeConstant.SWZ);
            JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                    organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
            request.setAttribute("busGroupList", listDescenDantByParentId.getData());
        }
        // 电销组
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(organizationQueryDTO);
        request.setAttribute("teleGroupList", queryOrgByParam.getData());
        // 查询字典店铺类型集合
        request.setAttribute("shopTypeList",
                getDictionaryByCode(DicCodeEnum.VISITSTORETYPE.getCode()));
        // 查询赠送类型集合
        request.setAttribute("giveTypeList", getDictionaryByCode(DicCodeEnum.GIVETYPE.getCode()));
        // 查询所有签约项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(AggregationConstant.NO);
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.queryBySign(param);
        request.setAttribute("projectList", allProject.getData());

        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("aggregation:payDetailManager");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());

        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:payDetailManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        request.setAttribute("userId", user.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", user.getOrgId().toString());
        request.setAttribute("type",type);
        return "paydetail/payDetailManagerPage";
    }

    /***
     * 付款信息列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("aggregation:payDetailManager:view")
    public JSONResult<PageBean<PayDetailListDTO>> list(@RequestBody PayDetailPageParam pageParam,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setOrgId(user.getOrgId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {

            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        JSONResult<PageBean<PayDetailListDTO>> list = payDetailFeignClient.list(pageParam);

        return list;
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

    /**
     * 获取当前 orgId所在的组织
     * 
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId + "");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if (!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}", idEntity, orgJr);
            return null;
        }
        return orgJr.getData();
    }

    /***
     * 下属员工列表
     * 
     * @return
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq,
            HttpServletRequest request) {
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
    }
}
