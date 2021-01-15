package com.kuaidao.manageweb.feign.dictionary;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DicItemWapper {
    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    public List<DictionaryItemRespDTO> findDicItemsByCode(String code){
        JSONResult<List<DictionaryItemRespDTO>> result = dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        return result.data();
    }
}
