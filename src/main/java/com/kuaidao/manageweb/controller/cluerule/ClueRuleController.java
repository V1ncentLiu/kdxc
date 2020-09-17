package com.kuaidao.manageweb.controller.cluerule;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.businessconfig.dto.cluerule.ClueReleaseAndReceiveRuleDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IntegerEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.cluerule.ClueRuleFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 
 * @author Chen
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
    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;


    @RequiresPermissions("aggregation:clueRule:view")
    @RequestMapping("/clueRulePage")
    public String clueRulePage(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setRoleCode(RoleCodeEnum.DXZJ.name());
        req.setBusinessLine(user.getBusinessLine());
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error(
                    "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    req, userJr);
        }

        List<UserInfoDTO> data = userJr.getData();
        if (CollectionUtils.isNotEmpty(data)) {
            for (UserInfoDTO userInfoDTO : data) {
                String name = userInfoDTO.getName();
                String orgName = userInfoDTO.getOrgName();
                userInfoDTO.setName(name + " (" + orgName + ")");
            }
            request.setAttribute("accountList", data);
        }
        request.setAttribute("businessLine", user.getBusinessLine());
        List<DictionaryItemRespDTO> dictionaryByCode =
                getDictionaryByCode(DicCodeEnum.BUSINESS_LINE.getCode());
        for (DictionaryItemRespDTO dictionaryItemRespDTO : dictionaryByCode) {
            if (dictionaryItemRespDTO.getValue().equals(user.getBusinessLine() + "")) {
                request.setAttribute("businessLineName", dictionaryItemRespDTO.getName());
            }
        }
        return "cluerule/clueRulePage";
    }


    /**
     * 查询所有规则
     * 
     * @return
     */
    @RequiresPermissions("aggregation:clueRule:view")
    @PostMapping("/queryAllClueRule")
    @ResponseBody
    public JSONResult<ClueReleaseAndReceiveRuleDTO> queryAllClueRule() {
        UserInfoDTO user = getUser();
        IntegerEntity integerEntity = new IntegerEntity();
        integerEntity.setId(user.getBusinessLine());
        return ClueRuleFeignClient.queryAllClueRule(integerEntity);
    }

    /**
     * 更新
     * 
     * @param reqAndReceiveRuleDTO
     * @return
     */
    @RequiresPermissions("aggregation:clueRule:add")
    @PostMapping("/insertAndUpdateClueRule")
    @ResponseBody
    @LogRecord(operationType = OperationType.INSERT, description = "资源释放领取规则",
            menuName = MenuEnum.CLUE_RELEASE_RECEIVE_RULE)
    public JSONResult<Boolean> insertAndUpdateClueRule(@RequestBody ClueReleaseAndReceiveRuleDTO reqAndReceiveRuleDTO) {
        UserInfoDTO user = getUser();
        reqAndReceiveRuleDTO.setOperatorUser(user.getId());
        return ClueRuleFeignClient.insertAndUpdateClueRule(reqAndReceiveRuleDTO);
    }

    /**
     * 删除电销人员规则
     * 
     * @return
     */
    @PostMapping("/deleteTeleDirectorRuleById")
    @ResponseBody
    @LogRecord(operationType = OperationType.DELETE, description = "资源释放领取规则-删除电销规则",
            menuName = MenuEnum.CLUE_RELEASE_RECEIVE_RULE)
    public JSONResult<Boolean> deleteTeleDirectorRuleById(@RequestBody IdEntityLong idEntityLong) {
        Long id = idEntityLong.getId();
        if (id == null) {
            return CommonUtil.getParamIllegalJSONResult();
        }
        return ClueRuleFeignClient.deleteTeleDirectorRuleById(idEntityLong);
    }

    /**
     * 获取当前登录账号
     * 
     * @param orgDTO
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 查询字典表
     *
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }
}
