package com.kuaidao.manageweb.controller.homepage;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kuaidao.aggregation.dto.deptcallset.DeptCallSetRespDTO;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.deptcallset.DeptCallSetFeignClient;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

@Controller
@RequestMapping("/homePage")
public class HomePageController {
    
    private static Logger logger = LoggerFactory.getLogger(HomePageController.class);

    @Value("${ws_url_http}")
    private String wsUrlHttp;
    @Value("${spring.rabbitmq.username}")
    private String mqUserName;
    @Value("${spring.rabbitmq.password}")
    private String mqPassword;
    @Value("${ws_url_https}")
    private String wsUrlHttps;
    
    @Autowired
    DeptCallSetFeignClient deptCallSetFeignClient;

    /**
     * 首页 跳转
     * @return
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        request.setAttribute("user", user);
        List<IndexModuleDTO> menuList = user.getMenuList();
        request.setAttribute("menuList",menuList);
        request.setAttribute("wsUrlHttp", wsUrlHttp);
        request.setAttribute("wsUrlHttps", wsUrlHttps);
        request.setAttribute("mqUserName", mqUserName);
        request.setAttribute("mqPassword", mqPassword);
        request.setAttribute("userId", user.getId());
        
        Long orgId = user.getOrgId();
        if(orgId!=null) {
            IdEntity idEntity = new IdEntity(orgId+"");
            JSONResult<DeptCallSetRespDTO>  clientOrg= deptCallSetFeignClient.queryByOrgid(idEntity);
            if(clientOrg==null || !JSONResult.SUCCESS.equals(clientOrg.getCode()) || clientOrg.getData()==null) {
                logger.error("query client setting by orgId,param{{}},res{{}}",orgId,clientOrg);
            }else {
                DeptCallSetRespDTO data = clientOrg.getData();
                request.setAttribute("enterpriseId",data.getCallCenterNo());
                request.setAttribute("token",data.getToken());
            }
            
        }
        
       return "index";
    }

}
