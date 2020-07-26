package com.kuaidao.manageweb.feign.user;

import java.util.List;

import com.kuaidao.sys.dto.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.PhoneEntity;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import feign.hystrix.FallbackFactory;

/**
 * 用户
 *
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "sys-service", path = "/sys/userInfo",
        fallbackFactory = LoginRecordFeignClient.HystrixClientFallback.class)
public interface UserInfoFeignClient {
    /**
     * 根据id查询用户信息
     *
     * @param
     * @return
     */
    @PostMapping("/get")
    public JSONResult<UserInfoDTO> get(@RequestBody IdEntityLong id);

    /**
     * 根据手机号查询用户信息
     *
     * @param
     * @return
     */
    @PostMapping("/getbyPhone")
    public JSONResult<UserInfoDTO> getbyPhone(@RequestBody PhoneEntity phone);

    /**
     * 根据用户名查询用户信息
     *
     * @param
     * @return
     */
    @PostMapping("/getbyUserName")
    public JSONResult<UserInfoDTO> getbyUserName(@RequestBody UserInfoReq userInfoReq);

    /**
     * 查询用户集合
     *
     * @param
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<UserInfoDTO>> list(@RequestBody UserInfoPageParam param);

    /**
     * 修改用户信息
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    public JSONResult<String> update(@RequestBody UserInfoReq req);

    /**
     * 新增用户
     *
     * @param
     * @return
     */
    @PostMapping("/create")
    public JSONResult<String> create(@RequestBody UserInfoReq req);

    /**
     * 角色列表
     *
     * @param
     * @return
     */
    @PostMapping("/roleList")
    public JSONResult<List<RoleInfoDTO>> roleList(@RequestBody RoleQueryDTO roleQueryDTO);


    /**
     * 根据状态列表或用户名称查询 用户 精确匹配
     */
    @PostMapping("/listUserInfoByParam")
    JSONResult<List<UserInfoDTO>> listUserInfoByParam(@RequestBody UserInfoParamListReqDTO reqDTO);

    /**
     * 根据条件查询用户集合
     */
    @PostMapping("/getUserInfoListByParam")
    JSONResult<List<UserInfoDTO>> getUserInfoListByParam(
            @RequestBody UserOrgRoleReq userOrgRoleReq);

    /**
     * 根据机构和角色Code查询账号集合
     */
    @PostMapping("/listByOrgAndRole")
    JSONResult<List<UserInfoDTO>> listByOrgAndRole(@RequestBody UserOrgRoleReq userOrgRoleReq);



    @RequestMapping(method = RequestMethod.POST, value = "/listById")
    public JSONResult<List<UserInfoDTO>> listById(@RequestBody IdListLongReq idList);

    /**
     * 查询商家用户集合
     *
     * @param
     * @return
     */
    @PostMapping("/merchantlist")
    public JSONResult<PageBean<UserInfoDTO>> merchantlist(@RequestBody UserInfoPageParam param);

    /**
     * 按条件查询用户 不分页
     */
    @PostMapping("/listNoPage")
    JSONResult<List<UserInfoDTO>> listNoPage(@RequestBody UserInfoPageParam param);
    /**
     * @Description: 根据条件查询用户与数据权限关系
     * @Param: [req]
     * @return: com.kuaidao.common.entity.JSONResult<java.util.List<com.kuaidao.sys.dto.user.UserDataAuthReq>>
     * @author: fanjd
     * @date: 2020/6/23 16:50
     * @version: V1.0
     */
    @PostMapping("/findUserDataAuthByParam")
     JSONResult<List<UserDataAuthReq>> findUserDataAuthByParam(@RequestBody UserDataAuthReq req);
    @Component
    static class HystrixClientFallback implements FallbackFactory<UserInfoFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public UserInfoFeignClient create(Throwable cause) {
            return new UserInfoFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
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
                public JSONResult<PageBean<UserInfoDTO>> list(
                        @RequestBody UserInfoPageParam param) {
                    return fallBackError("查询用户集合");
                }

                @Override
                public JSONResult<List<RoleInfoDTO>> roleList(
                        @RequestBody RoleQueryDTO roleQueryDTO) {
                    return fallBackError("查询角色列表");
                }

                @Override
                public JSONResult<List<UserInfoDTO>> listUserInfoByParam(
                        UserInfoParamListReqDTO reqDTO) {
                    return fallBackError("根据状态列表或用户名称查询 用户");
                }

                @Override
                public JSONResult<List<UserInfoDTO>> getUserInfoListByParam(
                        UserOrgRoleReq userOrgRoleReq) {
                    return fallBackError("根据条件查询用户集合");
                }

                @Override
                public JSONResult<List<UserInfoDTO>> listByOrgAndRole(
                        @RequestBody UserOrgRoleReq userOrgRoleReq) {
                    return fallBackError("根据机构和角色Code查询账号集合");
                }

                @Override
                public JSONResult<List<UserInfoDTO>> listById(IdListLongReq idList) {
                    // TODO Auto-generated method stub
                    return fallBackError("根据idlist查询用户集合");
                }

                @Override
                public JSONResult<PageBean<UserInfoDTO>> merchantlist(UserInfoPageParam param) {
                    return fallBackError("查询商家账号");
                }

                @Override
                public JSONResult<List<UserInfoDTO>> listNoPage(UserInfoPageParam param) {
                    return fallBackError("按条件查询用户（不分页）");
                }

                @Override
                public JSONResult<List<UserDataAuthReq>> findUserDataAuthByParam(UserDataAuthReq req) {
                    return fallBackError("按条件查询用户与数据权限关系");
                }
            };
        }

    }


}
