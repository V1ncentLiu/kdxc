package com.kuaidao.manageweb.controller.statistics.resourceEfficiency;


import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.resourceEfficiency.ResourceEfficiencyDto;
import com.kuaidao.stastics.dto.resourceEfficiency.ResourceEfficiencyQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
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
 * 资源接通有效率
 */
@Slf4j
@Controller
@RequestMapping("/statistics/resourceEfficiency")
public class ResourceEfficiencyController {

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    /**
     *资源接通有效率表
     * @return
     */
    @RequestMapping("/resourceEfficientTable")
    public String resourceConectEfficientTable(HttpServletRequest request) {
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        return "reportforms/resourceConnectEfficientTable";
    }

    /**
     * 获取资源有效率列表(资源有效)
     */
    @RequestMapping("/getResourceEfficientList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getResourceEfficientList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) throws Exception{
        PageBean<ResourceEfficiencyDto> pageData = mockData().getData();
        List<ResourceEfficiencyDto> totalData = mockCountData().getData();
        Map<String,Object> resMap = new HashMap<>();
        resMap.put("totalData", totalData);
        resMap.put("tableData", pageData);
        return new JSONResult<Map<String,Object>>().success(resMap);
    }

    /**
     * 获取资源有效率列表(首日有效)
     */
    @RequestMapping("/getFirstResourceEfficientList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getFirstResourceEfficientList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) throws Exception{
        PageBean<ResourceEfficiencyDto> pageData = mockData().getData();
        List<ResourceEfficiencyDto> totalData = mockCountData().getData();
        Map<String,Object> resMap = new HashMap<>();
        resMap.put("totalData", totalData);
        resMap.put("tableData", pageData);
        return new JSONResult<Map<String,Object>>().success(resMap);
    }


    /**
     * 获取合计数据
     */
    private List<ResourceEfficiencyDto> getResourceEfficiencyCount(List<ResourceEfficiencyDto> lists){
        List<ResourceEfficiencyDto> totleList = new ArrayList<>();
        ResourceEfficiencyDto totalResourceEfficiency = new ResourceEfficiencyDto();
        totalResourceEfficiency.setResourceMediumName("合计");
        totalResourceEfficiency.setProjectTypeName("合计");
        totalResourceEfficiency.setResourceCategoryName("合计");
        //下发资源量
        Integer issuedResources = lists.stream().mapToInt(ResourceEfficiencyDto::getIssuedResources).sum();
        //跟访资源量
        Integer followResources = lists.stream().mapToInt(ResourceEfficiencyDto::getFollowResources).sum();
        //首次接通资源量
        Integer firstResources = lists.stream().mapToInt(ResourceEfficiencyDto::getFirstResources).sum();
        //接通资源量
        Integer connectResources = lists.stream().mapToInt(ResourceEfficiencyDto::getConnectResources).sum();
        //未接通资源量
        Integer notConnectResources = lists.stream().mapToInt(ResourceEfficiencyDto::getNotConnectResources).sum();
        //接通有效资源量
        Integer connectEffectiveResources = lists.stream().mapToInt(ResourceEfficiencyDto::getConnectEffectiveResources).sum();
        //接通无效资源量
        Integer connectNotEffectiveResources = lists.stream().mapToInt(ResourceEfficiencyDto::getConnectNotEffectiveResources).sum();
        //未接通有效资源量
        Integer notConnectEffectiveResources = lists.stream().mapToInt(ResourceEfficiencyDto::getNotConnectEffectiveResources).sum();
        //未接通无效资源量
        Integer notConnectNotEffectiveResources = lists.stream().mapToInt(ResourceEfficiencyDto::getNotConnectNotEffectiveResources).sum();
        //跟访率
        BigDecimal followRate = lists.stream() .map(ResourceEfficiencyDto::getFollowRate).reduce(BigDecimal.ZERO,BigDecimal::add);
        //首次接通率
        BigDecimal firstRate = lists.stream() .map(ResourceEfficiencyDto::getFirstRate).reduce(BigDecimal.ZERO,BigDecimal::add);
        //资源接通率
        BigDecimal resourceConnectRate = lists.stream() .map(ResourceEfficiencyDto::getResourceConnectRate).reduce(BigDecimal.ZERO,BigDecimal::add);
        //资源有效率
        BigDecimal resourceEffectiveRate = lists.stream() .map(ResourceEfficiencyDto::getResourceEffectiveRate).reduce(BigDecimal.ZERO,BigDecimal::add);
        //接通有效率
        BigDecimal connectionRate = lists.stream() .map(ResourceEfficiencyDto::getConnectionRate).reduce(BigDecimal.ZERO,BigDecimal::add);
        totalResourceEfficiency.setIssuedResources(issuedResources);
        totalResourceEfficiency.setFollowResources(followResources);
        totalResourceEfficiency.setFirstResources(firstResources);
        totalResourceEfficiency.setConnectResources(connectResources);
        totalResourceEfficiency.setNotConnectResources(notConnectResources);
        totalResourceEfficiency.setConnectEffectiveResources(connectEffectiveResources);
        totalResourceEfficiency.setConnectNotEffectiveResources(connectNotEffectiveResources);
        totalResourceEfficiency.setNotConnectEffectiveResources(notConnectEffectiveResources);
        totalResourceEfficiency.setNotConnectNotEffectiveResources(notConnectNotEffectiveResources);
        totalResourceEfficiency.setFollowRate(followRate);
        totalResourceEfficiency.setFirstRate(firstRate);
        totalResourceEfficiency.setResourceConnectRate(resourceConnectRate);
        totalResourceEfficiency.setResourceEffectiveRate(resourceEffectiveRate);
        totalResourceEfficiency.setConnectionRate(connectionRate);
        totleList.add(totalResourceEfficiency);
        return totleList;
    }
    /**
     *
     *  导出资源接通有效率
     */
    @PostMapping("/exportResourceEfficiency")
    public void exportResourceEfficiency(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto,HttpServletResponse response) throws IOException {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitle());
        List<ResourceEfficiencyDto> orderList = mockCountData().getData();
        for(int i = 0; i<orderList.size(); i++){
            ResourceEfficiencyDto ra = orderList.get(i);
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
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = resourceEfficiencyQueryDto.getStartTime();
        Long endTime = resourceEfficiencyQueryDto.getEndTime();
        String name = "资源跟踪记录表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }


    private List<Object> getHeadTitle() {
        List<Object> headTitleList = new ArrayList<>();
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
        headTitleList.add("资源接通率");
        headTitleList.add("资源有效率");
        headTitleList.add("接通有效率");
        headTitleList.add("首日跟访资源量");
        headTitleList.add("首日接通资源量");
        headTitleList.add("首日未接通资源量");
        headTitleList.add("首日接通有效资源量");
        headTitleList.add("首日接通无效资源量");
        headTitleList.add("首日未接通有效资源量");
        headTitleList.add("首日未接通无效资源量");
        headTitleList.add("首日跟访率");
        headTitleList.add("首日资源接通率");
        headTitleList.add("首日资源有效率");
        headTitleList.add("首日接通有效率");
        return headTitleList;
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

    private void buildOrgIdList(@RequestBody ResourceEfficiencyDto resourceEfficiencyDto) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<OrganizationRespDTO> orgGroupByOrgId = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
        List<Long> orgIdList = orgGroupByOrgId.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
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
        return orgJr.getData();
    }


    /**
     * mock数据
     * @return
     */
    private JSONResult<PageBean<ResourceEfficiencyDto>> mockData() {
        List<ResourceEfficiencyDto> list = new ArrayList<>();
        ResourceEfficiencyDto resourceEfficiencyDto = new ResourceEfficiencyDto();
        resourceEfficiencyDto.setFollowResources(1091);
        resourceEfficiencyDto.setConnectEffectiveResources(1);
        resourceEfficiencyDto.setConnectionRate(new BigDecimal(12.22));
        resourceEfficiencyDto.setConnectNotEffectiveResources(1);
        resourceEfficiencyDto.setFirstRate(new BigDecimal(123.33));
        resourceEfficiencyDto.setFirstResources(1);
        resourceEfficiencyDto.setFollowRate(new BigDecimal(15.66));
        resourceEfficiencyDto.setIssuedResources(1);
        resourceEfficiencyDto.setProjectTypeName("项目名称");
        resourceEfficiencyDto.setResourceConnectRate(new BigDecimal(1.22));
        resourceEfficiencyDto.setResourceMediumName("媒介");
        list.add(resourceEfficiencyDto);
        PageBean<ResourceEfficiencyDto> pageBean = new PageBean<>();
        pageBean.setCurrentPage(1);
        pageBean.setData(list);
        pageBean.setPageSize(1);
        pageBean.setTotal(1);
        return  new JSONResult<PageBean<ResourceEfficiencyDto>>().success(pageBean);
    }

    private JSONResult<List<ResourceEfficiencyDto>> mockCountData() {
        List<ResourceEfficiencyDto> list = new ArrayList<>();
        ResourceEfficiencyDto resourceEfficiencyDto = new ResourceEfficiencyDto();
        resourceEfficiencyDto.setConnectEffectiveResources(1);
        resourceEfficiencyDto.setConnectionRate(new BigDecimal(12.22));
        resourceEfficiencyDto.setConnectNotEffectiveResources(1);
        resourceEfficiencyDto.setFirstRate(new BigDecimal(123.33));
        resourceEfficiencyDto.setFirstResources(1);
        resourceEfficiencyDto.setFollowRate(new BigDecimal(15.66));
        resourceEfficiencyDto.setIssuedResources(1);
        resourceEfficiencyDto.setProjectTypeName("合计");
        resourceEfficiencyDto.setResourceConnectRate(new BigDecimal(1.22));
        resourceEfficiencyDto.setResourceMediumName("合计");
        list.add(resourceEfficiencyDto);
        return new JSONResult<List<ResourceEfficiencyDto>>().success(list);
    }

}
