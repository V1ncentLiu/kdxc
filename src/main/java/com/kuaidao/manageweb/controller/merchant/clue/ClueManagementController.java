package com.kuaidao.manageweb.controller.merchant.clue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.clue.ClueManagementFeignClient;
import com.kuaidao.merchant.dto.clue.ClueAssignReqDto;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;

/**
 * 资源管理
 *
 * @author:fanjd
 * @date:2019/9/10
 * @since 1.0.0
 */
@RestController
@RequestMapping("/clue/management")
public class ClueManagementController {
    @Autowired
    private ClueManagementFeignClient clueManagementFeignClient;

    /**
     * 查询资源列表
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/queryPage")
    public JSONResult<PageBean<ClueManagementDto>> queryPage(@RequestBody ClueManagementParamDto pageParam) {

        return clueManagementFeignClient.queryPage(pageParam);
    }

    /**
     * 资源分配
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/clueAssign")
    public JSONResult<String> clueAssign(@RequestBody ClueAssignReqDto reqDto) {

        return clueManagementFeignClient.clueAssign(reqDto);
    }

}
