package com.kuaidao.manageweb.feign.client;

import com.kuaidao.callcenter.dto.HeliClientInsertReq;
import com.kuaidao.callcenter.dto.RonglianClientDTO;
import com.kuaidao.callcenter.dto.RonglianClientInsertReq;
import com.kuaidao.callcenter.dto.seatmanager.HeliClientReq;
import com.kuaidao.common.entity.IdListLongReq;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.callcenter.dto.HeLiClientOutboundReqDTO;
import com.kuaidao.callcenter.dto.HeliClientReqDTO;
import com.kuaidao.callcenter.dto.HeliClientRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;


/**
 * 合力 坐席接口
 * @author  Devin.Chen
 * @date 2019-08-08 10:39:01
 * @version V1.0
 */
@FeignClient(name = "callcenter-service", path = "/callcenter/heliClient", fallback = HeliClientFeignClient.HystrixClientFallback.class)
public interface HeliClientFeignClient {
    
    
    /**
     * 坐席登录 
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @SuppressWarnings("rawtypes")
    @PostMapping("/login")
    public JSONResult login (@RequestBody HeLiClientOutboundReqDTO  heLiClientOutboundReqDTO);
    
    
    /**
     * 坐席退出 
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @SuppressWarnings("rawtypes")
    @PostMapping("/logout")
    public JSONResult logout (@RequestBody HeLiClientOutboundReqDTO  heLiClientOutboundReqDTO);
    
    /**
     * 外呼
    * @param heLiClientOutboundReqDTO
    * @return
     */
    @PostMapping("/outbound")
    public JSONResult outbound(@RequestBody HeLiClientOutboundReqDTO heLiClientOutboundReqDTO);

    
    /**
     * 查询坐席列表
    * @param heliClientReqDTO
    * @return
     */
    @PostMapping("/listClientsPage")
    public JSONResult<PageBean<HeliClientRespDTO>> listClientsPage(@RequestBody HeliClientReqDTO heliClientReqDTO);

    /**
     * 查询坐席相关信息
    * @param heliClientReqDTO
    * @return
     */
    @PostMapping("/listClientByParams")
    public JSONResult<List<HeliClientRespDTO>> listClientByParams(@RequestBody HeliClientReqDTO heliClientReqDTO);

    /**
     * 添加坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/saveHeliClient")
    public JSONResult<Boolean> saveHeliClient(@RequestBody HeliClientInsertReq reqDTO);

    /**
     * 更新坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/updateHeliClient")
    public JSONResult<Boolean> updateHeliClient(@RequestBody HeliClientReq reqDTO);

    /**
     * 根据idList删除容联坐席
     * @param idListLongReq
     * @return
     */
    @PostMapping("/deleteHeliClient")
    public JSONResult<Boolean> deleteHeliClient(@RequestBody IdListLongReq idListLongReq);
    
    @Component
    static   class HystrixClientFallback implements HeliClientFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult login(HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
            return fallBackError("合力坐席登录");
        }

        @Override
        public JSONResult logout(HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
            return fallBackError("合力坐席退出");
        }

        @Override
        public JSONResult outbound(HeLiClientOutboundReqDTO heLiClientOutboundReqDTO) {
            return fallBackError("合力坐席外呼");
        }

        @Override
        public JSONResult<PageBean<HeliClientRespDTO>> listClientsPage(HeliClientReqDTO heliClientReqDTO) {
            return fallBackError("分页查询坐席列表");
        }

        @Override
        public JSONResult<List<HeliClientRespDTO>> listClientByParams(
                HeliClientReqDTO heliClientReqDTO) {
            return fallBackError("查询坐席相关信息");
        }

        @Override
        public JSONResult<Boolean> saveHeliClient(HeliClientInsertReq reqDTO) {
            return fallBackError("添加合力坐席");
        }

        @Override
        public JSONResult<Boolean> updateHeliClient(HeliClientReq reqDTO) {
            return fallBackError("更新合力坐席");
        }

        @Override
        public JSONResult<Boolean> deleteHeliClient(IdListLongReq idListLongReq) {
            return fallBackError("删除合力坐席");
        }
    }


}
