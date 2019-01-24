package com.kuaidao.manageweb.feign.user;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.PhoneEntity;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserInfoParamListReqDTO;
import com.kuaidao.sys.dto.user.UserInfoReq;

/**
 * 用户
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "sys-service", path = "/sys/userInfo",
        fallback = UserInfoFeignClient.HystrixClientFallback.class)
public interface UserInfoFeignClient {
    /**
     * 根据id查询用户信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/get")
    public JSONResult<UserInfoDTO> get(@RequestBody IdEntityLong id);

    /**
     * 根据手机号查询用户信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/getbyPhone")
    public JSONResult<UserInfoDTO> getbyPhone(@RequestBody PhoneEntity phone);

    /**
     * 根据用户名查询用户信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/getbyUserName")
    public JSONResult<UserInfoDTO> getbyUserName(@RequestBody UserInfoReq userInfoReq);

    /**
     * 查询用户集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<UserInfoDTO>> list(@RequestBody UserInfoPageParam param);

    /**
     * 修改用户信息
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/update")
    public JSONResult<String> update(@RequestBody UserInfoReq req);

    /**
     * 新增用户
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult<String> create(@RequestBody UserInfoReq req);

    /**
     * 角色列表
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/roleList")
    public JSONResult<List<RoleInfoDTO>> roleList(@RequestBody RoleQueryDTO roleQueryDTO);
    
    
    /**
     * 根据状态列表或用户名称查询 用户  精确匹配
     */
    @PostMapping("/listUserInfoByParam")
    JSONResult<List<UserInfoDTO>> listUserInfoByParam(@RequestBody UserInfoParamListReqDTO reqDTO);


    @Component
    static class HystrixClientFallback implements UserInfoFeignClient {

        private static Logger logger = LoggerFactory.getLogger(UserInfoFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<UserInfoDTO> get(@RequestBody IdEntityLong id) {
            return fallBackError("根据id查询用户信息");
        }


        @Override
        public JSONResult<UserInfoDTO> getbyPhone(@RequestBody PhoneEntity phone) {
            return fallBackError("根据手机号查询用户信息");
        }


        @Override
        public JSONResult<UserInfoDTO> getbyUserName(@RequestBody UserInfoReq userInfoReq) {
            return fallBackError("根据用户名查询用户信息");
        }

        @Override
        public JSONResult<String> update(@RequestBody UserInfoReq req) {
            return fallBackError("修改用户信息");
        }

        @Override
        public JSONResult<String> create(@RequestBody UserInfoReq req) {
            return fallBackError("新增用户");
        }


        @Override
        public JSONResult<PageBean<UserInfoDTO>> list(@RequestBody UserInfoPageParam param) {
            return fallBackError("查询用户集合");
        }

        @Override
        public JSONResult<List<RoleInfoDTO>> roleList(@RequestBody RoleQueryDTO roleQueryDTO) {
            return fallBackError("查询角色列表");
        }

        @Override
        public JSONResult<List<UserInfoDTO>> listUserInfoByParam(UserInfoParamListReqDTO reqDTO) {
            return fallBackError("根据状态列表或用户名称查询 用户");
        }



    }


}
