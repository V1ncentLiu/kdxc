package com.kuaidao.manageweb.feign.role;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;

@FeignClient(name = "sys-service",path="/sys/role/roleManager", fallback = RoleManagerFeignClient.HystrixClientFallback.class)
public interface RoleManagerFeignClient {
	

	@RequestMapping(method = RequestMethod.POST, value = "/queryRoleList")
	public JSONResult<List<RoleInfoDTO>> queryRoleList(RoleQueryDTO dto);
	
	
	
	

	@Component
	static class HystrixClientFallback implements RoleManagerFeignClient {

		private static final Logger logger = LoggerFactory.getLogger(RoleManagerFeignClient.class);

		@SuppressWarnings("rawtypes")
		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@SuppressWarnings("unchecked")
		@Override
		public JSONResult<List<RoleInfoDTO>> queryRoleList(RoleQueryDTO dto) {

			return fallBackError("查询角色列表数据失败");
		}

	}

}
