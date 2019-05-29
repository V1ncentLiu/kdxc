package com.kuaidao.manageweb.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公共方法
 * @author: Chen Chengxue
 * @date: 2019年1月2日 下午1:44:31   
 * @version V1.0
 */
public class CommUtil {

    private static Logger logger = LoggerFactory.getLogger(CommUtil.class);
    
    /**
     * 获取登录后 放在shiro 中的user 对象
     * @return
     */
    public static UserInfoDTO getCurLoginUser() {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        logger.info("当前登录人：{}",user);
        return user;
    }
    
    /**
     * 把 Integer 为null 转为 0
    * @param source
    * @return
     */
    public static Integer nullIntegerToZero(Integer source) {
        if(source==null) {
            return 0;
        }
        return source;
    }
    
    
}
