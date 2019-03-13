package com.kuaidao.manageweb.controller.cluerule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
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
import com.kuaidao.common.entity.JSONResult;
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
    
    @RequestMapping("/clueRulePage")
    public String clueRulePage(HttpServletRequest request) {
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setRoleCode(RoleCodeEnum.DXZJ.name());
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if(userJr==null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error("查询电销通话记录-获取组内顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",req,userJr);
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
    @PostMapping("/insertAndUpdateClueRule")
    @ResponseBody
    public JSONResult<Boolean> insertAndUpdateClueRule(@RequestBody ClueReleaseAndReceiveRuleDTO reqAndReceiveRuleDTO){
        return ClueRuleFeignClient.insertAndUpdateClueRule(reqAndReceiveRuleDTO);
    }
    
}
