/**
 * 
 */
package com.kuaidao.manageweb.controller.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.clue.BusScheduleFeignClient;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/business/schedule")
public class BusScheduleController {
    private static Logger logger = LoggerFactory.getLogger(BusScheduleController.class);
    @Autowired
    private BusScheduleFeignClient busScheduleFeignClient;


    /**
     * 到访记录提醒（定时任务）
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/visitRecordReminder")
    @ResponseBody
    @LogRecord(description = "到访记录提醒", operationType = OperationType.SCHEDULE,
            menuName = MenuEnum.INDEX)
    public void visitRecordReminder() {

        busScheduleFeignClient.visitRecordReminder();
    }

    /**
     * 签约记录提醒（定时任务）
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/signRecordReminder")
    @ResponseBody
    @LogRecord(description = "签约记录提醒", operationType = OperationType.SCHEDULE,
            menuName = MenuEnum.INDEX)
    public void signRecordReminder() {

        busScheduleFeignClient.signRecordReminder();
    }



}
