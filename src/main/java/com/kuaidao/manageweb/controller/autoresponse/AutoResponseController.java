package com.kuaidao.manageweb.controller.autoresponse;


import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.constant.AutoResponseTypeEnum;
import com.kuaidao.custservice.dto.autoresponse.AutoResponseReq;
import com.kuaidao.custservice.dto.autoresponse.AutoResponseReqDto;
import com.kuaidao.manageweb.feign.autoresponse.AutoResponseFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口层
 * Created  on 2020-9-1 10:42:23
 */
@RestController
@RequestMapping("/autoResponse")
public class AutoResponseController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AutoResponseFeignClient autoResponseFeignClient;


    /**
     * 保存自动提交信息
     *
     * @param autoResponseReq
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public JSONResult<Boolean> saveKaichangbai(@RequestBody AutoResponseReq autoResponseReq) {
        JSONResult<Boolean> jsonResult = checkParam(autoResponseReq);
        if (jsonResult.getCode().equals(JSONResult.FAIL)) {
            return jsonResult;
        }
        return autoResponseFeignClient.saveOrUpdate(autoResponseReq);
    }

    /**
     * 校验入参
     *
     * @param autoResponseReq
     * @return
     */
    private JSONResult<Boolean> checkParam(AutoResponseReq autoResponseReq) {
        if (!CollectionUtils.isNotEmpty(autoResponseReq.getKaiChangList())) {
            return new JSONResult<Boolean>().fail("-1", "开场白不能为空");
        }
        if (autoResponseReq.getTimeOut() == null) {
            return new JSONResult<Boolean>().fail("-1", "请设置超时信息");
        }
        if (autoResponseReq.getNonWorkTime() == null) {
            return new JSONResult<Boolean>().fail("-1", "请设置非工作时间信息");
        }
        if (autoResponseReq.getNonWorkTime() == null) {
            return new JSONResult<Boolean>().fail("-1", "请设置非工作时间信息");
        }
        if (autoResponseReq.getLixian() == null) {
            return new JSONResult<Boolean>().fail("-1", "请设置离线信息");
        }
        List<AutoResponseReqDto> kaiChangList = autoResponseReq.getKaiChangList();
        for (int i = 0; i < kaiChangList.size(); i++) {
            AutoResponseReqDto autoResponseReqDto = kaiChangList.get(i);
            if (StringUtils.isBlank(autoResponseReqDto.getContent())) {
                return new JSONResult<Boolean>().fail("-1", "开场白第" + i + 1 + "条，内容不能为空！");
            }
            if (autoResponseReqDto.getContent().length() > 500) {
                return new JSONResult<Boolean>().fail("-1", "开场白第" + i + 1 + "条，内容长度不能超过500！");
            }
        }

        if (StringUtils.isBlank(autoResponseReq.getTimeOut().getContent())) {
            return new JSONResult<Boolean>().fail("-1", "超时回复内容不能为空！");
        }
        if (autoResponseReq.getTimeOut().getContent().length() > 500) {
            return new JSONResult<Boolean>().fail("-1", "超时回复内容长度不能超过500！");
        }

        if (autoResponseReq.getTimeOut().getTimeOutEnable() == null) {
            return new JSONResult<Boolean>().fail("-1", "请设置超时回复启用状态！");
        }
        if (autoResponseReq.getTimeOut().getTimeOut() == null) {
            return new JSONResult<Boolean>().fail("-1", "请填写超时自动回复时间！");
        }


        if (StringUtils.isBlank(autoResponseReq.getNonWorkTime().getContent())) {
            return new JSONResult<Boolean>().fail("-1", "非工作时间自动回复内容不能为空！");
        }
        if (autoResponseReq.getNonWorkTime().getContent().length() > 500) {
            return new JSONResult<Boolean>().fail("-1", "非工作时间自动回复内容长度不能超过500！");
        }

        if (autoResponseReq.getNonWorkTime().getNonWorktimeEnable() == null) {
            return new JSONResult<Boolean>().fail("-1", "请设置非工作时间自动回复启用状态！");
        }
        if (autoResponseReq.getNonWorkTime().getStartTime() == null) {
            return new JSONResult<Boolean>().fail("-1", "非工作时间段-开始时间！");
        }
        if (autoResponseReq.getNonWorkTime().getEndTime() == null) {
            return new JSONResult<Boolean>().fail("-1", "非工作时间段-结束时间！");
        }

        if (autoResponseReq.getLixian().getContent() == null) {
            return new JSONResult<Boolean>().fail("-1", "离线、忙碌回复内容不能为空");
        }
        return new JSONResult<Boolean>().success(true);
    }


}
