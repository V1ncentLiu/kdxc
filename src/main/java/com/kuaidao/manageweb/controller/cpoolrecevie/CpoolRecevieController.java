package com.kuaidao.manageweb.controller.cpoolrecevie;

import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.cpoolrecevie.CpoolRecevieFeignClient;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleInsertOrUpdateDTO;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleReqDTO;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleRespDTO;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: admin
 * @Date: 2019/9/6 11:36
 * @Description:
 */
@Controller
@RequestMapping("/console/console")
public class CpoolRecevieController {
  private static Logger logger = LoggerFactory.getLogger(CpoolRecevieController.class);

  @Autowired
  private CpoolRecevieFeignClient cpoolRecevieFeignClient;



  /**
   * 创建共有池领取规则
   */
  @PostMapping("/create")
  public JSONResult<Long> create(@RequestBody CpoolReceivelRuleInsertOrUpdateDTO cpoolReceivelRule
     ) throws NoSuchAlgorithmException {
    logger.info("创建参数{}",cpoolReceivelRule);
    JSONResult<Long> josnResult = cpoolRecevieFeignClient.create(cpoolReceivelRule);
    return josnResult;
  }

  /**
   * 修改共有池领取规则
   */
  @PostMapping("/update")
  public JSONResult<String> update(@RequestBody CpoolReceivelRuleInsertOrUpdateDTO cpoolReceivelRule) throws NoSuchAlgorithmException {
    logger.info("更新参数{}",cpoolReceivelRule);
    JSONResult<String> josnResult =
        cpoolRecevieFeignClient.update(cpoolReceivelRule);
    return josnResult;
  }

  /**
   * 删除共有池领取规则
   */
  @PostMapping("/delete")
  public JSONResult<String> delete(@RequestBody IdListLongReq idList) {
    logger.info("删除参数{}",idList);
    JSONResult<String> delete = cpoolRecevieFeignClient.delete(idList);
    return delete;
  }


  /**
   * 查询共有池分配规则
   * @return
   */
  @PostMapping("/get")
  public JSONResult<CpoolReceivelRuleRespDTO> getbyId(@RequestBody IdEntityLong idEntity) {
    logger.info("查询参数{}",idEntity);
    JSONResult<CpoolReceivelRuleRespDTO> josnResult = cpoolRecevieFeignClient.getbyId(idEntity);
    return josnResult;
  }

  /**
   * 更新状态接口
   */
  @PostMapping("/updateStatus")
  public JSONResult<String> updateStatus(@RequestBody CpoolReceivelRuleReqDTO param) throws NoSuchAlgorithmException {
    logger.info("更新状态参数{}",param);
    JSONResult<String> stringJSONResult = cpoolRecevieFeignClient.updateStatus(param);
    return stringJSONResult;
  }

  /**
   * 查询共有池分配规则列表
   * @return
   */
  @PostMapping("/list")
  public JSONResult<PageBean<CpoolReceivelRuleRespDTO>> list(@RequestBody CpoolReceivelRuleReqDTO pageParam) {
    logger.info("列表查询参数{}",pageParam);
    // 业务逻辑处理
    JSONResult<PageBean<CpoolReceivelRuleRespDTO>> josnResult = cpoolRecevieFeignClient.list(pageParam);
    return josnResult;
  }

}
