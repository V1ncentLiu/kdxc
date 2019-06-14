package com.kuaidao.manageweb.controller.statistics.teleGroupResourceEfficiency;


import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.firstResourceAllocation.FirstResourceAllocationQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
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
        return "reportforms/resourceConectTelEfficientTable";
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
            log.error("first resourceAllocation getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }



}
