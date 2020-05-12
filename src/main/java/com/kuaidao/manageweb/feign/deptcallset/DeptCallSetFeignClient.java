package com.kuaidao.manageweb.feign.deptcallset;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.deptcallset.DeptCallSetAddAndUpdateDTO;
import com.kuaidao.aggregation.dto.deptcallset.DeptCallSetQueryDTO;
import com.kuaidao.aggregation.dto.deptcallset.DeptCallSetRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;


/**
 *
 * 功能描述: 部门呼叫设置
 * 
 * @author yangbiao
 * @date: 2019/1/8 17:35
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/deptcallset",
        fallback = DeptCallSetFeignClient.HystrixClientFallback.class)
public interface DeptCallSetFeignClient {

    /**
     * 插入
     * 
     * @param dto
     * @return
     */
    @PostMapping("/saveDeptCallSet")
    public JSONResult saveDeptCallSet(@RequestBody DeptCallSetAddAndUpdateDTO dto);

    /**
     * 分页查询
     * 
     * @param dto
     * @return
     */
    @PostMapping("/queryDeptCallSetList")
    public JSONResult<PageBean<DeptCallSetRespDTO>> queryDeptCallSetList(
            @RequestBody DeptCallSetQueryDTO dto);

    /**
     * 查询明细
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/queryOne")
    public JSONResult<DeptCallSetRespDTO> queryOne(@RequestBody IdEntity idEntity);

    /**
     * 更新全部
     * 
     * @param dto
     * @return
     */
    @PostMapping("/upateDeptCallSet")
    public JSONResult updateDeptCallSets(@RequestBody DeptCallSetAddAndUpdateDTO dto);

    /**
     * 更新不为空的字段
     * 
     * @param dto
     * @return
     */
    @PostMapping("/upateDeptCallSetForNotNull")
    public JSONResult updateDeptCallSetsForNotNull(@RequestBody DeptCallSetAddAndUpdateDTO dto);

    /**
     * 导入
     * 
     * @param list
     * @return
     */
    @PostMapping("/import")
    public JSONResult importDeptCallSets(@RequestBody List<DeptCallSetAddAndUpdateDTO> list);

    /**
     * 通过组织机构ID获取部门呼叫设置
     * 
     * @param orgid
     * @return
     */
    @PostMapping("/queryByOrgid")
    public JSONResult<DeptCallSetRespDTO> queryByOrgid(@RequestBody IdEntity orgid);

    @Component
    static class HystrixClientFallback implements DeptCallSetFeignClient {

        private static Logger logger = LoggerFactory.getLogger(HystrixClientFallback.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult saveDeptCallSet(DeptCallSetAddAndUpdateDTO dto) {
            return fallBackError("新增部门呼叫设置");
        }

        @Override
        public JSONResult<PageBean<DeptCallSetRespDTO>> queryDeptCallSetList(
                DeptCallSetQueryDTO dto) {
            return fallBackError("部门呼叫设置列表查询");
        }

        @Override
        public JSONResult<DeptCallSetRespDTO> queryOne(IdEntity idEntity) {
            return fallBackError("查询部门呼叫设置详细信息");
        }

        @Override
        public JSONResult updateDeptCallSets(DeptCallSetAddAndUpdateDTO dto) {
            return fallBackError("更新部门呼叫设置");
        }

        @Override
        public JSONResult updateDeptCallSetsForNotNull(DeptCallSetAddAndUpdateDTO dto) {
            return fallBackError("更新部门呼叫设置-更新不为空的字段");
        }

        @Override
        public JSONResult importDeptCallSets(List<DeptCallSetAddAndUpdateDTO> list) {
            return fallBackError("批量导入部门呼叫设置");
        }

        @Override
        public JSONResult queryByOrgid(IdEntity orgid) {
            return fallBackError("组织机构ID查询部门呼叫设置");
        }
    }

}
