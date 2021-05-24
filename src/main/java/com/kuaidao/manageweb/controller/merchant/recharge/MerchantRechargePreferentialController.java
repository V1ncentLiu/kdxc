package com.kuaidao.manageweb.controller.merchant.recharge;

import com.kuaidao.account.dto.recharge.MerchantRechargePreferentialDTO;
import com.kuaidao.account.dto.recharge.MerchantRechargePreferentialReq;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.merchant.recharge.MerchantRechargePreferentialFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created on: 2019-09-23-9:33
 */
@Controller
@RequestMapping("/merchant/merchantRechargePreferential")
public class MerchantRechargePreferentialController {

    private static Logger logger = LoggerFactory.getLogger(MerchantRechargePreferentialController.class);

    @Autowired
    private MerchantRechargePreferentialFeignClient merchantRechargePreferentialFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;

    /***
     * 线下付款页面
     *
     * @return
     */
    @RequestMapping("/initOfflinePayment")
//    @RequiresPermissions("???")
    public String initOfflinePayment(HttpServletRequest request) {
        request.setAttribute("paymentName",
            Constants.PAYMENT_NAME);
        request.setAttribute("paymentAccount",
            Constants.PAYMENT_ACCOUNT);
        request.setAttribute("bank",
            Constants.BANK);
        return "merchant/payment/paymentOffline";
    }

    /***
     * 充值优惠设置页面
     *
     * @return
     */
    @RequestMapping("/initRechargePreferential")
    @RequiresPermissions("merchant:merchantRechargePreferential:view")
    public String initRechargePreferential(HttpServletRequest request) {
        // 商家账号
        List<UserInfoDTO> userList = getMerchantUser(null);
        request.setAttribute("merchantUserList",userList);
        return "merchant/chargeSetting/chargeSetting";
    }

    /***
     * 批量保存充值优惠
     *
     * @return
     */
    @RequestMapping("/saveBatchRechargePreferential")
    @RequiresPermissions("merchant:merchantRechargePreferential:add")
    @ResponseBody
    public JSONResult saveBatchRechargePreferential(@RequestBody List<MerchantRechargePreferentialDTO> list) {
        //如果参数为空则直接返回
        if (CollectionUtils.isEmpty(list)) {
            return new JSONResult().success(null);
        }
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        Long id = user.getId();
        return merchantRechargePreferentialFeignClient.saveBatchRechargePreferential(list,id);
    }

    /***
     * 批量更新充值优惠
     *
     * @return
     */
    @RequestMapping("/updateBatchRechargePreferential")
    @RequiresPermissions("merchant:merchantRechargePreferential:edit")
    @ResponseBody
    public JSONResult updateBatchRechargePreferential(@RequestBody List<MerchantRechargePreferentialDTO> list) {
        //如果参数为空则直接返回
        if (CollectionUtils.isEmpty(list)) {
            return new JSONResult().success(null);
        }
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        Long id = user.getId();
        return merchantRechargePreferentialFeignClient.updateBatchRechargePreferential(list,id);
    }

    /***
     * 删除充值优惠
     *
     * @return
     */
    @RequestMapping("/deleteBatchRechargePreferential")
    @ResponseBody
    public JSONResult deleteBatchRechargePreferential(@RequestBody IdEntityLong idEntityLong) {
        return merchantRechargePreferentialFeignClient.deleteBatchRechargePreferential(idEntityLong);
    }

    /***
     * 查询所有优惠金额
     *
     * @return
     */
    @RequestMapping("/findAllRechargePreferential")
    @ResponseBody
    public JSONResult<List<MerchantRechargePreferentialDTO>> findAllRechargePreferential(
        @RequestBody MerchantRechargePreferentialReq req) {
        return merchantRechargePreferentialFeignClient.findAllRechargePreferential(req);
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
