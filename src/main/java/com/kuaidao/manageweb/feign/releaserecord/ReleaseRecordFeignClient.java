package com.kuaidao.manageweb.feign.releaserecord;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordReqDTO;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;


/**
 *
 * 功能描述: 资源释放记录表
 * 
 * @auther yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/releaserecord",
        fallback = ReleaseRecordFeignClient.HystrixClientFallback.class)
public interface ReleaseRecordFeignClient {

    @RequestMapping("/insert")
    public JSONResult<Boolean> saveReleaseRecord(@RequestBody ReleaseRecordInsertOrUpdateDTO dto);

    @RequestMapping("/queryPageList")
    public JSONResult<PageBean<ReleaseRecordRespDTO>> queryPageList(@RequestBody ReleaseRecordReqDTO dto);
    
    @RequestMapping("/getReleaseRecordListByCludId")
    public JSONResult<List<ReleaseRecordRespDTO>> getReleaseRecordListByCludId(@RequestBody ReleaseRecordRespDTO dto);
    

    @RequestMapping("/listNoPage")
    public JSONResult<List<ReleaseRecordRespDTO>> listNoPage(@RequestBody ReleaseRecordReqDTO dto);

    @Component
    static class HystrixClientFallback implements ReleaseRecordFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<Boolean> saveReleaseRecord(ReleaseRecordInsertOrUpdateDTO dto) {
            return fallBackError("释放记录-新增");
        }

        @Override
        public JSONResult<PageBean<ReleaseRecordRespDTO>> queryPageList(ReleaseRecordReqDTO dto) {
            return fallBackError("释放记录-分页查询");
        }

		@Override
		public JSONResult<List<ReleaseRecordRespDTO>> getReleaseRecordListByCludId(ReleaseRecordRespDTO dto) {
			return fallBackError("根据资源id查询释放记录失败");
		}
        @Override
        public JSONResult<List<ReleaseRecordRespDTO>> listNoPage(ReleaseRecordReqDTO dto) {
            return fallBackError("根据资源查询释放记录");
        }
    }

}
