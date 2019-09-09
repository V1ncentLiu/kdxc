package com.kuaidao.manageweb.controller.merchant.clue;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 资源申请审批
 */
@Controller
@RequestMapping("/merchant/clue/setting")
public class MerchantClueSettingController {


    /***
     * 资源需求申请跳转页面
     *
     * @return
     */
    @RequestMapping("/applyIndex")
    @RequiresPermissions("merchant:clueApply:view")
    public String applyIndex(HttpServletRequest request) {
        return "merchant/clueSetting/clueApply";
    }
    /***
     * 资源需求保存
     *
     * @return
     */
    @RequestMapping("/save")
    @RequiresPermissions("merchant:clueApply:add")
    public String save(HttpServletRequest request) {
        return "merchant/clueSetting/clueApply";
    }
}
