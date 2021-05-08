package com.kuaidao.agentweb.feign.consultantProject;

import com.kuaidao.agentservice.dto.consultantProject.ConsultantProjectIDeleteDTO;
import com.kuaidao.agentservice.dto.consultantProject.ConsultantProjectInsertDTO;
import com.kuaidao.agentservice.dto.consultantProject.ConsultantProjectReqDTO;
import com.kuaidao.agentservice.dto.consultantProject.ConsultantProjectRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 项目设置
 */
@FeignClient(name = "agent-service",path="/agentservice/agentConsultantProject",fallback = ConsultantProjectFeignClient.HystrixClientFallback.class)
public interface ConsultantProjectFeignClient {


    @RequestMapping("/save")
    public JSONResult<Boolean> saveConsultantProject(@RequestBody ConsultantProjectInsertDTO consultantProjectBatchInsertDTO);

    @RequestMapping("/deleteByConsultantId")
    public JSONResult<Boolean> deleteByConsultantId(@RequestBody IdEntityLong idEntityLong);

    @RequestMapping("/deleteById")
    public JSONResult<Boolean> deleteById(@RequestBody ConsultantProjectIDeleteDTO consultantProjectIDeleteDTO);

    @PostMapping("/getAgentConsultantProjectList")
    public JSONResult<PageBean<ConsultantProjectRespDTO>> getAgentConsultantProjectList(@RequestBody ConsultantProjectReqDTO consultantProjectReqDTO);

    @Component
    static class HystrixClientFallback implements ConsultantProjectFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> saveConsultantProject(ConsultantProjectInsertDTO consultantProjectBatchInsertDTO) {
            return fallBackError("保存项目设置");
        }

        @Override
        public JSONResult<Boolean> deleteByConsultantId(IdEntityLong idEntityLong) {
            return fallBackError("根据客户删除项目设置");
        }

        @Override
        public JSONResult<Boolean> deleteById(ConsultantProjectIDeleteDTO consultantProjectIDeleteDTO) {
            return fallBackError("根据ID删除项目设置");
        }

        @Override
        public JSONResult<PageBean<ConsultantProjectRespDTO>> getAgentConsultantProjectList(ConsultantProjectReqDTO consultantProjectReqDTO) {
            return fallBackError("查询项目设置列表");
        }
    }

}
