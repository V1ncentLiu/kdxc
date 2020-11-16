/**
 *
 */
package com.kuaidao.manageweb.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.kuaidao.common.constant.BusinessLineConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.MD5Util;
import com.kuaidao.custservice.constant.SaleOLOperationTypeEnum;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.ManagerWebErrorCodeEnum;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.entity.LoginReq;
import com.kuaidao.manageweb.feign.msgpush.MsgPushFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.LoginRecordFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.service.im.ImMassageService;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.msgpush.dto.SmsCodeAndMobileValidReq;
import com.kuaidao.msgpush.dto.SmsCodeSendReq;
import com.kuaidao.msgpush.dto.SmsVoiceCodeReq;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.constant.UserErrorCodeEnum;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.crazycake.shiro.exception.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * 登录
 *
 * @version V1.0
 * @author:zxy
 * @date: 2018年12月28日 下午1:45:12
 */
@Controller
public class LoginController {
    @Autowired
    private LoginRecordFeignClient loginRecordFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;
    @Autowired
    private MsgPushFeignClient msgPushFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private DefaultKaptcha captchaProducer;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${ws_url_http}")
    private String wsUrlHttp;
    @Value("${spring.rabbitmq.username}")
    private String mqUserName;
    @Value("${spring.rabbitmq.password}")
    private String mqPassword;
    @Value("${ws_url_https}")
    private String wsUrlHttps;
    @Value("${session_time_out}")
    private int sessionTimeOut;
    /** 汇聚-商家端 域名：用来判断首页跳转 **/
    @Value("${merchantServletName}")
    private String merchantServletName;
    /** 汇聚-餐盟 域名：用来判断首页跳转 **/
    @Value("${cmServletName}")
    private String cmServletName;

    /**
     * 是否显示验证码
     **/
    @Value("${VerificationCodeShow}")
    private boolean verificationCodeShow;
    @Autowired
    OrganizationFeignClient organizationFeignClient;
    @Autowired
    ImMassageService imMassageService;

    /***
     * 登录页
     *
     * @return
     */
    @GetMapping("/")
    public String loginPage() {
        return "redirect:/login";
    }

    /***
     * 登录页
     *
     * @return
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        Object isShowLogoutBox =
                SecurityUtils.getSubject().getSession().getAttribute("isShowLogoutBox");
        SecurityUtils.getSubject().getSession().removeAttribute("isShowLogoutBox");
        request.setAttribute("isShowLogoutBox", isShowLogoutBox);
        request.setAttribute("verificationCodeShow", verificationCodeShow);

        String serverName = request.getServerName();
        if (serverName.equals(merchantServletName)) {
            // 汇聚商家端：跳转
            return "merchantLogin/login";
        } else if (serverName.equals(cmServletName)) {
            // 汇聚餐盟：跳转
            return "canmengLogin/login";
        } else {
            // 系统默认 汇聚登录页
            return "login/login";
        }
    }

    /***
     * 修改密码页
     * 
     * @return
     */
    @RequestMapping("/login/resetPwd")
    public String resetPwd() {
        return "login/resetPwd";
    }

    /**
     * 商家版修改密码
     * 
     * @return
     */
    @RequestMapping("/merchantLogin/resetPwd")
    public String resetMerchantPwd() {
        return "merchantLogin/resetPwd";
    }

    /**
     * 餐盟忘记密码
     *
     * @return
     */
    @RequestMapping("/cmLogin/resetPwd")
    public String resetCmPwd() {
        return "canmengLogin/resetPwd";
    }


    @RequestMapping(value = "/login/index", method = {RequestMethod.POST})
    @ResponseBody
    @LogRecord(description = "登录", operationType = OperationType.LOGIN, menuName = MenuEnum.LOGIN)
    public JSONResult login(@RequestBody LoginReq loginReq, HttpServletRequest request, Model model,
            RedirectAttributes redirectAttributes) throws Exception {
        String username = loginReq.getUsername();
        String password = loginReq.getPassword();
        redirectAttributes.addFlashAttribute("username", username);
        redirectAttributes.addFlashAttribute("password", password);
        String errorMessage = "";

        String ipAddr = CommonUtil.getIpAddr(request);// 获取ip地址
        UserInfoReq userInfoReq = new UserInfoReq();
        userInfoReq.setUsername(username);
        // 查询用户信息
        JSONResult<UserInfoDTO> getbyUserName = userInfoFeignClient.getbyUserName(userInfoReq);
        if (!JSONResult.SUCCESS.equals(getbyUserName.getCode())) {
            return getbyUserName;
        }
        UserInfoDTO user = getbyUserName.getData();
        List<RoleInfoDTO> roleList = user.getRoleList();
        // TODO使用工具类判断
        if (roleList == null || roleList.size() == 0) {
            return new JSONResult<>().fail(ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(),
                    "当前账号没有角色");
        }
        // TODO修改错误码
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        // 判断登陆IP限制
        if (SysConstant.YES.equals(roleInfoDTO.getIsIpLimit())) {
            List<String> ipList = roleInfoDTO.getIpList();
            logger.info("login_ip_address{{}},ipList{{}}", ipAddr, ipList);
            boolean isMatch = false;
            for ( String  ip : ipList){
                if( null == ip) { continue; }
                ip = ip.trim();
                if(Pattern.matches(ip, ipAddr)){
                    // 匹配正确
                    isMatch = true;
                    break ;
                }
            }
            if (!isMatch) {
                return new JSONResult<>().fail(ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(),
                        "当前登录系统IP异常，请联系管理员");
            }
        }
        // 查询登录用户所属业务线放入登录对象中
        JSONResult<OrganizationDTO> organizationDTOJSONResult =
                organizationFeignClient.queryOrgById(new IdEntity(String.valueOf(user.getOrgId())));
        if (JSONResult.SUCCESS.equals(organizationDTOJSONResult.getCode())) {
            OrganizationDTO organizationDTO = organizationDTOJSONResult.getData();
            if (organizationDTO != null && organizationDTO.getBusinessLine() != null) {
                user.setBusinessLine(organizationDTO.getBusinessLine());
            }
            user.setPromotionCompany(organizationDTO.getPromotionCompany());
        }
        // 餐盟用户登录增加判断
        boolean cmFlag = null != loginReq.getLoginSource()
                && LoginReq.LOGIN_SOURCE.equals(loginReq.getLoginSource());
        if (cmFlag) {
            if (null != user.getBusinessLine()
                    && (BusinessLineConstant.SHANGJI == user.getBusinessLine()
                            || BusinessLineConstant.SHCS == user.getBusinessLine())) {
                if (!RoleCodeEnum.DXCYGW.name().equals(roleInfoDTO.getRoleCode())
                        && !RoleCodeEnum.SWJL.name().equals(roleInfoDTO.getRoleCode())) {
                    return new JSONResult<>().fail(
                            ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(), "抱歉您的账号暂未开放餐盟端");
                }
            } else {
                return new JSONResult<>().fail(ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(),
                        "抱歉您的账号暂未开放餐盟端");
            }
        }
        Date date = new Date();
        LoginRecordReq loginRecord = new LoginRecordReq();
        loginRecord.setId(IdUtil.getUUID());
        loginRecord.setUsername(username);
        loginRecord.setIp(ipAddr);
        loginRecord.setLoginTime(date);
        // * 校验验证码
        // 只有正式环境校验验证码 其他环境不校验 by fanjd 2019/5/8
        // 是否继续判断标识
        boolean flag = true;
        if (verificationCodeShow) {
            SmsCodeAndMobileValidReq smsCodeAndMobileValidReq = new SmsCodeAndMobileValidReq();
            smsCodeAndMobileValidReq.setCode(loginReq.getCode());
            smsCodeAndMobileValidReq.setMobile(user.getPhone());
            String msgId = redisTemplate.opsForValue().get(Constants.MSG_ID + user.getId());
            smsCodeAndMobileValidReq.setMsgId(msgId);
            JSONResult result = msgPushFeignClient.validCodeAndMobile(smsCodeAndMobileValidReq);
            if (!JSONResult.SUCCESS.equals(result.getCode())) {
                flag = false;
                logger.warn(result.getMsg());
                errorMessage = "验证码错误";
                loginRecord.setLoginStatus(Constants.LOGIN_STATUS_PASSWORD_ERROR);
            }
        }
        if (flag) {
            try {
                if (null != user) {
                    // 判断账号是否禁用
                    if (SysConstant.USER_STATUS_DISABLE.equals(user.getStatus())) {
                        errorMessage = "帐号已禁用，请联系管理员修改！";
                        return new JSONResult<>().fail(
                                ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(), errorMessage);
                    }
                    // 判断账号是否锁定
                    if (SysConstant.USER_STATUS_LOCK.equals(user.getStatus())) {
                        errorMessage = "账号锁定，请联系管理员修改！";
                        return new JSONResult<>().fail(
                                ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(), errorMessage);
                    }
                    // 判断密码是否过期。
                    String passwordExpires = getSysSetting(SysConstant.PASSWORD_EXPIRES);
                    if (StringUtils.isNotBlank(passwordExpires)) {
                        long pwdTime = Long.parseLong(passwordExpires);
                        if (isRepwdNotify(user, pwdTime)) {// 是否到密码提醒修改日期
                            int differentDays =
                                    DateUtil.differentDays(user.getResetPasswordTime(), new Date());
                            redirectAttributes.addFlashAttribute("isNotify",
                                    pwdTime - differentDays);
                        }
                        Date resetpwdTime = user.getResetPasswordTime();
                        if (date.getTime() - resetpwdTime.getTime() > pwdTime * 24 * 60 * 60
                                * 1000) {
                            errorMessage = "账号过期，请忘记密码方式找回！";
                            return new JSONResult<>().fail(
                                    ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(),
                                    errorMessage);
                        }
                    }
                }
                /** 强制清空session，防止非正常退出导致权限不刷新 **/
                kickOutUser(user.getId());
                // 用户登陆
                UsernamePasswordToken token = new UsernamePasswordToken(username,
                        MD5Util.StringToMd5(MD5Util.StringToMd5(password + user.getSalt())));
                Subject subject = SecurityUtils.getSubject();
                subject.login(token);
                SecurityUtils.getSubject().getSession().setTimeout(sessionTimeOut);
                SecurityUtils.getSubject().getSession().setAttribute("userId", "" + user.getId());
                SecurityUtils.getSubject().getSession().setAttribute("userName",
                        "" + user.getUsername());
                SecurityUtils.getSubject().getSession().setAttribute("user", user);
                // 登录成功，保存登录状态
                userInfoReq.setId(user.getId());
                userInfoReq.setIsLogin(Constants.IS_LOGIN_UP);
                userInfoFeignClient.update(userInfoReq);
                // 保存登录记录
                loginRecord.setLoginStatus(Constants.LOGIN_STATUS_SUCCESS);
                loginRecord.setIsChangeMachine(SysConstant.NO);
                // 判断是否是踢下线操作
                String sessionid = SecurityUtils.getSubject().getSession().getId().toString();
                String string =
                        redisTemplate.opsForValue().get(Constants.SESSION_ID + user.getId());
                logger.warn("newSessionId:" + sessionid);
                logger.warn("OldSessionId:" + string);
                if (Constants.IS_LOGIN_UP.equals(user.getIsLogin())) {
                    if (!sessionid.equals(string)) {
                        // 如果是踢下线操作1-判断累计次数，是否锁定账号 2-发送下线通知
                        loginRecord.setIsChangeMachine(SysConstant.YES);
                        // 发送下线通知
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                amqpTemplate.convertAndSend("amq.topic", string, string);
                            }
                        }).start();
                        // 判断累计次数
                        List<LoginRecordDTO> findList2 = findLoginRecordList(username, null,
                                new Date(date.getTime() - 1800000), date, null, SysConstant.YES);
                        // 30分钟内互踢5次 ，加上这次共6次锁定账号
                        if (findList2 != null && findList2.size() >= 5) {
                            // 锁定账号
                            UserInfoReq lock = new UserInfoReq();
                            lock.setId(user.getId());
                            lock.setStatus(SysConstant.USER_STATUS_LOCK);
                            userInfoFeignClient.update(lock);
                        }
                    }
                }
                SecurityUtils.getSubject().getSession().setAttribute("sessionid", "" + sessionid);
                redisTemplate.opsForValue().set(Constants.SESSION_ID + user.getId(), sessionid, 3,
                        TimeUnit.DAYS);
                loginRecordFeignClient.create(loginRecord);
                List<LoginRecordDTO> findList =
                        findLoginRecordList(username, null, new Date(date.getTime() - 60000), date,
                                Constants.LOGIN_STATUS_SUCCESS, null);
                // 同一账号 60秒内登陆成功6次
                if (findList != null && findList.size() >= 6) {
                    // 锁定账号
                    UserInfoReq lock = new UserInfoReq();
                    lock.setId(user.getId());
                    lock.setStatus(SysConstant.USER_STATUS_LOCK);
                    userInfoFeignClient.update(lock);
                    errorMessage = "账号锁定，请联系管理员修改！";
                    return new JSONResult<>()
                            .fail(ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(), errorMessage);
                }
                List<LoginRecordDTO> findList2 =
                        findLoginRecordList(username, null, new Date(date.getTime() - 600000), date,
                                Constants.LOGIN_STATUS_SUCCESS, null);
                // 同一账号 10分钟内登陆成功20次
                if (findList2 != null && findList2.size() >= 20) {
                    // 锁定账号
                    UserInfoReq lock = new UserInfoReq();
                    lock.setId(user.getId());
                    lock.setStatus(SysConstant.USER_STATUS_LOCK);
                    userInfoFeignClient.update(lock);
                    errorMessage = "账号锁定，请联系管理员修改！";
                    return new JSONResult<>()
                            .fail(ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(), errorMessage);
                }
                SecurityUtils.getSubject().getSession().setAttribute("wsUrlHttp", wsUrlHttp);
                SecurityUtils.getSubject().getSession().setAttribute("wsUrlHttps", wsUrlHttps);
                SecurityUtils.getSubject().getSession().setAttribute("mqUserName", mqUserName);
                SecurityUtils.getSubject().getSession().setAttribute("mqPassword", mqPassword);
                Map<String, Object> im = imMassageService.transOnlineLeaveLog(user, roleList,
                        SaleOLOperationTypeEnum.LOGIN_TYPE.getCode());

                return new JSONResult<>().success(im);

            } catch (UnknownAccountException uae) {
                errorMessage = "账号没有权限";// 产品要求账号不存在是展示"账号没有权限"
                loginRecord.setLoginStatus(Constants.LOGIN_STATUS_OTHER);
            } catch (IncorrectCredentialsException ise) {
                errorMessage = "登录密码错误，请重新输入";
                loginRecord.setLoginStatus(Constants.LOGIN_STATUS_PASSWORD_ERROR);
            } catch (AuthenticationException ae) {
                errorMessage = "账号没有权限";
                loginRecord.setLoginStatus(Constants.LOGIN_STATUS_OTHER);
            } catch (Exception e) {
                // errorMessage = e.getMessage();
                logger.error("登陆时出现异常！", e);
                errorMessage = "登陆时出现异常";
                loginRecord.setLoginStatus(Constants.LOGIN_STATUS_OTHER);
            }
        }
        // 保存登录记录
        loginRecord.setIsChangeMachine(SysConstant.NO);
        loginRecordFeignClient.create(loginRecord);
        if (Constants.LOGIN_STATUS_PASSWORD_ERROR.equals(loginRecord.getLoginStatus())) {
            // 累计当日错误次数
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String format = simpleDateFormat.format(date);
            String string = redisTemplate.opsForValue()
                    .get(Constants.PASSWORD_ERROR + user.getId() + format);
            if (string != null) {
                int parseInt = Integer.parseInt(string);
                if (parseInt >= 5) {
                    errorMessage = "账号操作错误过多，请忘记密码方式找回";
                } else if (parseInt >= 4) {
                    if ("验证码错误".equals(errorMessage)) {
                        errorMessage = "验证码错误，您还可以尝试1次";
                    } else {
                        errorMessage = "密码错误，您还可以尝试1次";
                    }
                } else if (parseInt >= 3) {
                    if ("验证码错误".equals(errorMessage)) {
                        errorMessage = "验证码错误，您还可以尝试2次";
                    } else {
                        errorMessage = "密码错误，您还可以尝试2次";
                    }
                }
                redisTemplate.opsForValue().set(Constants.PASSWORD_ERROR + user.getId() + format,
                        Integer.toString(parseInt + 1), 1, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set(Constants.PASSWORD_ERROR + user.getId() + format,
                        "1", 1, TimeUnit.DAYS);
            }
            // 判断密码错误次数 是否需要提示验证码
            List<LoginRecordDTO> findList =
                    findLoginRecordList(null, ipAddr, new Date(date.getTime() - 60000), date,
                            Constants.LOGIN_STATUS_PASSWORD_ERROR, null);
            if (findList != null && findList.size() >= 3) {
                redisTemplate.opsForValue().set(Constants.SHOW_CAPTCHA + ipAddr,
                        SysConstant.YES.toString(), 1, TimeUnit.DAYS);
            }
        }
        return new JSONResult<>().fail(ManagerWebErrorCodeEnum.ERR_LOGIN_ERROR.getCode(),
                errorMessage);
    }

    /**
     * 发送短信或语音验证码验证
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login/sendmsg", method = {RequestMethod.POST})
    @ResponseBody
    @LogRecord(description = "获取验证码", operationType = OperationType.PUSH,
            menuName = MenuEnum.GET_CODE)
    public JSONResult sendMsg(@RequestBody LoginReq loginReq, HttpServletRequest request)
            throws Exception {
        String msg = "";
        // 当前访问http的IP地址，获取真实的IP，越过各种代理
        String ip = CommonUtil.getIpAddr(request);
        if (StringUtils.isNotBlank(loginReq.getCode())) {
            String value =
                    (String) SecurityUtils.getSubject().getSession().getAttribute("captchaCode");
            if (value != null) {
                String string = redisTemplate.opsForValue().get(Constants.CAPTCHA_CODE + value);
                if (string != null) {
                    if (loginReq.getCode().equals(string)) {
                        // 输入正确后下次不再需要图形验证码
                        redisTemplate.opsForValue().set(Constants.SHOW_CAPTCHA + ip,
                                SysConstant.NO.toString(), 1, TimeUnit.DAYS);
                    } else {
                        return new JSONResult<>().fail(
                                ManagerWebErrorCodeEnum.ERR_CODE_ERROR.getCode(),
                                ManagerWebErrorCodeEnum.ERR_CODE_ERROR.getMessage());
                    }
                } else {
                    return new JSONResult<>().fail(
                            ManagerWebErrorCodeEnum.ERR_CODE_TIMEOUT.getCode(),
                            ManagerWebErrorCodeEnum.ERR_CODE_TIMEOUT.getMessage());
                }
            }
        } else {
            String string = redisTemplate.opsForValue().get(Constants.SHOW_CAPTCHA + ip);
            // 判断是否需要图形验证码
            if (SysConstant.YES.toString().equals(string)) {
                return new JSONResult<>().fail(ManagerWebErrorCodeEnum.ERR_CODE_NULL.getCode(),
                        ManagerWebErrorCodeEnum.ERR_CODE_NULL.getMessage());
            }
        }

        UserInfoReq userInfoReq = new UserInfoReq();
        userInfoReq.setUsername(loginReq.getUsername());
        // 查询用户信息
        JSONResult<UserInfoDTO> getbyUserName = userInfoFeignClient.getbyUserName(userInfoReq);
        if (getbyUserName.getData() == null) {
            msg = "登录用户名未注册";
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(), msg);
        }
        UserInfoDTO user = getbyUserName.getData();
        if (SysConstant.USER_STATUS_LOCK.equals(user.getStatus())) {
            msg = "账号锁定，请联系管理员！";
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(), msg);
        }
        if (SysConstant.USER_STATUS_DISABLE.equals(user.getStatus())) {
            msg = "帐号已禁用，请联系管理员！";
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(), msg);
        }
        if (!user.getPassword().equals(MD5Util
                .StringToMd5(MD5Util.StringToMd5(loginReq.getPassword() + user.getSalt())))) {
            msg = "登录密码错误，请重新输入";
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(), msg);
        }
        return send(loginReq.getType(), user);

    }

    /**
     * 发送短信或语音验证码验证
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login/sendmsgPwd", method = {RequestMethod.POST})
    @ResponseBody
    @LogRecord(description = "获取验证码", operationType = OperationType.PUSH,
            menuName = MenuEnum.GET_CODE)
    public JSONResult sendmsgPwd(@RequestBody LoginReq loginReq, HttpServletRequest request)
            throws Exception {
        String msg = "";

        UserInfoReq userInfoReq = new UserInfoReq();
        userInfoReq.setUsername(loginReq.getUsername());
        // 查询用户信息
        JSONResult<UserInfoDTO> getbyUserName = userInfoFeignClient.getbyUserName(userInfoReq);
        if (getbyUserName.getData() == null) {
            msg = "登录用户名未注册";
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(), msg);
        }
        UserInfoDTO user = getbyUserName.getData();
        if (SysConstant.USER_STATUS_LOCK.equals(user.getStatus())) {
            msg = "帐号锁定，请联系管理员。不允许再继续进行找回密码的操作啦。";
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(), msg);
        }
        if (SysConstant.USER_STATUS_DISABLE.equals(user.getStatus())) {
            msg = "帐号已禁用，请联系管理员。不允许再继续进行找回密码的操作啦。";
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(), msg);
        }
        // 发送验证码
        return send(loginReq.getType(), user);

    }

    /**
     * 忘记密码
     *
     * @return
     */
    @PostMapping("/login/updatePassword")
    @ResponseBody
    @LogRecord(description = "修改密码", operationType = OperationType.UPDATE,
            menuName = MenuEnum.UPDATE_PASSWORD)
    public JSONResult updateMenu(@Valid @RequestBody UpdateUserPasswordReq updateUserPasswordReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoReq req = new UserInfoReq();
        req.setUsername(updateUserPasswordReq.getUsername());
        // 查询用户信息
        JSONResult<UserInfoDTO> jsonResult = userInfoFeignClient.getbyUserName(req);

        UserInfoDTO data = jsonResult.getData();
        if (JSONResult.SUCCESS.equals(jsonResult.getCode()) && data != null) {
            // * 校验验证码
            SmsCodeAndMobileValidReq smsCodeAndMobileValidReq = new SmsCodeAndMobileValidReq();
            smsCodeAndMobileValidReq.setCode(updateUserPasswordReq.getCode());
            smsCodeAndMobileValidReq.setMobile(data.getPhone());
            String msgId = redisTemplate.opsForValue().get(Constants.MSG_ID + data.getId());
            smsCodeAndMobileValidReq.setMsgId(msgId);
            JSONResult validCodeAndMobile =
                    msgPushFeignClient.validCodeAndMobile(smsCodeAndMobileValidReq);
            if (!JSONResult.SUCCESS.equals(validCodeAndMobile.getCode())) {
                return new JSONResult<>().fail(ManagerWebErrorCodeEnum.ERR_CODE_ERROR.getCode(),
                        ManagerWebErrorCodeEnum.ERR_CODE_ERROR.getMessage());
            }
            UserInfoReq userInfoReq = new UserInfoReq();
            userInfoReq.setId(data.getId());
            userInfoReq.setPassword(updateUserPasswordReq.getOldPassword());
            // 修改密码
            return userInfoFeignClient.update(userInfoReq);
        } else {
            return new JSONResult().fail(UserErrorCodeEnum.ERR_PHONE_NOT_REGIST.getCode(),
                    UserErrorCodeEnum.ERR_PHONE_NOT_REGIST.getMessage());
        }

    }

    /**
     * 获取验证码图片
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login/getCaptcha", method = RequestMethod.GET,
            produces = MediaType.IMAGE_PNG_VALUE)
    public ModelAndView getCaptcha(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        String capText = captchaProducer.createText();
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(Constants.CAPTCHA_CODE + uuid, capText, 60 * 5,
                TimeUnit.SECONDS);
        SecurityUtils.getSubject().getSession().setAttribute("captchaCode", uuid);

        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }

    @RequestMapping("/index/logout")
    @LogRecord(description = "退出登录", operationType = OperationType.LOGINOUT,
            menuName = MenuEnum.LOGINOUT)
    public String logout(String type, Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        Object attribute = subject.getSession().getAttribute("user");
        UserInfoDTO user = null;
        if (attribute != null && attribute instanceof UserInfoDTO) {

            user = (UserInfoDTO) subject.getSession().getAttribute("user");
        }
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        if (user != null && !"1".equals(type)) {
            // 退出成功，保存退出状态
            UserInfoReq update = new UserInfoReq();
            update.setId(user.getId());
            update.setIsLogin(Constants.IS_LOGIN_DOWN);
            userInfoFeignClient.update(update);
            if ("3".equals(type)) {
                subject.getSession().setAttribute("isShowLogoutBox", type);
            }
        } else {
            subject.getSession().setAttribute("isShowLogoutBox", type);
        }
        return "redirect:/login";
    }

    /**
     * 查询登录记录
     *
     * @param username
     * @param ipAddr
     * @param startTime
     * @param endTime
     * @param loginStatus
     * @param isChangeMachine
     * @return
     */
    private List<LoginRecordDTO> findLoginRecordList(String username, String ipAddr, Date startTime,
            Date endTime, Integer loginStatus, Integer isChangeMachine) {
        LoginRecordReq loginRecordVo = new LoginRecordReq();
        loginRecordVo.setUsername(username);
        loginRecordVo.setIp(ipAddr);
        loginRecordVo.setEndTime(endTime);
        loginRecordVo.setStartTime(startTime);
        loginRecordVo.setLoginStatus(loginStatus);
        loginRecordVo.setIsChangeMachine(isChangeMachine);
        JSONResult<List<LoginRecordDTO>> list = loginRecordFeignClient.list(loginRecordVo);
        if (list != null && JSONResult.SUCCESS.equals(list.getCode())) {
            return list.getData();
        }
        return null;
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
     * 发送验证码
     *
     * @return
     */
    private JSONResult<String> send(String type, UserInfoDTO user) {
        JSONResult<String> jsonResult = null;
        if ("1".equals(type)) {
            // 发送短信验证码
            SmsCodeSendReq smsCodeSendReq = new SmsCodeSendReq();
            smsCodeSendReq.setMobile(user.getPhone());
            jsonResult = msgPushFeignClient.sendCode(smsCodeSendReq);
            // msg = "发送短信验证成功";
        } else {
            // 发送语音验证码
            SmsVoiceCodeReq voiceCodeReq = new SmsVoiceCodeReq();
            voiceCodeReq.setMobile(user.getPhone());
            voiceCodeReq.setTtl(60);
            jsonResult = msgPushFeignClient.sendVoiceCode(voiceCodeReq);
            // msg = "请注意接听语音来电获取验证码！";
        }
        if (JSONResult.SUCCESS.equals(jsonResult.getCode())) {
            redisTemplate.opsForValue().set(Constants.MSG_ID + user.getId(), jsonResult.getData(),
                    60 * 5, TimeUnit.SECONDS);
        }
        return jsonResult;
    }


    /**
     * 是否提醒修改密码
     *
     * @return
     */
    private boolean isRepwdNotify(UserInfoDTO loginUser, long pwdTime) {
        SysSettingReq sysSettingReq = new SysSettingReq();
        sysSettingReq.setCode(SysConstant.REMINDER_TIME);
        JSONResult<SysSettingDTO> byCode = sysSettingFeignClient.getByCode(sysSettingReq);
        if (byCode != null && JSONResult.SUCCESS.equals(byCode.getCode())) {
            String value = byCode.getData().getValue();
            if (StringUtils.isNotBlank(value)) {
                String[] dayArr = value.split(",");
                List<String> dayList = Arrays.asList(dayArr);
                int differentDays =
                        DateUtil.differentDays(loginUser.getResetPasswordTime(), new Date());
                if (dayList.contains(String.valueOf(pwdTime - differentDays))) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * 删除用户缓存信息(防止非正常登录事session存在导致权限不刷新)
     *
     *
     *
     * @author fanjd 2019/05/9 20:05:08
     * @param userId 当前登陆用户id
     */
    private void kickOutUser(Long userId) {
        // 处理session
        DefaultWebSecurityManager securityManager =
                (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();

        DefaultWebSessionManager sessionManager =
                (DefaultWebSessionManager) securityManager.getSessionManager();

        // 从redis获取sessionId
        String sessionId = redisTemplate.opsForValue().get(Constants.SESSION_ID + userId);
        if (StringUtils.isBlank(sessionId)) {
            return;
        }
        RedisSessionDAO redisSessionDAO = (RedisSessionDAO) sessionManager.getSessionDAO();
        if (null == redisSessionDAO) {
            return;
        }
        String keyPrefix = redisSessionDAO.getKeyPrefix();
        try {
            byte[] key = redisSessionDAO.getKeySerializer().serialize(keyPrefix + sessionId);
            Session session = (Session) redisSessionDAO.getValueSerializer()
                    .deserialize(redisSessionDAO.getRedisManager().get(key));
            if (null == session) {
                return;
            }
            Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (attribute == null) {
                return;
            }
            Authenticator authc = securityManager.getAuthenticator();
            // 删除cache，登录成功后重新授权
            ((LogoutAware) authc).onLogout((PrincipalCollection) attribute);

        } catch (SerializationException e) {
            e.printStackTrace();
            logger.error("清空用户权限出现异常,用户id:{},异常堆栈:{}", userId, e);
        }

    }
}
