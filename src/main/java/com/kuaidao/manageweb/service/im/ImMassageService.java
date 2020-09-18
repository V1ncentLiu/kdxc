package com.kuaidao.manageweb.service.im;

import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

import java.util.List;
import java.util.Map;

public interface ImMassageService {


    Map<String, Object> transOnlineLeaveLog(UserInfoDTO user, List<RoleInfoDTO> roleList,
            Integer onlineLeaveType);

    void transOnlineLeaveLogUpdateStatusEnable(UserInfoDTO user, List<RoleInfoDTO> roleList,
                                               Integer onlineLeaveType);
}
