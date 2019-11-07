package com.kuaidao.manageweb.util;

import java.util.List;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * 公共方法
 * @author: Chen Chengxue
 * @date: 2019年1月2日 下午1:44:31   
 * @version V1.0
 */
public class CommUtil {

    
    /**
     * 获取登录后 放在shiro 中的user 对象
     * @return
     */
    public static UserInfoDTO getCurLoginUser() {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if(user==null){
            user = new UserInfoDTO();
            user.setId(1179948058881024000L);
            user.setUserType(2);
        }
        return user;
    }
    
    /**
     * 获取当前登陆者 角色code
    * @param userInfoDTO
    * @return
     */
    public static String getRoleCode(UserInfoDTO curLoginUser) {
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        return roleInfoDTO.getRoleCode();
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
