package com.kuaidao.manageweb.feign.invitearea;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.ip.IpAccessManagerQueryDTO;
import com.kuaidao.sys.dto.ip.IpPackageInfoDTO;
import com.kuaidao.sys.dto.ip.IpRepositoryInfoDTO;

@FeignClient(name = "aggregation-service-1", path = "/aggregation/invitearea", fallback = InviteareaFeignClient.HystrixClientFallback.class)

public interface InviteareaFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "/inviteAreaListPage")
	public JSONResult<PageBean<InviteAreaDTO>> inviteAreaListPage(@RequestBody InviteAreaDTO queryDTO);
	
	@RequestMapping(method = RequestMethod.POST, value = "/deleInviteArea")
	public JSONResult deleInviteArea(@RequestBody InviteAreaDTO queryDTO);
	

	@Component
	static class HystrixClientFallback implements InviteareaFeignClient {

		private static Logger logger = LoggerFactory.getLogger(InviteareaFeignClient.class);

		@SuppressWarnings("rawtypes")
		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<PageBean<InviteAreaDTO>> inviteAreaListPage(InviteAreaDTO queryDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询邀约区域列表失败");
		}

		@Override
		public JSONResult deleInviteArea(InviteAreaDTO queryDTO) {
			// TODO Auto-generated method stub
			return fallBackError("删除邀约区域列表失败");
		}
	}
}
