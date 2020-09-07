package com.kuaidao.manageweb.controller.language;


import com.alibaba.fastjson.JSON;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.custservice.dto.language.*;
import com.kuaidao.manageweb.feign.language.CommonLanguageFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
* 接口层
* Created  on 2020-9-1 10:42:23
*/
@RestController
@RequestMapping("/commonLanguage")
public class CommonLanguageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CommonLanguageFeignClient commonLanguageFeignClient;


    /**
     * 更新常用词
     * @param commonLanguageReq
     * @return
     */
    @RequestMapping(value = "/save")
    public JSONResult<Boolean> save(CommonLanguageReqDto commonLanguageReq) {
        commonLanguageReq.setId(null);
        logger.info("manager-web CommonLanguageController saveOrUpdate:param{}", JSON.toJSONString(commonLanguageReq));
        if(commonLanguageReq.getType()==null){
            return new JSONResult<Boolean>().fail("-1","新增常用词类型不能为空");
        }
        if(commonLanguageReq.getComText()==null){
            return new JSONResult<Boolean>().fail("-1","常用词内容不能为空");
        }
        if(commonLanguageReq.getComText().length()>500){
            return new JSONResult<Boolean>().fail("-1","常用语内容不能超过500字");
        }
        commonLanguageReq.setCreateTime(new Date());
        return commonLanguageFeignClient.insert(commonLanguageReq);
    }

    /**
     * 更新常用词
     * @param commonLanguageReq
     * @return
     */
    @RequestMapping(value = "/update")
    public JSONResult<Boolean> update(CommonLanguageReqDto commonLanguageReq) {
        if(commonLanguageReq.getId()==null){
            return new JSONResult<Boolean>().fail("-1","修改操作常用语不能为空");
        }
        if(commonLanguageReq.getComText()==null){
            return new JSONResult<Boolean>().fail("-1","常用词内容不能为空");
        }
        if(commonLanguageReq.getComText().length()>500){
            return new JSONResult<Boolean>().fail("-1","常用语内容不能超过500字");
        }
        commonLanguageReq.setWeight(null);

        logger.info("manager-web CommonLanguageController saveOrUpdate:param{}", JSON.toJSONString(commonLanguageReq));
        commonLanguageReq.setUpdateTime(new Date());
        return commonLanguageFeignClient.update(commonLanguageReq);
    }

    /**
     * 根据ID查处常用词
     * @return
     */
    @RequestMapping(value = "/deleteById")
    public JSONResult<Boolean> deleteById(Long  id) {
        CommonLanguageReqDto commonLanguageReqDto = new CommonLanguageReqDto();
        commonLanguageReqDto.setId(id);
        return commonLanguageFeignClient.deleteById(commonLanguageReqDto);
    }



    /**
     * 更新顺序
     * @return
     */
    @PostMapping(value = "/updateOrder")
    public JSONResult<Boolean> updateOrder(@Valid @RequestBody CommonLanguageOrderReq commonLanguageOrderReq,
                                           BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return commonLanguageFeignClient.updateOrder(commonLanguageOrderReq);

    }

}
