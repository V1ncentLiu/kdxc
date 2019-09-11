package com.kuaidao.manageweb.controller.merchant.clue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.merchant.dto.clue.ClueAssignReqDto;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;
import com.kuaidao.merchant.service.clue.IClueManagementService;

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
    private IClueManagementService clueManagementService;

    /**
     * 查询资源列表
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/queryPage")
    public JSONResult<PageBean<ClueManagementDto>> queryPage(@RequestBody ClueManagementParamDto pageParam) {
        PageBean<ClueManagementDto> pageBean = clueManagementService.queryPage(pageParam);
        return new JSONResult<PageBean<ClueManagementDto>>().success(pageBean);
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
        clueManagementService.clueAssign(reqDto);
        return new JSONResult<String>().success(null);
    }

    /**
     * 查询不分页
     * 
     * @author: Fanjd
     * @param pageParam
     * @return: com.kuaidao.common.entity.JSONResult<java.lang.String>
     * @Date: 2019/9/11 9:37
     * @since: 1.0.0
     **/
    @ResponseBody
    @PostMapping("/listNoPage")
    public JSONResult<List<ClueManagementDto>> listNoPage(@RequestBody ClueManagementParamDto pageParam) {

        clueManagementService.queryPage(pageParam);
        return new JSONResult<List<ClueManagementDto>>().success(null);
    }
}
