package com.kuaidao.manageweb.controller.tracking;

import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordReqDTO;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordRespDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingReqDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingRespDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.releaserecord.ReleaseRecordFeignClient;
import com.kuaidao.manageweb.feign.tracking.TrackingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yangbiao
 * 接口层
 * Created  on 2019-2-12 15:06:38
 *  资源释放记录 对外接口类
 */

@RestController
@RequestMapping("/aggregation/tracking")
public class TrackingRecordController {

    private static Logger logger = LoggerFactory.getLogger(TrackingRecordController.class);

    @Autowired
    private TrackingFeignClient trackingFeignClient;

    /**
     *  查询：不分页
     */
    @RequestMapping("/queryList")
    public JSONResult<List<TrackingRespDTO>> queryPageList(@RequestBody TrackingReqDTO dto){
        JSONResult<List<TrackingRespDTO>> listJSONResult = trackingFeignClient.queryList(dto);
        return listJSONResult;
    }

}
