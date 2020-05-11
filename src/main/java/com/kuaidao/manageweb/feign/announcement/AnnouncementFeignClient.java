package com.kuaidao.manageweb.feign.announcement;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 *
 * 功能描述: 
 *      系统公告
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service",path="/sys/announcement",fallback = AnnouncementFeignClient.HystrixClientFallback.class)
public interface AnnouncementFeignClient {


    @PostMapping("/publish")
    public JSONResult publishAnnouncement(@RequestBody AnnouncementAddAndUpdateDTO dto);

    @PostMapping("/queryList")
    public JSONResult<PageBean<AnnouncementRespDTO>> queryAnnouncement(@RequestBody AnnouncementQueryDTO queryDTO);

    @PostMapping("/QueryOne")
    public JSONResult findByPrimaryKeyAnnouncement(@RequestBody IdEntity idEntity);

    @Component
    static class HystrixClientFallback implements AnnouncementFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult publishAnnouncement(AnnouncementAddAndUpdateDTO dto) {
            return fallBackError("公告发布失败");
        }

        @Override
        public JSONResult<PageBean<AnnouncementRespDTO>> queryAnnouncement(AnnouncementQueryDTO queryDTO) {
            return fallBackError("公告查询失败");
        }


        @Override
        public JSONResult findByPrimaryKeyAnnouncement(IdEntity idEntity) {
            return fallBackError("公告详细信息获取失败");
        }


    }

}
