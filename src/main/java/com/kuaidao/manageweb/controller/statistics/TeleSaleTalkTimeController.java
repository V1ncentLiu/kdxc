package com.kuaidao.manageweb.controller.statistics;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.statistics.TeleTalkTimeFeignClient;
import com.kuaidao.stastics.dto.callrecord.TeleSaleTalkTimeQueryDTO;
import com.kuaidao.stastics.dto.callrecord.TeleTalkTimeRespDTO;
import com.kuaidao.stastics.dto.callrecord.TotalDataDTO;

/**
 * 通话时长  
 * @author  Devin.Chen
 * @date 2019-05-18 16:11:24
 * @version V1.0
 */
@RestController
@RequestMapping("/callrecord/teleSaleTalkTime")
public class TeleSaleTalkTimeController {
    
    @Autowired
    TeleTalkTimeFeignClient teleTalkTimeFeignClient;
    
    /**
     * 昨日 七天 查询
      * 电销组通话总时长统计 分頁
     */
    @PostMapping("/listTeleGroupTalkTime")
    public JSONResult<Map<String,Object>> listTeleGroupTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
        JSONResult<PageBean<TeleTalkTimeRespDTO>> talkTimeList = teleTalkTimeFeignClient.listTeleGroupTalkTime(teleSaleTalkTimeQueryDTO);
        JSONResult<TeleTalkTimeRespDTO> totalTeleGroupTalkTime = teleTalkTimeFeignClient.totalTeleGroupTalkTime(teleSaleTalkTimeQueryDTO);
        HashMap<String,Object> resMap = new HashMap<>();
        resMap.put("totalData",totalTeleGroupTalkTime.getData());
        resMap.put("tableData", talkTimeList.getData());
        return new JSONResult<Map<String,Object>>().success(resMap);
    }
    
    
    /**
     * 昨日 七天
     * 电销组通话总时长统计 不分頁
    */
   @RequestMapping("/exportTeleGroupTalkTime")
   public void exportTeleGroupTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO,HttpServletResponse response) throws Exception {
       JSONResult<TotalDataDTO<TeleTalkTimeRespDTO, TeleTalkTimeRespDTO>> teleGroupTalkTimeJr = teleTalkTimeFeignClient.listTeleGroupTalkTimeNoPage(teleSaleTalkTimeQueryDTO);
        TotalDataDTO<TeleTalkTimeRespDTO, TeleTalkTimeRespDTO> resData = teleGroupTalkTimeJr.getData();
       //获取合计 
       TeleTalkTimeRespDTO totalTalkTimeDTO = resData.getTotalData();
       
       List<List<Object>> dataList = new ArrayList<List<Object>>();
       dataList.add(getGroupHeadTitleList());
       //合计 放进excel 
       addTotalTalkTimeToList(totalTalkTimeDTO,dataList);
       List<TeleTalkTimeRespDTO> teleGroupList   = resData.getTableData();
       for(int i = 0; i<teleGroupList.size(); i++){
           TeleTalkTimeRespDTO teleTalkTimeRespDTO = teleGroupList.get(i);
           List<Object> curList = new ArrayList<>();
           curList.add(i + 1);
           curList.add(teleTalkTimeRespDTO.getOrgName());
           curList.add("");
           curList.add(teleTalkTimeRespDTO.getCallCount());
           curList.add(teleTalkTimeRespDTO.getCalledClueCount());
           curList.add(teleTalkTimeRespDTO.getCallPercent());
           curList.add(teleTalkTimeRespDTO.getCallClueCount());
           curList.add(teleTalkTimeRespDTO.getCalledClueCount());
           curList.add(teleTalkTimeRespDTO.getClueCallecdPrecent());
           curList.add(teleTalkTimeRespDTO.getValidCallTime());
           curList.add(teleTalkTimeRespDTO.getUserAvgDayValidCallTime());
           dataList.add(curList);
       }
       teleGroupTalkTimeJr  = null;
       XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
       Long startTime = teleSaleTalkTimeQueryDTO.getStartTime();
       Long endTime = teleSaleTalkTimeQueryDTO.getEndTime();
       String name = "电销组通话时长表" +startTime+"-"+endTime + ".xlsx";
       response.addHeader("Content-Disposition",
               "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
       response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
       response.setContentType("application/octet-stream");
       ServletOutputStream outputStream = response.getOutputStream();
       wbWorkbook.write(outputStream);
       outputStream.close();
        
   }
   
   /**
    * 把合计放进total 
   * @param totalTalkTimeDTO
   * @param dataList
    */
   private void addTotalTalkTimeToList(TeleTalkTimeRespDTO totalTalkTimeDTO,List<List<Object>> dataList) {
       List<Object> totalList = new ArrayList<>();
       totalList.add("合计");
       totalList.add("");
       totalList.add("");
       totalList.add(totalTalkTimeDTO.getCallCount());
       totalList.add(totalTalkTimeDTO.getCalledCount());
       totalList.add(formatPercent(totalTalkTimeDTO.getCallPercent()));
       totalList.add(totalTalkTimeDTO.getCallClueCount());
       totalList.add(totalTalkTimeDTO.getCalledClueCount());
       totalList.add(formatPercent(totalTalkTimeDTO.getClueCallecdPrecent()));
       totalList.add(totalTalkTimeDTO.getValidCallTime());
       totalList.add(totalTalkTimeDTO.getUserAvgDayValidCallTime());
    }

   /**
    * 格式化 
   * @param callPercent
   * @return
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
    * 电销组通话时长 导出 头部
   * @return
    */
   private List<Object> getGroupHeadTitleList() {
       List<Object> headTitleList = new ArrayList<>();
       headTitleList.add("序号");
       headTitleList.add("电销组");
       headTitleList.add("电销顾问");
       headTitleList.add("通话次数");
       headTitleList.add("通话接通次数");
       headTitleList.add("通话接通率");
       headTitleList.add("通话量");
       headTitleList.add("接通量");
       headTitleList.add("资源接通率");
       headTitleList.add("总有效通话时长");
       headTitleList.add("人均天有效通话时长");
       return headTitleList;
    }

   

/**
   * 昨日 七天
   * 电销顾问通话总时长统计 
  */
 @RequestMapping("/listTeleSaleTalkTime")
 public JSONResult<PageBean<TeleTalkTimeRespDTO>> listTeleSaleTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
     return teleTalkTimeFeignClient.listTeleSaleTalkTime(teleSaleTalkTimeQueryDTO);
 }


/**
    * 昨日 七天 导出
    * 电销顾问通话总时长统计 不分頁
   */
  @RequestMapping("/exportTeleSaleTalkTime")
  public void exportTeleSaleTalkTimeNoPage(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO,HttpServletResponse response) throws Exception{
      JSONResult<List<TeleTalkTimeRespDTO>> teleSaleTalkTimeJr = teleTalkTimeFeignClient.listTeleSaleTalkTimeNoPage(teleSaleTalkTimeQueryDTO);
       List<List<Object>> dataList = new ArrayList<List<Object>>();
       dataList.add(getTeleSaleHeadTitleList());
       List<TeleTalkTimeRespDTO> teleSaleList   = teleSaleTalkTimeJr.getData();
       for(int i = 0; i<teleSaleList.size(); i++){
           TeleTalkTimeRespDTO teleTalkTimeRespDTO = teleSaleList.get(i);
           List<Object> curList = new ArrayList<>();
           curList.add(i + 1);
           curList.add(teleTalkTimeRespDTO.getDateId());
           curList.add(teleTalkTimeRespDTO.getOrgName());
           curList.add(teleTalkTimeRespDTO.getUserName());
           curList.add(teleTalkTimeRespDTO.getCallCount());
           curList.add(teleTalkTimeRespDTO.getCalledClueCount());
           curList.add(formatPercent(teleTalkTimeRespDTO.getCallPercent()));
           curList.add(teleTalkTimeRespDTO.getCallClueCount());
           curList.add(teleTalkTimeRespDTO.getCalledClueCount());
           curList.add(formatPercent(teleTalkTimeRespDTO.getClueCallecdPrecent()));
           curList.add(teleTalkTimeRespDTO.getValidCallTime());
           curList.add(teleTalkTimeRespDTO.getUserAvgDayValidCallTime());
           dataList.add(curList);
       }
       teleSaleTalkTimeJr  = null;
       XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
       String name = "电销顾问通话时长表" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
       response.addHeader("Content-Disposition",
               "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
       response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
       response.setContentType("application/octet-stream");
       ServletOutputStream outputStream = response.getOutputStream();
       wbWorkbook.write(outputStream);
       outputStream.close();
       
  }
  
  
  /**
   * 电销顾问通话时长 导出表头
  * @return
   */
  private List<Object> getTeleSaleHeadTitleList() {
      List<Object> headTitleList = new ArrayList<>();
      headTitleList.add("序号");
      headTitleList.add("日期");
      headTitleList.add("电销组");
      headTitleList.add("电销顾问");
      headTitleList.add("通话次数");
      headTitleList.add("通话接通次数");
      headTitleList.add("通话接通率");
      headTitleList.add("通话量");
      headTitleList.add("接通量");
      headTitleList.add("资源接通率");
      headTitleList.add("总有效通话时长");
      headTitleList.add("人均天有效通话时长");
      return headTitleList;

  }
  
  
  /**
   * 点击电销组 查询该组下用户信息
   * 电销通话总时长统计 分頁
  */
   @PostMapping("/listGroupTeleSaleTalkTime")
   public JSONResult<PageBean<TeleTalkTimeRespDTO>> listGroupTeleSaleTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
       Long orgId = teleSaleTalkTimeQueryDTO.getOrgId();
       if(orgId==null) {
           return CommonUtil.getParamIllegalJSONResult();
       }
       List<Long> orgIdList = new ArrayList<>();
       orgIdList.add(orgId);
       teleSaleTalkTimeQueryDTO.setOrgIdList(orgIdList);
       return teleTalkTimeFeignClient.listGroupTeleSaleTalkTime(teleSaleTalkTimeQueryDTO);
   }
   
   
   /**
    * 点击电销组 导出
    * 电销通话总时长统计 不分頁
   */
    
   @RequestMapping("/exportGroupTeleSaleTalkTimeNoPage")
    public void exportGroupTeleSaleTalkTimeNoPage(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO
            ,HttpServletResponse response) throws Exception{
        Long orgId = teleSaleTalkTimeQueryDTO.getOrgId();
        List<Long> orgIdList = new ArrayList<>();
        orgIdList.add(orgId);
        teleSaleTalkTimeQueryDTO.setOrgIdList(orgIdList);
        JSONResult<List<TeleTalkTimeRespDTO>> teleSaleListJr = teleTalkTimeFeignClient.listGroupTeleSaleTalkTimeNoPage(teleSaleTalkTimeQueryDTO);
       
        List<TeleTalkTimeRespDTO> teleSaleList = teleSaleListJr.getData();
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getGroupTeleSaleHeadTitleList());
        for(int i = 0; i<teleSaleList.size(); i++){
            TeleTalkTimeRespDTO teleTalkTimeRespDTO = teleSaleList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(teleTalkTimeRespDTO.getUserName());
            curList.add(teleTalkTimeRespDTO.getCallCount());
            curList.add(teleTalkTimeRespDTO.getCalledClueCount());
            curList.add(formatPercent(teleTalkTimeRespDTO.getCallPercent()));
            curList.add(teleTalkTimeRespDTO.getCallClueCount());
            curList.add(teleTalkTimeRespDTO.getCalledClueCount());
            curList.add(formatPercent(teleTalkTimeRespDTO.getClueCallecdPrecent()));
            curList.add(teleTalkTimeRespDTO.getValidCallTime());
            curList.add(teleTalkTimeRespDTO.getUserAvgDayValidCallTime());
            dataList.add(curList);
        }
        teleSaleListJr  = null;
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "电销顾问通话时长表" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
        
    }


    private List<Object> getGroupTeleSaleHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销顾问");
        headTitleList.add("通话次数");
        headTitleList.add("通话接通次数");
        headTitleList.add("通话接通率");
        headTitleList.add("通话量");
        headTitleList.add("接通量");
        headTitleList.add("资源接通率");
        headTitleList.add("总有效通话时长");
        headTitleList.add("人均天有效通话时长");
        return headTitleList;
    }
   

}
