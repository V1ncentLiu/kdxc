package com.kuaidao.manageweb.feign.customfield;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.customfield.CustomFieldAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuRespDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldRespDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.customfield.UserFieldReq;

/**
 * 自定义字段
 * 
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午5:06:37
 * @version V1.0
 */
@FeignClient(name = "sys-service-1", path = "/sys/customfield/customField",
        fallback = CustomFieldFeignClient.HystrixClientFallback.class)
public interface CustomFieldFeignClient {
    /**
     * 保存自定义字段 菜单
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/saveMenu")
    public JSONResult saveMenu(@RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO);

    /**
     * 更新自定义字段 菜单
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/updateMenu")
    public JSONResult updateMenu(@RequestBody CustomFieldMenuAddAndUpdateDTO menuDTO);

    /**
     * 删除自定义字段 菜单
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/deleteMenu")
    public JSONResult deleteMenu(@RequestBody IdListReq idListReq);

    /**
     * 查询自定义字段菜单 分页
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/listMenuPage")
    public JSONResult<PageBean<CustomFieldMenuRespDTO>> listMenuPage(
            @RequestBody CustomFieldMenuQueryDTO queryDTO);

    @PostMapping("/queryCustomField")
    public JSONResult<List<CustomFieldRespDTO>> queryCustomField(String menuCode);

    /**
     * 根据ID 查询自定义字段 菜单
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/queryFieldMenuById")
    public JSONResult<CustomFieldMenuRespDTO> queryFieldMenuById(@RequestBody IdEntity idEntity);

    /**
     * 保存自定义字段
     * 
     * @param customDTO
     * @return
     */
    @PostMapping("/save")
    public JSONResult save(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO);

    /**
     * 更新自定义字段
     * 
     * @param customDTO
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@Valid @RequestBody CustomFieldAddAndUpdateDTO customDTO);

    /**
     * 删除自定义字段
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListReq idListReq);

    /**
     * 查询菜单下自定义字段
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/query")
    public JSONResult<CustomFieldRespDTO> query(@RequestBody CustomFieldQueryDTO queryDTO);

    /**
     * 分页查询自定义字段列表
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/listCustomFieldPage")
    public JSONResult<PageBean<CustomFieldRespDTO>> listCustomFieldPage(
            @RequestBody CustomFieldQueryDTO queryDTO);

    /**
     * 根据菜单名成或菜单代码查询 菜单 是否存在
     * 
     * @param queryDTO
     */
    @PostMapping("/isExistsFieldMenu")
    public JSONResult<Boolean> isExistsFieldMenu(@RequestBody CustomFieldMenuQueryDTO queryDTO);

    /**
     * 批量上传自定义字段
     * 
     * @param dataList
     */
    @PostMapping("/saveBatchCustomField")
    public JSONResult saveBatchCustomField(@RequestBody List<CustomFieldAddAndUpdateDTO> dataList);

    /**
     * 根据角色、菜单Code 查询自定义字段
     * 
     * @param dataList
     */
    @PostMapping("/queryFieldByRoleAndMenu")
    public JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu(
            @RequestBody QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq);

    /**
     * 根据用户ID、菜单code 查询 自定义字段
     * 
     * @param dataList
     */
    @PostMapping("/queryFieldByUserAndMenu")
    public JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu(
            @RequestBody QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq);

    /**
     * 保存用户、菜单、字段关系
     * 
     * @param dataList
     */
    @PostMapping("/saveUserField")
    public JSONResult<Void> saveUserField(@RequestBody UserFieldReq userFieldReq);

    @Component
    static class HystrixClientFallback implements CustomFieldFeignClient {

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
        public JSONResult deleteMenu(IdListReq idListReq) {
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
        public JSONResult delete(IdListReq idListReq) {
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

        @Override
        public JSONResult<Boolean> isExistsFieldMenu(CustomFieldMenuQueryDTO queryDTO) {
            return fallBackError("是否存在自定义菜单组");
        }

        @Override
        public JSONResult saveBatchCustomField(List<CustomFieldAddAndUpdateDTO> dataList) {
            return fallBackError("批量上传自定义字段");
        }

        @Override
        public JSONResult<List<CustomFieldRespDTO>> queryCustomField(String menuCode) {
            // TODO Auto-generated method stub
            return fallBackError("根据菜单查询自定义字段");
        }

        @Override
        public JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu(
                @RequestBody QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq) {
            return fallBackError("根据角色、菜单Code 查询自定义字段");
        }

        @Override
        public JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu(
                @RequestBody QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq) {
            return fallBackError("根据用户id、菜单Code 查询自定义字段");
        }

        @Override
        public JSONResult<Void> saveUserField(@RequestBody UserFieldReq userFieldReq) {
            return fallBackError("保存用户、菜单、字段关系");
        }

    }

}
