package com.kuaidao.manageweb.controller.merchant.tracking;

import java.util.Date;

import javax.validation.Valid;

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
import com.kuaidao.merchant.entity.tracking.ClueTracking;
import com.kuaidao.merchant.service.tracking.IClueTrackingService;
import com.kuaidao.merchant.util.IdUtil;

/**
 * 跟进记录
 * 
 * @author fanjd
 */
@RestController
@RequestMapping("/tracking")
public class ClueTrackingController {

    private static Logger logger = LoggerFactory.getLogger(ClueTrackingController.class);

    @Autowired
    private IClueTrackingService<ClueTracking, Long> clueTrackingService;

    /**
     * 新增
     */
    @RequestMapping("/insert")
    public JSONResult<Boolean> saveTracking(@Valid @RequestBody TrackingInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        logger.info("插入一条跟进记录");
        dto.setId(IdUtil.getUUID());
        dto.setCreateTime(new Date());
        boolean b = clueTrackingService.saveTracking(dto);

        return CommonUtil.comResult(b);
    }

}
