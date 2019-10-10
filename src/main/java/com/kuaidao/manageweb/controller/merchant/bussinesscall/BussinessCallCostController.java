package com.kuaidao.manageweb.controller.merchant.bussinesscall;

import com.kuaidao.account.dto.call.MerchantCallCostDTO;
import com.kuaidao.account.dto.call.MerchantCallCostReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: BussinessCallCostController
 * @date: 2019/10/9 17:13
 * @author: xuyunfeng
 * @version: 1.0
 */
@Controller
@RequestMapping("/merchant/bussinessCallCost")
public class BussinessCallCostController {

  /**
  * @Description 商家通话费用页面初始化
  * @param request
  * @Return java.lang.String
  * @Author xuyunfeng
  * @Date 2019/10/9 17:15
  **/
  @RequestMapping("/initBussinessCallCost")
  @RequiresPermissions("merchant:bussinessCallCost:view")
  public String initBussinessCallCost(HttpServletRequest request) {

    return "merchant/bussinessCall/chargeRecord";
  }

  /**
   * @Description 商家端商家通话费用列表查询
   * @param req
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.call.MerchantCallCostDTO>>
   * @Author xuyunfeng
   * @Date 2019/10/10 17:08
   **/
  @RequestMapping("/getBussinessCallCostList")
  @ResponseBody
  public JSONResult<PageBean<MerchantCallCostDTO>> getBussinessCallCostList(MerchantCallCostReq req){
    try {


      return new JSONResult<PageBean<MerchantCallCostDTO>>().success(null);
    }catch (Exception e){
      return new JSONResult<PageBean<MerchantCallCostDTO>>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"获取商家端商家通话费用异常");
    }
  }
}
