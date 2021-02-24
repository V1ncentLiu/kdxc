package com.kuaidao.manageweb.controller.brand;

import com.alibaba.fastjson.JSONObject;
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.constant.CustServiceConstant;
import com.kuaidao.custservice.dto.brand.BrandListReqDTO;
import com.kuaidao.custservice.dto.saleim.SaleImDTO;
import com.kuaidao.manageweb.feign.im.SaleImFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.manageweb.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: 李锋镝
 * @date: 2020-09-02 14:15
 */
@RequestMapping(value ="/brand")
@RestController
public class BrandController {

    @Value("${kuaidaogroup.brand.domain}")
    private String kuaidaoGroupBrandDomain;

    @Autowired
    private SaleImFeignClient saleImFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;


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

    /**
     * 顾问绑定品牌
     */
    @PostMapping("/bindBrandNames")
    public List<String> bindBrandNames(@RequestBody IdEntityLong idEntityLong){
        JSONResult<SaleImDTO> byTeleSaleId = saleImFeignClient.getByTeleSaleId(idEntityLong);
        Map<Long, ProjectInfoDTO> allBrandMap = getAllProjectList().getData().stream()
                .collect(Collectors.toMap(ProjectInfoDTO::getId, a -> a, (k1, k2) -> k1));
        List<Long> brandIdList = byTeleSaleId.getData().getBrandIdList();
        List<ProjectInfoDTO> brandList = new ArrayList<ProjectInfoDTO>();
        for (Long brandId : brandIdList) {
            ProjectInfoDTO projectInfoDTO = allBrandMap.get(brandId);
            if (projectInfoDTO != null) {
                brandList.add(projectInfoDTO);
            }
        }
        List<String> collect = brandList.stream().map(a -> a.getProjectName()).collect(Collectors.toList());
        return collect;
    }

    private JSONResult<List<ProjectInfoDTO>> getAllProjectList() {
        List<String> list = new ArrayList<String>();
        list.add(CustServiceConstant.PROJECT_ATTRIBUTIVE_CMYXDJ);
        list.add(CustServiceConstant.PROJECT_ATTRIBUTIVE_CMYXFDJ);
        ProjectInfoPageParam pageParam = new ProjectInfoPageParam();
        pageParam.setProjectAttributiveList(list);
        pageParam.setIsNotSign(AggregationConstant.NO);
        JSONResult<List<ProjectInfoDTO>> projectList = projectInfoFeignClient.listNoPage(pageParam);
        return projectList;
    }
}
