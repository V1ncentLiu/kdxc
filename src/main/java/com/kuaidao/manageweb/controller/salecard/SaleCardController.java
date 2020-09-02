package com.kuaidao.manageweb.controller.salecard;

import com.alibaba.fastjson.JSON;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.dto.salecard.SaleCardReqDto;
import com.kuaidao.custservice.dto.salecard.SaleCardRespDto;
import com.kuaidao.manageweb.controller.dictionary.DictionaryController;
import com.kuaidao.manageweb.feign.dictionary.DictionaryFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.salecard.SaleCardFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 接口层
 * Created  on 2020-9-1 10:42:23
 */
@Controller
@RequestMapping("/saleCard")
public class SaleCardController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SaleCardFeignClient saleCardFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;


    /**
     * 根据类型查询  根据顾问ID查询
     *
     * @param telSaleId
     * @return
     */
    @RequestMapping(value = "/getSaleCard")
    @ResponseBody
    public JSONResult<SaleCardRespDto> getSaleCard(Long telSaleId, ModelMap modelMap) {
        logger.info("manager-web SaleCardController queryById:param{}", telSaleId);
        SaleCardReqDto saleCardReqDto = new SaleCardReqDto();
        saleCardReqDto.setTeleSaleId(telSaleId);
        saleCardReqDto.setQuerySource(2);
        return saleCardFeignClient.queryById(saleCardReqDto);
    }

    @RequestMapping(value = "/getDictionaryRank")
    @ResponseBody
    public JSONResult<List<DictionaryItemRespDTO>> getDictionaryRank() {
       return  dictionaryItemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.SALERANKDEATILS.getCode());
    }

    @RequestMapping(value = "/getDictionaryEvaluation")
    @ResponseBody
    public JSONResult<List<DictionaryItemRespDTO>> getDictionaryEvaluation() {
        return  dictionaryItemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.USEREVALUATIONDEATILS.getCode());

    }



    /**
     * 根据类型查询  根据顾问ID查询
     *
     * @param saleCardReqDto
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    @ResponseBody
    public JSONResult<Boolean> saveOrUpdate( SaleCardReqDto saleCardReqDto) {
        logger.info("custservice SaleCardController saveOrUpdate:param{}", JSON.toJSONString(saleCardReqDto));
        JSONResult<Boolean> jsonResult = checkParam(saleCardReqDto);
        if (jsonResult.getCode().equals(JSONResult.FAIL)) {
            return jsonResult;
        }
        long userId = getUserId();
        if (saleCardReqDto.getId() != null) {
            saleCardReqDto.setUpdateUser(userId);
        } else {
            saleCardReqDto.setCreateUser(userId);
        }
        return saleCardFeignClient.saveOrUpdate(saleCardReqDto);
    }

    /**
     * 校验入参
     *
     * @param saleCardReqDto
     * @return
     */
    private JSONResult<Boolean> checkParam(SaleCardReqDto saleCardReqDto) {
        if (saleCardReqDto.getTeleSaleId() == null) {
            new JSONResult<Boolean>().fail("-1", "客户ID不能为空");
        }
        if (saleCardReqDto.getDictionaryRankId() == null) {
            new JSONResult<Boolean>().fail("-1", "字典表职级不能为空");
        }
        if (saleCardReqDto.getDictionaryEvaluationId() == null) {
            new JSONResult<Boolean>().fail("-1", "字典表客户评价不能为空");
        }
        if (saleCardReqDto.getServiceNum() == null) {
            new JSONResult<Boolean>().fail("-1", "服务年限不能为空");
        }

        if (saleCardReqDto.getServiceScore() == null) {
            new JSONResult<Boolean>().fail("-1", "综合评分不能为空");
        }
        return new JSONResult<Boolean>().success(null);
    }

    /**
     * 获取当前登录账号ID
     *
     * @return
     */
    private long getUserId() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user.getId();
    }

}
