package com.kuaidao.manageweb.feign.visit;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.console.BusinessConsolePanelRespDTO;
import com.kuaidao.aggregation.dto.console.BusinessConsoleReqDTO;
import com.kuaidao.aggregation.dto.console.BusinessDirectorConsolePanelRespDTO;
import com.kuaidao.aggregation.dto.sign.PayDetailDTO;
import com.kuaidao.aggregation.dto.visitrecord.RejectVisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitNoRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitNoRecordRespDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;

/**
 * 来访记录
 * 
 * @author Chen
 * @date 2019年3月1日 下午6:36:23
 * @version V1.0
 */
@FeignClient(name = "aggregation-service-1", path = "/aggregation/visitrecord/customerVisitRecord", fallback = VisitRecordFeignClient.HystrixClientFallback.class)
public interface VisitRecordFeignClient {

	/**
	 * 查询 签约记录
	 * 
	 * @param reqDTO
	 * @return
	 */
	@PostMapping("/listVisitRecord")
	JSONResult<PageBean<VisitRecordRespDTO>> listVisitRecord(@RequestBody VisitRecordReqDTO visitRecordReqDTO);

	/**
	 * 查询为到访记录
	 * @param visitNoRecordReqDTO
	 * @return
	 */
	@PostMapping("/listNoVisitRecordNoPage")
	public JSONResult<List<VisitNoRecordRespDTO>> listNoVisitRecordNoPage(
			@RequestBody VisitNoRecordReqDTO visitNoRecordReqDTO);

	/**
	 * 签约记录驳回
	 * 
	 * @param reqDTO
	 * @return
	 */
	@PostMapping("/rejectVisitRecord")
	public JSONResult<Boolean> rejectVisitRecord(@RequestBody RejectVisitRecordReqDTO reqDTO);

	/**
	 * 根據sign_id 查詢 付款明細
	 * 
	 * @param idListLongReq
	 * @return
	 */
	@PostMapping("/listPayDetailNoPage")
	JSONResult<List<PayDetailDTO>> listPayDetailNoPage(IdListLongReq idListLongReq);

	/**
	 * 查询当月到访数
	 * 
	 * @param businessConsoleReqDTO
	 * @return
	 */
	@PostMapping("/countCurMonthNum")
	JSONResult<BusinessConsolePanelRespDTO> countCurMonthNum(BusinessConsoleReqDTO businessConsoleReqDTO);

	/**
	 * 商务总监 看板 9当月二次到访数 10 当月二次来访签约数
	 * 
	 * @param businessConsoleReqDTO
	 * @return
	 */
	@PostMapping("/countBusinessDirectorCurMonthNum")
	JSONResult<BusinessDirectorConsolePanelRespDTO> countBusinessDirectorCurMonthNum(
			BusinessConsoleReqDTO businessConsoleReqDTO);

	/**
	 * 查询到访记录
	 * 
	 * @param visitRecordReqDTO
	 * @return
	 */
	@PostMapping("/listVisitRecordNoPage")
	JSONResult<List<VisitRecordRespDTO>> listVisitRecordNoPage(VisitRecordReqDTO visitRecordReqDTO);

	/**
	 * 查询未到访数据
	 * 
	 * @param visitNoRecordReqDTO
	 * @return
	 */

	@PostMapping("/listNoVisitRecord")
	JSONResult<PageBean<VisitNoRecordRespDTO>> listNoVisitRecord(VisitNoRecordReqDTO visitNoRecordReqDTO);

	@Component
	static class HystrixClientFallback implements VisitRecordFeignClient {

		private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<List<PayDetailDTO>> listPayDetailNoPage(IdListLongReq idListLongReq) {
			return fallBackError("根据signID查询付款明细");
		}

		@Override
		public JSONResult<PageBean<VisitRecordRespDTO>> listVisitRecord(VisitRecordReqDTO visitRecordReqDTO) {
			return fallBackError("查询签约记录");
		}

		@Override
		public JSONResult<List<VisitNoRecordRespDTO>> listNoVisitRecordNoPage(VisitNoRecordReqDTO visitNoRecordReqDTO) {
			return fallBackError("未到访记录（不分页）");
		}

		@Override
		public JSONResult<Boolean> rejectVisitRecord(RejectVisitRecordReqDTO reqDTO) {
			return fallBackError("来访记录驳回");
		}

		@Override
		public JSONResult<BusinessConsolePanelRespDTO> countCurMonthNum(BusinessConsoleReqDTO businessConsoleReqDTO) {
			return fallBackError("查询到访数");
		}

		@Override
		public JSONResult<BusinessDirectorConsolePanelRespDTO> countBusinessDirectorCurMonthNum(
				BusinessConsoleReqDTO businessConsoleReqDTO) {
			return fallBackError("商务总监看板");
		}

		@Override
		public JSONResult<List<VisitRecordRespDTO>> listVisitRecordNoPage(VisitRecordReqDTO visitRecordReqDTO) {
			return fallBackError("查询到访记录");
		}

		@Override
		public JSONResult<PageBean<VisitNoRecordRespDTO>> listNoVisitRecord(VisitNoRecordReqDTO visitNoRecordReqDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询未到访记录");
		}

	}

}
