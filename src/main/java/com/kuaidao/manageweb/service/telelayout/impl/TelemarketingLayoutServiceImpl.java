package com.kuaidao.manageweb.service.telelayout.impl;

import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.manageweb.service.telelayout.ITelemarketingLayoutService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: admin
 * @Date: 2019/10/23 11:25
 * @Description:
 */
public class TelemarketingLayoutServiceImpl implements ITelemarketingLayoutService {


  @Autowired
  private TelemarketingLayoutFeignClient telemarketingLayoutFeignClient;



  @Override
  public Long getTelemarketingLayout(Long teamId){
    TelemarketingLayoutDTO queryDTO = new TelemarketingLayoutDTO();
    queryDTO.setTelemarketingTeamId(teamId);
    JSONResult<TelemarketingLayoutDTO> layout = telemarketingLayoutFeignClient
        .getTelemarketingLayoutByTeamId(queryDTO);
    Long companyGroupId =0L;
    if(CommonUtil.resultCheck(layout)){
      TelemarketingLayoutDTO data = layout.getData();
      companyGroupId = data.getCompanyGroupId();
    }
    return companyGroupId;
  }


  public Long getTeleTeamIdOnCompanyGroup(Long companygROUP){
    TelemarketingLayoutDTO queryDTO = new TelemarketingLayoutDTO();



    JSONResult<TelemarketingLayoutDTO> layout = telemarketingLayoutFeignClient
        .getTelemarketingLayoutByTeamId(queryDTO);
    Long companyGroupId =0L;
    if(CommonUtil.resultCheck(layout)){
      TelemarketingLayoutDTO data = layout.getData();
      companyGroupId = data.getCompanyGroupId();
    }
    return companyGroupId;
  }


}
