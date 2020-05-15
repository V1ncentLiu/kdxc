package com.kuaidao.manageweb.feign.client;

import com.kuaidao.callcenter.dto.ketianclient.*;
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

import java.util.List;

/**
 * 科天坐席 feign
 * @author Devin.Chen
 * @Date: 2019/10/23 16:32
 */
@FeignClient(name = "callcenter-service", path = "/callcenter/ketianClient", fallback = KetianFeignClient.HystrixClientFallback.class)
public interface KetianFeignClient {


    /**
     * 根据登录名和组织机构查询坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/listClient")
    public JSONResult<List<KetianClientRespDTO>> listClient(@RequestBody KetianClientReqDTO reqDTO);


    /**
     * 分页查询坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/listClientPage")
    JSONResult<PageBean<KetianClientRespDTO>> listClientPage(KetianClientPageReqDTO reqDTO);

    /**
     * 根据id 查询坐席
     * @param idEntityLong
     * @return
     */
    @PostMapping("/queryById")
    JSONResult<KetianClientRespDTO> queryById(IdEntityLong idEntityLong);

    /**
     * 添加或修改坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/insertAndUpdateClient")
    JSONResult insertAndUpdateClient(KetianClientInsertAndUpdateReqDTO reqDTO);

    /**
     * 外呼
     * @param outboundDTO
     * @return
     */
    @PostMapping("/outbound")
    JSONResult outbound(@RequestBody  KetianClientOutboundDTO outboundDTO);

    /**
     * 删除坐席
     * @param idListLongReq
     * @return
     */
    @PostMapping("/deleteClientByIdList")
    JSONResult deleteClientByIdList(@RequestBody IdListLongReq idListLongReq);


    @Component
    static class HystrixClientFallback implements KetianFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<List<KetianClientRespDTO>> listClient(KetianClientReqDTO reqDTO) {
            return fallBackError("根据登录名和组织机构查询坐席");
        }

        @Override
        public JSONResult<PageBean<KetianClientRespDTO>> listClientPage(KetianClientPageReqDTO reqDTO) {
            return fallBackError("分页查询科天坐席");
        }

        @Override
        public JSONResult<KetianClientRespDTO> queryById(IdEntityLong idEntityLong) {
            return fallBackError("根据ID查询坐席");
        }

        @Override
        public JSONResult insertAndUpdateClient(KetianClientInsertAndUpdateReqDTO reqDTO) {
            return fallBackError("添加或修改坐席");
        }

        @Override
        public JSONResult outbound(KetianClientOutboundDTO outboundDTO) {
            return fallBackError("坐席外呼失败");
        }

        @Override
        public JSONResult deleteClientByIdList(IdListLongReq idListLongReq) {
            return fallBackError("删除坐席");
        }
    }

}
