package com.kuaidao.manageweb.service;

import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;

import java.util.List;

/**
 * @author admin
 */
public interface NextRecordService {


    /**
     * 我的客户记录压入缓存
     * @param userId
     * @param customerClueDTOList
     */
    void pushList(Long userId , List<CustomerClueDTO> customerClueDTOList);

    /**
     * 获得下一条记录
     * @param userId
     * @param currentClueId
     * @return
     */
    Long next( Long userId , Long currentClueId);
}
