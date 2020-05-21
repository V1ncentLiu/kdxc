package com.kuaidao.manageweb.feign.merchant.clue;

import java.util.List;

import com.kuaidao.merchant.dto.clue.ClueFileDTO;
import com.kuaidao.merchant.dto.clue.ClueQueryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;
import com.kuaidao.merchant.dto.clue.ClueDTO;

@FeignClient(name = "merchant-service-1", path = "/merchant/clue/myCustomer", fallback = ClueInfoDetailFeignClient.HystrixClientFallback.class)
public interface ClueInfoDetailFeignClient {


    /**
     * 我的客户资源维护
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/updateCustomerClue")
    JSONResult<String> updateCustomerClue(ClueDTO dto);

    /**
     * 客户资源(基本信息)维护
     *
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/updateCustomerBasicInfoClue")
    JSONResult<String> updateCustomerBasicInfoClue(ClueDTO dto);

    /**
     * 我的客户资源维护查询
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/findClueInfo")
    JSONResult<ClueDTO> findClueInfo(ClueQueryDTO dto);

    /**
     * 我的客户资源文件上传
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/uploadClueFile")
    JSONResult<String> uploadClueFile(ClueFileDTO dto);

    /**
     * 查询资源文件
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/findClueFile")
    JSONResult<List<ClueFileDTO>> findClueFile(ClueQueryDTO dto);

    /**
     * 查询资源文件
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/updateCallTime")
    JSONResult<String> updateCallTime(ClueQueryDTO dto);

    /**
     * 删除资源文件
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/deleteClueFile")
    JSONResult<String> deleteClueFile(ClueFileDTO dto);

    /**
     * 保留客户资源
     * 
     * @param dto
     * @return
     */

    @RequestMapping(method = RequestMethod.POST, value = "/reserveClue")
    public JSONResult<String> reserveClue(ClueQueryDTO dto);



    @Component
    static class HystrixClientFallback implements ClueInfoDetailFeignClient {

        private static Logger logger = LoggerFactory.getLogger(InfoAssignFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(), SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }


        @Override
        public JSONResult<String> updateCustomerClue(ClueDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("维护资源数据失败");
        }

        @Override
        public JSONResult<String> updateCustomerBasicInfoClue(ClueDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("维护资源(基本信息)数据失败");
        }

        @Override
        public JSONResult<String> uploadClueFile(ClueFileDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("上传资源文件失败");
        }


        @Override
        public JSONResult<ClueDTO> findClueInfo(ClueQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("资源维护查询");
        }

        @Override
        public JSONResult<List<ClueFileDTO>> findClueFile(ClueQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询资源文件");
        }

        @Override
        public JSONResult<String> deleteClueFile(ClueFileDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("删除资源文件");
        }

        @Override
        public JSONResult<String> updateCallTime(ClueQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("更新最后拨打时间");
        }

        @Override
        public JSONResult<String> reserveClue(ClueQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("保留客户资源数据");
        }


    }


}
