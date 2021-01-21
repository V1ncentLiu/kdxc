package com.kuaidao.manageweb.feign.organization;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrganitionWapper {

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    /**
     * 获取指定组织下的电销组
     * @param orgId
     * @return
     */
    public List<OrganizationDTO> findDxzListByParentId(Long orgId){
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(orgId);
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        // 查询下级电销组(查询使用)
        JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
        List<OrganizationDTO> data = listDescenDantByParentId.getData();
        return data;
    }

    /**
     * 获取全部电销组
     */
    public List<OrganizationRespDTO> findAllDXZ(){
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam = organizationFeignClient.queryOrgByParam(organizationQueryDTO);
        return queryOrgByParam.data();
    }

    public OrganizationDTO findOrgById(Long orgId){
        IdEntity idEntity = new IdEntity();
        idEntity.setId(String.valueOf(orgId));
        JSONResult<OrganizationDTO> result = organizationFeignClient.queryOrgById(idEntity);
        return result.data();
    }


}
