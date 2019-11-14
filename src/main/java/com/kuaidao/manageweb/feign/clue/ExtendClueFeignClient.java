package com.kuaidao.manageweb.feign.clue;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.aggregation.dto.clue.ClueAgendaTaskDTO;
import com.kuaidao.aggregation.dto.clue.ClueAgendaTaskQueryDTO;
import com.kuaidao.aggregation.dto.clue.ClueDistributionedTaskDTO;
import com.kuaidao.aggregation.dto.clue.ClueDistributionedTaskQueryDTO;
import com.kuaidao.aggregation.dto.clue.PushClueReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;

@FeignClient(name = "aggregation-service", path = "/aggregation/extend/clueManager",
        fallback = ExtendClueFeignClient.HystrixClientFallback.class)
public interface ExtendClueFeignClient {

    /**
     * 待审核数据分页查询
     *
     * @param queryDto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/queryPageAgendaTask")
    public JSONResult<PageBean<ClueAgendaTaskDTO>> queryPageAgendaTask(
            @RequestBody ClueAgendaTaskQueryDTO queryDto);

    /**
     * 已审核列表查询
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/queryPageDistributionedTask")
    public JSONResult<PageBean<ClueDistributionedTaskDTO>> queryPageDistributionedTask(
            @RequestBody ClueDistributionedTaskQueryDTO queryDto);

    /**
     * 已审核列表查询
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/autoAllocationTask")
    public JSONResult<Integer> autoAllocationTask(@RequestBody IdListLongReq list);

    /**
     * 新建资源
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/createClue")
    public JSONResult<String> createClue(@RequestBody PushClueReq pushClueReq);

    /**
     * 编辑资源
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/distributedUpdateClue")
    public JSONResult<String> distributedUpdateClue(@RequestBody PushClueReq pushClueReq);

    /**
     * 撤回资源
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/recallClue")
    public JSONResult<String> recallClue(@RequestBody IdEntityLong idEntityLong);

    /**
     * 批量撤回资源
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/recallClues")
    public JSONResult<String> recallClues(@RequestBody IdListLongReq idListLongReq);

    /**
     * 删除资源
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/deleteResource")
    public JSONResult<String> deleteResource(@RequestBody IdListLongReq idListLongReq);

    /**
     * 导入资源
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/import")
    public JSONResult<List<PushClueReq>> importclue(@RequestBody List<PushClueReq> list);

    /**
     * 导出线索：线索情况
     */
    @RequestMapping("/findClues")
    public JSONResult<List<ClueDistributionedTaskDTO>> findClues(
            @RequestBody ClueDistributionedTaskQueryDTO queryDto);

    /**
     * 导出线索：资源沟通记录
     */
    @RequestMapping("/findCommunicateRecords")
    public JSONResult<List<ClueDistributionedTaskDTO>> findCommunicateRecords(
            @RequestBody ClueDistributionedTaskQueryDTO queryDto);

    /**
     * 导出线索：线索情况
     */
    @RequestMapping("/findCluesCount")
    public JSONResult<Long> findCluesCount(@RequestBody ClueDistributionedTaskQueryDTO queryDto);

    @Component
    static class HystrixClientFallback implements ExtendClueFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ExtendClueFeignClient.class);

        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<PageBean<ClueAgendaTaskDTO>> queryPageAgendaTask(
                ClueAgendaTaskQueryDTO queryDto) {
            // TODO Auto-generated method stub
            return fallBackError("查询待审核数据");
        }

        @Override
        public JSONResult<PageBean<ClueDistributionedTaskDTO>> queryPageDistributionedTask(
                ClueDistributionedTaskQueryDTO queryDto) {
            // TODO Auto-generated method stub
            return fallBackError("查询已审核业务");
        }

        @Override
        public JSONResult<Integer> autoAllocationTask(IdListLongReq list) {
            // TODO Auto-generated method stub
            return fallBackError("自动分配任务");
        }

        @Override
        public JSONResult<String> createClue(@RequestBody PushClueReq pushClueReq) {
            // TODO Auto-generated method stub
            return fallBackError("新建资源");
        }

        @Override
        public JSONResult<String> distributedUpdateClue(@RequestBody PushClueReq pushClueReq) {
            // TODO Auto-generated method stub
            return fallBackError("编辑资源");
        }

        @Override
        public JSONResult<String> recallClue(@RequestBody IdEntityLong idEntityLong) {
            // TODO Auto-generated method stub
            return fallBackError("撤回资源");
        }

        @Override
        public JSONResult<String> recallClues(@RequestBody IdListLongReq idEntityLong) {
            // TODO Auto-generated method stub
            return fallBackError("批量撤回资源");
        }

        @Override
        public JSONResult<String> deleteResource(@RequestBody IdListLongReq idEntityLong) {
            // TODO Auto-generated method stub
            return fallBackError("删除资源");
        }

        @Override
        public JSONResult<List<PushClueReq>> importclue(List<PushClueReq> list) {
            // TODO Auto-generated method stub
            return fallBackError("导入资源");
        }

        @Override
        public JSONResult<List<ClueDistributionedTaskDTO>> findClues(
                ClueDistributionedTaskQueryDTO queryDto) {
            return fallBackError("导出线索：资源情况");
        }

        @Override
        public JSONResult<List<ClueDistributionedTaskDTO>> findCommunicateRecords(
                ClueDistributionedTaskQueryDTO queryDto) {
            return fallBackError("导出线索：资源沟通记录");
        }

        @Override
        public JSONResult<Long> findCluesCount(ClueDistributionedTaskQueryDTO queryDto) {
            return fallBackError("导出线索：资源数量");
        }

    }

}
