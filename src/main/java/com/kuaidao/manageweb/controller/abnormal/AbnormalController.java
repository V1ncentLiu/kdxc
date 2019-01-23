package com.kuaidao.manageweb.controller.abnormal;

import com.kuaidao.aggregation.dto.abnormal.AbnomalUserAddAndUpdateDTO;
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserQueryDTO;
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserRespDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.abnormal.AbnormalUserFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnouncementFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.msgpush.MsgPushFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
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
import java.util.Map;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      标记异常客户
 */

@Controller
@RequestMapping("/abnoramluser")
public class AbnormalController {

    private static Logger logger = LoggerFactory.getLogger(AbnormalController.class);


    @Autowired
    AbnormalUserFeignClient abnormalUserFeignClient;

    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;
    
    
    

    @RequestMapping("/AbnoramlType")
    @ResponseBody
    public JSONResult<List<DictionaryItemRespDTO>> AbnoramlType(){
        JSONResult result = dictionaryItemFeignClient.queryDicItemsByGroupCode("AbnormalType");
        return result;
    }

    @RequestMapping("/saveOne")
    @ResponseBody
    public JSONResult insertOne(@Valid @RequestBody AbnomalUserAddAndUpdateDTO dto  , BindingResult result){
        if (result.hasErrors()) return  CommonUtil.validateParam(result);
        dto.setCreateTime(new Date());
        dto.setCreateUser(1084621842175623168L);
        dto.setStatus(0);
        return  abnormalUserFeignClient.saveAbnomalUser(dto);
    }

    @RequestMapping("/deleteAbnoramlUser")
    @ResponseBody
    public JSONResult deleteAbnoramlUser(@RequestBody Map map){
        return abnormalUserFeignClient.deleteAbnomalUsers((List<Long>)map.get("ids"));
    }

    @PostMapping("/queryAbnoramlUsers")
    @ResponseBody
    public JSONResult<PageBean<AbnomalUserRespDTO>> queryAbnoramlUsers(@RequestBody AbnomalUserQueryDTO dto){
        logger.info("====================列表查询==================");
        Date date1 = dto.getTime1();
        Date date2 = dto.getTime2();
        if(date1!=null && date2!=null ){
            if(date1.getTime()>date2.getTime()){
                return new JSONResult().fail("-1","时间选项，开始时间大于结束时间!");
            }
        }

//      这里需要添加权限验证。
        if("".equals("")){

        }

        JSONResult<PageBean<AbnomalUserRespDTO>> resList = abnormalUserFeignClient.queryAbnomalUserList(dto);
        List<AbnomalUserRespDTO> resdata = resList.getData().getData();

        UserInfoPageParam param = new UserInfoPageParam();
        param.setPageNum(1);
        param.setPageSize(99999);
        JSONResult<PageBean<UserInfoDTO>> userlist = userInfoFeignClient.list(param);
        List<UserInfoDTO> userData = userlist.getData().getData();

        JSONResult<List<DictionaryItemRespDTO>> abnoramlType = AbnoramlType();
        List<DictionaryItemRespDTO> abnoramlTypeData = abnoramlType.getData();

        List<AbnomalUserRespDTO> list2 = new ArrayList<>();
        for(int i = 0 ; i < resdata.size() ; i++){
            AbnomalUserRespDTO tempDto = resdata.get(i);
            for(UserInfoDTO userInfo:userData){
                if(userInfo.getId().equals(tempDto.getCreateUser())){
                    tempDto.setCreateUserName(userInfo.getUsername());
                }
            }
            for(DictionaryItemRespDTO abnType:abnoramlTypeData){
                if(abnType.getValue().equals(tempDto.getType().toString())){
                    tempDto.setTypeName(abnType.getName());
                }
            }
            list2.add(tempDto);
        }
        resList.getData().setData(list2);
        return resList;
    }

    @RequestMapping("/abnormalUserPage")
    public String pageIndex(){
        logger.info("====================跳转列表页面==================");
        return "abnormal/abnormalUserList";
    }

}
