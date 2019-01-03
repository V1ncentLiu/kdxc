package com.kuaidao.manageweb.feign.customfield;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.customfield.CustomFieldAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuRespDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldRespDTO;

/**
 * 自定义字段
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午5:06:37   
 * @version V1.0
 */
@FeignClient(name = "sys-service",path="/sys/customfield/customField",fallback = CustomFieldFeignClient.HystrixClientFallback.class)
public interface CustomFieldFeignClient {
    /**
     * 保存自定义字段  菜单
     * @param menuDTO
     * @return
     */
    @PostMapping("/saveMenu")
    public JSONResult saveMenu(@RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO);
    
    /**
     * 更新自定义字段 菜单
     * @param menuDTO
     * @return
     */
    @PostMapping("/updateMenu")
    public JSONResult updateMenu(@RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO);
    
    /**
     * 删除自定义字段 菜单
     * @param idEntity
     * @return
     */
    @PostMapping("/deleteMenu")
    public JSONResult deleteMenu(@RequestBody IdEntity idEntity);
    
    /**
     * 查询自定义字段菜单   分页 
     * @param queryDTO
     * @return
     */
    @PostMapping("/listMenuPage")
    public JSONResult<PageBean<CustomFieldMenuRespDTO>> listMenuPage(@RequestBody CustomFieldMenuQueryDTO queryDTO);
    
    /**
     * 根据ID 查询自定义字段 菜单 
     * @param idEntity
     * @return
     */
    @PostMapping("/queryFieldMenuById")
    public JSONResult<CustomFieldMenuRespDTO> queryFieldMenuById(@RequestBody IdEntity idEntity);
    
    /**
     * 保存自定义字段
     * @param customDTO
     * @return
     */
    @PostMapping("/save")
    public JSONResult save(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO);
    
    /**
     * 更新自定义字段
     * @param customDTO
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO);
    
    /**
     * 删除自定义字段 
     * @param idEntity
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdEntity idEntity);
    
    /**
     * 查询菜单下自定义字段
     * @param queryDTO
     * @return
     */
    @PostMapping("/query")
    public JSONResult<CustomFieldRespDTO> query(@RequestBody CustomFieldQueryDTO queryDTO);
    
    /**
     * 分页查询自定义字段列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/listCustomFieldPage")
    public JSONResult<PageBean<CustomFieldRespDTO>> listCustomFieldPage(@RequestBody CustomFieldQueryDTO queryDTO);
    
    
    
    @Component
    static class HystrixClientFallback implements  CustomFieldFeignClient{
        
        private static Logger logger = LoggerFactory.getLogger(CustomFieldFeignClient.class);

        
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult saveMenu(CustomFieldMenuAddAndUpdateDTO menuDTO) {
            return fallBackError("自定义字段-保存菜单组");
        }


        @Override
        public JSONResult updateMenu(CustomFieldMenuAddAndUpdateDTO menuDTO) {
            return fallBackError("自定义字段-更新菜单组");
        }


        @Override
        public JSONResult deleteMenu(IdEntity idEntity) {
            return fallBackError("自定义字段-删除菜单组");
        }


        @Override
        public JSONResult<PageBean<CustomFieldMenuRespDTO>> listMenuPage(
                CustomFieldMenuQueryDTO queryDTO) {
            return fallBackError("自定义字段-分页查询菜单组");
        }


        @Override
        public JSONResult<CustomFieldMenuRespDTO> queryFieldMenuById(IdEntity idEntity) {
            return fallBackError("自定义字段-根据ID菜单组");
        }


        @Override
        public JSONResult save(CustomFieldAddAndUpdateDTO customDTO) {
            return fallBackError("保存自定义字段");
        }


        @Override
        public JSONResult update(CustomFieldAddAndUpdateDTO customDTO) {
            return fallBackError("更新自定义字段");
        }


        @Override
        public JSONResult delete(IdEntity idEntity) {
            return fallBackError("删除自定义字段");
        }


        @Override
        public JSONResult<CustomFieldRespDTO> query(CustomFieldQueryDTO queryDTO) {
            return fallBackError("查询自定义字段");
        }


        @Override
        public JSONResult<PageBean<CustomFieldRespDTO>> listCustomFieldPage(
                CustomFieldQueryDTO queryDTO) {
            return fallBackError("分页查询自定义字段");
        }


    }

}
