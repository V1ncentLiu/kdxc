package com.kuaidao.manageweb.controller.merchant.mhomepage;

import com.kuaidao.account.dto.call.CallBuyPackageModel;
import com.kuaidao.account.dto.outboundpackage.OutboundPackageRespDTO;
import com.kuaidao.account.dto.recharge.MerchantUserAccountDTO;
import com.kuaidao.account.dto.recharge.MerchantUserAccountQueryDTO;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerResp;
import com.kuaidao.common.constant.ComConstant.DIMENSION;
import com.kuaidao.common.constant.ComConstant.QFLAG;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.StageContant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.merchant.bussinesscall.CallPackageFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.ClueManagementFeignClient;
import com.kuaidao.manageweb.feign.merchant.publiccustomer.PubcustomerFeignClient;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantUserAccountFeignClient;
import com.kuaidao.manageweb.feign.merchant.rule.RuleAssignRecordFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.outboundpackage.OutboundPackageFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsParamDTO;
import com.kuaidao.merchant.dto.index.IndexReqDTO;
import com.kuaidao.merchant.dto.index.IndexRespDTO;
import com.kuaidao.merchant.dto.index.ResourceCountDTO;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private CallPackageFeignClient callPackageFeignClient;

    @Autowired
    private MerchantUserAccountFeignClient merchantUserAccountFeignClient;

    @Autowired
    private OutboundPackageFeignClient outboundPackageFeignClient;

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
        userInfoRespDTO.setMerchantIcon(user.getMerchantIcon());
        request.setAttribute("user", userInfoRespDTO);
        List<IndexModuleDTO> menuList = user.getMenuList();
        request.setAttribute("menuList", menuList);
        request.setAttribute("isUpdatePassword", isUpdatePassword);
        request.setAttribute("wsUrlHttp", wsUrlHttp);
        request.setAttribute("wsUrlHttps", wsUrlHttps);
        request.setAttribute("mqUserName", mqUserName);
        request.setAttribute("mqPassword", mqPassword);
        request.setAttribute("userId", user.getId());
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        if(roleInfoDTO!=null){
            if(roleInfoDTO.getRoleCode().equals(RoleCodeEnum.HWY.name())||roleInfoDTO.getRoleCode().equals(RoleCodeEnum.XXLY.name())
                ||RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode())||RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())){
                request.setAttribute("accountType", StageContant.STAGE_PHONE_TRAFFIC);
            }else if(Constants.USER_TYPE_TWO.equals(user.getUserType()) || Constants.USER_TYPE_THREE.equals(user.getUserType())){
                request.setAttribute("accountType", StageContant.STAGE_MERCHANT);
            }else {
                request.setAttribute("accountType", StageContant.STAGE_TELE);
            }
        }
        //查询主账号是否购买云呼叫套餐
        boolean hasBuyPackage = false;
        String packageName = "";
        Long accountId = user.getId();
        if(Constants.USER_TYPE_TWO.equals(user.getUserType())){
            accountId = user.getId();
        }else if(Constants.USER_TYPE_THREE.equals(user.getUserType())){
            accountId = user.getParentId();
        }
        JSONResult<CallBuyPackageModel> hasBuyPackageResult = callPackageFeignClient.getCallBuyPackage(accountId);
      logger.info("accountId::"+accountId);
        if (JSONResult.SUCCESS.equals(hasBuyPackageResult.getCode())) {
          CallBuyPackageModel data = hasBuyPackageResult.getData();
          if(data!=null){
            hasBuyPackage = true;
            IdEntityLong idEntity = new IdEntityLong();
            idEntity.setId(data.getPackageId());
            logger.info("getPackageId::"+data.getPackageId());
            JSONResult<OutboundPackageRespDTO> outboundPackageRespDTOJSONResult = outboundPackageFeignClient
                .queryOutboundPackageById(idEntity);
            if(CommonUtil.resultCheck(outboundPackageRespDTOJSONResult)){
              OutboundPackageRespDTO data1 = outboundPackageRespDTOJSONResult.getData();
              packageName = data1.getPackageName();
            }
          }
        }
      logger.info("packageName::"+packageName);
        request.setAttribute("hasBuyPackage", hasBuyPackage);
        request.setAttribute("packageName", packageName);
        // 判断显示主/子账户首页
        Integer userType = user.getUserType();
        request.setAttribute("isShowConsoleBtn", userType); // 主账户==2  子账户==3
        return "merchantIndex"; // 需要修改成对应的正确地址
    }

    /***
     * 跳转控制台页面
     * @return
     */
    @RequestMapping("/index")
    public String index(String type, HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String username = curLoginUser.getName();
        Integer userType = curLoginUser.getUserType();
        if(Constants.USER_TYPE_TWO.equals(userType)){
            // 查询账户余额
            setCountBalance(request);
            // 查询是否购买套餐
//            JSONResult<Boolean> hasBuyPackageResult = callPackageFeignClient.hasBuyPackage(curLoginUser.getId());
            boolean buyedFlag = false;
            String packageName = "";
//            if (JSONResult.SUCCESS.equals(hasBuyPackageResult.getCode())) {
//              buyedFlag = hasBuyPackageResult.getData();
//            }
          JSONResult<CallBuyPackageModel> hasBuyPackageResult = callPackageFeignClient.getCallBuyPackage(curLoginUser.getId());
          logger.info("accountId::"+curLoginUser.getId());
          if (JSONResult.SUCCESS.equals(hasBuyPackageResult.getCode())) {
            CallBuyPackageModel data = hasBuyPackageResult.getData();
            if(data!=null){
              buyedFlag = true;
              IdEntityLong idEntity = new IdEntityLong();
              idEntity.setId(data.getPackageId());
              logger.info("getPackageId::"+data.getPackageId());
              JSONResult<OutboundPackageRespDTO> outboundPackageRespDTOJSONResult = outboundPackageFeignClient
                  .queryOutboundPackageById(idEntity);
              if(CommonUtil.resultCheck(outboundPackageRespDTOJSONResult)){
                OutboundPackageRespDTO data1 = outboundPackageRespDTOJSONResult.getData();
                packageName = data1.getPackageName();
              }
            }
          }
          request.setAttribute("buyedFlag",buyedFlag); // 当前无法进行。在第四批需求
          request.setAttribute("packageName", packageName);
        }else{
        }
        request.setAttribute("userType",userType);
        request.setAttribute("merchantName",username);
        request.setAttribute("countSources",countSource());
        return  "merchant/homePage/accoun";
    }


  private void setCountBalance( HttpServletRequest request ){
    //查询商家账号余额信息
    UserInfoDTO user = CommUtil.getCurLoginUser();
    MerchantUserAccountQueryDTO dto = new MerchantUserAccountQueryDTO();
    dto.setUserId(user.getId());
    JSONResult<MerchantUserAccountDTO> accountDTOJSONResult = merchantUserAccountFeignClient.getMerchantUserAccountInfo(dto);
    MerchantUserAccountDTO merchantUserAccountDTO = new MerchantUserAccountDTO();
    merchantUserAccountDTO = accountDTOJSONResult.getData();
    if(merchantUserAccountDTO == null || merchantUserAccountDTO.getBalance() == null){
      merchantUserAccountDTO.setBalance(new BigDecimal("0.00"));
    }
    if(merchantUserAccountDTO == null || merchantUserAccountDTO.getTotalAmounts() == null){
      merchantUserAccountDTO.setBalance(new BigDecimal("0.00"));
    }
    BigDecimal totalAmounts = merchantUserAccountDTO.getTotalAmounts();
    if(totalAmounts==null){
      request.setAttribute("countBlance","0.00");
    }else{
      request.setAttribute("countBlance",totalAmounts.doubleValue());
    }

  }

    private ResourceCountDTO countSource(){
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

        Integer dayCount = 0;
        Integer montCount = 0;
        Integer allCount = 0;

        // 获取分发
        if (null != assignDto && assignDto.getCode().equals(JSONResult.SUCCESS)) {
          // 今日分发资源
          dayCount += assignDto.getData().getTodayAssignClueNum();
          // 累计分发
          allCount += assignDto.getData().getTotalAssignClueNum();
          // 本月分发资源
          montCount += assignDto.getData().getMonthAssignClueNum();
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
            dayCount += receiveResource.getTodayReceiveClueNum();
            // 累计领取资源
            allCount += receiveResource.getTotalReceiveClueNum();
            // 本月领取资源
            montCount += receiveResource.getMonthReceiveClueNum();
          }
        }
        ResourceCountDTO resourceCount = new ResourceCountDTO();
        resourceCount.setDayCount(dayCount);
        resourceCount.setMonthCount(montCount);
        resourceCount.setAllCount(allCount);
        return resourceCount;
    }

  /**
   *  曲线图数据
   */
  @RequestMapping("/receiveStatics")
  @ResponseBody
  public JSONResult<IndexRespDTO> receiveStatics(@RequestBody IndexReqDTO indexReqDTO){
    int diffDay = 0;
    diffDay = DateUtil.differentDays(indexReqDTO.getEtime(), indexReqDTO.getStime());
    if(diffDay<=30){
      String qflag = indexReqDTO.getQflag();
      if(QFLAG.ONE.equals(qflag)){
        indexReqDTO.setStime(DateUtil.getPriorDay(new Date()));
        indexReqDTO.setEtime(DateUtil.getPriorDay(new Date()));
      }else if(QFLAG.SEVEN.equals(qflag)){
        indexReqDTO.setStime(DateUtil.getPriorDay(new Date(),6));
        indexReqDTO.setEtime(new Date());
      }else if(QFLAG.THREETH.equals(qflag)){
        indexReqDTO.setStime(DateUtil.getPriorDay(new Date(),29));
        indexReqDTO.setEtime(new Date());
      }
    }

    ResourceStatisticsParamDTO paramDTO = new ResourceStatisticsParamDTO();
    paramDTO.setDimension(indexReqDTO.getDimension());
    paramDTO.setEtime(indexReqDTO.getEtime());
    paramDTO.setStime(indexReqDTO.getStime());f
    List<Integer> yList = new ArrayList<>();
    List<String> xList = gainX(indexReqDTO);
    // 主账户相关统计
    UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
    List<Long> subIds = new ArrayList<>();
    // 获取主账号分发相关
    JSONResult<List<ResourceStatisticsDto>> assignDto = null;
    Integer userType = curLoginUser.getUserType();
    if (SysConstant.USER_TYPE_TWO.equals(userType)) {
      paramDTO.setUserId(curLoginUser.getId());
      assignDto = ruleAssignRecordFeignClient
          .countAssginStatistic(paramDTO);
      subIds = merchantUserList(curLoginUser);
    }
    // 查询子账号信息
    if (SysConstant.USER_TYPE_THREE.equals(curLoginUser.getUserType())) {
      paramDTO.setUserId(curLoginUser.getId());
      // 获取子账号分发相关
      assignDto = clueManagementFeignClient.countAssignResourceStatistics(paramDTO);
      // 子账号id
      subIds.add(curLoginUser.getId());
    }

    Map<String,Integer> map = new HashMap<>();
    if(CommonUtil.resultCheck(assignDto)){
      List<ResourceStatisticsDto> data = assignDto.getData();
      map = data.stream().collect(Collectors.toMap(ResourceStatisticsDto::getDimension,ResourceStatisticsDto::getReceiveNum));
    }

    // 获取领取相关
    if (CollectionUtils.isNotEmpty(subIds)) {
      paramDTO.setIdList(subIds);
      //主账号也需要将自己领取的查出来
      if (SysConstant.USER_TYPE_TWO.equals(curLoginUser.getUserType())) {
        subIds.add(curLoginUser.getId());
      }
      JSONResult<List<ResourceStatisticsDto>> listJSONResult = pubcustomerFeignClient
          .countReceiveResourceStatistics(paramDTO);
      if(CommonUtil.resultCheck(listJSONResult)){
        List<ResourceStatisticsDto> data = listJSONResult.getData();
        for(ResourceStatisticsDto resource:data){
          Integer integer = map.get(resource.getDimension());
          if(integer!=null){
            map.put(resource.getDimension(),resource.getReceiveNum()+map.get(resource.getDimension()));
          }else{
            map.put(resource.getDimension(),resource.getReceiveNum());
          }
        }
      }
    }

    // 数据排序
    if(DIMENSION.WEEK.equals(indexReqDTO.getDimension())){
      for(String str:gainXNum(indexReqDTO)){
        Integer integer = map.get(str);
        if(integer==null){
          integer = 0 ;
        }
        yList.add(integer);
      }
    }else{
      for(String str:xList){
        Integer integer = map.get(str);
        if(integer==null){
          integer = 0 ;
        }
        yList.add(integer);
      }
    }

    IndexRespDTO indexRespDTO = new IndexRespDTO();
    indexRespDTO.setXList(xList);
    indexRespDTO.setYList(yList);
    return new JSONResult().success(indexRespDTO);
  }


  private List<String> gainX(IndexReqDTO indexReqDTO){
    List<String> xList = new ArrayList<>();
    String dimension = indexReqDTO.getDimension();
    Date stime = indexReqDTO.getStime(); // 开始时间
    Date etime = indexReqDTO.getEtime(); // 结束时间
    Calendar calendar = Calendar.getInstance();
    Calendar calendar1 = Calendar.getInstance();
    if(DIMENSION.DAY.equals(dimension)){
      int diffDay = DateUtil.diffDay(stime, etime);
      for(int i = 0 ; i <= diffDay ; i++){
        Date date = DateUtil.addDays(stime, i);
        calendar.setTime(date);
        String s = DateUtil.convert2String(calendar.getTime(), DateUtil.ymd);
        xList.add(s);
      }
    }else if(DIMENSION.MONTH.equals(dimension)){ // 完
      calendar.setTime(stime);
      int smonth = calendar.get(Calendar.MONTH)+1;
      int syear = calendar.get(Calendar.YEAR);
      calendar1.setTime(etime);
      int emonth = calendar1.get(Calendar.MONTH)+1;
      int eyear = calendar1.get(Calendar.YEAR);
      while(!(smonth>emonth&&syear==eyear)){
        if(smonth<10){
          xList.add(syear+"-0"+smonth);
        }else{
          xList.add(syear+"-"+smonth);
        }
        smonth = smonth+1;
        if(smonth>12){
          smonth = 1;
          syear = syear+1;
        }
      }
    }else  if(DIMENSION.YRAR.equals(dimension)){ // 完
      calendar.setTime(stime);
      int syear = calendar.get(Calendar.YEAR);
      calendar1.setTime(etime);
      int eyear = calendar1.get(Calendar.YEAR);
      for(int i = syear ; i <= eyear ;i++){
        xList.add( ""+i );
      }
    }else  if(DIMENSION.WEEK.equals(dimension)){
      calendar1.setTime(etime);
      String s1 = DateUtil.convert2String(etime, DateUtil.ym);
      int diffDay = DateUtil.diffDay(stime, etime);
      for(int i = 0 ; i <= diffDay ; i++){
        Date date = DateUtil.addDays(stime, i);
        calendar.setTime(date);
        String s = DateUtil.convert2String(calendar.getTime(), DateUtil.ym);
        String s2 = this.toWeek(calendar.get(Calendar.WEEK_OF_MONTH));
        String week = s+s2;
        if(!xList.contains(week)&&!"".equals(s2)){
          xList.add(week);
        }
      }
    } else if (DIMENSION.HOUR.equals(dimension)) { // 完
      calendar.setTime(etime);
      int hour = calendar.get(Calendar.HOUR_OF_DAY);
      for(int i = 0 ; i<= hour ; i++){
        if( i<10){
          xList.add("0"+i);
        }else{
          xList.add(""+i);
        }
      }
    }
    return xList;
  }

  private List<String> gainXNum(IndexReqDTO indexReqDTO){
    Date etime = indexReqDTO.getEtime();
    Date stime = indexReqDTO.getStime();
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar = Calendar.getInstance();
    calendar1.setTime(etime);
    String s1 = DateUtil.convert2String(etime, DateUtil.ym);
    List<String> xList = new ArrayList<>();
    int diffDay = DateUtil.diffDay(stime, etime);
    for(int i = 0 ; i <= diffDay ; i++){
      Date date = DateUtil.addDays(stime, i);
      calendar.setTime(date);
      String s = DateUtil.convert2String(calendar.getTime(), DateUtil.ym);
      Integer s2 = calendar.get(Calendar.WEEK_OF_YEAR);
      String week = s+"-"+s2;
      if(!xList.contains(week)){
        xList.add(week);
      }
    }
    return xList;
  }


  private String toWeek(int week){
    String restr = "";
     switch(week){
      case 1 :
        restr = " 1st星期";
        break; //可选
      case 2 :
        restr = " 2nd星期";
        break;
      case 3 :
       restr = " 3rd星期";
       break; //可选
      case 4 :
       restr = " 4th星期";
       break; //可选
      case 5 :
         restr = " 5th星期";
         break; //可选
      default : restr = "";

    }
    return restr;
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


  /**
   *
   * @param args
   */
  public static void main(String[] args){
      Calendar c = Calendar.getInstance();
      c.setTime(new java.util.Date());
      int i = c.get(Calendar.WEEK_OF_MONTH);
      int i1 = c.get(Calendar.WEEK_OF_YEAR);
      int i2 = c.get(Calendar.HOUR_OF_DAY); // 24小时
      int i3 = c.get(Calendar.HOUR); // 12小时
      int i4 = c.get(Calendar.YEAR); // 12小时

      int weeksInWeekYear = c.getWeeksInWeekYear();
      int weekYear = c.getWeekYear();
      logger.info(""+i);
      logger.info(""+i1); // 和数据库中计算的星期数相同
      logger.info(""+i2);
      logger.info(""+i3);
      logger.info(""+i4);
      logger.info("月份"+c.get(Calendar.MONTH));
      logger.info(""+weeksInWeekYear);
      logger.info(""+weekYear);
      SimpleDateFormat endSdf = new SimpleDateFormat("yyyy-MM-dd  23:59:59");
      logger.info(endSdf.format(c.getTime()));
    }

}
