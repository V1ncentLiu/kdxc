package com.kuaidao.manageweb.feign.statistics.invitation;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.invitation.InvitationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name="statstics-service",path = "/statstics/invitation",fallback = InvitationFeignClient.HystrixClientFallback.class)
public interface InvitationFeignClient {


    @RequestMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/queryList")
    public JSONResult<List<InvitationDto>> queryListByParams(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/querySalePage")
    public JSONResult<Map<String,Object>> querySalePage(@RequestBody BaseQueryDto baseQueryDto);


    @RequestMapping("/querySaleList")
    public JSONResult<List<InvitationDto>> querySaleList(@RequestBody BaseQueryDto baseQueryDto);

    @RequestMapping("/queryManagerPage")
    public JSONResult<Map<String,Object>> queryManagerPage(@RequestBody BaseQueryDto baseQueryDto);

    @RequestMapping("/queryManagerList")
    public JSONResult<List<InvitationDto>> queryManagerList(@RequestBody BaseQueryDto baseQueryDto);

    @Component
    class HystrixClientFallback implements InvitationFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Map<String, Object>> queryByPage(BaseQueryDto baseQueryDto) {
            return fallBackError("自邀约跟踪表-组分页查询");
        }

        @Override
        public JSONResult<List<InvitationDto>> queryListByParams(BaseQueryDto baseQueryDto) {
            return fallBackError("自邀约跟踪表-组查询-导出excel");
        }

        @Override
        public JSONResult<Map<String, Object>> querySalePage(BaseQueryDto baseQueryDto) {
            return fallBackError("自邀约跟踪表-顾问查询个人分页");
        }

        @Override
        public JSONResult<List<InvitationDto>> querySaleList(BaseQueryDto baseQueryDto) {
            return fallBackError("自邀约跟踪表-顾问查询个人分页");
        }

        @Override
        public JSONResult<Map<String, Object>> queryManagerPage(BaseQueryDto baseQueryDto) {
            return fallBackError("自邀约跟踪表-总监查询组员-分页");
        }

        @Override
        public JSONResult<List<InvitationDto>> queryManagerList(BaseQueryDto baseQueryDto) {
            return fallBackError("自邀约跟踪表-总监查询组员-导出");
        }
    }

    }
