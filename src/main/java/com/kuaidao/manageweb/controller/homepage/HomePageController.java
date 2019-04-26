package com.kuaidao.manageweb.controller.homepage;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.kuaidao.aggregation.constant.AggregationConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.kuaidao.aggregation.dto.deptcallset.DeptCallSetRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.deptcallset.DeptCallSetFeignClient;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
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
     * 
     * @return
     */
    @RequestMapping("/index")
    public String index(@RequestParam(required = false) String isUpdatePassword,
            HttpServletRequest request) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        
        UserInfoDTO userInfoRespDTO = new UserInfoDTO();
        userInfoRespDTO.setId(user.getId());
        userInfoRespDTO.setName(user.getName());
        userInfoRespDTO.setOrgId(user.getOrgId());
        request.setAttribute("user", userInfoRespDTO);
        
        List<IndexModuleDTO> menuList = user.getMenuList();
        request.setAttribute("menuList", menuList);
        request.setAttribute("isUpdatePassword", isUpdatePassword);
        request.setAttribute("wsUrlHttp", wsUrlHttp);
        request.setAttribute("wsUrlHttps", wsUrlHttps);
        request.setAttribute("mqUserName", mqUserName);
        request.setAttribute("mqPassword", mqPassword);
        request.setAttribute("userId", user.getId());
//        RoleInfoDTO roleInfoDTO1 = user.getRoleList().get(0);
//        if(roleInfoDTO1!=null){
//            if(roleInfoDTO1.getRoleCode().equals(RoleCodeEnum.HWY.name())||roleInfoDTO1.getRoleCode().equals(RoleCodeEnum.XXLY.name())){
//                request.setAttribute("accountType", AggregationConstant.YES);
//            }else{
//                request.setAttribute("accountType", AggregationConstant.NO);
//            }
//        }
        Long orgId = user.getOrgId();
        if (orgId != null) {
            IdEntity idEntity = new IdEntity(orgId + "");
            JSONResult<DeptCallSetRespDTO> clientOrg =
                    deptCallSetFeignClient.queryByOrgid(idEntity);
            if (clientOrg == null || !JSONResult.SUCCESS.equals(clientOrg.getCode())
                    || clientOrg.getData() == null) {
                logger.error("query client setting by orgId,param{{}},res{{}}", orgId, clientOrg);
            } else {
                DeptCallSetRespDTO data = clientOrg.getData();
                request.setAttribute("enterpriseId", data.getCallCenterNo());
                request.setAttribute("token", data.getToken());
            }

        }
      //判断是否显示控制台按钮
        List<RoleInfoDTO> roleList = user.getRoleList();
        boolean isShowConsoleBtn = false;
        if(CollectionUtils.isNotEmpty(roleList)) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            String roleCode = roleInfoDTO.getRoleCode();
           if(RoleCodeEnum.DXCYGW.name().equals(roleCode) || RoleCodeEnum.DXZJ.name().equals(roleCode)
                   || RoleCodeEnum.SWJL.name().equals(roleCode) ||RoleCodeEnum.SWZJ.name().equals(roleCode)) {
               //电销顾问 电销总监 商务经理 商务总监
               isShowConsoleBtn = true;
           }
        }
        request.setAttribute("isShowConsoleBtn", isShowConsoleBtn);
        request.setAttribute("accountType",2);

        return "index";
    }

}
