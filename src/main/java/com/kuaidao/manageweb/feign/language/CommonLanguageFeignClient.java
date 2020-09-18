package com.kuaidao.manageweb.feign.language;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.custservice.dto.language.CommonLanguageOrderReq;
import com.kuaidao.custservice.dto.language.CommonLanguagePageReqDTO;
import com.kuaidao.custservice.dto.language.CommonLanguageReqDto;
import com.kuaidao.custservice.dto.language.CommonLanguageRespDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 自动提交模块服务调用Feign类
 *
 * @author fengyixuan
 */
@FeignClient(name = "cust-service-service", path="/custservice/commonLanguage",fallback = CommonLanguageFeignClient.HystrixClientFallback.class)
public interface CommonLanguageFeignClient {

    /**
     * 根据类型查询  根据ID查询返回常用词信息
     * @param commonLanguageReq
     * @return
     */
    @PostMapping(value = "/queryListByType")
     JSONResult<List<CommonLanguageRespDto>> queryListByType(@RequestBody CommonLanguageReqDto commonLanguageReq);


    /**
     * 根据类型查询  根据ID查询返回常用词
     * @param commonLanguageReq
     * @return
     */
    @PostMapping(value = "/queryById")
     JSONResult<CommonLanguageRespDto> queryById(@RequestBody CommonLanguageReqDto commonLanguageReq);


    /**
     * 更新常用词
     * @param commonLanguageReq
     * @return
     */
    @PostMapping(value = "/update")
    JSONResult<Boolean> update(@RequestBody CommonLanguageReqDto commonLanguageReq) ;

    /**
     * 新增常用词
     * @param commonLanguageReq
     * @return
     */
    @PostMapping(value = "/insert")
    JSONResult<Boolean> insert(@RequestBody CommonLanguageReqDto commonLanguageReq);

    /**
     * 根据ID查处常用词
     * @param commonLanguageReq
     * @return
     */
    @PostMapping(value = "/deleteById")
    JSONResult<Boolean> deleteById(@RequestBody CommonLanguageReqDto commonLanguageReq);

    /**
     * 查询常用词带分页
     *
     * @param commonLanguagePageReqDTO
     * @return
     */
    @PostMapping("/queryPageByType")
     JSONResult<PageBean<CommonLanguageRespDto>> queryPageByType(@RequestBody CommonLanguagePageReqDTO commonLanguagePageReqDTO);
    /**
     * 查询常用词带分页
     *
     * @param commonLanguageOrderReq
     * @return
     */
    @PostMapping("/updateOrder")
    JSONResult<Boolean> updateOrder(CommonLanguageOrderReq commonLanguageOrderReq);

    @Component
    static class HystrixClientFallback implements CommonLanguageFeignClient {

        private static final Logger logger = LoggerFactory.getLogger(CommonLanguageFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<CommonLanguageRespDto>> queryListByType(CommonLanguageReqDto commonLanguageReq) {
            return fallBackError("根据类型查询常用词List");
        }

        @Override
        public JSONResult<CommonLanguageRespDto> queryById(CommonLanguageReqDto commonLanguageReq) {
            return fallBackError("根据ID查询常用词");
        }

        @Override
        public JSONResult<Boolean> update(CommonLanguageReqDto commonLanguageReq) {
            return fallBackError("更新常用词");
        }

        @Override
        public JSONResult<Boolean> insert(CommonLanguageReqDto commonLanguageReq) {
            return fallBackError("新增常用词");
        }

        @Override
        public JSONResult<Boolean> deleteById(CommonLanguageReqDto commonLanguageReq) {
            return fallBackError("更具ID删除常用词");
        }

        @Override
        public JSONResult<PageBean<CommonLanguageRespDto>> queryPageByType(CommonLanguagePageReqDTO commonLanguagePageReqDTO) {
            return fallBackError("分页根据类型查询常用词");
        }

        @Override
        public JSONResult<Boolean> updateOrder(CommonLanguageOrderReq commonLanguageOrderReq) {
            return fallBackError("常用词分页");
        }
    }

}
