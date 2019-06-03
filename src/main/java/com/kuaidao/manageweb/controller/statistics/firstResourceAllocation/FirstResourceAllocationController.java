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
import java.util.ArrayList;
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


    private List<Object> getHeadTitleGroup() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("首次分配资源数");
        headTitleList.add("联展");
        headTitleList.add("竞价");
        headTitleList.add("优化");
        headTitleList.add("信息流");
        headTitleList.add("官网");
        headTitleList.add("行业");
        headTitleList.add("其他");
        headTitleList.add("网民未接");
        return headTitleList;
    }

    private List<Object> getHeadTitleListPersion() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("电销");
        headTitleList.add("首次分配资源数\n");
        headTitleList.add("联展");
        headTitleList.add("竞价");
        headTitleList.add("优化");
        headTitleList.add("信息流");
        headTitleList.add("官网");
        headTitleList.add("行业");
        headTitleList.add("其他");
        headTitleList.add("网民未接");
        return headTitleList;
    }

    private List<Object> getHeadTitleListDayPersion() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("日期");
        headTitleList.add("电销");
        headTitleList.add("首次分配资源数");
        headTitleList.add("联展");
        headTitleList.add("竞价");
        headTitleList.add("优化");
        headTitleList.add("信息流");
        headTitleList.add("官网");
        headTitleList.add("行业");
        headTitleList.add("其他");
        headTitleList.add("网民未接");
        return headTitleList;
    }

    /**
     * 查询合计
     */
    private FirstResourceAllocationDto getCountTotal(List<FirstResourceAllocationDto> list){
        FirstResourceAllocationDto firstResourceAllocationDto = new FirstResourceAllocationDto();
        //首次分配资源数
        Long assignClueCount = list.stream().mapToLong(FirstResourceAllocationDto::getAssignClueCount).sum();
        //联展
        Long jointExhibition = list.stream().mapToLong(FirstResourceAllocationDto::getJointExhibition).sum();
        //竞价
        Long priceCompetition = list.stream().mapToLong(FirstResourceAllocationDto::getPriceCompetition).sum();
        //优化
        Long optimization = list.stream().mapToLong(FirstResourceAllocationDto::getOptimization).sum();
        //信息流
        Long informationFlow = list.stream().mapToLong(FirstResourceAllocationDto::getInformationFlow).sum();
        //官网
        Long officialWebsite = list.stream().mapToLong(FirstResourceAllocationDto::getOfficialWebsite).sum();
        //行业
        Long industry = list.stream().mapToLong(FirstResourceAllocationDto::getIndustry).sum();
        //其他
        Long other = list.stream().mapToLong(FirstResourceAllocationDto::getOther).sum();
        //网民未接
        Long netizensMissed = list.stream().mapToLong(FirstResourceAllocationDto::getNetizensMissed).sum();
        firstResourceAllocationDto.setAssignClueCount(assignClueCount);
        firstResourceAllocationDto.setJointExhibition(jointExhibition);
        firstResourceAllocationDto.setPriceCompetition(priceCompetition);
        firstResourceAllocationDto.setOptimization(optimization);
        firstResourceAllocationDto.setInformationFlow(informationFlow);
        firstResourceAllocationDto.setOfficialWebsite(officialWebsite);
        firstResourceAllocationDto.setIndustry(industry);
        firstResourceAllocationDto.setOther(other);
        firstResourceAllocationDto.setNetizensMissed(netizensMissed);
        return firstResourceAllocationDto;
    }



}
