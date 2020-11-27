package com.kuaidao.manageweb.controller.customer;

import com.alibaba.fastjson.JSONObject;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.dto.custservice.Coupon;
import com.kuaidao.custservice.dto.custservice.CustomerInfoDTO;
import com.kuaidao.manageweb.controller.customefield.CustomFieldController;
import com.kuaidao.manageweb.feign.clue.ClueCustomerFeignClient;
import com.kuaidao.manageweb.feign.im.CustomerInfoFeignClient;
import com.kuaidao.manageweb.util.HttpClientUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口层 Created on 2020-8-28 16:35:05
 */
@RequestMapping(value = "/customerInfo")
@RestController
public class CustomerInfoController {
    private static Logger logger = LoggerFactory.getLogger(CustomFieldController.class);


    @Value("${kuaidaogroup.sys.domain}")
    private String kuaidaoGroupDomain;

    @Value("${kuaidaogroup.activityad.domain}")
    private String kuaidaoGroupActivityadDomain;

    @Resource
    private CustomerInfoFeignClient customerInfoFeignClient;

    @Resource
    private ClueCustomerFeignClient clueCustomerFeignClient;


    @PostMapping(value = "/customerInfoByIm")
    public JSONResult customerInfoByIm(@RequestBody IdEntity idEntity) {

        try {
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
            logger.info("CustomerInfoController customerInfoByIm={}",data);
            //查询客户名称
            this.setCusName(idEntity,data);
            //设置优惠券信息
            logger.info("222222222222222222222222222");
            logger.info("wewewewewewewe:{}",data);
            logger.info("wewewewewewewe:{}",(String)data.get("phoneNumber"));
            this.setCouponInfo((String)data.get("phoneNumber"),data);
            logger.info("222222333333333333333333333");

            return new JSONResult<>().success(data);
        } catch (RuntimeException e) {
            logger.error("CustomerInfoController error={}",e.getMessage());
        }
        return new JSONResult<>().fail("-1","失败");
    }

    /**
     * 设置优惠券
     */
    public void setCouponInfo(String phone, JSONObject jsonObject){
        Coupon couponIM = getCouponIM(phone);
        jsonObject.put("coupon",couponIM);
    }

    /**
     * 获取优惠券
     */
    public Coupon getCouponIM(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return new Coupon();
        }
        logger.info("dddddddddddd2222222222222222");
        Map map = new HashMap();
        map.put("phoneNumber", phone);
        JSONObject jsonObject = HttpClientUtils.httpPost(kuaidaoGroupActivityadDomain + "/v1.0/app/newUserInfo/info", map);
        if(jsonObject == null){
            return new Coupon();
        }
        logger.info("dddddddddddd:{}",jsonObject);
        Coupon remoteData = JSONObject.parseObject(
                null == jsonObject.get("data") ? null :jsonObject.get("data").toString(),
                Coupon.class);

        return remoteData;
    }


    /**
     * 获取客户姓名
     * @param idEntity
     * @param data
     */
    private void setCusName(IdEntity idEntity, JSONObject data ){
        JSONResult<CustomerInfoDTO> customerByImID = customerInfoFeignClient.findCustomerByImID(idEntity);
        if (customerByImID != null && customerByImID.getCode().equals(JSONResult.SUCCESS) && customerByImID.getData() != null) {
            logger.info("CustomerInfoController customerByImID={}",customerByImID);
            CustomerInfoDTO customerInfoDTO = customerByImID.getData();
            if (customerInfoDTO != null && customerInfoDTO.getClueId() != null && customerInfoDTO.getClueId() != 0) {
                IdEntityLong idEntityLong = new IdEntityLong();
                idEntityLong.setId(customerInfoDTO.getClueId());
                JSONResult<CustomerClueDTO> customerClueDTOJSONResult = clueCustomerFeignClient.findNameById(idEntityLong);
                if (customerClueDTOJSONResult != null
                        && customerClueDTOJSONResult.getCode().equals(JSONResult.SUCCESS) && customerClueDTOJSONResult.getData() != null
                        && StringUtils.isNotBlank(customerClueDTOJSONResult.getData().getCusName())) {
                    logger.info("CustomerInfoController customerClueDTOJSONResult={}",customerClueDTOJSONResult);
                    data.put("cusName", customerClueDTOJSONResult.getData().getCusName());
                }
            }
        }
    }



}
