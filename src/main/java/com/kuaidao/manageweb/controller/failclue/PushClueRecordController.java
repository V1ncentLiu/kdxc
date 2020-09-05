/**
 *
 */
package com.kuaidao.manageweb.controller.failclue;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.failclue.PushClueRecordFeignClient;
import com.kuaidao.publish.dto.clue.PushClueRecord;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/pushClueRecord")
public class PushClueRecordController {
    private static Logger logger = LoggerFactory.getLogger(PushClueRecordController.class);
    @Autowired
    private PushClueRecordFeignClient pushClueRecordFeignClient;

    /***
     * 查询推送资源页
     *
     * @return
     */
    @RequestMapping("/initPushClueRecordList")
    @RequiresPermissions("aggregation:pushClueRecordPage:view")
    public String initPushClueRecordList(HttpServletRequest request) {
        return "failclue/pushClueRecordPage";
    }

    /***
     * 查询推送资源
     *
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("aggregation:pushClueRecordPage:view")
    public JSONResult<List<PushClueRecord>> list(@RequestBody PushClueRecord pushClueRecord,
            HttpServletRequest request) {
        JSONResult<List<PushClueRecord>> list = pushClueRecordFeignClient.list(pushClueRecord);
        return list;
    }

}
