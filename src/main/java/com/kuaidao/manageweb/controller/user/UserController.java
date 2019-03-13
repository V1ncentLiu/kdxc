/**
 * 
 */
package com.kuaidao.manageweb.controller.user;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.entity.UpdatePasswordSettingReq;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.constant.UserErrorCodeEnum;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UpdateUserPasswordReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserInfoParamListReqDTO;
import com.kuaidao.sys.dto.user.UserInfoReq;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

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
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    /***
     * 用户列表页
     * 
     * @return
     */
    @RequestMapping("/initUserList")
    @RequiresPermissions("sys:userManager:view")
    public String initUserList(HttpServletRequest request) {

        JSONResult<List<RoleInfoDTO>> list = userInfoFeignClient.roleList(new RoleQueryDTO());

        request.setAttribute("roleList", list.getData());

        String passwordExpires = getSysSetting(SysConstant.PASSWORD_EXPIRES);
        String reminderTime = getSysSetting(SysConstant.REMINDER_TIME);
        request.setAttribute("passwordExpires", passwordExpires);
        if (reminderTime != null) {
            request.setAttribute("reminderTime", reminderTime);
        }
        // 查询组织机构树
        JSONResult<List<TreeData>> treeJsonRes = organizationFeignClient.query();
        // 查询组织机构树
        if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode())
                && treeJsonRes.getData() != null) {
            request.setAttribute("orgData", treeJsonRes.getData());
        } else {
            logger.error("query organization tree,res{{}}", treeJsonRes);
        }
        return "user/userManagePage";
    }

    /**
     * 
     * 
     * @param request
     * @return
     */

    @RequestMapping("/listByOrgAndRole")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> listByOrgAndRole(HttpServletRequest request,
            @RequestBody UserOrgRoleReq req) {
        UserOrgRoleReq userRole = new UserOrgRoleReq();
        userRole.setOrgId(req.getOrgId());
        userRole.setRoleCode(req.getRoleCode());
        return userInfoFeignClient.listByOrgAndRole(userRole);
    }

    /***
     * 新增用户页
     * 
     * @return
     */
    @RequestMapping("/initCreateUser")
    @RequiresPermissions("sys:userManager:add")
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
    @RequiresPermissions("sys:userManager:edit")
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
    @RequiresPermissions("sys:userManager:view")
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
    @RequiresPermissions("sys:userManager:add")
    @LogRecord(description = "新增用户", operationType = OperationType.INSERT,
            menuName = MenuEnum.USER_MANAGEMENT)
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
    @RequiresPermissions("sys:userManager:edit")
    @LogRecord(description = "修改用户信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.USER_MANAGEMENT)
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
     * 启用
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateStatusEnable")
    @ResponseBody
    @RequiresPermissions("sys:userManager:edit")
    @LogRecord(description = "启用", operationType = OperationType.ENABLE,
            menuName = MenuEnum.USER_MANAGEMENT)
    public JSONResult updateStatusEnable(@Valid @RequestBody UserInfoReq userInfoReq,
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
     * 禁用
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateStatusDisable")
    @ResponseBody
    @RequiresPermissions("sys:userManager:edit")
    @LogRecord(description = "禁用", operationType = OperationType.DISABLE,
            menuName = MenuEnum.USER_MANAGEMENT)
    public JSONResult updateStatusDisable(@Valid @RequestBody UserInfoReq userInfoReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = userInfoReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        if (2 == userInfoReq.getStatus()) {
            userInfoReq.setDisableTime(new Date());
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
    @LogRecord(description = "修改密码", operationType = OperationType.UPDATE,
            menuName = MenuEnum.UPDATE_PASSWORD)
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
                if (updatePwdRes != null && JSONResult.SUCCESS.equals(updatePwdRes.getCode())) {

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

    /**
     * 修改密码安全设置
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updatePasswordSetting")
    @ResponseBody
    @RequiresPermissions("sys:userManager:edit")
    @LogRecord(description = "修改密码安全设置", operationType = OperationType.UPDATE,
            menuName = MenuEnum.USER_MANAGEMENT)
    public JSONResult updatePasswordSetting(
            @Valid @RequestBody UpdatePasswordSettingReq updatePasswordSettingReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 更新密码最大使用时间
        SysSettingReq sysSettingReq = new SysSettingReq();
        sysSettingReq.setCode(SysConstant.PASSWORD_EXPIRES);
        sysSettingReq.setValue(updatePasswordSettingReq.getPasswordExpires());
        sysSettingFeignClient.updateByCode(sysSettingReq);

        // 更新密码到期提醒时间
        sysSettingReq.setCode(SysConstant.REMINDER_TIME);
        StringBuffer stringBuffer = new StringBuffer();
        List<String> reminderTimeList = updatePasswordSettingReq.getReminderTime();
        for (String string : reminderTimeList) {
            if (stringBuffer.length() == 0) {
                stringBuffer.append(string);
            } else {
                stringBuffer.append(",");
                stringBuffer.append(string);
            }
        }
        sysSettingReq.setValue(stringBuffer.toString());
        sysSettingFeignClient.updateByCode(sysSettingReq);

        return new JSONResult().success(null);
    }

    /**
     * 查询系统参数
     * 
     * @param code
     * @return
     */
    private String getSysSetting(String code) {
        SysSettingReq sysSettingReq = new SysSettingReq();
        sysSettingReq.setCode(code);
        JSONResult<SysSettingDTO> byCode = sysSettingFeignClient.getByCode(sysSettingReq);
        if (byCode != null && JSONResult.SUCCESS.equals(byCode.getCode())) {
            return byCode.getData().getValue();
        }
        return null;
    }

    /**
     * 根据状态列表或用户名称查询 用户 精确匹配
     */
    @PostMapping("/listUserInfoByParam")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> listUserInfoByParam() {
        UserInfoParamListReqDTO reqDTO = new UserInfoParamListReqDTO();
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        reqDTO.setStatusList(statusList);
        return userInfoFeignClient.listUserInfoByParam(reqDTO);
    }

    /**
     * 首页 修改密码
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updatePwd")
    @ResponseBody
    @LogRecord(description = "首页-修改密码", operationType = OperationType.UPDATE,
            menuName = MenuEnum.UPDATE_PASSWORD)
    public JSONResult updatePwd(@Valid @RequestBody UpdateUserPasswordReq updateUserPasswordReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        Long id = user.getId();
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
                if (updatePwdRes != null && JSONResult.SUCCESS.equals(updatePwdRes.getCode())) {

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
                        "当前密码错误，请重新输入");
            }
        } else {
            return new JSONResult().fail(UserErrorCodeEnum.ERR_ACCOUNT_NOT_EXIST.getCode(),
                    UserErrorCodeEnum.ERR_ACCOUNT_NOT_EXIST.getMessage());
        }

    }

}
