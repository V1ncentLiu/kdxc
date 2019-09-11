package com.kuaidao.manageweb.feign.merchant.clue;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.merchant.dto.clue.ClueAssignReqDto;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;

/**
 * 资源
 * 
 * @author: fanjd
 * @date: 2019年09月06日
 * @version V1.0
 */
@FeignClient(name = "merchant-service", path = "/merchant/clue/management", fallback = ClueManagementFeignClient.HystrixClientFallback.class)
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
    }


}
