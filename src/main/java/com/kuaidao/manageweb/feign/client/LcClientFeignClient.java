package com.kuaidao.manageweb.feign.client;

import com.kuaidao.callcenter.dto.LcClient.*;
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

@FeignClient(name = "callcenter-service", path = "/callcenter/lcClient", fallback = LcClientFeignClient.HystrixClientFallback.class)
public interface LcClientFeignClient {


    @PostMapping("/saveLcClient")
    public JSONResult<Boolean> saveLcClient(@RequestBody AddOrUpdateLcClientDTO reqDTO);

    @PostMapping("/updateLcClient")
    public JSONResult<Boolean> updateLcClient(@RequestBody AddOrUpdateLcClientDTO reqDTO);

    @PostMapping("/listLcClientPage")
    public JSONResult<PageBean<LcClientRespDTO>> listLcClientPage(@RequestBody LcClientQueryDTO queryClientDTO);

    @PostMapping("/queryLcClientById")
    public JSONResult<LcClientRespDTO> queryLcClientById(@RequestBody IdEntity idEntity);

    @PostMapping("/deleteLcClient")
    public JSONResult<Boolean> deleteLcClient(@RequestBody IdListReq idListReq);

    @PostMapping("/uploadLcClientData")
    public JSONResult<List<ImportLcClientDTO>> uploadLcClientData(@RequestBody UploadLcClientDataDTO<ImportLcClientDTO> reqDTO);

    @PostMapping("/queryLcClient")
    public JSONResult<LcClientRespDTO> queryLcClient(@RequestBody LcClientQueryDTO lcClientQueryDTO);

    @PostMapping("/lcOutbound")
    public JSONResult lcOutbound(@RequestBody LcClientOutboundDTO lcClientOutboundDTO);

    @Component
    static class HystrixClientFallback implements LcClientFeignClient {
        private static Logger logger = LoggerFactory.getLogger(LcClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> saveLcClient(@Valid AddOrUpdateLcClientDTO reqDTO) {
            return fallBackError("保存乐创坐席");
        }

        @Override
        public JSONResult<Boolean> updateLcClient(@Valid AddOrUpdateLcClientDTO reqDTO) {
            return fallBackError("修改乐创坐席");
        }

        @Override
        public JSONResult<PageBean<LcClientRespDTO>> listLcClientPage(LcClientQueryDTO queryClientDTO) {
            return fallBackError("分页查询乐创列表");
        }

        @Override
        public JSONResult<LcClientRespDTO> queryLcClientById(IdEntity idEntity) {
            return fallBackError("根据Id查询乐创坐席");
        }

        @Override
        public JSONResult<Boolean> deleteLcClient(IdListReq idListReq) {
            return fallBackError("根据Id删除乐创坐席");
        }

        @Override
        public JSONResult<List<ImportLcClientDTO>> uploadLcClientData(UploadLcClientDataDTO<ImportLcClientDTO> reqDTO) {
            return fallBackError("上传乐创坐席");
        }

        @Override
        public JSONResult<LcClientRespDTO> queryLcClient(LcClientQueryDTO lcClientQueryDTO) {
            return fallBackError("查询乐创列表");
        }

        @Override
        public JSONResult lcOutbound(LcClientOutboundDTO lcClientOutboundDTO) {
            return fallBackError("乐创外呼");
        }
    }


}
