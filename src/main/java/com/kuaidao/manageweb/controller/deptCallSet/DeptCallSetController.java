package com.kuaidao.manageweb.controller.deptCallSet;

import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      系统公告
 */

@Controller
@RequestMapping("/deptcallset")
public class DeptCallSetController {

    private static Logger logger = LoggerFactory.getLogger(DeptCallSetController.class);

    @RequestMapping("/deptcallsetPage")
    public String pageIndex(){
        return "deptCellSet/deptCallSetList";
    }


    @RequestMapping("/saveOne")
    @ResponseBody
    public JSONResult insertOne(@Valid @RequestBody DictionaryAddAndUpdateDTO dictionaryDTO  , BindingResult result){
        if (result.hasErrors()) return  CommonUtil.validateParam(result);

//        return  abnormalFeignClient.saveDictionary(dictionaryDTO);
        return new JSONResult();
}

    @RequestMapping("/deleteAbnoramlUser")
    @ResponseBody
    public JSONResult deleteAbnoramlUser(@RequestBody IdEntity idEntity){
//        return abnormalFeignClient.deleteDictionary(idEntity);
        return new JSONResult();
    }

    @PostMapping("/queryAbnoramlUsers")
    @ResponseBody
    public JSONResult<PageBean<DictionaryRespDTO>> queryDictionary(@RequestBody DictionaryQueryDTO queryDTO){
//        JSONResult<PageBean<DictionaryRespDTO>> listJSONResult = dictionaryFeignClient.queryDictionary(queryDTO);
        return new JSONResult();
    }
}
