package com.kuaidao.manageweb.controller.call;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;

@Controller
@RequestMapping("/call/callRecord")
public class CallRecordController {
    
    @Autowired
    CallRecordFeign callRecordFeign;
    
    
    /**
     * 我的通话记录
     * @return
     */
    @RequestMapping("/myCallRecord")
    public String myCallRecord() {
        return "call/myCallRecord";
    }

    
    /**
     * 获取我的通话记录 分页展示 ，参数模糊匹配
     * @return
     */
    @PostMapping("/listMyCallRecord")
    @ResponseBody
    public JSONResult<Map<String,Object>> listMyCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO) {
       return callRecordFeign.listMyCallRecord(myCallRecordReqDTO);
    }
    
    /**
     * 电销通话记录  分页展示 ，参数模糊匹配
     * @return
     */
    @PostMapping("/listAllTmCallRecord")
    @ResponseBody
    public JSONResult<Map<String,Object>> listAllTmCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO) {
       return callRecordFeign.listAllTmCallRecord(myCallRecordReqDTO);
    }
    
    
    /***
     * 电销通话时长统计 分页
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/listAllTmCallTalkTime")
    @ResponseBody
    public JSONResult<Map<String,Object>> listAllTmCallTalkTime(@RequestBody CallRecordReqDTO myCallRecordReqDTO){
        return callRecordFeign.listAllTmCallTalkTime(myCallRecordReqDTO);
    }
}
