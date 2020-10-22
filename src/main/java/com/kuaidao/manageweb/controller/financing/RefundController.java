package com.kuaidao.manageweb.controller.financing;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.google.common.collect.Lists;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;

import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import com.kuaidao.aggregation.dto.financing.RefundAndImgRespDTO;
import com.kuaidao.aggregation.dto.financing.RefundEditRejectReqDTO;
import com.kuaidao.aggregation.dto.financing.RefundImgDTO;
import com.kuaidao.aggregation.dto.financing.RefundImgRespDTO;
import com.kuaidao.aggregation.dto.financing.RefundInfoQueryDTO;
import com.kuaidao.aggregation.dto.financing.RefundQueryDTO;
import com.kuaidao.aggregation.dto.financing.RefundRespDTO;
import com.kuaidao.aggregation.dto.financing.RefundUpdateDTO;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.financing.FinanceLayoutFeignClient;
import com.kuaidao.manageweb.feign.financing.RefundFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * 退返款
 * 
 * @author Chen
 * @date 2019年4月10日 下午7:23:08
 * @version V1.0
 */
@RequestMapping("/financing/refund")
@Controller
public class RefundController {

    private static Logger logger = LoggerFactory.getLogger(RefundController.class);

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    RefundFeignClient refundFeignClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    RestTemplate restTemplate;

    @Value("${oss.url.directUpload}")
    private String ossUrl;

    @Autowired
    FinanceLayoutFeignClient financeLayoutFeignClient;

    /**
     * 退款申请页面
     * 
     * @return
     */
    @RequiresPermissions("aggregation:refundApply:view")
    @RequestMapping("/refundApplyPage")
    public String refundApplyPage(HttpServletRequest request) {
        request.setAttribute("ossUrl", ossUrl);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        request.setAttribute("businessLine", curLoginUser.getBusinessLine());
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        return "financing/refundApplyPage";
    }

    /**
     * 退款确认页面
     * 
     * @return
     */
    @RequiresPermissions("aggregation:refundConfirm:view")
    @RequestMapping("/refundConfirmPage")
    public String refundConfirmPage(HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        request.setAttribute("businessLine", curLoginUser.getBusinessLine());
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        return "financing/refundConfirmPage";
    }

    /***
     * 返款申请页面
     * 
     * @return
     */
    @RequiresPermissions("aggregation:rebateApply:view")
    @RequestMapping("/rebateApplyPage")
    public String rebateApplayPage(HttpServletRequest request) {
        request.setAttribute("ossUrl", ossUrl);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        request.setAttribute("businessLine", curLoginUser.getBusinessLine());
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        return "financing/rebateApplyPage";
    }

    /***
     * 返款确认页面页面
     * 
     * @return
     */
    @RequiresPermissions("aggregation:rebateConfirm:view")
    @RequestMapping("/rebateConfirmPage")
    public String rebateConfirmPage(HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        request.setAttribute("businessLine", curLoginUser.getBusinessLine());
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        return "financing/rebateConfirmPage";
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

    /***
     * 退款申请列表
     * 
     * @param queryDTO
     * @return
     */
    @RequiresPermissions("aggregation:refundApply:view")
    @PostMapping("/listRefundApplay")
    @ResponseBody
    public JSONResult<PageBean<RefundRespDTO>> listRefundApplay(
            @RequestBody RefundQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        // 处理商务参数
        // handleBusinessReqParam(queryDTO);
        // 处理电销参数
        // handleTeleReqParam(queryDTO);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        return refundFeignClient.listRefundApply(queryDTO);
    }


    /**
     * 处理电销参数
     * 
     * @param queryDTO
     */
    private void handleTeleReqParam(RefundQueryDTO queryDTO) {
        Long teleId = queryDTO.getTeleId();
        if (teleId != null) {
            List<Long> teleIdList = new ArrayList<>();
            teleIdList.add(teleId);
            queryDTO.setTeleIdList(teleIdList);
            return;
        }
        Long teleGroupId = queryDTO.getTeleGroupId();
        if (teleGroupId != null) {
            List<Long> teleGroupIdList = new ArrayList<>();
            teleGroupIdList.add(teleGroupId);
            queryDTO.setTeleGroupIdList(teleGroupIdList);
            return;
        }

        Long teleDeptId = queryDTO.getTeleDeptId();
        if (teleDeptId != null) {
            OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
            busGroupReqDTO.setParentId(teleDeptId);
            busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            busGroupReqDTO.setOrgType(OrgTypeConstant.DXZ);
            JSONResult<List<OrganizationRespDTO>> orgJr =
                    organizationFeignClient.queryOrgByParam(busGroupReqDTO);
            if (orgJr == null || !JSONResult.SUCCESS.equals(orgJr.getCode())) {
                logger.error("refund query org list res{{}}", orgJr);
            } else {
                List<OrganizationRespDTO> data = orgJr.getData();
                if (CollectionUtils.isNotEmpty(data)) {
                    List<Long> telGroupList = data.parallelStream().map(OrganizationRespDTO::getId)
                            .collect(Collectors.toList());
                    queryDTO.setBusGroupIdList(telGroupList);
                }
            }

        }
    }



    // 处理商务参数
    private void handleBusinessReqParam(RefundQueryDTO queryDTO) {
        Long busManagerId = queryDTO.getBusManagerId();
        /*
         * if(busManagerId!=null) { List<Long> busManagerIdList = new ArrayList<Long>();
         * busManagerIdList.add(busManagerId); queryDTO.setBusManagerIdList(busManagerIdList);
         * return; }
         */

        /*
         * Long busGroupId = queryDTO.getBusGroupId(); if(busGroupId!=null) { List<Long>
         * busGroupIdList = new ArrayList<Long>(); busGroupIdList.add(busGroupId);
         * queryDTO.setBusGroupIdList(busGroupIdList); return; }
         */

        /*
         * Long busAreaId = queryDTO.getBusAreaId(); if(busAreaId!=null) { // 商务小组
         * List<OrganizationDTO> businessGroupList = getBusinessGroupList(busAreaId,
         * OrgTypeConstant.SWZ); if(CollectionUtils.isNotEmpty(businessGroupList)) { List<Long>
         * busGroupIdList =
         * businessGroupList.parallelStream().map(OrganizationDTO::getId).collect(Collectors.toList(
         * )); queryDTO.setBusGroupIdList(busGroupIdList); }
         * 
         * return; }
         */

    }

    /**
     * 获取商务组
     * 
     * @param orgId
     * @param orgType
     * @return
     */
    private List<OrganizationDTO> getBusinessGroupList(Long orgId, Integer orgType) {
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationDTO>> orgJr =
                organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        if (orgJr == null || !JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("query org list res{{}}", orgJr);
            return null;
        }
        return orgJr.getData();

    }


    /**
     * 查询退款详情 根据Id
     * 
     * @return
     */
    @PostMapping("/queryRefundInfoById")
    @ResponseBody
    public JSONResult<RefundAndImgRespDTO> queryRefundInfoById(
            @RequestBody RefundInfoQueryDTO refundInfoQueryDTO) {
        Long id = refundInfoQueryDTO.getId();
        if (id == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundInfoQueryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.queryRefundInfoById(refundInfoQueryDTO);
    }


    /**
     * 标记退款结束
     * 
     * @return
     */
    @RequiresPermissions("aggregation:refundApply:updateRefundFinish")
    @PostMapping("/updateRefundInfo")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "标记退款结束",
            menuName = MenuEnum.REFUNDAPPLYLIST)
    public JSONResult<Boolean> updateRefundInfo(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_5);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }


    /**
     * 编辑驳回退款
     * 
     * @return
     */
    @PostMapping("/editRejectRefundInfo")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "编辑驳回退款",
            menuName = MenuEnum.REFUNDAPPLYLIST)
    public JSONResult<Boolean> editRejectRefundInfo(
            @RequestBody RefundEditRejectReqDTO refundUpdateDTO) {
        Long id = refundUpdateDTO.getId();
        if (id == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.editRejectRefundInfo(refundUpdateDTO);
    }


    /***
     * 退款确认列表
     * 
     * @param queryDTO
     * @return
     */
    @RequiresPermissions("aggregation:refundConfirm:view")
    @PostMapping("/listRefundConfirm")
    @ResponseBody
    public JSONResult<PageBean<RefundRespDTO>> listRefundConfirm(
            @RequestBody RefundQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if (RoleCodeEnum.QDSJCW.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        } else if (RoleCodeEnum.SJHZCW.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        }else if (RoleCodeEnum.SJHZCN.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        } else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }
        return refundFeignClient.listRefundApply(queryDTO);
    }

    /**
     * 退款确认
     * 
     * @param
     * @return
     */
    @RequiresPermissions("aggregation:refundConfirm:confirmRefund")
    @PostMapping("/updateRefundConfirm")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "退款确认",
            menuName = MenuEnum.REFUNDCONFIRM)
    public JSONResult<Boolean> updateRefundConfirm(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        refundUpdateDTO.setCurUser(curLoginUser.getId());
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_3);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }

    /**
     * 驳回退款
     * 
     * @return
     */
    @RequiresPermissions("aggregation:refundConfirm:rejectRefund")
    @PostMapping("/rejectRefund")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "驳回退款",
            menuName = MenuEnum.REFUNDCONFIRM)
    public JSONResult<Boolean> rejectRefund(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        refundUpdateDTO.setCurUser(curLoginUser.getId());
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_2);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }

    /**
     * 标记退款
     * 
     * @return
     */
    @RequiresPermissions("aggregation:refundConfirm:markRefund")
    @PostMapping("/markRefund")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "标记退款",
            menuName = MenuEnum.REFUNDCONFIRM)
    public JSONResult markRefund(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        refundUpdateDTO.setCurUser(curLoginUser.getId());
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_4);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }


    /***
     * 返款申请列表
     * 
     * @return
     */
    @RequiresPermissions("aggregation:rebateApply:view")
    @PostMapping("/listRebateApply")
    @ResponseBody
    public JSONResult<PageBean<RefundRespDTO>> listRebateApply(
            @RequestBody RefundQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        return refundFeignClient.listRefundApply(queryDTO);
    }

    /***
     * 返款确认列表
     * 
     * @return
     */
    @RequiresPermissions("aggregation:rebateConfirm:view")
    @PostMapping("/listRebateConfirm")
    @ResponseBody
    public JSONResult<PageBean<RefundRespDTO>> listRebateConfirm(
            @RequestBody RefundQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if (RoleCodeEnum.QDSJCW.name().equals(roleCode)
                || RoleCodeEnum.SJHZCW.name().equals(roleCode) ||  RoleCodeEnum.SJHZCN.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        } else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }
        // queryDTO.setRoleCode(RoleCodeEnum.QDSJCW.name());
        return refundFeignClient.listRefundApply(queryDTO);
    }


    /**
     * 标记返款结束
     * 
     * @return
     */
    @RequiresPermissions("aggregation:rebateApply:markRefundFinish")
    @PostMapping("/updateRebateInfo")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "标记返款结束",
            menuName = MenuEnum.REBATEAPPLYLIST)
    public JSONResult<Boolean> updateRebateInfo(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        refundUpdateDTO.setCurUser(curLoginUser.getId());
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_5);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }

    /**
     * 更新返款确认
     * 
     * @return
     */
    @RequiresPermissions("aggregation:rebateConfirm:confirmRebate")
    @PostMapping("/updateRebateConfirm")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "返款确认",
            menuName = MenuEnum.REBATECONFIRM)
    public JSONResult<Boolean> updateRebateConfirm(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        refundUpdateDTO.setCurUser(curLoginUser.getId());
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_3);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }

    /**
     * 驳回返款
     * 
     * @return
     */
    @RequiresPermissions("aggregation:rebateConfirm:rejectRebate")
    @PostMapping("/rejectRebate")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "驳回返款",
            menuName = MenuEnum.REBATECONFIRM)
    public JSONResult<Boolean> rejectRebate(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        refundUpdateDTO.setCurUser(curLoginUser.getId());
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_2);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }

    /**
     * 标记返款
     * 
     * @return
     */
    @RequiresPermissions("aggregation:rebateConfirm:markRebate")
    @PostMapping("/markRebate")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "标记返款",
            menuName = MenuEnum.REBATECONFIRM)
    public JSONResult markRebate(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        refundUpdateDTO.setCurUser(curLoginUser.getId());
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_4);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }

    /***
     * 根据退返款Id 查询图片列表
     */
    @PostMapping("/listImgById")
    @ResponseBody
    public JSONResult<List<RefundImgRespDTO>> listImgById(@RequestBody IdEntityLong idEntityLong) {
        if (idEntityLong.getId() == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        return refundFeignClient.listImgById(idEntityLong);
    }


    /**
     * 编辑驳回返款
     * 
     * @return
     */
    @PostMapping("/editRejectRebateInfo")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "编辑驳回返款",
            menuName = MenuEnum.REBATEAPPLYLIST)
    public JSONResult<Boolean> editRejectRebateInfo(
            @RequestBody RefundEditRejectReqDTO refundUpdateDTO) {
        Long id = refundUpdateDTO.getId();
        if (id == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.editRejectRefundInfo(refundUpdateDTO);
    }


    /**
     * 根据图片Id 删除 图片
     * 
     * @param idListLongReq
     * @return
     */
    @PostMapping("/deleteImgByIdList")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "删除图片",
            menuName = MenuEnum.REBATECONFIRM)
    public JSONResult<Boolean> deleteImgByIdList(@RequestBody IdListLongReq idListLongReq) {
        List<Long> idList = idListLongReq.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        return refundFeignClient.deleteImgByIdList(idListLongReq);
    }


    /**
     * 查询返款详情 根据Id
     * 
     * @return
     */
    @PostMapping("/queryRebateInfoById")
    @ResponseBody
    public JSONResult<RefundAndImgRespDTO> queryRebateInfoById(
            @RequestBody RefundInfoQueryDTO refundInfoQueryDTO) {
        Long id = refundInfoQueryDTO.getId();
        if (id == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundInfoQueryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.queryRefundInfoById(refundInfoQueryDTO);
    }


    /**
     * 查询签约单信息
     * 
     * @param
     * @return
     */
    @PostMapping("/querySignInfoBySignNo")
    @ResponseBody
    public JSONResult<Map<String, Object>> querySignInfoBySignNo(
            @RequestBody RefundQueryDTO refundQueryDTO) {
        String signNo = refundQueryDTO.getSignNo();
        if (!CommonUtil.isNotBlank(signNo)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        return refundFeignClient.querySignInfoBySignNo(refundQueryDTO);
    }

    /**
     * 下载oss 图片
     * 
     * @param url
     * @return
     */
    @RequestMapping("/downloadOssImg")
    @ResponseBody
    public ResponseEntity<byte[]> downloadOssImg(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<byte[]> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        byte[] imageBytes = response.getBody();
        return new ResponseEntity<byte[]>(imageBytes, headers, HttpStatus.OK);
    }

    /**
     * 插入图片信息
     * 
     * @param refundImgDTO
     * @return
     */
    @PostMapping("/insertImgInfo")
    @ResponseBody
    public JSONResult<Long> insertImgInfo(@RequestBody RefundImgDTO refundImgDTO) {
        return refundFeignClient.insertImgInfo(refundImgDTO);
    }
    /***
     * 退款申请列表
     *
     * @param queryDTO
     * @return
     */
    @LogRecord(description = "导出退款列表", operationType = OperationType.EXPORT,
            menuName = MenuEnum.REFUNDAPPLYLIST)
    @RequiresPermissions("aggregation:refundConfirm:export")
    @PostMapping("/listRefundApplyExport")
    public void listRefundApplyExport(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody RefundQueryDTO queryDTO) throws Exception {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        // 处理商务参数
        // handleBusinessReqParam(queryDTO);
        // 处理电销参数
        // handleTeleReqParam(queryDTO);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if (RoleCodeEnum.QDSJCW.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        } else if (RoleCodeEnum.SJHZCW.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        }
        JSONResult<List<RefundRespDTO>> refundRespResult = refundFeignClient.listRefundApplyExport(queryDTO);
        List<RefundExportModel> refundExportModels = new ArrayList<>();
        if (JSONResult.SUCCESS.equals(refundRespResult.getCode()) && refundRespResult.getData() != null
                && refundRespResult.getData().size() > 0) {
            List<RefundRespDTO> refundRespList = refundRespResult.getData();
            for(RefundRespDTO refundRespDTO:refundRespList){
                RefundExportModel refundExportModel = new RefundExportModel();
                BeanUtils.copyProperties(refundRespDTO, refundExportModel);
                refundExportModels.add(refundExportModel);
            }
        }
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            // XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel1(dataList);
            String name = "退款导出" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ExcelWriter excelWriter = EasyExcel.write(outputStream, RefundExportModel.class).build();
            Integer[] widthArr = new Integer[] {5200,3500,5200,4000,5200,5200,4000,4000,2600,5200,4000,4000,4000,4000,4000,5200,5200,5200};
            Map<Integer, Integer> columnWidthMap = new HashMap<>();

            //实例化表单
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "退款导出" ).build();
            writeSheet.setColumnWidthMap(ExcelUtil.setColunmWidth(widthArr));
            excelWriter.write(refundExportModels, writeSheet);
            excelWriter.finish();
        }
    }
    /***
     * 返款申请列表
     *
     * @param queryDTO
     * @return
     */
    @LogRecord(description = "导出返款列表", operationType = OperationType.EXPORT,
            menuName = MenuEnum.REBATEAPPLYLIST)
    @RequiresPermissions("aggregation:rebateConfirm:export")
    @PostMapping("/listRebateApplyExport")
    public void listRebateApplyExport(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody RefundQueryDTO queryDTO) throws Exception {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        // 处理商务参数
        // handleBusinessReqParam(queryDTO);
        // 处理电销参数
        // handleTeleReqParam(queryDTO);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if (RoleCodeEnum.QDSJCW.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        } else if (RoleCodeEnum.SJHZCW.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        }
        List<RebateExportModel> refundExportModels = new ArrayList<>();
        JSONResult<List<RefundRespDTO>> refundRespResult = refundFeignClient.listRefundApplyExport(queryDTO);
        if (JSONResult.SUCCESS.equals(refundRespResult.getCode()) && refundRespResult.getData() != null
                && refundRespResult.getData().size() > 0) {
            List<RefundRespDTO> refundRespList = refundRespResult.getData();
            for(RefundRespDTO refundRespDTO:refundRespList){
                RebateExportModel rebateExportModel = new RebateExportModel();
                BeanUtils.copyProperties(refundRespDTO, rebateExportModel);
                refundExportModels.add(rebateExportModel);
            }

        }
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            // XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel1(dataList);
            String name = "返款导出" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ExcelWriter excelWriter = EasyExcel.write(outputStream, RebateExportModel.class).build();
            Integer[] widthArr = new Integer[] {5200,3500,5200,4000,5200,5200,4000,4000,2600,5200,4000,4000,4000,4000,4000,5200,5200,5200};
            //实例化表单
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "返款导出" ).build();
            Map<Integer, Integer> columnWidthMap = new HashMap<>();
            writeSheet.setColumnWidthMap(ExcelUtil.setColunmWidth(widthArr));
            excelWriter.write(refundExportModels, writeSheet);
            excelWriter.finish();
        }
    }


}
