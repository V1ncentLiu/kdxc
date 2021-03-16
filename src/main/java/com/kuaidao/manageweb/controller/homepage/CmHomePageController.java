package com.kuaidao.manageweb.controller.homepage;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.kuaidao.manageweb.constant.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kuaidao.aggregation.dto.deptcallset.DeptCallSetRespDTO;
import com.kuaidao.common.constant.BusinessLineConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.StageContant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.deptcallset.DeptCallSetFeignClient;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

@Controller
@RequestMapping("/cm/homePage")
public class CmHomePageController {

    private static Logger logger = LoggerFactory.getLogger(CmHomePageController.class);

    /** 餐盟登录我知道了弹窗是否展示 -1 展示 **/
    private static final Integer SHOW_IKONW_FLAG_YES = 1;
    /** 餐盟登录我知道了弹窗是否展示 -0 不展示 **/
    private static final Integer SHOW_IKONW_FLAG_NO = 0;
    /** 餐盟电销顾问登录次数 **/
    private static final Long CM_DXGW_LOGIN_NUM = 1L;

    @Value("${ws_url_http}")
    private String wsUrlHttp;
    @Value("${spring.rabbitmq.username}")
    private String mqUserName;
    @Value("${spring.rabbitmq.password}")
    private String mqPassword;
    @Value("${ws_url_https}")
    private String wsUrlHttps;
    @Value("${dataBaseUrl}")
    private String dataBaseUrl;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    DeptCallSetFeignClient deptCallSetFeignClient;

    /**
     * 首页 跳转
     * @return
     */
    @RequestMapping("/index")
    public String index(@RequestParam(required = false) String isUpdatePassword, HttpServletRequest request) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        RoleInfoDTO roleInfoDTO1 = user.getRoleList().get(0);
        UserInfoDTO userInfoRespDTO = new UserInfoDTO();
        userInfoRespDTO.setId(user.getId());
        userInfoRespDTO.setName(user.getName());
        userInfoRespDTO.setOrgId(user.getOrgId());
        userInfoRespDTO.setMerchantIcon(user.getMerchantIcon());
        userInfoRespDTO.setRoleCode(roleInfoDTO1.getRoleCode());
        request.setAttribute("user", userInfoRespDTO);

        List<IndexModuleDTO> menuList = user.getMenuList();
        request.setAttribute("menuList", menuList);
        request.setAttribute("isUpdatePassword", isUpdatePassword);
        request.setAttribute("wsUrlHttp", wsUrlHttp);
        request.setAttribute("wsUrlHttps", wsUrlHttps);
        request.setAttribute("mqUserName", mqUserName);
        request.setAttribute("mqPassword", mqPassword);
        request.setAttribute("userId", user.getId());

        if (roleInfoDTO1 != null) {
            request.setAttribute("accountType", StageContant.STAGE_TELE);
        }
        Long orgId = user.getOrgId();
        if (orgId != null) {
            IdEntity idEntity = new IdEntity(orgId + "");
            JSONResult<DeptCallSetRespDTO> clientOrg = deptCallSetFeignClient.queryByOrgid(idEntity);
            if (clientOrg == null || !JSONResult.SUCCESS.equals(clientOrg.getCode()) || clientOrg.getData() == null) {
                logger.warn("query client setting by orgId,param{{}},res{{}}", orgId, clientOrg);
            } else {
                DeptCallSetRespDTO data = clientOrg.getData();
                request.setAttribute("enterpriseId", data.getCallCenterNo());
                request.setAttribute("token", data.getToken());
            }

        }
        //默认不展示我知道了弹框
        Integer showIKonwFlag = SHOW_IKONW_FLAG_NO;
        // 判断是否显示控制台按钮
        List<RoleInfoDTO> roleList = user.getRoleList();
        boolean isShowConsoleBtn = false;
        String roleCode = "";
        if (CollectionUtils.isNotEmpty(roleList)) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            roleCode = roleInfoDTO.getRoleCode();
            if (RoleCodeEnum.DXCYGW.name().equals(roleCode) || RoleCodeEnum.SWJL.name().equals(roleCode)) {
                // 电销顾问 电销总监 商务经理 商务总监
                isShowConsoleBtn = true;
            }
            //顾问首次登录展示我知道了弹框
            if (RoleCodeEnum.DXCYGW.name().equals(roleCode)) {
                Long loginNum = redisTemplate.opsForValue().increment(Constants.CM_DXGW_FIRST_LOGIN + user.getId());
                if (CM_DXGW_LOGIN_NUM.equals(loginNum)) {
                    showIKonwFlag = SHOW_IKONW_FLAG_YES;
                }
            }
        }
        request.setAttribute("showIKonwFlag", showIKonwFlag);
        request.setAttribute("isShowConsoleBtn", isShowConsoleBtn);
        boolean isShowDataBase = false;
        if((user.getBusinessLine() !=null && (user.getBusinessLine() == BusinessLineConstant.SHANGJI || user.getBusinessLine() == BusinessLineConstant.SHCS || user.getBusinessLine() == BusinessLineConstant.CMZSJJ)) || roleCode.equals(RoleCodeEnum.GLY.name()) ){
            isShowDataBase = true;
        }
        request.setAttribute("isShowDataBase", isShowDataBase);
        request.setAttribute("dataBaseUrl", dataBaseUrl);



        return "unionIndex";
    }

}
