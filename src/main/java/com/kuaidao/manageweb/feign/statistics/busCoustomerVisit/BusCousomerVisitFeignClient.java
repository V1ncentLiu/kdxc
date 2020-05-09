package com.kuaidao.manageweb.feign.statistics.busCoustomerVisit;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.statistics.appointmentVisit.AppointmentVisitFeignClient;
import com.kuaidao.stastics.dto.bussCoustomerVisit.CustomerVisitDto;
import com.kuaidao.stastics.dto.bussCoustomerVisit.CustomerVisitQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @author: guhuitao
 * @create: 2019-08-20 14:13
 **/
@FeignClient(name = "statstics-service", path = "/statstics/busCustomerVisit", fallback = BusCousomerVisitFeignClient.HystrixClientFallback.class)
public interface BusCousomerVisitFeignClient {

    @RequestMapping("/queryPage")
    public JSONResult<Map<String,Object>> queryByPage(CustomerVisitQueryDto customerVisitQueryDto);


    @RequestMapping("/queryList")
    public JSONResult<List<CustomerVisitDto>> queryListByParams(CustomerVisitQueryDto customerVisitQueryDto);



    @RequestMapping("/queryPageByManage")
    public JSONResult<Map<String,Object>> queryPageByManagerId(CustomerVisitQueryDto customerVisitQueryDto);


    @RequestMapping("/queryListByManagerId")
    public JSONResult<List<CustomerVisitDto>> queryManagerListByParams(CustomerVisitQueryDto customerVisitQueryDto);

     @Component
     class HystrixClientFallback implements  BusCousomerVisitFeignClient{
         private static Logger logger = LoggerFactory.getLogger(AppointmentVisitFeignClient.class);

         private JSONResult fallBackError(String name) {
             logger.error(name + "接口调用失败：无法获取目标服务");
             return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                     SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
         }

         @Override
         public JSONResult<Map<String,Object>> queryByPage(CustomerVisitQueryDto customerVisitQueryDto) {
             return fallBackError("来访签约统计");
         }

         @Override
         public JSONResult<List<CustomerVisitDto>> queryListByParams(CustomerVisitQueryDto customerVisitQueryDto) {
             return fallBackError("来访签约统计导出excel");
         }

         @Override
         public JSONResult<Map<String,Object>> queryPageByManagerId(CustomerVisitQueryDto customerVisitQueryDto) {
             return fallBackError("商务经理来访签约统计");
         }

         @Override
         public JSONResult<List<CustomerVisitDto>> queryManagerListByParams(CustomerVisitQueryDto customerVisitQueryDto) {
             return fallBackError("商务经理来访签约统计导出excel");
         }
     }


}

