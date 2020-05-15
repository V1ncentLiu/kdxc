package com.kuaidao.manageweb.feign.outboundpackage;


import com.kuaidao.account.dto.outboundpackage.OutboundPackageReqDTO;
import com.kuaidao.account.dto.outboundpackage.OutboundPackageRespDTO;
import com.kuaidao.account.dto.outboundpackage.OutboundPackageUpdateDTO;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;

import javax.validation.Valid;


/**
 *  外呼服务套餐 feign
 * @author  Devin.Chen
 * @date 2019-10-07 10:39:01
 * @version V1.0
 */
@FeignClient(name = "account-service", path = "/account/outboundPackage", fallback = OutboundPackageFeignClient.HystrixClientFallback.class)
public interface OutboundPackageFeignClient {

    /**
     * 分页查询外呼套餐配置
     * @param reqDTO
     * @return
     */
    @PostMapping("/listOutboundPackagePage")
    public JSONResult<PageBean<OutboundPackageRespDTO>> listOutboundPackagePage(OutboundPackageReqDTO reqDTO);

    /**
     * 添加服务套餐
     * @param updateDTO
     * @return
     */
    @PostMapping("/addOrUpdate")
    public JSONResult addOrUpdate(OutboundPackageUpdateDTO updateDTO);

    /**
     * 删除服务套餐
     * @param idListLongReq
     * @return
     */
    @PostMapping("/deleteOutboundPackage")
    public JSONResult delete(IdListLongReq idListLongReq);

    /**
     * 根据ID 查询服务套餐
     * @param idEntity
     * @return
     */
    @PostMapping("/queryOutboundPackageById")
    JSONResult<OutboundPackageRespDTO> queryOutboundPackageById(IdEntityLong idEntity);

    @Component
    static   class HystrixClientFallback implements OutboundPackageFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<OutboundPackageRespDTO>> listOutboundPackagePage(OutboundPackageReqDTO reqDTO) {
            return fallBackError("分页查询服务套餐");
        }

        @Override
        public JSONResult addOrUpdate(OutboundPackageUpdateDTO updateDTO) {
            return fallBackError("添加或编辑服务套餐");
        }

        @Override
        public JSONResult delete(IdListLongReq idListLongReq) {
            return fallBackError("删除服务套餐");
        }

        @Override
        public JSONResult<OutboundPackageRespDTO> queryOutboundPackageById(IdEntityLong idEntity) {
            return fallBackError("根据ID查询服务套餐");
        }
    }


}
