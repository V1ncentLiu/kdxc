package com.kuaidao.manageweb.feign.scoreset;

import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitDTO;
import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitPageParam;
import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitReq;
import com.kuaidao.businessconfig.dto.rule.UserScoreRuleDTO;
import com.kuaidao.businessconfig.dto.scoreset.ClueScoreSetDTO;
import com.kuaidao.businessconfig.dto.scoreset.ClueScoreSetParam;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 资源分配量
 * 
 * @author zxy
 *
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/cluescore",
        fallback = ClueScoreFegin.HystrixClientFallback.class)
public interface ClueScoreFegin {

     @RequestMapping(method = RequestMethod.POST,value ="/queryPage")
    public JSONResult< PageBean<ClueScoreSetDTO>> queryPage(@RequestBody ClueScoreSetParam param);

     @RequestMapping(method = RequestMethod.POST,value ="/queryByParam")
    public JSONResult<List<ClueScoreSetDTO>> queryByParam(@RequestBody ClueScoreSetParam param);

     @RequestMapping(method = RequestMethod.POST,value ="/insert")
    public JSONResult<Boolean> insert(@RequestBody ClueScoreSetDTO dto );

     @RequestMapping(method = RequestMethod.POST,value ="/update")
    public JSONResult<Boolean> update(@RequestBody ClueScoreSetDTO dto );

     @RequestMapping(method = RequestMethod.POST,value ="/delete")
    public JSONResult<Boolean> delete(@RequestBody IdListLongReq idListReq );

     @RequestMapping(method = RequestMethod.POST,value ="/queryOne")
    public JSONResult<ClueScoreSetDTO> queryOne(@RequestBody IdEntityLong idEntity);


    @Component
    static class HystrixClientFallback implements ClueScoreFegin {

        private static Logger logger = LoggerFactory.getLogger(ClueScoreFegin.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult< PageBean<ClueScoreSetDTO>> queryPage(@RequestBody ClueScoreSetParam param) {
            return fallBackError("资源评分-分页接口");
        }

        @Override
        public JSONResult<List<ClueScoreSetDTO>> queryByParam(ClueScoreSetParam param) {
            return fallBackError("资源评分-agg接口");
        }

        @Override
        public JSONResult<Boolean> insert(ClueScoreSetDTO dto) {
            return fallBackError("资源评分-插入接口");
        }

        @Override
        public JSONResult<Boolean> update(ClueScoreSetDTO dto) {
            return fallBackError("资源评分-更新接口");
        }

        @Override
        public JSONResult<Boolean> delete(IdListLongReq idListReq) {
            return fallBackError("资源评分-删除接口");
        }

        @Override
        public JSONResult<ClueScoreSetDTO> queryOne(IdEntityLong idEntity) {
            return fallBackError("资源评分-回显接口");
        }
    }

}
