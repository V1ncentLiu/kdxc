package com.kuaidao.manageweb.feign.statistics.FirstResourceAllocation;


import com.kuaidao.manageweb.feign.statistics.resourceAllocation.StatisticsFeignClient;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(name = "statstics-service", path = "/statstics/firstResourceAllocation", fallback = StatisticsFeignClient.HystrixClientFallback.class)
public interface FirstResourceAllocationFeignClient {
}
