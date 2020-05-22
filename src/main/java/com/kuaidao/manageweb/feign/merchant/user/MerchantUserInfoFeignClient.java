package com.kuaidao.manageweb.feign.merchant.user;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserInfoReq;

/**
 * 用户
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "sys-service", path = "/sys/merchant/userInfo",
        fallback = MerchantUserInfoFeignClient.HystrixClientFallback.class)
public interface MerchantUserInfoFeignClient {

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

    /**
     * 新增用户
     *
     * @param
     * @return
     */
    @PostMapping("/updateUser")
    public JSONResult<String> updateUser(@RequestBody UserInfoReq req);

    /**
     * 根据参数查询用户列表
     * 
     * @param userInfoDTO
     * @return
     */
    @PostMapping("/merchantUserList")
    public JSONResult<List<UserInfoDTO>> merchantUserList(@RequestBody UserInfoDTO userInfoDTO);


    @Component
    static class HystrixClientFallback implements MerchantUserInfoFeignClient {

        private static Logger logger = LoggerFactory.getLogger(MerchantUserInfoFeignClient.class);


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

        @Override
        public JSONResult<String> updateUser(UserInfoReq req) {
            return fallBackError("更新商家信息");
        }

        @Override
        public JSONResult<List<UserInfoDTO>> merchantUserList(UserInfoDTO userInfoDTO) {
            return fallBackError("查询账号失败");
        }

    }


}
