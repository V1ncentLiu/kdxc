package com.kuaidao.manageweb.feign.clue;

import com.kuaidao.aggregation.dto.clue.BusArrangeDTO;
import com.kuaidao.aggregation.dto.clue.BusArrangeParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 预约来访
 * 
 * @author: zhangxingyu
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/arrange", fallback = BusArrangeFeignClient.HystrixClientFallback.class)
public interface BusArrangeFeignClient {
	/**
	 * 商务排班表列表
	 * @return
	 */
	@PostMapping("/arrangeList")
	public JSONResult<PageBean<BusArrangeDTO>> arrangeList(@RequestBody BusArrangeParam param);

	@PostMapping("/exportBusArrangeList")
	public JSONResult<List<BusArrangeDTO>> exportBusArrangeList(@RequestBody BusArrangeParam param);
	@Component
	static class HystrixClientFallback implements BusArrangeFeignClient {

		private static Logger logger = LoggerFactory.getLogger(BusArrangeFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}
		@Override
		public JSONResult<PageBean<BusArrangeDTO>> arrangeList(@RequestBody BusArrangeParam param) {
			return fallBackError("查询商务排班表集合");
		}
		@Override
		public JSONResult<List<BusArrangeDTO>> exportBusArrangeList(@RequestBody BusArrangeParam param) {
			return fallBackError("导出商务排班表集合");
		}
	}


}
