package com.kuaidao.manageweb.feign.sign;


import java.util.List;

import com.kuaidao.aggregation.dto.paydetail.PayDetailReqDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.aggregation.dto.busmycustomer.RejectSignOrderReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordRespDTO;
import com.kuaidao.aggregation.dto.console.BusinessConsolePanelRespDTO;
import com.kuaidao.aggregation.dto.console.BusinessConsoleReqDTO;
import com.kuaidao.aggregation.dto.sign.PayDetailDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;


/**
 * 签约记录
 * @author  Chen
 * @date 2019年3月1日 下午6:36:23   
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/sign/signRecord", fallback = SignRecordFeignClient.HystrixClientFallback.class)
public interface SignRecordFeignClient {
    
    /**
     * 查询 签约记录
     * @param reqDTO
     * @return
     */
    @PostMapping("/listSignRecord")
    JSONResult<PageBean<SignRecordRespDTO>> listSignRecord(@RequestBody SignRecordReqDTO reqDTO);

    /**
     * 签约记录驳回
     * @param reqDTO
     * @return
     */
    @PostMapping("/rejectSignOrder")
    JSONResult<Boolean> rejectSignOrder(@RequestBody RejectSignOrderReqDTO reqDTO);
    
    /**
     * 根據pay_id 查詢 付款明細
     * @param payDetailReqDTO
     * @return
     */
    @PostMapping("/listPayDetailNoPage")
    JSONResult<List<PayDetailDTO>> listPayDetailNoPage(@RequestBody PayDetailReqDTO payDetailReqDTO);
    
    /**
     *当月签约数
     * @param businessConsoleReqDTO
     * @return
     */
    @PostMapping("/countCurMonthSignedNum")
    JSONResult<BusinessConsolePanelRespDTO> countCurMonthSignedNum(BusinessConsoleReqDTO businessConsoleReqDTO);
   
    /**
     * 商务总监控制台  待审批签约记录
     * @param reqDTO
     * @return
     */
    @PostMapping("/listSignRecordNoPage")
    JSONResult<List<SignRecordRespDTO>> listSignRecordNoPage(SignRecordReqDTO reqDTO);
    
    
    @Component
    static class HystrixClientFallback implements SignRecordFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<SignRecordRespDTO>> listSignRecord(SignRecordReqDTO reqDTO) {
            return fallBackError("查询签约记录");
        }

        @Override
        public JSONResult<Boolean> rejectSignOrder(RejectSignOrderReqDTO reqDTO) {
            return fallBackError("签约记录驳回");
        }

        @Override
        public JSONResult<List<PayDetailDTO>> listPayDetailNoPage(PayDetailReqDTO payDetailReqDTO) {
            return fallBackError("根据signID查询付款明细");
        }

        @Override
        public JSONResult<BusinessConsolePanelRespDTO> countCurMonthSignedNum(
                BusinessConsoleReqDTO businessConsoleReqDTO) {
            return fallBackError("查询签约数");
        }

        @Override
        public JSONResult<List<SignRecordRespDTO>> listSignRecordNoPage(SignRecordReqDTO reqDTO) {
            return fallBackError("查询待审批签约记录");
        }
    }

    

}

