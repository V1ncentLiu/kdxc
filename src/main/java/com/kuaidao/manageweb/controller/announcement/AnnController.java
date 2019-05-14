package com.kuaidao.manageweb.controller.announcement;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnouncementFeignClient;
import com.kuaidao.manageweb.feign.msgpush.MsgPushFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.service.IAnnounceService;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description: 系统公告
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
    IAnnounceService announceService;


    @Value("${AnnMessageTempId}")
    private String tempId;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping("/publishAnn")
    @ResponseBody
    public JSONResult saveAnn(@Valid @RequestBody AnnouncementAddAndUpdateDTO dto,
            BindingResult result) {
        if (result.hasErrors())
            return validateParam(result);

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (user == null) {
            dto.setCreateUser(123456L);
        } else {
            dto.setCreateUser(user.getId());
        }

        long annId = IdUtil.getUUID();
        dto.setId(annId); // 公告ID
        JSONResult jsonResult = announcementFeignClient.publishAnnouncement(dto);
        if (jsonResult.getCode().equals(JSONResult.SUCCESS)) {
            announceService.sendMessage(dto);
        }
        return jsonResult;
    }

    @RequestMapping("/queryOneAnn")
    @ResponseBody
    public JSONResult queryOneAnn(@RequestBody IdEntity idEntity) {
        return announcementFeignClient.findByPrimaryKeyAnnouncement(idEntity);
    }

    @RequestMapping("/queryAnnList")
    @ResponseBody
    public JSONResult queryAnnList(@RequestBody AnnouncementQueryDTO dto) {

        Date date1 = dto.getDate1();
        Date date2 = dto.getDate2();

        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                return new JSONResult().fail("-1", "时间选项，开始时间大于结束时间!");
            }
        }

        JSONResult<PageBean<AnnouncementRespDTO>> pageBeanJSONResult =
                announcementFeignClient.queryAnnouncement(dto);
        return pageBeanJSONResult;
    }


    @RequestMapping("/annlistPage")
    public String listPage() {
        logger.info(
                "--------------------------------------跳转到列表页面-----------------------------------------------");
        return "ann/annListPage";
    }

    @LogRecord(description = "公告发布", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.ANNOUNCE_MANAGEMENT)
    // @RequiresPermissions("announce:publishAnn")
    @RequestMapping("/annPublishPage")
    public String itemListPage(HttpServletRequest request) {
        logger.info(
                "--------------------------------------跳转到公告页面-----------------------------------------------");
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.size() > 0) {
            String roleCode = roleList.get(0).getRoleCode();
            request.setAttribute("roleCode", roleCode);
        }
        request.setAttribute("orgId", user.getOrgId().toString());
        return "ann/annPublishPage";
    }

    /**
     * 获取当前登录账号
     * 
     * @param orgDTO
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 错误参数检验
     * 
     * @param result
     * @return
     */
    private JSONResult validateParam(BindingResult result) {
        List<ObjectError> list = result.getAllErrors();
        for (ObjectError error : list) {
            logger.error("参数校验失败：{},错误信息：{}", error.getArguments(), error.getDefaultMessage());
        }
        return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
    }
}
