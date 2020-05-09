package com.kuaidao.manageweb.feign.dictionary;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.*;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 *
 * 功能描述: 
 *      数据字典
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service",path="/sys/dictionary",fallback = DictionaryFeignClient.HystrixClientFallback.class)
public interface DictionaryFeignClient {


    @PostMapping("/saveDictionary")
    public JSONResult saveDictionary(@RequestBody DictionaryAddAndUpdateDTO DictionaryDTO);

    @PostMapping("/updateDictionary")
    public JSONResult updateDictionary(@RequestBody DictionaryAddAndUpdateDTO DictionaryDTO);

    @PostMapping("/findByPrimaryKey")
    public JSONResult findByPrimaryKeyDictionary(@RequestBody IdEntity idEntity);

    @PostMapping("/deleteDictionary")
    public JSONResult deleteDictionary(@RequestBody IdEntity idEntity);

    @PostMapping("/deleteDictionarys")
    public JSONResult deleteDictionarys(@RequestBody String ids);

    @PostMapping("/queryDictionary")
    public JSONResult<PageBean<DictionaryRespDTO>> queryDictionary(@RequestBody DictionaryQueryDTO queryDTO);


    @Component
    static class HystrixClientFallback implements DictionaryFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult saveDictionary(DictionaryAddAndUpdateDTO DictionaryDTO) {
            return fallBackError("新增数据字典失败");
        }

        @Override
        public JSONResult updateDictionary(DictionaryAddAndUpdateDTO DictionaryDTO) {
            return fallBackError("更新数据字典失败");
        }

        @Override
        public JSONResult findByPrimaryKeyDictionary(IdEntity idEntity) {
            return fallBackError("查询数据字典失败");
        }

        @Override
        public JSONResult deleteDictionary(IdEntity idEntity) {
            return fallBackError("删除数据字典失败");
        }

        @Override
        public JSONResult deleteDictionarys(String ids) {
            return fallBackError("批量删除数据字典失败");
        }

        @Override
        public JSONResult<PageBean<DictionaryRespDTO>> queryDictionary(DictionaryQueryDTO queryDTO) {
            return fallBackError("查询数据字典失败");
        }
    }

}
