package com.kuaidao.manageweb.feign.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.dashboard.dto.bussale.BusSaleDTO;
import com.kuaidao.dashboard.dto.tele.DashboardTeleGroupDto;

/**
 * 看板计数
 *
 * @author: fanjd
 * @date: 2019年7月10日
 * @version V1.0
 */
@FeignClient(name = "dashboard-service", path = "/dashboard/dashboardTeleGroup", fallback = DashboardTeleGroupFeignClient.HystrixClientFallback.class)
public interface DashboardTeleGroupFeignClient {


    /**
     * 看板数据查询-电销总监
     */
    @PostMapping("/findTeleGroupDataByOrgId")
    public JSONResult<DashboardTeleGroupDto> findTeleGroupDataByOrgId(@RequestBody IdEntityLong idEntityLong);

    @Component
    class HystrixClientFallback implements DashboardTeleGroupFeignClient {

        private static Logger logger = LoggerFactory.getLogger(DashboardTeleGroupFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult findTeleGroupDataByOrgId(IdEntityLong idEntityLong) {
            return fallBackError("电销总监：" + idEntityLong.getId()+"获取看板");
        }
    }



}
