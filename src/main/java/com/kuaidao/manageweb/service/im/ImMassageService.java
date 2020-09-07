package com.kuaidao.manageweb.service.im;

import java.util.List;
import java.util.Map;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

public interface ImMassageService {


    Map<String, Object> transOnlineLeaveLog(UserInfoDTO user, List<RoleInfoDTO> roleList,
            Integer onlineLeaveType);
}
