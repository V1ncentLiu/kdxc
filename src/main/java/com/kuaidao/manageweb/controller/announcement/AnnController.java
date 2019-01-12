package com.kuaidao.manageweb.controller.announcement;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.controller.dictionary.DictionaryController;
import com.kuaidao.manageweb.feign.SysFeign;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnouncementFeignClient;
import com.kuaidao.manageweb.feign.msgpush.MsgPushFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.rabbitmq.http.client.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      系统公告
 */

@Controller
@RequestMapping("/ann")
public class AnnController {

    private static Logger logger = LoggerFactory.getLogger(AnnController.class);

    @Autowired
    AnnouncementFeignClient announcementFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    AnnReceiveFeignClient annReceiveFeignClient;

    @Autowired
    private MsgPushFeignClient msgPushFeignClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping("/publishAnn")
    @ResponseBody
    public JSONResult saveAnn(@Valid @RequestBody AnnouncementAddAndUpdateDTO dto  , BindingResult result){
        if (result.hasErrors()) return validateParam(result);
        JSONResult jsonResult = announcementFeignClient.publishAnnouncement(dto);
        if(jsonResult.getCode().equals(0)){

            Long orgId = dto.getOrgId();

            List<UserInfoDTO> list = new ArrayList();

            if(orgId==0){ //全部用户
                UserInfoPageParam param = new UserInfoPageParam();
                JSONResult<PageBean<UserInfoDTO>> list1 = userInfoFeignClient.list(param);
                list = list1.getData().getData();
            }else{//指定组织结构下的数据。

            }

            Integer type = dto.getType();
            if(type==1||type==0){ //站内公告通知
//                annReceiveFeignClient.batchInsert();
//              消息通知
                /**
                 * void send(Message message) throwsAmqpException;
                 * void send(String routingKey, Message message) throwsAmqpException;
                 * void send(String exchange, String routingKey, Message message) throwsAmqpException;
                 */

//                amqpTemplate.convertAndSend("","",new Message());
            }
            if(type==2||type==0){ //短信
                for(UserInfoDTO userInfo:list){
//                  获取电话：发送短信
//                  构建短信模板
                    String phone = userInfo.getPhone();
//                    msgPushFeignClient.setMessage();
                }
            }

        }
        return jsonResult;
    }

    @RequestMapping("/queryOneAnn")
    @ResponseBody
    public JSONResult queryOneAnn(@RequestBody IdEntity idEntity){
        return  announcementFeignClient.findByPrimaryKeyAnnouncement(idEntity);
    }

    @RequestMapping("/queryAnnList")
    @ResponseBody
    public JSONResult queryAnnList(@RequestBody AnnouncementQueryDTO dto){

        Date date1 = dto.getDate1();
        Date date2 = dto.getDate2();

        if(date1!=null && date2!=null ){
            if(date1.getTime()>date2.getTime()){
                return new JSONResult().fail("-1","时间选项，开始时间大于结束时间!");
            }
        }

        JSONResult<PageBean<AnnouncementRespDTO>> pageBeanJSONResult = announcementFeignClient.queryAnnouncement(dto);
        return  pageBeanJSONResult;
    }


    @RequestMapping("/annlistPage")
    public String listPage(){
        logger.info("--------------------------------------跳转到列表页面-----------------------------------------------");
        return "ann/annListPage";
    }

    @RequestMapping("/annPublishPage")
    public String itemListPage(){
        logger.info("--------------------------------------跳转到公告页面-----------------------------------------------");
        return "ann/annPublishPage";
    }

    /**
     * 错误参数检验
     * @param result
     * @return
     */
    private JSONResult validateParam(BindingResult result) {
        List<ObjectError> list = result.getAllErrors();
        for (ObjectError error : list) {
            logger.error("参数校验失败：{},错误信息：{}", error.getArguments(), error.getDefaultMessage());
        }
        return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
    }
}
