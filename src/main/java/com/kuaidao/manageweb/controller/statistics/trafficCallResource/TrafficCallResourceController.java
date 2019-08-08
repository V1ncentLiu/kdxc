package com.kuaidao.manageweb.controller.statistics.trafficCallResource;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.statistics.trafficCallResource.TrafficCallResourceFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.stastics.dto.trafficCallResource.TrafficCallResourceDto;
import com.kuaidao.stastics.dto.trafficCallResource.TrafficCallResourceQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/statistics/trafficCallResource")
public class TrafficCallResourceController {

    @Autowired
    private TrafficCallResourceFeignClient trafficCallResourceFeignClient;
    private static final Integer GROUP = 1;
    private static final Integer GROUP_PERSON = 2;
    private static final Integer GROUP_PERSON_DAY = 3;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    /**
     * 组一级页面跳转
     */
    @RequestMapping("/resourceAllocationDispose")
    public String resourceAllocationDispose(Integer category,String startTime,String endTime,Integer newResource,HttpServletRequest request) {
        pageParams(category,startTime,endTime,newResource,request);
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportformsTelephone/resourceAllocationDispose";
    }
    /**
     * 人二级页面跳转
     */
    @RequestMapping("/resourceAllocationDisposePerson")
    public String resourceAllocationPersonDispose(Integer category,String startTime,String endTime,Integer newResource,HttpServletRequest request) {
        pageParams(category,startTime,endTime,newResource,request);
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportformsTelephone/resourceAllocationDisposePerson";
    }
    /**
     * 人+天三级页面跳转
     */
    @RequestMapping("/resourceAllocationDisposePersonDay")
    public String resourceAllocationDisposeTeam(Integer category,String startTime,String endTime,Integer newResource,HttpServletRequest request) {
        pageParams(category,startTime,endTime,newResource,request);
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportformsTelephone/resourceAllocationDisposePersonDay";
    }
    /**
     * 一级页面查询组（分页）
     */
    @PostMapping("/getGroupPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getGroupPageList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto){
        return trafficCallResourceFeignClient.getGroupPageList(trafficCallResourceQueryDto);
    }
    /**
     * 二级页面查询(分页)
     */
    @PostMapping("/getPersonPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getPersonPageList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto){
        return trafficCallResourceFeignClient.getPersonPageList(trafficCallResourceQueryDto);
    }
    /**
     * 三级页面查询(分页)
     */
    @PostMapping("/getPersonDayPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getPersonDayPageList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto){
        return trafficCallResourceFeignClient.getPersonDayPageList(trafficCallResourceQueryDto);
    }
    //-----------------------------------------------------------------------------------------
    /**
     * 一级页面查询组（不分页）
     */
    @PostMapping("/exportGroupAllList")
    public void exportGroupAllList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto,HttpServletResponse response) throws IOException {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTrafficCallResourceHeadTitle(GROUP));
        JSONResult<Map<String, Object>> result =  trafficCallResourceFeignClient.getGroupAllList(trafficCallResourceQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<TrafficCallResourceDto> orderList = JSON.parseArray(listTxt, TrafficCallResourceDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        TrafficCallResourceDto sumReadd = JSON.parseObject(totalDataStr, TrafficCallResourceDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP);
        buildList(dataList, orderList,GROUP);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String startTime = trafficCallResourceQueryDto.getStartTime();
        String endTime = trafficCallResourceQueryDto.getEndTime();
        String name = "话务资源分配处理明细表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    /**
     * 二级页面查询(不分页)
     */
    @PostMapping("/exportPersonAllList")
    public void exportPersonAllList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto,HttpServletResponse response) throws IOException {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTrafficCallResourceHeadTitle(GROUP_PERSON));
        JSONResult<Map<String, Object>> result =  trafficCallResourceFeignClient.getPersonAllList(trafficCallResourceQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<TrafficCallResourceDto> orderList = JSON.parseArray(listTxt, TrafficCallResourceDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        TrafficCallResourceDto sumReadd = JSON.parseObject(totalDataStr, TrafficCallResourceDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP_PERSON);
        buildList(dataList, orderList,GROUP_PERSON);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String startTime = trafficCallResourceQueryDto.getStartTime();
        String endTime = trafficCallResourceQueryDto.getEndTime();
        String name = "话务资源分配处理明细表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    /**
     * 三级页面查询(不分页)
     */
    @PostMapping("/exportPersonDayAllList")
    public void exportPersonDayAllList(@RequestBody TrafficCallResourceQueryDto trafficCallResourceQueryDto,HttpServletResponse response) throws IOException {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTrafficCallResourceHeadTitle(GROUP_PERSON_DAY));
        JSONResult<Map<String, Object>> result =  trafficCallResourceFeignClient.getPersonDayAllList(trafficCallResourceQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<TrafficCallResourceDto> orderList = JSON.parseArray(listTxt, TrafficCallResourceDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        TrafficCallResourceDto sumReadd = JSON.parseObject(totalDataStr, TrafficCallResourceDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP_PERSON_DAY);
        buildList(dataList, orderList,GROUP_PERSON_DAY);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String startTime = trafficCallResourceQueryDto.getStartTime();
        String endTime = trafficCallResourceQueryDto.getEndTime();
        String name = "话务资源分配处理明细表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    private void pageParams(Integer category,String startTime,String endTime,Integer newResource,HttpServletRequest request){
        TrafficCallResourceQueryDto trafficCallResourceQueryDto = new TrafficCallResourceQueryDto();
        trafficCallResourceQueryDto.setCategory(category);
        trafficCallResourceQueryDto.setStartTime(startTime);
        trafficCallResourceQueryDto.setEndTime(endTime);
        trafficCallResourceQueryDto.setNewResource(newResource);
        request.setAttribute("trafficCallResourceQueryDto",trafficCallResourceQueryDto);
    }

    private List<Object> getTrafficCallResourceHeadTitle(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        if(type.equals(GROUP)){
            headTitleList.add("是否新资源");
            headTitleList.add("接收资源");
        }
        if(type.equals(GROUP_PERSON)){
            headTitleList.add("话务专员");
            headTitleList.add("是否新资源");
        }
        if(type.equals(GROUP_PERSON_DAY)){
            headTitleList.add("统计日期");
            headTitleList.add("是否新资源");
        }
        headTitleList.add("分配资源量");
        headTitleList.add("跟访量");
        headTitleList.add("转电销量");
        headTitleList.add("有效资源");
        headTitleList.add("跟访率");
        headTitleList.add("转电销率");
        headTitleList.add("资源有效率");
        return headTitleList;
    }
    private void buildList(List<List<Object>> dataList, List<TrafficCallResourceDto> orderList, Integer type) {
        for(int i = 0; i<orderList.size(); i++){
            TrafficCallResourceDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            if(type.equals(GROUP)){
                curList.add(ra.getNewResource()==0?"否":"是");
                curList.add(ra.getReceiveCount());
            }
            if(type.equals(GROUP_PERSON)){
                curList.add(ra.getUserName());
                curList.add(ra.getNewResource()==0?"否":"是");
            }
            if(type.equals(GROUP_PERSON_DAY)){
                curList.add(ra.getDays());
                curList.add(ra.getNewResource()==0?"否":"是");
            }
            curList.add(ra.getAssignCount());
            curList.add(ra.getTrackingCount());
            curList.add(ra.getTransCount());
            curList.add(ra.getEffectiveCount());
            curList.add(formatPercent(ra.getFollowRate()));
            curList.add(formatPercent(ra.getTransRate()));
            curList.add(formatPercent(ra.getEffectiveRate()));
            dataList.add(curList);
        }
    }
    private void addTotalExportData(TrafficCallResourceDto ra, List<List<Object>> dataList,Integer type) {
        List<Object> curList = new ArrayList<>();
        curList.add("");
        if(type.equals(GROUP)){
            curList.add("合计");
            curList.add(ra.getReceiveCount());
        }
        if(type.equals(GROUP_PERSON)){
            curList.add(ra.getUserName());
            curList.add("合计");
        }
        if(type.equals(GROUP_PERSON_DAY)){
            curList.add(ra.getDays());
            curList.add("合计");
        }
        curList.add(ra.getAssignCount());
        curList.add(ra.getTrackingCount());
        curList.add(ra.getTransCount());
        curList.add(ra.getEffectiveCount());
        curList.add(formatPercent(ra.getFollowRate()));
        curList.add(formatPercent(ra.getTransRate()));
        curList.add(formatPercent(ra.getEffectiveRate()));
        dataList.add(curList);
    }
    /**
     * 格式化
     */
    private String formatPercent(BigDecimal callPercent) {
        if(callPercent!=null) {
            callPercent = callPercent.multiply(new BigDecimal(100));
        }else {
            callPercent = BigDecimal.ZERO;
        }
        return callPercent+"%";
    }
    /**
     * 查询话务专员
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq,
                                                     HttpServletRequest request) {
        userOrgRoleReq.setRoleCode(RoleCodeEnum.HWY.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
    }
    /**
     * 查询字典表
     *
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }

}
