package com.kuaidao.manageweb.feign;

import com.kuaidao.common.entity.IdEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.kuaidao.common.entity.JSONResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="sys-service")
public interface SysFeign {

    @RequestMapping(method = RequestMethod.POST, value = "/sys/dictionary/findByPrimaryKey")
    public JSONResult<String> findOneDictionary(@RequestBody IdEntity idEntity);

}
