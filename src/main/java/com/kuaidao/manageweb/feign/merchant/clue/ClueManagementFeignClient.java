package com.kuaidao.manageweb.feign.merchant.clue;

import com.kuaidao.merchant.dto.clue.ResourceStatisticsParamDTO;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.merchant.dto.clue.ClueAssignReqDto;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;

/**
 * 资源
 * 
 * @author: fanjd
 * @date: 2019年09月06日
 * @version V1.0
 */
@FeignClient(name = "merchant-service-1", path = "/merchant/clue/management", fallback = ClueManagementFeignClient.HystrixClientFallback.class)
public interface ClueManagementFeignClient {

    /**
     * 资源管理列表
     * 
     * @param pageParam
     * @return
     */
    @PostMapping("/queryPage")
    JSONResult<PageBean<ClueManagementDto>> queryPage(@RequestBody ClueManagementParamDto pageParam);

    /**
     * 分配资源
     * 
     * @param reqDto
     * @return
     */
    @PostMapping("/clueAssign")
    JSONResult<String> clueAssign(@RequestBody ClueAssignReqDto reqDto);

    /**
     * 资源导出
     * 
     * @param reqDto
     * @return
     */
    @PostMapping("/listNoPage")
    JSONResult<List<ClueManagementDto>> listNoPage(@RequestBody ClueManagementParamDto reqDto);

    /**
     * 根据子账号id获取分配的资源
     *
     * @param
     * @return
     */
    @PostMapping("/getAssignResourceStatistics")
    JSONResult<ResourceStatisticsDto> getAssignResourceStatistics(@RequestBody IdEntityLong reqDto);

    @PostMapping("/countAssignResourceStatistics")
    JSONResult<List<ResourceStatisticsDto>> countAssignResourceStatistics(@RequestBody ResourceStatisticsParamDTO paramDTO);

    @Component
    static class HystrixClientFallback implements ClueManagementFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueManagementFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<PageBean<ClueManagementDto>> queryPage(ClueManagementParamDto pageParam) {
            return fallBackError("资源管理列表");
        }

        @Override
        public JSONResult<String> clueAssign(ClueAssignReqDto reqDto) {
            return fallBackError("资源分配");
        }

        @Override
        public JSONResult<List<ClueManagementDto>> listNoPage(@RequestBody ClueManagementParamDto reqDto) {
            return fallBackError("资源导出");
        }

        @Override
        public JSONResult<ResourceStatisticsDto> getAssignResourceStatistics(@RequestBody IdEntityLong reqDto) {
            return fallBackError("根据子账号id获取分配的资源");
        }

        @Override
        public JSONResult<List<ResourceStatisticsDto>> countAssignResourceStatistics(
            ResourceStatisticsParamDTO paramDTO) {
            return fallBackError("根据子账号id获取分配的资源-按照维度");
        }
    }


}
