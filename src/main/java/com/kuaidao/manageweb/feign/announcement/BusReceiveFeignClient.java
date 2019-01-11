package com.kuaidao.manageweb.feign.announcement;

import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveRespDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 这里
 * 功能描述:
 *      消息中心-业务中心
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service",path="/sys/annReceive",fallback = BusReceiveFeignClient.HystrixClientFallback.class)
public interface BusReceiveFeignClient {

    @PostMapping("/queryBussReceive")
    public JSONResult<List<BussReceiveRespDTO>> queryReceive(@RequestBody BussReceiveQueryDTO queryDTO) ;

    @PostMapping("/updateState")
    public JSONResult updateReceive(@RequestBody IdEntity idEntity);

    @PostMapping("/queryBussReceives")
    public JSONResult updateReceives(@RequestBody String ids);

    @Component
    static class HystrixClientFallback implements BusReceiveFeignClient {
        @Override
        public JSONResult<List<BussReceiveRespDTO>> queryReceive(BussReceiveQueryDTO queryDTO) {
            return null;
        }

        @Override
        public JSONResult updateReceive(IdEntity idEntity) {
            return null;
        }

        @Override
        public JSONResult updateReceives(String ids) {
            return null;
        }
    }
}
