package com.kuaidao.manageweb.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.kuaidao.common.entity.JSONResult;

@FeignClient(name="sys")
public interface SysFeign {

}
