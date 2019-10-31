package com.kuaidao.manageweb.controller.merchant.bussinesscall;

import com.kuaidao.account.dto.call.*;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.merchant.bussinesscall.CallPackageFeignClient;
import com.kuaidao.manageweb.feign.merchant.bussinesscall.CallPackageJobFeignClient;
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
    @Autowired
    private CallPackageJobFeignClient callPackageJobFeignClient;


    /**
     * @param request
     * @Description 云外呼页面初始化
     * @Return java.lang.String
     **/
    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        request.setAttribute("userId", user.getId());
        try {
            JSONResult<CallBuyPackageModel> jsonResult = callPackageFeignClient.getCallBuyPackage(user.getId());
            if (jsonResult.getCode().equals(JSONResult.SUCCESS)
                    && jsonResult.getData() != null) {
                request.setAttribute("originPackageId", jsonResult.getData().getPackageId());
                request.setAttribute("buyCount", jsonResult.getData().getSheetCount());
            }
        } catch (Exception e) {
            log.error("CallPackageController.index error,user={}", e, user);
        }
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
    @GetMapping("/user/account")
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
    @GetMapping("/list")
    public JSONResult<CallBuyPackageRes> list(@RequestParam Long userId) {
        log.info("CallPackageBuyController,list,userId={}", userId);
        return callPackageFeignClient.list(userId);
    }

    @PostMapping("/schedule/deduct/call")
    @ResponseBody
    public JSONResult<String> scheduleDeductCall() {
        log.info("CallPackageBuyController,list");
        return callPackageJobFeignClient.scheduleDeductCall();
    }

    /**
     * 收到扣除套餐费用 endTime是yyyyMMdd
     *
     * @param endTime
     * @return
     */
    @PostMapping("/deduct/package")
    @ResponseBody
    public JSONResult<String> deductPackage(@RequestParam("endTime") String endTime) {
        log.info("CallPackageBuyController,list,endTime={}", endTime);
        return callPackageJobFeignClient.deductPackage(endTime);
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
