package com.kuaidao.manageweb.controller.financing;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
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
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.financing.RefundFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 *  退返款
 * @author  Chen
 * @date 2019年4月10日 下午7:23:08   
 * @version V1.0
 */
@RequestMapping("/financing/refund")
@Controller
public class RefundController {

    @Autowired
    RefundFeignClient refundFeignClient;

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
        return refundFeignClient.listRefundApply(queryDTO);
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
    public JSONResult<Boolean> updateRefundInfo(@RequestBody RefundUpdateDTO refundUpdateDTO){
       List<Long> idList = refundUpdateDTO.getIdList();
       if(CollectionUtils.isEmpty(idList)) {
           return CommonUtil.getParamIllegalJSONResult();
       }
       refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_5);
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
     * 更新退款确认
     * @param result
     * @return
     */
    @PostMapping("/updateRefundConfirm")
    @ResponseBody
    public JSONResult<Boolean> updateRefundConfirm(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_3);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 驳回退款
     * @param result
     * @return
     */
    @PostMapping("/rejectRefund")
    @ResponseBody
    public JSONResult<Boolean> rejectRefund(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_2);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 标记退款
     * @param result
     * @return
     */
    @PostMapping("/markRefund")
    @ResponseBody
    public JSONResult markRefund(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_4);
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
    public JSONResult<Boolean> updateRebateInfo(@RequestBody RefundUpdateDTO refundUpdateDTO){
       List<Long> idList = refundUpdateDTO.getIdList();
       if(CollectionUtils.isEmpty(idList)) {
           return CommonUtil.getParamIllegalJSONResult();
       }
       refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_5);
       return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 更新返款确认
     * @param result
     * @return
     */
    @PostMapping("/updateRebateConfirm")
    @ResponseBody
    public JSONResult<Boolean> updateRebateConfirm(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_3);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 驳回返款
     * @param result
     * @return
     */
    @PostMapping("/rejectRebate")
    @ResponseBody
    public JSONResult<Boolean> rejectRebate(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_2);
        return refundFeignClient.updateRefundInfo(refundUpdateDTO);
    }
    
    /**
     * 标记返款
     * @param result
     * @return
     */
    @PostMapping("/markRebate")
    @ResponseBody
    public JSONResult markRebate(@RequestBody RefundUpdateDTO refundUpdateDTO) {
        List<Long> idList = refundUpdateDTO.getIdList();
        if(CollectionUtils.isEmpty(idList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        refundUpdateDTO.setStatus(AggregationConstant.REFOUND_REBATE_STATUS.STATUS_4);
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
