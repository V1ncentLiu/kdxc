package com.kuaidao.manageweb.feign.financing;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import com.kuaidao.aggregation.dto.paydetail.PayDetailAccountDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;

/**
 *  退返款 
 * @author  Chen
 * @date 2019年4月10日 下午7:35:14   
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/financing/balanceaccount", fallback = BalanceAccountApplyClient.HystrixClientFallback.class)
public interface BalanceAccountApplyClient {
    /**
     * 查询付款明细
     * @param queryDTO
     * @return
     */
    @PostMapping("/getPayDetailById")
    JSONResult<PayDetailAccountDTO> getPayDetailById(PayDetailAccountDTO queryDTO);

   
    @Component
    static class HystrixClientFallback implements BalanceAccountApplyClient {

        private static Logger logger = LoggerFactory.getLogger(BalanceAccountApplyClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

		@Override
		public JSONResult<PayDetailAccountDTO> getPayDetailById(PayDetailAccountDTO queryDTO) {
			return fallBackError("根据Id查询对账申请详情");
		}
    }

}
