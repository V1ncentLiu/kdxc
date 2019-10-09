package com.kuaidao.manageweb.controller.statistics.callrecord;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.callrecord.TeleTalkTimeFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.callrecord.TeleSaleTalkTimeQueryDTO;
import com.kuaidao.stastics.dto.callrecord.TeleTalkTimeRespDTO;
import com.kuaidao.stastics.dto.callrecord.TotalDataDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电销顾问通话时长
 */
@RestController
@RequestMapping("/callrecord/teleSaleTalkTime")
public class TeleSaleTalkTimeController {
    
    private static Logger logger = LoggerFactory.getLogger(TeleSaleTalkTimeController.class);
    
    @Autowired
    TeleTalkTimeFeignClient teleTalkTimeFeignClient;
    
    @Autowired
    OrganizationFeignClient organizationFeignClient;
    
    /**
     * 昨日 七天 查询
      * 电销组通话总时长统计 分頁
     */
    @PostMapping("/listTeleGroupTalkTime")
    public JSONResult<Map<String,Object>> listTeleGroupTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
        Long startTime = teleSaleTalkTimeQueryDTO.getStartTime();
        Long endTime = teleSaleTalkTimeQueryDTO.getEndTime();
        if(startTime==null || endTime==null) {
            logger.error("listTeleSaleGroupTalkTime illegal param,startTime{{}},endTime{{}}",startTime,endTime);
            return CommonUtil.getParamIllegalJSONResult();
        }
        //设置 startDate 和 endDate
        setQueryStartDateAndEndDate(teleSaleTalkTimeQueryDTO);
        HashMap<String,Object> resMap = new HashMap<>();
        
        Long orgId = teleSaleTalkTimeQueryDTO.getOrgId();
        if(orgId==null) {
            //查询当前组织机构下电销组织
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            JSONResult<List<OrganizationRespDTO>> orgGroupJr = getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
            if (!JSONResult.SUCCESS.equals(orgGroupJr.getCode())) {
                logger.info("listTeleGroupTalkTime get tele group param{{}},res{{}}",curLoginUser.getOrgId(),orgGroupJr);
                return new JSONResult<Map<String,Object>>().fail(orgGroupJr.getCode(),orgGroupJr.getMsg());
            }
            List<OrganizationRespDTO> orgGroup = orgGroupJr.getData();
            if(CollectionUtils.isEmpty(orgGroup)) {
                resMap.put("totalData", null);
                resMap.put("tableData", null);
                return new JSONResult<Map<String,Object>>().success(resMap);
            }
            List<Long> orgIdList = orgGroup.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
            teleSaleTalkTimeQueryDTO.setOrgIdList(orgIdList);
        }
    
        
        JSONResult<PageBean<TeleTalkTimeRespDTO>> talkTimeList = teleTalkTimeFeignClient.listTeleGroupTalkTime(teleSaleTalkTimeQueryDTO);
        JSONResult<TeleTalkTimeRespDTO> totalTeleGroupTalkTime = teleTalkTimeFeignClient.totalTeleGroupTalkTime(teleSaleTalkTimeQueryDTO);
        if(!JSONResult.SUCCESS.equals(talkTimeList.getCode())) {
          return new JSONResult<Map<String,Object>>().fail(talkTimeList.getCode(), talkTimeList.getMsg());  
        }
        
        if(!JSONResult.SUCCESS.equals(totalTeleGroupTalkTime.getCode())) {
            return new JSONResult<Map<String,Object>>().fail(totalTeleGroupTalkTime.getCode(), totalTeleGroupTalkTime.getMsg());  
          }
        
        
        resMap.put("totalData",totalTeleGroupTalkTime.getData());
        resMap.put("tableData", talkTimeList.getData());
        return new JSONResult<Map<String,Object>>().success(resMap);
    }
    
    /**
     * 获取当前组织机构
    * @return
     */
    private List<Long> getOrgList(){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        JSONResult<List<OrganizationRespDTO>> orgGroupJr = getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        if (!JSONResult.SUCCESS.equals(orgGroupJr.getCode())) {
            logger.info("listTeleGroupTalkTime get tele group param{{}},res{{}}",curLoginUser.getOrgId(),orgGroupJr);
            return null;
        }
        List<OrganizationRespDTO> orgGroup = orgGroupJr.getData();
        if(CollectionUtils.isEmpty(orgGroup)) {
            return null;
        }
        List<Long> orgIdList = orgGroup.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
        return orgIdList;
    }
    
    /**
     * 昨日 七天
     * 电销组通话总时长统计 不分頁
    */
   @RequiresPermissions("statistics:teleSaleTalkTime:export")
   @RequestMapping("/exportTeleGroupTalkTime")
   public void exportTeleGroupTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO,HttpServletResponse response) throws Exception {
      setQueryStartDateAndEndDate(teleSaleTalkTimeQueryDTO);
       boolean isReqData = true;
       Long orgId = teleSaleTalkTimeQueryDTO.getOrgId();
       if(orgId==null) {
           List<Long> orgIdList = getOrgList();
           if(orgIdList==null) {
               isReqData= false;
           }
           teleSaleTalkTimeQueryDTO.setOrgIdList(orgIdList);
       }
       
       TeleTalkTimeRespDTO totalTalkTimeDTO = null;
       List<TeleTalkTimeRespDTO> teleGroupList = new ArrayList<>();
       JSONResult<TotalDataDTO<TeleTalkTimeRespDTO, TeleTalkTimeRespDTO>> teleGroupTalkTimeJr  = null;
       if(isReqData) {
           teleGroupTalkTimeJr = teleTalkTimeFeignClient.listTeleGroupTalkTimeNoPage(teleSaleTalkTimeQueryDTO);
           TotalDataDTO<TeleTalkTimeRespDTO, TeleTalkTimeRespDTO> resData = teleGroupTalkTimeJr.getData();
          //获取合计 
          totalTalkTimeDTO = resData.getTotalData();
          teleGroupList   = resData.getTableData();
       }
       
       List<List<Object>> dataList = new ArrayList<List<Object>>();
       dataList.add(getGroupHeadTitleList());
       //合计 放进excel 
       addTotalTalkTimeToList(totalTalkTimeDTO,dataList);
       
       for(int i = 0; i<teleGroupList.size(); i++){
           TeleTalkTimeRespDTO teleTalkTimeRespDTO = teleGroupList.get(i);
           List<Object> curList = new ArrayList<>();
           curList.add(i + 1);
           curList.add(teleTalkTimeRespDTO.getOrgName());
           curList.add(teleTalkTimeRespDTO.getCallCount());
           curList.add(CommUtil.nullIntegerToZero(teleTalkTimeRespDTO.getCalledCount()));
           curList.add(formatPercent(teleTalkTimeRespDTO.getCallPercent()));
           curList.add(teleTalkTimeRespDTO.getCallClueCount());
           curList.add(CommUtil.nullIntegerToZero(teleTalkTimeRespDTO.getCalledClueCount()));
           curList.add(formatPercent(teleTalkTimeRespDTO.getClueCallecdPrecent()));
           curList.add(formatSeconds(teleTalkTimeRespDTO.getValidCallTime()));
           curList.add(formatSeconds(teleTalkTimeRespDTO.getUserAvgDayValidCallTime()));
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
   
   private String formatSeconds(Integer seconds) {
       if(seconds==null) {
           return "00时00分00秒";
       }
       return DateUtil.second2TimeWithUnit(seconds);
   }
   
   /**
    * 把合计放进total 
   * @param totalTalkTimeDTO
   * @param dataList
    */
   private void addTotalTalkTimeToList(TeleTalkTimeRespDTO totalTalkTimeDTO,List<List<Object>> dataList) {
       if(totalTalkTimeDTO==null) {
           return;
       }
       List<Object> totalList = new ArrayList<>();
       totalList.add("");
       totalList.add("合计");
       totalList.add(totalTalkTimeDTO.getCallCount());
       totalList.add(totalTalkTimeDTO.getCalledCount());
       totalList.add(formatPercent(totalTalkTimeDTO.getCallPercent()));
       totalList.add(totalTalkTimeDTO.getCallClueCount());
       totalList.add(totalTalkTimeDTO.getCalledClueCount());
       totalList.add(formatPercent(totalTalkTimeDTO.getClueCallecdPrecent()));
       totalList.add(formatSeconds(totalTalkTimeDTO.getValidCallTime()));
       totalList.add(formatSeconds(totalTalkTimeDTO.getUserAvgDayValidCallTime()));
       dataList.add(totalList);
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
   * 合计 个人
   * 电销顾问通话总时长统计 
  */
 @RequestMapping("/listTeleSaleTalkTime")
 public JSONResult<PageBean<TeleTalkTimeRespDTO>> listTeleSaleTalkTime(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
     Long startTime = teleSaleTalkTimeQueryDTO.getStartTime();
     Long endTime = teleSaleTalkTimeQueryDTO.getEndTime();
     if(startTime==null || endTime==null) {
         logger.error("listTeleSaleTalkTimeBySaleId illegal param（合计、个人）,startTime{{}},endTime{{}}",startTime,endTime);
         return CommonUtil.getParamIllegalJSONResult();
     }
     //设置 startDate 和 endDate
     setQueryStartDateAndEndDate(teleSaleTalkTimeQueryDTO);
     Long orgId = teleSaleTalkTimeQueryDTO.getOrgId();
     if(orgId==null) {
         UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
         List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
         RoleInfoDTO roleInfoDTO = roleList.get(0);
         String roleCode = roleInfoDTO.getRoleCode();
         if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
             teleSaleTalkTimeQueryDTO.setOrgId(orgId);
         }else {
             Long curOrgId = curLoginUser.getOrgId();
             OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
             busGroupReqDTO.setParentId(curOrgId);
             busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
             busGroupReqDTO.setOrgType(OrgTypeConstant.DXZ);
             JSONResult<List<OrganizationRespDTO>> orgJr = getOrgGroupByOrgId(curOrgId,OrgTypeConstant.DXZ);
             if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
                 logger.error("listTeleSaleTalkTime queryOrgByParam,param{{}},res{{}}",busGroupReqDTO,orgJr);
                 return new JSONResult<PageBean<TeleTalkTimeRespDTO>>().fail(orgJr.getCode(),orgJr.getMsg());
             }
             List<OrganizationRespDTO> orgRespDTOList = orgJr.getData();
             if(CollectionUtils.isNotEmpty(orgRespDTOList)) {
                 List<Long> orgIdList = orgRespDTOList.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
                 teleSaleTalkTimeQueryDTO.setOrgIdList(orgIdList);
             }else {
                 logger.error("listTeleSaleTalkTime queryOrgByParam,param{{}},res{{}}",busGroupReqDTO,orgJr);
                 return new JSONResult<PageBean<TeleTalkTimeRespDTO>>().success(PageBean.getEmptyDataPageBean(teleSaleTalkTimeQueryDTO.getPageNum()
                         , teleSaleTalkTimeQueryDTO.getPageSize()));
                 
             }
             
         }
         
     }
     
     
     return teleTalkTimeFeignClient.listTeleSaleTalkTime(teleSaleTalkTimeQueryDTO);
 }


/**
    * 合计 和个人  页面  导出
    * 电销顾问通话总时长统计 不分頁
   */
 @RequiresPermissions("statistics:teleSaleTalkTime:export")
  @RequestMapping("/exportTeleSaleTalkTime")
  public void exportTeleSaleTalkTimeNoPage(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO,HttpServletResponse response) throws Exception{
     setQueryStartDateAndEndDate(teleSaleTalkTimeQueryDTO);
     Long orgId = teleSaleTalkTimeQueryDTO.getOrgId();
     //是否需要远程调用接口 获取数据
     boolean isReqData = true;
     if(orgId==null) {
         UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
         List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
         RoleInfoDTO roleInfoDTO = roleList.get(0);
         String roleCode = roleInfoDTO.getRoleCode();
         if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
             teleSaleTalkTimeQueryDTO.setOrgId(orgId);
         }else {
             Long curOrgId = curLoginUser.getOrgId();
             OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
             busGroupReqDTO.setParentId(curOrgId);
             busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
             busGroupReqDTO.setOrgType(OrgTypeConstant.DXZ);
             JSONResult<List<OrganizationRespDTO>> orgJr = getOrgGroupByOrgId(curOrgId,OrgTypeConstant.DXZ);
             if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
                 logger.error("exportTeleSaleTalkTimeNoPage queryOrgByParam,param{{}},res{{}}",busGroupReqDTO,orgJr);
                 isReqData = false;
             }else {
                 List<OrganizationRespDTO> orgRespDTOList = orgJr.getData();
                 if(CollectionUtils.isNotEmpty(orgRespDTOList)) {
                     List<Long> orgIdList = orgRespDTOList.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
                     teleSaleTalkTimeQueryDTO.setOrgIdList(orgIdList);
                 }else {
                     isReqData = false;
                     logger.error("exportTeleSaleTalkTimeNoPage queryOrgByParam,param{{}},res{{}}",busGroupReqDTO,orgJr);
                 } 
             }
         }
         
     }
     JSONResult<List<TeleTalkTimeRespDTO>> teleSaleTalkTimeJr = null;
     List<TeleTalkTimeRespDTO> teleSaleList  = new ArrayList<>();
     if(isReqData) {
         
         teleSaleTalkTimeJr = teleTalkTimeFeignClient.listTeleSaleTalkTimeNoPage(teleSaleTalkTimeQueryDTO);
         teleSaleList   = teleSaleTalkTimeJr.getData();
     }

       List<List<Object>> dataList = new ArrayList<List<Object>>();
       dataList.add(getTeleSaleHeadTitleList());
      
       for(int i = 0; i<teleSaleList.size(); i++){
           TeleTalkTimeRespDTO teleTalkTimeRespDTO = teleSaleList.get(i);
           List<Object> curList = new ArrayList<>();
           curList.add(i + 1);
           curList.add(teleTalkTimeRespDTO.getDateId());
           curList.add(teleTalkTimeRespDTO.getOrgName());
           curList.add(teleTalkTimeRespDTO.getUserName());
           curList.add(teleTalkTimeRespDTO.getCallCount());
           curList.add(CommUtil.nullIntegerToZero(teleTalkTimeRespDTO.getCalledCount()));
           curList.add(formatPercent(teleTalkTimeRespDTO.getCallPercent()));
           curList.add(teleTalkTimeRespDTO.getCallClueCount());
           curList.add(CommUtil.nullIntegerToZero(teleTalkTimeRespDTO.getCalledClueCount()));
           curList.add(formatPercent(teleTalkTimeRespDTO.getClueCallecdPrecent()));
           curList.add(formatSeconds(teleTalkTimeRespDTO.getValidCallTime()));
           curList.add(formatSeconds(teleTalkTimeRespDTO.getUserAvgDayValidCallTime()));
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
       Long startTime = teleSaleTalkTimeQueryDTO.getStartTime();
       Long endTime = teleSaleTalkTimeQueryDTO.getEndTime();
       if(orgId==null) {
           logger.error("listTeleSaleTalkTimeByOrgId illegal param,startTime{{}},endTime{{}},orgId{{}}",startTime,endTime,orgId);
           return CommonUtil.getParamIllegalJSONResult();
       }
       //设置 startDate 和 endDate
       setQueryStartDateAndEndDate(teleSaleTalkTimeQueryDTO);
       List<Long> orgIdList = new ArrayList<>();
       orgIdList.add(orgId);
       teleSaleTalkTimeQueryDTO.setOrgIdList(orgIdList);
       return teleTalkTimeFeignClient.listGroupTeleSaleTalkTime(teleSaleTalkTimeQueryDTO);
   }
   
   
   /**
    * 点击电销组 导出
    * 电销通话总时长统计 不分頁
   */
   @RequiresPermissions("statistics:teleSaleTalkTime:export")
   @RequestMapping("/exportGroupTeleSaleTalkTimeNoPage")
    public void exportGroupTeleSaleTalkTimeNoPage(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO
            ,HttpServletResponse response) throws Exception{
       setQueryStartDateAndEndDate(teleSaleTalkTimeQueryDTO);
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
            curList.add(CommUtil.nullIntegerToZero(teleTalkTimeRespDTO.getCalledCount()));
            curList.add(formatPercent(teleTalkTimeRespDTO.getCallPercent()));
            curList.add(teleTalkTimeRespDTO.getCallClueCount());
            curList.add(CommUtil.nullIntegerToZero(teleTalkTimeRespDTO.getCalledClueCount()));
            curList.add(formatPercent(teleTalkTimeRespDTO.getClueCallecdPrecent()));
            curList.add(formatSeconds(teleTalkTimeRespDTO.getValidCallTime()));
            curList.add(formatSeconds(teleTalkTimeRespDTO.getUserAvgDayValidCallTime()));
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
   
    
    /**
     * 查询组织结构下 组织
    * @param orgId
    * @param orgType
    * @return
     */
    private JSONResult<List<OrganizationRespDTO>> getOrgGroupByOrgId(Long orgId,Integer orgType) {
        // 电销组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        if (!JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            return null;
        }
        List<OrganizationRespDTO> list = new ArrayList<>();
        if(listJSONResult != null && listJSONResult.getData().size() > 0){
            List<OrganizationDTO> data = listJSONResult.getData();
            for(OrganizationDTO organizationDTO : data){
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(organizationDTO.getId());
                organizationRespDTO.setName(organizationDTO.getName());
                list.add(organizationRespDTO);
            }
        }
        return new JSONResult<List<OrganizationRespDTO>>().success(list);
    }
    
    /**
     * 根据 开始时间和结束时间设置  开始日期和结束日期，优化hive 查询速度
    * @param teleSaleTalkTimeQueryDTO
     */
    private void setQueryStartDateAndEndDate(@RequestBody TeleSaleTalkTimeQueryDTO teleSaleTalkTimeQueryDTO) {
        Long startTime = teleSaleTalkTimeQueryDTO.getStartTime();
        Long  startDate = Long.valueOf(String.valueOf(startTime).substring(0,8));
        Long endTime = teleSaleTalkTimeQueryDTO.getEndTime();
        Long endDate = Long.valueOf(String.valueOf(endTime).substring(0,8));
        teleSaleTalkTimeQueryDTO.setStartDate(startDate);
        teleSaleTalkTimeQueryDTO.setEndDate(endDate);
    }

}
