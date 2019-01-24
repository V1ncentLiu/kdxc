package com.kuaidao.manageweb.feign.area;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.aggregation.dto.assignrule.InfoAssignDTO;
import com.kuaidao.aggregation.dto.assignrule.InfoAssignQueryDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.area.SysRegionDTO;

@FeignClient(name = "sys-service", path = "/area/sysregion", fallback = SysRegionFeignClient.HystrixClientFallback.class)
public interface SysRegionFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "/getproviceList")
	JSONResult<List<SysRegionDTO>> getproviceList();

	

	@Component
	static class HystrixClientFallback implements SysRegionFeignClient {

		private static Logger logger = LoggerFactory.getLogger(SysRegionFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}


		@Override
		public JSONResult<List<SysRegionDTO>> getproviceList() {
			// TODO Auto-generated method stub
			return fallBackError("查询省份列表数据失败");
		}

	

	}
}
