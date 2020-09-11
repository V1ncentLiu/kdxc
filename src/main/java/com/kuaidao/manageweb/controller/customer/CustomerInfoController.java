package com.kuaidao.manageweb.controller.customer;

import com.alibaba.fastjson.JSONObject;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.dto.custservice.CustomerInfoDTO;
import com.kuaidao.manageweb.feign.clue.ClueCustomerFeignClient;
import com.kuaidao.manageweb.feign.im.CustomerInfoFeignClient;
import com.kuaidao.manageweb.util.HttpClientUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.Id;

/**
 * 接口层 Created on 2020-8-28 16:35:05
 */
@RequestMapping(value = "/customerInfo")
@RestController
public class CustomerInfoController {


    @Value("${kuaidaogroup.sys.domain}")
    private String kuaidaoGroupDomain;

    @Resource
    private CustomerInfoFeignClient customerInfoFeignClient;

    @Resource
    private ClueCustomerFeignClient clueCustomerFeignClient;


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
        //
        JSONObject data = (JSONObject) jsonObject.get("data");

        data.put("cusName", "");
        //查询客户名称
        JSONResult<CustomerInfoDTO> customerByImID = customerInfoFeignClient.findCustomerByImID(idEntity);
        if (customerByImID != null && customerByImID.getCode().equals(JSONResult.SUCCESS) && customerByImID.getData() != null) {
            CustomerInfoDTO customerInfoDTO = customerByImID.getData();
            if (customerInfoDTO != null && customerInfoDTO.getClueId() != null && customerInfoDTO.getClueId() != 0) {
                IdEntityLong idEntityLong = new IdEntityLong();
                idEntityLong.setId(Long.parseLong(idEntity.getId()));
                JSONResult<CustomerClueDTO> customerClueDTOJSONResult = clueCustomerFeignClient.findNameById(idEntityLong);
                if (customerClueDTOJSONResult != null && customerClueDTOJSONResult.getCode().equals(JSONResult.SUCCESS) && customerClueDTOJSONResult.getData() != null && StringUtils.isNotBlank(customerClueDTOJSONResult.getData().getCusName())) {
                    data.put("cusName", customerClueDTOJSONResult.getData().getCusName());
                }
            }
        }
        return new JSONResult<>().success(data);

    }
}
