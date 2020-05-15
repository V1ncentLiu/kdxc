package com.kuaidao.manageweb.feign.assignrule;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.businessconfig.dto.assignrule.TeleAssignRuleQueryDTO;
import com.kuaidao.businessconfig.dto.assignrule.TelemarketingAssignRuleDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

@FeignClient(name = "business-config-service",
        path = "/businessConfig/assignrule/telemarketingAssignRule",
        fallback = TelemarketingAssignRuleFeignClient.HystrixClientFallback.class)
public interface TelemarketingAssignRuleFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/queryTeleAssignRulePage")
    JSONResult<PageBean<TelemarketingAssignRuleDTO>> queryTeleAssignRulePage(
            TeleAssignRuleQueryDTO queryDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/saveTeleAssignRule")
    JSONResult<String> saveTeleAssignRule(TelemarketingAssignRuleDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/updateTeleAssignRule")
    JSONResult<String> updateTeleAssignRule(TelemarketingAssignRuleDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/deleteTeleAssignRule")
    JSONResult<String> deleteTeleAssignRule(TelemarketingAssignRuleDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/updateTeleAssignRuleStatus")
    JSONResult<String> updateTeleAssignRuleStatus(TelemarketingAssignRuleDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryTeleAssignRuleById")
    JSONResult<TelemarketingAssignRuleDTO> queryTeleAssignRuleById(TeleAssignRuleQueryDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/queryTeleAssignRuleByName")
    public JSONResult<List<TelemarketingAssignRuleDTO>> queryTeleAssignRuleByName(
            TeleAssignRuleQueryDTO queryDto);

    @Component
    static class HystrixClientFallback implements TelemarketingAssignRuleFeignClient {

        private static Logger logger =
                LoggerFactory.getLogger(TelemarketingAssignRuleFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<TelemarketingAssignRuleDTO>> queryTeleAssignRulePage(
                TeleAssignRuleQueryDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("分页查询电销分配规则表数据失败");
        }

        @Override
        public JSONResult<String> saveTeleAssignRule(TelemarketingAssignRuleDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("保存电销分配规则表数据失败");
        }

        @Override
        public JSONResult<String> updateTeleAssignRule(TelemarketingAssignRuleDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("修改电销分配规则表数据失败");
        }

        @Override
        public JSONResult<String> deleteTeleAssignRule(TelemarketingAssignRuleDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("删除 电销分配规则表数据失败");
        }

        @Override
        public JSONResult<String> updateTeleAssignRuleStatus(TelemarketingAssignRuleDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("修改电销分配规则状态表数据失败");
        }

        @Override
        public JSONResult<TelemarketingAssignRuleDTO> queryTeleAssignRuleById(
                TeleAssignRuleQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("根据主键查询电销分配规则数据失败");
        }

        @Override
        public JSONResult<List<TelemarketingAssignRuleDTO>> queryTeleAssignRuleByName(
                TeleAssignRuleQueryDTO queryDto) {
            // TODO Auto-generated method stub
            return fallBackError("根据名称查询电销分配规则数据失败");
        }

    }
}
