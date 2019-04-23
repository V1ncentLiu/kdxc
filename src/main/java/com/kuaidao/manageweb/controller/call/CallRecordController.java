package com.kuaidao.manageweb.controller.call;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.call.CallRecordCountDTO;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/call/callRecord")
public class CallRecordController {
    
    private static Logger logger = LoggerFactory.getLogger(CallRecordController.class);
    
    @Autowired
    CallRecordFeign callRecordFeign;
    @Autowired
    UserInfoFeignClient userInfoFeignClient;
    
    
    /**
     * 我的通话记录
     * @return
     */
    @RequiresPermissions("aggregation:myCallRecord:view")
    @RequestMapping("/myCallRecord")
    public String myCallRecord() {
        return "call/myCallRecord";
    }
    
    
    /**
     * 电销顾问通话记录
     * @return
     */
    @RequiresPermissions("aggregation:telCallRecord:view")
    @RequestMapping("/telCallRecord")
    public String telCallRecord(HttpServletRequest request) {
        
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if(roleList!=null && roleList.size()!=0) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            String roleName = roleInfoDTO.getRoleName();
            if(RoleCodeEnum.DXZJ.value().equals(roleName)) {
                UserOrgRoleReq req = new UserOrgRoleReq();
                req.setOrgId(orgId);
                req.setRoleCode(RoleCodeEnum.DXCYGW.name());
                JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                if(userJr==null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
                    logger.error("查询电销通话记录-获取组内顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",req,userJr);
                }
                request.setAttribute("tmList",userJr.getData());
            }
        }
         
   
      
        return "call/telCallRecord";
    }
    

    
    /**
     * 电销顾问总时长统计
     * @return
     */
    @RequiresPermissions("aggregation:tmTalkTimeCallRecord:view")
    @RequestMapping("/tmTalkTimeCallRecord")
    public String tmTalkTimeCallRecord() {
        return "call/tmTalkTimeCallRecord";
    }
    
    /**
     * 获取我的通话记录 分页展示 ，参数模糊匹配
     * @return
     */
    @RequiresPermissions("aggregation:myCallRecord:view")
    @PostMapping("/listMyCallRecord")
    @ResponseBody
    public JSONResult<Map<String,Object>> listMyCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getId();
        List<Long> accountIdList = new ArrayList<Long>();
        accountIdList.add(id);
        myCallRecordReqDTO.setAccountIdList(accountIdList);
        return callRecordFeign.listMyCallRecord(myCallRecordReqDTO);
    }
    
    /**
     * 我的通话记录 统计总时长
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/countMyCallRecordTalkTime")
    @ResponseBody
    public JSONResult<Integer> countMyCallRecordTalkTime(@RequestBody CallRecordReqDTO myCallRecordReqDTO){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getId();
        List<Long> accountIdList = new ArrayList<Long>();
        accountIdList.add(id);
        myCallRecordReqDTO.setAccountIdList(accountIdList);
        return callRecordFeign.countMyCallRecordTalkTime(myCallRecordReqDTO);
    }
    
    /**
     * 电销通话记录  分页展示 ，参数模糊匹配
     * @return
     */
    @RequiresPermissions("aggregation:telCallRecord:view")
    @PostMapping("/listAllTmCallRecord")
    @ResponseBody
    public JSONResult<Map<String,Object>> listAllTmCallRecord(@RequestBody CallRecordReqDTO myCallRecordReqDTO) {
      //根据角色查询  下属顾问
       UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if(roleList!=null && roleList.size()!=0) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            String roleName = roleInfoDTO.getRoleName();
            if(RoleCodeEnum.DXZJ.value().equals(roleName)) {
                List<Long> accountIdList = myCallRecordReqDTO.getAccountIdList();
                if(accountIdList==null || accountIdList.size()==0) {
                    UserOrgRoleReq req = new UserOrgRoleReq();
                    req.setOrgId(orgId);
                    req.setRoleCode(RoleCodeEnum.DXCYGW.name());
                    JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                    if(userJr==null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
                        logger.error("查询电销通话记录-获取组内顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",req,userJr);
                        return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                    }
                    
                    List<UserInfoDTO> data = userJr.getData();
                    if(data==null || data.size()==0) {
                        return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该电销总监下无顾问");
                    }
                    List<Long> idList = data.stream().map(user->user.getId()).collect(Collectors.toList());
                    myCallRecordReqDTO.setAccountIdList(idList);
                }
                return callRecordFeign.listAllTmCallRecord(myCallRecordReqDTO);

              
            }else {
                return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"只有电销总监才可以查询");
            }
            
        }else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"只有电销总监才可以查询");
        }
    }
    
    
    /***
     * 电销通话时长统计 分页
     * @param myCallRecordReqDTO
     * @return
     */
    @RequiresPermissions("aggregation:tmTalkTimeCallRecord:view")
    @PostMapping("/listAllTmCallTalkTime")
    @ResponseBody
    public JSONResult<Map<String,Object>> listAllTmCallTalkTime(@RequestBody CallRecordReqDTO myCallRecordReqDTO){
        
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if(roleList!=null && roleList.size()!=0) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            String roleName = roleInfoDTO.getRoleName();
            if(RoleCodeEnum.DXZJ.value().equals(roleName)) {
                String accoutName = myCallRecordReqDTO.getAccoutName();
                if(StringUtils.isBlank(accoutName)) {
                    UserOrgRoleReq req = new UserOrgRoleReq();
                    req.setOrgId(orgId);
                    req.setRoleCode(RoleCodeEnum.DXCYGW.name());
                    JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                    if(userJr==null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
                        logger.error("查询电销通话记录-获取组内顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",req,userJr);
                        return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                    }
                    
                    List<UserInfoDTO> data = userJr.getData();
                    if(data==null || data.size()==0) {
                        return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该电销总监下无顾问");
                    }
                    List<Long> idList = data.stream().map(user->user.getId()).collect(Collectors.toList());
                    myCallRecordReqDTO.setAccountIdList(idList); 
                }
                
                return callRecordFeign.listAllTmCallTalkTime(myCallRecordReqDTO);
            }else {
                return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"只有电销总监才可以查询");
            }
        }else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"只有电销总监才可以查询"); 
        }
    }
    
    
    /**
     * 根据clueId 或 customerPhone 查询 通话记录
     * @param myCallRecordReqDTO 参数 clueid 或 customerPhone 
     * @return
     */
    @PostMapping("/listTmCallReacordByParams")
    @ResponseBody
    public  JSONResult<PageBean<CallRecordRespDTO>> listTmCallReacordByParams(@RequestBody CallRecordReqDTO myCallRecordReqDTO){
        return callRecordFeign.listTmCallReacordByParams(myCallRecordReqDTO); 
     }
    
    /**
     * 根据clueId 或 customerPhone 查询 通话记录
     * @param myCallRecordReqDTO 参数 clueid 或 customerPhone 
     * @return
     */
    @PostMapping("/listTmCallReacordByParamsNoPage")
    @ResponseBody
    JSONResult<List<CallRecordRespDTO>> listTmCallReacordByParamsNoPage(@RequestBody CallRecordReqDTO myCallRecordReqDTO){
        return callRecordFeign.listTmCallReacordByParamsNoPage(myCallRecordReqDTO);
    }
    
    /**
     *  获取天润通话记录地址 根据 记录Id
     * @param reqDTO
     * @return
     */
    @PostMapping("/getRecordFile")
    @ResponseBody
    public JSONResult<String> getRecordFile(@RequestBody IdEntity idEntity){
        return callRecordFeign.getRecordFile(idEntity);
    }
    
    /**
     * 根据clueId List 分组统计 拨打次数
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/countCallRecordTotalByClueIdList")
    @ResponseBody
    public JSONResult<List<CallRecordCountDTO>> countCallRecordTotalByClueIdList(@RequestBody CallRecordReqDTO myCallRecordReqDTO){
        return callRecordFeign.countCallRecordTotalByClueIdList(myCallRecordReqDTO);
    }
    
    
    
    /**
     * 统计 通话时长
     * @param teleConsoleReqDTO
     * @return
     */
    @PostMapping("/countTodayTalkTime")
    @ResponseBody
    public JSONResult<Integer> countTodayTalkTime(@RequestBody TeleConsoleReqDTO teleConsoleReqDTO){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getId();
        teleConsoleReqDTO.setTeleSaleId(id);
        teleConsoleReqDTO.setStartTime(DateUtil.getTodayStartTime());
        teleConsoleReqDTO.setEndTime(new Date());
        return callRecordFeign.countTodayTalkTime(teleConsoleReqDTO);
    }
    
}
