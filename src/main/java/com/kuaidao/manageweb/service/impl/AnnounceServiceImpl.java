package com.kuaidao.manageweb.service.impl;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.service.IAnnounceService;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveAddAndUpdateDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AnnounceServiceImpl implements IAnnounceService {

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    AnnReceiveFeignClient annReceiveFeignClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Async("threadPoolExecutor")
    @Override
    public void sendMessage(AnnouncementAddAndUpdateDTO dto) {
        long startTime = System.currentTimeMillis();
        Long orgId = dto.getOrgId();
        List<UserInfoDTO> list = new ArrayList();
        List<Long> idsList = new ArrayList<>();

        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        if (orgId == 0) { // 全部用户

        } else {// 指定组织结构下的数据。
            // 获取多个组织下的用户，通过组织ID进行获取。
            List<Long> orgids = dto.getOrgids();
            userOrgRoleReq.setOrgIdList(orgids);
        }
        JSONResult<List<UserInfoDTO>> list1 = userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);

        if (list1.getCode().equals("0")) {
            list = list1.getData();
            InsertBatch(list1, dto.getType(), idsList, dto.getId());
        }
        Integer type = dto.getType();
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }

    public void sendMessage(Integer type, List<UserInfoDTO> list, List<Long> idsList, Long annId) {
        if (type == 1 || type == 0) { // 站内公告通知
            for (int i = 0; i < list.size(); i++) {
                Long aLong = idsList.get(i);
                UserInfoDTO userInfo = list.get(i);
                amqpTemplate.convertAndSend("amq.topic",
                        userInfo.getOrgId() + "." + userInfo.getId(),
                        "announce," + annId + "," + aLong);
            }
        }
    }

    public void InsertBatch(JSONResult<List<UserInfoDTO>> list1, Integer type, List<Long> idsList,
            Long annId) {
        List<UserInfoDTO> list = list1.getData();
        List<AnnReceiveAddAndUpdateDTO> annrList = new ArrayList<AnnReceiveAddAndUpdateDTO>();
        for (UserInfoDTO userinfo : list) {
            AnnReceiveAddAndUpdateDTO annDto = new AnnReceiveAddAndUpdateDTO();
            long annRecId = IdUtil.getUUID();
            idsList.add(annRecId);
            annDto.setId(annRecId);
            annDto.setReceiveUser(userinfo.getId());
            annDto.setAnnouncementId(annId);
            annrList.add(annDto);
        }
        annReceiveFeignClient.batchInsert(annrList);
        sendMessage(type, list, idsList, annId);
    }


}
