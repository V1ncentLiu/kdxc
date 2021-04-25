package com.kuaidao.manageweb.feign.client;

import com.kuaidao.callcenter.dto.OkccClient.*;
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

@FeignClient(name = "callcenter-service", path = "/callcenter/okCCClient", fallback = OkccClientFeignClient.HystrixClientFallback.class)
public interface OkccClientFeignClient {


    @PostMapping("/saveOkccClient")
    public JSONResult<Boolean> saveOkccClient(@RequestBody AddOrUpdateOkccClientDTO reqDTO);

    @PostMapping("/updateOkccClient")
    public JSONResult<Boolean> updateOkccClient(@RequestBody AddOrUpdateOkccClientDTO reqDTO);

    @PostMapping("/listOkccClientPage")
    public JSONResult<PageBean<OkccClientRespDTO>> listOkccClientPage(@RequestBody OkccClientQueryDTO queryClientDTO);

    @PostMapping("/queryOkccClientById")
    public JSONResult<OkccClientRespDTO> queryOkccClientById(@RequestBody IdEntity idEntity);

    @PostMapping("/deleteOkccClient")
    public JSONResult<Boolean> deleteOkccClient(@RequestBody IdListReq idListReq);

    @PostMapping("/uploadOkccClientData")
    public JSONResult<List<ImportOkccClientDTO>> uploadOkccClientData(@RequestBody UploadOkccClientDataDTO<ImportOkccClientDTO> reqDTO);

    @PostMapping("/queryOkccClient")
    public JSONResult<OkccClientRespDTO> queryOkccClient(@RequestBody OkccClientQueryDTO okccClientQueryDTO);

    @PostMapping("/okccOutbound")
    public JSONResult okccOutbound(@RequestBody OkccClientOutboundDTO okccClientOutboundDTO);

    @Component
    static class HystrixClientFallback implements OkccClientFeignClient {
        private static Logger logger = LoggerFactory.getLogger(OkccClientFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> saveOkccClient(@Valid AddOrUpdateOkccClientDTO reqDTO) {
            return fallBackError("保存赤晨坐席");
        }

        @Override
        public JSONResult<Boolean> updateOkccClient(@Valid AddOrUpdateOkccClientDTO reqDTO) {
            return fallBackError("修改赤晨坐席");
        }

        @Override
        public JSONResult<PageBean<OkccClientRespDTO>> listOkccClientPage(OkccClientQueryDTO queryClientDTO) {
            return fallBackError("分页查询赤晨列表");
        }

        @Override
        public JSONResult<OkccClientRespDTO> queryOkccClientById(IdEntity idEntity) {
            return fallBackError("根据Id查询赤晨坐席");
        }

        @Override
        public JSONResult<Boolean> deleteOkccClient(IdListReq idListReq) {
            return fallBackError("根据Id删除赤晨坐席");
        }

        @Override
        public JSONResult<List<ImportOkccClientDTO>> uploadOkccClientData(UploadOkccClientDataDTO<ImportOkccClientDTO> reqDTO) {
            return fallBackError("上传赤晨坐席");
        }

        @Override
        public JSONResult<OkccClientRespDTO> queryOkccClient(OkccClientQueryDTO OkccClientQueryDTO) {
            return fallBackError("查询赤晨列表");
        }

        @Override
        public JSONResult okccOutbound(OkccClientOutboundDTO OkccClientOutboundDTO) {
            return fallBackError("赤晨外呼");
        }
    }


}
