package com.kuaidao.manageweb.feign.financing;

import com.kuaidao.aggregation.dto.financing.*;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

/**
 *  退返款 
 * @author  Chen
 * @date 2019年4月10日 下午7:35:14   
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/financing/refund", fallback = RefundFeignClient.HystrixClientFallback.class)
public interface RefundFeignClient {
    /**
     * 退返款申请列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/listRefundApply")
    JSONResult<PageBean<RefundRespDTO>> listRefundApply(RefundQueryDTO queryDTO);



    /**
     * 更新退还款信息
     * @param refundUpdateDTO
     * @return
     */
    @PostMapping("/updateRefundInfo")
    JSONResult<Boolean> updateRefundInfo(RefundUpdateDTO refundUpdateDTO);
    
    /**
     * 根据退返款ID 查询 图片地址
     * @param idEntityLong
     * @return
     */
    @PostMapping("/listImgById")
    JSONResult<List<RefundImgRespDTO>> listImgById(IdEntityLong idEntityLong);
    
    
    /**
     * 根据图片地址删除图片
     * @param idListLongReq
     * @return
     */
    @PostMapping("/deleteImgByIdList")
    JSONResult<Boolean> deleteImgByIdList(IdListLongReq idListLongReq);
    
    /**
     * 查询退返款详情 根据Id
     * @param idEntityLong
     * @return
     */
    @PostMapping("/queryRefundInfoById")
    JSONResult<RefundAndImgRespDTO> queryRefundInfoById(RefundInfoQueryDTO refundInfoQueryDTO);
    
    
    /**
     * 编辑驳回退款
     * @param refundUpdateDTO
     * @return
     */
    @PostMapping("/editRejectRefundInfo")
    JSONResult<Boolean> editRejectRefundInfo(RefundEditRejectReqDTO refundUpdateDTO);
    
    /**
     *   查询签约单信息  
     * @param refundQueryDTO
     * @return
     */
    @PostMapping("/querySignInfoBySignNo")
    JSONResult<Map<String, Object>> querySignInfoBySignNo(RefundQueryDTO refundQueryDTO);

    /**
     * 条件查询签约单退款信息
     * @param
     * @return
     */
    @PostMapping("/getRefundInfo")
    JSONResult<RefundRebateDTO> getRefundInfo(Map<String, Object> map);
    
    
/***
 * 插入图片
 * @param refundImgDTO
 * @return
 */
    @PostMapping("/insertImgInfo")
    JSONResult<Long> insertImgInfo(RefundImgDTO refundImgDTO);

    /**
     * 退返款申请列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/listRefundApplyExport")
    JSONResult<List<RefundRespDTO>> listRefundApplyExport(RefundQueryDTO queryDTO);

    @Component
    static class HystrixClientFallback implements RefundFeignClient {

        private static Logger logger = LoggerFactory.getLogger(RefundFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<RefundRespDTO>> listRefundApply(RefundQueryDTO queryDTO) {
            return fallBackError("查询退返款申请列表");
        }

        @Override
        public JSONResult<Boolean> updateRefundInfo(RefundUpdateDTO refundUpdateDTO) {
            return fallBackError("更新退返款信息");
        }

        @Override
        public JSONResult<List<RefundImgRespDTO>> listImgById(IdEntityLong idEntityLong) {
            return fallBackError("根据退返款ID 查询 图片地址");
        }

        @Override
        public JSONResult<Boolean> deleteImgByIdList(IdListLongReq idListLongReq) {
            return fallBackError("根据图片地址删除图片");
        }

        @Override
        public JSONResult<RefundAndImgRespDTO> queryRefundInfoById(RefundInfoQueryDTO refundInfoQueryDTO) {
            return fallBackError("根据Id查询退返款详情");
        }

        @Override
        public JSONResult<Boolean> editRejectRefundInfo(RefundEditRejectReqDTO refundUpdateDTO) {
            return fallBackError("编辑驳回退款");
        }

        @Override
        public JSONResult<Map<String, Object>> querySignInfoBySignNo(
                RefundQueryDTO refundQueryDTO) {
            return fallBackError("根据signno查询签约单编号");
        }

        @Override
        public JSONResult<RefundRebateDTO> getRefundInfo(Map<String, Object> map) {
            return fallBackError("条件查询签约单退款信息");
        }

        @Override
        public JSONResult<Long> insertImgInfo(RefundImgDTO refundImgDTO) {
            return fallBackError("插入图片");
        }

        @Override
        public JSONResult<List<RefundRespDTO>> listRefundApplyExport(RefundQueryDTO queryDTO) {
            return fallBackError("查询退返款申请列表");
        }
    }


}
