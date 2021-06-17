package com.kuaidao.manageweb.feign.merchant.clue;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.merchant.dto.clue.ClueInfoDto;
import com.kuaidao.merchant.dto.clue.ClueInfoReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@FeignClient(name = "merchant-service", path = "/merchant/merchantClue",
        fallback = MerchantClueInfoFeignClient.HystrixClientFallback.class)
public interface MerchantClueInfoFeignClient {

    /**
     * 我的客户查询数据
     * 
     * @param req
     * @return
     */

    @RequestMapping(method = RequestMethod.POST, value = "/info/findPageList")
    JSONResult<PageBean<ClueInfoDto>> findPageList(ClueInfoReq req);
    /**
     * 更新状态
     *
     * @param req
     * @return
     */

    @RequestMapping(method = RequestMethod.POST, value = "/info/updateStatus")
    JSONResult<String> updateStatus(ClueInfoReq req);
    /**
     * 我的客户查询数据
     *
     * @param req
     * @return
     */

    @RequestMapping(method = RequestMethod.POST, value = "/info/findList")
    JSONResult<List<ClueInfoDto>> findList(ClueInfoReq req);

    /**
     * 获取扣费状态
     * @param req
     * @return
     */
    @PostMapping("/getClueInfoCharge")
    JSONResult<ClueInfoReq> getClueInfoCharge(@RequestBody ClueInfoReq req );

    /**
     * 扣费
     * @param req
     * @return
     */
    @PostMapping("/clueInfoDeduction")
    JSONResult<Void> clueInfoDeduction(@RequestBody ClueInfoReq req);

    @PostMapping("/add")
    public JSONResult<ClueInfoReq> add(@RequestBody ClueInfoReq merchantClueInfoReq );

    @PostMapping("/deleteByAccountId")
    public JSONResult<Boolean> deleteByAccountId(@RequestBody IdEntityLong idEntityLong );

    @PostMapping("/initBalanceStatus")
    public JSONResult<Boolean> initBalanceStatus();

    @PostMapping("/getUserBalanceStatus")
    public JSONResult<Map<Long,Integer>> getUserBalanceStatus(@RequestBody IdListLongReq idListLongReq);


    @Component
    static class HystrixClientFallback implements MerchantClueInfoFeignClient {

        private static Logger logger = LoggerFactory.getLogger(MerchantClueInfoFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<ClueInfoDto>> findPageList(ClueInfoReq queryDTO) {
            return fallBackError("分页查询客户数据失败");
        }

        @Override
        public JSONResult<String> updateStatus(ClueInfoReq queryDTO) {
            return fallBackError("更新客户跟踪状态数据失败");
        }

        @Override
        public JSONResult<List<ClueInfoDto>> findList(ClueInfoReq queryDTO) {
            return fallBackError("查询客户管理数据失败");
        }

        @Override
        public JSONResult<ClueInfoReq   > getClueInfoCharge(ClueInfoReq req) {
            return fallBackError("查询该客户扣费标准失败");
        }

        @Override
        public JSONResult<Void> clueInfoDeduction(ClueInfoReq req) {
            return fallBackError("扣费失败");
        }

        @Override
        public JSONResult<ClueInfoReq> add(ClueInfoReq merchantClueInfoReq) {
            return fallBackError("新增失败");
        }

        @Override
        public JSONResult<Boolean> deleteByAccountId(IdEntityLong idEntityLong) {
            return fallBackError("根据商户删除客户信息失败");
        }

        @Override
        public JSONResult<Boolean> initBalanceStatus() {
            return fallBackError("初始化商户状态");
        }

        @Override
        public JSONResult<Map<Long, Integer>> getUserBalanceStatus(IdListLongReq idListLongReq) {
            return fallBackError("获取商户状态");
        }
    }


}
