package com.kuaidao.manageweb.controller.statistics.busGroupRanking;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.busGroupRanking.BusGroupRankingFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.stastics.dto.base.BaseBusinessDto;
import com.kuaidao.stastics.dto.receptionVisit.ReceptionVisitQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * / 商务报表 / 业绩报表 / 集团项目业绩表
 */
@Slf4j
@RequestMapping("/busGroupRanking")
@Controller
public class BusGroupRankingController extends BaseStatisticsController {

    @Autowired
    private BusGroupRankingFeignClient busGroupRankingFeignClient;


    /**
     * 一级页面跳转
     */
    @RequestMapping("/toGroupProjectPerformance")
    public String toGroupProjectPerformance(HttpServletRequest request) {
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportBusPerformance/groupProjectPerformance";
    }

    /**
     * 二级页面跳转
     */
    @RequestMapping("/toGroupProjectPerformanceDetail")
    public String toGroupProjectPerformanceDetail(Long busAreaId,Long businessGroupId,Long startTime,Long endTime,Long businessManagerId,Long groupId,Long projectId,HttpServletRequest request) {
        initParam(busAreaId,businessGroupId,startTime,endTime,businessManagerId,groupId,projectId,request);
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportBusPerformance/groupProjectPerformanceDetail";
    }


    /**
     * 一级页面分页查询
     */
    @RequestMapping("/getOnePageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getOneBusGroupRankingPageList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        initAuth(baseBusQueryDto);
        JSONResult<Map<String, Object>> oneBusGroupRankingPageList = busGroupRankingFeignClient.getOneBusGroupRankingPageList(baseBusQueryDto);
        return oneBusGroupRankingPageList;
    }
    /**
     * 一级页面 导出
     */
    @RequestMapping("/exportOneList")
    public void exportOneList(HttpServletResponse response, @RequestBody BaseBusQueryDto baseBusQueryDto) throws IOException {
        initAuth(baseBusQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTitleList(1));
        JSONResult<Map<String, Object>> result =  busGroupRankingFeignClient.getOneBusGroupRankingList(baseBusQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<BaseBusinessDto> orderList = JSON.parseArray(listTxt, BaseBusinessDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        BaseBusinessDto sumReadd = JSON.parseObject(totalDataStr, BaseBusinessDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,1);
        buildList(dataList, orderList,1);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "商务报表集团项目业绩表" +baseBusQueryDto.getStartTime()+"-"+baseBusQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    /**
     * 二级页面分页查询
     */
    @RequestMapping("/getTwoPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getTwoBusGroupRankingPageList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        initAuth(baseBusQueryDto);
        JSONResult<Map<String, Object>> twoBusGroupRankingPageList = busGroupRankingFeignClient.getTwoBusGroupRankingPageList(baseBusQueryDto);
        return twoBusGroupRankingPageList;
    }
    /**
     * 二级页面查询全部
     */
    @RequestMapping("/exportTwoList")
    public void exportTwoList(HttpServletResponse response, @RequestBody BaseBusQueryDto baseBusQueryDto) throws IOException {
        initAuth(baseBusQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTitleList(2));
        JSONResult<Map<String, Object>> result =  busGroupRankingFeignClient.getTwoBusGroupRankingList(baseBusQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<BaseBusinessDto> orderList = JSON.parseArray(listTxt, BaseBusinessDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        BaseBusinessDto sumReadd = JSON.parseObject(totalDataStr, BaseBusinessDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,2);
        buildList(dataList, orderList,2);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "商务报表集团项目业绩表" +baseBusQueryDto.getStartTime()+"-"+baseBusQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }


    private List<Object> getTitleList(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("商务大区");
        headTitleList.add("餐饮集团");
        if(type == 2){
            headTitleList.add("项目");
        }
        headTitleList.add("首访数");
        headTitleList.add("签约数");
        headTitleList.add("签约率");
        headTitleList.add("净业绩金额");
        headTitleList.add("签约单笔");
        headTitleList.add("来访单笔");
        return headTitleList;
    }

    private void addTotalExportData(BaseBusinessDto ra, List<List<Object>> dataList, Integer type) {
        List<Object> curList = new ArrayList<>();
        curList.add("");
        curList.add("");
        curList.add("合计");
        if(type.equals(2)){
            curList.add("");
        }
        curList.add(ra.getFirstVisitNum());
        curList.add(ra.getSignNum());
        curList.add(ra.getSignRate());
        curList.add(ra.getAmount());
        curList.add(ra.getSignSingle());
        curList.add(ra.getFirstVisitMoney());
        dataList.add(curList);
    }

    private void buildList(List<List<Object>> dataList, List<BaseBusinessDto> orderList, Integer type) {
        for(int i = 0; i<orderList.size(); i++){
            BaseBusinessDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getBusinessAreaName());
            curList.add(ra.getGroupName());
            if(type == 2){
                curList.add(ra.getProjectName());
            }
            curList.add(ra.getFirstVisitNum());
            curList.add(ra.getSignNum());
            curList.add(ra.getSignRate());
            curList.add(ra.getAmount());
            curList.add(ra.getSignSingle());
            curList.add(ra.getFirstVisitMoney());
            dataList.add(curList);
        }
    }

    private void initParam(Long busAreaId,Long businessGroupId,Long startTime,Long endTime,Long businessManagerId,Long groupId,Long projectId,HttpServletRequest request){
        BaseBusQueryDto baseBusQueryDto = new BaseBusQueryDto();
        baseBusQueryDto.setBusAreaId(busAreaId);
        baseBusQueryDto.setBusinessGroupId(businessGroupId);
        baseBusQueryDto.setStartTime(startTime);
        baseBusQueryDto.setEndTime(endTime);
        baseBusQueryDto.setBusinessManagerId(businessManagerId);
        baseBusQueryDto.setGroupId(groupId);
        baseBusQueryDto.setProjectId(projectId);
        request.setAttribute("baseBusQueryDto",baseBusQueryDto);
    }

}
