package com.kuaidao.manageweb.feign.organization;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.OrgUserReqDTO;
import com.kuaidao.sys.dto.user.UserAndRoleRespDTO;

/**
 * 组织机构
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午5:06:53   
 * @version V1.0
 */
@FeignClient(name = "sys-service-1",path="/sys/organization/organization",fallback = OrganizationFeignClient.HystrixClientFallback.class)
public interface OrganizationFeignClient {

    @PostMapping("/save")
    public JSONResult save(@RequestBody OrganizationAddAndUpdateDTO orgDTO);
    
    @PostMapping("/update")
    public JSONResult update(@RequestBody OrganizationAddAndUpdateDTO orgDTO);   
    
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListReq idListReq);
    
    /***
     *  组织机构 数据查询  分页 
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryOrgDataByParam")
    public JSONResult<PageBean<OrganizationRespDTO>> queryOrgDataByParam(@RequestBody OrganizationQueryDTO queryDTO);
    
    /**
     * 查询组织机构
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryOrgByParam")
    public JSONResult<List<OrganizationRespDTO>> queryOrgByParam( @RequestBody OrganizationQueryDTO queryDTO);
    
    
    /**
     * 查询组织机构 树
     * @param queryDTO
     * @return
     */
    @PostMapping("/query")
    JSONResult<List<TreeData>> query();
    
    /**
     * 查询组织机构下是否由下级
     * @param idListReq
     * @return
     */
    @PostMapping("/queryOrgByParentId")
    JSONResult<Boolean> queryOrgByParentId(IdListReq idListReq);
    
    /**
     * 根据组织机构ID ,查询组织机构信息
     * @param idEntity
     * @return
     */
    @PostMapping("/queryOrgById")
    JSONResult<OrganizationDTO> queryOrgById(@RequestBody IdEntity idEntity);
    
    
    /**
     * 查询组织机构下的所有用户
     * @param reqDTO
     * @return
     */
    @PostMapping("/listOrgUserInfo")
    JSONResult<PageBean<UserAndRoleRespDTO>> listOrgUserInfo(@RequestBody OrgUserReqDTO reqDTO);

    
    @Component
    static class HystrixClientFallback implements  OrganizationFeignClient{
        
        private static Logger logger = LoggerFactory.getLogger(OrganizationFeignClient.class);

        
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult save(OrganizationAddAndUpdateDTO orgDTO) {
            return fallBackError("保存组织机构");
        }


        @Override
        public JSONResult update(OrganizationAddAndUpdateDTO orgDTO) {
            return fallBackError("更新组织机构");
        }


        @Override
        public JSONResult delete(IdListReq idListReq) {
            return fallBackError("删除组织机构");
        }


        @Override
        public JSONResult<PageBean<OrganizationRespDTO>> queryOrgDataByParam(OrganizationQueryDTO queryDTO) {
            return fallBackError("查询组织机构数据，分页");
        }


        @Override
        public JSONResult<List<OrganizationRespDTO>> queryOrgByParam(OrganizationQueryDTO queryDTO) {
            return fallBackError("查询组织机构");
        }


        @Override
        public JSONResult<List<TreeData>> query() {
            return fallBackError("查询组织机构树");
        }


        @Override
        public JSONResult<Boolean> queryOrgByParentId(IdListReq idListReq) {
            return fallBackError("查询组织机构是否有下级");
        }


        @Override
        public JSONResult<OrganizationDTO> queryOrgById(IdEntity idEntity) {
            return fallBackError("根据ID查询组织机构信息");
        }


        @Override
        public JSONResult<PageBean<UserAndRoleRespDTO>> listOrgUserInfo(OrgUserReqDTO reqDTO) {
            return fallBackError("查询组织机构下的用户信息");
        }
    }

    
   

  

}
