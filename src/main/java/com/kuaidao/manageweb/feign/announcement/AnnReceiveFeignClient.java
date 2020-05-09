package com.kuaidao.manageweb.feign.announcement;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


/**
 *
 * 功能描述: 
 *      消息中心-系统公告
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service",path="/sys/annoReceive",fallback = AnnReceiveFeignClient.HystrixClientFallback.class)
public interface AnnReceiveFeignClient {

    @PostMapping("/queryAnnReceive")
    public JSONResult<PageBean<AnnReceiveRespDTO>> queryReceive(@RequestBody AnnReceiveQueryDTO queryDTO);

    @PostMapping("/updateState")
    public JSONResult updateReceive(@RequestBody IdEntity idEntity);

    @PostMapping("/queryAnnReceives")
    public JSONResult updateReceives(@RequestBody String ids);

    @PostMapping("/queryAnnReceiveOne")
    JSONResult<AnnReceiveRespDTO> queryReceiveOne(IdEntity idEntity);

    @PostMapping("/unreadCount")
    public JSONResult annUnreadCount(@RequestBody Map map);

    @PostMapping("/batchInsert")
    public JSONResult batchInsert(@RequestBody List<AnnReceiveAddAndUpdateDTO> list);
    
    @PostMapping("/queryAnnReceiveNoPage")
    public JSONResult<List<AnnReceiveRespDTO>> queryAnnReceiveNoPage(AnnReceiveQueryDTO queryDTO);

    @Component
    static class HystrixClientFallback implements AnnReceiveFeignClient {

        private static Logger logger = LoggerFactory.getLogger(AnnReceiveFeignClient.HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<AnnReceiveRespDTO>> queryReceive(AnnReceiveQueryDTO queryDTO) {
            return fallBackError("公告记录获取失败");
        }

        @Override
        public JSONResult updateReceive(IdEntity idEntity) {
            return fallBackError("公告记录，更新状态失败");
        }

        @Override
        public JSONResult updateReceives(String ids) {
            return fallBackError("公告记录，批量更新状态事变");
        }

        @Override
        public JSONResult<AnnReceiveRespDTO> queryReceiveOne(IdEntity idEntity) {
            return fallBackError("公告记录，详细信息获取失败");
        }

        @Override
        public JSONResult annUnreadCount(Map map) {
            return fallBackError("公告记录，未读信息数量获取失败");
        }

        @Override
        public JSONResult batchInsert(List<AnnReceiveAddAndUpdateDTO> list) {
            return fallBackError("公告记录：站内通知-批量插入失败");
        }

        @Override
        public JSONResult<List<AnnReceiveRespDTO>> queryAnnReceiveNoPage(
                AnnReceiveQueryDTO queryDTO) {
            return fallBackError("查询消息");
        }
    }


}
