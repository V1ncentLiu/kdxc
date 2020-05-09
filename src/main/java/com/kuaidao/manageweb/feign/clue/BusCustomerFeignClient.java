package com.kuaidao.manageweb.feign.clue;

import com.kuaidao.aggregation.dto.clue.BusVisitPerDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.clue.BusCustomerDTO;
import com.kuaidao.aggregation.dto.clue.BusCustomerPageParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 待分配新资源
 * 
 * @author: zhangxingyu
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service-ooo1", path = "/aggregation/busCustomer",
        fallback = BusCustomerFeignClient.HystrixClientFallback.class)
public interface BusCustomerFeignClient {


    /**
     * 查询商务客户管理列表
     *
     * @return
     */
    @PostMapping("/busCustomerList")
    public JSONResult<PageBean<BusCustomerDTO>> busCustomerList(
            @RequestBody BusCustomerPageParam pageParam);

    @PostMapping("/exportVisitPer")
    public  JSONResult<List<BusVisitPerDTO>> exportVisitPer(
        @RequestBody BusCustomerPageParam pageParam);


    @Component
    static class HystrixClientFallback implements BusCustomerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(BusCustomerFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<BusCustomerDTO>> busCustomerList(
                @RequestBody BusCustomerPageParam pageParam) {
            return fallBackError("查询商务客户管理列表");
        }

        @Override
        public JSONResult<List<BusVisitPerDTO>> exportVisitPer(BusCustomerPageParam pageParam) {
            return fallBackError("导出到访业绩");
        }


    }


}
