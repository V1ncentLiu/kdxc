package com.kuaidao.manageweb.feign.merchant.clue;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.merchant.dto.clue.ClueApplyAuditReqDto;
import com.kuaidao.merchant.dto.clue.ClueApplyPageDto;
import com.kuaidao.merchant.dto.clue.ClueApplyPageParamDto;
import com.kuaidao.merchant.dto.clue.ClueApplyReqDto;
import com.kuaidao.merchant.dto.clue.MerchantClueApplyDto;

/**
 * 资源
 * 
 * @author: fanjd
 * @date: 2019年09月06日
 * @version V1.0
 */
@FeignClient(name = "merchant-service", path = "/merchant/merchant/clue/setting", fallback = MerchantClueApplyFeignClient.HystrixClientFallback.class)
public interface MerchantClueApplyFeignClient {
    @PostMapping("/save")
    JSONResult<Boolean> save(@Valid @RequestBody ClueApplyReqDto reqDto);

    @PostMapping("/applyPage")
    JSONResult<PageBean<ClueApplyPageDto>> applyPage(@RequestBody ClueApplyPageParamDto reqDto);

    @PostMapping("/getPendingReview")
    JSONResult<Integer> getPendingReview(@RequestBody ClueApplyPageParamDto reqDto);

    @PostMapping("/pass")
    JSONResult<Boolean> pass(@RequestBody ClueApplyAuditReqDto reqDto);

    @PostMapping("/reject")
    JSONResult<Boolean> reject(@RequestBody ClueApplyAuditReqDto reqDto);

    @PostMapping("/getPassByUserId")
    JSONResult<MerchantClueApplyDto> getPassByUserId(@RequestBody IdEntityLong userId);

    @PostMapping("/getByUserId")
    JSONResult<MerchantClueApplyDto> getByUserId(@RequestBody IdEntityLong userId);

    @Component
    static class HystrixClientFallback implements MerchantClueApplyFeignClient {

        private static Logger logger = LoggerFactory.getLogger(MerchantClueApplyFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<Boolean> save(ClueApplyReqDto reqDto) {
            return fallBackError("资源需求申请保存");
        }

        @Override
        public JSONResult<PageBean<ClueApplyPageDto>> applyPage(ClueApplyPageParamDto reqDto) {
            return fallBackError("资源需求申请列表");
        }

        @Override
        public JSONResult<Integer> getPendingReview(ClueApplyPageParamDto reqDto) {
            return fallBackError("资源需求申请列表-待审核数");
        }

        @Override
        public JSONResult<Boolean> pass(@RequestBody ClueApplyAuditReqDto reqDto) {
            return fallBackError("资源需求审核通过");
        }

        @Override
        public JSONResult<Boolean> reject(@RequestBody ClueApplyAuditReqDto reqDto) {
            return fallBackError("资源需求审核驳回");
        }

        @Override
        public JSONResult<MerchantClueApplyDto> getPassByUserId(@RequestBody IdEntityLong userId) {
            return fallBackError("查询商家最新审批过的申请");
        }
        @Override
        public JSONResult<MerchantClueApplyDto> getByUserId(@RequestBody IdEntityLong userId) {
            return fallBackError("查询商家最新申请");
        }
    }


}
