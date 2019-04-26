package com.kuaidao.manageweb.feign.clue;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueFileDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.ReleaseClueDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueSaveDTO;
import com.kuaidao.aggregation.dto.clueappiont.ClueAppiontmentDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;

@FeignClient(name = "aggregation-service-zhang", path = "/aggregation/clue/myCustomer",
        fallback = MyCustomerFeignClient.HystrixClientFallback.class)
public interface MyCustomerFeignClient {

    /**
     * 我的客户查询数据
     * 
     * @param queryDTO
     * @return
     */

    @RequestMapping(method = RequestMethod.POST, value = "/findTeleClueInfo")
    JSONResult<PageBean<CustomerClueDTO>> findTeleClueInfo(CustomerClueQueryDTO queryDTO);

    /**
     * 我的客户创建数据
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/createCustomerClue")
    JSONResult<String> createCustomerClue(ClueDTO dto);

    /**
     * 我的客户资源维护
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/updateCustomerClue")
    JSONResult<String> updateCustomerClue(ClueDTO dto);

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
     * 我的客户预约来访
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/saveAppiontment")
    JSONResult<String> saveAppiontment(ClueAppiontmentDTO dto);

    /**
     * 我的客户重单资源查询
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/queryRepeatClue")
    JSONResult<List<RepeatClueDTO>> queryRepeatClue(@RequestBody RepeatClueQueryDTO dto);

    /**
     * 保存重单申请界面
     * 
     * @param dto
     * @return
     */

    @RequestMapping(method = RequestMethod.POST, value = "/saveRepeatClue")
    JSONResult<String> saveRepeatClue(@RequestBody RepeatClueSaveDTO dto);

    /**
     * 释放资源
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/releaseClue")
    JSONResult<String> releaseClue(ReleaseClueDTO dto);

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


    /***
     * 电销 人员待跟进客户资源
     * 
     * @param queryDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/listTodayFollowClue")
    JSONResult<PageBean<CustomerClueDTO>> listTodayFollowClue(CustomerClueQueryDTO queryDto);

    @Component
    static class HystrixClientFallback implements MyCustomerFeignClient {

        private static Logger logger = LoggerFactory.getLogger(InfoAssignFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<CustomerClueDTO>> findTeleClueInfo(
                CustomerClueQueryDTO queryDTO) {
            // TODO Auto-generated method stub
            return fallBackError("分页查询线索数据失败");
        }

        @Override
        public JSONResult<String> createCustomerClue(ClueDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("创建线索数据失败");
        }

        @Override
        public JSONResult<String> updateCustomerClue(ClueDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("维护线索数据失败");
        }

        @Override
        public JSONResult<String> uploadClueFile(ClueFileDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("上传线索文件失败");
        }

        @Override
        public JSONResult<String> saveAppiontment(ClueAppiontmentDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("保存预约来访");
        }

        @Override
        public JSONResult<List<RepeatClueDTO>> queryRepeatClue(RepeatClueQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询重单");
        }

        @Override
        public JSONResult<String> releaseClue(ReleaseClueDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("释放资源");
        }

        @Override
        public JSONResult<String> saveRepeatClue(RepeatClueSaveDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("保存重单");
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

        @Override
        public JSONResult<PageBean<CustomerClueDTO>> listTodayFollowClue(
                CustomerClueQueryDTO queryDto) {
            return fallBackError("电销 人员待跟进客户资源");
        }



    }


}
