package com.kuaidao.manageweb.feign.autoresponse;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.dto.autoresponse.AutoResponseReq;
import com.kuaidao.custservice.dto.autoresponse.AutoResponseReqDto;
import com.kuaidao.custservice.dto.autoresponse.AutoResponseRespDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 自动提交模块服务调用Feign类
 *
 * @author fengyixuan
 */
@FeignClient(name = "cust-service-service", path = "/custservice/autoReponse", fallback = AutoResponseFeignClient.HystrixClientFallback.class)
public interface AutoResponseFeignClient {

    /*
     * 根据自动提交类型查询提交信息 当type =0时默认查所有
     */
    @RequestMapping(method = RequestMethod.POST, value = "/queryListByType")
    JSONResult<List<AutoResponseRespDto>> queryListByType(@RequestBody AutoResponseReqDto autoResponseReq);

    /*
     * 根据ID查询自动提交信息
     */
    @RequestMapping(method = RequestMethod.POST, value = "/queryById")
    JSONResult<AutoResponseRespDto> queryById(@RequestBody AutoResponseReqDto autoResponseReq);


    /*
     * 更新自动提交信息
     */
    @RequestMapping(method = RequestMethod.POST, value = "/update")
    JSONResult<Boolean> update(@RequestBody AutoResponseReqDto autoResponseReq);


    /*
     *新增自动提交信息
     */
    @RequestMapping(method = RequestMethod.POST, value = "/insert")
    JSONResult<Boolean> insert(@RequestBody AutoResponseReqDto autoResponseReq);

    /*
     *删除自动提交信息
     */
    @RequestMapping(method = RequestMethod.POST, value = "/deleteById")
    JSONResult<Boolean> deleteById(@RequestBody AutoResponseReqDto autoResponseReq);

    /*
     *新增和修改自动提交信息
     */
    @RequestMapping(method = RequestMethod.POST, value = "/saveOrUpdate")
    JSONResult<Boolean> saveOrUpdate(AutoResponseReq autoResponseReq);


    @Component
    static class HystrixClientFallback implements AutoResponseFeignClient {

        private static final Logger logger = LoggerFactory.getLogger(AutoResponseFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<AutoResponseRespDto>> queryListByType(AutoResponseReqDto autoResponseReq) {
            return fallBackError("根据自动提交类型查询提交信息");
        }

        @Override
        public JSONResult<AutoResponseRespDto> queryById(AutoResponseReqDto autoResponseReq) {
            return fallBackError("根据ID查询自动提交信息");
        }

        @Override
        public JSONResult<Boolean> update(AutoResponseReqDto autoResponseReq) {
            return fallBackError("更新自动提交信息");
        }

        @Override
        public JSONResult<Boolean> insert(AutoResponseReqDto autoResponseReq) {
            return fallBackError("新增自动提交信息");
        }

        @Override
        public JSONResult<Boolean> deleteById(AutoResponseReqDto autoResponseReq) {
            return fallBackError("删除自动提交信息");
        }

        @Override
        public JSONResult<Boolean> saveOrUpdate(AutoResponseReq autoResponseReq) {
            return fallBackError("新增和修改提交信息");
        }
    }

}
