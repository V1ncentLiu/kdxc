package com.kuaidao.manageweb.controller.call;

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
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
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
        return callRecordFeign.listMyCallRecord(myCallRecordReqDTO);
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
        List<Long> accountIdList = myCallRecordReqDTO.getAccountIdList();
        if(accountIdList==null || accountIdList.size()==0) {
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
                        return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                    }
                    
                    List<UserInfoDTO> data = userJr.getData();
                    if(data==null || data.size()==0) {
                        return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该用户下无顾问");
                    }
                    List<Long> idList = data.stream().map(user->user.getId()).collect(Collectors.toList());
                    myCallRecordReqDTO.setAccountIdList(idList);
                  
                }else {
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"只有电销总监才可以查询");
                }
                
            }
           

        }
      
        
       return callRecordFeign.listAllTmCallRecord(myCallRecordReqDTO);
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
        return callRecordFeign.listAllTmCallTalkTime(myCallRecordReqDTO);
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
    
    
}
