package com.kuaidao.manageweb.controller.im;

import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.im.dto.submitCust.SubmitCustsDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/im")
public class ImChartListAndSubmitCustController {

    /**
     * 顾问提交资源
     */
    @PostMapping("/submit")
    @ResponseBody
    public JSONResult<Boolean> submit(@RequestBody SubmitCustsDTO submitCusts) {

        /**
         * 1:插入提交记录  接口完成
         * -- 线索插入流程待确认!!
         * 2:线索表插入线索
         * 3:返回clueID
         * 4:更新绑定关系  接口完成
         */

        return  null;
    }
    /**
     * 查询提交客户列表
     */
    @PostMapping("/submitCustList")
    @ResponseBody
    public JSONResult<List> submitCustList(@RequestBody IdEntityLong id) {
        // 这块的一套操作待确认啊
        return  null;
    }

    /**
     * 查询客户咨询品牌、客户是否提交+clueId接口
     */
    @PostMapping("/brandAndIssubmit")
    @ResponseBody
    public JSONResult<List> brandAndIssubmit(@RequestBody IdListReq ids) {
        // 直接调用集合Id查询即可 接口完成
        return  null;
    }

}
