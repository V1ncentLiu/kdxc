package com.kuaidao.manageweb.feign.announcement;

import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;


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

        @Override
        public JSONResult publishAnnouncement(AnnouncementAddAndUpdateDTO dto) {
            return null;
        }

        @Override
        public JSONResult<PageBean<AnnouncementRespDTO>> queryAnnouncement(AnnouncementQueryDTO queryDTO) {
            return null;
        }


        @Override
        public JSONResult findByPrimaryKeyAnnouncement(IdEntity idEntity) {
            return null;
        }


    }

}
