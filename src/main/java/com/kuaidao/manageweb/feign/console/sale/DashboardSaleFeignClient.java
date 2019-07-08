package com.kuaidao.manageweb.feign.console.sale;

import com.kuaidao.aggregation.dto.clue.ClueRelateReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.dashboard.dto.bussale.BusSaleDTO;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 线索-所属组织
 * 
 * @author: yangbiao
 * @date: 2019年6月17日
 * @version V1.0
 */
@FeignClient(name = "dashboard-service", path = "/dashboard/bussale", fallback = DashboardSaleFeignClient.HystrixClientFallback.class)
public interface DashboardSaleFeignClient {

    /**
     * 看板数据查询-商务经理
     */
    public JSONResult<BusSaleDTO> findDataByUserId(IdEntityLong idEntityLong);


    @Component
    class HystrixClientFallback implements DashboardSaleFeignClient {

        private static Logger logger = LoggerFactory.getLogger(DashboardSaleFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult findDataByUserId(IdEntityLong idEntityLong) {
            return fallBackError("商务经理："+idEntityLong.getId());
        }

    }



}
