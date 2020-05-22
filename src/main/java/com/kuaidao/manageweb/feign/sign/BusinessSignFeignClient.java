package com.kuaidao.manageweb.feign.sign;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.aggregation.dto.busmycustomer.SignRecordReqDTO;
import com.kuaidao.aggregation.dto.sign.BusSignInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.sign.BusSignRespDTO;
import com.kuaidao.aggregation.dto.sign.BusinessSignDTO;
import com.kuaidao.aggregation.dto.sign.PayDetailDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

@FeignClient(name = "aggregation-service-1", path = "/aggregation/sign/businesssign", fallback = BusinessSignFeignClient.HystrixClientFallback.class)

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


	/**
	 *  找到最大值。
	 */
	@PostMapping("/findMaxNewOne")
	public JSONResult<BusSignRespDTO> findMaxNewOne(@RequestBody IdEntityLong idEntityLong);

	/**
	 * 重单处理列表
	 *
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/businessSignDealList")
	public JSONResult<PageBean<BusinessSignDTO>> businessSignDealList(@RequestBody BusinessSignDTO businessSignDTO);

	@PostMapping("/repeatPaymentDetails")
	public JSONResult<BusinessSignDTO> repeatPaymentDetails(@RequestBody BusinessSignDTO businessSignDTO);

	@PostMapping("/getPaymentDetailsById")
	public JSONResult<PayDetailDTO> getPaymentDetailsById(@RequestBody PayDetailDTO detailDTO);
	@RequestMapping("/insert")
	public JSONResult<Boolean> saveSign(@Valid @RequestBody BusSignInsertOrUpdateDTO dto);

	@RequestMapping("/update")
	public JSONResult<Boolean> updateSign(@Valid @RequestBody BusSignInsertOrUpdateDTO dto);

	@RequestMapping("/one")
	public JSONResult<BusSignRespDTO> queryOne(@RequestBody IdEntityLong idEntityLong);

	@RequestMapping("/querySignList")
	public JSONResult<List<BusSignRespDTO>> querySignList(@RequestBody SignRecordReqDTO dto);
	@RequestMapping("/querySignById")
	public JSONResult<BusSignRespDTO> querySignById(@RequestBody IdEntityLong idEntityLong);
	@RequestMapping("/updateSignDetail")
	public JSONResult<Boolean> updateSignDetail(@Valid @RequestBody BusSignInsertOrUpdateDTO dto);

	/**
	 * 分配判单用户
	 * @param
	 * @return
	 */
	@PostMapping("/distributionPdUser")
	public JSONResult distributionPdUser(@RequestBody BusinessSignDTO businessSignDTO);
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

		@Override
		public JSONResult<BusSignRespDTO> findMaxNewOne(IdEntityLong idEntityLong) {
			return fallBackError("最新到访记录");
		}

		@Override
		public JSONResult<Boolean> saveSign(BusSignInsertOrUpdateDTO dto) {
			return fallBackError("新增签约单");
		}

		@Override
		public JSONResult<Boolean> updateSign(BusSignInsertOrUpdateDTO dto) {
			return fallBackError("更新签约单");
		}

		@Override
		public JSONResult<BusSignRespDTO> queryOne(IdEntityLong idEntityLong) {
			return fallBackError("查询签约单明细");
		}

		@Override
		public JSONResult<List<BusSignRespDTO>> querySignList(SignRecordReqDTO dto) {
			return fallBackError("查询签约单不分页");
		}

		@Override
		public JSONResult<PageBean<BusinessSignDTO>> businessSignDealList(BusinessSignDTO businessSignDTO) {
			return fallBackError("查询签约重单列表失败");
		}

		@Override
		public JSONResult<BusinessSignDTO> repeatPaymentDetails(BusinessSignDTO businessSignDTO) {
			return fallBackError("查询签约重单详情失败");
		}

		@Override
		public JSONResult<PayDetailDTO> getPaymentDetailsById(PayDetailDTO detailDTO) {
			return fallBackError("根据id查询付款明细详情失败");
		}

		@Override
		public JSONResult<BusSignRespDTO> querySignById(IdEntityLong idEntityLong) {
			return fallBackError("查询签约单明细");
		}

		@Override
		public JSONResult<Boolean> updateSignDetail(BusSignInsertOrUpdateDTO dto) {
			return fallBackError("更新签约单");
		}

		@Override
		public JSONResult distributionPdUser(BusinessSignDTO businessSignDTO) {
			return fallBackError("分配失败");
		}

	}
}
