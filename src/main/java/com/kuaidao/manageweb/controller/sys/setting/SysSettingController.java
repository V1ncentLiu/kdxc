package com.kuaidao.manageweb.controller.sys.setting;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 参数管理
 */
@Controller
@RequestMapping(value = "/sys/setting")
public class SysSettingController {


    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    /**系统跳转*/
    @RequestMapping("/page")
    public String page() {
        return "sys/setting/page";
    }

    /**
     * 系统参数分页
     * @param sysSettingReq
     * @return
     */
    @RequestMapping("page")
    public @ResponseBody JSONResult<PageBean<SysSettingDTO>> page(SysSettingReq sysSettingReq){

        return sysSettingFeignClient.page(sysSettingReq) ;
    }
}
