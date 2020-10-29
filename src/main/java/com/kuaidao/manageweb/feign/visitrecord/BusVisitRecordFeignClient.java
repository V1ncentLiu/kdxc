package com.kuaidao.manageweb.feign.visitrecord;

import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordRespDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitNoRecordRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "aggregation-service", path = "/aggregation/busvisitrecord", fallback = BusVisitRecordFeignClient.HystrixClientFallback.class)
public interface BusVisitRecordFeignClient {

	@RequestMapping("/insert")
	public JSONResult<Boolean> saveVisitRecord(@RequestBody BusVisitRecordInsertOrUpdateDTO dto);
	@RequestMapping("/update")
	public JSONResult<Boolean> updateVisitRecord(@RequestBody BusVisitRecordInsertOrUpdateDTO dto);
	@RequestMapping("/queryList")
	public JSONResult<List<BusVisitRecordRespDTO>> queryList(@RequestBody BusVisitRecordReqDTO dto);
	@RequestMapping("/one")
	public JSONResult<BusVisitRecordRespDTO> queryOne(@RequestBody IdEntityLong idEntityLong);

	@PostMapping("/echoAppoinment")
	public JSONResult<Map> echoAppoinment(IdEntityLong idEntityLong);
	@PostMapping("/findMaxNewOne")
	public JSONResult<BusVisitRecordRespDTO> findMaxNewOne(@RequestBody IdEntityLong idEntityLong);

	@PostMapping("/findByIds")
	public JSONResult<List<BusVisitRecordRespDTO>> findByIds(@RequestBody IdListLongReq idListLongReq);

	@RequestMapping("/notVisitMaxNewone")
	public JSONResult<VisitNoRecordRespDTO> findMaxNewNotVisitOne(@RequestBody IdEntityLong idEntityLong);


	@RequestMapping("/notVisitOne")
	public JSONResult<VisitNoRecordRespDTO> notVisitOne(@RequestBody IdEntityLong idEntityLong);

	@Component
	static class HystrixClientFallback implements BusVisitRecordFeignClient {
		private static Logger logger = LoggerFactory.getLogger(BusVisitRecordFeignClient.class);

		@SuppressWarnings("rawtypes")
		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<Boolean> saveVisitRecord(BusVisitRecordInsertOrUpdateDTO dto) {
			return fallBackError("保存访问记录");
		}

		@Override
		public JSONResult<Boolean> updateVisitRecord(BusVisitRecordInsertOrUpdateDTO dto) {
			return fallBackError("更新访问记录");
		}

		@Override
		public JSONResult<List<BusVisitRecordRespDTO>> queryList(BusVisitRecordReqDTO dto) {
			return fallBackError("访问记录查询（不分页）");
		}

		@Override
		public JSONResult<BusVisitRecordRespDTO> queryOne(IdEntityLong idEntityLong) {
			return fallBackError("访问记录查询明细");
		}

		@Override
		public JSONResult<Map> echoAppoinment(IdEntityLong idEntityLong) {
			return fallBackError("回显邀约来访记录信息");
		}

		@Override
		public JSONResult<BusVisitRecordRespDTO> findMaxNewOne(IdEntityLong idEntityLong) {
			return fallBackError("回显邀签约记录信息");
		}

		@Override
		public JSONResult<List<BusVisitRecordRespDTO>> findByIds(IdListLongReq idListLongReq) {
			return fallBackError("通过IDS查询到访记录");
		}

		@Override
		public JSONResult<VisitNoRecordRespDTO> findMaxNewNotVisitOne(IdEntityLong idEntityLong) {
			return fallBackError("查询最新一条未到访记录");
		}

		@Override
		public JSONResult<VisitNoRecordRespDTO> notVisitOne(IdEntityLong idEntityLong) {
			return fallBackError("查询未到访记录");
		}
	}
}
