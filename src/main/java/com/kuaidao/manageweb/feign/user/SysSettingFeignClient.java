package com.kuaidao.manageweb.feign.user;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 登录记录
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "sys-service", path = "/sys/sysSetting",
        fallback = SysSettingFeignClient.HystrixClientFallback.class)
public interface SysSettingFeignClient {


    /**
     * 查询系统参数
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/getbyCode")
    public JSONResult<SysSettingDTO> getByCode(@RequestBody SysSettingReq sysSettingReq);

    /**
     * 修改系统参数
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/update")
    public JSONResult<Void> update(@RequestBody SysSettingReq sysSettingReq);

    /**
     * 修改系统参数
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/updateByCode")
    public JSONResult<Void> updateByCode(@RequestBody SysSettingReq sysSettingReq);

    /**
     * 系统参数分页
     * @param sysSettingReq
     * @return
     */
    @PostMapping("/page")
    public JSONResult<PageBean<SysSettingDTO>> page(@RequestBody SysSettingReq sysSettingReq);


    @Component
    static class HystrixClientFallback implements SysSettingFeignClient {

        private static Logger logger = LoggerFactory.getLogger(SysSettingFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



        @Override
        public JSONResult<SysSettingDTO> getByCode(@RequestBody SysSettingReq sysSettingReq) {
            return fallBackError("查询系统参数");
        }

        @Override
        public JSONResult<Void> update(@RequestBody SysSettingReq sysSettingReq) {
            return fallBackError("修改系统参数");
        }

        @Override
        public JSONResult<Void> updateByCode(@RequestBody SysSettingReq sysSettingReq) {
            return fallBackError("修改系统参数");
        }

        @Override
        public JSONResult<PageBean<SysSettingDTO>> page(SysSettingReq sysSettingReq) {
            return fallBackError("系统参数分页");
        }


    }

}
