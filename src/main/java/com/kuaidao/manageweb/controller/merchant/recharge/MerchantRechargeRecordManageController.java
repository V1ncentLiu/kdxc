package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeRecordQueryDTO;
import com.kuaidao.account.dto.recharge.RechargeAccountDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargeRecordManageFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: MerchantRechargeRecordBusinessController
 * @date: 2019/9/26 15:48
 * @author: xuyunfeng
 * @version: 1.0
 */
@Controller
@RequestMapping("/merchant/merchantRechargeRecordManage")
public class MerchantRechargeRecordManageController {
  private static Logger logger = LoggerFactory.getLogger(MerchantRechargeRecordManageController.class);


  @Autowired
  MerchantRechargeRecordManageFeignClient merchantRechargeRecordManageFeignClient;

  /**
   * @Description 初始化管理端充值记录页面
   * @param request
   * @Return java.lang.String
   * @Author xuyunfeng
   * @Date 2019/9/26 15:57
   **/
  @RequestMapping("/initRechargeRecordManage")
  public String initRechargeRecordBusiness(HttpServletRequest request){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      MerchantRechargeRecordQueryDTO queryDTO = new MerchantRechargeRecordQueryDTO();
      queryDTO.setManageRechargeUser(user.getId());
      JSONResult<RechargeAccountDTO> rechargeAccountDTOJSONResult = merchantRechargeRecordManageFeignClient.getNowDayAndMonthRechargeMoney(queryDTO);
      RechargeAccountDTO rechargeAccountDTO = rechargeAccountDTOJSONResult.getData();
      request.setAttribute("rechargeAccountDTO",rechargeAccountDTO);
    }catch (Exception e){
      logger.error("initRechargeRecordManage:{}",e);
    }
    return null;
  }
  /**
   * @Description 管理端充值记录列表查询
   * @param queryDTO
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>>
   * @Author xuyunfeng
   * @Date 2019/9/26 16:07
   **/
  @RequestMapping("/queryManagePageList")
  public JSONResult<PageBean<MerchantRechargeRecordDTO>> queryBusinessPageList(@RequestBody MerchantRechargeRecordQueryDTO queryDTO ){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      queryDTO.setManageRechargeUser(user.getId());
      JSONResult<PageBean<MerchantRechargeRecordDTO>> list = merchantRechargeRecordManageFeignClient.queryManagePageList(queryDTO);
      return list;
    }catch (Exception e){
      logger.error("queryManagePageList:{}",e);
      return new JSONResult<PageBean<MerchantRechargeRecordDTO>>().fail("-1","queryManagePageList接口异常");
    }
  }
}
