package com.kuaidao.manageweb.config;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoReq;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

// 实现AuthorizingRealm接口用户用户认证
public class MyShiroRealm extends AuthorizingRealm {

    @Lazy
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;



    // 角色权限和对应权限添加
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取登录用户名
        String username = (String) principalCollection.getPrimaryPrincipal();
        UserInfoReq userInfoReq = new UserInfoReq();
        userInfoReq.setUsername(username);
        // 查询用户名称
        JSONResult<UserInfoDTO> getbyUserName = userInfoFeignClient.getbyUserName(userInfoReq);

        // 添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // simpleAuthorizationInfo.addRoles(roles);
        if (JSONResult.SUCCESS.equals(getbyUserName.getCode()) && getbyUserName.getData() != null) {
            // 添加权限
            simpleAuthorizationInfo
                    .addStringPermissions(getbyUserName.getData().getOperationList());
        }
        return simpleAuthorizationInfo;

    }

    // 用户认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        // 加这一步的目的是在Post请求的时候会先进认证，然后在到请求
        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        // 获取用户信息
        String username = authenticationToken.getPrincipal().toString();
        UserInfoReq userInfoReq = new UserInfoReq();
        userInfoReq.setUsername(username);
        // 查询用户名称
        // 通过username从数据库中查找 User对象，如果找到，没找到.
        // 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        JSONResult<UserInfoDTO> getbyUserName = userInfoFeignClient.getbyUserName(userInfoReq);
        if (getbyUserName.getData() == null) {
            // 这里返回后会报出对应异常
            return null;
        }
        // 这里验证authenticationToken和simpleAuthenticationInfo的信息
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username,
                getbyUserName.getData().getPassword().toString(),
                ByteSource.Util.bytes(getbyUserName.getData().getSalt()), getName());
        return simpleAuthenticationInfo;

    }
}
