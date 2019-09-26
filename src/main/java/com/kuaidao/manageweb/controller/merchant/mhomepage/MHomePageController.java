package com.kuaidao.manageweb.controller.merchant.mhomepage;

import com.kuaidao.aggregation.dto.deptcallset.DeptCallSetRespDTO;
import com.kuaidao.common.constant.ComConstant;
import com.kuaidao.common.constant.ComConstant.UserStatus;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.StageContant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.deptcallset.DeptCallSetFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.ClueManagementFeignClient;
import com.kuaidao.manageweb.feign.merchant.publiccustomer.PubcustomerFeignClient;
import com.kuaidao.manageweb.feign.merchant.rule.RuleAssignRecordFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;
import com.kuaidao.merchant.dto.index.IndexReqDTO;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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

@Controller
@RequestMapping("/mhomePage")
public class MHomePageController {

    private static Logger logger = LoggerFactory.getLogger(
        MHomePageController.class);

    @Value("${ws_url_http}")
    private String wsUrlHttp;
    @Value("${spring.rabbitmq.username}")
    private String mqUserName;
    @Value("${spring.rabbitmq.password}")
    private String mqPassword;
    @Value("${ws_url_https}")
    private String wsUrlHttps;

    @Autowired
    private RuleAssignRecordFeignClient ruleAssignRecordFeignClient;

    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;

    @Autowired
    private ClueManagementFeignClient clueManagementFeignClient;

    @Autowired
    private PubcustomerFeignClient pubcustomerFeignClient;

    /**
     * 首页 跳转
     *
     * @return
     */
    @RequestMapping("/merchantIndex")
    public String merchantIndex(@RequestParam(required = false) String isUpdatePassword,
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
        // 判断显示主/子账户首页
        Integer userType = user.getUserType();
        request.setAttribute("isShowConsoleBtn", userType); // 主账户==2  子账户==3
        return "index"; // 需要修改成对应的正确地址
    }

    /***
     * 跳转控制台页面
     * @return
     */
    @RequestMapping("/index")
    public String index(String type, HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String username = curLoginUser.getUsername();
        Integer userType = curLoginUser.getUserType();
        String path = "";
        if(Constants.USER_TYPE_TWO.equals(userType)){
            // 查询账户余额
            request.setAttribute("countBlance","111.1"); // 代码合并后，补上代码
            // 查询是否购买套餐
            request.setAttribute("buyedFlag",1); // 当前无法进行。在第四批需求
            path = "console/consoleBusinessMajordomo"; // 跳转主账户首页
        }else{
            path = "console/consoleBusinessMajordomo"; // 跳转子账户首页
        }
        request.setAttribute("merchantName",username);
        return  path;
    }

    private void countSource(){
        // 主账户相关统计
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<Long> subIds = new ArrayList<>();
        // 获取主账号分发相关
       JSONResult<ResourceStatisticsDto> assignDto = null;
        if (SysConstant.USER_TYPE_TWO.equals(curLoginUser.getUserType())) {
            IdEntityLong reqDto = new IdEntityLong();
            reqDto.setId(curLoginUser.getId());
            assignDto = ruleAssignRecordFeignClient
                .countAssginNum(reqDto);
            subIds = merchantUserList(curLoginUser);
        }

        // 查询子账号信息
        if (SysConstant.USER_TYPE_THREE.equals(curLoginUser.getUserType())) {
            IdEntityLong reqDto = new IdEntityLong();
            reqDto.setId(curLoginUser.getId());
            // 获取子账号分发相关
             assignDto = clueManagementFeignClient.getAssignResourceStatistics(reqDto);
            // 子账号id
            subIds.add(curLoginUser.getId());
        }

      // 获取分发
      if (null != assignDto && assignDto.getCode().equals(JSONResult.SUCCESS)) {
        // 今日分发资源
        assignDto.getData().getTodayAssignClueNum();
        // 累计分发
        assignDto.getData().getTotalAssignClueNum();
        // 本月分发资源

      }

      // 获取领取相关
      if (CollectionUtils.isNotEmpty(subIds)) {
        IdListLongReq ids = new IdListLongReq();
        ids.setIdList(subIds);
        //主账号也需要将自己领取的查出来
        if (SysConstant.USER_TYPE_TWO.equals(curLoginUser.getUserType())) {
          subIds.add(curLoginUser.getId());
        }
        JSONResult<ResourceStatisticsDto> receiveResourceList = pubcustomerFeignClient.getReceiveResourceStatistics(ids);
        if (receiveResourceList.getCode().equals(JSONResult.SUCCESS)) {
          ResourceStatisticsDto receiveResource = receiveResourceList.getData();
          // 今日领取资源
          receiveResource.getTodayReceiveClueNum();
          // 累计领取资源
          receiveResource.getTotalReceiveClueNum();
          // 本月领取资源

        }
      }
    }

  /**
   *  曲线图数据
   */
  private void  receiveStatics(IndexReqDTO indexReqDTO){

  }


  private List<Long> merchantUserList( UserInfoDTO curLoginUser){
        Integer userType = curLoginUser.getUserType();
        Long userId = curLoginUser.getId();
        List<Long> subIds = new ArrayList<>();
        // 查询主账号信息
        if (SysConstant.USER_TYPE_TWO.equals(userType)) {
            // 获取商家主账号下的子账号列表
            UserInfoDTO userReqDto = buildQueryReqDto(SysConstant.USER_TYPE_THREE,userId);
            JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userReqDto);
            if (merchantUserList.getCode().equals(JSONResult.SUCCESS)) {
                if (CollectionUtils.isNotEmpty(merchantUserList.getData())) {
                    // 获取子账号id放入子账号集合中
                    subIds.addAll(merchantUserList.getData().stream().map(UserInfoDTO::getId).collect(
                        Collectors.toList()));
                }
            }
        }
        return subIds;
    }

    /**
     * 构建商家子账户查询实体
     * @return
     */
    private UserInfoDTO buildQueryReqDto(Integer userType, Long id) {
        // 获取商家主账号下的子账号列表
        UserInfoDTO userReqDto = new UserInfoDTO();
        // 商家主账户
        userReqDto.setUserType(userType);
        // 启用和锁定
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        userReqDto.setStatusList(statusList);
        // 商家主账号id
        userReqDto.setParentId(id);
        return userReqDto;
    }

}
