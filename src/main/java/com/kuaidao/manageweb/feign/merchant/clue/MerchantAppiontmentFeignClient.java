package com.kuaidao.manageweb.feign.merchant.clue;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.merchant.dto.clue.ClueAssignReqDto;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;
import com.kuaidao.merchant.dto.clue.MerchantAppiontmentDTO;
import com.kuaidao.merchant.dto.clue.MerchantAppiontmentReq;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsParamDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created on: 2019-10-03-20:06
 */
@FeignClient(name = "merchant-service", path = "/merchant/clue/MerchantAppiontment", fallback = MerchantAppiontmentFeignClient.HystrixClientFallback.class)
public interface MerchantAppiontmentFeignClient {

    /**
     * 查询邀约来访记录列表
     *
     * @param req
     * @return
     */
    @PostMapping("/queryPage")
    JSONResult<PageBean<MerchantAppiontmentDTO>> queryPage(@RequestBody MerchantAppiontmentReq req);

    /**
     * 新增邀约来访记录
     *
     * @param dto
     * @return
     */
    @PostMapping("/saveMerchantAppiontment")
    JSONResult<Boolean> saveMerchantAppiontment(@RequestBody MerchantAppiontmentDTO dto);

    @Component
    static class HystrixClientFallback implements MerchantAppiontmentFeignClient {

        private static Logger logger = LoggerFactory.getLogger(MerchantAppiontmentFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<MerchantAppiontmentDTO>> queryPage(MerchantAppiontmentReq req) {
            return fallBackError("查询邀约来访记录列表");
        }

        @Override
        public JSONResult<Boolean> saveMerchantAppiontment(MerchantAppiontmentDTO dto) {
            return fallBackError("新增邀约来访记录");
        }
    }

}
