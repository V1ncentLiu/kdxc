package com.kuaidao.manageweb.controller.dictionary;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import com.kuaidao.common.util.SortUtils;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;

/**
 * @author  yangbiao
 * @Date: 2019/1/2 15:14
 * @Description:
 *      数据字典-词条
 */

@Controller
@RequestMapping("/dictionary/DictionaryItem")
public class DictionaryItemController {

    private static Logger logger = LoggerFactory.getLogger(DictionaryItemController.class);

    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;


    @SuppressWarnings("unchecked")
    @PostMapping("/dicItemsByGroupCode")
    @ResponseBody
    public JSONResult<List<DictionaryItemRespDTO>> itemsByGroupCode(@RequestBody DictionaryItemQueryDTO queryDTO) {
        JSONResult<List<DictionaryItemRespDTO>> result = new JSONResult<>();
        result = dictionaryItemFeignClient.queryDicItemsByGroupCode(queryDTO.getGroupCode());
        return result;
    }

    /**
     * 字典结果集根据中文排序
     * 
     * @param queryDTO
     * @return
     */
    @ResponseBody
    @PostMapping("/dicItemsByGroupCodeSort")
    public JSONResult<List<DictionaryItemRespDTO>> itemsByGroupCodeSort(@RequestBody DictionaryItemQueryDTO queryDTO) {
        JSONResult<List<DictionaryItemRespDTO>> result = new JSONResult<>();
        result = dictionaryItemFeignClient.queryDicItemsByGroupCode(queryDTO.getGroupCode());
        if (result.getCode().equals(JSONResult.SUCCESS)) {
            // 对结果集进行排序
            List<DictionaryItemRespDTO> resultList = SortUtils.sortList(result.getData(), "name");
            result.setData(resultList);
        }
        return result;
    }
    @PostMapping("/queryTeleMyCusCustomerStatus")
    @ResponseBody
    public JSONResult<List<DictionaryItemRespDTO>> queryTeleMyCusCustomerStatus(@RequestBody DictionaryItemQueryDTO queryDTO){
        JSONResult result = dictionaryItemFeignClient.queryTeleMyCusCustomerStatus(queryDTO.getGroupCode());
        return result;
    }


    @RequestMapping("/itemListPage")
    public String itemListPage(HttpServletRequest request){
        logger.info("--------------------------------------跳转到词条页面-----------------------------------------------");
        String dicid = request.getParameter("dicid");
        String groupCode = request.getParameter("groupCode");
//        DictionaryItemQueryDTO dto = new DictionaryItemQueryDTO();
//        dto.setDicId(Long.valueOf(dicid));
//        dto.setPageNum(1);
//        dto.setPageSize(20);
//        JSONResult<PageBean<DictionaryItemRespDTO>> list = dictionaryItemFeignClient.queryDictionaryItem(dto);
//        if (list!=null) {
//            request.setAttribute("resList",new ArrayList());
//        }else{
//            request.setAttribute("resList",new ArrayList());
//        }
        request.setAttribute("dicid",dicid);
        request.setAttribute("groupCode",groupCode);
        return "dictionary/dicItemListPage";
    }

    /**
     * 数据字典-列表查询
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryDictionaryItems")
    @ResponseBody
    public JSONResult<PageBean<DictionaryItemRespDTO>> queryDictionary(@RequestBody DictionaryItemQueryDTO queryDTO){
        JSONResult<PageBean<DictionaryItemRespDTO>> pageBeanJSONResult = dictionaryItemFeignClient.queryDictionaryItem(queryDTO);
        return pageBeanJSONResult;
    }

    /**
     *  web端进行的是非业务逻辑。
     */
    @LogRecord(description = "新增词条",operationType = LogRecord.OperationType.INSERT,menuName = MenuEnum.DICTIONARY_MANAGEMENT_ITEM)
    @RequiresPermissions("dictionary:update")
    @RequestMapping("/saveDictionaryItem")
    @ResponseBody
    public JSONResult saveDictionary(@Valid @RequestBody DictionaryItemAddAndUpdateDTO dictionaryItemDTO  , BindingResult result){
        if (result.hasErrors()) {
            return validateParam(result);
        }
        return  dictionaryItemFeignClient.saveDictionaryItem(dictionaryItemDTO);
    }
    @LogRecord(description = "更新词条",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.DICTIONARY_MANAGEMENT_ITEM)
    @RequiresPermissions("dictionary:update")
    @RequestMapping("/updateDictionaryItem")
    @ResponseBody
    public JSONResult updateDictionary(@Valid @RequestBody DictionaryItemAddAndUpdateDTO dictionaryDTO , BindingResult result){
        if (result.hasErrors()) {
            return validateParam(result);
        }
        return dictionaryItemFeignClient.updateDictionaryItem(dictionaryDTO);
    }

    @RequestMapping("/findByPrimaryKey")
    @ResponseBody
    public JSONResult<DictionaryItemRespDTO> findByPrimaryKeyDictionary(@RequestBody IdEntity idEntity){
        return dictionaryItemFeignClient.queryDictionaryOneItem(idEntity);
    }

    @LogRecord(description = "删除词条",operationType = LogRecord.OperationType.DELETE,menuName = MenuEnum.DICTIONARY_MANAGEMENT_ITEM)
    @RequiresPermissions("dictionary:update")
    @RequestMapping("/deleteItemDictionarys")
    @ResponseBody
    public JSONResult deleteDictionarys(@RequestBody Map<String, String> map){
        return dictionaryItemFeignClient.deleteDictionaryItem(map.get("ids"));
    }

    /**
     * 错误参数检验
     * @param result
     * @return
     */
    private JSONResult validateParam(BindingResult result) {
        List<ObjectError> list = result.getAllErrors();
        for (ObjectError error : list) {
            logger.error("参数校验失败：{},错误信息：{}", error.getArguments(), error.getDefaultMessage());
        }
        return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
    }

}
