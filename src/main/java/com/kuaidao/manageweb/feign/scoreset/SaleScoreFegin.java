package com.kuaidao.manageweb.feign.scoreset;

import com.kuaidao.businessconfig.dto.rule.UserScoreRuleDTO;
import com.kuaidao.businessconfig.dto.scoreset.ClueScoreSetDTO;
import com.kuaidao.businessconfig.dto.scoreset.ClueScoreSetParam;
import com.kuaidao.businessconfig.dto.scoreset.SaleScoreSetDTO;
import com.kuaidao.businessconfig.dto.scoreset.SaleScoreSetParam;
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
 * 顾问分配量
 * 
 * @author zxy
 *
 */
@FeignClient(name = "business-config-service", path = "/businessConfig/salescoreset",
        fallback = SaleScoreFegin.HystrixClientFallback.class)
public interface SaleScoreFegin {


    @RequestMapping(method = RequestMethod.POST,value = "/queryPage")
    public JSONResult<PageBean<SaleScoreSetDTO>> queryPage(@RequestBody SaleScoreSetParam param);

     @RequestMapping(method = RequestMethod.POST,value ="/queryByParam")
    public JSONResult< List<SaleScoreSetDTO>> queryByParam(@RequestBody SaleScoreSetParam param);

     @RequestMapping(method = RequestMethod.POST,value ="/insert")
    public JSONResult<Boolean> insert(@RequestBody SaleScoreSetDTO dto );

     @RequestMapping(method = RequestMethod.POST,value ="/update")
    public JSONResult<Boolean> update(@RequestBody SaleScoreSetDTO dto );

     @RequestMapping(method = RequestMethod.POST,value ="/delete")
    public JSONResult<Boolean> delete(@RequestBody IdListLongReq idListReq );

     @RequestMapping(method = RequestMethod.POST,value ="/queryOne")
    public JSONResult<SaleScoreSetDTO> queryOne(@RequestBody IdEntityLong idEntity);

    @Component
    static class HystrixClientFallback implements SaleScoreFegin {

        private static Logger logger = LoggerFactory.getLogger(SaleScoreFegin.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<SaleScoreSetDTO>> queryPage(SaleScoreSetParam param) {
            return fallBackError("顾问评分-分页接口");
        }

        @Override
        public JSONResult<List<SaleScoreSetDTO>> queryByParam(SaleScoreSetParam param) {
            return fallBackError("顾问评分-agg接口");
        }

        @Override
        public JSONResult<Boolean> insert(SaleScoreSetDTO dto) {
            return fallBackError("顾问评分-插入接口");
        }

        @Override
        public JSONResult<Boolean> update(SaleScoreSetDTO dto) {
            return fallBackError("顾问评分-更新接口");
        }

        @Override
        public JSONResult<Boolean> delete(IdListLongReq idListReq) {
            return fallBackError("顾问评分-删除接口");
        }

        @Override
        public JSONResult<SaleScoreSetDTO> queryOne(IdEntityLong idEntity) {
            return fallBackError("顾问评分-回显接口");
        }


      
    }

}
