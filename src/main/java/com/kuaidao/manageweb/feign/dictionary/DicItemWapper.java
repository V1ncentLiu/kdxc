package com.kuaidao.manageweb.feign.dictionary;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.netflix.discovery.converters.Auto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DicItemWapper {
    @Auto
    DictionaryItemFeignClient dictionaryItemFeignClient;

    public List<DictionaryItemRespDTO> findDicItemsByCode(String code){
        JSONResult<List<DictionaryItemRespDTO>> result = dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        return result.data();
    }
}
