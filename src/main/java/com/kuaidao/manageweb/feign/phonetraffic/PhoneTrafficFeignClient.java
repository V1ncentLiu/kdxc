package com.kuaidao.manageweb.feign.phonetraffic;

import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficParamDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficRespDTO;
import com.kuaidao.aggregation.dto.phonetraffic.TrafficParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "aggregation-service", path = "/aggregation/phonetraffic", fallback = PhoneTrafficFeignClient.HystrixClientFallback.class)
public interface PhoneTrafficFeignClient {

	@PostMapping("/queryPage")
	public JSONResult<PageBean<PhoneTrafficRespDTO>> queryList(@RequestBody PhoneTrafficParamDTO dto);

	@PostMapping("/allocationClue")
    JSONResult allocationClue(AllocationClueReq allocationClueReq);

	@PostMapping("/transferClue")
	JSONResult transferClue(AllocationClueReq allocationClueReq);

	@PostMapping("/toTele")
	public JSONResult toTele(@RequestBody ClueDTO clueDTO);

	@PostMapping("/releaseClue")
	public JSONResult releaseClue(TrafficParam trafficParam);

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
			return fallBackError("话务管理分页查询");
		}

		@Override
		public JSONResult allocationClue(AllocationClueReq allocationClueReq) {
			return fallBackError("话务管理-资源分配");
		}

		@Override
		public JSONResult transferClue(AllocationClueReq allocationClueReq) {
			return fallBackError("话务管理-资源转移");
		}

		@Override
		public JSONResult toTele(ClueDTO clueDTO) {
			return fallBackError("话务管理-转电销");
		}

		@Override
		public JSONResult releaseClue(TrafficParam trafficParam) {
				return fallBackError("话务管理-释放资源（废弃池/共有池）");
		}

	}
}
