package com.kuaidao.manageweb.feign.dictionary;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.dictionary.DictionaryItemAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import feign.hystrix.FallbackFactory;

/**
 *
 * 功能描述: 数据字典--词条
 * 
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "sys-service-ooo1", path = "/sys/DictionaryItem",
        fallbackFactory = DictionaryItemFeignClient.HystrixClientFallback.class)
public interface DictionaryItemFeignClient {


    @PostMapping("/saveDictionaryItem")
    public JSONResult saveDictionaryItem(
            @Valid @RequestBody DictionaryItemAddAndUpdateDTO DictionaryDTO);

    @PostMapping("/updateDictionaryItem")
    public JSONResult updateDictionaryItem(
            @Valid @RequestBody DictionaryItemAddAndUpdateDTO DictionaryItemDTO);

    @PostMapping("/deleteDictionaryItem")
    public JSONResult deleteDictionaryItem(@RequestBody IdEntity idEntity);


    @PostMapping("/deleteDictionaryItems")
    public JSONResult deleteDictionaryItem(@RequestBody String ids);

    @PostMapping("/queryDictionaryItems")
    public JSONResult<PageBean<DictionaryItemRespDTO>> queryDictionaryItem(
            @RequestBody DictionaryItemQueryDTO queryDTO);

    @PostMapping("/queryDictionaryItem")
    public JSONResult<DictionaryItemRespDTO> queryDictionaryOneItem(@RequestBody IdEntity idEntity);

    @PostMapping("/queryDictionaryItemsByGroupCode")
    public JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode(String groupCode);

    @PostMapping("/queryTeleMyCusCustomerStatus")
    public JSONResult<List<DictionaryItemRespDTO>> queryTeleMyCusCustomerStatus(String groupCode);

    @Component
    static class HystrixClientFallback implements FallbackFactory<DictionaryItemFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public DictionaryItemFeignClient create(Throwable cause) {
            return new DictionaryItemFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                            SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

                @Override
                public JSONResult saveDictionaryItem(DictionaryItemAddAndUpdateDTO DictionaryDTO) {
                    return fallBackError("新增词条失败");
                }

                @Override
                public JSONResult updateDictionaryItem(
                        DictionaryItemAddAndUpdateDTO DictionaryItemDTO) {
                    return fallBackError("更新词条失败");
                }

                @Override
                public JSONResult deleteDictionaryItem(IdEntity idEntity) {
                    return fallBackError("删除词条失败");
                }

                @Override
                public JSONResult deleteDictionaryItem(String ids) {
                    return fallBackError("批量删除词条失败");
                }

                @Override
                public JSONResult<PageBean<DictionaryItemRespDTO>> queryDictionaryItem(
                        DictionaryItemQueryDTO queryDTO) {
                    return fallBackError("查询词条失败");
                }


                @Override
                public JSONResult<DictionaryItemRespDTO> queryDictionaryOneItem(IdEntity idEntity) {
                    return fallBackError("获取词条详细信息失败");
                }

                @Override
                public JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode(
                        String groupCode) {
                    return fallBackError("通过GroupCode获取词条");
                }

                @Override
                public JSONResult<List<DictionaryItemRespDTO>> queryTeleMyCusCustomerStatus(
                        String groupCode) {
                    return fallBackError("通过GroupCode获取词条");
                }

            };
        }



    }

}
