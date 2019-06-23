package com.kuaidao.manageweb.controller.dictionary;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;

/**
 * @author : yangbiao
 * @Date: 2019/1/2 15:14
 * @Description: 数据字典
 */

@Controller
@RequestMapping("/dictionary/Dictionary")
public class DictionaryController {

    private static Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    @Autowired
    DictionaryFeignClient dictionaryFeignClient;

    @RequestMapping("/diclistPage")
    public String listPage() {
        logger.info(
                "--------------------------------------跳转到列表页面-----------------------------------------------");
        return "dictionary/dicListPage";
    }


    /**
     * 数据字典-列表查询
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryDictionary")
    @ResponseBody
    public JSONResult<PageBean<DictionaryRespDTO>> queryDictionary(
            @RequestBody DictionaryQueryDTO queryDTO) {
        JSONResult<PageBean<DictionaryRespDTO>> listJSONResult =
                dictionaryFeignClient.queryDictionary(queryDTO);
        return listJSONResult;
    }

    /**
     * web端进行的是非业务逻辑。
     */
    @RequiresPermissions("dictionary:add")
    @LogRecord(description = "字典新增", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.DICTIONARY_MANAGEMENT)
    @RequestMapping("/saveDictionary")
    @ResponseBody
    public JSONResult saveDictionary(@Valid @RequestBody DictionaryAddAndUpdateDTO dictionaryDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return validateParam(result);
        }
        return dictionaryFeignClient.saveDictionary(dictionaryDTO);
    }


    @RequiresPermissions("dictionary:update")
    @LogRecord(description = "字典更新", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.DICTIONARY_MANAGEMENT)
    @RequestMapping("/updateDictionary")
    @ResponseBody
    public JSONResult updateDictionary(@Valid @RequestBody DictionaryAddAndUpdateDTO dictionaryDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return validateParam(result);
        }
        return dictionaryFeignClient.updateDictionary(dictionaryDTO);
    }

    @RequestMapping("/findByPrimaryKey")
    @ResponseBody
    public JSONResult findByPrimaryKeyDictionary(@RequestBody IdEntity idEntity) {
        return dictionaryFeignClient.findByPrimaryKeyDictionary(idEntity);
    }

    @LogRecord(description = "字典删除", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.DICTIONARY_MANAGEMENT)
    @RequestMapping("/deleteDictionary")
    @ResponseBody
    public JSONResult deleteDictionary(@RequestBody IdEntity idEntity) {
        return dictionaryFeignClient.deleteDictionary(idEntity);
    }

    @LogRecord(description = "字典删除-批量删除", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.DICTIONARY_MANAGEMENT)
    @RequiresPermissions("dictionary:delete")
    @RequestMapping("/deleteDictionarys")
    @ResponseBody
    public JSONResult deleteDictionarys(@RequestBody Map<String, String> map) {
        return dictionaryFeignClient.deleteDictionarys(map.get("ids"));
    }

    /**
     * 错误参数检验
     * 
     * @param result
     * @return
     */
    private JSONResult validateParam(BindingResult result) {
        List<ObjectError> list = result.getAllErrors();
        for (ObjectError error : list) {
            logger.error("参数校验失败：{},错误信息：{}", error.getArguments(), error.getDefaultMessage());
        }
        return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
    }
}
