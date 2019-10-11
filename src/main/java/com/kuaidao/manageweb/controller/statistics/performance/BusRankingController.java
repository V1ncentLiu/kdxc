package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: guhuitao
 * @create: 2019-09-23 15:39
 * 商务报表-业绩表-业绩排名
 **/
@Controller
@RequestMapping("/busRanking")
public class BusRankingController extends BaseStatisticsController {

  @Autowired
  private OrganizationFeignClient organizationFeignClient;

    /**
     * 商务组排名
     * @param request
     * @return
     */
    @RequestMapping("/busRankingList")
    public String  busTeamList(HttpServletRequest request){
        initBugOrg(request);
        return "reportBusPerformance/rankingGroup";
    }

    /**
     * 商务经理排名
     * @param request
     * @return
     */
    @RequestMapping("/busManageRankingList")
    public String busSaleList(HttpServletRequest request){
        initBugOrg(request);
        return "reportBusPerformance/rankingManager";
    }



}





