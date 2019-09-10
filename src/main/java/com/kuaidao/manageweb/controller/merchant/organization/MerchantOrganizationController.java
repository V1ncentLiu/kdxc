package com.kuaidao.manageweb.controller.merchant.organization;

import com.kuaidao.common.constant.*;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.OrgUserReqDTO;
import com.kuaidao.sys.dto.user.UserAndRoleRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 组织机构类
 * 
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午3:04:53
 * @version V1.0
 */
@Controller
@RequestMapping("/merchant/merchantorganization")
public class MerchantOrganizationController {

    private static Logger logger = LoggerFactory.getLogger(MerchantOrganizationController.class);

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    /**
     * 商家组织结构
     * 
     * @return
     */
    @RequiresPermissions("merchantOrganization:view")
    @RequestMapping("/organizationPage")
    public String organizationPage(HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        RoleInfoDTO roleInfoDTO = curLoginUser.getRoleList().get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        // JSONResult<List<TreeData>> treeJsonRes = organizationFeignClient.query();
        JSONResult<List<TreeData>> treeJsonRes = null;
        OrganizationQueryDTO reqDto = new OrganizationQueryDTO();
        reqDto.setSource(2);
        if (RoleCodeEnum.GLY.name().equals(roleCode)) {
            // 管理员
            treeJsonRes = organizationFeignClient.queryList(reqDto);
        } else {
            // 业务管理员
            Long orgId = curLoginUser.getOrgId();
            reqDto.setParentId(orgId);

            treeJsonRes = organizationFeignClient.queryByOrg(reqDto);
        }
        if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode())
                && treeJsonRes.getData() != null) {
            request.setAttribute("orgData", treeJsonRes.getData());
        } else {
            logger.error("query organization tree,res{{}}", treeJsonRes);
        }
        request.setAttribute("tgzxBusinessLine", SysConstant.PROMOTION_BUSINESS_LINE);

        JSONResult<List<DictionaryItemRespDTO>> orgTypeJR = dictionaryItemFeignClient
                .queryDicItemsByGroupCode(DicCodeEnum.ORGANIZATIONTYPE.getCode());
        request.setAttribute("orgTypeList", orgTypeJR.getData());

        JSONResult<List<DictionaryItemRespDTO>> businessLineJR = dictionaryItemFeignClient
                .queryDicItemsByGroupCode(DicCodeEnum.BUSINESS_LINE.getCode());
        request.setAttribute("businessLineList", businessLineJR.getData());

        return "merchant/organization/organizationPage";
    }

    /**
     * 保存或更新组织机构信息
     *
     * @param orgDTO
     * @param result
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = "merchantOrganization:add")
    @PostMapping("/save")
    @ResponseBody
    @LogRecord(description = "添加商家组织机构信息", operationType = OperationType.INSERT,
            menuName = MenuEnum.MERCHANT_ORGANIZATION_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody OrganizationAddAndUpdateDTO orgDTO,
                           BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        orgDTO.setSystemCode(SystemCodeConstant.HUI_JU);

        Long id = orgDTO.getId();
        if (id != null) {
            return organizationFeignClient.update(orgDTO);
        } else {
            Subject subject = SecurityUtils.getSubject();
            UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
            orgDTO.setCreateUser(user.getId());
            return organizationFeignClient.save(orgDTO);
        }

    }

    /**
     * 更新组织机构
     *
     * @param orgDTO
     * @return
     */
    @RequiresPermissions("merchantOrganization:edit")
    @PostMapping("/update")
    @ResponseBody
    @LogRecord(description = "修改商家组织机构信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.MERCHANT_ORGANIZATION_MANAGEMENT)
    public JSONResult update(@Valid @RequestBody OrganizationAddAndUpdateDTO orgDTO,
                             BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return organizationFeignClient.update(orgDTO);
    }
    /**
     * 删除组织机构
     *
     * @param
     * @return
     */
    @RequiresPermissions("merchantOrganization:delete")
    @PostMapping("/delete")
    @ResponseBody
    @LogRecord(description = "删除商家组织机构信息", operationType = OperationType.DELETE,
            menuName = MenuEnum.MERCHANT_ORGANIZATION_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        logger.info("delete organization by id{{}}", idList);
        return organizationFeignClient.delete(idListReq);
    }
}
