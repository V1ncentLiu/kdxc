package com.kuaidao.manageweb.feign.clue;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.businessconfig.dto.clueTemplate.ClueTemplateDTO;
import com.kuaidao.businessconfig.dto.clueTemplate.ClueTemplatePageParam;
import com.kuaidao.businessconfig.dto.clueTemplate.ClueTemplateReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 资源模板
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/clueTemplate",
        fallback = ClueTemplateFeignClient.HystrixClientFallback.class)
public interface ClueTemplateFeignClient {
    /**
     * 根据id查询资源模板信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/get")
    public JSONResult<ClueTemplateDTO> get(@RequestBody IdEntityLong id);

    /**
     * 根据id查询资源模板信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListLongReq idList);

    /**
     * 查询资源模板集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<ClueTemplateDTO>> list(@RequestBody ClueTemplatePageParam param);

    /**
     * 查询资源模板集合.
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/listNoPage")
    public JSONResult<List<ClueTemplateDTO>> listNoPage(@RequestBody ClueTemplatePageParam param);

    /**
     * 修改资源模板信息
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/update")
    public JSONResult<String> update(@RequestBody ClueTemplateReq req);

    /**
     * 新增资源模板
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult<String> create(@RequestBody ClueTemplateReq req);



    @Component
    static class HystrixClientFallback implements ClueTemplateFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClueTemplateFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<ClueTemplateDTO> get(@RequestBody IdEntityLong id) {
            return fallBackError("根据id查询资源模板信息");
        }

        @Override
        public JSONResult delete(@RequestBody IdListLongReq idList) {
            return fallBackError("删除资源模板信息");
        }


        @Override
        public JSONResult<String> update(@RequestBody ClueTemplateReq req) {
            return fallBackError("修改资源模板信息");
        }

        @Override
        public JSONResult<String> create(@RequestBody ClueTemplateReq req) {
            return fallBackError("新增资源模板");
        }


        @Override
        public JSONResult<PageBean<ClueTemplateDTO>> list(
                @RequestBody ClueTemplatePageParam param) {
            return fallBackError("查询资源模板集合");
        }

        @Override
        public JSONResult<List<ClueTemplateDTO>> listNoPage(ClueTemplatePageParam param) {
            return fallBackError("查询资源模板集合.");
        }



    }


}
