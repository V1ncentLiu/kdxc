package com.kuaidao.manageweb.feign.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;


/**
 * 
 * @Description:    日志记录   
 * @author: Chen Chengxue
 * @date:   2018年8月13日 上午9:50:27   
 * @version V1.0
 */

@FeignClient(name = "log-service", fallback = LogMgtFeignClient.HystrixClientFallback.class)
public interface LogMgtFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "/log/logging/insertLogRecord")
	public JSONResult insertLogRecord(@RequestBody AccessLogReqDTO logReqDTO);
	
	@RequestMapping(method = RequestMethod.POST, value = "/log/logging/queryLogRecord")
	public JSONResult<PageBean<AccessLogReqDTO>> queryLogRecord(@RequestBody AccessLogReqDTO logReqDTO);
	
	@Component
	static class HystrixClientFallback implements LogMgtFeignClient {
		private static final Logger logger = LoggerFactory.getLogger(LogMgtFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult insertLogRecord(AccessLogReqDTO logReqDTO) {
			return fallBackError("插入访问日志");
		}

		@Override
		public JSONResult<PageBean<AccessLogReqDTO>> queryLogRecord(AccessLogReqDTO logReqDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询访问日志失败");
		}
	}

}
