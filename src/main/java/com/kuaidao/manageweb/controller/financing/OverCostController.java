package com.kuaidao.manageweb.controller.financing;

import javax.servlet.http.HttpServletRequest;
import com.kuaidao.aggregation.dto.financing.FinanceOverCostReqDto;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.financing.OverCostFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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


}
