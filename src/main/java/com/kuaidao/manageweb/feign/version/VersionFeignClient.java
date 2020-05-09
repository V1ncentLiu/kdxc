package com.kuaidao.manageweb.feign.version;

import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.version.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 版本管理服务调用Feign类
 *
 * @author
 */
@FeignClient(name = "version-service", path = "/version/v1.0/version",fallback = VersionFeignClient.HystrixClientFallback.class)
public interface VersionFeignClient {
	/**
	 * 版本信息列表
	 *
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/list")
	public JSONResult<PageBean<VersionManageDTO>> list(
      @RequestBody VersionManageListDTO versionManageListDTO);

	/**
	 * 创建版本信息
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/create")
	JSONResult<String> create(@RequestBody VersionCreateReq versionCreateReq);

	/**
	 * 版本信息维护
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/update")
	JSONResult update(@RequestBody VersionUpdateReq versionUpdateReq);

	/**
	 * 删除版本信息
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/delete")
	public JSONResult<Void> delete(@RequestBody IdEntity idEntity);

	/**
	 * 查询版本信息
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/getVersion")
	JSONResult<VersionManageDTO> getVersion(@RequestBody IdEntity idEntity);

	/**
	 * 设置最新版本
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/setNewVersion")
	public JSONResult<Void> setNewVersion(@RequestBody VersionManageSetNewDTO versionManageSetNewDTO);

	/**
	 * 设置下架
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/setOneOldVersion")
	public JSONResult<Void> setOneOldVersion(@RequestBody VersionManageSetNewDTO versionManageSetNewDTO);

	/**
	 * 校验版本号是否重复
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/checkNum")
	JSONResult<Boolean> checkNum(@RequestBody VersionNumCheckReq versionNumCheckReq);

	/**
	 * 这边采取了和Spring
	 * Cloud官方文档相同的做法，将fallback类作为内部类放入Feign的接口中，当然也可以单独写一个fallback类。
	 *
	 * @author guoruiling
	 */
	@Component
	static class HystrixClientFallback implements VersionFeignClient {
		private static final Logger logger = LoggerFactory.getLogger(VersionFeignClient.class);

		/**
		 * 熔断器返回信息
		 *
		 * @param name
		 *            接口名称
		 * @return
		 */
		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<PageBean<VersionManageDTO>> list(@RequestBody VersionManageListDTO versionManageListDTO) {
			return fallBackError("版本信息列表");
		}

		@Override
		public JSONResult<String> create(VersionCreateReq versionCreateReq) {
			return fallBackError("新增版本信息");
		}

		@Override
		public JSONResult update(VersionUpdateReq versionUpdateReq) {
			return fallBackError("修改版本信息");
		}

		@Override
		public JSONResult<Void> delete(@RequestBody IdEntity idEntity) {
			return fallBackError("删除版本信息");
		}

		@Override
		public JSONResult<VersionManageDTO> getVersion(@RequestBody IdEntity idEntity){
			return fallBackError("查询版本信息");
		}

		@Override
		public JSONResult<Void> setNewVersion(@RequestBody VersionManageSetNewDTO versionManageSetNewDTO) {
			return fallBackError("设置最新版本");
		}

		@Override
		public JSONResult<Void> setOneOldVersion(@RequestBody VersionManageSetNewDTO versionManageSetNewDTO) {
			return fallBackError("设置下架");
		}

		@Override
		public JSONResult<Boolean> checkNum(VersionNumCheckReq versionNumCheckReq) {
			return fallBackError("校验版本号是否重复");
		}

	}

}
