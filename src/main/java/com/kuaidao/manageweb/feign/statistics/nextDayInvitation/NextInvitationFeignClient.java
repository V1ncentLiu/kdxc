package com.kuaidao.manageweb.feign.statistics.nextDayInvitation;


import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.invitation.NextInvitationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name="statstics-service3",path = "/statstics/nextInvitation",fallback = NextInvitationFeignClient.HystrixClientFallback.class)
public interface NextInvitationFeignClient {


    @RequestMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/queryList")
    public JSONResult<List<NextInvitationDto>> queryListByParams(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/queryAreaPage")
    public JSONResult<Map<String,Object>> queryAreaPage(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/queryAreaList")
    public JSONResult<List<NextInvitationDto>> queryAreaList(@RequestBody BaseQueryDto baseQueryDto);

    @Component
    class HystrixClientFallback implements NextInvitationFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPage(BaseQueryDto baseQueryDto) {
            return fallBackError("次日邀约统计表-分页查询");
        }

        @Override
        public JSONResult<List<NextInvitationDto>> queryListByParams(BaseQueryDto baseQueryDto) {
            return fallBackError("次日邀约统计表-导出excel查询");
        }

        @Override
        public JSONResult<Map<String, Object>> queryAreaPage(BaseQueryDto baseQueryDto) {
            return fallBackError("次日邀约统计表-电销组分页查询");
        }

        @Override
        public JSONResult<List<NextInvitationDto>> queryAreaList(BaseQueryDto baseQueryDto) {
            return fallBackError("次日邀约统计表-电销组导出excel查询");
        }
    }

}
