package com.kuaidao.manageweb.controller.merchant.bussinesscall;

import com.kuaidao.account.dto.call.*;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.merchant.bussinesscall.CallPackageFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shiguopu on 2019/10/9.
 */
@Controller
@RequestMapping("/merchant/call/package")
@Slf4j
public class CallPackageController {

    @Autowired
    private CallPackageFeignClient callPackageFeignClient;


    /**
     * @Description 云外呼页面初始化
     * @param request
     * @Return java.lang.String
     **/
    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        request.setAttribute("userId", user.getId());
        return "merchant/cloudCall/cloudCall";
    }

    @RequestMapping("/buy")
    @ResponseBody
    public JSONResult<CallBuyPackageBuyRes> buy(@RequestBody CallBuyPackageReq callBuyPackageReq) {
        log.info("CallPackageController,buy,callBuyPackageReq={}", callBuyPackageReq);
        return callPackageFeignClient.buy(callBuyPackageReq);
    }
    @ResponseBody
    @RequestMapping("/change")
    public JSONResult<CallBuyPackageChangeRes> change(@RequestBody CallChangePackageReq callChangePackageReq) {
        log.info("CallPackageBuyController,change,callChangePackageReq={}", callChangePackageReq);
        return callPackageFeignClient.change(callChangePackageReq);
    }

    /**
     * 购买套餐用户余额
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/user/account")
    public JSONResult<CallUserAccountRes> getUserAccount(@RequestParam Long userId) {
        log.info("CallPackageBuyController.getUserAccount,userId={}", userId);
        return callPackageFeignClient.getUserAccount(userId);
    }


    /**
     * 套餐列表
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/list")
    public JSONResult<CallBuyPackageRes> list(@RequestParam Long userId) {
        log.info("CallPackageBuyController,list,userId={}", userId);
        return callPackageFeignClient.list(userId);
    }

    /**
     * 获取当前登录账号
     *
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }
}
