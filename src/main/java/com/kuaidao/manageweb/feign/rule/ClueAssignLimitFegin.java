package com.kuaidao.manageweb.feign.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitDTO;
import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitPageParam;
import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 * 资源分配量
 * 
 * @author zxy
 *
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/clueAssignLimit",
        fallback = ClueAssignLimitFegin.HystrixClientFallback.class)
public interface ClueAssignLimitFegin {


    /**
     * 新增资源分配量
     *
     * @param commentInsertReq
     * @return
     */
    @PostMapping("/create")
    public JSONResult<Long> create(@RequestBody ClueAssignLimitReq clueAssignLimitReq);

    /**
     * 修改资源分配量
     *
     * @param commentInsertReq
     * @return
     */
    @PostMapping("/update")
    public JSONResult<Long> update(@RequestBody ClueAssignLimitReq clueAssignLimitReq);

    /**
     * 根据id查询资源分配量
     *
     * @param commentInsertReq
     * @return
     */
    @PostMapping("/get")
    public JSONResult<ClueAssignLimitDTO> get(@RequestBody IdEntityLong idEntity);

    /**
     * 查询资源分配量列表
     *
     * @param commentInsertReq
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<ClueAssignLimitDTO>> list(
            @RequestBody ClueAssignLimitPageParam pageParam);



    @Component
    static class HystrixClientFallback implements ClueAssignLimitFegin {

        private static Logger logger = LoggerFactory.getLogger(ClueAssignLimitFegin.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Long> create(ClueAssignLimitReq clueAssignLimitReq) {
            return fallBackError("新增资源分配量");
        }

        @Override
        public JSONResult<Long> update(ClueAssignLimitReq clueAssignLimitReq) {
            return fallBackError("修改资源分配量");
        }

        @Override
        public JSONResult<ClueAssignLimitDTO> get(IdEntityLong idEntity) {
            return fallBackError("根据id查询资源分配量");
        }

        @Override
        public JSONResult<PageBean<ClueAssignLimitDTO>> list(ClueAssignLimitPageParam pageParam) {
            return fallBackError("查询资源分配量列表");
        }

    }

}
