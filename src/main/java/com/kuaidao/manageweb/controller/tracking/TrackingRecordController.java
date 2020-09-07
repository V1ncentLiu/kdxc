package com.kuaidao.manageweb.controller.tracking;

import java.util.List;

import com.kuaidao.aggregation.dto.tracking.TrackingRemarkDTO;
import com.kuaidao.common.constant.StageContant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kuaidao.aggregation.dto.tracking.TrackingReqDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingRespDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.tracking.TrackingFeignClient;

/**
 * @author yangbiao 接口层 Created on 2019-2-12 15:06:38 资源释放记录 对外接口类
 */

@RestController
@RequestMapping("/aggregation/tracking")
public class TrackingRecordController {

    private static Logger logger = LoggerFactory.getLogger(TrackingRecordController.class);

    @Autowired
    private TrackingFeignClient trackingFeignClient;

    /**
     * 查询：不分页
     */
    @RequestMapping("/queryList")
    public JSONResult<List<TrackingRespDTO>> queryPageList(@RequestBody TrackingReqDTO dto) {
        if(dto ==null || dto.getStage() ==null){
            dto.setStage(StageContant.STAGE_TELE);
        }
        JSONResult<List<TrackingRespDTO>> listJSONResult = trackingFeignClient.queryList(dto);
        return listJSONResult;
    }
    /**
     * 查询：不分页
     */
    @RequestMapping("/updateTrackingRemark")
    public JSONResult<String> updateTrackingRemark(@RequestBody TrackingRemarkDTO dto) {
        JSONResult<String> jsonResult =  trackingFeignClient.updateTrackingRemark(dto);
        return jsonResult;
    }
}
