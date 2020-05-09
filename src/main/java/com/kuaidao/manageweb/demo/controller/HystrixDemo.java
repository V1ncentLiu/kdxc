package com.kuaidao.manageweb.demo.controller;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.SysFeign;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import com.netflix.hystrix.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: admin
 * @Date: 2019/10/24 11:37
 * @Description:
 */
@Slf4j
@RequestMapping(value = "/hystrixDemo")
@RestController
public class HystrixDemo {

  @Autowired
  SysFeign sysFeign;


  /**
   * Feign集成hystrix后，自带的异步调用
   */
  @PostMapping("/demo")
  public void demo(){
    IdEntity idEntity = new IdEntity();
    idEntity.setId(""+1103100500246663168L);
    long l = System.currentTimeMillis();
//    JSONResult<DictionaryRespDTO> oneDictionary = sysFeign.findOneDictionary(idEntity);
//    idEntity.setId(""+1098821898311176192L);
//    JSONResult<DictionaryRespDTO> oneDictionary2 = sysFeign.findOneDictionary(idEntity);
//    idEntity.setId(""+1098822694146805760L);
//    JSONResult<DictionaryRespDTO> oneDictionary3 = sysFeign.findOneDictionary(idEntity);
//    idEntity.setId(""+1098824483889876992L);
//    JSONResult<DictionaryRespDTO> oneDictionary4 = sysFeign.findOneDictionary(idEntity);
    long l1 = System.currentTimeMillis();
    log.info("调用一个花费时间："+(l1-l));
//    HystrixCommand<JSONResult<DictionaryRespDTO>> one = sysFeign
//        .findOneDicByHystrixCommand(idEntity);
//    HystrixCommand<JSONResult<DictionaryRespDTO>> two = sysFeign
//        .findOneDicByHystrixCommand(idEntity);
//    HystrixCommand<JSONResult<DictionaryRespDTO>> three = sysFeign
//        .findOneDicByHystrixCommand(idEntity);
//    HystrixCommand<JSONResult<DictionaryRespDTO>> four = sysFeign
//        .findOneDicByHystrixCommand(idEntity);
//    JSONResult<DictionaryRespDTO> execute = one.execute();
//    JSONResult<DictionaryRespDTO> execute1 = two.execute();
//    JSONResult<DictionaryRespDTO> execute2 = three.execute();
//    JSONResult<DictionaryRespDTO> execute3 = four.execute();
    long l2 = System.currentTimeMillis();
    log.info("调用dier个花费时间："+(l2-l1));
  }

//  @FeignClient(name="sys-service" , fallback = com.kuaidao.manageweb.feign.SysFeign.HystrixClientFallback.class)
//  public interface SysFeign {
//
//    @RequestMapping(method = RequestMethod.POST, value = "/sys/dictionary/findByPrimaryKey")
//    public JSONResult<DictionaryRespDTO> findOneDictionary(@RequestBody IdEntity idEntity);
//
//    @RequestMapping(method = RequestMethod.POST, value = "/sys/dictionary/findByPrimaryKey")
//    public HystrixCommand<JSONResult<DictionaryRespDTO>> findOneDicByHystrixCommand(@RequestBody IdEntity idEntity);
//
//    @Component
//    static class HystrixClientFallback implements com.kuaidao.manageweb.feign.SysFeign {
//
//
//      private JSONResult fallBackError(String name) {
//        return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
//            SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
//      }
//
//
//      @Override
//      public JSONResult<DictionaryRespDTO> findOneDictionary(IdEntity idEntity) {
//        return null;
//      }
//
//      @Override
//      public HystrixCommand<JSONResult<DictionaryRespDTO>> findOneDicByHystrixCommand(IdEntity idEntity) {
//        return null;
//      }
//    }
//  }


}
