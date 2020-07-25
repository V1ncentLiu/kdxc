package com.kuaidao.manageweb.feign;

import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="sys-service-fanjd")
public interface SysFeign {

    @RequestMapping(method = RequestMethod.POST, value = "/sys/dictionary/findByPrimaryKey")
    public JSONResult<String> findOneDictionary(@RequestBody IdEntity idEntity);

}
