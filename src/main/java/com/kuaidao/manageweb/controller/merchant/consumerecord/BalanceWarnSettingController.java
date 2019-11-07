/**
 *
 */
package com.kuaidao.manageweb.controller.merchant.consumerecord;

import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.account.dto.balancewarn.BalanceWarnSettingDTO;
import com.kuaidao.account.dto.balancewarn.BalanceWarnSettingReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.merchant.consumerecord.BalanceWarnSettingFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/merchant/balanceWarnSetting")
public class BalanceWarnSettingController {
    private static Logger logger = LoggerFactory.getLogger(MerchantConsumeRecordController.class);
    @Autowired
    private BalanceWarnSettingFeignClient balanceWarnSettingFeignClient;


    /***
     * 余额不足提醒设置
     *
     * @return
     */
    @PostMapping("/setting")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:setting")
    public JSONResult<Long> setting(@RequestBody BalanceWarnSettingReq balanceWarnSettingReq,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        balanceWarnSettingReq.setUserId(user.getId());
        JSONResult<Long> setting = balanceWarnSettingFeignClient.setting(balanceWarnSettingReq);
        return setting;
    }

    /***
     * 获取余额不足提醒设置
     *
     * @return
     */
    @PostMapping("/getBalanceWarnSetting")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:setting")
    public JSONResult<BalanceWarnSettingDTO> getBalanceWarnSetting(HttpServletRequest request) {
        JSONResult<BalanceWarnSettingDTO> balanceWarnSetting =
                balanceWarnSettingFeignClient.getBalanceWarnSetting();
        return balanceWarnSetting;
    }

    /**
     * 获取当前登录账号
     *
     * @param orgDTO
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

}
