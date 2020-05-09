package com.kuaidao.manageweb.controller.statistics.appointmentVisit;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.appointmentVisit.AppointmentVisitFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.appointmentVisit.AppointmentVisitDto;
import com.kuaidao.stastics.dto.appointmentVisit.AppointmentVisitQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/statistics/appointmentVisit")
public class AppointmentVisitController {

    private static Logger logger = LoggerFactory.getLogger(AppointmentVisitController.class);
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private AppointmentVisitFeignClient appointmentVisitFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;


    private static final Integer GROUP = 1;
    private static final Integer GROUP_DAY = 2;



    /**
     * 组一级页面跳转
     */
    @RequestMapping("/businessVisitTable")
    public String businessVisitTable(Long userId,Long orgId,Long startTime,Long endTime,Long projectId,HttpServletRequest request) {
        pageParams(userId,orgId,startTime,endTime,projectId,request);
        initOrgList(request);
        return "reportformsBusiness/businessVisitTable";
    }

    /**
     * 天二级页面跳转
     */
    @RequestMapping("/businessVisitTableTeam")
    public String businessVisitTableTeam(Long userId,Long orgId,Long startTime,Long endTime,Long projectId,HttpServletRequest request) {
        pageParams(userId,orgId,startTime,endTime,projectId,request);
        initOrgList(request);
        return "reportformsBusiness/businessVisitTableTeam";
    }


    /**
     * 组分页
     */
    @PostMapping("/getGroupPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getGroupPageList(@RequestBody AppointmentVisitQueryDto appointmentVisitQueryDto){
        initAuth(appointmentVisitQueryDto);
        return appointmentVisitFeignClient.getGroupPageList(appointmentVisitQueryDto);
    }

    /**
     * 组不分页 导出
     */
    @PostMapping("/exportGroupList")
    public void exportGroupList(@RequestBody AppointmentVisitQueryDto appointmentVisitQueryDto,HttpServletResponse response) throws IOException {
        initAuth(appointmentVisitQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getAppointmentVisitTitleList(GROUP));
        JSONResult<Map<String, Object>> result =   appointmentVisitFeignClient.getGroupList(appointmentVisitQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<AppointmentVisitDto> orderList = JSON.parseArray(listTxt, AppointmentVisitDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        AppointmentVisitDto sumReadd = JSON.parseObject(totalDataStr, AppointmentVisitDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP);
        buildList(dataList, orderList,GROUP);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "预约情况表" +appointmentVisitQueryDto.getStartTime()+"-"+appointmentVisitQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 组+天分页
     */
    @PostMapping("/getGroupDayPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getGroupDayPageList(@RequestBody AppointmentVisitQueryDto appointmentVisitQueryDto){
        initAuth(appointmentVisitQueryDto);
        return appointmentVisitFeignClient.getGroupDayPageList(appointmentVisitQueryDto);
    }

    /**
     * 组+天不分页 导出
     */
    @PostMapping("/exportGroupDayList")
    public void exportGroupDayList(@RequestBody AppointmentVisitQueryDto appointmentVisitQueryDto,HttpServletResponse response) throws IOException {
        initAuth(appointmentVisitQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getAppointmentVisitTitleList(GROUP_DAY));
        JSONResult<Map<String, Object>> result = appointmentVisitFeignClient.getGroupDayList(appointmentVisitQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<AppointmentVisitDto> orderList = JSON.parseArray(listTxt, AppointmentVisitDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        AppointmentVisitDto sumReadd = JSON.parseObject(totalDataStr, AppointmentVisitDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP_DAY);
        buildList(dataList, orderList,GROUP_DAY);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "预约情况表" +appointmentVisitQueryDto.getStartTime()+"-"+appointmentVisitQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }


    private List<OrganizationRespDTO> getOrgGroupByOrgId() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        // 商务组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setParentId(curLoginUser.getOrgId());
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> listJSONResult = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        return listJSONResult.getData();
    }

    /**
     *  返回页面携带参数
     */
    private void pageParams(Long userId,Long orgId,Long startTime,Long endTime,Long projectId,HttpServletRequest request){
        AppointmentVisitQueryDto appointmentVisitQueryDto = new AppointmentVisitQueryDto();
        appointmentVisitQueryDto.setOrgId(orgId);
        appointmentVisitQueryDto.setStartTime(startTime);
        appointmentVisitQueryDto.setEndTime(endTime);
        appointmentVisitQueryDto.setUserId(userId);
        appointmentVisitQueryDto.setProjectId(projectId);
        request.setAttribute("appointmentVisitQueryDto",appointmentVisitQueryDto);
    }
    private void initOrgList(HttpServletRequest request){
        //查询全部电销组
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("teleGroupList",queryOrgByParam.getData());

        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
    }

    /**
     * 查询话务专员
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq,
                                                     HttpServletRequest request) {
        userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
    }

    private List<Object> getAppointmentVisitTitleList(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        if(GROUP_DAY.equals(type)){
            headTitleList.add("预约日期");
        }
        headTitleList.add("来访项目");
        headTitleList.add("正常邀约数");
        headTitleList.add("取消邀约数");
        headTitleList.add("删除邀约数");
        headTitleList.add("正常邀约次数");
        headTitleList.add("取消邀约次数");
        headTitleList.add("删除邀约次数");
        return headTitleList;
    }

    private void buildList(List<List<Object>> dataList, List<AppointmentVisitDto> orderList, Integer type) {
        for(int i = 0; i<orderList.size(); i++){
            AppointmentVisitDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            if(GROUP_DAY.equals(type)){
                StringBuilder sb = new StringBuilder(ra.getReserveTime());
                sb.insert(6,"-");
                sb.insert(4,"-");
                curList.add(sb);
            }
            curList.add(ra.getProjectName());
            curList.add(ra.getInvitationNum());
            curList.add(ra.getCancelInvitationNum());
            curList.add(ra.getDeleteInvitationNum());
            curList.add(ra.getInvitationFrequency());
            curList.add(ra.getCancelInvitationFrequency());
            curList.add(ra.getDeleteInvitationFrequency());
            dataList.add(curList);
        }
    }

    private void addTotalExportData(AppointmentVisitDto ra, List<List<Object>> dataList, Integer type) {
        List<Object> curList = new ArrayList<>();
        curList.add("");
        if(type.equals(GROUP_DAY)){
            curList.add("");
        }
        curList.add(ra.getProjectName());
        curList.add(ra.getInvitationNum());
        curList.add(ra.getCancelInvitationNum());
        curList.add(ra.getDeleteInvitationNum());
        curList.add(ra.getInvitationFrequency());
        curList.add(ra.getCancelInvitationFrequency());
        curList.add(ra.getDeleteInvitationFrequency());
        dataList.add(curList);
    }

    private void initAuth(AppointmentVisitQueryDto appointmentVisitQueryDto){
        List<Long> orgIdList = new ArrayList<>();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        orgIdList.add(curLoginUser.getOrgId());
        appointmentVisitQueryDto.setBusCompayList(orgIdList);
    }

}
