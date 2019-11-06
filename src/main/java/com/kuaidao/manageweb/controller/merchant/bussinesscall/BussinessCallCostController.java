package com.kuaidao.manageweb.controller.merchant.bussinesscall;

import com.kuaidao.account.dto.call.MerchantCallCostReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.merchant.bussinesscall.MerchantCallCostFeign;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description: BussinessCallCostController
 * @date: 2019/10/9 17:13
 * @author: xuyunfeng
 * @version: 1.0
 */
@Controller
@RequestMapping("/merchant/bussinessCallCost")
public class BussinessCallCostController {
  private static Logger logger = LoggerFactory.getLogger(BussinessCallCostController.class);
  @Autowired
  private MerchantCallCostFeign merchantCallCostFeign;
  @Autowired
  private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
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
    UserInfoDTO user = CommUtil.getCurLoginUser();
    //获取商家绑定字账号
    UserInfoDTO userInfoDTO = new UserInfoDTO();
    userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
    userInfoDTO.setStatusList(null);
    userInfoDTO.setParentId(user.getId());
    List<UserInfoDTO> userInfoList = getMerchantUser(userInfoDTO);
    userInfoList.add(user);
    request.setAttribute("userInfoList",userInfoList);
    //获取商家累计消费
    MerchantCallCostReq req = new MerchantCallCostReq();
    req.setBusinessAccount(user.getId());
    JSONResult<String> costJson = merchantCallCostFeign.getTotalMerchantCost(req);
    if(costJson.getCode().equals("0")){
      request.setAttribute("totalMerchantCost",costJson.getData());
    }
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
  public JSONResult<Map<String, Object>> getBussinessCallCostList(@RequestBody  MerchantCallCostReq req){
    try {
      logger.info("getBussinessCallCostList参数{{}}",req);
      UserInfoDTO user = CommUtil.getCurLoginUser();
      req.setBusinessAccount(user.getId());
      JSONResult<Map<String, Object>> jsonResult = merchantCallCostFeign.getBussinessCallCostList(req);
      return jsonResult;
    }catch (Exception e){
      e.printStackTrace();
      logger.error("获取商家端端商家通话费用接口异常",e.getMessage());
      return new JSONResult<Map<String, Object>>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"获取商家端商家通话费用异常");
    }
  }

  /**
   * 查询商家账号
   *
   * @param userInfoDTO
   * @return
   */
  private List<UserInfoDTO> getMerchantUser(UserInfoDTO userInfoDTO) {
    JSONResult<List<UserInfoDTO>> merchantUserList =
        merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
    return merchantUserList.getData();
  }
}
