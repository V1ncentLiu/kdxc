package com.kuaidao.manageweb.feign.im;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.clue.IMSubmitQueryDTO;
import com.kuaidao.aggregation.dto.es.EsQueryDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.custservice.dto.custservice.BrandAndIsSubmitReq;
import com.kuaidao.custservice.dto.custservice.CustomerInfoDTO;
import com.kuaidao.custservice.dto.onlineleave.SaleMonitorDTO;
import com.kuaidao.custservice.dto.onlineleave.SaleOnlineLeaveLogReq;
import com.kuaidao.custservice.dto.onlineleave.TSaleMonitorReq;

/**
 * 退返款
 * 
 * @author Chen
 * @date 2019年4月10日 下午7:35:14
 * @version V1.0
 */
@FeignClient(name = "cust-service-service", path = "/custservice/customerInfo",
        fallback = CustomerInfoFeignClient.HystrixClientFallback.class)
public interface CustomerInfoFeignClient {

    @PostMapping(value = "/brandAndIssubmit")
    JSONResult<List<CustomerInfoDTO>> brandAndIssubmit(
            @RequestBody BrandAndIsSubmitReq brandAndIsSubmitReq);

    @PostMapping(value = "/onlineleave")
    JSONResult<Boolean> onlineleave(SaleOnlineLeaveLogReq saleOnlineLeaveLogReq);

    @PostMapping(value = "/getSaleMonitor")
    JSONResult<PageBean<SaleMonitorDTO>> getSaleMonitor(TSaleMonitorReq tSaleMonitorReq);

    @PostMapping(value = "/getSaleImStateNum")
    JSONResult<List<Map<String, Object>>> getSaleImStateNum(Map<String, Object> map);

    @PostMapping(value = "/costomerList")
    JSONResult<PageBean<IMSubmitQueryDTO>> costomerList(@RequestBody EsQueryDTO submitQuery);

    @PostMapping(value = "/getCustomerInfoListByClueId")
    JSONResult<List<CustomerInfoDTO>> getCustomerInfoListByClueId(
            @RequestBody Map<String, Object> map);

    @PostMapping(value = "/findCustomerByImID")
    JSONResult<CustomerInfoDTO> findCustomerByImID(@RequestBody IdEntity idEntity);

    @Component
    static class HystrixClientFallback implements CustomerInfoFeignClient {

        private static Logger logger = LoggerFactory.getLogger(CustomerInfoFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<CustomerInfoDTO>> brandAndIssubmit(
                BrandAndIsSubmitReq brandAndIsSubmitReq) {
            return fallBackError("品牌信息以及是否提交接口");
        }

        @Override
        public JSONResult<Boolean> onlineleave(SaleOnlineLeaveLogReq saleOnlineLeaveLogReq) {
            return fallBackError("在线离线日志");
        }

        @Override
        public JSONResult<PageBean<SaleMonitorDTO>> getSaleMonitor(
                TSaleMonitorReq tSaleMonitorReq) {
            return fallBackError("顾问监控量查询");
        }

        @Override
        public JSONResult<List<Map<String, Object>>> getSaleImStateNum(Map<String, Object> map) {
            return fallBackError("顾问在线离线忙碌状态数量");
        }

        @Override
        public JSONResult<PageBean<IMSubmitQueryDTO>> costomerList(EsQueryDTO submitQuery) {
            return fallBackError("右侧客户列表");
        }

        @Override
        public JSONResult<List<CustomerInfoDTO>> getCustomerInfoListByClueId(
                Map<String, Object> map) {
            return fallBackError("根据客户Id获得客户Im信息集合");
        }

        @Override
        public JSONResult<CustomerInfoDTO> findCustomerByImID(IdEntity idEntity) {
            return fallBackError("根据Imid获取客户ID");
        }

    }


}
