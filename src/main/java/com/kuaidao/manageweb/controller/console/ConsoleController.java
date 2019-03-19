package com.kuaidao.manageweb.controller.console;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.TeleConsoleReqDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.BusReceiveFeignClient;
import com.kuaidao.manageweb.feign.clue.AppiontmentFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

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

    /***
     * 跳转控制台页面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleName = roleInfoDTO.getRoleName();
        String path= "";
        if(RoleCodeEnum.DXCYGW.value().equals(roleName)) {
            //电销顾问
        }else if(RoleCodeEnum.DXZJ.value().equals(roleName)) {
            //电销总监
        }else if(RoleCodeEnum.SWJL.value().equals(roleName)) {
            //商务经理
        }else if(RoleCodeEnum.SWZJ.value().equals(roleName)) {
            //商务总监
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
     * 统计今日分配资源数
     * @param reqDTO
     * @return
     */
    @PostMapping("/countAssignClueNum")
    @ResponseBody
    public JSONResult<Integer> countAssignClueNum(@RequestBody TeleConsoleReqDTO reqDTO){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setTeleSaleId(curLoginUser.getId());
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
     * 统计今日領取数
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
        return  clueBasicFeignClient.countAssignClueNum(reqDTO);
    }
    
    
    
   /***
    * 控制台 今日邀约单 不包括 删除的
    * @param teleConsoleReqDTO
    * @return
    */
   @PostMapping("/countTodayAppiontmentNum")
   @ResponseBody
   public JSONResult<Integer> countTodayAppiontmentNum(@RequestBody TeleConsoleReqDTO teleConsoleReqDTO){
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
       Long id = curLoginUser.getId();
       teleConsoleReqDTO.setTeleSaleId(id);
       teleConsoleReqDTO.setStartTime(DateUtil.getTodayStartTime());
       teleConsoleReqDTO.setEndTime(new Date());
       return appiontmentFeignClient.countTodayAppiontmentNum(teleConsoleReqDTO);
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
    }

}
