package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeRecordQueryDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeReq;
import com.kuaidao.account.dto.recharge.RechargeAccountDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargeRecordManageFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.project.ProjectDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @description: MerchantRechargeRecordBusinessController
 * @date: 2019/9/26 15:48
 * @author: xuyunfeng
 * @version: 1.0
 */
@Controller
@RequestMapping("/merchant/merchantRechargeRecordManage")
public class MerchantRechargeRecordManageController {
  private static Logger logger = LoggerFactory.getLogger(MerchantRechargeRecordManageController.class);

  @Value("${oss.url.directUpload}")
  private String ossUrl;

  @Autowired
  MerchantRechargeRecordManageFeignClient merchantRechargeRecordManageFeignClient;
  @Autowired
  private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
  @Autowired
  RedisTemplate redisTemplate;

  @Autowired
  private ProjectInfoFeignClient projectInfoFeignClient;
  /**
   * @Description 初始化管理端充值记录页面
   * @param request
   * @Return java.lang.String
   * @Author xuyunfeng
   * @Date 2019/9/26 15:57
   **/
  @RequestMapping("/initRechargeRecordManage")
  @RequiresPermissions("merchant:merchantRechargeRecordManage:view")
  public String initRechargeRecordBusiness(HttpServletRequest request){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      MerchantRechargeRecordQueryDTO queryDTO = new MerchantRechargeRecordQueryDTO();
      queryDTO.setManageRechargeUser(user.getId());
      JSONResult<RechargeAccountDTO> rechargeAccountDTOJSONResult = merchantRechargeRecordManageFeignClient.getNowDayAndMonthRechargeMoney(queryDTO);
      RechargeAccountDTO rechargeAccountDTO = new RechargeAccountDTO();
      if(rechargeAccountDTOJSONResult.getCode().equals(JSONResult.SUCCESS)){
        rechargeAccountDTO = rechargeAccountDTOJSONResult.getData();
      }
      if(rechargeAccountDTO == null || rechargeAccountDTO.getDaySumMoney() == null){
        rechargeAccountDTO.setDaySumMoney(new BigDecimal("0.00"));
      }
      if(rechargeAccountDTO == null || rechargeAccountDTO.getMonthSumMoney() == null){
        rechargeAccountDTO.setMonthSumMoney(new BigDecimal("0.00"));
      }
      request.setAttribute("rechargeAccountDTO",rechargeAccountDTO);
      // 商家账号
      List<UserInfoDTO> userList = getMerchantUser(null);
      request.setAttribute("merchantUserList",userList);

      IdListLongReq idListLongReq = new IdListLongReq();
      idListLongReq.setIdList(ListUtils.emptyIfNull(userList).stream().map(UserInfoDTO::getId).collect(Collectors.toList()));
      JSONResult<List<ProjectInfoDTO>> listJSONResult = projectInfoFeignClient.queryListByGroupId(idListLongReq);
      if(JSONResult.SUCCESS.equals(listJSONResult.getCode())){
        request.setAttribute("projectList", listJSONResult.getData());

      }
    }catch (Exception e){
      logger.error("initRechargeRecordManage:{}",e);
    }
    return "merchant/rechargeRecord/rechargeRecord";
  }
  /**
   * @Description 管理端充值记录列表查询
   * @param queryDTO
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>>
   * @Author xuyunfeng
   * @Date 2019/9/26 16:07
   **/
  @ResponseBody
  @RequestMapping("/queryManagePageList")
  @RequiresPermissions("merchant:merchantRechargeRecordManage:view")
  public JSONResult<PageBean<MerchantRechargeRecordDTO>> queryBusinessPageList(@RequestBody MerchantRechargeRecordQueryDTO queryDTO ){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      queryDTO.setManageRechargeUser(user.getId());
      if(queryDTO.getProjectId()!=null){
        JSONResult<ProjectInfoDTO> projectInfoDTOJSONResult = projectInfoFeignClient.get(new IdEntityLong(queryDTO.getProjectId()));
        if(JSONResult.SUCCESS.equals(projectInfoDTOJSONResult.getCode()) && projectInfoDTOJSONResult.getData()!=null){
          String groupId = projectInfoDTOJSONResult.getData().getGroupId();
          if(queryDTO.getRechargeBusiness()!=null && queryDTO.getRechargeBusiness().longValue()!=Long.parseLong(groupId)){
            return new JSONResult<PageBean<MerchantRechargeRecordDTO>>().success(null);
          }
          if(queryDTO.getRechargeBusiness()==null){
            queryDTO.setRechargeBusiness(Long.parseLong(groupId));
          }
        }
      }
      JSONResult<PageBean<MerchantRechargeRecordDTO>> list = merchantRechargeRecordManageFeignClient.queryManagePageList(queryDTO);
      if(JSONResult.SUCCESS.equals(list.getCode())){
        Map<String, String> userProject = getUserProject(ListUtils.emptyIfNull(list.getData().getData()).stream().map(MerchantRechargeRecordDTO::getRechargeBusiness).collect(Collectors.toList()));
        for (MerchantRechargeRecordDTO merchantRechargeRecordDTO : list.getData().getData()) {
          if(userProject.containsKey(merchantRechargeRecordDTO.getRechargeBusiness()+"")){
            merchantRechargeRecordDTO.setProjectName(userProject.get(merchantRechargeRecordDTO.getRechargeBusiness()+""));
          }
        }

      }
      return list;
    }catch (Exception e){
      logger.error("queryManagePageList:{}",e);
      return new JSONResult<PageBean<MerchantRechargeRecordDTO>>().fail("-1","queryManagePageList接口异常");
    }
  }

  /**
   * @Description 加载管理端录入线下付款页面
   * @param request
   * @Return com.kuaidao.common.entity.JSONResult<java.lang.Boolean>
   * @Author xuyunfeng
   * @Date 2019/9/27 15:30
   **/
  @RequestMapping("/initOfflinePayment")
  @RequiresPermissions("merchant:merchantRechargeRecordManage:add")
  public String initOfflinePayment(HttpServletRequest request){
    UserInfoDTO user = CommUtil.getCurLoginUser();
    // 商家账号
    List<UserInfoDTO> userList = getMerchantUser(null);
    if(CollectionUtils.isNotEmpty(userList)){
      Map<String, List<ProjectInfoDTO>> userProjectList = getUserProjectList(ListUtils.emptyIfNull(userList).stream().map(UserInfoDTO::getId).collect(Collectors.toList()));
      BeanCopier beanCopier = BeanCopier.create(ProjectInfoDTO.class, ProjectDTO.class, false);
      for (UserInfoDTO userInfoDTO : userList) {
          if(userProjectList.containsKey(userInfoDTO.getId()+"")){
            List<ProjectInfoDTO> projectInfoDTOList = userProjectList.get(userInfoDTO.getId() + "");
            if(CollectionUtils.isNotEmpty(projectInfoDTOList)){
              List<ProjectDTO> list = new ArrayList<>();
              for (ProjectInfoDTO projectInfoDTO : projectInfoDTOList) {
                  ProjectDTO projectDTO = new ProjectDTO();
                  beanCopier.copy(projectInfoDTO,projectDTO,null);
                  list.add(projectDTO);
              }
              userInfoDTO.setChildren(list);
            }
          }
      }
    }
    request.setAttribute("merchantUserList",userList);
    request.setAttribute("ossUrl",ossUrl);
    String token = UUID.randomUUID().toString();
    //随机生成token放入Redis
    //redisTemplate.opsForValue().set(user.getId().toString(),token);
    request.setAttribute("token",token);
    return "merchant/rechargeRecord/rechargePaymentOffline";
  }

  /**
   * @Description 录入线下付款信息
   * @param
   * @Return com.kuaidao.common.entity.JSONResult<java.lang.Boolean>
   * @Author xuyunfeng
   * @Date 2019/9/27 15:30
   **/
  @ResponseBody
  @RequestMapping("/saveOfflinePayment")
  @RequiresPermissions("merchant:merchantRechargeRecordManage:add")
  public JSONResult<Boolean> saveOfflinePayment(@RequestBody  MerchantRechargeReq req){
    UserInfoDTO user = CommUtil.getCurLoginUser();
    try {
      //判断是否重复提交
//      if (!isSubmit(req,user)) {
//        return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"请不要重复提交");
//      }
      req.setCreateUser(user.getId());
      JSONResult<Boolean> list = merchantRechargeRecordManageFeignClient.saveOfflinePayment(req);
      return list;
    }catch (Exception e){
      logger.error("录入线下付款信息:{}",e);
      //redisTemplate.opsForValue().set(user.getId().toString(),req.getToken());
      return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"录入线下付款信息接口异常");
    }
  }
  /**
   * @Title: isSubmit
   * @Description: 判断token值是否相同以及是否有伪token值得传入
   * @param req
   * @return
   * boolean
   */

  public boolean isSubmit(MerchantRechargeReq req,UserInfoDTO user){
    String sessionToken = null;
    if(redisTemplate.hasKey(user.getId().toString())){
      sessionToken = (String)redisTemplate.opsForValue().get(user.getId().toString());
      if (!(sessionToken.equals(req.getToken()))) {
        return false;
      }
      redisTemplate.delete(user.getId().toString());
    }else {
      return false;
    }
    return true;
  }
  /**
   * 查询商家账号
   *
   * @param arrayList
   * @return
   */
  private List<UserInfoDTO> getMerchantUser(List<Integer> arrayList) {
    UserInfoDTO userInfoDTO = new UserInfoDTO();
    userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
    userInfoDTO.setStatusList(arrayList);
    JSONResult<List<UserInfoDTO>> merchantUserList =
            merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
    return merchantUserList.getData();
  }

  /**
   * 根据用户id  返回项目名
   * @param userIds
   * @return
   */
  private Map<String,String> getUserProject(List<Long> userIds){
    IdListLongReq idListLongReq = new IdListLongReq();
    idListLongReq.setIdList(userIds);
    JSONResult<List<ProjectInfoDTO>> listJSONResult = projectInfoFeignClient.queryListByGroupId(idListLongReq);
    List<ProjectInfoDTO> projectInfoDTOList = new ArrayList<>();
    if(listJSONResult.getCode().equals(JSONResult.SUCCESS)) {
      projectInfoDTOList =listJSONResult.data();
    }
    return  ListUtils.emptyIfNull(projectInfoDTOList).stream().
            collect(Collectors.
                    groupingBy(ProjectInfoDTO::getGroupId,
                            Collectors.mapping(ProjectInfoDTO::getProjectName,
                                    Collectors.joining("、"))));

  }

  /**
   * 根据用户id  返回项目名
   * @param userIds
   * @return
   */
  private Map<String,List<ProjectInfoDTO>> getUserProjectList(List<Long> userIds){
    IdListLongReq idListLongReq = new IdListLongReq();
    idListLongReq.setIdList(userIds);
    JSONResult<List<ProjectInfoDTO>> listJSONResult = projectInfoFeignClient.queryListByGroupId(idListLongReq);
    List<ProjectInfoDTO> projectInfoDTOList = new ArrayList<>();
    if(listJSONResult.getCode().equals(JSONResult.SUCCESS)) {
      projectInfoDTOList =listJSONResult.data();
    }
    return ListUtils.emptyIfNull(projectInfoDTOList).stream().collect(Collectors.groupingBy(ProjectInfoDTO::getGroupId));
  }
}
