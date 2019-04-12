package com.kuaidao.manageweb.controller.financing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.financing.RefundAndImgRespDTO;
import com.kuaidao.aggregation.dto.financing.RefundImgRespDTO;
import com.kuaidao.aggregation.dto.financing.RefundInfoQueryDTO;
import com.kuaidao.aggregation.dto.financing.RefundQueryDTO;
import com.kuaidao.aggregation.dto.financing.RefundRespDTO;
import com.kuaidao.aggregation.dto.financing.RefundUpdateDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.financing.RefundFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 *  退返款
 * @author  Chen
 * @date 2019年4月10日 下午7:23:08   
 * @version V1.0
 */
@RequestMapping("/financing/refund")
@Controller
public class RefundController {
    
    private static Logger logger = LoggerFactory.getLogger(RefundController.class);

    @Autowired
    RefundFeignClient refundFeignClient;
    
    @Autowired
    OrganizationFeignClient organizationFeignClient;


    /**
     * 退款申请页面
     * @return
     */
    @RequestMapping("/refundApplyPage")
    public String refundApplyPage() {
        return "financing/refundApplyPage";
    }

    /**
     * 退款确认页面
     * @return
     */
    @RequestMapping("/refundConfirmPage")
    public String refundConfirmPage() {
        return "financing/refundConfirmPage";
    }

    /***
     * 返款申请页面
     * @return
     */
    @RequestMapping("/rebateApplayPage")
    public String rebateApplayPage() {
        return "financing/rebateApplayPage";
    }

    /***
     * 返款确认页面页面
     * @return
     */
    @RequestMapping("/rebateConfirmPage")
    public String rebateConfirmPage() {
        return "financing/rebateConfirmPage";
    }


    /***
     * 退款申请列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/listRefundApplay")
    @ResponseBody
    public JSONResult<PageBean<RefundRespDTO>> listRefundApplay(
            @RequestBody RefundQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        //处理商务参数
        handleBusinessReqParam(queryDTO);
        //处理电销参数
        handleTeleReqParam(queryDTO);
        return refundFeignClient.listRefundApply(queryDTO);
    }
    
    
    /**
     * 处理电销参数
     * @param queryDTO
     */
     private void handleTeleReqParam(RefundQueryDTO queryDTO) {
        Long teleId = queryDTO.getTeleId();
        if(teleId!=null) {
            List<Long> teleIdList = new ArrayList<>();
            teleIdList.add(teleId);
            queryDTO.setTeleIdList(teleIdList);
            return;
         }
        Long teleGroupId = queryDTO.getTeleGroupId();
        if(teleGroupId!=null) {
            List<Long> teleGroupIdList = new ArrayList<>();
            teleGroupIdList.add(teleGroupId);
            queryDTO.setTeleGroupIdList(teleGroupIdList);
            return ;
        }
        
        Long teleDeptId = queryDTO.getTeleDeptId();
        if(teleDeptId!=null) {
            OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
            busGroupReqDTO.setParentId(teleDeptId); 
            busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            busGroupReqDTO.setOrgType(OrgTypeConstant.DXZ);
            JSONResult<List<OrganizationRespDTO>> orgJr = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
            if (orgJr == null || !JSONResult.SUCCESS.equals(orgJr.getCode())) {
                logger.error("refund query org list res{{}}", orgJr);
            }else {
                List<OrganizationRespDTO> data = orgJr.getData();
                if(CollectionUtils.isNotEmpty(data)) {
                    List<Long> telGroupList = data.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
                    queryDTO.setBusGroupIdList(telGroupList);
                }
            }
            
        }
    }
     


    //处理商务参数
    private void handleBusinessReqParam(RefundQueryDTO queryDTO) {
       Long busManagerId = queryDTO.getBusManagerId();
       if(busManagerId!=null) {
           List<Long> busManagerIdList = new ArrayList<Long>();
           busManagerIdList.add(busManagerId);
           queryDTO.setBusManagerIdList(busManagerIdList);
           return;
       }
       
       Long busGroupId = queryDTO.getBusGroupId();
       if(busGroupId!=null) {
           List<Long> busGroupIdList = new ArrayList<Long>();
           busGroupIdList.add(busGroupId);
           queryDTO.setBusGroupIdList(busGroupIdList);
           return;
       }
       
       Long busAreaId = queryDTO.getBusAreaId();
       if(busAreaId!=null) {
           // 商务小组
           List<OrganizationDTO> businessGroupList = getBusinessGroupList(busAreaId, OrgTypeConstant.SWZ);
           if(CollectionUtils.isNotEmpty(businessGroupList)) {
               List<Long> busGroupIdList = businessGroupList.parallelStream().map(OrganizationDTO::getId).collect(Collectors.toList());
               queryDTO.setBusGroupIdList(busGroupIdList);
           }
         
           return;
       }
       
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
     * @param idEntityLong
     * @return
     */
    @PostMapping("/queryRefundInfoById")
    @ResponseBody
    public JSONResult<RefundAndImgRespDTO> queryRefundInfoById(@RequestBody RefundInfoQueryDTO refundInfoQueryDTO){
        Long id = refundInfoQueryDTO.getId();
        if(id == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundInfoQueryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.queryRefundInfoById(refundInfoQueryDTO);
    }
    
    
    /**
     * 标记退款结束
     * @return
     */
    @PostMapping("/updateRefundInfo")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "标记退款结束",
    menuName = MenuEnum.REFUNDAPPLYLIST)
    public JSONResult<Boolean> updateRefundInfo(@RequestBody RefundUpdateDTO refundUpdateDTO){
       List<Long> idList = refundUpdateDTO.getIdList();
       if(CollectionUtils.isEmpty(idList)) {
           return CommonUtil.getParamIllegalJSONResult();
       }
       refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_5);
       refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
       return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    
    /***
     * 退款确认列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/listRefundConfirm")
    @ResponseBody
    public JSONResult<PageBean<RefundRespDTO>> listRefundConfirm(
            @RequestBody RefundQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if (RoleCodeEnum.QDSJCW.name().equals(roleCode)){
            queryDTO.setRoleCode(roleCode);
        }else if(RoleCodeEnum.SJHZCW.name().equals(roleCode)) {
            queryDTO.setRoleCode(roleCode);
        }else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }
        return refundFeignClient.listRefundApply(queryDTO);
    }
    /**
     * 退款确认
     * @param result
     * @return
     */
    @PostMapping("/updateRefundConfirm")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "退款确认",
    menuName = MenuEnum.REFUNDCONFIRM)
    public JSONResult<Boolean> updateRefundConfirm(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_3);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 驳回退款
     * @param result
     * @return
     */
    @PostMapping("/rejectRefund")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "驳回退款",
    menuName = MenuEnum.REFUNDCONFIRM)
    public JSONResult<Boolean> rejectRefund(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_2);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 标记退款
     * @param result
     * @return
     */
    @PostMapping("/markRefund")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "标记退款",
    menuName = MenuEnum.REFUNDCONFIRM)
    public JSONResult markRefund(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_4);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REFOUND_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }


    /***
     * 返款申请列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/listRebateApplay")
    @ResponseBody
    public JSONResult<PageBean<RefundRespDTO>> listRebateApplay(
            @RequestBody RefundQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.listRefundApply(queryDTO);
    }
    
    /***
     * 返款确认列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/listRebateConfirm")
    @ResponseBody
    public JSONResult<PageBean<RefundRespDTO>> listRebateConfirm(
            @RequestBody RefundQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setCurUser(curLoginUser.getId());
        queryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
       if (RoleCodeEnum.QDSJCW.name().equals(roleCode)){
           queryDTO.setRoleCode(roleCode);
       }else if(RoleCodeEnum.SJHZCW.name().equals(roleCode)) {
           queryDTO.setRoleCode(roleCode);
       }else {
           return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
       }
        return refundFeignClient.listRefundApply(queryDTO);
    }
    
    
    /**
     * 标记返款结束
     * @return
     */
    @PostMapping("/updateRebateInfo")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "标记返款结束",
    menuName = MenuEnum.REBATEAPPLYLIST)
    public JSONResult<Boolean> updateRebateInfo(@RequestBody RefundUpdateDTO refundUpdateDTO){
       List<Long> idList = refundUpdateDTO.getIdList();
       if(CollectionUtils.isEmpty(idList)) {
           return CommonUtil.getParamIllegalJSONResult();
       }
       refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_5);
       refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
       return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 更新返款确认
     * @param result
     * @return
     */
    @PostMapping("/updateRebateConfirm")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "返款确认",
    menuName = MenuEnum.REBATECONFIRM)
    public JSONResult<Boolean> updateRebateConfirm(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_3);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 驳回返款
     * @param result
     * @return
     */
    @PostMapping("/rejectRebate")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "驳回返款",
    menuName = MenuEnum.REBATECONFIRM)
    public JSONResult<Boolean> rejectRebate(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_2);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 标记返款
     * @param result
     * @return
     */
    @PostMapping("/markRebate")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "标记返款",
    menuName = MenuEnum.REBATECONFIRM)
    public JSONResult markRebate(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_4);
        refundUpdateDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /***
     * 根据Id 查询图片列表
     */
    @PostMapping("/listImgById")
    @ResponseBody
    public JSONResult<List<RefundImgRespDTO>> listImgById(@RequestBody IdEntityLong idEntityLong){
        if(idEntityLong.getId()==null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        return refundFeignClient.listImgById(idEntityLong);
    }
    
    /**
     * 根据图片Id 删除 图片
     * @param idListLongReq
     * @return
     */
    @PostMapping("/deleteImgByIdList")
    @ResponseBody
    @LogRecord(operationType = OperationType.UPDATE, description = "删除图片",
    menuName = MenuEnum.REBATECONFIRM)
    public JSONResult<Boolean> deleteImgByIdList(@RequestBody IdListLongReq idListLongReq){
        List<Long> idList = idListLongReq.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        return  refundFeignClient.deleteImgByIdList(idListLongReq);
    }
    
    
    /**
     * 查询返款详情 根据Id
     * @param idEntityLong
     * @return
     */
    @PostMapping("/queryRebateInfoById")
    @ResponseBody
    public JSONResult<RefundAndImgRespDTO> queryRebateInfoById(@RequestBody RefundInfoQueryDTO refundInfoQueryDTO){
        Long id = refundInfoQueryDTO.getId();
        if(id == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundInfoQueryDTO.setType(AggregationConstant.REFOUND_REBATE_TYPE.REBATE_TYPE);
        return refundFeignClient.queryRefundInfoById(refundInfoQueryDTO);
    }

}
