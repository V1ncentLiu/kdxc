package com.kuaidao.manageweb.controller.log;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.log.LogMgtFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import lombok.val;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

/**
 * 组织机构类
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
     * @return
     */
    @RequestMapping("/visitLog")
    @LogRecord(description="登录",operationType=OperationType.LOGIN,menuName=MenuEnum.LOGIN)
    public String visitLog(HttpServletRequest request) {
        
        return "log/logPage";
    }
    /**
     *操作日志
     * @return
     */
    @RequestMapping("/operationLog")
    @LogRecord(description="退出",operationType=OperationType.INSERT,menuName=MenuEnum.ACCOUNT_MANAGEMENT)
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
	public JSONResult<PageBean<AccessLogReqDTO>> queryLogDataList(@RequestBody AccessLogReqDTO logReqDTO, HttpServletRequest request,
			HttpServletResponse response) {
		return logMgtFeignClient.queryLogRecord(logReqDTO);
	}

}
