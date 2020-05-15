package com.kuaidao.manageweb.feign.console.sale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.dashboard.dto.tele.DashboardTeleSaleDto;

/**
 * 线索-所属组织
 * 
 * @author: yangbiao
 * @date: 2019年6月17日
 * @version V1.0
 */
@FeignClient(name = "dashboard-service", path = "/dashboard/dashboardTeleSale", fallback = DashboardTeleSaleFeignClient.HystrixClientFallback.class)
public interface DashboardTeleSaleFeignClient {

    /**
     * 看板数据查询-电销顾问
     */
	@PostMapping("/findDashboardTeleSaleByUserId")
    public JSONResult<DashboardTeleSaleDto> findDashboardTeleSaleByUserId(IdEntityLong idEntityLong);


    @Component
    class HystrixClientFallback implements DashboardTeleSaleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(DashboardTeleSaleFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult findDashboardTeleSaleByUserId(IdEntityLong idEntityLong) {
            return fallBackError("电销顾问："+idEntityLong.getId());
        }

    }



}
