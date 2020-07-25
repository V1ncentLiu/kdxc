package com.kuaidao.manageweb.feign.user;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.sys.dto.user.LoginRecordDTO;
import com.kuaidao.sys.dto.user.LoginRecordReq;
import feign.hystrix.FallbackFactory;

/**
 * 登录记录
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "sys-service-fanjd", path = "/sys/loginRecord",
        fallbackFactory = LoginRecordFeignClient.HystrixClientFallback.class)
public interface LoginRecordFeignClient {


    /**
     * 查询登录记录
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<List<LoginRecordDTO>> list(@RequestBody LoginRecordReq loginRecord);


    /**
     * 新增登录记录
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult create(@RequestBody LoginRecordReq loginRecord);

    @Component
    static class HystrixClientFallback implements FallbackFactory<LoginRecordFeignClient> {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @Override
        public LoginRecordFeignClient create(Throwable cause) {
            return new LoginRecordFeignClient() {
                @SuppressWarnings("rawtypes")
                private JSONResult fallBackError(String name) {
                    logger.error("接口调用失败");
                    logger.error("接口名{}", name);
                    logger.error("失败原因{}", cause);
                    return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                            SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
                }

                @Override
                public JSONResult create(@RequestBody LoginRecordReq loginRecord) {
                    return fallBackError("新增登录记录");
                }


                @Override
                public JSONResult<List<LoginRecordDTO>> list(
                        @RequestBody LoginRecordReq loginRecord) {
                    return fallBackError("查询登录记录");
                }

            };
        }

    }
}
