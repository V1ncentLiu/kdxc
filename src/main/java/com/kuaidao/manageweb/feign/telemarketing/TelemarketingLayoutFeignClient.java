package com.kuaidao.manageweb.feign.telemarketing;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

@FeignClient(name = "aggregation-service", path = "/aggregation/telemarketinglayout",
        fallback = TelemarketingLayoutFeignClient.HystrixClientFallback.class)

public interface TelemarketingLayoutFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/getTelemarketingLayoutList")
    public JSONResult<PageBean<TelemarketingLayoutDTO>> getTelemarketingLayoutList(
            @RequestBody TelemarketingLayoutDTO queryDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/findTelemarketingById")
    public JSONResult<TelemarketingLayoutDTO> findTelemarketingById(
            @RequestBody TelemarketingLayoutDTO queryDTO);


    @RequestMapping(method = RequestMethod.POST, value = "/deleTelemarketingLayout")
    public JSONResult deleTelemarketingLayout(@RequestBody TelemarketingLayoutDTO queryDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/addOrUpdateTelemarketingLayout")
    public JSONResult addOrUpdateTelemarketingLayout(@RequestBody TelemarketingLayoutDTO queryDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/addTelemarketingLayoutList")
    public JSONResult addTelemarketingLayoutList(
            @RequestBody List<TelemarketingLayoutDTO> queryDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/getTelemarketingLayoutByTeamId")
    public JSONResult<TelemarketingLayoutDTO> getTelemarketingLayoutByTeamId(
            @RequestBody TelemarketingLayoutDTO queryDTO);


    @Component
    static class HystrixClientFallback implements TelemarketingLayoutFeignClient {

        private static Logger logger =
                LoggerFactory.getLogger(TelemarketingLayoutFeignClient.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<TelemarketingLayoutDTO>> getTelemarketingLayoutList(
                TelemarketingLayoutDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("查询电销布局列表失败");
        }

        @Override
        public JSONResult deleTelemarketingLayout(TelemarketingLayoutDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("删除电销布局列表失败");
        }

        @Override
        public JSONResult addOrUpdateTelemarketingLayout(TelemarketingLayoutDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("添加电销布局失败");
        }

        @Override
        public JSONResult addTelemarketingLayoutList(List<TelemarketingLayoutDTO> queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("批量插入电销布局失败");
        }

        @Override
        public JSONResult<TelemarketingLayoutDTO> getTelemarketingLayoutByTeamId(
                TelemarketingLayoutDTO queryDTO) {
            return fallBackError("查询电销布局失败");
        }

        @Override
        public JSONResult<TelemarketingLayoutDTO> findTelemarketingById(
                TelemarketingLayoutDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("根据id查询电销布局失败");
        }
    }
}
