package com.kuaidao.manageweb.feign.paydetail;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.aggregation.dto.paydetail.PayDetailInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailReqDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailRespDTO;
import com.kuaidao.aggregation.dto.sign.BusSignInsertOrUpdateDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

@FeignClient(name = "aggregation-service", path = "/aggregation/paydetail",
        fallback = PayDetailFeignClient.HystrixClientFallback.class)
public interface PayDetailFeignClient {


    @RequestMapping("/queryList")
    JSONResult<List<PayDetailRespDTO>> queryList(@RequestBody PayDetailReqDTO dto);

    @RequestMapping("/list")
    JSONResult<PageBean<PayDetailListDTO>> list(@RequestBody PayDetailPageParam req);

    @RequestMapping("/insert")
    JSONResult<Boolean> savePayDedail(@RequestBody PayDetailInsertOrUpdateDTO dto);

    @RequestMapping("/payOrSignUpdate")
    JSONResult<Boolean> payOrSignUpdate(@RequestBody BusSignInsertOrUpdateDTO dto);

    @Component
    static class HystrixClientFallback implements PayDetailFeignClient {
        private static Logger logger = LoggerFactory.getLogger(PayDetailFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<PayDetailRespDTO>> queryList(PayDetailReqDTO dto) {
            return fallBackError("付款详情");
        }

        @Override
        public JSONResult<PageBean<PayDetailListDTO>> list(@RequestBody PayDetailPageParam req) {
            return fallBackError("付款信息列表");
        }

        @Override
        public JSONResult<Boolean> savePayDedail(PayDetailInsertOrUpdateDTO dto) {
            return fallBackError("新增付款明细");
        }

        @Override
        public JSONResult<Boolean> payOrSignUpdate(BusSignInsertOrUpdateDTO dto) {
            return fallBackError("修改付款明细和签约单");
        }
    }
}
