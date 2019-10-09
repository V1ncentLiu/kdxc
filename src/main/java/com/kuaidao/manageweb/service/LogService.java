package com.kuaidao.manageweb.service;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;

/**
 * 
 * @Description:       访问日志记录
 * @author: Chen Chengxue
 * @date:   2018年8月13日 上午10:25:15   
 * @version V1.0
 */
public interface LogService {
	
	JSONResult insertLogRecord(AccessLogReqDTO logReqDTO);

}
