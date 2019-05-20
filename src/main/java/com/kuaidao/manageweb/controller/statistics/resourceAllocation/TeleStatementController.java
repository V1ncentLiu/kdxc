package com.kuaidao.manageweb.controller.statistics.resourceAllocation;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.resourceAllocation.StatisticsFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceAllocationDto;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceAllocationQueryDto;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
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
    public String resourceAllocationTable(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 查询所有电销组
        List<OrganizationRespDTO> saleGroupList = getSaleGroupList();
        request.setAttribute("saleGroupList", saleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        return "/reportforms/resourceAllocationTable";
    }

    /**
     * 资源分配页面（组）
    * @return
     */
    @RequestMapping("/getResourceAllocationTable")
    @ResponseBody
    public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationTable(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto) {
        JSONResult<PageBean<ResourceAllocationDto>> resourceAllocationPage = statisticsFeignClient.getResourceAllocationPage(resourceAllocationQueryDto);
        System.out.println(resourceAllocationPage);
        return resourceAllocationPage;
    }

    /**
     * 导出（组）
     * @param response
     * @throws Exception
     */
    @PostMapping("/exportResourceAllocationGroup")
    public void exportResourceAllocation(
            @RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto,
            HttpServletResponse response) throws Exception {
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
     * 资源分配页面（个人）
     * @return
     */
    @RequestMapping("/getResourceAllocationPersionTable")
    @ResponseBody
    public JSONResult<PageBean<ResourceAllocationDto>> getResourceAllocationPersionTable(@RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 查询所有电销组
        List<OrganizationRespDTO> saleGroupList = getSaleGroupList();
        request.setAttribute("saleGroupList", saleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        JSONResult<PageBean<ResourceAllocationDto>> resourceAllocationPage = statisticsFeignClient.getResourceAllocationPagePersion(resourceAllocationQueryDto);
        System.out.println(resourceAllocationPage);
        return resourceAllocationPage;
    }

    /**
     * 导出个人
     * @param resourceAllocationQueryDto
     * @param response
     * @throws Exception
     */
    @PostMapping("/exportResourceAllocationPersion")
    public void exportResourceAllocationPersion(
            @RequestBody ResourceAllocationQueryDto resourceAllocationQueryDto,
            HttpServletResponse response) throws Exception {
        JSONResult<List<ResourceAllocationDto>> resourceAllocationList = statisticsFeignClient.getResourceAllocationsPersion(resourceAllocationQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleListPersion());
        List<ResourceAllocationDto> orderList = resourceAllocationList.getData();
        for(int i = 0; i<orderList.size(); i++){
            ResourceAllocationDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getUserName());
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
    public String resourceAllocationTableTeam(String groupId,HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 查询所有电销组
        List<OrganizationRespDTO> saleGroupList = getSaleGroupList();
        request.setAttribute("saleGroupList", saleGroupList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("statistics:teleStatement:resourceAllocation");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
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
    public String telemarketingCallTableTeam(Long orgId,HttpServletRequest request) {
        request.setAttribute("parentOrgId",orgId);
        return "reportforms/telemarketingCallTableTeam";
    }
    
    
    /**
     * 电销顾问通话时长表页面  个人
    * @return
     */
    @RequestMapping("/telemarketingCallTablePerson")
    public String telemarketingCallTablePerson(Long userId,HttpServletRequest request) {
        request.setAttribute("curUserId",userId);
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

    private List<Object> getHeadTitleListPersion() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销人员");
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

    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }


    /**
     * 获取电销组
     */
    private List<OrganizationRespDTO> getSaleGroupList() {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        // 查询下级电销组
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> data = queryOrgByParam.getData();
        return data;
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
