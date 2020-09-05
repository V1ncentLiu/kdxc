package com.kuaidao.manageweb.feign.salecard;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.custservice.dto.language.CommonLanguagePageReqDTO;
import com.kuaidao.custservice.dto.language.CommonLanguageReqDto;
import com.kuaidao.custservice.dto.language.CommonLanguageRespDto;
import com.kuaidao.custservice.dto.salecard.SaleCardReqDto;
import com.kuaidao.custservice.dto.salecard.SaleCardRespDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 自动提交模块服务调用Feign类
 *
 * @author fengyixuan
 */
@FeignClient(name = "cust-service-service", path="/custservice/saleCard",fallback = SaleCardFeignClient.HystrixClientFallback.class)
public interface SaleCardFeignClient {


    /**
     * 根据类型查询  根据顾问ID查询
     *
     * @param saleCardReqDto
     * @return
     */
    @PostMapping(value = "/queryById")
     JSONResult<SaleCardRespDto> queryById(@RequestBody SaleCardReqDto saleCardReqDto);

    /**
     * 新增修改顾问名片
     * @param saleCardReqDto
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    JSONResult<Boolean> saveOrUpdate(@RequestBody SaleCardReqDto saleCardReqDto) ;

    @Component
    static class HystrixClientFallback implements SaleCardFeignClient {

        private static final Logger logger = LoggerFactory.getLogger(SaleCardFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<SaleCardRespDto> queryById(SaleCardReqDto saleCardReqDto) {
            return fallBackError("根据顾问ID查询名片");
        }

        @Override
        public JSONResult<Boolean> saveOrUpdate(SaleCardReqDto saleCardReqDto) {
            return fallBackError("新增修改顾问名片");
        }
    }

}
