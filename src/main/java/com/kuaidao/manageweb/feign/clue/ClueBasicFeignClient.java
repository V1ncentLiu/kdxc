package com.kuaidao.manageweb.feign.clue;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.clue.PendingAllocationClueDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationCluePageParam;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
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
@FeignClient(name = "aggregation-service", path = "/aggregation/clueBasic",
        fallback = ClueBasicFeignClient.HystrixClientFallback.class)
public interface ClueBasicFeignClient {


    /**
     * 查询待分配资源列表
     *
     * @param commentInsertReq
     * @return
     */
    @PostMapping("/pendingAllocationList")
    public JSONResult<PageBean<PendingAllocationClueDTO>> pendingAllocationList(
            @RequestBody PendingAllocationCluePageParam pageParam);

    /**
     * 分配资源
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/allocationClue")
    public JSONResult<String> allocationClue(@RequestBody AllocationClueReq allocationClueReq);

    /**
     * 转移资源
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/transferClue")
    public JSONResult<String> transferClue(@RequestBody AllocationClueReq allocationClueReq);

    /**
     * 批量分发
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/batchDistributionClue")
    public JSONResult<String> batchDistributionClue(
            @RequestBody AllocationClueReq allocationClueReq);

    /**
     * 副总分发
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/ceoDistributionClue")
    public JSONResult<String> ceoDistributionClue(@RequestBody AllocationClueReq allocationClueReq);

    /**
     * 通过CusName模糊查询获取ClueIds
     * 
     * @param cusName
     * @return
     */
    @PostMapping("/queryClueIdsByCusName")
    public JSONResult<List<Long>> queryClueIdsByCusName(@RequestBody String cusName);
    
    
    /**
     * 统计分配的资源数
     * @param reqDTO
     * @return
     */
    @PostMapping("/countAssignClueNum")
    public JSONResult<Integer> countAssignClueNum(@RequestBody TeleConsoleReqDTO reqDTO);
    
    
    /**
     * 电销总监 查询待分配资源
     * @param pageParam
     * @return
     */
    @PostMapping("/listUnAssignClue")
    public JSONResult<PageBean<PendingAllocationClueDTO>> listUnAssignClue(
            PendingAllocationCluePageParam pageParam);

    /**
     * 根据资源id查询是否有邀请函
     * @return
     */
    @PostMapping("/getIsInviteLetterById")
    public JSONResult<Integer> getIsInviteLetterById(@RequestBody Long clueId);

    @Component
    static class HystrixClientFallback implements ClueBasicFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueBasicFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @PostMapping("/pendingAllocationList")
        @Override
        public JSONResult<PageBean<PendingAllocationClueDTO>> pendingAllocationList(
                @RequestBody PendingAllocationCluePageParam pageParam) {
            return fallBackError("查询待分配资源列表");
        }

        @Override
        public JSONResult<String> allocationClue(@RequestBody AllocationClueReq allocationClueReq) {
            return fallBackError("分配资源");
        }

        @Override
        public JSONResult<String> transferClue(@RequestBody AllocationClueReq allocationClueReq) {
            return fallBackError("转移资源");
        }

        @Override
        public JSONResult<String> batchDistributionClue(
                @RequestBody AllocationClueReq allocationClueReq) {
            return fallBackError("批量分发");
        }

        @Override
        public JSONResult<String> ceoDistributionClue(
                @RequestBody AllocationClueReq allocationClueReq) {
            return fallBackError("副总分发");
        }

        @Override
        public JSONResult<List<Long>> queryClueIdsByCusName(String cusName) {
            return fallBackError("通过客户姓名模糊查询获取clueIds");
        }


        @Override
        public JSONResult<Integer> countAssignClueNum(TeleConsoleReqDTO reqDTO) {
            return fallBackError("统计分配资源数");
        }


        @Override
        public JSONResult<PageBean<PendingAllocationClueDTO>> listUnAssignClue(
                PendingAllocationCluePageParam pageParam) {
            return fallBackError("查询待分配资源");
        }

        @Override
        public JSONResult<Integer> getIsInviteLetterById(Long clueId) {
            return fallBackError("查询是否有邀请函");
        }


    }

   


}
