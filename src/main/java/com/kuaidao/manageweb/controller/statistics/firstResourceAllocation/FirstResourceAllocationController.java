package com.kuaidao.manageweb.controller.statistics.firstResourceAllocation;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.FirstResourceAllocation.FirstResourceAllocationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationDto;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/statistics/firstResourceAllocation")
public class FirstResourceAllocationController {

    private static Logger logger = LoggerFactory.getLogger(FirstResourceAllocationController.class);

    @Autowired
    private FirstResourceAllocationFeignClient firstResourceAllocationFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /**
     * 组页面查询
     */
    @RequestMapping("/getFirstResourceAllocationPage")
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPage(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto){
        return firstResourceAllocationFeignClient.getFirstResourceAllocationPage(firstResourceAllocationQueryDto);
    }

    /**
     * 组页面导出
     */
    @PostMapping("/exportFirstResourceAllocationPage")
    public void exportFirstResourceAllocationPage(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
            HttpServletResponse response){
        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationList =
                firstResourceAllocationFeignClient.getFirstResourceAllocationList(firstResourceAllocationQueryDto);
    }

    /**
     * 个人页面查询
     */
    @RequestMapping("/getFirstResourceAllocationPagePersion")
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto){
        return firstResourceAllocationFeignClient.getFirstResourceAllocationPagePersion(firstResourceAllocationQueryDto);
    }

    /**
     * 个人页面导出
     */
    @PostMapping("/exportFirstResourceAllocationPagePersion")
    public void exportFirstResourceAllocationPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
                                                          HttpServletResponse response){
        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationsPersion =
                firstResourceAllocationFeignClient.getFirstResourceAllocationsPersion(firstResourceAllocationQueryDto);
    }

    /**
     * 个人按天查询
     */
    @RequestMapping("/getFirstResourceAllocationDayPagePersion")
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationDayPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto){
        return firstResourceAllocationFeignClient.getFirstResourceAllocationDayPagePersion(firstResourceAllocationQueryDto);
    }

    /**
     * 个人按天导出
     */
    @PostMapping("/exportFirstResourceAllocationDayPagePersion")
    public void exportFirstResourceAllocationDayPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
                                                            HttpServletResponse response){

    }

    /**
     * 组
     * @return
     */
    @RequestMapping("/firstRATable")
    public String firstRATable() {
        return "reportforms/firstRATable";
    }

    /**
     * 合计
     * @return
     */
    @RequestMapping("/firstRATableSum")
    public String firstRATableSum() {
        return "reportforms/firstRATableSum";
    }
    /**
     * 组
     * @return
     */
    @RequestMapping("/firstRATableTeam")
    public String firstRATableTeam() {
        return "reportforms/firstRATableTeam";
    }

    /**
     * 个人
     * @return
     */
    @RequestMapping("/firstRATablePerson")
    public String firstRATablePerson() {
        return "reportforms/firstRATablePerson";
    }



}
