package com.kuaidao.manageweb.controller.customer;

import com.alibaba.fastjson.JSONObject;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口层 Created on 2020-8-28 16:35:05
 */
@RequestMapping(value = "/customerInfo")
@RestController
public class CustomerInfoController {


    @Value("${kuaidaogroup.sys.domain}")
    private String kuaidaoGroupDomain;


    @PostMapping(value = "/customerInfoByIm")
    public JSONResult customerInfoByIm(@RequestBody IdEntity idEntity) {

        // 封装客户
        JSONObject object = new JSONObject();
        object.put("id", idEntity.getId());
        JSONObject jsonObject = HttpClientUtils
                .httpPost(kuaidaoGroupDomain + "/v1.0/sysServer/account/customerInfoByIm", object);
        if (null == jsonObject) {
            throw new RuntimeException("获得客户信息失败");
        }
        Object data = jsonObject.get("data");

        return new JSONResult<>().success(data);
    }

}