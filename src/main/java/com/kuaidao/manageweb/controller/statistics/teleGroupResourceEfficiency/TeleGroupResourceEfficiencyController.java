package com.kuaidao.manageweb.controller.statistics.teleGroupResourceEfficiency;


import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.callrecord.ClueConnectValidRateFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationQueryDto;
import com.kuaidao.stastics.dto.teleGroupResourceEfficiency.TeleGroupResourceEfficiencyAllDto;
import com.kuaidao.stastics.dto.teleGroupResourceEfficiency.TeleGroupResourceEfficiencyDto;
import com.kuaidao.stastics.dto.teleGroupResourceEfficiency.TeleGroupResourceEfficiencyQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserDataAuthReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 电销组资源接通有效率
 */
@Slf4j
@Controller
@RequestMapping("/statistics/teleGroupResourceEfficiency")
public class TeleGroupResourceEfficiencyController {
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    
    @Autowired
    ClueConnectValidRateFeignClient clueConnectValidRateFeignClient;

    /**
     *电销组资源接通有效率表
     * @return
     */
    @RequestMapping("/resourceConectTelEfficientTable")
    public String resourceConectTelEfficientTable(HttpServletRequest request) {
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        Map<String, Object> orgList = getOrgList();
        String curOrgId = (String) orgList.get("curOrgId");
        List<OrganizationRespDTO> teleGroupList = (List<OrganizationRespDTO>) orgList.get("saleGroupList");
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        return "reportforms/resourceConectTelEfficientTable";
    }

    /**
     * 获取电销组资源有效率列表(非首日资源有效)
     */
    @RequestMapping("/getResourceConectTelEfficientList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getResourceConectTelEfficientList(@RequestBody TeleGroupResourceEfficiencyQueryDto teleGroupResourceEfficiencyQueryDto) throws Exception{
   /*     PageBean<TeleGroupResourceEfficiencyDto> pageData = mockData().getData();
        List<TeleGroupResourceEfficiencyDto> totalData = mockCountData().getData();
        Map<String,Object> resMap = new HashMap<>();
        resMap.put("totalData", totalData);
        resMap.put("tableData", pageData);
        return new JSONResult<Map<String,Object>>().success(resMap);*/
        initAuth(teleGroupResourceEfficiencyQueryDto);
        JSONResult<Map<String, Object>> nonFirstClueValidListJr = clueConnectValidRateFeignClient.nonFirstClueValidList(teleGroupResourceEfficiencyQueryDto);
        return nonFirstClueValidListJr;
    }

    /**
     * 获取电销组资源有效率列表(首日资源有效)
     */
    @RequestMapping("/getFirstResourceConectTelEfficientList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getFirstResourceConectTelEfficientList(@RequestBody TeleGroupResourceEfficiencyQueryDto teleGroupResourceEfficiencyQueryDto) throws Exception{
        /*PageBean<TeleGroupResourceEfficiencyDto> pageData = mockData().getData();
        List<TeleGroupResourceEfficiencyDto> totalData = mockCountData().getData();
        Map<String,Object> resMap = new HashMap<>();
        resMap.put("totalData", totalData);
        resMap.put("tableData", pageData);
        return new JSONResult<Map<String,Object>>().success(resMap);*/
        initAuth(teleGroupResourceEfficiencyQueryDto);
        JSONResult<Map<String, Object>> firstClueValidListJr = clueConnectValidRateFeignClient.firstClueValidList(teleGroupResourceEfficiencyQueryDto);
        return firstClueValidListJr;
    }


    /**
     *
     *  导出电销组资源接通有效率
     */
    @PostMapping("/exportResourceTelEfficiency")
    public void exportResourceTelEfficiency(@RequestBody TeleGroupResourceEfficiencyQueryDto queryDto, HttpServletResponse response) throws IOException {
    /*    List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitle());
        List<TeleGroupResourceEfficiencyDto> orderList = mockCountData().getData();
        for(int i = 0; i<orderList.size(); i++){
            TeleGroupResourceEfficiencyDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getResourceCategoryName());
            curList.add(ra.getResourceMediumName());
            curList.add(ra.getProjectTypeName());
            curList.add(ra.getIssuedResources());
            curList.add(ra.getFollowResources());
            curList.add(ra.getFirstResources());
            curList.add(ra.getConnectResources());
            curList.add(ra.getNotConnectResources());
            dataList.add(curList);
        }*/
        initAuth(queryDto);
        
        JSONResult<List<TeleGroupResourceEfficiencyAllDto>> dataJR  = clueConnectValidRateFeignClient.getAllClueValidList(queryDto);
        List<TeleGroupResourceEfficiencyAllDto> groupValidList = dataJR.getData();
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitle());
        String curOrgName = "";
        String curCategoryType = "";
        String curMedia = "";
        String curProjectName = "";
        for (int i=0;i<groupValidList.size();i++) {
            TeleGroupResourceEfficiencyAllDto curDto = groupValidList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            //设置 非首日属性
            setNonFirstClueValidField(curList,curDto,curOrgName,curCategoryType,curMedia,curProjectName);
            //设置 首日属性
            setFirstClueValidField(curList,curDto);
            /*curOrgName = curDto.getOrgName();
            curCategoryType = curDto.getResourceCategoryName();
            curMedia = curDto.getResourceMediumName();
            curProjectName = curDto.getProjectTypeName();*/
            dataList.add(curList);
        }
       
        
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = queryDto.getStartTime();
        Long endTime = queryDto.getEndTime();
        String name = "电销组资源接通有效率表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 导出 设置 首日 属性
    * @param curList
    * @param curDto
     */
    private void setFirstClueValidField(List<Object> curList,TeleGroupResourceEfficiencyAllDto curDto) {
        curList.add(CommUtil.nullIntegerToZero(curDto.getFirstFollowResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getFirstConnectResources()));
        //首日未接通资源量 
        curList.add(CommUtil.nullIntegerToZero(curDto.getFirstNotConnectResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getFirstConnectEffectiveResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getFirstConnectNotEffectiveResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getFirstNotConnectEffectiveResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getFirstNotConnectNotEffectiveResources()));
        curList.add(formatPercent(curDto.getFirstFollowRate()));
        curList.add(formatPercent(curDto.getFirstAssignedConnectedRate()));
        curList.add(formatPercent(curDto.getFirstAssignedValidRate()));
        curList.add(formatPercent(curDto.getFirstResourceConnectRate()));
        curList.add(formatPercent(curDto.getFirstResourceEffectiveRate()));
        curList.add(formatPercent(curDto.getFirstConnectionRate()));
    }

    /**
     * 导出 设置非首日属性
    * @param curList
    * @param curDto
     */
    private void setNonFirstClueValidField(List<Object> curList,TeleGroupResourceEfficiencyAllDto curDto,String curOrgName
            , String curCategoryType,String curMedia, String curProjectName) {
        String orgName = curDto.getOrgName();
     /*   if(CommonUtil.isNotBlank(curOrgName) && curOrgName.equals(orgName)) {
            orgName = "";
        }*/
        curList.add(orgName);
        
        String resourceCategoryName = curDto.getResourceCategoryName();
       /* if(CommonUtil.isNotBlank(curCategoryType) && curCategoryType.equals(resourceCategoryName)) {
            resourceCategoryName="";
        }*/
        curList.add(resourceCategoryName);
        
        String resourceMediumName = curDto.getResourceMediumName();
        /*if(CommonUtil.isNotBlank(curMedia) && curMedia.equals(resourceMediumName)) {
            resourceMediumName="";
        }*/
        curList.add(resourceMediumName);
        String projectTypeName = curDto.getProjectTypeName();
      /*  if(CommonUtil.isNotBlank(curProjectName) && curProjectName.equals(projectTypeName)) {
            projectTypeName ="";
        }*/
        curList.add(projectTypeName);
        curList.add(CommUtil.nullIntegerToZero(curDto.getIssuedResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getFollowResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getFirstResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getConnectResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getNotConnectResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getConnectEffectiveResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getConnectNotEffectiveResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getNotConnectEffectiveResources()));
        curList.add(CommUtil.nullIntegerToZero(curDto.getNotConnectNotEffectiveResources()));
        curList.add(formatPercent(curDto.getFollowRate()));
        curList.add(formatPercent(curDto.getFirstRate()));
        curList.add(formatPercent(curDto.getAssignedConnectedRate()));
        curList.add(formatPercent(curDto.getAssignedValidRate()));
        curList.add(formatPercent(curDto.getResourceConnectRate()));
        curList.add(formatPercent(curDto.getResourceEffectiveRate()));
        curList.add(formatPercent(curDto.getConnectionRate()));
    }

    /**
     * 格式化 
    * @param callPercent
    * @return
     */
    private String formatPercent(BigDecimal callPercent) {
        if(callPercent!=null) {
            callPercent = callPercent.multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_DOWN);
        }else {
            callPercent = BigDecimal.ZERO;
        }
       return callPercent+"%"; 
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

    private void buildOrgIdList(@RequestBody FirstResourceAllocationQueryDto firstResourceAllocationQueryDto) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<OrganizationRespDTO> orgGroupByOrgId = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
        List<Long> orgIdList = orgGroupByOrgId.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
        firstResourceAllocationQueryDto.setOrgIdList(orgIdList);
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
        if(orgId!=null) {
            busGroupReqDTO.setParentId(orgId);
        }
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationRespDTO>> orgJr = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        return orgJr.getData();
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
            teleGroupList =  getOrgGroupByOrgId(null,OrgTypeConstant.DXZ);
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
            log.error("first resourceAllocation getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }


    private List<Object> getHeadTitle() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("资源类别");
        headTitleList.add("媒介");
        headTitleList.add("资源项目");
        headTitleList.add("下发资源量");
        headTitleList.add("跟访资源量");
        headTitleList.add("首次接通资源量");
        headTitleList.add("接通资源量");
        headTitleList.add("未接通资源量");
        headTitleList.add("接通有效资源量");
        headTitleList.add("接通无效资源量");
        headTitleList.add("未接通有效资源量");
        headTitleList.add("未接通无效资源量");
        headTitleList.add("跟访率");
        headTitleList.add("首次接通率");
        headTitleList.add("下发接通率");
        headTitleList.add("下发有效率");
        headTitleList.add("跟进接通率");
        headTitleList.add("跟进有效率");
        headTitleList.add("接通有效率");
        headTitleList.add("首日跟访资源量");
        headTitleList.add("首日接通资源量");
        headTitleList.add("首日未接通资源量");
        headTitleList.add("首日接通有效资源量");
        headTitleList.add("首日接通无效资源量");
        headTitleList.add("首日未接通有效资源量");
        headTitleList.add("首日未接通无效资源量");
        headTitleList.add("首日跟访率");
        headTitleList.add("首日下发接通率");
        headTitleList.add("首日下发有效率");
        headTitleList.add("首日跟进接通率");
        headTitleList.add("首日跟进有效率");
        headTitleList.add("首日接通有效率");
        return headTitleList;
    }

    /**
     * mock数据
     * @return
     */
    private JSONResult<PageBean<TeleGroupResourceEfficiencyDto>> mockData() {
        List<TeleGroupResourceEfficiencyDto> list = new ArrayList<>();
        for(int i=0;i<3;i++){
            TeleGroupResourceEfficiencyDto teleGroupResourceEfficiencyDto = new TeleGroupResourceEfficiencyDto();
            teleGroupResourceEfficiencyDto.setFollowResources(1091);
            teleGroupResourceEfficiencyDto.setConnectEffectiveResources(1);
            teleGroupResourceEfficiencyDto.setConnectionRate(new BigDecimal(12.22));
            teleGroupResourceEfficiencyDto.setConnectNotEffectiveResources(1);
            teleGroupResourceEfficiencyDto.setFirstRate(new BigDecimal(123.33));
            teleGroupResourceEfficiencyDto.setFirstResources(1);
            teleGroupResourceEfficiencyDto.setFollowRate(new BigDecimal(15.66));
            teleGroupResourceEfficiencyDto.setIssuedResources(1);
            teleGroupResourceEfficiencyDto.setProjectTypeName("项目名称");
            teleGroupResourceEfficiencyDto.setResourceConnectRate(new BigDecimal(1.22));
            teleGroupResourceEfficiencyDto.setResourceMediumName("媒介");
            teleGroupResourceEfficiencyDto.setOrgId(Long.parseLong(String.valueOf(i)));
            teleGroupResourceEfficiencyDto.setOrgName("电销一组");
            list.add(teleGroupResourceEfficiencyDto);
        }
        PageBean<TeleGroupResourceEfficiencyDto> pageBean = new PageBean<>();
        pageBean.setCurrentPage(1);
        pageBean.setData(list);
        pageBean.setPageSize(3);
        pageBean.setTotal(3);
        return  new JSONResult<PageBean<TeleGroupResourceEfficiencyDto>>().success(pageBean);
    }

    private JSONResult<List<TeleGroupResourceEfficiencyDto>> mockCountData() {
        List<TeleGroupResourceEfficiencyDto> list = new ArrayList<>();
        for(int i=0;i<3;i++){
            TeleGroupResourceEfficiencyDto teleGroupResourceEfficiencyDto = new TeleGroupResourceEfficiencyDto();
            teleGroupResourceEfficiencyDto.setConnectEffectiveResources(1);
            teleGroupResourceEfficiencyDto.setConnectionRate(new BigDecimal(12.22));
            teleGroupResourceEfficiencyDto.setConnectNotEffectiveResources(1);
            teleGroupResourceEfficiencyDto.setFirstRate(new BigDecimal(123.33));
            teleGroupResourceEfficiencyDto.setFirstResources(1);
            teleGroupResourceEfficiencyDto.setFollowRate(new BigDecimal(15.66));
            teleGroupResourceEfficiencyDto.setIssuedResources(1);
            teleGroupResourceEfficiencyDto.setProjectTypeName("合计");
            teleGroupResourceEfficiencyDto.setResourceConnectRate(new BigDecimal(1.22));
            teleGroupResourceEfficiencyDto.setResourceMediumName("合计");
            teleGroupResourceEfficiencyDto.setOrgId(Long.parseLong(String.valueOf(i)));
            teleGroupResourceEfficiencyDto.setOrgName("电销一组");
            list.add(teleGroupResourceEfficiencyDto);
        }

        return new JSONResult<List<TeleGroupResourceEfficiencyDto>>().success(list);
    }
    
    public void initAuth(TeleGroupResourceEfficiencyQueryDto resourceEfficiencyQueryDto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<UserDataAuthReq> userDataAuthList = curLoginUser.getUserDataAuthList();
        Map<Integer,String> map = new HashMap<>();
        if(null != userDataAuthList && userDataAuthList.size() > 0){
            for(UserDataAuthReq udar : userDataAuthList){
                map.put(udar.getBusinessLine(),udar.getDicValue());
            }
            resourceEfficiencyQueryDto.setBusinessLineMap(map);
        }
    }

}
