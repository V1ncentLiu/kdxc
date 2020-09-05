package com.kuaidao.manageweb.service.im;

import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

import java.util.List;

public interface ImMassageService {


    boolean transOnlineLeaveLog(UserInfoDTO user, List<RoleInfoDTO> roleList , Integer onlineLeaveType );
}
