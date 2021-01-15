package com.kuaidao.manageweb.feign.clue;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.aggregation.dto.clue.ClueReceiveRecordsDTO;
import com.kuaidao.aggregation.dto.clue.ClueRepetitionDTO;
import com.kuaidao.aggregation.dto.sign.BusinessSignDTO;
import com.kuaidao.aggregation.dto.sign.PayDetailDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.customfield.CustomFieldAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuAddAndUpdateDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldMenuRespDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.CustomFieldRespDTO;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 资源领取
 * @author Administrator
 *
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/clue/cluereceiverecords", fallback = ClueReceiveRecordsFeignClient.HystrixClientFallback.class)
public interface ClueReceiveRecordsFeignClient {
	/**
	 * 资源领取
	 * 
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/receiveClueByClueIds")
	public JSONResult<ClueReceiveRecordsDTO> receiveClueByClueIds(@RequestBody ClueReceiveRecordsDTO clueReceiveRecordsDTO);

	@RequestMapping("/validateCluePhase")
	public JSONResult validateCluePhase(@RequestBody ClueReceiveRecordsDTO clueReceiveRecordsDTO);
	
	@Component
	static class HystrixClientFallback implements ClueReceiveRecordsFeignClient {

		private static Logger logger = LoggerFactory.getLogger(ClueReceiveRecordsFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}



		@Override
		public JSONResult<ClueReceiveRecordsDTO> receiveClueByClueIds(ClueReceiveRecordsDTO clueReceiveRecordsDTO) {
			return fallBackError("领取公有池资源失败");
		}

		@Override
		public JSONResult validateCluePhase(ClueReceiveRecordsDTO clueReceiveRecordsDTO) {
			return fallBackError("领取公有池资源校验");
		}

	}

}
