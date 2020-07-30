package com.kuaidao.manageweb.controller.sys.setting;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 参数管理
 */
@Controller
@RequestMapping(value = "/sys/setting")
public class SysSettingController {


    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    /**系统跳转*/
    @RequiresPermissions("sys:setting:view")
    @RequestMapping("/index")
    public String page() {
        return "sys/setting/index";
    }

    /**
     * 系统参数分页
     * @param sysSettingReq
     * @return
     */
    @RequiresPermissions("sys:setting:view")
    @RequestMapping("/querySysSettingByPage")
    public @ResponseBody JSONResult<PageBean<SysSettingDTO>> querySysSettingByPage(@RequestBody SysSettingReq sysSettingReq){
        return sysSettingFeignClient.querySysSettingByPage(sysSettingReq) ;
    }

    /**
     * 系统参数删除
     * @param map
     * @return
     */
    @RequiresPermissions("sys:setting:delete")
    @RequestMapping("/deleteSysSettingByCode")
    public @ResponseBody JSONResult<Boolean> deleteSysSettingByCode(@RequestBody Map<String,String> map) {
        String codes = map.get("codes");
        if(StringUtils.isBlank(codes)){
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return sysSettingFeignClient.deleteSysSettingByCode(codes) ;
    }

    /**
     * 根据Code查询系统参数
     * @param sysSettingReq
     * @return
     */
    @RequiresPermissions("sys:setting:view")
    @RequestMapping("/findSysSettingByCode")
    public @ResponseBody JSONResult<SysSettingDTO> findSysSettingByCode(@RequestBody SysSettingReq sysSettingReq){
        return sysSettingFeignClient.findSysSettingByCode(sysSettingReq) ;
    }


    /**
     * 根据Code修改参数
     * @param sysSettingReq
     * @return
     */
    @RequiresPermissions("sys:setting:edit")
    @RequestMapping("/updateSysSettingByCode")
    public @ResponseBody JSONResult<Boolean> updateSysSettingByCode(@RequestBody SysSettingReq sysSettingReq){
        if(null == sysSettingReq || StringUtils.isBlank(sysSettingReq.getCode()) || StringUtils.isBlank(sysSettingReq.getValue())){
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return sysSettingFeignClient.updateSysSettingByCode(sysSettingReq) ;
    }

    /**
     * 添加系统参数
     * @param sysSettingReq
     * @return
     */
    @RequiresPermissions("sys:setting:add")
    @RequestMapping("/addSysSetting")
    public @ResponseBody JSONResult<Boolean> addSysSetting(@RequestBody SysSettingReq sysSettingReq){
        if(null == sysSettingReq || StringUtils.isBlank(sysSettingReq.getCode()) || StringUtils.isBlank(sysSettingReq.getValue())){
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return sysSettingFeignClient.addSysSetting(sysSettingReq) ;
    }
}
