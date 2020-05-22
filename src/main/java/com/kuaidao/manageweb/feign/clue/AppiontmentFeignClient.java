package com.kuaidao.manageweb.feign.clue;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.clue.AppiontmentCancelDTO;
import com.kuaidao.aggregation.dto.clue.ClueAppiontmentDTO;
import com.kuaidao.aggregation.dto.clue.ClueAppiontmentPageParam;
import com.kuaidao.aggregation.dto.clue.ClueAppiontmentReq;
import com.kuaidao.aggregation.dto.clue.ClueRepeatPhoneDTO;
import com.kuaidao.aggregation.dto.console.BusinessConsoleReqDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 预约来访
 * 
 * @author: zhangxingyu
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/clueAppiontment", fallback = AppiontmentFeignClient.HystrixClientFallback.class)
public interface AppiontmentFeignClient {
	/**
	 * 根据id查询预约来访信息
	 * @return
	 */
	@PostMapping("/get")
	public JSONResult<ClueAppiontmentDTO> get(@RequestBody IdEntityLong id);

	/**
	 * 删除预约来访信息
	 * @return
	 */
	@PostMapping("/delete")
	public JSONResult delete(@RequestBody IdListLongReq idList, @RequestParam("loginUserId") Long loginUserId);

	/**
	 * 查询预约来访集合
	 * @return
	 */
	@PostMapping("/list")
	public JSONResult<PageBean<ClueAppiontmentDTO>> list(@RequestBody ClueAppiontmentPageParam param);

	/**
	 * 查询重复手机号资源
	 * @return
	 */
	@PostMapping("/repeatPhonelist")
	public JSONResult<List<ClueRepeatPhoneDTO>> repeatPhonelist(@RequestBody ClueAppiontmentReq param);

	/**
	 * 查询重复手机号资源
	 * @return
	 */
	@PostMapping("/repeatPhoneMap")
	public JSONResult<Map> repeatPhoneMap(@RequestBody ClueAppiontmentReq clueAppiontmentReq);

	/**
	 * 修改预约来访信息
	 * @return
	 */
	@PostMapping("/update")
	public JSONResult<String> update(@RequestBody ClueAppiontmentReq req);

	/**
	 * 修改预约来访信息
	 * @return
	 */
	@PostMapping("/updateView")
	public JSONResult<String> updateView(@RequestBody ClueAppiontmentReq req);

	/**
	 * 查询取消邀约来访数据
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping("/findCancelList")
	public JSONResult<List<ClueAppiontmentDTO>> findCancelList(@RequestBody AppiontmentCancelDTO dto);

	/**
	 * 取消预约来访
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping("/cancelAppiontment")
	public JSONResult<String> cancelAppiontment(@RequestBody AppiontmentCancelDTO dto);
	
	/**
	 * 根据开始时间结束时间查询邀约单数量 不包括删除的
	 * @param teleConsoleReqDTO
	 * @return
	 */
	@PostMapping("/countTodayAppiontmentNum")
	 public JSONResult<Integer> countTodayAppiontmentNum(@RequestBody TeleConsoleReqDTO teleConsoleReqDTO);

	   /**
     * 电销总监 预计明日来访数
     * @param reqDTO
     * @return
     */
	@PostMapping("/countTeleDirecotorTomorrowArriveTime")
    public JSONResult<Integer> countTeleDirecotorTomorrowArriveTime(TeleConsoleReqDTO reqDTO);

    /**
     * 商务总监 预计明日到访数
     * @param businessConsoleReqDTO
     * @return
     */
	@PostMapping("/countBusiDirecotorTomorrowArriveTime")
    public JSONResult<Integer> countBusiDirecotorTomorrowArriveTime(BusinessConsoleReqDTO businessConsoleReqDTO);

	@Component
	static class HystrixClientFallback implements AppiontmentFeignClient {

		private static Logger logger = LoggerFactory.getLogger(AppiontmentFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

		@Override
		public JSONResult<ClueAppiontmentDTO> get(@RequestBody IdEntityLong id) {
			return fallBackError("根据id查询预约来访信息");
		}

		@Override
		public JSONResult delete(@RequestBody IdListLongReq idList, @RequestParam("loginUserId") Long loginUserId) {
			return fallBackError("删除预约来访信息");
		}

		@Override
		public JSONResult<String> update(@RequestBody ClueAppiontmentReq req) {
			return fallBackError("修改预约来访信息");
		}

		@Override
		public JSONResult<PageBean<ClueAppiontmentDTO>> list(@RequestBody ClueAppiontmentPageParam param) {
			return fallBackError("查询预约来访集合");
		}

		@Override
		public JSONResult<List<ClueRepeatPhoneDTO>> repeatPhonelist(@RequestBody ClueAppiontmentReq param) {
			return fallBackError("查询重复手机号资源");
		}

		@Override
		public JSONResult<Map> repeatPhoneMap(ClueAppiontmentReq clueAppiontmentReq) {
			return fallBackError("查询重复手机号Map查询资源");
		}

		@Override
		public JSONResult<String> cancelAppiontment(AppiontmentCancelDTO dto) {
			// TODO Auto-generated method stub
			return fallBackError("取消预约来访");
		}

		@Override
		public JSONResult<List<ClueAppiontmentDTO>> findCancelList(AppiontmentCancelDTO dto) {
			// TODO Auto-generated method stub
			return fallBackError("查询取消预约来访数据");
		}

		@Override
		public JSONResult<String> updateView(ClueAppiontmentReq req) {
			// TODO Auto-generated method stub
			return fallBackError("修改资源是否查看");
		}

        @Override
        public JSONResult<Integer> countTodayAppiontmentNum(TeleConsoleReqDTO teleConsoleReqDTO) {
            return fallBackError("统计邀约单数量");
        }

        @Override
        public JSONResult<Integer> countTeleDirecotorTomorrowArriveTime(TeleConsoleReqDTO reqDTO) {
            return fallBackError("预计明日来访数");
        }

        @Override
        public JSONResult<Integer> countBusiDirecotorTomorrowArriveTime(
                BusinessConsoleReqDTO businessConsoleReqDTO) {
            return fallBackError("预计明日到访数");
        }

	}


}
