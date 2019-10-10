package com.kuaidao.manageweb.controller.merchant.tracking;

import java.util.Date;
import javax.validation.Valid;
import com.kuaidao.manageweb.feign.merchant.tracking.TrackingFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.merchant.dto.tracking.TrackingInsertOrUpdateDTO;


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
    private TrackingFeignClient trackingFeignClient;

    /**
     * 新增
     */
    @RequestMapping("/insert")
    public JSONResult<Boolean> saveTracking(@Valid @RequestBody TrackingInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return trackingFeignClient.saveTracking(dto);
    }

}
