package com.kuaidao.manageweb.controller.merchant.pubcustomer;

import com.kuaidao.aggregation.dto.pubcusres.ClueQueryParamDTO;
import com.kuaidao.aggregation.dto.pubcusres.PublicCustomerResourcesRespDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;

import com.kuaidao.manageweb.feign.merchant.publiccustomer.PublicCustomerFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.Date;
import java.util.List;
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
 * @Description: 公共客户资源
 */
@Controller
@RequestMapping("/aggregation/publiccustomer")
public class PublicCustomerResourcesController {

    private static Logger logger = LoggerFactory.getLogger(PublicCustomerResourcesController.class);

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    private PublicCustomerFeignClient publicCustomerFeignClient;

    /**
     * 分页查询
     */
    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {

        // 资源类别
        request.setAttribute("clueCategory",Constants.CLUE_CATEGORY);
        // 资源类型
        request.setAttribute("clueType",Constants.CLUE_TYPE);
        // 行业类别
        request.setAttribute("industryCategory",Constants.INDUSTRY_CATEGORY);

        return "merchant/pubcustomer/publicCustomer";
    }

    /**
     * 查询字典表
     *
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }

    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(
            @RequestBody ClueQueryParamDTO dto) {
        return publicCustomerFeignClient.queryListPage(dto);
    }


}
