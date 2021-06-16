package com.kuaidao.manageweb.controller.announcement;

import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.emun.sys.AnnBuinessTypeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnouncementFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.msgpush.MsgPushFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.service.IAnnounceService;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;


    @Value("${AnnMessageTempId}")
    private String tempId;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping("/publishAnn")
    @ResponseBody
    public JSONResult saveAnn(@Valid @RequestBody AnnouncementAddAndUpdateDTO dto,
                              BindingResult result) {
        if (result.hasErrors()) {
            return validateParam(result);
        }

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (user == null) {
            dto.setCreateUser(123456L);
        } else {
            dto.setCreateUser(user.getId());
        }
        logger.info("==============公告发布  开始=============");
        logger.info("发布人：{}" + user);
        long annId = IdUtil.getUUID();
        dto.setId(annId); // 公告ID
        JSONResult jsonResult = announcementFeignClient.publishAnnouncement(dto);
        if (jsonResult.getCode().equals(JSONResult.SUCCESS)) {
            announceService.sendMessage(dto);
        }
        logger.info("==============公告发布  结束=============");
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
        UserInfoDTO user = getUser();
        dto.setCreateUser(user.getId());
        if (user.getRoleList() != null && user.getRoleList().size() != 0) {
            dto.setRoleCode(user.getRoleList().get(0).getRoleCode());
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
     * 全部或招商宝充值协议内容推送
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping("/newpublishAnn")
    @ResponseBody
    public JSONResult newpublishAnn(@Valid @RequestBody AnnouncementAddAndUpdateDTO dto,
                                    BindingResult result) {
        if (result.hasErrors()) {
            return validateParam(result);
        }

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (user == null) {
            dto.setCreateUser(123456L);
        } else {
            dto.setCreateUser(user.getId());
        }
        dto.setCreateTime(new Date());
        logger.info("==============公告发布  开始=============");
        logger.info("发布人：{}" + user);
        long annId = IdUtil.getUUID();
        dto.setId(annId); // 公告ID
        List<Integer> types = Arrays.asList(dto.getTypes().split(",")).stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
        String flagType="";
        for (Integer type : types) {
            switch (type){
                case 1:
                    flagType=flagType+1;
                    break;
                case 2:
                    flagType=flagType+2;
                    break;
                case 3:
                    flagType=flagType+4;
                    break;
                default:
                    break;
            }
        }
        dto.setType(Integer.parseInt(flagType));
        if(AnnBuinessTypeEnum.招商宝充值协议.getType().equals(dto.getBusinessType())){
            List<UserInfoDTO> merchantUser = getMerchantUser();
            dto.setUserIds(merchantUser);
            dto.setOrgId(-1L);
        }
        JSONResult jsonResult = announcementFeignClient.publishAnnouncement(dto);
        if (jsonResult.getCode().equals(JSONResult.SUCCESS)) {
            announceService.sendNewMessage(dto);
        }
        logger.info("==============公告发布  结束=============");
        return jsonResult;
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

    /**
     * 查询商家账号
     *
     * @return
     */
    private List<UserInfoDTO> getMerchantUser() {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        JSONResult<List<UserInfoDTO>> merchantUserList =
                merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }

}
