package com.kuaidao.manageweb.controller.statistics.busTeleDistribution;


import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.statistics.busTeleDistribution.BusTeleDistributionFeignClient;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 商务报表 / 业绩报表 / 电销组织分布表
 */
@RequestMapping("/busTeleDistribution")
@Controller
public class BusTeleDistributionController extends BaseStatisticsController {

    @Autowired
    private BusTeleDistributionFeignClient busTeleDistributionFeignClient;


    /**
     * 一级页面跳转
     */
    @RequestMapping("/toTeleOrganizeDistributed")
    public String toTeleOrganizationDistributed(HttpServletRequest request) {
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportBusPerformance/teleOrganizeDistributed";
    }

    /**
     * 二级页面跳转
     */
    @RequestMapping("/toTeleOrganizeDistributedDetail")
    public String toTeleOrganizationDistributedDetail(Long busAreaId,Long businessGroupId,Long startTime,Long endTime,Long businessManagerId,Long groupId,Long projectId,BaseBusQueryDto baseBusQueryDto ,HttpServletRequest request) {
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportBusPerformance/teleOrganizeDistributedDetail";
    }

    /**
     *
     * 一级页面查询（不分页）
     */
    @RequestMapping("/getOneAllList")
    public JSONResult<Map<String,Object>> getOneAllList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        initAuth(baseBusQueryDto);
        JSONResult<Map<String, Object>> oneAllList = busTeleDistributionFeignClient.getOneAllList(baseBusQueryDto);
        return oneAllList;
    }
    /**
     *
     * 一级页面查询（分页）
     */
    @RequestMapping("/getOnePageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getOnePageList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        initAuth(baseBusQueryDto);
        JSONResult<Map<String, Object>> onePageList = busTeleDistributionFeignClient.getOnePageList(baseBusQueryDto);
        return onePageList;
    }

    /**
     *
     * 二级页面查询（不分页）
     */
    @RequestMapping("/getTwoAllList")
    public JSONResult<Map<String,Object>> getTwoAllList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        initAuth(baseBusQueryDto);
        JSONResult<Map<String, Object>> twoAllList = busTeleDistributionFeignClient.getTwoAllList(baseBusQueryDto);
        return twoAllList;
    }
    /**
     *
     * 二级页面查询（分页）
     */
    @RequestMapping("/getTwoPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getTwoPageList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        initAuth(baseBusQueryDto);
        JSONResult<Map<String, Object>> twoPageList = busTeleDistributionFeignClient.getTwoPageList(baseBusQueryDto);
        return twoPageList;
    }
}
