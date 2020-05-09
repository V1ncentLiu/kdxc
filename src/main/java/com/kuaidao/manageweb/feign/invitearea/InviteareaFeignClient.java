package com.kuaidao.manageweb.feign.invitearea;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.aggregation.dto.invitearea.InviteAreaDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

@FeignClient(name = "aggregation-service", path = "/aggregation/invitearea", fallback = InviteareaFeignClient.HystrixClientFallback.class)
public interface InviteareaFeignClient {

	/**
	 * 查询邀约区域列表
	 * @param queryDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/inviteAreaListPage")
	public JSONResult<PageBean<InviteAreaDTO>> inviteAreaListPage(@RequestBody InviteAreaDTO queryDTO);
	
	/**
	 * 删除邀约区域列表
	 * @param queryDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/deleInviteArea")
	public JSONResult deleInviteArea(@RequestBody InviteAreaDTO queryDTO);
	
	/**
	 * 添加或者修改邀约区域列表
	 * @param queryDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/addOrUpdateInviteArea")
	public JSONResult addOrUpdateInviteArea(@RequestBody InviteAreaDTO queryDTO);

	/**
	 * 批量添加邀约区域列表
	 * @param queryDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/addInviteAreaList")
	public JSONResult addInviteAreaList(@RequestBody List<InviteAreaDTO> queryDTO);
	
	/**
	 * 根据ids查询邀约区域列表
	 * @param queryDTO
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/getInviteAreaListByIds")
	public JSONResult<List<InviteAreaDTO>> getInviteAreaListByIds(@RequestBody InviteAreaDTO queryDTO);
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

		@Override
		public JSONResult addOrUpdateInviteArea(InviteAreaDTO queryDTO) {
			// TODO Auto-generated method stub
			return fallBackError("添加或者修改邀约区域列表失败");
		}

		@Override
		public JSONResult addInviteAreaList(List<InviteAreaDTO> queryDTO) {
			// TODO Auto-generated method stub
			return fallBackError("批量插入邀约区域列表失败");
		}

		@Override
		public JSONResult<List<InviteAreaDTO>> getInviteAreaListByIds(InviteAreaDTO queryDTO) {
			// TODO Auto-generated method stub
			return fallBackError("根据ids查询邀约区域列表失败");
		}
	}
}
