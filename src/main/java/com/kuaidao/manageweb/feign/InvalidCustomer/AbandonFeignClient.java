package com.kuaidao.manageweb.feign.InvalidCustomer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.invalidcustomer.AbandonParamDTO;
import com.kuaidao.aggregation.dto.invalidcustomer.AbandonRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

import java.util.List;

/**
 *
 * 功能描述: 无效客户资源
 * 
 * @auther yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service-wyp", path = "/aggregation/abandon",
        fallback = AbandonFeignClient.HystrixClientFallback.class)
public interface AbandonFeignClient {

    @PostMapping("/queryPage")
    public JSONResult<PageBean<AbandonRespDTO>> queryListPage(@RequestBody AbandonParamDTO dto);
    @PostMapping("/queryListExport")
    public JSONResult<List<AbandonRespDTO>> queryListExport(@RequestBody AbandonParamDTO dto);
    @PostMapping("/findAbandonCluesCount")
    public JSONResult<Long> findAbandonCluesCount(@RequestBody AbandonParamDTO dto);

    @Component
    static class HystrixClientFallback implements AbandonFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<AbandonRespDTO>> queryListPage(
                @RequestBody AbandonParamDTO dto) {
            return fallBackError("废弃池分页查询");
        }

        @Override
        public JSONResult<List<AbandonRespDTO>> queryListExport(@RequestBody AbandonParamDTO dto) {
            return fallBackError("废弃池分页查询");
        }

        @Override
        public JSONResult<Long> findAbandonCluesCount(AbandonParamDTO dto) {
            return fallBackError("废弃池分页查询导出数量");
        }


    }
}
