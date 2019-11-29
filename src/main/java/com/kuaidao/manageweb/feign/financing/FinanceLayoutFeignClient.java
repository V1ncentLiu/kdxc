package com.kuaidao.manageweb.feign.financing;

import com.kuaidao.aggregation.dto.financing.FinanceLayoutDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
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

import javax.validation.Valid;

/**
 *
 * 功能描述:
 *      数据字典--词条
 * @auther: yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service",path="/aggregation/financing/financeLayout",fallback = FinanceLayoutFeignClient.HystrixClientFallback.class)
public interface FinanceLayoutFeignClient {



    @PostMapping("/getFinanceLayoutList")
    public JSONResult<PageBean<FinanceLayoutDTO>> getFinanceLayoutList(@Valid @RequestBody FinanceLayoutDTO financeLayoutDTO);

    @RequestMapping(method = RequestMethod.POST, value = "/addOrUpdateFinanceLayout")
	public JSONResult addOrUpdateFinanceLayout(@RequestBody FinanceLayoutDTO queryDTO);
    
    @RequestMapping(method = RequestMethod.POST, value = "/deleFinanceLayout")
	public JSONResult deleFinanceLayout(@RequestBody FinanceLayoutDTO financeLayoutDTO);
    
    @RequestMapping(method = RequestMethod.POST, value = "/findFinanceLayoutById")
	public JSONResult<FinanceLayoutDTO> findFinanceLayoutById(@RequestBody FinanceLayoutDTO financeLayoutDTO);
    
    
    @Component
    static class HystrixClientFallback implements FinanceLayoutFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

		@Override
		public JSONResult<PageBean<FinanceLayoutDTO>> getFinanceLayoutList(FinanceLayoutDTO financeLayoutDTO) {
			return fallBackError("查询财务布局失败");
		}

		@Override
		public JSONResult addOrUpdateFinanceLayout(FinanceLayoutDTO queryDTO) {
			return fallBackError("添加或者修改财务布局失败");
		}

		@Override
		public JSONResult deleFinanceLayout(FinanceLayoutDTO financeLayoutDTO) {
			return fallBackError("删除财务布局失败");
		}

		@Override
		public JSONResult<FinanceLayoutDTO> findFinanceLayoutById(FinanceLayoutDTO financeLayoutDTO) {
			return fallBackError("根据id查询财务布局失败");
		}

    }

}
