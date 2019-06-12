package com.kuaidao.manageweb.controller.statistics.resourceEfficiency;


import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationQueryDto;
import com.kuaidao.stastics.dto.resourceEfficiency.ResourceEfficiencyDto;
import com.kuaidao.stastics.dto.resourceEfficiency.ResourceEfficiencyQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源接通有效率
 */
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
    public JSONResult<PageBean<ResourceEfficiencyDto>> getResourceEfficientList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) throws Exception{
        return mockData();
    }

    /**
     * 获取资源有效率列表(首日有效)
     */
    @RequestMapping("/getFirstResourceEfficientList")
    @ResponseBody
    public JSONResult<PageBean<ResourceEfficiencyDto>> getFirstResourceEfficientList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) throws Exception{
        return mockData();
    }


    /**
     * 获取合计数据 （资源有效）
     */
    @RequestMapping("/getResourceEfficiencyCount")
    @ResponseBody
    public JSONResult<List<ResourceEfficiencyDto>> getResourceEfficiencyCount(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto){
        return mockCountData();
    }
    /**
     * 获取合计数据 （首日资源有效）
     */
    @RequestMapping("/getFirstResourceEfficiencyCount")
    @ResponseBody
    public JSONResult<List<ResourceEfficiencyDto>> getFirstResourceEfficiencyCount(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto){
        return mockCountData();
    }

    /**
     *
     *  导出资源接通有效率
     */
    @PostMapping("/exportResourceEfficiency")
    public void exportResourceEfficiency(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) throws IOException {

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
        resourceEfficiencyDto.setProjectTypeName("项目名称");
        resourceEfficiencyDto.setResourceConnectRate(new BigDecimal(1.22));
        resourceEfficiencyDto.setResourceMediumName("媒介");
        list.add(resourceEfficiencyDto);
        return new JSONResult<List<ResourceEfficiencyDto>>().success(list);
    }

}
