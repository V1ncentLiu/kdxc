package com.kuaidao.manageweb.controller.statistics.unpaidFunds;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.unpaidFunds.UnpaidFundsFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.unpaidFunds.UnpaidFundsDto;
import com.kuaidao.stastics.dto.unpaidFunds.UnpaidFundsQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 累计未收齐款项统计
 */
@Controller
@RequestMapping("/statistics/unpaidFunds")
public class UnpaidFundsController {

    @Autowired
    private UnpaidFundsFeignClient unpaidFundsFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    private static final Integer DIRECTOR_ONE = 1;
    private static final Integer DIRECTOR_TWO = 2;
    private static final Integer MANAGER_ONE = 3;



    /**
     * 总监一级级页面跳转
     */
    @RequestMapping("/businessTotalArrearsTable")
    public String businessVisitReceptionTable(Long userId,Long orgId,Long endTime,Long projectId,String days,HttpServletRequest request) {
        pageParams(userId,orgId,endTime,projectId,days,request);
        initAuth(null,request);
        return "reportformsBusiness/businessTotalArrearsTable";
    }
    /**
     * 总监二级页面跳转
     */
    @RequestMapping("/businessTotalArrearsTableTeam")
    public String businessVisitReceptionTablePerson(Long userId,Long orgId,Long endTime,Long projectId,String days,HttpServletRequest request) {
        pageParams(userId,orgId,endTime,projectId,days,request);
        initAuth(null,request);
        return "reportformsBusiness/businessTotalArrearsTableTeam";
    }
    /**
     * 商务经理跳转
     */
    @RequestMapping("/businessTotalArrearsTableTeamPerson")
    public String businessTotalArrearsTableTeamPerson(Long userId,Long orgId,Long endTime,Long projectId,String days,HttpServletRequest request) {
        pageParams(userId,orgId,endTime,projectId,days,request);
        initAuth(null,request);
        return "reportformsBusiness/businessTotalArrearsTableTeamPerson";
    }

    /**
     * 商务总监一级页面查询 分页
     */
    @PostMapping("/getDirectorOnePageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getDirectorOnePageList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto){
        initAuth(unpaidFundsQueryDto,null);
        return unpaidFundsFeignClient.getDirectorOnePageList(unpaidFundsQueryDto);
    }
    /**
     * 商务总监一级页面查询
     */
    @PostMapping("/exportDirectorOneList")
    public void exportDirectorOneList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto,HttpServletResponse response) throws IOException {
        initAuth(unpaidFundsQueryDto,null);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getUnpaidFundsTitleList(DIRECTOR_ONE));
        JSONResult<Map<String, Object>> result =  unpaidFundsFeignClient.getDirectorOneList(unpaidFundsQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<UnpaidFundsDto> orderList = JSON.parseArray(listTxt, UnpaidFundsDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        UnpaidFundsDto sumReadd = JSON.parseObject(totalDataStr, UnpaidFundsDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,DIRECTOR_ONE);
        buildList(dataList,orderList,DIRECTOR_ONE);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "累计未收齐款项统计" +unpaidFundsQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }
    /**
     * 商务总监二级页面查询 分页
     */
    @PostMapping("/getDirectorTwoPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getDirectorTwoPageList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto){
        initAuth(unpaidFundsQueryDto,null);
        return unpaidFundsFeignClient.getDirectorTwoPageList(unpaidFundsQueryDto);
    }
    /**
     * 商务总监二级页面查询
     */
    @PostMapping("/exportDirectorTwoList")
    public void exportDirectorTwoList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto,HttpServletResponse response) throws IOException {
        initAuth(unpaidFundsQueryDto,null);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getUnpaidFundsTitleList(DIRECTOR_TWO));
        JSONResult<Map<String, Object>> result =  unpaidFundsFeignClient.getDirectorTwoList(unpaidFundsQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<UnpaidFundsDto> orderList = JSON.parseArray(listTxt, UnpaidFundsDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        UnpaidFundsDto sumReadd = JSON.parseObject(totalDataStr, UnpaidFundsDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,DIRECTOR_TWO);
        buildList(dataList,orderList,DIRECTOR_TWO);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "累计未收齐款项统计" +unpaidFundsQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    /**
     * 商务经理一级页面查询 分页
     */
    @PostMapping("/getManagerOnePageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getManagerOnePageList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto){
        initAuth(unpaidFundsQueryDto,null);
        return unpaidFundsFeignClient.getManagerOnePageList(unpaidFundsQueryDto);
    }
    /**
     * 商务经理一级页面查询
     */
    @PostMapping("/exportManagerOneList")
    public void exportManagerOneList(@RequestBody UnpaidFundsQueryDto unpaidFundsQueryDto,HttpServletResponse response) throws IOException {
        initAuth(unpaidFundsQueryDto,null);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getUnpaidFundsTitleList(MANAGER_ONE));
        JSONResult<Map<String, Object>> result =  unpaidFundsFeignClient.getManagerOneList(unpaidFundsQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<UnpaidFundsDto> orderList = JSON.parseArray(listTxt, UnpaidFundsDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        UnpaidFundsDto sumReadd = JSON.parseObject(totalDataStr, UnpaidFundsDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,MANAGER_ONE);
        buildList(dataList,orderList,MANAGER_ONE);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "累计未收齐款项统计" +unpaidFundsQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    /**
     *  返回页面携带参数
     */
    private void pageParams(Long userId,Long orgId,Long endTime,Long projectId,String days,HttpServletRequest request){
        UnpaidFundsQueryDto unpaidFundsQueryDto = new UnpaidFundsQueryDto();
        unpaidFundsQueryDto.setOrgId(orgId);
        unpaidFundsQueryDto.setEndTime(endTime);
        unpaidFundsQueryDto.setUserId(userId);
        unpaidFundsQueryDto.setProjectId(projectId);
        unpaidFundsQueryDto.setDays(days);
        request.setAttribute("unpaidFundsQueryDto",unpaidFundsQueryDto);
    }

    private List<OrganizationDTO> getOrgGroupByOrgId(Long orgId,Integer orgType){
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setOrgType(orgType);
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        List<OrganizationDTO> data = listJSONResult.getData();
        return data;
    }
    /**
     * 获取当前 orgId所在的组
     */
    private List<OrganizationDTO> getCurOrgGroupByOrgId(Long orgId) {
        IdEntity idEntity = new IdEntity();
        List<OrganizationDTO> data = new ArrayList<>();
        idEntity.setId(String.valueOf(orgId));
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        data.add(orgJr.getData());
        return data;
    }

    /**
     * 初始化权限
     */
    private void initAuth(UnpaidFundsQueryDto unpaidFundsQueryDto,HttpServletRequest request){
        List<OrganizationDTO> teleGroupList = new ArrayList<>();
        String curUserId = "";
        String curOrgId = "";
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            curUserId = String.valueOf(curLoginUser.getId());
            curOrgId = String.valueOf(curLoginUser.getOrgId());
            teleGroupList = getCurOrgGroupByOrgId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            curOrgId = String.valueOf(curLoginUser.getOrgId());
            teleGroupList = getCurOrgGroupByOrgId(curLoginUser.getOrgId());
        }else{
            teleGroupList = getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.SWZ);
        }
        if(null != unpaidFundsQueryDto && null != teleGroupList && teleGroupList.size() > 0){
            List<Long> orgIdList = teleGroupList.parallelStream().map(OrganizationDTO::getId).collect(Collectors.toList());
            unpaidFundsQueryDto.setOrgIdList(orgIdList);
        }
        //商务经理查询 考虑借调 删除组限制
        if(null != unpaidFundsQueryDto && RoleCodeEnum.SWJL.name().equals(roleCode)){
            unpaidFundsQueryDto.setOrgIdList(null);
            unpaidFundsQueryDto.setOrgId(null);
        }
        if(null != request){
            // 查询所有项目
            JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
            request.setAttribute("projectList", allProject.getData());
            request.setAttribute("teleGroupList",teleGroupList);
            request.setAttribute("curOrgId",curOrgId);
            request.setAttribute("curUserId",curUserId);
        }
    }
    private List<Object> getUnpaidFundsTitleList(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        if(type.equals(MANAGER_ONE) || type.equals(DIRECTOR_TWO)){
            headTitleList.add("项目");
        }
        headTitleList.add("欠款天数");
        if(type.equals(DIRECTOR_TWO)){
            headTitleList.add("商务经理");
        }
        headTitleList.add("未收齐尾款笔数");
        headTitleList.add("未收齐尾款金额");
        return headTitleList;
    }
    private void buildList(List<List<Object>> dataList, List<UnpaidFundsDto> orderList, Integer type) {
        for(int i = 0; i<orderList.size(); i++){
            UnpaidFundsDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            if(type.equals(MANAGER_ONE) || type.equals(DIRECTOR_TWO)){
                curList.add(ra.getProjectName());
            }
            curList.add(ra.getOwedDays());
            if(type.equals(DIRECTOR_TWO)){
                curList.add(ra.getUserName());
            }
            curList.add(ra.getNoCreditNum());
            curList.add(ra.getNoCreditAmount());
            dataList.add(curList);
        }
    }
    private void addTotalExportData(UnpaidFundsDto ra, List<List<Object>> dataList,Integer type) {
        List<Object> curList = new ArrayList<>();
        curList.add("");
        if(type.equals(DIRECTOR_TWO)){
            curList.add("");
            curList.add("");
        }
        if(type.equals(MANAGER_ONE)){
            curList.add("");
        }
        curList.add(ra.getOwedDays());
        curList.add(ra.getNoCreditNum());
        curList.add(ra.getNoCreditAmount());
        dataList.add(curList);
    }
}
