package com.kuaidao.manageweb.feign.call;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.alibaba.fastjson.JSONObject;
import com.kuaidao.aggregation.dto.call.CallRecordCountDTO;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.call.QueryPhoneLocaleDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;


@FeignClient(name = "aggregation-service-wyp", path = "/aggregation/call/callRecord", fallback = CallRecordFeign.HystrixClientFallback.class)
public interface CallRecordFeign {
    
    /**
     *   获取我的通话记录 分页展示 ，参数模糊匹配
     * @return
     */
    @PostMapping("/listMyCallRecord")
    JSONResult<Map<String,Object>> listMyCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
    
    /**
     * 电销通话记录  分页展示 ，参数模糊匹配
     * @return
     */
    @PostMapping("/listAllTmCallRecord")
    JSONResult<Map<String,Object>> listAllTmCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO);

    /**
     *话务通话记录  分页展示 ，参数模糊匹配
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/listHwAllTmCallRecord")
    JSONResult<Map<String,Object>> listHwAllTmCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
    /***
     * 电销通话时长统计 分页
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/listAllTmCallTalkTime")
    JSONResult<Map<String,Object>> listAllTmCallTalkTime(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
    
    /**
     * 根据clueId 或 customerPhone 查询 通话记录
     * @param myCallRecordReqDTO 参数 clueid 或 customerPhone 
     * @return
     */
    @PostMapping("/listTmCallReacordByParams")
    JSONResult<PageBean<CallRecordRespDTO>> listTmCallReacordByParams(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
   
    /**
     * 根据clueId 或 customerPhone 查询 通话记录
     * @param myCallRecordReqDTO 参数 clueid 或 customerPhone 
     * @return
     */
    @PostMapping("/listTmCallReacordByParamsNoPage")
    JSONResult<List<CallRecordRespDTO>> listTmCallReacordByParamsNoPage(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
  
    
    /**
     *  获取天润通话记录地址 根据 记录Id
     * @param reqDTO
     * @return
     */
    @PostMapping("/getRecordFile")
    JSONResult<String> getRecordFile(@RequestBody IdEntity idEntity);
    
    /**
     * 根据clueId List 分组统计 拨打次数
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/countCallRecordTotalByClueIdList")
    JSONResult<List<CallRecordCountDTO>> countCallRecordTotalByClueIdList(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
    
    
    /***
     * 
     * @param teleConsoleReqDTO
     * @return
     */
    @PostMapping("/countTodayTalkTime")
    JSONResult<Integer> countTodayTalkTime(TeleConsoleReqDTO teleConsoleReqDTO);
    
    /**
     * 我的通话记录 统计总时长
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/countMyCallRecordTalkTime")
    JSONResult<Integer> countMyCallRecordTalkTime(CallRecordReqDTO myCallRecordReqDTO);

    /**
     * 记录拨打时间
     * @param myCallRecordReqDTO
     */
    @PostMapping("/recodeCallTime")
    JSONResult<Boolean> recodeCallTime(@RequestBody CallRecordReqDTO myCallRecordReqDTO);
    
    /**
     * 查询手机号归属地
    * @param queryPhoneLocaleDTO
    * @return
     */
    @PostMapping("/queryPhoneLocale")
    JSONResult<JSONObject> queryPhoneLocale(QueryPhoneLocaleDTO queryPhoneLocaleDTO);


    @Component
    static class HystrixClientFallback implements CallRecordFeign {
        
        private static Logger logger = LoggerFactory.getLogger(CallRecordFeign.class);
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }
        @Override
        public JSONResult<Map<String, Object>> listMyCallRecord(
                CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("我的通话记录");
        }
        @Override
        public JSONResult<Map<String, Object>> listAllTmCallRecord(
                CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("电销通话记录");
        }
        @Override
        public JSONResult<Map<String, Object>> listHwAllTmCallRecord(
            CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("话务通话记录");
        }
        @Override
        public JSONResult<Map<String, Object>> listAllTmCallTalkTime(
                CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("电销通话时长");
        }
        @Override
        public JSONResult<PageBean<CallRecordRespDTO>> listTmCallReacordByParams(CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("查询通话记录");
        }
        @Override
        public JSONResult<List<CallRecordRespDTO>> listTmCallReacordByParamsNoPage(
                CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("根据资源ID或手机号查询通话记录-不分页");
        }
        @Override
        public JSONResult<String> getRecordFile(IdEntity idEntity) {
            return fallBackError("获取录音文件地址");
        }
        @Override
        public JSONResult<List<CallRecordCountDTO>> countCallRecordTotalByClueIdList(
                CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("根据资源Id统计拨打次数");
        }
        @Override
        public JSONResult<Integer> countTodayTalkTime(TeleConsoleReqDTO teleConsoleReqDTO) {
            return fallBackError("通话时长");
        }
        @Override
        public JSONResult<Integer> countMyCallRecordTalkTime(CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("我的通话记录-总时长");
        }

        @Override
        public JSONResult<Boolean> recodeCallTime(CallRecordReqDTO myCallRecordReqDTO) {
            return fallBackError("记录拨打时间");
        }
        @Override
        public JSONResult<JSONObject> queryPhoneLocale(QueryPhoneLocaleDTO queryPhoneLocaleDTO) {
            return fallBackError("查询手机号归属地");
        }

    }

}
