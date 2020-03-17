package com.kuaidao.manageweb.controller.financing;

import javax.servlet.http.HttpServletRequest;
import com.kuaidao.aggregation.dto.financing.FinanceOverCostReqDto;
import com.kuaidao.aggregation.dto.financing.FinanceOverCostRespDto;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.financing.OverCostFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 超成本申请
 * 
 * @author fanjd
 * @date 2020年3月13日 9:23:08
 * @version V1.0
 */

@Controller
@RequestMapping("/financing/overCost")
public class OverCostController {

    @Autowired
    private OverCostFeignClient overCostFeignClient;

    /**
     * 申请页面
     *
     * @return
     */
    @RequestMapping("/overCostApplyPage")
    public String balanceAccountPage(HttpServletRequest request) {

        return "financing/overCostApply";
    }
    /**
     * 超红线申请列表
     *
     * @return
     */
    @RequestMapping("/overCostApplyList")
    @ResponseBody
    public JSONResult<PageBean<FinanceOverCostRespDto>> overCostApplyList(HttpServletRequest request, @RequestBody FinanceOverCostReqDto financeOverCostReqDto) {
        UserInfoDTO userInfoDTO = getUser();
        financeOverCostReqDto.setUserId(userInfoDTO.getId());
        financeOverCostReqDto.setRoleCode(userInfoDTO.getRoleCode());
        JSONResult<PageBean<FinanceOverCostRespDto>> list = overCostFeignClient.overCostApplyList(financeOverCostReqDto);
        return list;
    }

    /**
     * 超成本申请确认页面
     *
     * @return
     */
    @RequestMapping("/overCostconfirmPage")
    public String overCostconfirmPage(HttpServletRequest request) {

        return "financing/overCostApply";
    }

    /**
     * 超成本申请确认
     *
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2020/3/12 11:37
     * @since: 1.0.0
     **/
    @PostMapping("/confirm")
    @LogRecord(description = "超成本申请确认", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.OVERCOST_CONFIRM)

    public JSONResult<String> confirm(@RequestBody FinanceOverCostReqDto reqDto) {
        return overCostFeignClient.confirm(reqDto);
    }

    /**
     * 超成本申请驳回
     *
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2020/3/12 11:37
     * @since: 1.0.0
     **/
    @PostMapping("/reject")
    @LogRecord(description = "超成本申请驳回", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.OVERCOST_REJECT)
    public JSONResult<String> reject(@RequestBody FinanceOverCostReqDto reqDto) {
        return overCostFeignClient.reject(reqDto);
    }

    /**
     * 获取当前登录账号
     *
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }
}
