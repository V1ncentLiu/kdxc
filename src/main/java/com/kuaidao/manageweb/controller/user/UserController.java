/**
 * 
 */
package com.kuaidao.manageweb.controller.user;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.MD5Util;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.UserErrorCodeEnum;
import com.kuaidao.sys.dto.user.UpdateUserPasswordReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserInfoReq;

/**
 * @author gpc
 *
 */

/**
 * 用户信息
 * 
 * @author: Chen Chengxue
 * @date: 2018年12月28日 下午1:45:12
 * @version V1.0
 */
@Controller
@RequestMapping("/user/userManager")
public class UserController {
    @Autowired
    private RoleManagerFeignClient roleManagerFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /***
     * 用户列表页
     * 
     * @return
     */
    @RequestMapping("/initUserList")
    public String initUserList() {

        return "user/userList";
    }

    /***
     * 用户列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public JSONResult<PageBean<UserInfoDTO>> queryRoleList(
            @RequestBody UserInfoPageParam userInfoPageParam, HttpServletRequest request,
            HttpServletResponse response) {

        JSONResult<PageBean<UserInfoDTO>> list = userInfoFeignClient.list(userInfoPageParam);

        return list;
    }



    /**
     * 保存用户
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/saveUser")
    @ResponseBody
    public JSONResult saveMenu(@Valid @RequestBody UserInfoReq userInfoReq, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        return userInfoFeignClient.create(userInfoReq);
    }

    /**
     * 修改用户信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateUser")
    @ResponseBody
    public JSONResult updateMenu(@Valid @RequestBody UserInfoReq userInfoReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = userInfoReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return userInfoFeignClient.update(userInfoReq);
    }

    /**
     * 修改密码
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updatePassword")
    @ResponseBody
    public JSONResult updateMenu(@Valid @RequestBody UpdateUserPasswordReq updateUserPasswordReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = updateUserPasswordReq.getId();
        // 查询用户信息
        JSONResult<UserInfoDTO> jsonResult = userInfoFeignClient.get(new IdEntityLong(id));
        UserInfoDTO data = jsonResult.getData();
        if (JSONResult.SUCCESS.equals(jsonResult.getCode()) && data != null) {
            // 判断密码是否正确
            if (data.getPassword().equals(MD5Util.StringToMd5(MD5Util
                    .StringToMd5(updateUserPasswordReq.getOldPassword() + data.getSalt())))) {
                UserInfoReq userInfoReq = new UserInfoReq();
                userInfoReq.setId(id);
                userInfoReq.setPassword(updateUserPasswordReq.getNewPassword());
                // 修改密码
                return userInfoFeignClient.update(userInfoReq);
            } else {
                return new JSONResult().fail(UserErrorCodeEnum.ERR_WRONG_PHONE_PASSWORD.getCode(),
                        UserErrorCodeEnum.ERR_WRONG_PHONE_PASSWORD.getMessage());
            }
        } else {
            return new JSONResult().fail(UserErrorCodeEnum.ERR_ACCOUNT_NOT_EXIST.getCode(),
                    UserErrorCodeEnum.ERR_ACCOUNT_NOT_EXIST.getMessage());
        }

    }

    /**
     * 查询用户信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/getUser")
    @ResponseBody
    public JSONResult deleteMenu(@RequestBody IdEntityLong idEntity) {

        return userInfoFeignClient.get(idEntity);
    }



}
