package com.kuaidao.manageweb.feign.log;

import com.kuaidao.aggregation.dto.log.ImLogsDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * 聊天记录
 */

@FeignClient(name = "log-service", fallback = ImLogMgtFeignClient.HystrixClientFallback.class)
public interface ImLogMgtFeignClient {
	@RequestMapping(method = RequestMethod.POST, value = "/log/imLog/queryIMLogRecord")
	public JSONResult<PageBean<ImLogsDTO>> queryIMLogRecord(@RequestBody ImLogsDTO logReqDTO);
	
	@Component
	static class HystrixClientFallback implements ImLogMgtFeignClient {
		private static final Logger logger = LoggerFactory.getLogger(ImLogMgtFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<PageBean<ImLogsDTO>> queryIMLogRecord(ImLogsDTO logReqDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询聊天记录失败");
		}
	}

}
