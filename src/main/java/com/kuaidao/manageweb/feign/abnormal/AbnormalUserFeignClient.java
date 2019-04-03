package com.kuaidao.manageweb.feign.abnormal;

import com.kuaidao.aggregation.dto.abnormal.AbnomalUserAddAndUpdateDTO;
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserQueryDTO;
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;


/**
 *
 * 功能描述: 
 *      数据字典
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service-1",path="/aggregation/abnormaluser",fallback = AbnormalUserFeignClient.HystrixClientFallback.class)
public interface AbnormalUserFeignClient {

    @PostMapping("/saveAbnomalUser")
    public JSONResult saveAbnomalUser(@RequestBody AbnomalUserAddAndUpdateDTO dto);

    @PostMapping("/queryAbnomalUserList")
    public JSONResult<PageBean<AbnomalUserRespDTO>> queryAbnomalUserList(@RequestBody AbnomalUserQueryDTO dto);

    @PostMapping("/deleteAbnomalUsers")
    public JSONResult deleteAbnomalUsers(@RequestBody List<Long> ids);

    @Component
    static class HystrixClientFallback implements AbnormalUserFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult saveAbnomalUser(AbnomalUserAddAndUpdateDTO dto) {
            return fallBackError("新增标记异常客户");
        }

        @Override
        public JSONResult<PageBean<AbnomalUserRespDTO>> queryAbnomalUserList(AbnomalUserQueryDTO dto) {
            return fallBackError("标记异常客户列表查询");
        }

        @Override
        public JSONResult deleteAbnomalUsers(List<Long> ids) {
            return fallBackError("标记异常客户批量删除");
        }
    }

}
