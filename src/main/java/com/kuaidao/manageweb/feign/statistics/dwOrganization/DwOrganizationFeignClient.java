package com.kuaidao.manageweb.feign.statistics.dwOrganization;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.dwOrganizationQueryDTO.DwOrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "statstics-service", path = "/statstics/dwOrganization", fallback = DwOrganizationFeignClient.HystrixClientFallback.class)
public interface DwOrganizationFeignClient {

    @RequestMapping("/getDwOrganization")
    JSONResult<List<OrganizationRespDTO>> getDwOrganization(@RequestBody DwOrganizationQueryDTO dwOrganizationQueryDTO);

    @Component
    class HystrixClientFallback implements DwOrganizationFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<List<OrganizationRespDTO>> getDwOrganization(DwOrganizationQueryDTO dwOrganizationQueryDTO) {
            return fallBackError("查询DW组织机构失败");
        }
    }
}
