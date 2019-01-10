package com.kuaidao.manageweb.feign.dictionary;

import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * 功能描述:
 *      数据字典--词条
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service",path="/sys/DictionaryItem",fallback = DictionaryItemFeignClient.HystrixClientFallback.class)
public interface DictionaryItemFeignClient {


    @PostMapping("/saveDictionaryItem")
    public JSONResult saveDictionaryItem(@Valid @RequestBody DictionaryItemAddAndUpdateDTO DictionaryDTO);

    @PostMapping("/updateDictionaryItem")
    public JSONResult updateDictionaryItem(@Valid  @RequestBody DictionaryItemAddAndUpdateDTO DictionaryItemDTO);

    @PostMapping("/deleteDictionaryItem")
    public JSONResult deleteDictionaryItem(@RequestBody IdEntity idEntity);


    @PostMapping("/deleteDictionaryItems")
    public JSONResult deleteDictionaryItem(@RequestBody String ids);

    @PostMapping("/queryDictionaryItems")
    public JSONResult<PageBean<DictionaryItemRespDTO>> queryDictionaryItem(@RequestBody DictionaryItemQueryDTO queryDTO);

    @PostMapping("/queryDictionaryItem")
    public JSONResult<DictionaryItemRespDTO> queryDictionaryOneItem(@RequestBody IdEntity idEntity);

    @Component
    static class HystrixClientFallback implements DictionaryItemFeignClient {


        @Override
        public JSONResult saveDictionaryItem(DictionaryItemAddAndUpdateDTO DictionaryDTO) {
            return null;
        }

        @Override
        public JSONResult updateDictionaryItem(DictionaryItemAddAndUpdateDTO DictionaryItemDTO) {
            return null;
        }

        @Override
        public JSONResult deleteDictionaryItem(IdEntity idEntity) {
            return null;
        }

        @Override
        public JSONResult deleteDictionaryItem(String ids) {
            return null;
        }

        @Override
        public JSONResult<PageBean<DictionaryItemRespDTO>> queryDictionaryItem(DictionaryItemQueryDTO queryDTO) {
            return null;
        }


        @Override
        public JSONResult<DictionaryItemRespDTO> queryDictionaryOneItem(IdEntity idEntity) {
            return null;
        }
    }

}
