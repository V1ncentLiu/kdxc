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

import javax.annotation.Resource;
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

    @Resource
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
        return new JSONResult<Boolean>().success(true);
    }


}
