package com.kuaidao.manageweb.controller.merchant.managecall;

import com.kuaidao.account.dto.call.MerchantCallCostDTO;
import com.kuaidao.account.dto.call.MerchantCallCostReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.bussinesscall.MerchantCallCostFeign;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/merchant/manageCallCost")
public class ManageCallCostController {

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
    userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
    userInfoDTO.setStatusList(null);
    userInfoDTO.setParentId(user.getId());
    List<UserInfoDTO> userInfoList = getMerchantUser(userInfoDTO);
    request.setAttribute("userInfoList",userInfoList);
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
  public JSONResult<PageBean<MerchantCallCostDTO>> getManageCallCostList(MerchantCallCostReq req){
    try {
      JSONResult<PageBean<MerchantCallCostDTO>> jsonResult = merchantCallCostFeign.getManageCallCostList(req);
      return jsonResult;
    }catch (Exception e){
      return new JSONResult<PageBean<MerchantCallCostDTO>>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"获取管理端商家通话费用异常");
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
