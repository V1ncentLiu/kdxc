package com.kuaidao.manageweb.controller.sys.setting;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * 参数管理
 */
@Controller
@RequestMapping(value = "/sys/setting")
public class SysSettingController {


    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    /**系统跳转*/
    @RequestMapping("/index")
    public String page() {
        return "sys/setting/index";
    }

    /**
     * 系统参数分页
     * @param sysSettingReq
     * @return
     */
    @RequestMapping("/querySysSettingByPage")
    public @ResponseBody JSONResult<PageBean<SysSettingDTO>> querySysSettingByPage(@RequestBody SysSettingReq sysSettingReq){
        return sysSettingFeignClient.querySysSettingByPage(sysSettingReq) ;
    }

    /**
     * 系统参数删除
     * @param codes
     * @return
     */
    @RequestMapping("/deleteSysSettingByCode")
    public @ResponseBody JSONResult<Boolean> deleteSysSettingByCode(@Valid @RequestBody String codes){
        return sysSettingFeignClient.deleteSysSettingByCode(codes) ;
    }
}
