package com.kuaidao.manageweb.controller.statistics.firstResourceAllocation;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.FirstResourceAllocation.FirstResourceAllocationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationDto;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationQueryDto;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserDataAuthReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
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
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics/firstResourceAllocation")
public class FirstResourceAllocationController extends BaseStatisticsController {

    private static Logger logger = LoggerFactory.getLogger(FirstResourceAllocationController.class);

    @Autowired
    private FirstResourceAllocationFeignClient firstResourceAllocationFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    /**
     * 组页面查询
     */
    @RequestMapping("/getFirstResourceAllocationPage")
    @ResponseBody
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPage(
            @RequestBody( required = false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
//        Long orgId = firstResourceAllocationQueryDto.getDeptId();
//        if (null == orgId) {
//            buildOrgIdList(firstResourceAllocationQueryDto, orgId);
//            List<Long> orgIdList = firstResourceAllocationQueryDto.getOrgIdList();
//            if (orgIdList == null || orgIdList.size() == 0) {
//                PageBean emptyDataPageBean = PageBean.getEmptyListDataPageBean(
//                        firstResourceAllocationQueryDto.getPageNum(),
//                        firstResourceAllocationQueryDto.getPageSize());
//                return new JSONResult<PageBean<FirstResourceAllocationDto>>()
//                        .success(emptyDataPageBean);
//            }
//        }
        initParams(firstResourceAllocationQueryDto);
        return firstResourceAllocationFeignClient.getFirstResourceAllocationPage(firstResourceAllocationQueryDto);
    }

    /**
     * 组页面导出
     */
    @RequiresPermissions("statistics:firstResourceAllocation:export")
    @PostMapping("/exportFirstResourceAllocationPage")
    public void exportFirstResourceAllocationPage(
            @RequestBody(required = false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
            HttpServletResponse response) throws IOException {
//        Long orgId = firstResourceAllocationQueryDto.getOrgId();
//        buildOrgIdList(firstResourceAllocationQueryDto, orgId);

        initParams(firstResourceAllocationQueryDto);

        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationList =
                firstResourceAllocationFeignClient.getFirstResourceAllocationList(firstResourceAllocationQueryDto);
        List<FirstResourceAllocationDto> orderList = firstResourceAllocationList.getData();
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        // 获取合计
        FirstResourceAllocationDto countTotal = getCountTotal(orderList);
        // 加表头
        dataList.add(getHeadTitleGroup());
        // 增加合计列
        addTotalTexportData(countTotal, dataList);
        for (int i = 0; i < orderList.size(); i++) {
            FirstResourceAllocationDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getDeptName());
            curList.add(ra.getOrgName());
            curList.add(ra.getAssignClueCount());
            curList.add(ra.getCallRate());
            curList.add(ra.getTrackRate());
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
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = firstResourceAllocationQueryDto.getStartTime();
        Long endTime = firstResourceAllocationQueryDto.getEndTime();
        String name = "首次分配记录表" + startTime + "-" + endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    /**
     * 个人页面查询
     */
    @RequestMapping("/getFirstResourceAllocationPagePersion")
    @ResponseBody
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPagePersion(
            @RequestBody(
                    required = false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
        Long orgId = firstResourceAllocationQueryDto.getOrgId();
        if (null == orgId) {
            buildOrgIdList(firstResourceAllocationQueryDto, orgId);
            List<Long> orgIdList = firstResourceAllocationQueryDto.getOrgIdList();
            if (orgIdList == null || orgIdList.size() == 0) {
                PageBean emptyDataPageBean = PageBean.getEmptyListDataPageBean(
                        firstResourceAllocationQueryDto.getPageNum(),
                        firstResourceAllocationQueryDto.getPageSize());
                return new JSONResult<PageBean<FirstResourceAllocationDto>>()
                        .success(emptyDataPageBean);
            }
        }else{
            firstResourceAllocationQueryDto.setOrgIdList(Arrays.asList(orgId));
        }
        logger.info(
                "首次分配个人查询参数" + com.alibaba.fastjson.JSON.toJSONString(firstResourceAllocationQueryDto));
        return firstResourceAllocationFeignClient
                .getFirstResourceAllocationPagePersion(firstResourceAllocationQueryDto);
    }

    /**
     * 个人页面导出
     */
    @RequiresPermissions("statistics:firstResourceAllocation:export")
    @PostMapping("/exportFirstResourceAllocationPagePersion")
    public void exportFirstResourceAllocationPagePersion(
            @RequestBody(
                    required = false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
            HttpServletResponse response) throws IOException {
        Long orgId = firstResourceAllocationQueryDto.getOrgId();
        buildOrgIdList(firstResourceAllocationQueryDto, orgId);
        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationsPersion =
                firstResourceAllocationFeignClient
                        .getFirstResourceAllocationsPersion(firstResourceAllocationQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleListPersion());
        List<FirstResourceAllocationDto> orderList = firstResourceAllocationsPersion.getData();
        for (int i = 0; i < orderList.size(); i++) {
            FirstResourceAllocationDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(ra.getUserName());
            curList.add(ra.getAssignClueCount());
            curList.add(ra.getCallRate());
            curList.add(ra.getTrackRate());
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
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = firstResourceAllocationQueryDto.getStartTime();
        Long endTime = firstResourceAllocationQueryDto.getEndTime();
        String name = "首次分配记录表" + startTime + "-" + endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 个人按天查询
     */
    @RequestMapping("/getFirstResourceAllocationDayPagePersion")
    @ResponseBody
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationDayPagePersion(
            @RequestBody(
                    required = false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
        Long orgId = firstResourceAllocationQueryDto.getOrgId();
        if (null == orgId) {
            buildOrgIdList(firstResourceAllocationQueryDto, orgId);
            List<Long> orgIdList = firstResourceAllocationQueryDto.getOrgIdList();
            if (orgIdList == null || orgIdList.size() == 0) {
                PageBean emptyDataPageBean = PageBean.getEmptyListDataPageBean(
                        firstResourceAllocationQueryDto.getPageNum(),
                        firstResourceAllocationQueryDto.getPageSize());
                return new JSONResult<PageBean<FirstResourceAllocationDto>>()
                        .success(emptyDataPageBean);
            }
        }else{
            firstResourceAllocationQueryDto.setOrgIdList(Arrays.asList(orgId));
        }
        return firstResourceAllocationFeignClient
                .getFirstResourceAllocationDayPagePersion(firstResourceAllocationQueryDto);
    }

    /**
     * 个人按天导出
     */
    @RequiresPermissions("statistics:firstResourceAllocation:export")
    @PostMapping("/exportFirstResourceAllocationDayPagePersion")
    public void exportFirstResourceAllocationDayPagePersion(
            @RequestBody(
                    required = false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
            HttpServletResponse response) throws IOException {
        Long orgId = firstResourceAllocationQueryDto.getOrgId();
        buildOrgIdList(firstResourceAllocationQueryDto, orgId);
        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationsDayPersion =
                firstResourceAllocationFeignClient
                        .getFirstResourceAllocationsDayPersion(firstResourceAllocationQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleListDayPersion());
        List<FirstResourceAllocationDto> orderList = firstResourceAllocationsDayPersion.getData();
        for (int i = 0; i < orderList.size(); i++) {
            FirstResourceAllocationDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            String str = null;
            if (ra.getDateId() != null) {
                StringBuilder sb = new StringBuilder(ra.getDateId().toString());
                sb.insert(6, "-");
                sb.insert(4, "-");
                str = sb.toString();
            }
            curList.add(str);
            curList.add(ra.getUserName());
            curList.add(ra.getAssignClueCount());
            curList.add(ra.getTrackRate());
            curList.add(ra.getCallRate());
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
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = firstResourceAllocationQueryDto.getStartTime();
        Long endTime = firstResourceAllocationQueryDto.getEndTime();
        String name = "首次分配记录表" + startTime + "-" + endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 组
     *
     * @return
     */
    @RequestMapping("/firstRATable")
    public String firstRATable(Long orgId, Long startTime, Long endTime, Integer isTransfer,
                               HttpServletRequest request) {
        FirstResourceAllocationQueryDto fraQueryDto = new FirstResourceAllocationQueryDto();
        fraQueryDto.setOrgId(orgId);
        fraQueryDto.setStartTime(startTime);
        fraQueryDto.setEndTime(endTime);
        fraQueryDto.setIsTransfer(isTransfer);
        request.setAttribute("fraQueryDto", fraQueryDto);

        super.initSaleDept(request);

        UserInfoDTO user = getUser();
        Map<String, Object> orgList = getOrgList();
        String curOrgId = (String) orgList.get("curOrgId");
        List<OrganizationRespDTO> teleGroupList =
                (List<OrganizationRespDTO>) orgList.get("saleGroupList");
        request.setAttribute("curOrgId", curOrgId);
        request.setAttribute("saleGroupList", teleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:firstResourceAllocation");
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
        queryFieldByUserAndMenuReq.setMenuCode("statistics:firstResourceAllocation");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        return "reportforms/firstRATable";
    }


    @RequestMapping("/getFirstResourceAllocationCount")
    @ResponseBody
    public JSONResult<List<FirstResourceAllocationDto>> getGroupCountTotal(
            @RequestBody FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
//        Long orgId = firstResourceAllocationQueryDto.getOrgId();
//        buildOrgIdList(firstResourceAllocationQueryDto, orgId);
        initParams(firstResourceAllocationQueryDto);
        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationList =
                firstResourceAllocationFeignClient.getFirstResourceAllocationList(firstResourceAllocationQueryDto);
        FirstResourceAllocationDto countTotal = getCountTotal(firstResourceAllocationList.getData());
        List<FirstResourceAllocationDto> list = new ArrayList<>();
        list.add(countTotal);
        return new JSONResult<List<FirstResourceAllocationDto>>().success(list);
    }


    /**
     * 合计
     *
     * @return
     */
    @RequestMapping("/firstRATableSum")
    public String firstRATableSum(Long deptId,Long orgId, Long startTime, Long endTime, Integer isTransfer,
                                  HttpServletRequest request) {
        FirstResourceAllocationQueryDto fraQueryDto = new FirstResourceAllocationQueryDto();
        fraQueryDto.setOrgId(orgId);
        fraQueryDto.setDeptId(deptId);
        fraQueryDto.setStartTime(startTime);
        fraQueryDto.setEndTime(endTime);
        fraQueryDto.setIsTransfer(isTransfer);
        request.setAttribute("fraQueryDto", fraQueryDto);
        UserInfoDTO user = getUser();
        Map<String, Object> orgList = getOrgList();
        String curOrgId = (String) orgList.get("curOrgId");

        super.initSaleDept(request);

        List<OrganizationRespDTO> teleGroupList =
                (List<OrganizationRespDTO>) orgList.get("saleGroupList");
        request.setAttribute("curOrgId", curOrgId);
        request.setAttribute("saleGroupList", teleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:firstResourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        List<CustomFieldQueryDTO> listField=queryFieldByRoleAndMenu.getData();
        listField.removeIf(s -> s.getFieldCode().equals("orgName"));
        listField.removeIf(s -> s.getFieldCode().equals("deptName"));
        request.setAttribute("fieldList", listField);
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("statistics:firstResourceAllocation");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        return "reportforms/firstRATableSum";
    }

    /**
     * 组
     *
     * @return
     */
    @RequestMapping("/firstRATableTeam")
    public String firstRATableTeam(Long orgId, Long startTime, Long endTime, Integer isTransfer,
                                   HttpServletRequest request) {
        FirstResourceAllocationQueryDto fraQueryDto = new FirstResourceAllocationQueryDto();
        fraQueryDto.setOrgId(orgId);

        fraQueryDto.setStartTime(startTime);
        fraQueryDto.setEndTime(endTime);
        fraQueryDto.setIsTransfer(isTransfer);

        request.setAttribute("fraQueryDto", fraQueryDto);
        UserInfoDTO user = getUser();
        Map<String, Object> orgList = getOrgList();
        String curOrgId = (String) orgList.get("curOrgId");
        List<OrganizationRespDTO> teleGroupList =
                (List<OrganizationRespDTO>) orgList.get("saleGroupList");
        request.setAttribute("curOrgId", curOrgId);
        request.setAttribute("saleGroupList", teleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:firstResourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        List<CustomFieldQueryDTO> data = queryFieldByRoleAndMenu.getData();
        data.removeIf(s -> s.getFieldCode().equals("day"));
        data.removeIf(s -> s.getFieldCode().equals("deptName"));
        request.setAttribute("fieldList", data);
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("statistics:firstResourceAllocation");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        return "reportforms/firstRATableTeam";
    }

    /**
     * 个人
     *
     * @return
     */
    @RequestMapping("/firstRATablePerson")
    public String firstRATablePerson(Long orgId, Long startTime, Long endTime, Integer isTransfer,
                                     Long userId, HttpServletRequest request) {
        FirstResourceAllocationQueryDto fraQueryDto = new FirstResourceAllocationQueryDto();
        fraQueryDto.setOrgId(orgId);
        fraQueryDto.setStartTime(startTime);
        fraQueryDto.setEndTime(endTime);
        fraQueryDto.setIsTransfer(isTransfer);
        fraQueryDto.setUserId(userId);
        request.setAttribute("fraQueryDto", fraQueryDto);
        UserInfoDTO user = getUser();
        Map<String, Object> orgList = getOrgList();
        String curOrgId = (String) orgList.get("curOrgId");
        List<OrganizationRespDTO> teleGroupList =
                (List<OrganizationRespDTO>) orgList.get("saleGroupList");
        request.setAttribute("curOrgId", curOrgId);
        request.setAttribute("saleGroupList", teleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:firstResourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        queryFieldByRoleAndMenu.getData().removeIf(s -> s.getFieldCode().equals("deptName"));
        queryFieldByRoleAndMenu.getData().removeIf(s -> s.getFieldCode().equals("orgName"));
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("statistics:firstResourceAllocation");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        return "reportforms/firstRATablePerson";
    }


    private List<Object> getHeadTitleGroup() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("事业部");
        headTitleList.add("电销组");
        headTitleList.add("首次分配资源数");
        headTitleList.add("资源回访率");
        headTitleList.add("首次资源回访率");
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
        return headTitleList;
    }

    private List<Object> getHeadTitleListPersion() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("电销顾问");
        headTitleList.add("首次分配资源数");
        headTitleList.add("首次分源数回访率");
        headTitleList.add("资源回访率");
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
        return headTitleList;
    }

    private List<Object> getHeadTitleListDayPersion() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("日期");
//        headTitleList.add("电销组");
        headTitleList.add("电销顾问");
        headTitleList.add("首次分配资源数");
        headTitleList.add("资源回访率");
        headTitleList.add("首次资源跟访率");
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
        return headTitleList;
    }

    /**
     * 查询合计
     */
    private FirstResourceAllocationDto getCountTotal(List<FirstResourceAllocationDto> list) {
        FirstResourceAllocationDto firstResourceAllocationDto = new FirstResourceAllocationDto();
        // 首次分配资源数
        Long assignClueCount = list.stream().mapToLong(FirstResourceAllocationDto::getAssignClueCount).sum();
        // 联展
        Long jointExhibition = list.stream().mapToLong(FirstResourceAllocationDto::getJointExhibition).sum();
        // 竞价
        Long priceCompetition = list.stream().mapToLong(FirstResourceAllocationDto::getPriceCompetition).sum();
        // 优化
        Long optimization = list.stream().mapToLong(FirstResourceAllocationDto::getOptimization).sum();
        // 信息流
        Long informationFlow = list.stream().mapToLong(FirstResourceAllocationDto::getInformationFlow).sum();
        // 官网
        Long officialWebsite = list.stream().mapToLong(FirstResourceAllocationDto::getOfficialWebsite).sum();
        // 行业
        Long industry = list.stream().mapToLong(FirstResourceAllocationDto::getIndustry).sum();
        // 其他
        Long other = list.stream().mapToLong(FirstResourceAllocationDto::getOther).sum();
        // 新媒体
        Long netizensMissed = list.stream().mapToLong(FirstResourceAllocationDto::getNetizensMissed).sum();
        // 品牌
        Long brand = list.stream().mapToLong(FirstResourceAllocationDto::getBrand).sum();

        Long callCounts=list.stream().mapToLong(FirstResourceAllocationDto::getCallCounts).sum();

        Long trackCounts=list.stream().mapToLong(FirstResourceAllocationDto::getTrackCounts).sum();

        // 商机盒子
        Long sjhz = list.stream().mapToLong(FirstResourceAllocationDto::getSjhz).sum();
        //餐盟平台
        Long cmpt = list.stream().mapToLong(FirstResourceAllocationDto::getCmpt).sum();
        firstResourceAllocationDto.setOrgId(0L);
        firstResourceAllocationDto.setDeptName("");
        firstResourceAllocationDto.setOrgName("合计");
        firstResourceAllocationDto.setAssignClueCount(assignClueCount);
        firstResourceAllocationDto.setCallCounts(callCounts);
        firstResourceAllocationDto.setTrackCounts(trackCounts);
        firstResourceAllocationDto.setJointExhibition(jointExhibition);
        firstResourceAllocationDto.setPriceCompetition(priceCompetition);
        firstResourceAllocationDto.setOptimization(optimization);
        firstResourceAllocationDto.setInformationFlow(informationFlow);
        firstResourceAllocationDto.setOfficialWebsite(officialWebsite);
        firstResourceAllocationDto.setIndustry(industry);
        firstResourceAllocationDto.setBrand(brand);
        firstResourceAllocationDto.setSjhz(sjhz);
        firstResourceAllocationDto.setCmpt(cmpt);
        firstResourceAllocationDto.setOther(other);
        firstResourceAllocationDto.setTrackCounts(trackCounts);
        firstResourceAllocationDto.setCallCounts(callCounts);
        firstResourceAllocationDto.setNetizensMissed(netizensMissed);
        return firstResourceAllocationDto;
    }


    private Map<String, Object> getOrgList() {
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO> teleGroupList = new ArrayList<>();
        if (RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId = String.valueOf(curLoginUser.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            // 电销总监查他自己的组
            if (curOrgGroupByOrgId != null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                teleGroupList.add(organizationRespDTO);
            }
        } else if(RoleCodeEnum.GLY.name().equals(roleCode) || RoleCodeEnum.DXZC.name().equals(roleCode)){
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setOrgType(OrgTypeConstant.DXZ);
            JSONResult<List<OrganizationRespDTO>> json= organizationFeignClient.queryOrgByParam(queryDTO);
            if("0".equals(json.getCode())){
                teleGroupList=json.getData();
            }
        }else if(RoleCodeEnum.TGZJ.name().equals(roleCode) || RoleCodeEnum.NQJL.name().equals(roleCode) || RoleCodeEnum.NQZG.name().equals(roleCode)){
            //该角色下 查询 授权的业务线数据
            List<UserDataAuthReq> authList=curLoginUser.getUserDataAuthList();
            teleGroupList=queryOrgByUserAuth(authList,OrgTypeConstant.DXZ);
        }else{
            teleGroupList = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("curOrgId", curOrgId);
        resultMap.put("saleGroupList", teleGroupList);
        return resultMap;
    }

    /**
     * 获取当前 orgId所在的组织
     *
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId + "");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if (!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("first resourceAllocation getCurOrgGroupByOrgId,param{{}},res{{}}",
                    idEntity, orgJr);
            return null;
        }
        return orgJr.getData();
    }

    /**
     * 获取当前 orgId 下的 电销组
     *
     * @param orgId
     * @param orgType
     * @return
     */
    private List<OrganizationRespDTO> getOrgGroupByOrgId(Long orgId, Integer orgType) {
        // 电销组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setParentId(orgId);
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
        return list;
    }

    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }


    private void buildOrgIdList(
            @RequestBody FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
            Long orgId) {
        if (null == orgId) {
            logger.info("查询默认组");
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
            if(RoleCodeEnum.TGZJ.name().equals(roleCode) || RoleCodeEnum.NQJL.name().equals(roleCode) || RoleCodeEnum.NQZG.name().equals(roleCode)){
                List<UserDataAuthReq> authList = curLoginUser.getUserDataAuthList();
                List<OrganizationRespDTO> list = queryOrgByUserAuth(authList, OrgTypeConstant.DXZ);
                if (!list.isEmpty()) {
                    List<Long> orgids = list.stream().map(c -> c.getId()).collect(Collectors.toList());
                    firstResourceAllocationQueryDto.setOrgIdList(orgids);
                } else {
                    firstResourceAllocationQueryDto.setOrgIdList(Arrays.asList(-1l));
                }
            }else {
                List<OrganizationRespDTO> orgGroupByOrgId =
                        getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
                List<Long> orgIdList = orgGroupByOrgId.parallelStream().map(OrganizationRespDTO::getId)
                        .collect(Collectors.toList());
                firstResourceAllocationQueryDto.setOrgIdList(orgIdList);
            }
        }else{
            firstResourceAllocationQueryDto.setOrgIdList(Arrays.asList(orgId));
        }
    }

    /**
     * 参数控制权限-已经显示结果
     * 一级列表所有权限筛选由 组id控制
     * @param baseQueryDto
     */
    public void initParams(FirstResourceAllocationQueryDto baseQueryDto){
        //筛选组
        if(null!=baseQueryDto.getOrgId()){
            List<Long> ids= Arrays.asList(baseQueryDto.getOrgId());
            baseQueryDto.setOrgIdList(ids);
            return ;
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        //电销组
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();

        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
            List<OrganizationRespDTO> orgGroupByOrgId = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
            List<Long> orgids = orgGroupByOrgId.stream().map(c->c.getId()).collect(Collectors.toList());
            baseQueryDto.setOrgIdList(orgids);
            return;
            //如果有事业部筛选
//            if(null!=baseQueryDto.getDeptId()){
//                queryDTO.setParentId(baseQueryDto.getDeptId());
//            }else{
//                queryDTO.setParentId(curLoginUser.getOrgId());
//            }
        }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
            queryDTO.setParentId(curLoginUser.getOrgId());
            if(null!=baseQueryDto.getDeptId()){
                queryDTO.setParentId(baseQueryDto.getDeptId());
            }
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode) || RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            baseQueryDto.setOrgIdList(Arrays.asList(curLoginUser.getOrgId()));
            return;
        }else if(RoleCodeEnum.GLY.name().equals(roleCode) || RoleCodeEnum.DXZC.name().equals(roleCode)){
            //管理员可以查看全部
            if(null!=baseQueryDto.getDeptId()){
                queryDTO.setParentId(baseQueryDto.getDeptId());
            }
        }else if(RoleCodeEnum.TGZJ.name().equals(roleCode) || RoleCodeEnum.NQJL.name().equals(roleCode) || RoleCodeEnum.NQZG.name().equals(roleCode)){
            //有条件筛选
            if(null!=baseQueryDto.getDeptId()){
                queryDTO.setParentId(baseQueryDto.getDeptId());
            }else {
                //该角色下 查询 授权的业务线数据
                List<UserDataAuthReq> authList = curLoginUser.getUserDataAuthList();
                List<OrganizationRespDTO> list = queryOrgByUserAuth(authList, OrgTypeConstant.DXZ);
                if (!list.isEmpty()) {
                    List<Long> orgids = list.stream().map(c -> c.getId()).collect(Collectors.toList());
                    baseQueryDto.setOrgIdList(orgids);
                } else {
                    baseQueryDto.setOrgIdList(Arrays.asList(-1l));
                }
                return;
            }
        }else{
            //other 没权限
            queryDTO.setId(curLoginUser.getOrgId());
        }
        JSONResult<List<OrganizationRespDTO>> json= organizationFeignClient.queryOrgByParam(queryDTO);
        if("0".equals(json.getCode()) && null!=json.getData() && json.getData().size()>0){
            List<Long> orgids=json.getData().stream().map(c->c.getId()).collect(Collectors.toList());
            baseQueryDto.setOrgIdList(orgids);
        }else{
            baseQueryDto.setOrgIdList(Arrays.asList(-1l));
        }
    }

    private void addTotalTexportData(FirstResourceAllocationDto resTotal,
                                     List<List<Object>> dataList) {
        List<Object> totalList = new ArrayList<>();
        totalList.add("");
        totalList.add("");
        totalList.add("合计");
        totalList.add(resTotal.getAssignClueCount());
//        totalList.add(resTotal.getCallCounts());
//        totalList.add(resTotal.getTrackCounts());
        totalList.add(resTotal.getCallRate());
        totalList.add(resTotal.getTrackRate());
        totalList.add(resTotal.getJointExhibition());
        totalList.add(resTotal.getPriceCompetition());
        totalList.add(resTotal.getOptimization());
        totalList.add(resTotal.getInformationFlow());
        totalList.add(resTotal.getOfficialWebsite());
        totalList.add(resTotal.getIndustry());
        totalList.add(resTotal.getBrand());
        totalList.add(resTotal.getSjhz());
        totalList.add(resTotal.getCmpt());
        totalList.add(resTotal.getOther());
        totalList.add(resTotal.getNetizensMissed());
        dataList.add(totalList);
    }


}
