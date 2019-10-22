package com.kuaidao.manageweb.handler;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {


    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("GlobalExceptionHandler",e);
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = ex.getBindingResult();
           String msg = handlerErrors(bindingResult);
           e = new Exception(msg);

        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            BindingResult bindingResult = ex.getBindingResult();
            String msg = handlerErrors(bindingResult);
            e = new Exception(msg);
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }
    /**
     * 拦截捕捉权限验证异常 UnauthorizedException.class
     * @param e
     * @return
     */
    @ExceptionHandler(value = UnauthorizedException.class)
    public ModelAndView UnauthorizedExceptionErrorHandler(HttpServletRequest req,UnauthorizedException e) {
        logger.error("GlobalExceptionHandler",e);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error/authen_fail");
        return mav;
    }
    /**
     * 处理异常信息
     *
     * @param bindingResult
     * @return
     */
    private String handlerErrors(BindingResult bindingResult) {
        List<ObjectError> errors = bindingResult.getAllErrors();
        ObjectError error = errors.get(0);
        String msg = error.getDefaultMessage();
        return msg;
    }

}
