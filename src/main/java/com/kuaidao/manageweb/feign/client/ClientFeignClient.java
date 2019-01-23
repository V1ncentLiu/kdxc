package com.kuaidao.manageweb.feign.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.client.AddOrUpdateQimoClientDTO;
import com.kuaidao.aggregation.dto.client.AddOrUpdateTrClientDTO;
import com.kuaidao.aggregation.dto.client.QimoClientQueryDTO;
import com.kuaidao.aggregation.dto.client.QimoClientRespDTO;
import com.kuaidao.aggregation.dto.client.QimoDataRespDTO;
import com.kuaidao.aggregation.dto.client.QueryQimoDTO;
import com.kuaidao.aggregation.dto.client.QueryTrClientDTO;
import com.kuaidao.aggregation.dto.client.TrClientDataRespDTO;
import com.kuaidao.aggregation.dto.client.TrClientQueryDTO;
import com.kuaidao.aggregation.dto.client.TrClientRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

/**
 *  坐席管理
 * 
 * @author Chen Chengxue
 * @date: 2019年1月3日 下午5:06:37
 * @version V1.0
 */
@FeignClient(name = "aggregation-service-chen", path = "/aggregation/client/client", fallback = ClientFeignClient.HystrixClientFallback.class)
public interface ClientFeignClient {
    
    /**
     * 保存天润坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/saveTrClient")
    JSONResult<Boolean> saveTrClient( @RequestBody AddOrUpdateTrClientDTO reqDTO);

    /**
     *更新天润坐席
     */
    @PostMapping("/updateTrClient")
    JSONResult<Boolean> updateTrClient(@RequestBody AddOrUpdateTrClientDTO reqDTO);
    
    /**
     * 删除天润坐席
     * @param idListReq
     * @return
     */
    @PostMapping("/deleteTrClient")
    JSONResult<Boolean> deleteTrClient(@RequestBody IdListReq idListReq);
    
    /**
     * 根据Id 查询天润坐席
     * @param idEntity
     * @return
     */
    @PostMapping("/queryTrClient")
    JSONResult<TrClientRespDTO> queryTrClient(@RequestBody IdEntity idEntity);
    
    /**
     *  根据参数查询数据 精确匹配
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryTrClientByParam")
    JSONResult<TrClientRespDTO> queryTrClientByParam(@RequestBody TrClientQueryDTO queryDTO);
    
    /**
     * 分页查询天润坐席
     * @param queryClientDTO
     * @return
     */
    @PostMapping("/listTrClientPage")
    JSONResult<PageBean<TrClientDataRespDTO>> listTrClientPage(@RequestBody QueryTrClientDTO queryClientDTO);
    
    /**
     * 保存七陌坐席
     * @param reqDTO
     * @return
     */
    @PostMapping("/saveQimoClient")
    JSONResult<Boolean> saveQimoClient(@RequestBody AddOrUpdateQimoClientDTO reqDTO);
    
    
    /**
     * 更新七陌坐席
     * @param reqDTO
     * @param result
     * @return
     */
    @PostMapping("/updateQimoClient")
    JSONResult<Boolean> updateQimoClient(@RequestBody AddOrUpdateQimoClientDTO reqDTO);
    
    /**
     * 删除七陌坐席 ，根据ID list 
     * @param idListReq
     * @return
     */
    @PostMapping("/deleteQimoClient")
    JSONResult<Boolean> deleteQimoClient(@RequestBody IdListReq idListReq);
    
    
    /**
     * 根据Id 查询七陌坐席
     * @param idEntity
     * @return
     */
    @PostMapping("/queryQimoClientById")
    JSONResult<QimoClientRespDTO> queryQimoClientById(@RequestBody IdEntity idEntity);
    
    /**
     *   根据参数查询数据 精确匹配
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryQimoClientByParam")
    JSONResult<QimoClientRespDTO> queryQimoClientByParam(@RequestBody QimoClientQueryDTO queryDTO);
    

    /**
     * 分页查询天润坐席
     * @param queryClientDTO
     * @return
     */
    @PostMapping("/listQimoClientPage")
    JSONResult<PageBean<QimoDataRespDTO>> listQimoClientPage( @RequestBody QueryQimoDTO queryClientDTO);

	@Component
	static class HystrixClientFallback implements ClientFeignClient {

		private static Logger logger = LoggerFactory.getLogger(ClientFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}

        @Override
        public JSONResult<Boolean> saveTrClient(AddOrUpdateTrClientDTO reqDTO) {
            return fallBackError("保存天润坐席");
        }

        @Override
        public JSONResult<Boolean> updateTrClient(AddOrUpdateTrClientDTO reqDTO) {
            return fallBackError("更新天润坐席");
        }

        @Override
        public JSONResult<Boolean> deleteTrClient(IdListReq idListReq) {
            return fallBackError("删除天润坐席");

        }

        @Override
        public JSONResult<TrClientRespDTO> queryTrClient(IdEntity idEntity) {
            return fallBackError("根据Id查询天润坐席");
        }

        @Override
        public JSONResult<TrClientRespDTO> queryTrClientByParam(TrClientQueryDTO queryDTO) {
            return fallBackError("根据参数查询天润坐席"); 
        }

        @Override
        public JSONResult<PageBean<TrClientDataRespDTO>> listTrClientPage(
                QueryTrClientDTO queryClientDTO) {
            return fallBackError("分页查询天润坐席");
        }

        @Override
        public JSONResult<Boolean> saveQimoClient(AddOrUpdateQimoClientDTO reqDTO) {
            return fallBackError("保存七陌坐席");
        }

        @Override
        public JSONResult<Boolean> updateQimoClient(AddOrUpdateQimoClientDTO reqDTO) {
            return fallBackError("更新七陌坐席");
        }

        @Override
        public JSONResult<Boolean> deleteQimoClient(IdListReq idListReq) {
            return fallBackError("删除七陌坐席");
        }

        @Override
        public JSONResult<QimoClientRespDTO> queryQimoClientById(IdEntity idEntity) {
            return fallBackError("根据ID查询七陌坐席");
        }

        @Override
        public JSONResult<QimoClientRespDTO> queryQimoClientByParam(QimoClientQueryDTO queryDTO) {
            return fallBackError("根据参数查询七陌坐席");
        }

        @Override
        public JSONResult<PageBean<QimoDataRespDTO>> listQimoClientPage(
                QueryQimoDTO queryClientDTO) {
            return fallBackError("分页查询七陌坐席");
        }

	}

}
