package com.kuaidao.manageweb.service.impl;

import com.kuaidao.common.constant.SmsTempIdConstant;
import com.kuaidao.common.constant.emun.sys.AnnBuinessTypeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.EmailSend;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.msgpush.MsgPushFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.service.IAnnounceService;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.msgpush.dto.SmsTemplateCodeReq;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveAddAndUpdateDTO;
import com.kuaidao.sys.dto.user.MerchantUserReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AnnounceServiceImpl implements IAnnounceService {

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    AnnReceiveFeignClient annReceiveFeignClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private EmailSend emailSend;

    @Autowired
    private Configuration configuration;

    @Autowired
    private MsgPushFeignClient msgPushFeignClient;

    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;



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



    @Async("threadPoolExecutor")
    @Override
    public void sendNewMessage(AnnouncementAddAndUpdateDTO dto) {
        long startTime = System.currentTimeMillis();
        List<UserInfoDTO> userList = dto.getUserIds();
        List<MerchantUserReq> merchantUserReqList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userList)){
            List<AnnReceiveAddAndUpdateDTO> annrList = new ArrayList<>();
            Map<Long,Long> userAnnMap = new HashMap<>();
            for (UserInfoDTO userinfo : userList) {
                AnnReceiveAddAndUpdateDTO annDto = new AnnReceiveAddAndUpdateDTO();
                long annRecId = IdUtil.getUUID();
                annDto.setId(annRecId);
                annDto.setReceiveUser(userinfo.getId());
                annDto.setAnnouncementId(dto.getId());
                annrList.add(annDto);
                userAnnMap.put(userinfo.getId(),annRecId);
                MerchantUserReq merchantUserReq = new MerchantUserReq();
                merchantUserReq.setUserId(userinfo.getId());
                merchantUserReq.setChargeAgreeAnnId(dto.getId());
                merchantUserReqList.add(merchantUserReq);
            }
            //消息批量入库
            annReceiveFeignClient.batchInsert(annrList);
            sendOtherMessaage(userList,dto,userAnnMap);
            merchantUserInfoFeignClient.batchUpdateMerchantUser(merchantUserReqList);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }

    private void sendOtherMessaage(List<UserInfoDTO> list,AnnouncementAddAndUpdateDTO dto,  Map<Long,Long> userAnnMap) {
        for (UserInfoDTO userInfoDTO : list) {
            if(dto.getBusinessType().equals(AnnBuinessTypeEnum.招商宝充值协议)){
                //这个就要进行判断
                if(StringUtils.isNotBlank(dto.getTypes())){
                    String[] types = dto.getTypes().split(",");
                    for (String type : types) {
                        int typeInt = Integer.parseInt(type);
                        switch (typeInt){
                            case 0:
                                //全部
                                //sendSiteMessage(userInfoDTO,dto,userAnnMap.get(userInfoDTO.getId()));
                                sendSmsMessage(userInfoDTO,dto);
                                sendEmailMessage(userInfoDTO,dto);
                                break;
                            case 1:
                                //站内
                               // sendSiteMessage(userInfoDTO,dto,userAnnMap.get(userInfoDTO.getId()));
                                break;
                            case 2:
                                //短信
                                sendSmsMessage(userInfoDTO,dto);
                                break;
                            case 3:
                                //邮件
                                sendEmailMessage(userInfoDTO,dto);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }else{
                sendSiteMessage(userInfoDTO,dto,userAnnMap.get(userInfoDTO.getId()));
            }
        }
    }

    /**
     * 短信模版
     * @param userInfoDTO
     * @param dto
     */
    private void sendSmsMessage(UserInfoDTO userInfoDTO, AnnouncementAddAndUpdateDTO dto) {
        SmsTemplateCodeReq smsTemplateCodeReq = new SmsTemplateCodeReq();
        smsTemplateCodeReq.setMobile(userInfoDTO.getPhone());
        smsTemplateCodeReq.setTempId(SmsTempIdConstant.SEND_MSG_TEMPID_MERCHANT_RECHARGE_ANN);
        Map<String,Object> param = new HashMap<>();
        param.put("time", DateUtil.convert2String(dto.getCreateTime(),DateUtil.ymd));
        param.put("title",dto.getTitle());
        smsTemplateCodeReq.setTempPara(param);
        JSONResult<String> smsFlag = msgPushFeignClient.sendTempSms(smsTemplateCodeReq);
        if (smsFlag == null || !JSONResult.SUCCESS.equals(smsFlag.getCode())) {
            log.error("audit pass sms sub error ,res{{}}", smsFlag);
        }
    }

    /**
     * 邮箱模版
     * @param userInfoDTO
     * @param dto
     */
    private void sendEmailMessage(UserInfoDTO userInfoDTO, AnnouncementAddAndUpdateDTO dto) {

        try {
            Map<String,Object> dataMap = new HashMap<>();

            dataMap.put("time",DateUtil.convert2String(dto.getCreateTime(),DateUtil.ymd));
            String emailContent = FreeMarkerTemplateUtils.processTemplateIntoString(this.configuration.getTemplate("email/huijuMerchantCharge.html"),dataMap);
            emailSend.sendEmail("【餐盟严选】"+dto.getTitle(), emailContent, userInfoDTO.getEmail());
        } catch (Exception e) {
            log.error("AnnounceServiceImpl sendEmailMessage userInfoDTO[{}],e[{}] ",userInfoDTO,e);
        }

    }


    /**
     * 站内模版
     * @param userInfoDTO
     * @param dto
     * @param id
     */
    private void sendSiteMessage(UserInfoDTO userInfoDTO,AnnouncementAddAndUpdateDTO dto, Long id) {
        amqpTemplate.convertAndSend("amq.topic",
                userInfoDTO.getOrgId() + "." + userInfoDTO.getId(),
                "announce," + dto.getId() + "," + id);
    }

    /**
     * 获取发送的用户集合
     * @param dto
     * @return
     */
    private List<UserInfoDTO> getUserList(AnnouncementAddAndUpdateDTO dto) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        if (dto.getOrgId() != 0) {
            // 获取多个组织下的用户，通过组织ID进行获取。
            List<Long> orgids = dto.getOrgids();
            userOrgRoleReq.setOrgIdList(orgids);
        }
        JSONResult<List<UserInfoDTO>> list1 = userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        if (list1.getCode().equals(JSONResult.SUCCESS)) {
            return list1.getData();
        }
        return null;
    }
}
