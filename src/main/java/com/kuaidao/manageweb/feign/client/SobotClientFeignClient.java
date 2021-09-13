package com.kuaidao.manageweb.feign.client;

import com.kuaidao.callcenter.dto.sobotClient.*;
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

@FeignClient(name = "callcenter-service", path = "/callcenter/sobotClient", fallback = SobotClientFeignClient.HystrixClientFallback.class)
public interface SobotClientFeignClient {


    @PostMapping("/saveSobotClient")
    public JSONResult<Boolean> saveSobotClient(@RequestBody AddOrUpdateSobotClientDTO reqDTO);

    @PostMapping("/updateSobotClient")
    public JSONResult<Boolean> updateSobotClient(@RequestBody AddOrUpdateSobotClientDTO reqDTO);

    @PostMapping("/listSobotClientPage")
    public JSONResult<PageBean<SobotClientRespDTO>> listSobotClientPage(@RequestBody SobotClientQueryDTO queryClientDTO);

    @PostMapping("/querySobotClientById")
    public JSONResult<SobotClientRespDTO> querySobotClientById(@RequestBody IdEntity idEntity);

    @PostMapping("/deleteSobotClient")
    public JSONResult<Boolean> deleteSobotClient(@RequestBody IdListReq idListReq);

    @PostMapping("/uploadSobotClientData")
    public JSONResult<List<ImportSobotClientDTO>> uploadSobotClientData(@RequestBody UploadSobotClientDataDTO<ImportSobotClientDTO> reqDTO);

    @PostMapping("/querySobotClient")
    public JSONResult<SobotClientRespDTO> querySobotClient(@RequestBody SobotClientQueryDTO sobotClientQueryDTO);

    @Component
    static class HystrixClientFallback implements SobotClientFeignClient {
        private static Logger logger = LoggerFactory.getLogger(SobotClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> saveSobotClient(@Valid AddOrUpdateSobotClientDTO reqDTO) {
            return fallBackError("保存智齿坐席");
        }

        @Override
        public JSONResult<Boolean> updateSobotClient(@Valid AddOrUpdateSobotClientDTO reqDTO) {
            return fallBackError("修改智齿坐席");
        }

        @Override
        public JSONResult<PageBean<SobotClientRespDTO>> listSobotClientPage(SobotClientQueryDTO queryClientDTO) {
            return fallBackError("分页查询智齿列表");
        }

        @Override
        public JSONResult<SobotClientRespDTO> querySobotClientById(IdEntity idEntity) {
            return fallBackError("根据Id查询智齿坐席");
        }

        @Override
        public JSONResult<Boolean> deleteSobotClient(IdListReq idListReq) {
            return fallBackError("根据Id删除智齿坐席");
        }

        @Override
        public JSONResult<List<ImportSobotClientDTO>> uploadSobotClientData(UploadSobotClientDataDTO<ImportSobotClientDTO> reqDTO) {
            return fallBackError("上传智齿坐席");
        }

        @Override
        public JSONResult<SobotClientRespDTO> querySobotClient(SobotClientQueryDTO sobotClientQueryDTO) {
            return fallBackError("查询智齿列表");
        }
    }


}
