package com.kuaidao.manageweb.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.logmgt.dto.AccessLogReqDTO;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.service.LogService;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * 
 * @Description: 访问日志记录 ,存到mongo
 * @author: Chen Chengxue
 * @date: 2018年8月10日 上午9:20:39
 * @version V1.0
 */
@Component
@Aspect
public class AccesssLogRecordAop {
    private static final String FAIL_CODE = "10001";

    @Autowired
    HttpServletRequest request;

    @Autowired
    LogService logService;

    @Pointcut("execution(public * com.kuaidao.manageweb.controller..*.*(..))")
    public void pointCut() {}

    @Pointcut("@annotation(com.kuaidao.manageweb.config.LogRecord)")
    public void logCut() {}

    /**
     * 
     * @Description:只有标记了@LogRecord 注解的方法才会进入 @throws
     */
    @Around("pointCut() && logCut() ")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object obj = null;
        AccessLogReqDTO logRecord = new AccessLogReqDTO();
        logRecord.setReqStartTime(DateUtil.convert2String(new Date(), DateUtil.ymdhms));
        logRecord.setCreateTime(new Date());
        try {
            // 获取方法上的注解
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            Method method = signature.getMethod();
            // 注解中参数
            getLogAnnoParam(logRecord, method);
            // 请求中参数
            if (!(OperationType.LOGIN.toString()).equals(logRecord.getOperationType())) {
                getRequestParam(logRecord, request);
            }
            Object[] args = pjp.getArgs();
            logRecord.setRequestParam(Arrays.toString(args));
            // 执行方法
            obj = pjp.proceed();
            // 响应数据
            if ((OperationType.LOGIN.toString()).equals(logRecord.getOperationType())) {
                getRequestParam(logRecord, request);
            }
            getRespParam(logRecord, obj);
            if ("0".equals(logRecord.getResCode())) {
                if ((OperationType.LOGIN.toString()).equals(logRecord.getOperationType())) {
                    logRecord.setContent("登录成功");
                }
                if ((OperationType.LOGINOUT.toString()).equals(logRecord.getOperationType())) {
                    logRecord.setContent("退出成功");
                }
            } else {
                if ((OperationType.LOGIN.toString()).equals(logRecord.getOperationType())) {
                    logRecord.setContent("登录失败");
                }

            }
            logRecord.setReqEndTime(DateUtil.convert2String(new Date(), DateUtil.ymdhms));
        } catch (Throwable throwable) {
            logRecord.setResCode(FAIL_CODE);
            logRecord.setErrMsg(ExceptionUtils.getStackTrace(throwable));
            logRecord.setReqEndTime(DateUtil.convert2String(new Date(), DateUtil.ymdhms));
            asyncInsertLog(logRecord);
            throw throwable;
        }
        asyncInsertLog(logRecord);
        return obj;
    }

    /*
     * 异步记录日志
     */
    private void asyncInsertLog(AccessLogReqDTO logRecord) {
        logService.insertLogRecord(logRecord);
    }

    /**
     * 获取注解上的参数
     */
    private void getLogAnnoParam(AccessLogReqDTO logRecord, Method method) {
        LogRecord logAnno = method.getAnnotation(LogRecord.class);
        OperationType operationType = logAnno.operationType();
        if (operationType != null) {
            logRecord.setOperationType(operationType.name());
        }
        String description = logAnno.description();
        if (StringUtils.isNotBlank(description)) {
            if (description.trim().length() > 50) {
                description = description.substring(0, 50);
            }
            logRecord.setDescription(description);
        }
        MenuEnum menuName = logAnno.menuName();
        logRecord.setMenuName(menuName.getName());
    }

    /**
     * 获取请求中参数
     */
    private void getRequestParam(AccessLogReqDTO logRecord, HttpServletRequest request) {
        // get post 请求
        String reqMethod = request.getMethod();
        logRecord.setReqeuestType(reqMethod);
        UserInfoDTO user =
                (UserInfoDTO) SecurityUtils.getSubject().getSession().getAttribute("user");
        if (user != null) {
            logRecord.setUserName(user.getUsername());
            logRecord.setName(user.getName());
            logRecord.setPhone(user.getPhone());
            logRecord.setRequestURL(request.getRequestURI());
            logRecord.setRemoteIP(CommonUtil.getIpAddr(request));
        }
    }

    /**
     * 获取响应中数据
     */
    private void getRespParam(AccessLogReqDTO logRecord, Object obj) {
        if (obj instanceof JSONResult) {
            String code = ((JSONResult) obj).getCode();
            logRecord.setResCode(code);
            String msg = ((JSONResult) obj).getMsg();
            logRecord.setErrMsg(msg);
        }
    }
}
