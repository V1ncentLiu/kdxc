package com.kuaidao.manageweb.controller.merchant.bussinesscall;

import com.kuaidao.account.dto.call.*;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.merchant.bussinesscall.CallPackageFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shiguopu on 2019/10/9.
 */
@RestController
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
    @RequiresPermissions("merchant:bussinessCallCost:view")
    public String index(HttpServletRequest request) {
        return "merchant/cloudCall/cloudCall";
    }

    @RequestMapping("/buy")
    public JSONResult<CallBuyPackageBuyRes> buy(@RequestBody CallBuyPackageReq callBuyPackageReq) {
        log.info("CallPackageController,buy,callBuyPackageReq={}", callBuyPackageReq);
        return callPackageFeignClient.buy(callBuyPackageReq);
    }

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
    @PostMapping("/list")
    public JSONResult<CallBuyPackageRes> list(@RequestParam Long userId) {
        log.info("CallPackageBuyController,list,userId={}", userId);
        return callPackageFeignClient.list(userId);
    }
}
