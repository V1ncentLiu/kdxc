package com.kuaidao.manageweb.feign.clue;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.clue.BusAllocationClueReq;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationDTO;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationPageParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 待分配新资源
 * 
 * @author: zhangxingyu
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/pendingVisit",
        fallback = PendingVisitFeignClient.HystrixClientFallback.class)
public interface PendingVisitFeignClient {


    /**
     * 查询待分配来访客户列表
     *
     * @param commentInsertReq
     * @return
     */
    @PostMapping("/pendingVisitList")
    public JSONResult<PageBean<BusPendingAllocationDTO>> pendingVisitList(
            @RequestBody BusPendingAllocationPageParam pageParam);

    /**
     * 分配资源
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/busAllocationClue")
    public JSONResult<String> busAllocationClue(
            @RequestBody BusAllocationClueReq busAllocationClueReq);
    
    

    /**
     * 商务总监控制台 待分配邀约来访记录 
     * @param pageParam
     * @return
     */
    @PostMapping("/pendingVisitListNoPage")
    public JSONResult<List<BusPendingAllocationDTO>> pendingVisitListNoPage(
            BusPendingAllocationPageParam pageParam);


    @Component
    static class HystrixClientFallback implements PendingVisitFeignClient {

        private static Logger logger = LoggerFactory.getLogger(PendingVisitFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<BusPendingAllocationDTO>> pendingVisitList(
                @RequestBody BusPendingAllocationPageParam pageParam) {
            return fallBackError("查询待分配来访客户列表");
        }

        @Override
        public JSONResult<String> busAllocationClue(
                @RequestBody BusAllocationClueReq busAllocationClueReq) {
            return fallBackError("分配资源");
        }


        @Override
        public JSONResult<List<BusPendingAllocationDTO>> pendingVisitListNoPage(
                BusPendingAllocationPageParam pageParam) {
            return fallBackError("查询待分配来访客户列表");
        }

    }



}
