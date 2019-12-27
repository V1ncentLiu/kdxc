package com.kuaidao.manageweb.service.telelayout;

import java.util.List;

/**
 * @Auther: admin
 * @Date: 2019/10/23 11:24
 * @Description:
 */
public interface ITelemarketingLayoutService {

  Long getTelemarketingLayout(Long teamId);

  List<Long> getTeleTeamIdOnCompanyGroup(Long companygId);
}
