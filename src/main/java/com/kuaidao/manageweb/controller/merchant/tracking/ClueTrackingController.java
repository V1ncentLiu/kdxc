package com.kuaidao.manageweb.controller.merchant.tracking;

import javax.validation.Valid;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.merchant.dto.tracking.TrackingRespDTO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.merchant.tracking.TrackingMerchantFeignClient;
import com.kuaidao.merchant.dto.tracking.TrackingInsertOrUpdateDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import java.util.List;


/**
 * 跟进记录
 * 
 * @author fanjd
 */
@Slf4j
@RestController
@RequestMapping("/tracking")
public class ClueTrackingController {

    @Autowired
    private TrackingMerchantFeignClient trackingMerchantFeignClient;
    private JSONResult<List<TrackingRespDTO>> listJSONResult;

    /**
     * 新增
     */
    @RequestMapping("/insert")
    public JSONResult<Boolean> saveTracking(@Valid @RequestBody TrackingInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 获取当前登录人
        UserInfoDTO user = getUser();
        dto.setCreateUser(user.getId());
        dto.setOrgId(user.getOrgId());
        return trackingMerchantFeignClient.saveTracking(dto);
    }

    /**
     * 根据资源id查询跟进记录
     * 
     * @param clueId
     * @return
     */
    @RequestMapping("/findByClueId")
    public JSONResult<List<TrackingRespDTO>> findByClueId(@RequestParam("clueId") Long clueId) {
        if (null == clueId) {
            return new JSONResult<List<TrackingRespDTO>>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return trackingMerchantFeignClient.findByClueId(clueId);
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
