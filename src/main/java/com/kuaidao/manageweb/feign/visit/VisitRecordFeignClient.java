package com.kuaidao.manageweb.feign.visit;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.sign.PayDetailDTO;
import com.kuaidao.aggregation.dto.visitrecord.RejectVisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;


/**
 * 来访记录
 * @author  Chen
 * @date 2019年3月1日 下午6:36:23   
 * @version V1.0
 */
@FeignClient(name = "aggregation-service-chen", path = "/aggregation/visitrecord/customerVisitRecord", fallback = VisitRecordFeignClient.HystrixClientFallback.class)
public interface VisitRecordFeignClient {
    
    /**
     * 查询 签约记录
     * @param reqDTO
     * @return
     */
    @PostMapping("/listVisitRecord")
    JSONResult<PageBean<VisitRecordRespDTO>> listVisitRecord(@RequestBody VisitRecordReqDTO visitRecordReqDTO);

    /**
     * 签约记录驳回
     * @param reqDTO
     * @return
     */
    @PostMapping("/rejectVisitRecord")
    public JSONResult<Boolean> rejectVisitRecord( @RequestBody RejectVisitRecordReqDTO reqDTO);
    
    /**
     * 根據sign_id 查詢 付款明細
     * @param idListLongReq
     * @return
     */
    @PostMapping("/listPayDetailNoPage")
    JSONResult<List<PayDetailDTO>> listPayDetailNoPage(IdListLongReq idListLongReq);
    
    @Component
    static class HystrixClientFallback implements VisitRecordFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<List<PayDetailDTO>> listPayDetailNoPage(IdListLongReq idListLongReq) {
            return fallBackError("根据signID查询付款明细");
        }


        @Override
        public JSONResult<PageBean<VisitRecordRespDTO>> listVisitRecord(
                VisitRecordReqDTO visitRecordReqDTO) {
            return fallBackError("查询签约记录");
        }


        @Override
        public JSONResult<Boolean> rejectVisitRecord(RejectVisitRecordReqDTO reqDTO) {
            return fallBackError("来访记录驳回");
        }
    }


}

