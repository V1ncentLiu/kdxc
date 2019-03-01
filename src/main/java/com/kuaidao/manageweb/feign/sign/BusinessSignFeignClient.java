package com.kuaidao.manageweb.feign.sign;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.aggregation.dto.sign.BusinessSignDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.ip.IpAccessManagerQueryDTO;
import com.kuaidao.sys.dto.ip.IpPackageInfoDTO;
import com.kuaidao.sys.dto.ip.IpRepositoryInfoDTO;

@FeignClient(name = "aggregation-service", path = "/aggregation/sign/businesssign", fallback = BusinessSignFeignClient.HystrixClientFallback.class)

public interface BusinessSignFeignClient {

	/**
	 * 有效性签约单确认列表
	 * @param queryDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/businessSignValidList")
	public JSONResult<PageBean<BusinessSignDTO>> businessSignValidList(@RequestBody BusinessSignDTO businessSignDTO);

	/**
	 * 有效性签约单状态修改
	 * @param queryDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/updateBusinessSignDTOValidByIds")
	public JSONResult updateBusinessSignDTOValidByIds(@RequestBody BusinessSignDTO businessSignDTO);

	@Component
	static class HystrixClientFallback implements BusinessSignFeignClient {

		private static Logger logger = LoggerFactory.getLogger(BusinessSignFeignClient.class);

		@SuppressWarnings("rawtypes")
		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<PageBean<BusinessSignDTO>> businessSignValidList(BusinessSignDTO businessSignDTO) {
			// TODO Auto-generated method stub
			return fallBackError("有效签约单查询失败");
		}

		@Override
		public JSONResult updateBusinessSignDTOValidByIds(BusinessSignDTO businessSignDTO) {
			// TODO Auto-generated method stub
			return fallBackError("签约单有效性判断");
		}

		
		
	}
}
