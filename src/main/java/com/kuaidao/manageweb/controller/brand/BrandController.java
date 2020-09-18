package com.kuaidao.manageweb.controller.brand;

import com.alibaba.fastjson.JSONObject;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.dto.brand.BrandListReqDTO;
import com.kuaidao.manageweb.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author: 李锋镝
 * @date: 2020-09-02 14:15
 */
@RequestMapping(value ="/brand")
@RestController
public class BrandController {

    @Value("${kuaidaogroup.brand.domain}")
    private String kuaidaoGroupBrandDomain;

    @GetMapping(value = "/brandCategory/list")
    public JSONResult brandCategoryList(){

        JSONObject jsonObject = HttpClientUtils.httpGet(kuaidaoGroupBrandDomain + "/brand/v1.0/brandCategory/list");
        if(null == jsonObject){
            throw new RuntimeException("获取餐盟分类信息失败") ;
        }
        Object data = jsonObject.get("data");

        return new JSONResult<>().success(data);
    }

    @PostMapping("/listBrand")
    public JSONResult listBrand(@RequestBody BrandListReqDTO reqDTO) {

        reqDTO.setStatus("6");
        JSONObject jsonObject = HttpClientUtils.httpPost(kuaidaoGroupBrandDomain + "/brand/v1.0/brandInfo/listBrand", reqDTO);
        if(null == jsonObject){
            throw new RuntimeException("查询品牌列表失败") ;
        }
        Object data = jsonObject.get("data");

        return new JSONResult<>().success(data);
    }
}
