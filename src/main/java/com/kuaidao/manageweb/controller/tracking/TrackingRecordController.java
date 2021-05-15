package com.kuaidao.manageweb.controller.tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kuaidao.agentservice.dto.tracking.AgentClueTrackingDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingRemarkDTO;
import com.kuaidao.common.constant.StageContant;
import com.kuaidao.manageweb.feign.agent.AgentServiceWapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private AgentServiceWapper agentServiceWapper;

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
     * 查询跟进记录，电销/经纪/顾问
     */
    @RequestMapping("/queryListWithSale")
    public JSONResult<List<TrackingRespDTO>> queryListWithSale(@RequestBody TrackingReqDTO dto) {
        if(dto ==null || dto.getStage() ==null){
            dto.setStage(StageContant.STAGE_TELE);
        }
        JSONResult<List<TrackingRespDTO>> listJSONResult = trackingFeignClient.queryList(dto);

        List<Long> objects = new ArrayList<>();
        objects.add(dto.getClueId());
        List<AgentClueTrackingDTO> agentTrackingList = agentServiceWapper.queryTracksByClueIdList(objects);
        List<TrackingRespDTO> collect = agentTrackingList.stream().map(a -> {
            TrackingRespDTO trackingRespDTO = new TrackingRespDTO();
            trackingRespDTO.setId(a.getId());
            trackingRespDTO.setCustomerStatusName(a.getCustomerStatusName());
            trackingRespDTO.setCustomerStatus(a.getCustomerStatus());
            trackingRespDTO.setCallDetailsName(a.getCallDetailsName());
            trackingRespDTO.setCreateUserName(a.getCreateUserName());
            trackingRespDTO.setIsCall(a.getCall());
            trackingRespDTO.setCallTime(a.getTrackingTime());
            trackingRespDTO.setFocusPoint(a.getFocusPoint());
            trackingRespDTO.setNextVisitTime(a.getNextVisitTime());
            trackingRespDTO.setClueId(a.getClueId());
            return trackingRespDTO;
        }).collect(Collectors.toList());
        listJSONResult.data().addAll(collect);
        return listJSONResult;
    }





    /**
     * 查询：不分页
     */
    @RequestMapping("/updateTrackingRemark")
    @RequiresPermissions("customerManager:updateTrackingRemark")
    public JSONResult<String> updateTrackingRemark(@RequestBody TrackingRemarkDTO dto) {
        JSONResult<String> jsonResult =  trackingFeignClient.updateTrackingRemark(dto);
        return jsonResult;
    }
}
