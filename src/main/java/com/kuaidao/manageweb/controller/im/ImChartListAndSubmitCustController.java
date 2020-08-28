package com.kuaidao.manageweb.controller.im;

import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.im.dto.SubmitCustsDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/im")
public class ImChartListAndSubmitCustController {

    /**
     * 顾问提交资源
     */
    @PostMapping("/submit")
    @ResponseBody
    public JSONResult<Boolean> submit(@RequestBody SubmitCustsDTO submitCusts) {
        return  null;
    }

    /**
     * 查询提交客户列表
     */
    @PostMapping("/submitCustList")
    @ResponseBody
    public JSONResult<List> submitCustList(@RequestBody IdEntityLong id) {
        return  null;
    }

    /**
     * 查询客户咨询品牌、客户是否提交接口
     */
    @PostMapping("/brandAndIssubmit")
    @ResponseBody
    public JSONResult<List> brandAndIssubmit(@RequestBody IdListReq ids) {
        return  null;
    }


}
