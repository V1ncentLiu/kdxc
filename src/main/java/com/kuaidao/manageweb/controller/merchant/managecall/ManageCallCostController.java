package com.kuaidao.manageweb.controller.merchant.managecall;

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
@RequestMapping("/merchant/manageCallCost")
public class ManageCallCostController {
  private static Logger logger = LoggerFactory.getLogger(ManageCallCostController.class);
  @Autowired
  private MerchantCallCostFeign merchantCallCostFeign;
  @Autowired
  private MerchantUserInfoFeignClient merchantUserInfoFeignClient;


  /**
   * @Description 管理端商家通话费用页面初始化
   * @param request
   * @Return java.lang.String
   * @Author xuyunfeng
   * @Date 2019/10/9 17:15
   **/
  @RequestMapping("/initManageCallCost")
  @RequiresPermissions("merchant:manageCallCost:view")
  public String initManageCallCost(HttpServletRequest request) {
    UserInfoDTO user = CommUtil.getCurLoginUser();
    //获取商家绑定字账号
    UserInfoDTO userInfoDTO = new UserInfoDTO();
    userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
    userInfoDTO.setStatusList(null);
    List<UserInfoDTO> userInfoList = getMerchantUser(userInfoDTO);
    request.setAttribute("userInfoList",userInfoList);
    //获取商家累计消费
    MerchantCallCostReq req = new MerchantCallCostReq();
    JSONResult<String> costJson = merchantCallCostFeign.getTotalMerchantCost(req);
    if(costJson.getCode().equals("0")){
      request.setAttribute("totalMerchantCost",costJson.getData());
    }
    return "merchant/manageCall/manageChargeRecord";
  }

  /**
  * @Description 管理端商家通话费用查询
  * @param req
  * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.call.MerchantCallCostDTO>>
  * @Author xuyunfeng
  * @Date 2019/10/10 17:47
  **/
  @RequestMapping("/getManageCallCostList")
  @ResponseBody
  public JSONResult<Map<String, Object>> getManageCallCostList(@RequestBody MerchantCallCostReq req){
    logger.info("getManageCallCostList参数{{}}",req);
    try {
      JSONResult<Map<String, Object>> jsonResult = merchantCallCostFeign.getManageCallCostList(req);
      return jsonResult;
    }catch (Exception e){
      e.printStackTrace();
      logger.error("获取管理端商家通话费用接口异常",e.getMessage());
      return new JSONResult<Map<String, Object>>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"获取管理端商家通话费用异常");
    }
  }

  /**
  * @Description 查询商家账号
  * @param userInfoDTO
  * @Return java.util.List<com.kuaidao.sys.dto.user.UserInfoDTO>
  * @Author xuyunfeng
  * @Date 2019/10/15 17:19
  **/
  private List<UserInfoDTO> getMerchantUser(UserInfoDTO userInfoDTO) {
    JSONResult<List<UserInfoDTO>> merchantUserList =
        merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
    return merchantUserList.getData();
  }
}
