package com.kuaidao.manageweb.feign.merchant.publiccustomer;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.publiccustomer.PublicCustomerFeignClient.HystrixClientFallback;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsParamDTO;
import com.kuaidao.merchant.dto.pubcusres.ClueQueryParamDTO;
import com.kuaidao.merchant.dto.pubcusres.ClueReceiveRecordsDTO;
import com.kuaidao.merchant.dto.pubcusres.PublicCustomerResourcesRespDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 *
 * 功能描述: 
 *      公共客户资源-商家版
 * @auther  yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "merchant-service",path="/merchant/pubcustomer",fallback = PubcustomerFeignClient.HystrixClientFallback.class)
public interface PubcustomerFeignClient {


    @PostMapping("/queryPage")
    public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(
        @RequestBody ClueQueryParamDTO dto);

    @PostMapping("/receiveClue")
    public JSONResult<ClueReceiveRecordsDTO> receiveClue(@RequestBody ClueReceiveRecordsDTO dto);
    /**
     * 根据用户id集合获取领取的的资源
     *
     * @param
     * @return
     */
    @PostMapping("/getReceiveResourceStatistics")
    JSONResult<ResourceStatisticsDto> getReceiveResourceStatistics(@RequestBody IdListLongReq reqDto);

  /**
   * 按照用户ID集合获取领取资源-按维度进行分组
   * @param paramDTO
   * @return
   */
   @PostMapping("/countReceiveResourceStatistics")
   JSONResult<List<ResourceStatisticsDto>> countReceiveResourceStatistics(@RequestBody ResourceStatisticsParamDTO paramDTO);

    @Component
    static class HystrixClientFallback implements
        PubcustomerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }



        @Override
        public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(ClueQueryParamDTO dto) {
            return fallBackError("公共客户资源分页查询");
        }

      @Override
      public JSONResult<ClueReceiveRecordsDTO> receiveClue(ClueReceiveRecordsDTO dto) {
        return fallBackError("公共客户资源-领取资源");
      }
        @Override
        public JSONResult<ResourceStatisticsDto> getReceiveResourceStatistics(@RequestBody IdListLongReq reqDto){
            return fallBackError("根据id集合获取领取资源");
        }

      @Override
      public JSONResult<List<ResourceStatisticsDto>> countReceiveResourceStatistics(
          ResourceStatisticsParamDTO paramDTO) {
        return fallBackError("根据id集合获取领取资源-按维度分组");
      }

    }

}
