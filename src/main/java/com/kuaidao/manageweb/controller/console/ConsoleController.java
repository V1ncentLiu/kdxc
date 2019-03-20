package com.kuaidao.manageweb.controller.console;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationClueDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationCluePageParam;
import com.kuaidao.aggregation.dto.console.BusinessConsolePanelRespDTO;
import com.kuaidao.aggregation.dto.console.BusinessConsoleReqDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.common.constant.CluePhase;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.BusReceiveFeignClient;
import com.kuaidao.manageweb.feign.clue.AppiontmentFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.sign.SignRecordFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.feign.visit.VisitRecordFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveRespDTO;
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
    /***
     * 跳转控制台页面
     * @return
     */
    @RequestMapping("/index")
    public String index(String type ) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleName = roleInfoDTO.getRoleName();
        String path= "";
/*        if(RoleCodeEnum.DXCYGW.value().equals(roleName)) {
            //电销顾问
        }else if(RoleCodeEnum.DXZJ.value().equals(roleName)) {
            //电销总监
        }else if(RoleCodeEnum.SWJL.value().equals(roleName)) {
            //商务经理
        }else if(RoleCodeEnum.SWZJ.value().equals(roleName)) {
            //商务总监
        }*/
        if(type.equals("1")) {
            path = "console/consoleTelemarketing";
        }else if(type.equals("2")) {
            path="console/consoleTelMajordomo";
        }else if(type.equals("3")) {
            path="console/consoleBusinessManager";
        }else if(type.equals("4")) {
            path="console/consoleBusinessMajordomo";
        }
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
   public JSONResult<PageBean<CustomerClueDTO>> listTodayFollowClue() throws Exception{
       CustomerClueQueryDTO queryDto = new CustomerClueQueryDTO();
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
   public JSONResult<List<PendingAllocationClueDTO>> listUnAssignClue(
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

}
