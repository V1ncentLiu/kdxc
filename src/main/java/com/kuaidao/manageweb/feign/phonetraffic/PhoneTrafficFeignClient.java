package com.kuaidao.manageweb.feign.phonetraffic;

import com.kuaidao.aggregation.dto.paydetail.PayDetailInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailReqDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailRespDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficParamDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "aggregation-service-ooo1", path = "/aggregation/phonetraffic", fallback = PhoneTrafficFeignClient.HystrixClientFallback.class)
public interface PhoneTrafficFeignClient {

	@PostMapping("/queryPage")
	@ResponseBody
	public JSONResult<PageBean<PhoneTrafficRespDTO>> queryList(@RequestBody PhoneTrafficParamDTO dto);

	@Component
	static class HystrixClientFallback implements PhoneTrafficFeignClient {
		private static Logger logger = LoggerFactory.getLogger(PhoneTrafficFeignClient.class);

		@SuppressWarnings("rawtypes")
		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<PageBean<PhoneTrafficRespDTO>> queryList(PhoneTrafficParamDTO dto) {
			return fallBackError("话务管理");
		}

	}
}
