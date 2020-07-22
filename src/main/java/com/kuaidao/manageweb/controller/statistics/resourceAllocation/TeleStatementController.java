package com.kuaidao.manageweb.controller.statistics.resourceAllocation;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.resourceAllocation.StatisticsFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.callrecord.TeleSaleTempDTO;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceAllocationDto;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceAllocationQueryDto;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /**
     * 资源分配（组）
     * @return
     */
    @RequestMapping("/resourceAllocation")
    public String resourceAllocationTable(HttpServletRequest request) throws Exception{
        UserInfoDTO user = getUser();
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);

        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        List<CustomFieldQueryDTO> data = queryFieldByRoleAndMenu.getData();
        data.removeIf(s -> s.getFieldCode().equals("day"));
        data.removeIf(s -> s.getFieldCode().equals("userName"));
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        return "reportforms/resourceAllocationTable";
    }

    /**
     * 个人按天
     */
    @RequestMapping("/getResourceAllocationsDayPersion")
    @ResponseBody
    public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationsDayPersion(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto) throws Exception{
        Long org_id = resourceAllocationQueryDto.getOrg_Id();
        buildOrgIdList(resourceAllocationQueryDto, org_id);
        JSONResult<PageBean<ResourceAllocationDto>> resourceAllocationDayPagePersion =
                statisticsFeignClient.getResourceAllocationDayPagePersion(resourceAllocationQueryDto);
        return resourceAllocationDayPagePersion;
    }

    /**
     * 资源分配页面（组）
    * @return
     */
    @RequestMapping("/getResourceAllocationTable")
    @ResponseBody
    public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationTable(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto) {
        Long org_id = resourceAllocationQueryDto.getOrg_Id();
        if(null == org_id){
            buildOrgIdList(resourceAllocationQueryDto, org_id);
            List<Long> orgIdList = resourceAllocationQueryDto.getOrgIdList();
            if(orgIdList == null || orgIdList.size() == 0){
                PageBean emptyDataPageBean = PageBean.getEmptyListDataPageBean(resourceAllocationQueryDto.getPageNum(), resourceAllocationQueryDto.getPageSize());
                return new JSONResult<PageBean<ResourceAllocationDto>>().success(emptyDataPageBean);
            }
        }
        JSONResult<PageBean<ResourceAllocationDto>> resourceAllocationPage = statisticsFeignClient.getResourceAllocationPage(resourceAllocationQueryDto);
        return resourceAllocationPage;
    }

    /**
     * 获取分配页面统计数据
     * @param resourceAllocationQueryDto
     * @return
     */
    @RequestMapping("/getResourceAllocationCount")
    @ResponseBody
    public JSONResult<List<ResourceAllocationDto>> getGroupCountTotal(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto){
        Long org_id = resourceAllocationQueryDto.getOrg_Id();
        buildOrgIdList(resourceAllocationQueryDto, org_id);
        JSONResult<List<ResourceAllocationDto>> countRes = statisticsFeignClient.getResourceAllocationCount(resourceAllocationQueryDto);
        return countRes;
    }

    /**
     * 导出（组）
     * @param response
     * @throws Exception
     */
    @RequiresPermissions("statistics:teleStatement:resourceAllocation:export")
    @PostMapping("/exportResourceAllocationGroup")
    public void exportResourceAllocation(
            @RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto,
            HttpServletResponse response) throws Exception {
        Long org_id = resourceAllocationQueryDto.getOrg_Id();
        buildOrgIdList(resourceAllocationQueryDto, org_id);
        JSONResult<List<ResourceAllocationDto>> resourceAllocationList = statisticsFeignClient.getResourceAllocationList(resourceAllocationQueryDto);
        JSONResult<List<ResourceAllocationDto>> countRes = statisticsFeignClient.getResourceAllocationCount(resourceAllocationQueryDto);
        List<ResourceAllocationDto> total = countRes.getData();
        ResourceAllocationDto resTotal = total.get(0);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        //加表头
        dataList.add(getHeadTitleList());
        //加合计
        addTotalTexportResourceAllocation(resTotal,dataList);
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
            curList.add(ra.getBrand());
            curList.add(ra.getSjhz());
            curList.add(ra.getCmpt());
            curList.add(ra.getOther());
            curList.add(ra.getNetizensMissed());
            curList.add(ra.getOther2());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = resourceAllocationQueryDto.getStartTime();
        Long endTime = resourceAllocationQueryDto.getEndTime();
        String name = "分配记录表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    private void buildOrgIdList(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto, Long org_id) {
        if(null == org_id){
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            List<OrganizationRespDTO> orgGroupByOrgId = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
            List<Long> orgIdList = orgGroupByOrgId.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
            resourceAllocationQueryDto.setOrgIdList(orgIdList);
        }
    }


    private void addTotalTexportResourceAllocation(ResourceAllocationDto resTotal, List<List<Object>> dataList) {
        List<Object> totalList = new ArrayList<>();
        totalList.add("");
        totalList.add("合计");
        totalList.add(resTotal.getAssignClueCount());
        totalList.add(resTotal.getJointExhibition());
        totalList.add(resTotal.getPriceCompetition());
        totalList.add(resTotal.getOptimization());
        totalList.add(resTotal.getInformationFlow());
        totalList.add(resTotal.getOfficialWebsite());
        totalList.add(resTotal.getIndustry());
        totalList.add(resTotal.getBrand());
        totalList.add(resTotal.getSjhz());
        totalList.add(resTotal.getOther());
        totalList.add(resTotal.getNetizensMissed());
        totalList.add(resTotal.getOther2());
        dataList.add(totalList);
    }

    /**
     * 资源分配页面（个人）
     * @return
     */
    @RequestMapping("/getResourceAllocationPersionTable")
    @ResponseBody
    public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationPersionTable(
            @RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto,HttpServletRequest request) {
        Long org_id = resourceAllocationQueryDto.getOrg_Id();
        buildOrgIdList(resourceAllocationQueryDto, org_id);
        UserInfoDTO user = getUser();
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);

        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        List<CustomFieldQueryDTO> data = queryFieldByRoleAndMenu.getData();
//        data.removeIf(s -> s.getFieldCode().equals("orgName"));
//        data.removeIf(s -> s.getFieldCode().equals("day"));
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        JSONResult<PageBean<ResourceAllocationDto>> resourceAllocationPage = statisticsFeignClient.getResourceAllocationPagePersion(resourceAllocationQueryDto);
        return resourceAllocationPage;
    }

    /**
     * 导出个人
     * @param resourceAllocationQueryDto
     * @param response
     * @throws Exception
     */
    @RequiresPermissions("statistics:teleStatement:resourceAllocation:export")
    @PostMapping("/exportResourceAllocationPersion")
    public void exportResourceAllocationPersion(
            @RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto,
            HttpServletResponse response) throws Exception {
        Long org_id = resourceAllocationQueryDto.getOrg_Id();
        buildOrgIdList(resourceAllocationQueryDto, org_id);
        JSONResult<List<ResourceAllocationDto>> resourceAllocationList = statisticsFeignClient.getResourceAllocationsPersion(resourceAllocationQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleListPersion());
        List<ResourceAllocationDto> orderList = resourceAllocationList.getData();
        for(int i = 0; i<orderList.size(); i++){
            ResourceAllocationDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(ra.getUserName());
            curList.add(ra.getAssignClueCount());
            curList.add(ra.getJointExhibition());
            curList.add(ra.getPriceCompetition());
            curList.add(ra.getOptimization());
            curList.add(ra.getInformationFlow());
            curList.add(ra.getOfficialWebsite());
            curList.add(ra.getIndustry());
            curList.add(ra.getBrand());
            curList.add(ra.getSjhz());
            curList.add(ra.getCmpt());
            curList.add(ra.getOther());
            curList.add(ra.getNetizensMissed());
            curList.add(ra.getOther2());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = resourceAllocationQueryDto.getStartTime();
        Long endTime = resourceAllocationQueryDto.getEndTime();
        String name = "分配记录表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 个人按天导出
     */
    @RequiresPermissions("statistics:teleStatement:resourceAllocation:export")
    @PostMapping("/exportResourceAllocationDayPersion")
    public void exportResourceAllocationDayPersion(
            @RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto,
            HttpServletResponse response) throws Exception {
        Long org_id = resourceAllocationQueryDto.getOrg_Id();
        buildOrgIdList(resourceAllocationQueryDto, org_id);
        JSONResult<List<ResourceAllocationDto>> resourceAllocationsDayPersion = statisticsFeignClient.getResourceAllocationsDayPersion(resourceAllocationQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleListDayPersion());
        List<ResourceAllocationDto> orderList = resourceAllocationsDayPersion.getData();
        for(int i = 0; i<orderList.size(); i++){
            ResourceAllocationDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            String str = null;
            if(ra.getDateId() != null){
                StringBuilder sb = new StringBuilder(ra.getDateId().toString());
                sb.insert(6,"-");
                sb.insert(4,"-");
                str = sb.toString();
            }
            curList.add(str);
            curList.add(ra.getUserName());
            curList.add(ra.getAssignClueCount());
            curList.add(ra.getJointExhibition());
            curList.add(ra.getPriceCompetition());
            curList.add(ra.getOptimization());
            curList.add(ra.getInformationFlow());
            curList.add(ra.getOfficialWebsite());
            curList.add(ra.getIndustry());
            curList.add(ra.getBrand());
            curList.add(ra.getSjhz());
            curList.add(ra.getCmpt());
            curList.add(ra.getOther());
            curList.add(ra.getNetizensMissed());
            curList.add(ra.getOther2());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = resourceAllocationQueryDto.getStartTime();
        Long endTime = resourceAllocationQueryDto.getEndTime();
        String name = "分配记录表" +startTime+"-"+endTime + ".xlsx";
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
    public String resourceAllocationTableSum(Long org_Id,Long startTime,Long endTime,Integer new_resource,HttpServletRequest request) {
        ResourceAllocationQueryDto resourceAllocationQueryDto = new ResourceAllocationQueryDto();
        resourceAllocationQueryDto.setOrg_Id(org_Id);
        resourceAllocationQueryDto.setStartTime(startTime);
        resourceAllocationQueryDto.setEndTime(endTime);
        resourceAllocationQueryDto.setNew_resource(new_resource);
        request.setAttribute("resourceAllocationQueryDto",resourceAllocationQueryDto);
        UserInfoDTO user = getUser();
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        List<CustomFieldQueryDTO> data = queryFieldByRoleAndMenu.getData();
//        data.removeIf(s -> s.getFieldCode().equals("day"));
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        return "reportforms/resourceAllocationTableSum";
    }
    
    
    /**
     * 资源分配页面  组 
    * @return
     */
    @RequestMapping("/resourceAllocationTableTeam")
    public String resourceAllocationTableTeam(Long org_Id,Long startTime,Long endTime,Integer new_resource,HttpServletRequest request) {
        ResourceAllocationQueryDto resourceAllocationQueryDto = new ResourceAllocationQueryDto();
        resourceAllocationQueryDto.setOrg_Id(org_Id);
        resourceAllocationQueryDto.setStartTime(startTime);
        resourceAllocationQueryDto.setEndTime(endTime);
        resourceAllocationQueryDto.setNew_resource(new_resource);
        request.setAttribute("resourceAllocationQueryDto",resourceAllocationQueryDto);
        UserInfoDTO user = getUser();
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        List<CustomFieldQueryDTO> data = queryFieldByRoleAndMenu.getData();
        data.removeIf(s -> s.getFieldCode().equals("day"));
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        return "reportforms/resourceAllocationTableTeam";
    }
    
    
    /**
     * 资源分配页面  个人 
    * @return
     */
    @RequestMapping("/resourceAllocationTablePerson")
    public String resourceAllocationTablePerson(Long user_id,Long org_Id,Long startTime,Long endTime,Integer new_resource,HttpServletRequest request) {
        ResourceAllocationQueryDto resourceAllocationQueryDto = new ResourceAllocationQueryDto();
        resourceAllocationQueryDto.setUser_Id(user_id);
        resourceAllocationQueryDto.setOrg_Id(org_Id);
        resourceAllocationQueryDto.setStartTime(startTime);
        resourceAllocationQueryDto.setEndTime(endTime);
        resourceAllocationQueryDto.setNew_resource(new_resource);
        request.setAttribute("resourceAllocationQueryDto",resourceAllocationQueryDto);
        UserInfoDTO user = getUser();
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        return "reportforms/resourceAllocationTablePerson";
    }

    /**
     * 资源分配页面  个人
     * @return
     */
    @RequestMapping("/getResourceAllocationDayPagePersion")
    @ResponseBody
    public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationDayPagePersion(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto) {
        JSONResult<PageBean<ResourceAllocationDto>> resourceAllocationPage = statisticsFeignClient.getResourceAllocationDayPagePersion(resourceAllocationQueryDto);
        return resourceAllocationPage;
    }
    

    
    
    /**
     * 电销顾问通话时长表页面
    * @return
     */
    @RequestMapping("/telemarketingCallTable")
    public String telemarketingCallTable(HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("teleGroupList",teleGroupList);
        return "reportforms/telemarketingCallTable";
    }
    
    
    /**
     * 获取当前 orgId所在的组织
    * @param orgId
    * @param
    * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId+"");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }
    
    /**
     * 获取当前 orgId 下的 电销组
    * @param orgId
    * @param orgType
    * @return
     */
    private List<OrganizationRespDTO> getOrgGroupByOrgId(Long orgId,Integer orgType) {
        // 电销组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        if(!JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
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
        return list;
    }
    
    
   private List<UserInfoDTO> getUserInfoByOrgId(Long orgId,String roleCode){
      
       UserOrgRoleReq req = new UserOrgRoleReq();
       req.setOrgId(orgId);
       req.setRoleCode(RoleCodeEnum.DXCYGW.name());
       JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
       if(userJr==null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
           logger.error("查询电销组-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",req,userJr);
       }
       return userJr.getData();
   }
    
    /**
     * 电销顾问通话时长表页面  合计
    * @return
     */
    @RequestMapping("/telemarketingCallTableSum")
    public String telemarketingCallTableSum(Long orgId,Long startTime,Long endTime,Long userId,HttpServletRequest request) {
        TeleSaleTempDTO teleSaleTempDTO = new TeleSaleTempDTO();
        teleSaleTempDTO.setStartTime(startTime);
        teleSaleTempDTO.setEndTime(endTime);
        teleSaleTempDTO.setOrgId(orgId);
        teleSaleTempDTO.setUserId(userId);
        request.setAttribute("parentParam",teleSaleTempDTO);
        
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            curOrgId = String.valueOf(curLoginUser.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        
        request.setAttribute("teleGroupList",teleGroupList);
        request.setAttribute("curOrgId",curOrgId);
        return "reportforms/telemarketingCallTableSum";
    }
    
    
    
    /**
     * 电销顾问通话时长表页面  组
    * @return
     */
    @RequestMapping("/telemarketingCallTableTeam")
    public String telemarketingCallTableTeam(Long orgId,Long startTime,Long endTime,Long userId,HttpServletRequest request) {
        TeleSaleTempDTO teleSaleTempDTO = new TeleSaleTempDTO();
        teleSaleTempDTO.setStartTime(startTime);
        teleSaleTempDTO.setEndTime(endTime);
        teleSaleTempDTO.setOrgId(orgId);
        teleSaleTempDTO.setUserId(userId);
        request.setAttribute("parentParam",teleSaleTempDTO);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();

        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String  curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        
        request.setAttribute("teleGroupList",teleGroupList);
        
        request.setAttribute("curOrgId",curOrgId);
        return "reportforms/telemarketingCallTableTeam";
    }
    
    
    /**
     * 电销顾问通话时长表页面  个人
    * @return
     */
    @RequestMapping("/telemarketingCallTablePerson")
    public String telemarketingCallTablePerson(Long orgId,Long startTime,Long endTime,Long userId,HttpServletRequest request) {
        TeleSaleTempDTO teleSaleTempDTO = new TeleSaleTempDTO();
        teleSaleTempDTO.setStartTime(startTime);
        teleSaleTempDTO.setEndTime(endTime);
        teleSaleTempDTO.setOrgId(orgId);
        teleSaleTempDTO.setUserId(userId);
        request.setAttribute("parentParam",teleSaleTempDTO);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String  curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            curOrgId = String.valueOf(curLoginUser.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        
        request.setAttribute("teleGroupList",teleGroupList);
        request.setAttribute("curOrgId",curOrgId);
        return "reportforms/telemarketingCallTablePerson";
    }



    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组名称");
        headTitleList.add("分配资源数");
        headTitleList.add("联展");
        headTitleList.add("竞价");
        headTitleList.add("优化");
        headTitleList.add("信息流");
        headTitleList.add("官网");
        headTitleList.add("行业");
        headTitleList.add("品牌");
        headTitleList.add("商机盒子");
        headTitleList.add("餐盟平台");
        headTitleList.add("其他");
        headTitleList.add("新媒体");
        headTitleList.add("其他2");
        return headTitleList;
    }

    private List<Object> getHeadTitleListPersion() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组名称");
        headTitleList.add("电销人员");
        headTitleList.add("分配资源数");
        headTitleList.add("联展");
        headTitleList.add("竞价");
        headTitleList.add("优化");
        headTitleList.add("信息流");
        headTitleList.add("官网");
        headTitleList.add("行业");
        headTitleList.add("品牌");
        headTitleList.add("商机盒子");
        headTitleList.add("餐盟平台");
        headTitleList.add("其他");
        headTitleList.add("新媒体");
        headTitleList.add("其他2");
        return headTitleList;
    }

    private List<Object> getHeadTitleListDayPersion() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组名称");
        headTitleList.add("日期");
        headTitleList.add("电销人员");
        headTitleList.add("分配资源数");
        headTitleList.add("联展");
        headTitleList.add("竞价");
        headTitleList.add("优化");
        headTitleList.add("信息流");
        headTitleList.add("官网");
        headTitleList.add("行业");
        headTitleList.add("品牌");
        headTitleList.add("商机盒子");
        headTitleList.add("餐盟平台");
        headTitleList.add("其他");
        headTitleList.add("新媒体");
        headTitleList.add("其他2");
        return headTitleList;
    }

    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /***
     * 下属电销员工列表
     *
     * @return
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

}
