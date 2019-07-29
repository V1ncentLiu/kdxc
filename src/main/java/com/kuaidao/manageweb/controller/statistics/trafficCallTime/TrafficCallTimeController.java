package com.kuaidao.manageweb.controller.statistics.trafficCallTime;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.trafficCallTime.TrafficCallTimeFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.resourceEfficiency.ResourceEfficiencyAllDataDto;
import com.kuaidao.stastics.dto.trafficCallTime.TrafficCallTimeDto;
import com.kuaidao.stastics.dto.trafficCallTime.TrafficCallTimeQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
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
import java.util.stream.Collectors;

/**
 * 话务通话时长
 */
@Controller
@RequestMapping("/statistics/trafficCallTime")
public class TrafficCallTimeController {

    @Autowired
    private TrafficCallTimeFeignClient trafficCallTimeFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    private static final Integer GROUP = 1;
    private static final Integer GROUP_PERSON = 2;
    private static final Integer GROUP_PERSON_DAY = 3;

    /**
     * 组一级页面跳转
     */
    @RequestMapping("/telephoneCallTable")
    public String telephoneCallTable(Long orgId,Long startTime,Long endTime,Long userId,Integer newResource,HttpServletRequest request) {
        //返回页面携带参数
        pageParams(orgId,startTime,endTime,userId,newResource,request);
        //加载话务组
        initOrgList(request);
        return "reportformsTelephone/telephoneCallTable";
    }

    /**
     * 组+人 二级页面跳转
     */
    @RequestMapping("/telephoneCallTablePerson")
    public String telephoneCallTablePerson(Long orgId,Long startTime,Long endTime,Long userId,Integer newResource,HttpServletRequest request) {
        pageParams(orgId,startTime,endTime,userId,newResource,request);
        //加载话务组
        initOrgList(request);
        return "reportformsTelephone/telephoneCallTableTeam";
    }

    /**
     * 组+人+天 三级页面跳转
     */
    @RequestMapping("/telephoneCallTableTeam")
    public String telephoneCallTableTeam(Long orgId,Long startTime,Long endTime,Long userId,Integer newResource,HttpServletRequest request) {
        pageParams(orgId,startTime,endTime,userId,newResource,request);
        //加载话务组
        initOrgList(request);
        return "reportformsTelephone/telephoneCallTablePerson";
    }
    /**
     * 组分页
     */
    @PostMapping("/getGroupPageList")
    public JSONResult<Map<String,Object>> getGroupPageList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto){
        initAuth(trafficCallTimeQueryDto);
        return trafficCallTimeFeignClient.getGroupPageList(trafficCallTimeQueryDto);
    }

    /**
     * 组不分页
     */
    @PostMapping("/exportGroupList")
    public void exportGroupList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto,HttpServletResponse response) throws IOException {
        initAuth(trafficCallTimeQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTrafficCallHeadTitleList(GROUP));
        JSONResult<Map<String, Object>> result = trafficCallTimeFeignClient.getGroupList(trafficCallTimeQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<TrafficCallTimeDto> orderList = JSON.parseArray(listTxt, TrafficCallTimeDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        TrafficCallTimeDto sumReadd = JSON.parseObject(totalDataStr, TrafficCallTimeDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP);
        for(int i = 0; i<orderList.size(); i++){
            TrafficCallTimeDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(ra.getCallCount());
            curList.add(ra.getCalledCount());
            curList.add(formatPercent(ra.getCallPercent()));
            curList.add(ra.getCallClueCount());
            curList.add(ra.getCalledClueCount());
            curList.add(formatPercent(ra.getClueCallecdPrecent()));
            curList.add(formatSeconds(ra.getValidCallTime()));
            curList.add(formatSeconds(ra.getUserAvgDayValidCallTime()));
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = trafficCallTimeQueryDto.getStartTime();
        Long endTime = trafficCallTimeQueryDto.getEndTime();
        String name = "话务接通时长表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }


    /**
     * 组+人 分页
     */
    @PostMapping("/getGroupPersonPageList")
    public JSONResult<Map<String,Object>> getGroupPersonPageList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto){
        initAuth(trafficCallTimeQueryDto);
        return trafficCallTimeFeignClient.getGroupPersonPageList(trafficCallTimeQueryDto);
    }

    /**
     * 组+人 不分页
     */
    @PostMapping("/exportGroupPersonList")
    public void exportGroupPersonList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto,HttpServletResponse response) throws IOException {
        initAuth(trafficCallTimeQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTrafficCallHeadTitleList(GROUP_PERSON));
        JSONResult<Map<String, Object>> result = trafficCallTimeFeignClient.getGroupPersonList(trafficCallTimeQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<TrafficCallTimeDto> orderList = JSON.parseArray(listTxt, TrafficCallTimeDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        TrafficCallTimeDto sumReadd = JSON.parseObject(totalDataStr, TrafficCallTimeDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP_PERSON);
        buildList(dataList, orderList,GROUP_PERSON);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = trafficCallTimeQueryDto.getStartTime();
        Long endTime = trafficCallTimeQueryDto.getEndTime();
        String name = "话务接通时长表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
        trafficCallTimeFeignClient.getGroupPersonList(trafficCallTimeQueryDto);
    }

    /**
     * 组+人+天 分页
     */
    @PostMapping("/getGroupPersonDayPageList")
    public JSONResult<Map<String,Object>> getGroupPersonDayPageList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto){
        initAuth(trafficCallTimeQueryDto);
        return trafficCallTimeFeignClient.getGroupPersonDayPageList(trafficCallTimeQueryDto);
    }

    /**
     * 组+人+天 不分页
     */
    @PostMapping("/exportGroupPersonDayList")
    public void getGroupPersonDayList(@RequestBody TrafficCallTimeQueryDto trafficCallTimeQueryDto,HttpServletResponse response) throws IOException {
        initAuth(trafficCallTimeQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTrafficCallHeadTitleList(GROUP_PERSON_DAY));
        JSONResult<Map<String, Object>> result = trafficCallTimeFeignClient.getGroupPersonDayList(trafficCallTimeQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<TrafficCallTimeDto> orderList = JSON.parseArray(listTxt, TrafficCallTimeDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        TrafficCallTimeDto sumReadd = JSON.parseObject(totalDataStr, TrafficCallTimeDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP_PERSON_DAY);
        buildList(dataList, orderList,GROUP_PERSON_DAY);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = trafficCallTimeQueryDto.getStartTime();
        Long endTime = trafficCallTimeQueryDto.getEndTime();
        String name = "话务接通时长表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    private void buildList(List<List<Object>> dataList, List<TrafficCallTimeDto> orderList,Integer type) {
        for(int i = 0; i<orderList.size(); i++){
            TrafficCallTimeDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            if(type.equals(GROUP_PERSON_DAY)){
                curList.add(ra.getDateId());
            }
            curList.add(ra.getUserName());
            curList.add(ra.getOrgName());
            curList.add(ra.getCallCount());
            curList.add(ra.getCalledCount());
            curList.add(formatPercent(ra.getCallPercent()));
            curList.add(ra.getCallClueCount());
            curList.add(ra.getCalledClueCount());
            curList.add(formatPercent(ra.getClueCallecdPrecent()));
            curList.add(formatSeconds(ra.getValidCallTime()));
            curList.add(formatSeconds(ra.getUserAvgDayValidCallTime()));
            dataList.add(curList);
        }
    }

    private List<OrganizationDTO> getOrgGroupByOrgId(Long orgId, Integer orgType) {
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        return listJSONResult.getData();
    }
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId+"");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            return null;
        }
        return orgJr.getData();
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
     *  返回页面携带参数
     */
    private void pageParams(Long orgId,Long startTime,Long endTime,Long userId,Integer newResource,HttpServletRequest request){
        TrafficCallTimeQueryDto trafficCallTimeQueryDto = new TrafficCallTimeQueryDto();
        trafficCallTimeQueryDto.setOrgId(orgId);
        trafficCallTimeQueryDto.setStartTime(startTime);
        trafficCallTimeQueryDto.setEndTime(endTime);
        trafficCallTimeQueryDto.setUserId(userId);
        trafficCallTimeQueryDto.setNewResource(newResource);
        request.setAttribute("trafficCallTimeQueryDto",trafficCallTimeQueryDto);
    }

    /**
     * 初始化话务组
     */
    private void initOrgList(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String  curOrgId = "";
        List<OrganizationDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.HWZG.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationDTO organizationRespDTO = new OrganizationDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.HWZ);
        }
        request.setAttribute("teleGroupList",teleGroupList);
        request.setAttribute("curOrgId",curOrgId);
    }

    /**
     * 查询条件限制
     */
    private void initAuth(TrafficCallTimeQueryDto trafficCallTimeQueryDto){
        Long orgId = trafficCallTimeQueryDto.getOrgId();
        if(null == orgId){
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            List<OrganizationDTO> orgGroupByOrgId = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.HWZ);
            List<Long> orgIdList = orgGroupByOrgId.parallelStream().map(OrganizationDTO::getId).collect(Collectors.toList());
            trafficCallTimeQueryDto.setOrgIdList(orgIdList);
        }
    }

    private List<Object> getTrafficCallHeadTitleList(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        if(type.equals(GROUP_PERSON_DAY)){
            headTitleList.add("统计日期");
        }
        if(!type.equals(GROUP)){
            headTitleList.add("话务专员");
        }
        headTitleList.add("话务组");
        headTitleList.add("拨打次数");
        headTitleList.add("接通次数");
        headTitleList.add("接通率");
        headTitleList.add("拨打量");
        headTitleList.add("接通量");
        headTitleList.add("资源接通率");
        headTitleList.add("总有效通话时长");
        if(type.equals(GROUP_PERSON_DAY)){
            headTitleList.add("人均总有效通话时长");
        }else{
            headTitleList.add("天均总有效通话时长");
        }
        return headTitleList;
    }
    private void addTotalExportData(TrafficCallTimeDto ra, List<List<Object>> dataList,Integer type) {
        List<Object> curList = new ArrayList<>();
        curList.add("");
        if(type.equals(GROUP_PERSON_DAY)){
            curList.add(ra.getDateId());
        }
        if(!type.equals(GROUP)){
            curList.add(ra.getUserName());
        }
        curList.add(ra.getOrgName());
        curList.add(ra.getCallCount());
        curList.add(ra.getCalledCount());
        curList.add(formatPercent(ra.getCallPercent()));
        curList.add(ra.getCallClueCount());
        curList.add(ra.getCalledClueCount());
        curList.add(formatPercent(ra.getClueCallecdPrecent()));
        curList.add(formatSeconds(ra.getValidCallTime()));
        curList.add(formatSeconds(ra.getUserAvgDayValidCallTime()));
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
    private String formatSeconds(Integer seconds) {
        if(seconds==null) {
            return "00时00分00秒";
        }
        return DateUtil.second2TimeWithUnit(seconds);
    }

}
