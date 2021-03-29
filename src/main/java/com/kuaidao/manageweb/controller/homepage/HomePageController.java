package com.kuaidao.manageweb.controller.homepage;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.kuaidao.common.constant.BusinessLineConstant;
import com.kuaidao.common.constant.MenuEnum;
import com.kuaidao.sys.dto.module.ModuleInfoDTO;
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
import com.kuaidao.common.constant.StageContant;
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

    // 汇聚-商家端 域名：用来判断首页跳转
    @Value("${merchantServletName}")
    private String merchantServletName;
    @Value("${dataBaseUrl}")
    private String dataBaseUrl;


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
        userInfoRespDTO.setMerchantIcon(user.getMerchantIcon());
        request.setAttribute("user", userInfoRespDTO);

        List<IndexModuleDTO> menuList = user.getMenuList();
        if(CollectionUtils.isNotEmpty(menuList)){
            menuList = menuList.stream().filter(a->{
                if((a.getId()!=null&&a.getId()==15) || (a.getParentId()!=null&&  a.getParentId()==15)){
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            //小物种业务线电销相关角色不允许查看公有池，add by guoruiling 2021/3/29
            if(null != user.getBusinessLine() && BusinessLineConstant.XIAOWUZHONG==user.getBusinessLine()){
                menuList.forEach(m->{
                    //电销管理所有菜单
                    if (MenuEnum.TEL_SALE_MANAGE.getName().equals(m.getName())){
                        List<ModuleInfoDTO> subMenus = m.getSubList();
                        subMenus = subMenus.stream().filter(s->{
                            //判断如果是公有池菜单则去除
                            if (MenuEnum.TM_PUBLIC_POOL.getName().equals(s.getName())) {
                                return false;
                            }
                            return true;
                        }).collect(Collectors.toList());
                        m.setSubList(subMenus);
                    }
                });
            }
        }
        request.setAttribute("menuList", menuList);
        request.setAttribute("isUpdatePassword", isUpdatePassword);
        request.setAttribute("wsUrlHttp", wsUrlHttp);
        request.setAttribute("wsUrlHttps", wsUrlHttps);
        request.setAttribute("mqUserName", mqUserName);
        request.setAttribute("mqPassword", mqPassword);
        request.setAttribute("userId", user.getId());
        RoleInfoDTO roleInfoDTO1 = user.getRoleList().get(0);
        if (roleInfoDTO1 != null) {
            if (roleInfoDTO1.getRoleCode().equals(RoleCodeEnum.HWY.name())
                    || roleInfoDTO1.getRoleCode().equals(RoleCodeEnum.XXLY.name())
                    || RoleCodeEnum.HWZG.name().equals(roleInfoDTO1.getRoleCode())
                    || RoleCodeEnum.HWJL.name().equals(roleInfoDTO1.getRoleCode())) {
                request.setAttribute("accountType", StageContant.STAGE_PHONE_TRAFFIC);
            } else {
                request.setAttribute("accountType", StageContant.STAGE_TELE);
            }
        }
        Long orgId = user.getOrgId();
        if (orgId != null) {
            IdEntity idEntity = new IdEntity(orgId + "");
            JSONResult<DeptCallSetRespDTO> clientOrg =
                    deptCallSetFeignClient.queryByOrgid(idEntity);
            if (clientOrg == null || !JSONResult.SUCCESS.equals(clientOrg.getCode())
                    || clientOrg.getData() == null) {
                logger.warn("query client setting by orgId,param{{}},res{{}}", orgId, clientOrg);
            } else {
                DeptCallSetRespDTO data = clientOrg.getData();
                request.setAttribute("enterpriseId", data.getCallCenterNo());
                request.setAttribute("token", data.getToken());
            }

        }

        // 判断是否显示控制台按钮
        List<RoleInfoDTO> roleList = user.getRoleList();
        boolean isShowConsoleBtn = false;
        String roleCode = "";
        if (CollectionUtils.isNotEmpty(roleList)) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            roleCode = roleInfoDTO.getRoleCode();
            if (RoleCodeEnum.DXCYGW.name().equals(roleCode)
                    || RoleCodeEnum.DXZJ.name().equals(roleCode)
                    || RoleCodeEnum.SWJL.name().equals(roleCode)
                    || RoleCodeEnum.SWZJ.name().equals(roleCode)) {
                // 电销顾问 电销总监 商务经理 商务总监
                isShowConsoleBtn = true;
            }
        }
        request.setAttribute("isShowConsoleBtn", isShowConsoleBtn);
        boolean isShowDataBase = false;
        if((user.getBusinessLine() !=null && (user.getBusinessLine() == BusinessLineConstant.SHANGJI || user.getBusinessLine() == BusinessLineConstant.SHCS || user.getBusinessLine() == BusinessLineConstant.CMZSJJ)) || roleCode.equals(RoleCodeEnum.GLY.name()) ){
            isShowDataBase = true;
        }
        request.setAttribute("isShowDataBase", isShowDataBase);
        request.setAttribute("dataBaseUrl", dataBaseUrl);
        return "index";
    }


}
