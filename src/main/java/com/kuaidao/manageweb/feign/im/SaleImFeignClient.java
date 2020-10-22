package com.kuaidao.manageweb.feign.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.custservice.dto.saleim.SaleImDTO;
import com.kuaidao.custservice.dto.saleim.SaleImPageParam;
import com.kuaidao.custservice.dto.saleim.SaleImReq;

/**
 * 顾问im授权
 * @author zxy
 * @date 2020年8月31日
 * @version V1.0
 */
@FeignClient(name = "cust-service-service", path = "/custservice/saleIm", fallback = SaleImFeignClient.HystrixClientFallback.class)
public interface SaleImFeignClient {
    /**
     * 保存顾问im授权
     * @param saleImReq
     * @return
     */
    @PostMapping(value = "/save")
    JSONResult<Long> save(@RequestBody SaleImReq saleImReq);

    /**
     * 解绑im授权
     * @param saleImReq
     * @return
     */
    @PostMapping(value = "/untie")
    JSONResult<Void> untie(@RequestBody SaleImReq saleImReq);

    /**
     * 根据顾问id查询授权信息
     * @param idEntity
     * @return
     */
    @PostMapping(value = "/getByTeleSaleId")
    JSONResult<SaleImDTO> getByTeleSaleId(@RequestBody IdEntityLong idEntity);

    /**
     * 查询顾问im授权列表
     * @param pageParam
     * @return
     */
    @PostMapping(value = "/list")
    JSONResult<PageBean<SaleImDTO>> list(@RequestBody SaleImPageParam pageParam);

    @Component
    static class HystrixClientFallback implements SaleImFeignClient {

        private static Logger logger = LoggerFactory.getLogger(SaleImFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Long> save(@RequestBody SaleImReq saleImReq) {
            return fallBackError("保存顾问im授权");
        }

        @Override
        public JSONResult<Void> untie(@RequestBody SaleImReq saleImReq) {
            return fallBackError("解绑im授权");
        }

        @Override
        public JSONResult<SaleImDTO> getByTeleSaleId(@RequestBody IdEntityLong idEntity) {
            return fallBackError("根据顾问id查询授权信息");
        }

        @Override
        public JSONResult<PageBean<SaleImDTO>> list(@RequestBody SaleImPageParam pageParam) {
            return fallBackError("查询顾问im授权列表");
        }
    }

}
