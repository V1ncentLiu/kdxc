package com.kuaidao.manageweb.feign.clue;

import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationClueDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationCluePageParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 待分配新资源
 * 
 * @author: zhangxingyu
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/clueCustomer",
        fallback = ClueCustomerFeignClient.HystrixClientFallback.class)
public interface ClueCustomerFeignClient {


    /**
     * 通过ClueIds获取客户姓名
     * @param idListLongReq
     * @return
     */
    @RequestMapping("/findcustomersByClueIds")
    public JSONResult<List<CustomerClueDTO>> findcustomersByClueIds(@RequestBody IdListLongReq idListLongReq);



    /**
     * 通过ClueId获取客户姓名
     * @param idEntity
     * @return
     */
    @PostMapping("/findNameById")
    public JSONResult<CustomerClueDTO> findNameById(@RequestBody IdEntityLong idEntity);

    @Component
    static class HystrixClientFallback implements ClueCustomerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueCustomerFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<List<CustomerClueDTO>> findcustomersByClueIds(IdListLongReq idListLongReq) {
            return fallBackError("通过ClueIds获取客户姓名");
        }

        @Override
        public JSONResult<CustomerClueDTO> findNameById(IdEntityLong idEntity) {
            return fallBackError("通过ClueId获取客户姓名");
        }
    }


}
