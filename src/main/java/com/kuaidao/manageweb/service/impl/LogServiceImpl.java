package com.kuaidao.manageweb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.feign.log.LogMgtFeignClient;
import com.kuaidao.manageweb.service.LogService;


@Service
public class LogServiceImpl  implements LogService {
	@Autowired
	LogMgtFeignClient logMgtFeignClient;

	@Async
	@Override
	public JSONResult insertLogRecord(AccessLogReqDTO logReqDTO) {
		return logMgtFeignClient.insertLogRecord(logReqDTO);
	}

}
