package com.kuaidao.manageweb.feign.merchant.user;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.*;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 用户
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "sys-service", path = "/sys/merchant/userInfo",
        fallback = MechantUserInfoFeignClient.HystrixClientFallback.class)
public interface MechantUserInfoFeignClient {

    /**
     * 查询商家用户集合
     *
     * @param
     * @return
     */
    @PostMapping("/merchantlist")
    public JSONResult<PageBean<UserInfoDTO>> merchantlist(@RequestBody UserInfoPageParam param);
    /**
     * 新增用户
     *
     * @param
     * @return
     */
    @PostMapping("/create")
    public JSONResult<String> create(@RequestBody UserInfoReq req);
    /**
     * 根据id查询用户
     *
     * @param
     * @return
     */
    @PostMapping("/getMechantUserById")
    public JSONResult<UserInfoReq> getMechantUserById(@RequestBody UserInfoReq req);

    @Component
    static class HystrixClientFallback implements MechantUserInfoFeignClient {

        private static Logger logger = LoggerFactory.getLogger(MechantUserInfoFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<UserInfoDTO>> merchantlist(UserInfoPageParam param) {
            return fallBackError("查询商家账号");
        }

        @Override
        public JSONResult<String> create(UserInfoReq req) {
            return fallBackError("添加商家账号失败");
        }

        @Override
        public JSONResult<UserInfoReq> getMechantUserById(UserInfoReq req) {
            return fallBackError("根据id查询商家详细信息");
        }

    }


}
