package com.kuaidao.manageweb.feign.statistics.unpaidFunds;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.unpaidFunds.UnpaidFundsQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "statstics-service", path = "/statstics/unpaidFunds", fallback = UnpaidFundsFeignClient.HystrixClientFallback.class)
public interface UnpaidFundsFeignClient {
    /**
     * 商务总监一级页面查询 分页
     */
    @PostMapping("/getDirectorOnePageList")
    JSONResult<Map<String,Object>> getDirectorOnePageList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto);
    /**
     * 商务总监一级页面查询
     */
    @PostMapping("/getDirectorOneList")
    JSONResult<Map<String,Object>> getDirectorOneList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto);
    /**
     * 商务总监二级页面查询 分页
     */
    @PostMapping("/getDirectorTwoPageList")
    JSONResult<Map<String,Object>> getDirectorTwoPageList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto);
    /**
     * 商务总监二级页面查询
     */
    @PostMapping("/getDirectorTwoList")
    JSONResult<Map<String,Object>> getDirectorTwoList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto);
    /**
     * 商务经理一级页面查询 分页
     */
    @PostMapping("/getManagerOnePageList")
    JSONResult<Map<String,Object>> getManagerOnePageList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto);
    /**
     * 商务经理一级页面查询
     */
    @PostMapping("/getManagerOneList")
    JSONResult<Map<String,Object>> getManagerOneList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto);

    @Component
    class HystrixClientFallback implements UnpaidFundsFeignClient {

        private static Logger logger = LoggerFactory.getLogger(UnpaidFundsFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> getDirectorOnePageList(UnpaidFundsQueryDto unpaidFundsQueryDto) {
            return fallBackError("商务总监一级页面查询分页");
        }

        @Override
        public JSONResult<Map<String, Object>> getDirectorOneList(UnpaidFundsQueryDto unpaidFundsQueryDto) {
            return fallBackError("商务总监一级页面查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getDirectorTwoPageList(UnpaidFundsQueryDto unpaidFundsQueryDto) {
            return fallBackError("商务总监二级页面查询分页");
        }

        @Override
        public JSONResult<Map<String, Object>> getDirectorTwoList(UnpaidFundsQueryDto unpaidFundsQueryDto) {
            return fallBackError("商务总监二级页面查询");
        }

        @Override
        public JSONResult<Map<String, Object>> getManagerOnePageList(UnpaidFundsQueryDto unpaidFundsQueryDto) {
            return fallBackError("商务经理一级页面查询分页");
        }

        @Override
        public JSONResult<Map<String, Object>> getManagerOneList(UnpaidFundsQueryDto unpaidFundsQueryDto) {
            return fallBackError("商务经理一级页面查询");
        }
    }

}
