package com.kuaidao.manageweb.feign.organization;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.sys.dto.organization.OrganitionRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;

/**
 * 组织机构
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午5:06:53   
 * @version V1.0
 */
@FeignClient(name = "sys-service",path="/sys/organization/organization",fallback = OrganizationFeignClient.HystrixClientFallback.class)
public interface OrganizationFeignClient {

    @PostMapping("/save")
    public JSONResult save(@RequestBody OrganizationAddAndUpdateDTO orgDTO);
    
    @PostMapping("/update")
    public JSONResult update(@RequestBody OrganizationAddAndUpdateDTO orgDTO);   
    
    @RequestMapping(method = RequestMethod.POST, value = "/delete")
    public JSONResult delete(@RequestBody IdEntity idEntity);
    
    @PostMapping("/queryOrgDataByParam")
    public JSONResult<PageBean<OrganitionRespDTO>> queryOrgDataByParam(@RequestBody OrganizationQueryDTO queryDTO);
    
    @PostMapping("/queryOrgByParam")
    public JSONResult<List<OrganitionRespDTO>> queryOrgByParam( @RequestBody OrganizationQueryDTO queryDTO);
    
    /**
     * 查询组织机构数
     * @param queryDTO
     * @return
     */
    @PostMapping("/query")
    JSONResult<TreeData> query();
    
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
        public JSONResult delete(IdEntity idEntity) {
            return fallBackError("删除组织机构");
        }


        @Override
        public JSONResult<PageBean<OrganitionRespDTO>> queryOrgDataByParam(OrganizationQueryDTO queryDTO) {
            return fallBackError("查询组织机构数据，分页");
        }


        @Override
        public JSONResult<List<OrganitionRespDTO>> queryOrgByParam(OrganizationQueryDTO queryDTO) {
            return fallBackError("查询组织机构");
        }


        @Override
        public JSONResult<TreeData> query() {
            return fallBackError("查询组织机构树");
        }
    }

}
