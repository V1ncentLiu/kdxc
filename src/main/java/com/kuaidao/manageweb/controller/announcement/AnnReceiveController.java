package com.kuaidao.manageweb.controller.announcement;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnouncementFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      系统公告
 */

@Controller
@RequestMapping("/annReceive")
public class AnnReceiveController {

    private static Logger logger = LoggerFactory.getLogger(AnnReceiveController.class);

    @Autowired
    AnnReceiveFeignClient annReceiveFeignClient;



    /**
     * 带参数查询
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public JSONResult<PageBean<AnnReceiveRespDTO>> queryOne(@RequestBody AnnReceiveQueryDTO dto){

        Date date1 = dto.getDate1();
        Date date2 = dto.getDate2();

        if(date1!=null && date2!=null ){
            if(date1.getTime()>date2.getTime()){
                return new JSONResult().fail("-1","时间选项，结束时间不能早于开始时间!");
            }
        }
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if(user == null){
            return new JSONResult().fail("-1","用户未登录");
        }
        dto.setReceiveUser(user.getId());
        return annReceiveFeignClient.queryReceive(dto);
    }
    /**
     * 通过主键查询
     * @return
     */
    @RequestMapping("/one")
    @ResponseBody
    public JSONResult<AnnReceiveRespDTO> queryOne(@RequestBody IdEntity idEntity){
        JSONResult<AnnReceiveRespDTO> bussReceiveRespDTOJSONResult = annReceiveFeignClient.queryReceiveOne(idEntity);
        return bussReceiveRespDTOJSONResult;
    }
    /**
     * 批量更新状态
     * @return
     */
    @LogRecord(description = "公告状态更新",operationType = LogRecord.OperationType.UPDATE,menuName = MenuEnum.MESSAGE_CENTER)
    @RequestMapping("/batchUpdate")
    @ResponseBody
    public JSONResult<Void> batchUpdate(@RequestBody Map<String, String> map){
        JSONResult ids = annReceiveFeignClient.updateReceives(map.get("ids"));
        return ids;
    }

    @RequestMapping("/unreadCount")
    @ResponseBody
    public JSONResult<Void> unreadCount(){
        Map map = new HashMap();
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        map.put("receiveUser",user.getId());
        JSONResult ids = annReceiveFeignClient.annUnreadCount(map);
        return ids;
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
