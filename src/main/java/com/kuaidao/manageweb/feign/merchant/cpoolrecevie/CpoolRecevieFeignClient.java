package com.kuaidao.manageweb.feign.merchant.cpoolrecevie;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleInsertOrUpdateDTO;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleReqDTO;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleRespDTO;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Auther: admin
 * @Date: 2019/9/6 11:37
 * @Description:
 */
@FeignClient(name = "merchant-service", path = "/merchant/cpoolReceivelRule",
    fallback = CpoolRecevieFeignClient.HystrixClientFallback.class)
public interface CpoolRecevieFeignClient {

  @PostMapping("/create")
  public JSONResult<Long> create(@Valid @RequestBody CpoolReceivelRuleInsertOrUpdateDTO cpoolReceivelRule);

  @PostMapping("/update")
  public JSONResult<String> update(@Valid @RequestBody CpoolReceivelRuleInsertOrUpdateDTO cpoolReceivelRule);

  @PostMapping("/delete")
  public JSONResult<String> delete(@Valid @RequestBody IdListLongReq idList);

  @PostMapping("/get")
  public JSONResult<CpoolReceivelRuleRespDTO> getbyId(@Valid @RequestBody IdEntityLong idEntity);

  @PostMapping("/updateStatus")
  public JSONResult<String> updateStatus(@Valid @RequestBody CpoolReceivelRuleReqDTO param);

  @PostMapping("/list")
  public JSONResult<PageBean<CpoolReceivelRuleRespDTO>> list(
      @RequestBody CpoolReceivelRuleReqDTO pageParam);

  @Component
  static class HystrixClientFallback implements CpoolRecevieFeignClient {

    private static Logger logger = LoggerFactory.getLogger(CustomFieldFeignClient.class);

    private JSONResult fallBackError(String name) {
      logger.error(name + "接口调用失败：无法获取目标服务");
      return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
          SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }

    @Override
    public JSONResult<Long> create(CpoolReceivelRuleInsertOrUpdateDTO cpoolReceivelRule) {
      return fallBackError("创建共有池领取规则");
    }

    @Override
    public JSONResult<String> update(CpoolReceivelRuleInsertOrUpdateDTO cpoolReceivelRule) {
      return fallBackError("更新共有池领取规则");
    }

    @Override
    public JSONResult<String> delete(IdListLongReq idList) {
      return fallBackError("删除共有池领取规则");
    }

    @Override
    public JSONResult<CpoolReceivelRuleRespDTO> getbyId(IdEntityLong idEntity) {
      return fallBackError("获取共有池领取规则");
    }

    @Override
    public JSONResult<String> updateStatus(CpoolReceivelRuleReqDTO param) {
      return fallBackError("共有池领取规则状态更新");
    }

    @Override
    public JSONResult<PageBean<CpoolReceivelRuleRespDTO>> list(CpoolReceivelRuleReqDTO pageParam) {
      return fallBackError("共有池领取规则列表查询");
    }
  }


}
