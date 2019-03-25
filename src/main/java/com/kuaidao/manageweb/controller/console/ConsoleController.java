package com.kuaidao.manageweb.controller.console;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerRespDTO;
import com.kuaidao.aggregation.dto.busmycustomer.MyCustomerParamDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordRespDTO;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationDTO;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationPageParam;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationClueDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationCluePageParam;
import com.kuaidao.aggregation.dto.console.BusinessConsolePanelRespDTO;
import com.kuaidao.aggregation.dto.console.BusinessConsoleReqDTO;
import com.kuaidao.aggregation.dto.console.BusinessDirectorConsolePanelRespDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordRespDTO;
import com.kuaidao.common.constant.CluePhase;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.BusReceiveFeignClient;
import com.kuaidao.manageweb.feign.busmycustomer.BusMyCustomerFeignClient;
import com.kuaidao.manageweb.feign.clue.AppiontmentFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.clue.PendingVisitFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.sign.SignRecordFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.feign.visit.VisitRecordFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 控制台
 * @author  Chen
 * @date 2019年3月16日 下午2:27:31   
 * @version V1.0
 */

@Controller
@RequestMapping("/console/console")
public class ConsoleController {
    private static Logger logger = LoggerFactory.getLogger(ConsoleController.class);
     
    @Autowired
    AnnReceiveFeignClient annReceiveFeignClient;

    @Autowired
    BusReceiveFeignClient busReceiveFeignClient;
    
    @Autowired
    ClueBasicFeignClient clueBasicFeignClient;
    
    @Autowired
    AppiontmentFeignClient appiontmentFeignClient;
    
    
    @Autowired
    UserInfoFeignClient userInfoFeignClient;
    
    @Autowired
    MyCustomerFeignClient myCustomerFeignClient;

    @Autowired
    VisitRecordFeignClient visitRecordFeignClient;
    
    @Autowired
    SignRecordFeignClient signRecordFeignClient;
    
    @Autowired
    PendingVisitFeignClient pendingVisitFeignClient;
    
    @Autowired
    BusMyCustomerFeignClient busMyCustomerFeignClient;
    
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    /***
     * 跳转控制台页面
     * @return
     */
    @RequestMapping("/index")
    public String index(String type,HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String path= "";
        
       if(RoleCodeEnum.DXCYGW.name().equals(roleCode)) {
            //电销顾问
           path = "console/consoleTelemarketing";
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            statusList.add(SysConstant.USER_STATUS_LOCK);
            List<UserInfoDTO> userList =
                    getUserList(orgId, RoleCodeEnum.DXCYGW.name(), statusList);
            request.setAttribute("saleList", userList);
            path = "console/consoleTelMajordomo";
        }else if(RoleCodeEnum.SWJL.name().equals(roleCode)) {
            //商务经理
            // 项目
            ProjectInfoPageParam param = new ProjectInfoPageParam();
            JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
            if(JSONResult.SUCCESS.equals(proJson.getCode())){
                request.setAttribute("proSelect", proJson.getData());
            }
            path="console/consoleBusinessManager";
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode)) {
            //商务总监
            // 查询所有商务经理
            List<Map<String, Object>> allSaleList = getAllSaleList();
            request.setAttribute("allSaleList", allSaleList);
            path="console/consoleBusinessMajordomo";
        }
 /*       if(type.equals("1")) {
            path = "console/consoleTelemarketing";
        }else if(type.equals("2")) {
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            statusList.add(SysConstant.USER_STATUS_LOCK);
            List<UserInfoDTO> userList = getUserList(orgId, RoleCodeEnum.DXCYGW.name(), statusList);
            request.setAttribute("saleList", userList);
            path="console/consoleTelMajordomo";
        }else if(type.equals("3")) {
            // 项目
            ProjectInfoPageParam param = new ProjectInfoPageParam();
            JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
            if(JSONResult.SUCCESS.equals(proJson.getCode())){
                request.setAttribute("proSelect", proJson.getData());
            }

            path="console/consoleBusinessManager";
        }else if(type.equals("4")) {
            // 查询所有商务经理
            List<Map<String, Object>> allSaleList = getAllSaleList();
            request.setAttribute("allSaleList", allSaleList);
            path="console/consoleBusinessMajordomo";
        }*/
        return path;
    }
    
    
    
    /**
     *   查询公告 --不 带分页
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryAnnReceiveNoPage")
    @ResponseBody
    public JSONResult<List<AnnReceiveRespDTO>> queryAnnReceiveNoPage() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        AnnReceiveQueryDTO queryDTO = new AnnReceiveQueryDTO();
        queryDTO.setReceiveUser(curLoginUser.getId());
        Date curDate = new Date();
        Date addDays = DateUtil.addDays(curDate, -7);
        queryDTO.setDate1(addDays);
        queryDTO.setDate2(curDate);
        return annReceiveFeignClient.queryAnnReceiveNoPage(queryDTO);
    }
    
    /**
     * 控制台使用
     *   查询业务消息 -- 不带分页
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryBussReceiveNoPage")
    @ResponseBody
    public JSONResult<List<BussReceiveRespDTO>> queryBussReceiveNoPage(@RequestBody BussReceiveQueryDTO queryDTO) {
        
        return busReceiveFeignClient.queryBussReceiveNoPage(queryDTO);
    }
    
    
    /**
     *  统计电销人员今日分配资源数
     * @param reqDTO
     * @return
     */
    @PostMapping("/countAssignClueNum")
    @ResponseBody
    public JSONResult<Integer> countAssignClueNum(@RequestBody TeleConsoleReqDTO reqDTO){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setTeleSaleId(curLoginUser.getId());
        reqDTO.setType(1);
        Date curDate = new Date();
        reqDTO.setEndTime(curDate);
        reqDTO.setStartTime(DateUtil.getCurStartDate());
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add(1);
        sourceList.add(2);
        reqDTO.setSourceList(sourceList);
        return  clueBasicFeignClient.countAssignClueNum(reqDTO);
    }
    
    
    /**
     * 统计电销人员今日領取数
     * @param reqDTO
     * @return
     */
    @PostMapping("/countReceiveClueNum")
    @ResponseBody
    public JSONResult<Integer> countReceiveClueNum(@RequestBody TeleConsoleReqDTO reqDTO){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setTeleSaleId(curLoginUser.getId());
        Date curDate = new Date();
        reqDTO.setEndTime(curDate);
       // reqDTO.setStartTime(DateUtil.getCurStartDate());
        reqDTO.setStartTime(DateUtil.getTodayStartTime());
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add(3);
        reqDTO.setSourceList(sourceList);
        reqDTO.setType(1);
        return  clueBasicFeignClient.countAssignClueNum(reqDTO);
    }
    
    
    
   /***
    * 控制台 今日邀约单 不包括 删除的
    * @param teleConsoleReqDTO
    * @return
    */
   @PostMapping("/countTodayAppiontmentNum")
   @ResponseBody
   public JSONResult<Integer> countTodayAppiontmentNum(){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       TeleConsoleReqDTO teleConsoleReqDTO = new TeleConsoleReqDTO();
       Long id = curLoginUser.getId();
       teleConsoleReqDTO.setTeleSaleId(id);
       teleConsoleReqDTO.setStartTime(DateUtil.getTodayStartTime());
       teleConsoleReqDTO.setEndTime(new Date());
       return appiontmentFeignClient.countTodayAppiontmentNum(teleConsoleReqDTO);
   }
   
   
   /**
    *   查询电销人员 待跟进客户资源
    * 
    * @param queryDto
    * @return
    */
   @PostMapping("/listTodayFollowClue")
   @ResponseBody
   public JSONResult<PageBean<CustomerClueDTO>> listTodayFollowClue(@RequestBody CustomerClueQueryDTO queryDto) throws Exception{
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       queryDto.setTeleSale(curLoginUser.getId());
       Date curDate = new Date();
       queryDto.setAssignTimeStart(DateUtil.getStartOrEndOfDay(curDate, LocalTime.MIN));
       queryDto.setAssignTimeEnd(DateUtil.getStartOrEndOfDay(curDate, LocalTime.MAX));
       return  myCustomerFeignClient.listTodayFollowClue(queryDto);
   }
   
   /**
    * 电销总监未分配资源数
    * @param reqDTO
    * @return
    */
   @PostMapping("/countTeleDircortoerUnAssignClueNum")
   @ResponseBody
   public JSONResult<Integer> countTeleDircortoerUnAssignClueNum(@RequestBody TeleConsoleReqDTO reqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       reqDTO.setTeleDirectorId(curLoginUser.getId());
       Date curDate = new Date();
       reqDTO.setEndTime(curDate);
       reqDTO.setStartTime(DateUtil.getTodayStartTime());
       reqDTO.setPhase(CluePhase.PHAE_3RD.getCode());
       reqDTO.setType(2);
       return  clueBasicFeignClient.countAssignClueNum(reqDTO);
   }
   
   
   /**
    * 电销总监今日接受资源数
    * @param reqDTO
    * @return
    */
   @PostMapping("/countTeleDircortoerReceiveClueNum")
   @ResponseBody
   public JSONResult<Integer> countTeleDircortoerReceiveClueNum(@RequestBody TeleConsoleReqDTO reqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       reqDTO.setTeleDirectorId(curLoginUser.getId());
       Date curDate = new Date();
       reqDTO.setEndTime(curDate);
       reqDTO.setStartTime(DateUtil.getTodayStartTime());
       reqDTO.setType(2);
       List<Integer> teleDirectorSourceList = new ArrayList<>();
       teleDirectorSourceList.add(2);
       teleDirectorSourceList.add(3);
       reqDTO.setTeleDirectorSourceList(teleDirectorSourceList);
       return  clueBasicFeignClient.countAssignClueNum(reqDTO);
   }
   
   /**
    * 电销总监今日领取资源数
    * @param reqDTO
    * @return
    */
   @PostMapping("/countTeleDircortoerGetClueNum")
   @ResponseBody
   public JSONResult<Integer> countTeleDircortoerGetClueNum(@RequestBody TeleConsoleReqDTO reqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       reqDTO.setTeleDirectorId(curLoginUser.getId());
       Date curDate = new Date();
       reqDTO.setEndTime(curDate);
       reqDTO.setStartTime(DateUtil.getTodayStartTime());
       reqDTO.setType(2);
       List<Integer> teleDirectorSourceList = new ArrayList<>();
       teleDirectorSourceList.add(1);
       reqDTO.setTeleDirectorSourceList(teleDirectorSourceList);
       return  clueBasicFeignClient.countAssignClueNum(reqDTO);
   }
   
   /**
    * 电销总监  今日邀约数
    * @return
    */
   @PostMapping("/countTeleDirectorTodayAppiontmentNum")
   @ResponseBody
   public JSONResult<Integer> countTeleDirectorTodayAppiontmentNum(){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       TeleConsoleReqDTO teleConsoleReqDTO = new TeleConsoleReqDTO();
       Long id = curLoginUser.getId();
       UserOrgRoleReq req = new UserOrgRoleReq();
       req.setOrgId(id);
       req.setRoleCode(RoleCodeEnum.DXCYGW.name());
       JSONResult<List<UserInfoDTO>> userInfoJr = userInfoFeignClient.listByOrgAndRole(req);
       if(!JSONResult.SUCCESS.equals(userInfoJr.getCode())) {
          return  new JSONResult<Integer>().fail(userInfoJr.getCode(),userInfoJr.getMsg()); 
       }
       List<UserInfoDTO> data = userInfoJr.getData();
       if(CollectionUtils.isEmpty(data)) {
          return new JSONResult<Integer>().success(0);
       }
       List<Long> teleSaleIdList = data.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
       teleConsoleReqDTO.setTeleSaleIdList(teleSaleIdList);       
       teleConsoleReqDTO.setStartTime(DateUtil.getTodayStartTime());
       teleConsoleReqDTO.setEndTime(new Date());
       return appiontmentFeignClient.countTodayAppiontmentNum(teleConsoleReqDTO);
   }
   
   
   /**
    *  电销总监 预计明日到访数
    *  
    * @return
    */
   @PostMapping("/countTeleDirecotorTomorrowArriveTime")
   @ResponseBody
   public JSONResult<Integer> countTeleDirecotorTomorrowArriveTime(@RequestBody TeleConsoleReqDTO reqDTO){
       
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       TeleConsoleReqDTO teleConsoleReqDTO = new TeleConsoleReqDTO();
       Long id = curLoginUser.getId();
       UserOrgRoleReq req = new UserOrgRoleReq();
       req.setOrgId(id);
       req.setRoleCode(RoleCodeEnum.DXCYGW.name());
       JSONResult<List<UserInfoDTO>> userInfoJr = userInfoFeignClient.listByOrgAndRole(req);
       if(!JSONResult.SUCCESS.equals(userInfoJr.getCode())) {
          return  new JSONResult<Integer>().fail(userInfoJr.getCode(),userInfoJr.getMsg()); 
       }
       List<UserInfoDTO> data = userInfoJr.getData();
       if(CollectionUtils.isEmpty(data)) {
          return new JSONResult<Integer>().success(0);
       }
       List<Long> teleSaleIdList = data.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
       teleConsoleReqDTO.setTeleSaleIdList(teleSaleIdList);  
       Date curDate = new Date();
       Date nextDate = DateUtil.addDays(curDate, 1);
       teleConsoleReqDTO.setStartTime(DateUtil.getStartOrEndOfDay(nextDate, LocalTime.MIN));
       teleConsoleReqDTO.setEndTime(DateUtil.getStartOrEndOfDay(nextDate, LocalTime.MAX));
       return appiontmentFeignClient.countTeleDirecotorTomorrowArriveTime(teleConsoleReqDTO);
   }
   
   /**
    * 电销总监 查询待分配资源
    * @param pageParam
    * @param result
    * @return
    */
   @PostMapping("/listUnAssignClue")
   @ResponseBody
   public JSONResult<PageBean<PendingAllocationClueDTO>> listUnAssignClue(
           @RequestBody PendingAllocationCluePageParam pageParam){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       pageParam.setUserId(curLoginUser.getId());
       return clueBasicFeignClient.listUnAssignClue(pageParam);
   }
   
   
   
   /**
    * 商务经理 看板统计
    * @return
    */
   @RequestMapping("/countCurMonthNum")
   @ResponseBody
   public JSONResult<BusinessConsolePanelRespDTO> countCurMonthVisitedNum(@RequestBody BusinessConsoleReqDTO businessConsoleReqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       List<Long> accountIdList = new ArrayList<Long>();
       accountIdList.add(curLoginUser.getId());
       businessConsoleReqDTO.setAccountIdList(accountIdList);
       Date curDate = new Date();
       businessConsoleReqDTO.setEndTime(curDate);
       businessConsoleReqDTO.setStartTime(DateUtil.getCurStartDate());
       return  visitRecordFeignClient.countCurMonthNum(businessConsoleReqDTO);
   }
   
   
   /**
    * 商务经理控制台  待处理邀约来访客户
    * @param param
    * @return
    */
   @PostMapping("/listPendingInviteCustomer")
   @ResponseBody
   public JSONResult<PageBean<BusMyCustomerRespDTO>> listPendingInviteCustomer(@RequestBody MyCustomerParamDTO param){
       UserInfoDTO user = CommUtil.getCurLoginUser();
       param.setBusSaleId(user.getId());
       //param.setBusSaleId(1084621842175623168L);
       
       return busMyCustomerFeignClient.listPendingInviteCustomer(param);
       
   }
   
/*   *//**
    * 商务经理当月签约数
    * @param businessConsoleReqDTO
    * @return
    *//*
   @RequestMapping("/countCurMonthSignedNum")
   @ResponseBody
   public JSONResult<BusinessConsolePanelRespDTO> countCurMonthSignedNum(@RequestBody BusinessConsoleReqDTO businessConsoleReqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       List<Long> accountIdList = new ArrayList<Long>();
       accountIdList.add(curLoginUser.getId());
       Date curDate = new Date();
       businessConsoleReqDTO.setEndTime(curDate);
       businessConsoleReqDTO.setStartTime(DateUtil.getCurStartDate());
       return  signRecordFeignClient.countCurMonthSignedNum(businessConsoleReqDTO);
   }*/
   
   
   /**
    * 商务总监 1. 待分配任务数  9当月二次到访数  10 当月二次来访签约数： 看板统计
    * @return
    */
   @RequestMapping("/countBusinessDirectorCurMonthNum")
   @ResponseBody
   public JSONResult<BusinessDirectorConsolePanelRespDTO> countBusinessDirectorCurMonthNum(@RequestBody BusinessConsoleReqDTO businessConsoleReqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       List<Long> accountIdList = new ArrayList<Long>();
       accountIdList.add(curLoginUser.getId());
       businessConsoleReqDTO.setAccountIdList(accountIdList);
       Date curDate = new Date();
       businessConsoleReqDTO.setEndTime(curDate);
       //本月第一天 00
       businessConsoleReqDTO.setStartTime(DateUtil.getCurStartDate());
       return  visitRecordFeignClient.countBusinessDirectorCurMonthNum(businessConsoleReqDTO);
   }
   
   /**
    * 商务总监  4预计明日到访数
    * @param businessConsoleReqDTO
    * @return
    */
   @PostMapping("/countBusiDirecotorTomorrowArriveTime")
   @ResponseBody
   public JSONResult<Integer> countBusiDirecotorTomorrowArriveTime(@RequestBody BusinessConsoleReqDTO businessConsoleReqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       Long id = curLoginUser.getId();
       UserOrgRoleReq req = new UserOrgRoleReq();
       req.setOrgId(id);
       req.setRoleCode(RoleCodeEnum.DXCYGW.name());
       JSONResult<List<UserInfoDTO>> userInfoJr = userInfoFeignClient.listByOrgAndRole(req);
       if(!JSONResult.SUCCESS.equals(userInfoJr.getCode())) {
          return  new JSONResult<Integer>().fail(userInfoJr.getCode(),userInfoJr.getMsg()); 
       }
       List<UserInfoDTO> data = userInfoJr.getData();
       if(CollectionUtils.isEmpty(data)) {
          return new JSONResult<Integer>().success(0);
       }
       List<Long> teleSaleIdList = data.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
       businessConsoleReqDTO.setAccountIdList(teleSaleIdList);  
       Date curDate = new Date();
       Date nextDate = DateUtil.addDays(curDate, 1);
       businessConsoleReqDTO.setStartTime(DateUtil.getStartOrEndOfDay(nextDate, LocalTime.MIN));
       businessConsoleReqDTO.setEndTime(DateUtil.getStartOrEndOfDay(nextDate, LocalTime.MAX));
       
       return appiontmentFeignClient.countBusiDirecotorTomorrowArriveTime(businessConsoleReqDTO);
   }
   
   
   /***
    * 商务总监 待分配来访客户列表
    * 
    * @return
    */
   @PostMapping("/pendingVisitListNoPage")
   @ResponseBody
   public JSONResult<List<BusPendingAllocationDTO>> pendingVisitListNoPage(
           @RequestBody BusPendingAllocationPageParam pageParam, HttpServletRequest request) {
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       // 插入当前用户、角色信息
       pageParam.setUserId(curLoginUser.getId());
       List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
       if (roleList != null) {
           pageParam.setRoleCode(roleList.get(0).getRoleCode());
       }

       JSONResult<List<BusPendingAllocationDTO>> pendingAllocationList =
               pendingVisitFeignClient.pendingVisitListNoPage(pageParam);

       return pendingAllocationList;
   }
   
   
   /**
    * 商务总监 待审批到访记录  待审批未到访记录 
    * @param visitRecordReqDTO  isVisit:是否到访  
    * @return
    */
   @PostMapping("/listVisitRecord")
   @ResponseBody
   public JSONResult<List<VisitRecordRespDTO>> listVisitRecord(@RequestBody VisitRecordReqDTO visitRecordReqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       List<Long> busGroupIdList = new ArrayList<>();
       busGroupIdList.add(curLoginUser.getId());
       visitRecordReqDTO.setBusGroupIdList(busGroupIdList);
       visitRecordReqDTO.setStatus(1);
      return  visitRecordFeignClient.listVisitRecordNoPage(visitRecordReqDTO);
   }
  
   
   
   /**
    * 商务总监 待审批签约记录
    * @param reqDTO
    * @return
    */
   @PostMapping("/listSignRecord")
   @ResponseBody
   public JSONResult<List<SignRecordRespDTO>> listSignRecord(@RequestBody SignRecordReqDTO reqDTO) {
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       List<Long> businessGroupIdList = new ArrayList<>();
       businessGroupIdList.add(curLoginUser.getId());
       reqDTO.setBusinessGroupIdList(businessGroupIdList);
       reqDTO.setStatus(AggregationConstant.SIGN_ORDER_STATUS.AUDITING);
       return signRecordFeignClient.listSignRecordNoPage(reqDTO);
   }
    
    
    public static void main(String[] args) {
        Date curDate = new Date();
        Date addDays = DateUtil.addDays(curDate, -7);
        System.out.println(addDays);
        System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00"));
     // 获取当天凌晨0点0分0秒Date
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        Date beginOfDate = calendar1.getTime();
        System.out.println(beginOfDate);
        Date disableTime = DateUtil.convert2Date("2019-03-18 14:00:48", DateUtil.ymdhms);
        Date addDays2 = DateUtil.addDays(disableTime,1);
        System.out.println(addDays2);
        System.out.println(DateUtil.diffTimes(addDays2, new Date()));
    }
    
    /**
     * 根据机构和角色类型获取用户
     * 
     * @param orgDTO
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }
    
    /**
     * 获取所有商务经理（组织名-大区名）
     * 
     * @param orgDTO
     * @return
     */
    private List<Map<String, Object>> getAllSaleList() {
        // 查询所有商务组
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> groupList = queryOrgByParam.getData();
        // 查询所有商务大区
        queryDTO.setOrgType(OrgTypeConstant.SWDQ);
        JSONResult<List<OrganizationRespDTO>> busArea =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> busAreaLsit = busArea.getData();
        // 查询所有商务经理
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.SWJL.name(), statusList);

        Map<Long, OrganizationRespDTO> orgMap = new HashMap<Long, OrganizationRespDTO>();
        // 生成<机构id，机构>map
        for (OrganizationRespDTO org : groupList) {
            orgMap.put(org.getId(), org);
        }
        for (OrganizationRespDTO org : busAreaLsit) {
            orgMap.put(org.getId(), org);
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 生成结果集，匹配电销组以及电销总监
        for (UserInfoDTO user : userList) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            OrganizationRespDTO group = orgMap.get(user.getOrgId());
            if (group != null) {
                OrganizationRespDTO area = orgMap.get(group.getParentId());
                resultMap.put("id", user.getId().toString());
                if (area != null) {
                    resultMap.put("name",
                            user.getName() + "(" + area.getName() + "--" + group.getName() + ")");
                } else {
                    resultMap.put("name", user.getName() + "(" + group.getName() + ")");

                }
                result.add(resultMap);
            }
        }
        return result;
    }

}
