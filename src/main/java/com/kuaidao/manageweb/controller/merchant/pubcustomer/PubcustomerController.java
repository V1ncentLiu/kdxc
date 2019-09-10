package com.kuaidao.manageweb.controller.merchant.pubcustomer;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;

import com.kuaidao.manageweb.feign.merchant.publiccustomer.PubcustomerFeignClient;
import com.kuaidao.merchant.dto.pubcusres.ClueQueryParamDTO;
import com.kuaidao.merchant.dto.pubcusres.PublicCustomerResourcesRespDTO;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yangbiao
 * @Date: 2019/2/11 15:08
 * @Description: 公共客户资源-商家端
 */
@Controller
@RequestMapping("/merchant/pubcustomer")
public class PubcustomerController {

    private static Logger logger = LoggerFactory.getLogger(PubcustomerController.class);

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    private PubcustomerFeignClient pubcustomerFeignClient;

    /**
     * 分页查询
     */
    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {
        return "merchant/pubcustomer/publicCustomer";
    }

  /**
   * 资源领取
   */
  


  @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(
            @RequestBody ClueQueryParamDTO dto) {
        return pubcustomerFeignClient.queryListPage(dto);
    }


}
