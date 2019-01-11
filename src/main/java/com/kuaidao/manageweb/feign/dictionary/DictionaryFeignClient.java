package com.kuaidao.manageweb.feign.dictionary;

import com.kuaidao.common.entity.*;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
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

        @Override
        public JSONResult saveDictionary(DictionaryAddAndUpdateDTO DictionaryDTO) {
            return null;
        }

        @Override
        public JSONResult updateDictionary(DictionaryAddAndUpdateDTO DictionaryDTO) {
            return null;
        }

        @Override
        public JSONResult findByPrimaryKeyDictionary(IdEntity idEntity) {
            return null;
        }

        @Override
        public JSONResult deleteDictionary(IdEntity idEntity) {
            return null;
        }

        @Override
        public JSONResult deleteDictionarys(String ids) {
            return null;
        }

        @Override
        public JSONResult<PageBean<DictionaryRespDTO>> queryDictionary(DictionaryQueryDTO queryDTO) {
            return null;
        }
    }

}
