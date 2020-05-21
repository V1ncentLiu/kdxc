package com.kuaidao.manageweb.feign.clue;

import com.kuaidao.aggregation.dto.clue.RepeatClueRecordDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueRecordQueryDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "aggregation-service-1", path = "/aggregation/repeatClueRecord",
    fallback = RepeatClueRecordFeignClient.HystrixClientFallback.class)
public interface RepeatClueRecordFeignClient {

  /**
   * 查询重复资源信息
   */
  @RequestMapping(method = RequestMethod.POST, value = "/queryList")
  JSONResult<List<RepeatClueRecordDTO>> queryList(@RequestBody RepeatClueRecordQueryDTO dto);

  @Component
  static class HystrixClientFallback implements RepeatClueRecordFeignClient {

    private static Logger logger = LoggerFactory.getLogger(RepeatClueRecordFeignClient.class);

    private JSONResult fallBackError(String name) {
      logger.error(name + "接口调用失败：无法获取目标服务");
      return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
          SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }

    @Override
    public JSONResult<List<RepeatClueRecordDTO>> queryList(
        RepeatClueRecordQueryDTO pageParam) {
      return fallBackError("查询重复资源信息");
    }
  }
}
