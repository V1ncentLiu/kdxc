package com.kuaidao.manageweb.controller.im;

import com.kuaidao.aggregation.dto.clue.IMSubmitQueryDTO;
import com.kuaidao.aggregation.dto.es.EsQueryDTO;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.custservice.dto.custservice.CustomerInfoDTO;
import com.kuaidao.custservice.dto.submitCust.SubmitCustsDTO;
import com.kuaidao.manageweb.feign.im.CustomerInfoFeignClient;
import com.kuaidao.manageweb.feign.im.ImSubmitCustomerRecordClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/im")
public class ImChartListAndSubmitCustController {


    @Autowired
    CustomerInfoFeignClient customerInfoFeignClient;

    @Autowired
    ImSubmitCustomerRecordClient submitCustomerRecordClient;

    /**
     * 顾问提交资源
     */
    @PostMapping("/submit")
    @ResponseBody
    public JSONResult<Long> submit(@RequestBody SubmitCustsDTO submitCusts) {
        return submitCustomerRecordClient.submit(submitCusts);
    }

    /**
     * 查询提交客户列表
     */
    @PostMapping("/submitCustList")
    @ResponseBody
    public JSONResult<PageBean<IMSubmitQueryDTO>> submitCustList(@RequestBody EsQueryDTO submitQuery) {
        return customerInfoFeignClient.costomerList(submitQuery);
    }

    /**
     * 查询客户咨询品牌、客户是否提交+clueId接口
     */
    @PostMapping("/brandAndIssubmit")
    @ResponseBody
    public JSONResult<List<CustomerInfoDTO>> brandAndIssubmit(@RequestBody IdListReq ids) {
        JSONResult<List<CustomerInfoDTO>> result = customerInfoFeignClient.brandAndIssubmit(ids);
        return result;
    }
}
