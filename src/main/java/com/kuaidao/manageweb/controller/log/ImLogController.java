package com.kuaidao.manageweb.controller.log;

import com.kuaidao.aggregation.dto.log.ImLogsDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.feign.log.ImLogMgtFeignClient;
import com.kuaidao.manageweb.feign.log.LogMgtFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 组织机构类
 * 
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午3:04:53
 * @version V1.0
 */
@Controller
@RequestMapping("/log/imLog")
public class ImLogController {

    private static Logger logger = LoggerFactory.getLogger(ImLogController.class);
    @Autowired
    ImLogMgtFeignClient imLogMgtFeignClient;


    /**
     *
     * @param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/queryIMLogRecord")
    @ResponseBody
    public JSONResult<PageBean<ImLogsDTO>> queryIMLogRecord(
            @RequestBody ImLogsDTO imLogDto, HttpServletRequest request,
            HttpServletResponse response) {
        JSONResult<PageBean<ImLogsDTO>> imLogs = imLogMgtFeignClient.queryIMLogRecord(imLogDto);
        return imLogs;
    }
    /**
     *根据clueId查询是否有聊天记录
     * @param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getImLogByClueId")
    @ResponseBody
    public boolean getImLogByClueId(
            @RequestBody ImLogsDTO imLogDto, HttpServletRequest request,
            HttpServletResponse response) {
        JSONResult<ImLogsDTO> imLogs = imLogMgtFeignClient.getImLogByClueId(imLogDto);
        boolean isShowImButton = false;
        if(imLogs != null && JSONResult.SUCCESS.equals(imLogs.getCode())) {
            ImLogsDTO ImLogsDTO = imLogs.getData();
            if(ImLogsDTO !=null && ImLogsDTO.getId() !=null){
                isShowImButton = true;
            }
        }
        return isShowImButton;
    }
}
