package com.kuaidao.manageweb.feign.client;

import com.kuaidao.callcenter.dto.ZkClient.*;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "callcenter-service", path = "/callcenter/zkClient", fallback = ZkClientFeignClient.HystrixClientFallback.class)
public interface ZkClientFeignClient {


    @PostMapping("/saveZkClient")
    public JSONResult<Boolean> saveZkClient(@RequestBody AddOrUpdateZkClientDTO reqDTO);

    @PostMapping("/updateZkClient")
    public JSONResult<Boolean> updateZkClient(@RequestBody AddOrUpdateZkClientDTO reqDTO);

    @PostMapping("/listZkClientPage")
    public JSONResult<PageBean<ZkClientRespDTO>> listZkClientPage(@RequestBody ZkClientQueryDTO queryClientDTO);

    @PostMapping("/queryZkClientById")
    public JSONResult<ZkClientRespDTO> queryZkClientById(@RequestBody IdEntity idEntity);

    @PostMapping("/deleteZkClient")
    public JSONResult<Boolean> deleteZkClient(@RequestBody IdListReq idListReq);

    @PostMapping("/uploadZkClientData")
    public JSONResult<List<ImportZkClientDTO>> uploadZkClientData(@RequestBody UploadZkClientDataDTO<ImportZkClientDTO> reqDTO);

    @PostMapping("/queryZkClient")
    public JSONResult<ZkClientRespDTO> queryZkClient(@RequestBody ZkClientQueryDTO zkClientQueryDTO);

    @PostMapping("/zkOutbound")
    public JSONResult zkOutbound(@RequestBody ZkClientOutboundDTO zkClientOutboundDTO);

    @Component
    static class HystrixClientFallback implements ZkClientFeignClient {
        private static Logger logger = LoggerFactory.getLogger(ZkClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> saveZkClient(@Valid AddOrUpdateZkClientDTO reqDTO) {
            return fallBackError("保存中科坐席");
        }

        @Override
        public JSONResult<Boolean> updateZkClient(@Valid AddOrUpdateZkClientDTO reqDTO) {
            return fallBackError("修改中科坐席");
        }

        @Override
        public JSONResult<PageBean<ZkClientRespDTO>> listZkClientPage(ZkClientQueryDTO queryClientDTO) {
            return fallBackError("分页查询中科列表");
        }

        @Override
        public JSONResult<ZkClientRespDTO> queryZkClientById(IdEntity idEntity) {
            return fallBackError("根据Id查询中科坐席");
        }

        @Override
        public JSONResult<Boolean> deleteZkClient(IdListReq idListReq) {
            return fallBackError("根据Id删除中科坐席");
        }

        @Override
        public JSONResult<List<ImportZkClientDTO>> uploadZkClientData(UploadZkClientDataDTO<ImportZkClientDTO> reqDTO) {
            return fallBackError("上传中科坐席");
        }

        @Override
        public JSONResult<ZkClientRespDTO> queryZkClient(ZkClientQueryDTO zkClientQueryDTO) {
            return fallBackError("查询中科列表");
        }

        @Override
        public JSONResult zkOutbound(ZkClientOutboundDTO zkClientOutboundDTO) {
            return fallBackError("中科外呼");
        }
    }


}
