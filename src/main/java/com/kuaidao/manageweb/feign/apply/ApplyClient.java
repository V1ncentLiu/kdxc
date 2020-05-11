package com.kuaidao.manageweb.feign.apply;

import com.kuaidao.aggregation.dto.apply.TeleCooperateApplyDTO;
import com.kuaidao.aggregation.dto.clue.BusAllocationClueReq;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationDTO;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationPageParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 待分配新资源
 * 
 * @author: zhangxingyu
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/apply",
        fallback = ApplyClient.HystrixClientFallback.class)
public interface ApplyClient {


    /**
     * 查询合作申请
     *
     * @param
     * @return
     */
    @PostMapping("/findApplyList")
    public JSONResult<PageBean<TeleCooperateApplyDTO>> findApplyList(
            @RequestBody TeleCooperateApplyDTO pageParam);

    @PostMapping("/transferApply")
    public JSONResult<String> transferApply(@RequestBody TeleCooperateApplyDTO teleCooperateApplyDTO);

    /**
     * 导出合作申请
     * @param teleCooperateApplyDTO
     * @return
     */
    @PostMapping("/exportApply")
    public JSONResult<List<TeleCooperateApplyDTO>> exportApply(@RequestBody TeleCooperateApplyDTO teleCooperateApplyDTO);

    /**
     * 查询详情
     * @param teleCooperateApplyDTO
     * @return
     */
    @PostMapping("/getApplyDetail")
    public JSONResult<TeleCooperateApplyDTO> getApplyDetail(@RequestBody TeleCooperateApplyDTO teleCooperateApplyDTO);


    @Component
    static class HystrixClientFallback implements ApplyClient {

        private static Logger logger = LoggerFactory.getLogger(ApplyClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<TeleCooperateApplyDTO>> findApplyList(
                @RequestBody TeleCooperateApplyDTO pageParam) {
            return fallBackError("查询合作申请列表");
        }

        @Override
        public JSONResult<String> transferApply(TeleCooperateApplyDTO teleCooperateApplyDTO) {
            return fallBackError("转发合作申请给顾问");
        }

        @Override
        public JSONResult<List<TeleCooperateApplyDTO>> exportApply(TeleCooperateApplyDTO teleCooperateApplyDTO) {
            return fallBackError("导出合作申请");
        }

        @Override
        public JSONResult<TeleCooperateApplyDTO> getApplyDetail(TeleCooperateApplyDTO teleCooperateApplyDTO) {
            return fallBackError("查询合作申请详情");
        }


    }



}
