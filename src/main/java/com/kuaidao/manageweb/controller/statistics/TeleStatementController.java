package com.kuaidao.manageweb.controller.statistics;

import com.kuaidao.aggregation.dto.financing.FinanceLayoutDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.statistics.StatisticsFeignClient;
import com.kuaidao.stastics.dto.ResourceAllocationDto;
import com.kuaidao.stastics.dto.ResourceAllocationQueryDto;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 电销相关报表
 * @author  Devin.Chen
 * @date 2019-05-15 20:51:09
 * @version V1.0
 */
@Controller
@RequestMapping("/statistics/teleStatement")
public class TeleStatementController {

    private static Logger logger = LoggerFactory.getLogger(TeleStatementController.class);

    @Autowired
    private StatisticsFeignClient statisticsFeignClient;


    @RequestMapping("/resourceAllocation")
    public String resourceAllocationTable() {
        return "/reportforms/resourceAllocationTable";
    }

    /**
     * 资源分配页面
    * @return
     */
    @RequestMapping("/getResourceAllocationTable")
    @ResponseBody
    public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationTable() {
        ResourceAllocationQueryDto resourceAllocationQueryDto = new ResourceAllocationQueryDto();
        resourceAllocationQueryDto.setPageSize(10);
        resourceAllocationQueryDto.setPageNum(1);
        JSONResult<PageBean<ResourceAllocationDto>> resourceAllocationPage = statisticsFeignClient.getResourceAllocationPage(resourceAllocationQueryDto);
        System.out.println(resourceAllocationPage);
        return resourceAllocationPage;
    }


    @PostMapping("/exportResourceAllocationGroup")
    public void exportResourceAllocation(HttpServletResponse response) throws Exception {
        ResourceAllocationQueryDto resourceAllocationQueryDto = new ResourceAllocationQueryDto();
        resourceAllocationQueryDto.setOrg_Id(1l);
        JSONResult<List<ResourceAllocationDto>> resourceAllocationList = statisticsFeignClient.getResourceAllocationList(resourceAllocationQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());
        List<ResourceAllocationDto> orderList = resourceAllocationList.getData();
        for(int i = 0; i<orderList.size(); i++){
            ResourceAllocationDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(ra.getAssignClueCount());
            curList.add(ra.getJointExhibition());
            curList.add(ra.getPriceCompetition());
            curList.add(ra.getOptimization());
            curList.add(ra.getInformationFlow());
            curList.add(ra.getOfficialWebsite());
            curList.add(ra.getIndustry());
            curList.add(ra.getOther());
            curList.add(ra.getNetizensMissed());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "分配记录表" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    
    /**
     * 资源分配页面 合计 
    * @return
     */
    @RequestMapping("/resourceAllocationTableSum")
    public String resourceAllocationTableSum() {
        return "/reportforms/resourceAllocationTableSum";
    }
    
    
    /**
     * 资源分配页面  组 
    * @return
     */
    @RequestMapping("/resourceAllocationTableTeam")
    public String resourceAllocationTableTeam() {
        ResourceAllocationQueryDto resourceAllocationQueryDto = new ResourceAllocationQueryDto();
        resourceAllocationQueryDto.setPageSize(1);
        resourceAllocationQueryDto.setPageNum(10);
        JSONResult<PageBean<ResourceAllocationDto>> resourceAllocationPage = statisticsFeignClient.getResourceAllocationPage(resourceAllocationQueryDto);
        System.out.println(resourceAllocationPage);
        return "/reportforms/resourceAllocationTableTeam";
    }
    
    
    /**
     * 资源分配页面  个人 
    * @return
     */
    @RequestMapping("/resourceAllocationTablePerson")
    public String resourceAllocationTablePerson() {
        return "reportforms/resourceAllocationTablePerson";
    }
    
    
    /**
     * 电销顾问跟踪表页面
    * @return
     */
    @RequestMapping("/telemarketingFollowTable")
    public String telemarketingFollowTable() {
        return "reportforms/telemarketingFollowTable";
    }
    
    
    /**
     * 电销顾问跟踪表页面 合计
    * @return
     */
    @RequestMapping("/telemarketingFollowTableSum")
    public String telemarketingFollowTableSum() {
        return "reportforms/telemarketingFollowTableSum";
    }
    
    
    /**
     * 电销顾问跟踪表页面 组
    * @return
     */
    @RequestMapping("/telemarketingFollowTableTeam")
    public String telemarketingFollowTableTeam() {
        return "reportforms/telemarketingFollowTableTeam";
    }
    
    /**
     * 电销顾问跟踪表页面 个人
    * @return
     */
    @RequestMapping("/telemarketingFollowTablePerson")
    public String telemarketingFollowTablePerson() {
        return "reportforms/telemarketingFollowTablePerson";
    }
    
    
    /**
     * 电销顾问通话时长表页面
    * @return
     */
    @RequestMapping("/telemarketingCallTable")
    public String telemarketingCallTable() {
        return "reportforms/telemarketingCallTable";
    }
    
    /**
     * 电销顾问通话时长表页面  合计
    * @return
     */
    @RequestMapping("/telemarketingCallTableSum")
    public String telemarketingCallTableSum() {
        return "reportforms/telemarketingCallTableSum";
    }
    
    
    
    /**
     * 电销顾问通话时长表页面  组
    * @return
     */
    @RequestMapping("/telemarketingCallTableTeam")
    public String telemarketingCallTableTeam() {
        return "reportforms/telemarketingCallTableTeam";
    }
    
    
    /**
     * 电销顾问通话时长表页面  个人
    * @return
     */
    @RequestMapping("/telemarketingCallTablePerson")
    public String telemarketingCallTablePerson() {
        return "reportforms/telemarketingCallTablePerson";
    }



    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("分配资源数");
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
    
    
    
    
    
    
}
