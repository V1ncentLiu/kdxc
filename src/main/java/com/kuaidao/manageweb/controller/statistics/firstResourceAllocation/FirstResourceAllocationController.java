package com.kuaidao.manageweb.controller.statistics.firstResourceAllocation;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.FirstResourceAllocation.FirstResourceAllocationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationDto;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationQueryDto;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceAllocationDto;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceAllocationQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics/firstResourceAllocation")
public class FirstResourceAllocationController {

    private static Logger logger = LoggerFactory.getLogger(FirstResourceAllocationController.class);

    @Autowired
    private FirstResourceAllocationFeignClient firstResourceAllocationFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /**
     * 组页面查询
     */
    @RequestMapping("/getFirstResourceAllocationPage")
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPage(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto){
        return firstResourceAllocationFeignClient.getFirstResourceAllocationPage(firstResourceAllocationQueryDto);
    }

    /**
     * 组页面导出
     */
    @PostMapping("/exportFirstResourceAllocationPage")
    public void exportFirstResourceAllocationPage(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
            HttpServletResponse response) throws IOException {

        Long orgId = firstResourceAllocationQueryDto.getOrgId();
        buildOrgIdList(firstResourceAllocationQueryDto, orgId);
        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationList =
                firstResourceAllocationFeignClient.getFirstResourceAllocationList(firstResourceAllocationQueryDto);
        List<FirstResourceAllocationDto> orderList = firstResourceAllocationList.getData();
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        //获取合计
        FirstResourceAllocationDto countTotal = getCountTotal(orderList);
        //增加合计列
        addTotalTexportData(countTotal,dataList);
        //加表头
        dataList.add(getHeadTitleGroup());
        for(int i = 0; i<orderList.size(); i++){
            FirstResourceAllocationDto ra = orderList.get(i);
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
        Long startTime = firstResourceAllocationQueryDto.getStartTime();
        Long endTime = firstResourceAllocationQueryDto.getEndTime();
        String name = "首次分配记录表" +startTime+"-"+endTime + ".xlsx";
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
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto){
        return firstResourceAllocationFeignClient.getFirstResourceAllocationPagePersion(firstResourceAllocationQueryDto);
    }

    /**
     * 个人页面导出
     */
    @PostMapping("/exportFirstResourceAllocationPagePersion")
    public void exportFirstResourceAllocationPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
                                                          HttpServletResponse response) throws IOException {
        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationsPersion =
                firstResourceAllocationFeignClient.getFirstResourceAllocationsPersion(firstResourceAllocationQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleListPersion());
        List<FirstResourceAllocationDto> orderList = firstResourceAllocationsPersion.getData();
        for(int i = 0; i<orderList.size(); i++){
            FirstResourceAllocationDto ra = orderList.get(i);
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
            curList.add(ra.getOther());
            curList.add(ra.getNetizensMissed());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = firstResourceAllocationQueryDto.getStartTime();
        Long endTime = firstResourceAllocationQueryDto.getEndTime();
        String name = "首次分配记录表" +startTime+"-"+endTime + ".xlsx";
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
    public JSONResult<PageBean<FirstResourceAllocationDto>> getFirstResourceAllocationDayPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto){
        return firstResourceAllocationFeignClient.getFirstResourceAllocationDayPagePersion(firstResourceAllocationQueryDto);
    }

    /**
     * 个人按天导出
     */
    @PostMapping("/exportFirstResourceAllocationDayPagePersion")
    public void exportFirstResourceAllocationDayPagePersion(
            @RequestBody(required=false) FirstResourceAllocationQueryDto firstResourceAllocationQueryDto,
                                                            HttpServletResponse response) throws IOException {
        JSONResult<List<FirstResourceAllocationDto>> firstResourceAllocationsDayPersion =
                firstResourceAllocationFeignClient.getFirstResourceAllocationsDayPersion(firstResourceAllocationQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleListDayPersion());
        List<FirstResourceAllocationDto> orderList = firstResourceAllocationsDayPersion.getData();
        for(int i = 0; i<orderList.size(); i++){
            FirstResourceAllocationDto ra = orderList.get(i);
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
            curList.add(ra.getOther());
            curList.add(ra.getNetizensMissed());
            curList.add(ra.getOther2());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = firstResourceAllocationQueryDto.getStartTime();
        Long endTime = firstResourceAllocationQueryDto.getEndTime();
        String name = "首次分配记录表" +startTime+"-"+endTime + ".xlsx";
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
     * @return
     */
    @RequestMapping("/firstRATable")
    public String firstRATable(HttpServletRequest request) {
        Map<String, Object> orgList = getOrgList();
        String curOrgId = (String) orgList.get("curOrgId");
        List<OrganizationRespDTO> teleGroupList = (List<OrganizationRespDTO>) orgList.get("teleGroupList");
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        return "reportforms/firstRATable";
    }

    /**
     * 合计
     * @return
     */
    @RequestMapping("/firstRATableSum")
    public String firstRATableSum(HttpServletRequest request) {
        return "reportforms/firstRATableSum";
    }
    /**
     * 组
     * @return
     */
    @RequestMapping("/firstRATableTeam")
    public String firstRATableTeam(HttpServletRequest request) {
        return "reportforms/firstRATableTeam";
    }

    /**
     * 个人
     * @return
     */
    @RequestMapping("/firstRATablePerson")
    public String firstRATablePerson(HttpServletRequest request) {
        return "reportforms/firstRATablePerson";
    }


    private List<Object> getHeadTitleGroup() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("首次分配资源数");
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
        headTitleList.add("电销组");
        headTitleList.add("电销");
        headTitleList.add("首次分配资源数");
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

    private List<Object> getHeadTitleListDayPersion() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("日期");
        headTitleList.add("电销");
        headTitleList.add("首次分配资源数");
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

    /**
     * 查询合计
     */
    private FirstResourceAllocationDto getCountTotal(List<FirstResourceAllocationDto> list){
        FirstResourceAllocationDto firstResourceAllocationDto = new FirstResourceAllocationDto();
        //首次分配资源数
        Long assignClueCount = list.stream().mapToLong(FirstResourceAllocationDto::getAssignClueCount).sum();
        //联展
        Long jointExhibition = list.stream().mapToLong(FirstResourceAllocationDto::getJointExhibition).sum();
        //竞价
        Long priceCompetition = list.stream().mapToLong(FirstResourceAllocationDto::getPriceCompetition).sum();
        //优化
        Long optimization = list.stream().mapToLong(FirstResourceAllocationDto::getOptimization).sum();
        //信息流
        Long informationFlow = list.stream().mapToLong(FirstResourceAllocationDto::getInformationFlow).sum();
        //官网
        Long officialWebsite = list.stream().mapToLong(FirstResourceAllocationDto::getOfficialWebsite).sum();
        //行业
        Long industry = list.stream().mapToLong(FirstResourceAllocationDto::getIndustry).sum();
        //其他
        Long other = list.stream().mapToLong(FirstResourceAllocationDto::getOther).sum();
        //网民未接
        Long netizensMissed = list.stream().mapToLong(FirstResourceAllocationDto::getNetizensMissed).sum();
        firstResourceAllocationDto.setAssignClueCount(assignClueCount);
        firstResourceAllocationDto.setJointExhibition(jointExhibition);
        firstResourceAllocationDto.setPriceCompetition(priceCompetition);
        firstResourceAllocationDto.setOptimization(optimization);
        firstResourceAllocationDto.setInformationFlow(informationFlow);
        firstResourceAllocationDto.setOfficialWebsite(officialWebsite);
        firstResourceAllocationDto.setIndustry(industry);
        firstResourceAllocationDto.setOther(other);
        firstResourceAllocationDto.setNetizensMissed(netizensMissed);
        return firstResourceAllocationDto;
    }

    private Map<String,Object> getOrgList(){
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            //电销总监查他自己的组
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("curOrgId",curOrgId);
        resultMap.put("saleGroupList",teleGroupList);
        return resultMap;
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
            logger.error("first resourceAllocation getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
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
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationRespDTO>> orgJr = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            return null;
        }
        return orgJr.getData();
    }

    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }


    private void buildOrgIdList(@RequestBody FirstResourceAllocationQueryDto firstResourceAllocationQueryDto, Long orgId) {
        if(null == orgId){
            logger.info("查询默认组");
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            List<OrganizationRespDTO> orgGroupByOrgId = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
            List<Long> orgIdList = orgGroupByOrgId.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
            firstResourceAllocationQueryDto.setOrgIdList(orgIdList);
        }
    }

    private void addTotalTexportData(FirstResourceAllocationDto resTotal, List<List<Object>> dataList) {
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
        totalList.add(resTotal.getOther());
        totalList.add(resTotal.getNetizensMissed());
        dataList.add(totalList);
    }


}
