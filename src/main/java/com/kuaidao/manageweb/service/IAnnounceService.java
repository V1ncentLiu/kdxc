package com.kuaidao.manageweb.service;

import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;

/**
 * @Auther: admin
 * @Date: 2019/4/8 19:40
 * @Description:
 */
public interface IAnnounceService {

    public void sendMessage( AnnouncementAddAndUpdateDTO dto);

    public void sendNewMessage(AnnouncementAddAndUpdateDTO dto);
}
