package com.kuaidao.manageweb.feign.statistics;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.stastics.dto.callrecord.TeleSaleTalkTimeQueryDTO;
import com.kuaidao.stastics.dto.callrecord.TeleTalkTimeRespDTO;

@FeignClient(name = "statstics-service-chen", path = "/statstics/callrecord/teleSaleTalkTime", fallback = TeleTalkTimeFeignClient.HystrixClientFallback.class)
public interface TeleTalkTimeFeignClient {

    /***
     * 昨日 七天 电销组通话时长
    * @param teleSaleTalkTimeQueryDTO
    * @return
     */
    @PostMapping("/listTeleGroupTalkTime")
    JSONResult<PageBean<TeleTalkTimeRespDTO>> listTeleGroupTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO);
    
    /**
     *  昨日 七天 电销组通话时长
    * @param teleSaleTalkTimeQueryDTO
    * @return
     */
    @PostMapping("/listTeleGroupTalkTimeNoPage")
    public JSONResult<Map<String,Object>> listTeleGroupTalkTimeNoPage(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO);
    

    /**
     * 昨日 七天 电销顾问 通话时长表
    * @param teleSaleTalkTimeQueryDTO
    * @return
     */
    @PostMapping("/listTeleSaleTalkTimeNoPage")
    public JSONResult<List<TeleTalkTimeRespDTO>> listTeleSaleTalkTimeNoPage(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO);
    
    
    /**
     * 昨日 七天 电销顾问 通话时长表
    * @param teleSaleTalkTimeQueryDTO
    * @return
     */
    @RequestMapping("/listTeleSaleTalkTime")
    public JSONResult<PageBean<TeleTalkTimeRespDTO>> listTeleSaleTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO);
    
    /**
     * 电销组 合计
    * @param teleSaleTalkTimeQueryDTO
    * @return
     */
    @PostMapping("/totalTeleGroupTalkTime")
    public JSONResult<TeleTalkTimeRespDTO> totalTeleGroupTalkTime(TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO);
    
    @Component
    class HystrixClientFallback implements TeleTalkTimeFeignClient {

        private static Logger logger = LoggerFactory.getLogger(TeleTalkTimeFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<TeleTalkTimeRespDTO>> listTeleGroupTalkTime(
                TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
            return fallBackError("昨日-七天-电销组通话时长");
        }

        @Override
        public JSONResult<Map<String,Object>> listTeleGroupTalkTimeNoPage(
                TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
            return fallBackError("昨日-七天-电销组通话时长不分页");
        }

        @Override
        public JSONResult<List<TeleTalkTimeRespDTO>> listTeleSaleTalkTimeNoPage(
                TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
            return fallBackError("昨日 七天 销顾问 通话时长表");
        }

        @Override
        public JSONResult<PageBean<TeleTalkTimeRespDTO>> listTeleSaleTalkTime(
                TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
            return fallBackError("昨日-七天电销顾问 通话时长表");
        }

        @Override
        public JSONResult<TeleTalkTimeRespDTO> totalTeleGroupTalkTime(
                TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
            return fallBackError("电销组通话时长合计");
        }

    }

}
