package com.kuaidao.manageweb.controller.autodistribution;


import com.kuaidao.businessconfig.dto.automodel.AutoDisModelDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.autodismodel.AutoDisModelFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/autodistribution")
public class AutoDistributionController {

    @Autowired
    AutoDisModelFeignClient autoDisModelFeignClient;

    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    @RequestMapping("/page")
    public String pageIndex(HttpServletRequest request) {
        log.info("====================跳转列表页面==================");

        JSONResult<List<DictionaryItemRespDTO>> businessLineJR = dictionaryItemFeignClient
                .queryDicItemsByGroupCode(DicCodeEnum.BUSINESS_LINE.getCode());
        request.setAttribute("businessLineList", businessLineJR.getData());

        return "assignrule/autoDistributionSet";
    }

    /**
     *  页面流程
     */
    @ResponseBody
    @PostMapping(value = "/update")
    public JSONResult<Boolean> update(@RequestBody AutoDisModelDTO dto, BindingResult result){
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setUpdateUser(curLoginUser.getId());
        return autoDisModelFeignClient.update(dto);
    }

    @ResponseBody
    @PostMapping(value = "/insert")
    public JSONResult<Boolean> insert(@RequestBody AutoDisModelDTO dto ,BindingResult result){
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setCreateUser(curLoginUser.getId());
        return autoDisModelFeignClient.insert(dto);
    }


    @ResponseBody
    @PostMapping(value = "/queryByParams")
    public JSONResult<AutoDisModelDTO> queryByParams(@RequestBody AutoDisModelDTO dto){
        return autoDisModelFeignClient.queryByParams(dto);
    }

}
