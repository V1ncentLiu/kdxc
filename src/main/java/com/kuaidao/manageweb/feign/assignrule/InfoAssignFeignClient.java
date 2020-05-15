package com.kuaidao.manageweb.feign.assignrule;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.businessconfig.dto.assignrule.InfoAssignDTO;
import com.kuaidao.businessconfig.dto.assignrule.InfoAssignQueryDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

@FeignClient(name = "business-config-service", path = "/businessConfig/assignrule/infoAssign",
        fallback = InfoAssignFeignClient.HystrixClientFallback.class)
public interface InfoAssignFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/queryInfoAssignPage")
    JSONResult<PageBean<InfoAssignDTO>> queryInfoAssignPage(InfoAssignQueryDTO queryDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/saveInfoAssign")
    JSONResult<String> saveInfoAssign(InfoAssignDTO queryDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/updateInfoAssign")
    JSONResult<String> updateInfoAssign(InfoAssignDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/delteInfoAssign")
    JSONResult<String> delteInfoAssign(@RequestBody InfoAssignDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryInfoAssignById")
    JSONResult<InfoAssignDTO> queryInfoAssignById(@RequestBody InfoAssignQueryDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/findListInfoAssignByName")
    public JSONResult<List<InfoAssignDTO>> findListInfoAssignByName(InfoAssignQueryDTO queryDto);

    @Component
    static class HystrixClientFallback implements InfoAssignFeignClient {

        private static Logger logger = LoggerFactory.getLogger(InfoAssignFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<InfoAssignDTO>> queryInfoAssignPage(
                InfoAssignQueryDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("分页查询信息流分配规则表数据失败");
        }

        @Override
        public JSONResult<String> saveInfoAssign(InfoAssignDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("保存信息流分配规则表数据失败");
        }

        @Override
        public JSONResult<String> updateInfoAssign(InfoAssignDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("修改信息流分配规则表数据失败");
        }

        @Override
        public JSONResult<String> delteInfoAssign(InfoAssignDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("删除信息流分配规则表数据失败");
        }

        @Override
        public JSONResult<InfoAssignDTO> queryInfoAssignById(InfoAssignQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("根据主键查询信息流分配规则数据失败");
        }

        @Override
        public JSONResult<List<InfoAssignDTO>> findListInfoAssignByName(
                InfoAssignQueryDTO queryDto) {
            // TODO Auto-generated method stub
            return fallBackError("根据名称查询信息流分配规则数据失败");
        }

    }
}
