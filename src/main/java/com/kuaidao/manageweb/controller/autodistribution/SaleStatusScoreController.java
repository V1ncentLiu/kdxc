package com.kuaidao.manageweb.controller.autodistribution;


import com.kuaidao.businessconfig.dto.scoreset.SaleScoreSetDTO;
import com.kuaidao.businessconfig.dto.scoreset.SaleScoreSetParam;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.scoreset.SaleScoreFegin;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/salestatus")
public class SaleStatusScoreController {

    @Autowired
    SaleScoreFegin saleScoreFegin;

    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    @RequestMapping("/page")
    public String listPage(HttpServletRequest request) {
        JSONResult<List<DictionaryItemRespDTO>> businessLineJR = dictionaryItemFeignClient
                .queryDicItemsByGroupCode(DicCodeEnum.BUSINESS_LINE.getCode());
        request.setAttribute("businessLineList", businessLineJR.getData());
        return "assignrule/telemarketingStatusScore";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value = "/queryPage")
    public JSONResult<PageBean<SaleScoreSetDTO>> queryPage(@RequestBody SaleScoreSetParam param){
        return saleScoreFegin.queryPage(param);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value ="/insert")
    public JSONResult<Boolean> insert(@RequestBody SaleScoreSetDTO dto ){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setCreateUser(curLoginUser.getId());
        return saleScoreFegin.insert(dto);
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value ="/update")
    public JSONResult<Boolean> update(@RequestBody SaleScoreSetDTO dto ){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setUpdateUser(curLoginUser.getId());
        return saleScoreFegin.update(dto);
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value ="/delete")
    public JSONResult<Boolean> delete(@RequestBody IdListLongReq idListReq ){
        return saleScoreFegin.delete(idListReq);
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value ="/queryOne")
    public JSONResult<SaleScoreSetDTO> queryOne(@RequestBody IdEntityLong idEntity){
        return saleScoreFegin.queryOne(idEntity);
    }


}