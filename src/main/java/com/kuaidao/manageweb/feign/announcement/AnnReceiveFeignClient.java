package com.kuaidao.manageweb.feign.announcement;

import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 *
 * 功能描述: 
 *      消息中心-系统公告
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service",path="/sys/busBussReceive",fallback = AnnReceiveFeignClient.HystrixClientFallback.class)
public interface AnnReceiveFeignClient {

    @PostMapping("/queryAnnReceive")
    public JSONResult<List<AnnReceiveRespDTO>> queryReceive(@RequestBody AnnReceiveQueryDTO queryDTO);

    @PostMapping("/updateState")
    public JSONResult updateReceive(@RequestBody IdEntity idEntity);

    @PostMapping("/queryAnnReceives")
    public JSONResult updateReceives(@RequestBody String ids);

    @Component
    static class HystrixClientFallback implements AnnReceiveFeignClient {

        @Override
        public JSONResult<List<AnnReceiveRespDTO>> queryReceive(AnnReceiveQueryDTO queryDTO) {
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
