package com.kuaidao.manageweb.service.im.impl;

import com.alibaba.fastjson.JSON;
import com.kuaidao.common.constant.BusinessLineConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.custservice.dto.onlineleave.SaleOnlineLeaveLogReq;
import com.kuaidao.manageweb.feign.im.CustomerInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.service.im.ImMassageService;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ImMassageServiceImpl implements ImMassageService {


    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private CustomerInfoFeignClient customerInfoFeignClient;
    /**
     * 在线离线后台调用日志接口
     * @param user
     * @param roleList
     * @param onlineLeaveType
     */
    public boolean transOnlineLeaveLog(UserInfoDTO user, List<RoleInfoDTO> roleList , Integer onlineLeaveType ) {
        if(null == user || StringUtils.isBlank(user.getUsername())){
            log.warn("user is null or username is null! user = {} " , JSON.toJSONString(user));
            return false;
        }
        // 角色为空
        if(CollectionUtils.isEmpty(roleList)){
            UserInfoReq userInfoReq = new UserInfoReq();
            userInfoReq.setUsername(user.getUsername());
            // 用户登录名查询
            JSONResult<UserInfoDTO> getbyUserName = userInfoFeignClient.getbyUserName(userInfoReq);
            if(null == getbyUserName || !JSONResult.SUCCESS.equals(getbyUserName.getCode()) ||
                    null == getbyUserName.getData() || CollectionUtils.isEmpty(roleList = getbyUserName.getData().getRoleList())){
                log.warn("user is null! user = {} " , JSON.toJSONString(user));
                return false;
            }
        }
        Map<String, String> roleMap = roleList.stream().map(RoleInfoDTO::getRoleCode).collect(Collectors.toMap(k -> k, v -> v, (x, y) -> x));
        // 电销顾问 & 业务线是的商机盒子的
        if(roleMap.containsKey(RoleCodeEnum.DXCYGW.name()) && ((Integer) BusinessLineConstant.SHANGJI).equals(user.getBusinessLine())){
            SaleOnlineLeaveLogReq saleOnlineLeaveLogReq = new SaleOnlineLeaveLogReq();
            saleOnlineLeaveLogReq.setOperationType(onlineLeaveType);
            saleOnlineLeaveLogReq.setTeleSaleId(user.getId()); // 顾问Id
            customerInfoFeignClient.onlineleave(saleOnlineLeaveLogReq);
        }
        return true ;
    }
}
