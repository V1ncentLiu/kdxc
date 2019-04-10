package com.kuaidao.manageweb.controller.financing;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *  退返款
 * @author  Chen
 * @date 2019年4月10日 下午7:23:08   
 * @version V1.0
 */
@RequestMapping("/financing/refund")
@Controller
public class RefundController {
    
    /**
     * 退款申请页面
     * @return
     */
    @RequestMapping("/refundApplyPage")
    public String refundApplyPage() {
        return "financing/refundApplyPage";
    }
    /**
     * 退款确认页面
     * @return
     */
    @RequestMapping("/refundConfirmPage")
    public String refundConfirmPage() {
        return "financing/refundConfirmPage";
    }
    
    /***
     * 返款申请页面
     * @return
     */
    @RequestMapping("/rebateApplayPage")
    public String rebateApplayPage() {
        return "financing/rebateApplayPage";
    }
    /***
     * 返款确认页面页面
     * @return
     */
    @RequestMapping("/rebateConfirmPage")
    public String rebateConfirmPage() {
        return "financing/rebateConfirmPage";
    }
    

}
