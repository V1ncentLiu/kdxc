package com.kuaidao.manageweb.feign.client;

import com.kuaidao.callcenter.dto.RonglianClientDTO;
import com.kuaidao.callcenter.dto.RonglianClientInsertReq;
import com.kuaidao.callcenter.dto.RonglianClientResqDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created on: 2019-10-29-20:26
 */
@FeignClient(name = "callcenter-service", path = "/callcenter/ronglianClient", fallback = RonglianFeignClient.HystrixClientFallback.class)
public interface RonglianFeignClient {

    /**
     * 根据登录坐席和用户id查询容联坐席
     * @param ronglianClientDTO
     * @return
     */
    @PostMapping("/queryRonglianClientByLoginName")
    public JSONResult<RonglianClientResqDTO> queryRonglianClientByLoginName(@RequestBody RonglianClientDTO ronglianClientDTO);


    /**
     * 分页查询坐席
     * @param queryClientDTO
     * @return
     */
    @PostMapping("/listRonglianClientPage")
    public JSONResult<PageBean<RonglianClientResqDTO>> listRonglianClientPage(
        @RequestBody RonglianClientDTO queryClientDTO);

    /**
     * 根据id 查询坐席
     * @param idEntity
     * @return
     */
    @PostMapping("/queryRonglianClientById")
    public JSONResult<RonglianClientResqDTO> queryRonglianClientById(@RequestBody IdEntity idEntity);

    /**
     * 添加坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/saveRonglianClient")
    public JSONResult<Boolean> saveRonglianClient(@RequestBody RonglianClientInsertReq reqDTO);

    /**
     * 更新坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/updateRonglianClient")
    public JSONResult<Boolean> updateRonglianClient(@RequestBody RonglianClientDTO reqDTO);

    /**
     * 根据idList删除容联坐席
     * @param idListLongReq
     * @return
     */
    @PostMapping("/deleteRonglianClient")
    public JSONResult<Boolean> deleteRonglianClient(@RequestBody IdListLongReq idListLongReq);

    /**
     * 设置坐席状态
     * @param ronglianClientDTO
     * @return
     */
    @PostMapping("/setRonglianClientState")
    public JSONResult setRonglianClientState (@RequestBody RonglianClientDTO ronglianClientDTO);

    /**
     * 容联外呼
     * @param ronglianClientDTO
     * @return
     */
    @PostMapping("/ronglianOutBoundCall")
    public JSONResult ronglianOutBoundCall (@RequestBody RonglianClientDTO ronglianClientDTO);

    @Component
    static class HystrixClientFallback implements RonglianFeignClient {

        private static Logger logger = LoggerFactory.getLogger(RonglianFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<RonglianClientResqDTO> queryRonglianClientByLoginName(
            RonglianClientDTO ronglianClientDTO) {
            return fallBackError("根据登录坐席和用户id查询容联坐席");
        }

        @Override
        public JSONResult<PageBean<RonglianClientResqDTO>> listRonglianClientPage(
            RonglianClientDTO queryClientDTO) {
            return fallBackError("分页查询坐席");
        }

        @Override
        public JSONResult<RonglianClientResqDTO> queryRonglianClientById(IdEntity idEntity) {
            return fallBackError("根据id 查询坐席");
        }

        @Override
        public JSONResult<Boolean> saveRonglianClient(RonglianClientInsertReq reqDTO) {
            return fallBackError("添加坐席");
        }

        @Override
        public JSONResult<Boolean> updateRonglianClient(RonglianClientDTO reqDTO) {
            return fallBackError("更新坐席");
        }

        @Override
        public JSONResult<Boolean> deleteRonglianClient(IdListLongReq idListLongReq) {
            return fallBackError("根据idList删除容联坐席");
        }

        @Override
        public JSONResult setRonglianClientState(
            RonglianClientDTO ronglianClientDTO) {
            return fallBackError("设置坐席状态");
        }

        @Override
        public JSONResult ronglianOutBoundCall(
            RonglianClientDTO ronglianClientDTO) {
            return fallBackError("容联外呼");
        }
    }
}
