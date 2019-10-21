package com.kuaidao.manageweb.controller.statistics.resourcesReport;

import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.statistics.resourceFreeReceive.ResourceFreeReceiveFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: guhuitao
 * @create: 2019-08-30 16:59
 * 资源释放统计表
 **/
@Controller
@RequestMapping("/freedReport")
public class FreedReportController extends BaseStatisticsController {

    @Autowired
    private ResourceFreeReceiveFeignClient resourceFreeReceiveFeignClient;


    /**
     * 事业部统计
     * @param request
     * @return
     */
    @RequestMapping("/deptList")
    public String deptList(HttpServletRequest request){
        initSaleDept(request);
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportResources/resourceFreedDept";
    }

    /**
     * 电销组统计
     * @param request
     * @return
     */
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request){
        initSaleDept(request);
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportResources/resourceFreedGroup";
    }

    /**
     * 电销顾问统计
     * @param request
     * @return
     */
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request){
        initSaleDept(request);
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportResources/resourceFreedManager";
    }




}
