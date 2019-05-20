package com.kuaidao.manageweb.controller.cluerule;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.cluerule.ClueReleaseAndReceiveRuleDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.cluerule.ClueRuleFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 
 * @author  Chen
 * @date 2019年3月11日 下午4:29:10   
 * @version V1.0
 */
@RequestMapping("/cluerule/clueRule")
@Controller
public class ClueRuleController {
    private static Logger logger = LoggerFactory.getLogger(ClueRuleController.class);
    
    @Autowired
    ClueRuleFeignClient ClueRuleFeignClient;
    
    @Autowired
    UserInfoFeignClient userInfoFeignClient;
    
    
    @RequiresPermissions("aggregation:clueRule:view")
    @RequestMapping("/clueRulePage")
    public String clueRulePage(HttpServletRequest request) {
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setRoleCode(RoleCodeEnum.DXZJ.name());
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if(userJr==null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error("查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",req,userJr);
        } 
        
        List<UserInfoDTO> data = userJr.getData();
        if(CollectionUtils.isNotEmpty(data)) {
            for (UserInfoDTO userInfoDTO : data) {
                String name = userInfoDTO.getName();
                String orgName = userInfoDTO.getOrgName();
                userInfoDTO.setName( name+" ("+orgName+")");
            }
            request.setAttribute("accountList", data);
        }
        return "cluerule/clueRulePage";
    }
    
    
    /**
     * 查询所有规则
     * @return
     */
    @RequiresPermissions("aggregation:clueRule:view")
    @PostMapping("/queryAllClueRule")
    @ResponseBody
    public JSONResult<ClueReleaseAndReceiveRuleDTO> queryAllClueRule(){
        return ClueRuleFeignClient.queryAllClueRule();
    }
    
    /**
     * 更新
     * @param reqAndReceiveRuleDTO
     * @return
     */
    @RequiresPermissions("aggregation:clueRule:add")
    @PostMapping("/insertAndUpdateClueRule")
    @ResponseBody
    @LogRecord(operationType = OperationType.INSERT, description = "资源释放领取规则",menuName = MenuEnum.CLUE_RELEASE_RECEIVE_RULE)
    public JSONResult<Boolean> insertAndUpdateClueRule(@RequestBody ClueReleaseAndReceiveRuleDTO reqAndReceiveRuleDTO){
        return ClueRuleFeignClient.insertAndUpdateClueRule(reqAndReceiveRuleDTO);
    }
    
    /**
     * 删除电销人员规则
     * @return
     */
    @PostMapping("/deleteTeleDirectorRuleById")
    @ResponseBody
    @LogRecord(operationType = OperationType.DELETE, description = "资源释放领取规则-删除电销规则",menuName = MenuEnum.CLUE_RELEASE_RECEIVE_RULE)
    public JSONResult<Boolean> deleteTeleDirectorRuleById(@RequestBody IdEntityLong idEntityLong){
        Long id = idEntityLong.getId();
        if(id==null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        return ClueRuleFeignClient.deleteTeleDirectorRuleById(idEntityLong);
    }
    
}
