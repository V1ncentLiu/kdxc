package com.kuaidao.manageweb.service.telelayout.impl;

import com.kuaidao.businessconfig.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.manageweb.service.telelayout.ITelemarketingLayoutService;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: admin
 * @Date: 2019/10/23 11:25
 * @Description:
 */
@Service
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
  @Override
  public List<Long> getTeleTeamIdOnCompanyGroup(Long companygId){
    TelemarketingLayoutDTO queryDTO = new TelemarketingLayoutDTO();
    queryDTO.setCompanyGroupId(companygId);
    JSONResult<List<OrganizationDTO>> layout = telemarketingLayoutFeignClient
        .getdxListByCompanyGroupId(queryDTO);
    Long companyGroupId =0L;
    List<Long> teleTeamIdList = new ArrayList();
    if(CommonUtil.resultCheck(layout)){
      List<OrganizationDTO> data = layout.getData();
      teleTeamIdList = data.stream().map(a ->a.getId())
          .collect(Collectors.toList());
    }
    return teleTeamIdList;
  }


}
