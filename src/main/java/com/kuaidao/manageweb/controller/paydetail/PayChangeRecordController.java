package com.kuaidao.manageweb.controller.paydetail;

import com.kuaidao.manageweb.feign.paydetail.PayChangeRecordFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kuaidao.aggregation.dto.paydetail.PayChangeRecordDTO;
import com.kuaidao.aggregation.dto.paydetail.PayChangeRecordParamDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 修改付款明细操作记录 Created on 2019-11-25 16:45:56
 */
@RestController
@RequestMapping("/payChangRecord")
public class PayChangeRecordController {

    @Autowired
    private PayChangeRecordFeignClient payChangeRecordFeignClient;
    /**
     * 付款明细操作记录列表
     * 
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/11/26 15:37
     * @since: 1.0.0
     **/
    @PostMapping("/getPageList")
    public JSONResult<PageBean<PayChangeRecordDTO>> getPageList(@RequestBody PayChangeRecordParamDTO payChangRecordParamDTO) {
        JSONResult<PageBean<PayChangeRecordDTO>> jsonResult =  payChangeRecordFeignClient.getPageList(payChangRecordParamDTO);
        return new JSONResult<PageBean<PayChangeRecordDTO>>().success(jsonResult);
    }
}
