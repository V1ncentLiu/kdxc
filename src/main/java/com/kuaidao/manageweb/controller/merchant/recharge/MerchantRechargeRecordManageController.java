package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeRecordQueryDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargeReq;
import com.kuaidao.account.dto.recharge.RechargeAccountDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargeRecordManageFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
  @Autowired
  private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
  /**
   * @Description 初始化管理端充值记录页面
   * @param request
   * @Return java.lang.String
   * @Author xuyunfeng
   * @Date 2019/9/26 15:57
   **/
  @RequestMapping("/initRechargeRecordManage")
  @RequiresPermissions("merchant:merchantRechargeRecordManage:view")
  public String initRechargeRecordBusiness(HttpServletRequest request){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      MerchantRechargeRecordQueryDTO queryDTO = new MerchantRechargeRecordQueryDTO();
      queryDTO.setManageRechargeUser(user.getId());
      JSONResult<RechargeAccountDTO> rechargeAccountDTOJSONResult = merchantRechargeRecordManageFeignClient.getNowDayAndMonthRechargeMoney(queryDTO);
      RechargeAccountDTO rechargeAccountDTO = rechargeAccountDTOJSONResult.getData();
      request.setAttribute("rechargeAccountDTO",rechargeAccountDTO);
      // 商家账号
      List<UserInfoDTO> userList = getMerchantUser(null);
      request.setAttribute("merchantUserList",userList);
    }catch (Exception e){
      logger.error("initRechargeRecordManage:{}",e);
    }
    return "/merchant/rechargeRecord/rechargeRecord";
  }
  /**
   * @Description 管理端充值记录列表查询
   * @param queryDTO
   * @Return com.kuaidao.common.entity.JSONResult<com.kuaidao.common.entity.PageBean<com.kuaidao.account.dto.recharge.MerchantRechargeRecordDTO>>
   * @Author xuyunfeng
   * @Date 2019/9/26 16:07
   **/
  @ResponseBody
  @RequestMapping("/queryManagePageList")
  @RequiresPermissions("merchant:merchantRechargeRecordManage:view")
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

  /**
  * @Description 加载管理端录入线下付款页面
  * @param request
  * @Return com.kuaidao.common.entity.JSONResult<java.lang.Boolean>
  * @Author xuyunfeng
  * @Date 2019/9/27 15:30
  **/
  @RequestMapping("/initOfflinePayment")
  @RequiresPermissions("merchant:merchantRechargeRecordManage:add")
  public String initOfflinePayment(HttpServletRequest request){
    // 商家账号
    List<UserInfoDTO> userList = getMerchantUser(null);
    request.setAttribute("merchantUserList",userList);

    return "/merchant/rechargeRecord/rechargePaymentOffline";
  }

  /**
  * @Description 录入线下付款信息
  * @param
  * @Return com.kuaidao.common.entity.JSONResult<java.lang.Boolean>
  * @Author xuyunfeng
  * @Date 2019/9/27 15:30
  **/
  @ResponseBody
  @RequestMapping("/saveOfflinePayment")
  @RequiresPermissions("merchant:merchantRechargeRecordManage:add")
  public JSONResult<Boolean> saveOfflinePayment(@RequestBody  MerchantRechargeReq req){
    try {
      UserInfoDTO user = CommUtil.getCurLoginUser();
      req.setCreateUser(user.getId());
      JSONResult<Boolean> list = merchantRechargeRecordManageFeignClient.saveOfflinePayment(req);
      return list;
    }catch (Exception e){
      logger.error("录入线下付款信息:{}",e);
      return new JSONResult<Boolean>().fail("-1","录入线下付款信息接口异常");
    }
  }

  /**
   * 查询商家账号
   *
   * @param arrayList
   * @return
   */
  private List<UserInfoDTO> getMerchantUser(List<Integer> arrayList) {
    UserInfoDTO userInfoDTO = new UserInfoDTO();
    userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
    userInfoDTO.setStatusList(arrayList);
    JSONResult<List<UserInfoDTO>> merchantUserList =
        merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
    return merchantUserList.getData();
  }
}
