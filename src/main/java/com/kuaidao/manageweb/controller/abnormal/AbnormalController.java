package com.kuaidao.manageweb.controller.abnormal;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnouncementFeignClient;
import com.kuaidao.manageweb.feign.msgpush.MsgPushFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/abnoramluser")
public class AbnormalController {

    private static Logger logger = LoggerFactory.getLogger(AbnormalController.class);

    @RequestMapping("/abnormalUserPage")
    public String pageIndex(){
        return "abnormal/abnormalUserList";
    }


    @RequestMapping("/saveOne")
    @ResponseBody
    public JSONResult insertOne(@Valid @RequestBody DictionaryAddAndUpdateDTO dictionaryDTO  , BindingResult result){
        if (result.hasErrors()) return  CommonUtil.validateParam(result);

//        return  abnormalFeignClient.saveDictionary(dictionaryDTO);
        return new JSONResult();
}

    @RequestMapping("/deleteAbnoramlUser")
    @ResponseBody
    public JSONResult deleteAbnoramlUser(@RequestBody IdEntity idEntity){
//        return abnormalFeignClient.deleteDictionary(idEntity);
        return new JSONResult();
    }

    @PostMapping("/queryAbnoramlUsers")
    @ResponseBody
    public JSONResult<PageBean<DictionaryRespDTO>> queryDictionary(@RequestBody DictionaryQueryDTO queryDTO){
//        JSONResult<PageBean<DictionaryRespDTO>> listJSONResult = dictionaryFeignClient.queryDictionary(queryDTO);
        return new JSONResult();
    }
}
