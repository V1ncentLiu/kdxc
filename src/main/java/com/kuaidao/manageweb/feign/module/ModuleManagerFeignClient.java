package com.kuaidao.manageweb.feign.module;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.sys.dto.module.IndexModuleDTO;
import com.kuaidao.sys.dto.module.ModuleInfoDTO;
import com.kuaidao.sys.dto.module.ModuleQueryDTO;

@FeignClient(name = "sys-service", path = "/sys/module/moduleManager",
        fallback = ModuleManagerFeignClient.HystrixClientFallback.class)
public interface ModuleManagerFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/queryModuleTree")
    public JSONResult<List<TreeData>> queryModuleTree(ModuleQueryDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryModulePageList")
    public JSONResult<PageBean<ModuleInfoDTO>> queryModulePageList(ModuleQueryDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/saveModuleInfo")
    public JSONResult<String> saveModuleInfo(ModuleInfoDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/updateModuleInfo")
    public JSONResult<String> updateModuleInfo(ModuleInfoDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/deleteModuleInfo")
    public JSONResult<String> deleteModuleInfo(ModuleInfoDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryModuleById")
    public JSONResult<ModuleInfoDTO> queryModuleById(ModuleQueryDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryModuleShow")
    public JSONResult<List<IndexModuleDTO>> queryModuleShow(String systemCode);

    @RequestMapping(method = RequestMethod.POST, value = "/queryModuleByParam")
    public JSONResult<List<ModuleInfoDTO>> queryModuleByParam(ModuleQueryDTO dto);


    @Component
    static class HystrixClientFallback implements ModuleManagerFeignClient {

        private static final Logger logger =
                LoggerFactory.getLogger(ModuleManagerFeignClient.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<List<TreeData>> queryModuleTree(ModuleQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询菜单树数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<PageBean<ModuleInfoDTO>> queryModulePageList(ModuleQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询菜单分页数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<String> saveModuleInfo(ModuleInfoDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("保存菜单数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<String> updateModuleInfo(ModuleInfoDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("修改菜单数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<String> deleteModuleInfo(ModuleInfoDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("删除菜单数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<ModuleInfoDTO> queryModuleById(ModuleQueryDTO dto) {
            return fallBackError("查询修改菜单数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<List<IndexModuleDTO>> queryModuleShow(String systemCode) {
            // TODO Auto-generated method stub
            return fallBackError("查询菜单操作数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<List<ModuleInfoDTO>> queryModuleByParam(ModuleQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询菜单数据失败");
        }

    }

}
