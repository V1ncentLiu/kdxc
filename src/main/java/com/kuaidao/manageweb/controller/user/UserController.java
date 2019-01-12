/**
 * 
 */
package com.kuaidao.manageweb.controller.user;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.MD5Util;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.UserErrorCodeEnum;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UpdateUserPasswordReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserInfoReq;

/**
 * @author gpc
 *
 */

@Controller
@RequestMapping("/user/userManager")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private RoleManagerFeignClient roleManagerFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    /***
     * 用户列表页
     * 
     * @return
     */
    @RequestMapping("/initUserList")
    public String initUserList(HttpServletRequest request) {

        JSONResult<List<RoleInfoDTO>> list = userInfoFeignClient.roleList(new RoleQueryDTO());

        request.setAttribute("roleList", list.getData());

        return "user/userManagePage";
    }

    /***
     * 新增用户页
     * 
     * @return
     */
    @RequestMapping("/initCreateUser")
    public String initCreateUser(HttpServletRequest request) {

        // 查询组织机构树
        JSONResult<List<TreeData>> treeJsonRes = organizationFeignClient.query();
        if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode())
                && treeJsonRes.getData() != null) {
            request.setAttribute("orgData", treeJsonRes.getData());
        } else {
            logger.error("query organization tree,res{{}}", treeJsonRes);
        }
        // 查询角色列表
        JSONResult<List<RoleInfoDTO>> list = userInfoFeignClient.roleList(new RoleQueryDTO());

        request.setAttribute("roleList", list.getData());
        return "user/addUserPage";
    }

    /***
     * 编辑用户页
     * 
     * @return
     */
    @RequestMapping("/initUpdateUser")
    public String initUpdateUser(@RequestParam long id, HttpServletRequest request) {
        JSONResult<List<TreeData>> treeJsonRes = organizationFeignClient.query();
        // 查询用户信息
        JSONResult<UserInfoDTO> jsonResult = userInfoFeignClient.get(new IdEntityLong(id));
        request.setAttribute("user", jsonResult.getData());
        // 查询组织机构树
        if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode())
                && treeJsonRes.getData() != null) {
            request.setAttribute("orgData", treeJsonRes.getData());
        } else {
            logger.error("query organization tree,res{{}}", treeJsonRes);
        }
        // 查询角色列表
        JSONResult<List<RoleInfoDTO>> list = userInfoFeignClient.roleList(new RoleQueryDTO());

        request.setAttribute("roleList", list.getData());

        return "user/editUserPage";
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
                JSONResult<String> updatePwdRes = userInfoFeignClient.update(userInfoReq);
                if(updatePwdRes!=null && JSONResult.SUCCESS.equals(updatePwdRes.getCode())) {
                    
                    Subject subject = SecurityUtils.getSubject();
                    UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
                    if (subject.isAuthenticated()) {
                        subject.logout();
                    }
                    // 退出成功，保存退出状态
                    UserInfoReq update = new UserInfoReq();
                    update.setId(user.getId());
                    update.setIsLogin(Constants.IS_LOGIN_DOWN);
                    userInfoFeignClient.update(update);
                }
                return updatePwdRes;
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
