package com.kuaidao.manageweb.controller.statistics.reportInvitation;

import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.statistics.invitation.InvitationFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: guhuitao
 * @create: 2019-10-07 14:11
 **/
@Controller
@RequestMapping("/invitation")
public class InvitationController extends BaseStatisticsController {

    @Autowired
    private InvitationFeignClient invitationFeignClient;





}
