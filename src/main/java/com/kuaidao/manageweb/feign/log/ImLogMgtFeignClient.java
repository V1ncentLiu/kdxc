package com.kuaidao.manageweb.feign.log;

import com.kuaidao.aggregation.dto.log.ImLogsDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


/**
 * 聊天记录
 */

@FeignClient(name = "aggregation-service", fallback = ImLogMgtFeignClient.HystrixClientFallback.class)
public interface ImLogMgtFeignClient {
	@RequestMapping(method = RequestMethod.POST, value = "/aggregation/imLog/queryIMLogRecord")
	public JSONResult<List<ImLogsDTO>> queryIMLogRecord(@RequestBody ImLogsDTO logReqDTO);

	/**
	 * 根据资源id查询聊天记录
	 * @param logReqDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/aggregation/imLog/getImLogByClueId")
	public JSONResult<ImLogsDTO> getImLogByClueId(@RequestBody ImLogsDTO logReqDTO);

	
	@Component
	static class HystrixClientFallback implements ImLogMgtFeignClient {
		private static final Logger logger = LoggerFactory.getLogger(ImLogMgtFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<List<ImLogsDTO>> queryIMLogRecord(ImLogsDTO logReqDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询聊天记录失败");
		}

		@Override
		public JSONResult<ImLogsDTO> getImLogByClueId(ImLogsDTO logReqDTO) {
			return fallBackError("根据clueId查询聊天记录失败");
		}
	}

}
