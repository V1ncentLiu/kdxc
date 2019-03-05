package com.kuaidao.manageweb.feign.clue;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kuaidao.aggregation.dto.clue.ClueRepetitionDTO;
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

/**
 * 重单处理管理
 * @author Administrator
 *
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/cluerepetition", fallback = ClueRepetitionFeignClient.HystrixClientFallback.class)
public interface ClueRepetitionFeignClient {
	/**
	 * 重单申请列表
	 * 
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/queryRepeatList")
	public JSONResult<PageBean<ClueRepetitionDTO>> queryRepeatList(@RequestBody ClueRepetitionDTO menuDTO);

	/**
	 * 根据id查询详情
	 * 
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/queryRepeatById")
	public JSONResult<ClueRepetitionDTO> queryRepeatById(@RequestBody ClueRepetitionDTO menuDTO);
	
	/**
	 * 撤销重单申请
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/delRepeatByIds")
	public JSONResult delRepeatByIds(@RequestBody ClueRepetitionDTO menuDTO);

	
	/**
	 * 重单处理列表
	 * 
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/dealPetitionList")
	public JSONResult<PageBean<ClueRepetitionDTO>> dealPetitionList(@RequestBody ClueRepetitionDTO menuDTO);

	/**
	 * 重单处理
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/dealPetitionById")
	public JSONResult dealPetitionById(@RequestBody ClueRepetitionDTO menuDTO);
	
	/**
	 * 审核重单
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/updatePetitionById")
	public JSONResult updatePetitionById(@RequestBody ClueRepetitionDTO menuDTO);
	
	/**
	 * 重单比例调整
	 * @param menuDTO
	 * @return
	 */
	@PostMapping("/updatePayDetailById")
	public JSONResult updatePayDetailById(@RequestBody PayDetailDTO payDetailDTO);
	
	@Component
	static class HystrixClientFallback implements ClueRepetitionFeignClient {

		private static Logger logger = LoggerFactory.getLogger(ClueRepetitionFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}


		@Override
		public JSONResult<PageBean<ClueRepetitionDTO>> queryRepeatList(ClueRepetitionDTO menuDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询重单列表失败");
		}


		@Override
		public JSONResult<ClueRepetitionDTO> queryRepeatById(ClueRepetitionDTO menuDTO) {
			// TODO Auto-generated method stub
			return fallBackError("根据id查询重单详情失败");
		}


		@Override
		public JSONResult delRepeatByIds(ClueRepetitionDTO menuDTO) {
			// TODO Auto-generated method stub
			return fallBackError("撤销重单申请失败");
		}


		@Override
		public JSONResult<PageBean<ClueRepetitionDTO>> dealPetitionList(ClueRepetitionDTO menuDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询重单处理列表失败");
		}


		@Override
		public JSONResult dealPetitionById(ClueRepetitionDTO menuDTO) {
			// TODO Auto-generated method stub
			return fallBackError("重单处理失败");
		}


		@Override
		public JSONResult updatePetitionById(ClueRepetitionDTO menuDTO) {
			// TODO Auto-generated method stub
			return fallBackError("重单审核失败");
		}


		@Override
		public JSONResult updatePayDetailById(PayDetailDTO payDetailDTO) {
			// TODO Auto-generated method stub
			return fallBackError("付款明细比例调整失败");
		}

	
	}

}
