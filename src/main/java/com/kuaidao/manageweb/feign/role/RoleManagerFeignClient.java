package com.kuaidao.manageweb.feign.role;

import java.util.List;

import com.kuaidao.common.entity.IdListReq;
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

@FeignClient(name = "sys-service", path = "/sys/role/roleManager", fallback = RoleManagerFeignClient.HystrixClientFallback.class)
public interface RoleManagerFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "/queryRoleList")
	public JSONResult<List<RoleInfoDTO>> queryRoleList(RoleQueryDTO dto);

	@RequestMapping(method = RequestMethod.POST, value = "/saveRoleInfo")
	public JSONResult<String> saveRoleInfo(RoleInfoDTO dto);

	@RequestMapping(method = RequestMethod.POST, value = "/updateRoleInfo")
	public JSONResult<String> updateRoleInfo(RoleInfoDTO dto);

	@RequestMapping(method = RequestMethod.POST, value = "/deleteRoleInfo")
	public JSONResult<String> deleteRoleInfo(RoleInfoDTO dto);

	@RequestMapping(method = RequestMethod.POST, value = "/qeuryRoleById")
	public JSONResult<RoleInfoDTO> qeuryRoleById(RoleQueryDTO roleDTO);

	@RequestMapping(method = RequestMethod.POST, value = "/qeuryRoleByName")
	public JSONResult<List<RoleInfoDTO>> qeuryRoleByName(RoleQueryDTO roleDTO);
	@RequestMapping(method = RequestMethod.POST, value = "/qeuryRoleListByRoleIds")
	public JSONResult<List<RoleInfoDTO>> qeuryRoleListByRoleIds(IdListReq idListReq);

	/**
	 * 删除角色时判断是否有用户关联
	 * 
	 * @param roleDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/qeuryRoleByUserId")
	public JSONResult<List<RoleInfoDTO>> qeuryRoleByUserId(RoleQueryDTO roleDTO);

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

		@SuppressWarnings("unchecked")
		@Override
		public JSONResult<String> saveRoleInfo(RoleInfoDTO dto) {
			// TODO Auto-generated method stub
			return fallBackError("保存角色列表数据失败");
		}

		@SuppressWarnings("unchecked")
		@Override
		public JSONResult<String> updateRoleInfo(RoleInfoDTO dto) {
			// TODO Auto-generated method stub
			return fallBackError("修改角色列表数据失败");
		}

		@SuppressWarnings("unchecked")
		@Override
		public JSONResult<String> deleteRoleInfo(RoleInfoDTO dto) {
			// TODO Auto-generated method stub
			return fallBackError("删除角色列表数据失败");
		}

		@SuppressWarnings("unchecked")
		@Override
		public JSONResult<RoleInfoDTO> qeuryRoleById(RoleQueryDTO roleDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询角色修改数据失败");
		}

		@SuppressWarnings("unchecked")
		@Override
		public JSONResult<List<RoleInfoDTO>> qeuryRoleByName(RoleQueryDTO roleDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询角色数据失败");
		}

		@Override
		public JSONResult<List<RoleInfoDTO>> qeuryRoleListByRoleIds(IdListReq idListReq) {
			return fallBackError("根据idList查询角色");
		}

		@Override
		public JSONResult<List<RoleInfoDTO>> qeuryRoleByUserId(RoleQueryDTO roleDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询角色数据失败");
		}
		
		

	}

}
