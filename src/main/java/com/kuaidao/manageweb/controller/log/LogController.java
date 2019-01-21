package com.kuaidao.manageweb.controller.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.log.LogMgtFeignClient;

/**
 * 组织机构类
 * 
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午3:04:53
 * @version V1.0
 */
@Controller
@RequestMapping("/log/log")
public class LogController {

    private static Logger logger = LoggerFactory.getLogger(LogController.class);
    @Autowired
    LogMgtFeignClient logMgtFeignClient;

    /**
     * 访问日志
     * 
     * @return
     */
    @RequestMapping("/visitLog")
    public String visitLog(HttpServletRequest request) {

        return "log/logPage";
    }

    /**
     * 操作日志
     * 
     * @return
     */
    @RequestMapping("/operationLog")
    public String operationLog(HttpServletRequest request) {
        return "log/operationLog";
    }

    /* *//***
          * 自定义字段 首页
          * 
          * @return
          */
    @RequestMapping("/queryLogDataList")
    @ResponseBody
    public JSONResult<PageBean<AccessLogReqDTO>> queryLogDataList(
            @RequestBody AccessLogReqDTO logReqDTO, HttpServletRequest request,
            HttpServletResponse response) {
        return logMgtFeignClient.queryLogRecord(logReqDTO);
    }
    
  public static void main(String[] args) {
	
}

}
