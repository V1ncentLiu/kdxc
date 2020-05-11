package com.kuaidao.manageweb.feign.statistics.callrecord;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.teleGroupResourceEfficiency.TeleGroupResourceEfficiencyAllDto;
import com.kuaidao.stastics.dto.teleGroupResourceEfficiency.TeleGroupResourceEfficiencyQueryDto;

/**
 * 电销组资源接通有效率表
 * @author  Devin.Chen
 * @date 2019-06-24 14:42:09
 * @version V1.0
 */
@FeignClient(name = "statstics-service", path = "/statstics/callrecord/clueConnectValidRate", fallback = ClueConnectValidRateFeignClient.HystrixClientFallback.class)
public interface ClueConnectValidRateFeignClient {
    /**
     *  非首日 资源有效
    * @return
     */
    @PostMapping("/nonFirstClueValidList")
    public JSONResult<Map<String,Object>> nonFirstClueValidList(@RequestBody TeleGroupResourceEfficiencyQueryDto queryDto);
    
    /**
     * 首日 资源有效
    * @return
     */
    @PostMapping("/firstClueValidList")
    public JSONResult<Map<String,Object>> firstClueValidList(@RequestBody TeleGroupResourceEfficiencyQueryDto queryDto);
  
    
    /**
     * 查询全部数据
     */
    @PostMapping("/getAllClueValidList")
    JSONResult<List<TeleGroupResourceEfficiencyAllDto>> getAllClueValidList(TeleGroupResourceEfficiencyQueryDto queryDto);
    

    @Component
    class HystrixClientFallback implements ClueConnectValidRateFeignClient {
        private static Logger logger = LoggerFactory.getLogger(TeleTalkTimeFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> nonFirstClueValidList(TeleGroupResourceEfficiencyQueryDto queryDto) {
            return fallBackError("电销组资源接通有效率表-非首日有效list");
        }

        @Override
        public JSONResult<Map<String, Object>> firstClueValidList(TeleGroupResourceEfficiencyQueryDto queryDto) {
            return fallBackError("电销组资源接通有效率表-首日有效list");
        }

        @Override
        public JSONResult<List<TeleGroupResourceEfficiencyAllDto>> getAllClueValidList(TeleGroupResourceEfficiencyQueryDto queryDto) {
            return fallBackError("电销组资源接通有效率表-导出查询");
        }

        
    }

}
