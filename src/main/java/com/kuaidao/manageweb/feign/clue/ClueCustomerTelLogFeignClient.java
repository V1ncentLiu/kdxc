package com.kuaidao.manageweb.feign.clue;

import com.kuaidao.aggregation.dto.clue.ClueCustomerTelLogDto;
import com.kuaidao.aggregation.dto.clue.ClueCustomerTelLogReqDto;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 待分配新资源
 * 
 * @author: fanjd
 * @date: 2020年6月18日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/clueCustomerTelLog",
        fallback = ClueCustomerTelLogFeignClient.HystrixClientFallback.class)
public interface ClueCustomerTelLogFeignClient {


    /**
     * 新建手机号查询列表
     * @param
     * @return
     */
    @RequestMapping("/queryPageList")
    JSONResult<PageBean<ClueCustomerTelLogDto>> queryPageList(@RequestBody ClueCustomerTelLogReqDto reqDTO);

    @Component
    static class HystrixClientFallback implements ClueCustomerTelLogFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueCustomerTelLogFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<ClueCustomerTelLogDto>> queryPageList( ClueCustomerTelLogReqDto reqDTO) {
            return fallBackError("新建手机号查询列表");
        }
    }


}
