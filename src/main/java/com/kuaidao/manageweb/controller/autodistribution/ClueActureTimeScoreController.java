package com.kuaidao.manageweb.controller.autodistribution;


import com.kuaidao.businessconfig.dto.scoreset.ClueScoreSetDTO;
import com.kuaidao.businessconfig.dto.scoreset.ClueScoreSetParam;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.scoreset.ClueScoreFegin;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/clueacture")
public class ClueActureTimeScoreController {


    @Autowired
    ClueScoreFegin clueScoreFegin;


    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    @RequestMapping("/page")
    public String listPage(HttpServletRequest request) {
        JSONResult<List<DictionaryItemRespDTO>> businessLineJR = dictionaryItemFeignClient
                .queryDicItemsByGroupCode(DicCodeEnum.BUSINESS_LINE.getCode());
        request.setAttribute("businessLineList", businessLineJR.getData());
        return "assignrule/cluesActualTimeScore";
    }

    @RequestMapping(method = RequestMethod.POST,value ="/queryPage")
    public JSONResult<PageBean<ClueScoreSetDTO>> queryPage(@RequestBody ClueScoreSetParam param) {
        return clueScoreFegin.queryPage(param);
    }

    @RequestMapping(method = RequestMethod.POST,value ="/insert")
    public JSONResult<Boolean> insert(@RequestBody ClueScoreSetDTO dto ){

        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setCreateUser(curLoginUser.getId());

        return clueScoreFegin.insert(dto);
    }

    @RequestMapping(method = RequestMethod.POST,value ="/update")
    public JSONResult<Boolean> update(@RequestBody ClueScoreSetDTO dto ){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setUpdateUser(curLoginUser.getId());
        return clueScoreFegin.update(dto);
    }

    @RequestMapping(method = RequestMethod.POST,value ="/delete")
    public JSONResult<Boolean> delete(@RequestBody IdListLongReq idListReq ){
        return clueScoreFegin.delete(idListReq);
    }

    @RequestMapping(method = RequestMethod.POST,value ="/queryOne")
    public JSONResult<ClueScoreSetDTO> queryOne(@RequestBody IdEntityLong idEntity){
        return clueScoreFegin.queryOne(idEntity);
    }

}
