package com.kuaidao.manageweb.feign.paydetail;

import com.kuaidao.aggregation.dto.paydetail.PayDetailInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailReqDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailRespDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "aggregation-service", path = "/aggregation/paydetail", fallback = PayDetailFeignClient.HystrixClientFallback.class)
public interface PayDetailFeignClient {


	@RequestMapping("/queryList")
	public JSONResult<List<PayDetailRespDTO>> queryList(@RequestBody PayDetailReqDTO dto);
	@RequestMapping("/insert")
	public JSONResult<Boolean> savePayDedail(@RequestBody PayDetailInsertOrUpdateDTO dto);

	@Component
	static class HystrixClientFallback implements PayDetailFeignClient {
		private static Logger logger = LoggerFactory.getLogger(PayDetailFeignClient.class);

		@SuppressWarnings("rawtypes")
		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<List<PayDetailRespDTO>> queryList(PayDetailReqDTO dto) {
			return fallBackError("付款详情");
		}

		@Override
		public JSONResult<Boolean> savePayDedail(PayDetailInsertOrUpdateDTO dto) {
			return fallBackError("新增付款明细");
		}
	}
}
