package com.kuaidao.manageweb.feign.busmycustomer;

import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerRespDTO;
import com.kuaidao.aggregation.dto.busmycustomer.MyCustomerParamDTO;
import com.kuaidao.aggregation.dto.clue.ClueBasicDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *  商务 - 我的客户
 * 
 * @author yangbiao
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/busmycustomer",
        fallback = BusMyCustomerFeignClient.HystrixClientFallback.class)
public interface BusMyCustomerFeignClient {

    @PostMapping("/queryPageList")
    public JSONResult<PageBean<BusMyCustomerRespDTO>> queryPageList(@RequestBody MyCustomerParamDTO param);

    @PostMapping("/queryList")
    public JSONResult<List<BusMyCustomerRespDTO>> queryList(@RequestBody MyCustomerParamDTO param);

    @PostMapping("/notVisit")
    public JSONResult<Boolean> notVisit(@RequestBody BusMyCustomerReqDTO param);

    @PostMapping("/notVisitReason")
    public JSONResult<ClueBasicDTO> notVisitReason(@RequestBody IdEntityLong idEntityLong);

    @RequestMapping(method = RequestMethod.POST, value = "/createCustomerClue")
    JSONResult<Boolean> createCustomerClue(ClueDTO dto);
    /**
     * 商务经理控制台  待处理邀约来访记录
     * @param param
     * @return
     */
    @PostMapping("/listPendingInviteCustomer")
    public JSONResult<PageBean<BusMyCustomerRespDTO>> listPendingInviteCustomer(@RequestBody MyCustomerParamDTO param);

    @Component
    static class HystrixClientFallback implements BusMyCustomerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(BusMyCustomerFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<BusMyCustomerRespDTO>> queryPageList(MyCustomerParamDTO param) {
            return fallBackError("商务：我的客户列表分页查询");
        }

        @Override
        public JSONResult<List<BusMyCustomerRespDTO>> queryList(MyCustomerParamDTO param) {
            return fallBackError("商务：我的客户列表不分页查询");
        }

        @Override
        public JSONResult<Boolean> notVisit(BusMyCustomerReqDTO param) {
            return fallBackError("标记未到访");
        }

        @Override
        public JSONResult<ClueBasicDTO> notVisitReason(IdEntityLong idEntityLong) {
            return fallBackError("查看未到访原因");
        }

        @Override
        public JSONResult<Boolean> createCustomerClue(ClueDTO dto) {
            return fallBackError("商务-创建资源");
        }

        @Override
        public JSONResult<PageBean<BusMyCustomerRespDTO>> listPendingInviteCustomer(
                MyCustomerParamDTO param) {
            return fallBackError("商务经理控制台-待处理邀约来访记录");
        }
    }


}
