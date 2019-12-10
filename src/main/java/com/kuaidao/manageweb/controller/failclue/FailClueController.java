/**
 *
 */
package com.kuaidao.manageweb.controller.failclue;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.failclue.FailClueFeignClient;
import com.kuaidao.publish.dto.clue.FailCluePageParam;
import com.kuaidao.publish.dto.clue.FailPushClue;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/failClue")
public class FailClueController {
    private static Logger logger = LoggerFactory.getLogger(FailClueController.class);
    @Autowired
    private FailClueFeignClient failClueFeignClient;

    /***
     * 失败资源列表页
     *
     * @return
     */
    @RequestMapping("/initFailClueList")
    @RequiresPermissions("aggregation:failClueManager:view")
    public String initCompanyList(HttpServletRequest request) {
        return "failclue/failClueManagerPage";
    }

    /***
     * 失败资源列表
     *
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("aggregation:failClueManager:view")
    public JSONResult<PageBean<FailPushClue>> list(@RequestBody FailCluePageParam pageParam,
            HttpServletRequest request) {
        JSONResult<PageBean<FailPushClue>> list = failClueFeignClient.failClueRecord(pageParam);
        return list;
    }


    /**
     * 重新推送失败资源
     *
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/push")
    @ResponseBody
    @RequiresPermissions("aggregation:failClueManager:push")
    @LogRecord(description = "重新推送失败资源", operationType = OperationType.PUSH,
            menuName = MenuEnum.FAIL_CLUE_MANAGEMENT)
    public JSONResult push(@Valid @RequestBody IdListReq idListReq, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return failClueFeignClient.pushFailClue(idListReq);
    }



}
