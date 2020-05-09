package com.kuaidao.manageweb.feign.announcement;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveInsertAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 这里
 * 功能描述:
 *      消息中心-业务中心
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service",path="/sys/busReceive",fallback = BusReceiveFeignClient.HystrixClientFallback.class)
public interface BusReceiveFeignClient {

    @PostMapping("/queryBussReceive")
    public JSONResult<PageBean<BussReceiveRespDTO>> queryReceive(@RequestBody BussReceiveQueryDTO queryDTO) ;

    @PostMapping("/updateState")
    public JSONResult updateReceive(@RequestBody IdEntity idEntity);

    @PostMapping("/updateStates")
    public JSONResult updateReceives(@RequestBody String ids);

    @PostMapping("/queryBussReceiveOne")
    public JSONResult<BussReceiveRespDTO> queryReceiveOne(@RequestBody IdEntity idEntity) ;

    @PostMapping("/unreadCount")
    public JSONResult unreadCount(@RequestBody Map map);

    @PostMapping("/insertAndSent")
    public JSONResult insertAndSent(@RequestBody BussReceiveInsertAndUpdateDTO rev)
            ;
    
    @PostMapping("/queryBussReceiveNoPage")
    public JSONResult<List<BussReceiveRespDTO>> queryBussReceiveNoPage(
            BussReceiveQueryDTO queryDTO);
    
    @Component
    static class HystrixClientFallback implements BusReceiveFeignClient {


        private static Logger logger = LoggerFactory.getLogger(BusReceiveFeignClient.HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<BussReceiveRespDTO>> queryReceive(BussReceiveQueryDTO queryDTO) {
            return fallBackError("业务消息记录获取失败");
        }

        @Override
        public JSONResult updateReceive(IdEntity idEntity) {
            return fallBackError("业务消息记录，状态更新失败");
        }

        @Override
        public JSONResult updateReceives(String ids) {
            return fallBackError("业务消息记录，批量状态更新失败");
        }

        @Override
        public JSONResult<BussReceiveRespDTO> queryReceiveOne(IdEntity idEntity) {
            return fallBackError("业务消息记录，详细信息获取失败");
        }

        @Override
        public JSONResult unreadCount(Map map) {
            return fallBackError("业务消息记录，未读消息数量获取失败");
        }

        @Override
        public JSONResult insertAndSent(BussReceiveInsertAndUpdateDTO rev) {
            return fallBackError("业务消息记录，信息发送");
        }

        @Override
        public JSONResult<List<BussReceiveRespDTO>> queryBussReceiveNoPage(
                BussReceiveQueryDTO queryDTO) {
            return fallBackError("查询消息");
        }

    }


}
