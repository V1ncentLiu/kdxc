package com.kuaidao.manageweb.feign.agent;

import com.kuaidao.agentservice.dto.call.CallRecordCountDTO;
import com.kuaidao.agentservice.dto.call.CallRecordReqDTO;
import com.kuaidao.agentservice.dto.call.CallRecordRespDTO;
import com.kuaidao.agentservice.dto.tracking.AgentClueTrackingDTO;
import com.kuaidao.agentservice.dto.tracking.TrackingReqDTO;
import com.kuaidao.common.entity.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Component
public class AgentServiceWapper {

    @Autowired
    AgentServiceFeignClient agentServiceFeignClient;

    public List<AgentClueTrackingDTO> queryTracksByClueIdList(List<Long> clueIdList){
        TrackingReqDTO dto = new TrackingReqDTO();
        dto.setClueIdList(clueIdList);
        JSONResult<List<AgentClueTrackingDTO>> result = agentServiceFeignClient.queryTrackList(dto);
        return result.data(new ArrayList<>());
    }

    public List<CallRecordCountDTO> countCallRecordTotalByClueIdList(List<Long> clueIdList){
         CallRecordReqDTO myCallRecordReqDTO = new CallRecordReqDTO();
        myCallRecordReqDTO.setClueIdList(clueIdList);
        JSONResult<List<CallRecordCountDTO>> result = agentServiceFeignClient.countCallRecordTotalByClueIdList(myCallRecordReqDTO);
        return result.data(new ArrayList<>());
    }

    public List<CallRecordRespDTO> listAgentCallRecordByParamsNoPage (String clueId){
        CallRecordReqDTO dto = new CallRecordReqDTO();
        dto.setClueId(clueId);
        return agentServiceFeignClient.listAgentCallRecordByParamsNoPage(dto).data(new ArrayList<>());
    }


}
