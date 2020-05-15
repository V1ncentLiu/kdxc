package com.kuaidao.manageweb.feign.merchant.seatmanager;

import com.kuaidao.callcenter.dto.seatmanager.SeatInsertOrUpdateDTO;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerReq;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerResp;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created on: 2019-09-23-17:39
 */
@FeignClient(name = "callcenter-service", path = "/callcenter/seatManager",
    fallback = SeatManagerFeignClient.HystrixClientFallback.class)
public interface SeatManagerFeignClient {

    @PostMapping("/create")
    public JSONResult<Boolean> create(@RequestBody SeatInsertOrUpdateDTO insertOrUpdateDTO);

    @PostMapping("/update")
    public JSONResult<Boolean> update( @RequestBody SeatInsertOrUpdateDTO insertOrUpdateDTO);

    @PostMapping("/delete")
    public JSONResult<Boolean> delete( @RequestBody IdListLongReq idList);

    @PostMapping("/findOne")
    public JSONResult<SeatManagerResp> findOne( @RequestBody IdEntityLong idEntityLong);

    @PostMapping("/queryList")
    public JSONResult<PageBean<SeatManagerResp>> queryList( @RequestBody SeatManagerReq seatManagerReq);

    @PostMapping("/queryListNoPage")
    public JSONResult<List<SeatManagerResp>> queryListNoPage( @RequestBody SeatManagerReq seatManagerReq);

    @PostMapping("/queryListBySubMerchant")
    public JSONResult<SeatManagerResp> queryListBySubMerchant( @RequestBody SeatManagerReq seatManagerReq);

    @PostMapping("/countSeatNumOnMerchent")
    public JSONResult<List<SeatManagerResp>> countSeatNumOnMerchent();

    @Component
    static class HystrixClientFallback implements SeatManagerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(
            SeatManagerFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> create(SeatInsertOrUpdateDTO insertOrUpdateDTO) {
            return fallBackError("坐席管理-创建");
        }

        @Override
        public JSONResult<Boolean> update(SeatInsertOrUpdateDTO insertOrUpdateDTO) {
            return fallBackError("坐席管理-更新");
        }

        @Override
        public JSONResult<Boolean> delete(IdListLongReq idList) {
            return fallBackError("坐席管理-删除");
        }

        @Override
        public JSONResult<SeatManagerResp> findOne(IdEntityLong idEntityLong) {
            return fallBackError("坐席管理-findOne");
        }

        @Override
        public JSONResult<PageBean<SeatManagerResp>> queryList(SeatManagerReq seatManagerReq) {
            return fallBackError("坐席管理-列表查询（分页）");
        }

        @Override
        public JSONResult<List<SeatManagerResp>> queryListNoPage(SeatManagerReq seatManagerReq) {
            return fallBackError("坐席管理-列表查询（不分页）");
        }

        @Override
        public JSONResult<SeatManagerResp> queryListBySubMerchant(SeatManagerReq seatManagerReq) {
            return fallBackError("通过绑定商家子账号查询");
        }

        @Override
        public JSONResult<List<SeatManagerResp>> countSeatNumOnMerchent() {
            return fallBackError("绑定坐席数量");
        }
    }

}
