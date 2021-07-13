package com.kuaidao.manageweb.controller.clue;

import com.alibaba.fastjson.JSONObject;
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.constant.ClueCirculationConstant;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.call.QueryPhoneLocaleDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationReqDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationRespDTO;
import com.kuaidao.aggregation.dto.clue.*;
import com.kuaidao.aggregation.dto.clueappiont.ClueAppiontmentDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingReqDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingRespDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.businessconfig.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.constant.BusinessLineConstant;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.SortUtils;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.circulation.CirculationFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.clue.RepeatClueRecordFeignClient;
import com.kuaidao.manageweb.feign.clue.TelCreatePhoneAuditFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.deduplicationDetail.DeduplicationDetailFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.manageweb.feign.tracking.TrackingFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tele/clueMyCustomerInfo")
public class MyCustomerClueController {
    private static Logger logger = LoggerFactory.getLogger(MyCustomerClueController.class);
    private static final Integer IS_NOT_SIGN_NO = -1;
    private static final Integer DAY_15 = 15;
    private static final Integer DAY_7 = 7;
    private static final Integer DIFF_MIN = 10;
    /**今日跟访次数0次**/
    private static  final  String TODAYFOLLOWNUM_ZERO = "0";
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private MyCustomerFeignClient myCustomerFeignClient;

    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private CallRecordFeign callRecordFeign;

    @Autowired
    private TrackingFeignClient trackingFeignClient;

    @Autowired
    private CirculationFeignClient circulationFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;

    @Autowired
    private ClueBasicFeignClient clueBasicFeignClient;

    @Autowired
    private RepeatClueRecordFeignClient repeatClueRecordFeignClient;

    @Autowired
    private TelemarketingLayoutFeignClient telemarketingLayoutFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private DeduplicationDetailFeignClient deduplicationDetailFeignClient;
    @Autowired
    private TelCreatePhoneAuditFeignClient telCreatePhoneAuditFeignClient;

    @Value("${oss.url.directUpload}")
    private String ossUrl;

    public static final String VALIDATE_PHONE_WECHAT_MSG = "请上传资料（沟通记录录音或者聊天截图）";

    /**
     * 初始化我的客户
     *
     * @param request
     * @param model
     * @return
     */
    @RequiresPermissions("myCustomerInfo:view")
    @RequestMapping("/initmyCustomer")
    public String initmyCustomer(HttpServletRequest request, Model model, @RequestParam(required = false) Integer type, @RequestParam(required = false) Integer hJtype) {
        UserInfoDTO user = getUser();
        //TODO 业务线8 删除搜索词列
        Integer businessLine = user.getBusinessLine();
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("myCustomerInfo");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu = customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        List<CustomFieldQueryDTO> data = queryFieldByRoleAndMenu.getData();
//        if(null != businessLine && CollectionUtils.isNotEmpty(data) && businessLine.equals(8)){
//            data.removeIf(s -> s.getFieldCode().equals("searchWord"));
//        }
        request.setAttribute("fieldList", data);
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setMenuCode("myCustomerInfo");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu = customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        List<UserFieldDTO> data1 = queryFieldByUserAndMenu.getData();
        if(null != businessLine && CollectionUtils.isNotEmpty(data1) && businessLine.equals(8)){
            data1.removeIf(s -> s.getFieldCode().equals("searchWord"));
        }
        request.setAttribute("userFieldList", data1);
        request.setAttribute("ossUrl", ossUrl);
        // 添加重单字段限制的业务线
        String repetitionBusinessLine = getSysSetting(SysConstant.REPETITION_BUSINESSLINE);
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(-1);
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (JSONResult.SUCCESS.equals(proJson.getCode())) {
            request.setAttribute("proSelect", proJson.getData());
        }
        boolean isShowRepetition = false;
        if (("," + repetitionBusinessLine + ",").contains("," + user.getBusinessLine() + ",")) {
            isShowRepetition = true;
        }
        TelemarketingLayoutDTO telemarketingLayoutDTO = new TelemarketingLayoutDTO();
        //获取电销布局
        telemarketingLayoutDTO = getTelemarketingLayoutDTO(user,user.getRoleList().get(0).getRoleCode(),telemarketingLayoutDTO,OrgTypeConstant.DXZ);
        request.setAttribute("telemarketingLayout", telemarketingLayoutDTO);
        request.setAttribute("isShowRepetition", isShowRepetition);
        request.setAttribute("type", type);
        request.setAttribute("hJtype", hJtype);
        List<String> todayFollowNumList =  buildTodayFollowNum(user.getRoleList().get(0).getRoleCode());
        request.setAttribute("todayFollowNumList", todayFollowNumList);
        request.setAttribute("roleCode", user.getRoleList().get(0).getRoleCode());
        request.setAttribute("businessLine", user.getBusinessLine());
        return "clue/myCustom";
    }

    /**
     * 我的客户分页查询
     *
     * @param request
     * @param dto
     * @return
     */

    @RequestMapping("/findTeleClueInfo")
    @ResponseBody
    public JSONResult<PageBean<CustomerClueDTO>> findTeleClueInfo(HttpServletRequest request,
                                                                  @RequestBody CustomerClueQueryDTO dto) {
        long time1 = System.currentTimeMillis();
        java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (null != dto && null != dto.getDayTel()) {
            // 当日拨打电话
            dto.setTelTime(formatter.format(new Date()));
        }
        if (null != dto && null != dto.getTrackingDay() && dto.getTrackingDay().intValue() == 1) {
            // 当日跟进
            dto.setTrackingTime(formatter.format(new Date()));
        } else if (null != dto && null != dto.getTrackingDay()
                && dto.getTrackingDay().intValue() == 0) {
            dto.setNotTrackingTime(formatter.format(new Date()));

        }
        // 数据权限处理
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            String roleCode = user.getRoleList().get(0).getRoleCode();
            if (null != roleCode) {
                if (roleCode.equals(RoleCodeEnum.GLY.name())) {
                    // 管理员查看所有

                } else if (roleCode.equals(RoleCodeEnum.DXZJ.name())) {
                    dto.setTeleGorup(user.getOrgId());

                } else if (roleCode.equals(RoleCodeEnum.DXCYGW.name())) {

                    dto.setTeleSale(user.getId());

                }
            }
        }
        dto.setOrgId(user.getOrgId());
        JSONResult<PageBean<CustomerClueDTO>> jsonResult =
                myCustomerFeignClient.findTeleClueInfo(dto);
        long time2 = System.currentTimeMillis();
        logger.info("我的客户列表查询时间：" + (time2 - time1));
        return jsonResult;
    }

    /**
     * 我的客户创建资源
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/createClue")
    @RequiresPermissions("myCustomerInfo:add")
    public String createClue(HttpServletRequest request, Model model) {

        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            model.addAttribute("proSelect", proJson.getData());
        }
        model.addAttribute("ossUrl", ossUrl);
        // 系统参数优化资源类别
        String optList = getSysSetting(SysConstant.OPT_CATEGORY);
        request.setAttribute("optList", optList);
        // 系统参数非优化资源类别
        String notOptList = getSysSetting(SysConstant.NOPT_CATEGORY);
        request.setAttribute("notOptList", notOptList);
        return "clue/addCustomerResources";
    }

    /**
     * 我的客户释放资源
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/releaseClue")
    @ResponseBody
    public JSONResult<String> releaseClue(HttpServletRequest request,
                                          @RequestBody ReleaseClueDTO dto) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setReleaseUserId(user.getId());
        }
        return myCustomerFeignClient.releaseClue(dto);
    }

    /**
     * 维护客户资源数据
     *
     * @param request
     * @param clueId
     * @return
     */
    @RequestMapping("/customerEditInfo")
    public String customerEditInfo(HttpServletRequest request, @RequestParam String clueId ) {
        logger.info("customerEditInfo_clueId {{}}", clueId);
        UserInfoDTO user = getUser();
        List<Long> accountList = new ArrayList<Long>();
        List<DictionaryItemRespDTO> mediumList = new ArrayList<>();
        TelemarketingLayoutDTO telemarketingLayoutDTO = new TelemarketingLayoutDTO();
        String role = null;
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            String roleCode = user.getRoleList().get(0).getRoleCode();
            role = roleCode;
            if (null != roleCode) {
                if (roleCode.equals(RoleCodeEnum.GLY.name())) {
                    // 管理员查看所有

                } else if (roleCode.equals(RoleCodeEnum.DXZJ.name())) {
                    UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
                    userOrgRoleReq.setOrgId(user.getOrgId());
                    userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
                    JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                            userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
                    if (listByOrgAndRole.getCode().equals(JSONResult.SUCCESS)
                            && null != listByOrgAndRole.getData()
                            && listByOrgAndRole.getData().size() > 0) {
                        accountList = listByOrgAndRole.getData().stream().map(c -> c.getId())
                                .collect(Collectors.toList());
                    }
                    accountList.add(user.getId()); // 电销总监，能够看见自身已经手下全部电销顾问的记录
                } else if (roleCode.equals(RoleCodeEnum.DXCYGW.name())) {
                    accountList.add(user.getId());
                }
                //获取电销布局
                telemarketingLayoutDTO = getTelemarketingLayoutDTO(user,roleCode,telemarketingLayoutDTO,OrgTypeConstant.DXZ);
            }
        }
        // 获取资源跟进记录数据
        TrackingReqDTO dto = new TrackingReqDTO();
        // 获取已上传的文件数据
        ClueQueryDTO fileDto = new ClueQueryDTO();
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(clueId);
        if (accountList.size() > 0) {
            call.setAccountIdList(accountList);
            fileDto.setIdList(accountList);
        }
        JSONResult<List<CallRecordRespDTO>> callRecord =
                callRecordFeign.listTmCallReacordByParamsNoPage(call);
        // 资源通话记录
        if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode())
                && callRecord.getData() != null) {

            request.setAttribute("callRecord", callRecord.getData());
            // CallRecordRespDTO callRecordRespDTO = null;
            // Optional<CallRecordRespDTO> optional =callRecord.getData().stream()
            // .filter(a -> StringUtils.isNotBlank(a.getStartTime()))
            // .max(Comparator.comparing(CallRecordRespDTO::getStartTime));
            // if(optional !=null && optional.isPresent()){
            // callRecordRespDTO = optional.get();
            // }
            // if (callRecordRespDTO != null) {
            // String date =
            // convertTimeToString(Long.valueOf(callRecordRespDTO.getStartTime()) * 1000L);
            // request.setAttribute("teleEndTime", date);
            // } else {
            // request.setAttribute("teleEndTime", new Date());
            // }
        } else {
            // request.setAttribute("teleEndTime", new Date());
        }
        ClueQueryDTO queryDTO = new ClueQueryDTO();

        queryDTO.setClueId(new Long(clueId));
        queryDTO.setOrgId(user.getOrgId());
        request.setAttribute("clueId", clueId);

        request.setAttribute("ossUrl", ossUrl);

        JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);

        // 维护的资源数据
        if (clueInfo != null && JSONResult.SUCCESS.equals(clueInfo.getCode())
                && clueInfo.getData() != null) {

            if (null != clueInfo.getData().getClueCustomer()) {
                ClueCustomerDTO clueCustomerDTO = clueInfo.getData().getClueCustomer();
                if (StringUtils.isNotBlank(role) && role.equals(RoleCodeEnum.DXZJ.name())
                        || StringUtils.isNotBlank(role)
                        && role.equals(RoleCodeEnum.DXCYGW.name())) {
                    setPhoneLocales(clueCustomerDTO);
                }
                clueCustomerDTO.setPhoneCreateTime(null);
                clueCustomerDTO.setPhone2CreateTime(null);
                clueCustomerDTO.setPhone3CreateTime(null);
                clueCustomerDTO.setPhone4CreateTime(null);
                clueCustomerDTO.setPhone5CreateTime(null);
                request.setAttribute("customer", clueCustomerDTO);
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueBasic()) {
                ClueBasicDTO clueBasicDTO = clueInfo.getData().getClueBasic();
                if (telemarketingLayoutDTO != null && telemarketingLayoutDTO.getId() != null) {
                    if (StringUtils.isNotBlank(telemarketingLayoutDTO.getCategory())
                            && clueBasicDTO.getCategory() != null
                            && ("," + telemarketingLayoutDTO.getCategory() + ",")
                            .contains("," + clueBasicDTO.getCategory().toString() + ",")) {
                        mediumList = getDictionaryByCode(Constants.MEDIUM);
                        request.setAttribute("telemarketingLayout", telemarketingLayoutDTO);
                    }
                }
                //TODO 业务线8 删除搜索词列
                Integer businessLine = user.getBusinessLine();
//                if(null != businessLine && businessLine.equals(8)){
//                    clueBasicDTO.setSearchWord(null);
//                }
                request.setAttribute("base", clueBasicDTO);
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueRelate()) {
                request.setAttribute("teleSaleId",
                        clueInfo.getData().getClueRelate().getTeleSaleId());
            } else {
                request.setAttribute("teleSaleId", 0);
            }
            if (null != clueInfo.getData().getClueIntention()) {
                request.setAttribute("intention", clueInfo.getData().getClueIntention());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueProject()) {
                request.setAttribute("clueProject", clueInfo.getData().getClueProject());
            } else {
                request.setAttribute("clueProject", new ArrayList());
            }
            if (null != clueInfo.getData().getClueReceive()) {
                request.setAttribute("clueReceive", clueInfo.getData().getClueReceive());
            } else {
                request.setAttribute("clueReceive", new ArrayList());
            }
        }

        dto.setClueId(new Long(clueId));
        JSONResult<List<TrackingRespDTO>> trackingList = trackingFeignClient.queryList(dto);
        if (trackingList != null && JSONResult.SUCCESS.equals(trackingList.getCode())
                && trackingList.getData() != null) {
            request.setAttribute("trackingList", trackingList.getData());
        } else {
            request.setAttribute("trackingList", new ArrayList());
        }

        // 获取资源流转数据
        CirculationReqDTO circDto = new CirculationReqDTO();
        circDto.setClueId(new Long(clueId));
        JSONResult<List<CirculationRespDTO>> circulationList =
                circulationFeignClient.queryList(circDto);
        if (circulationList != null && JSONResult.SUCCESS.equals(circulationList.getCode())
                && circulationList.getData() != null) {
            request.setAttribute("circulationList", circulationList.getData());
        } else {
            request.setAttribute("circulationList", new ArrayList());
        }
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            List<ProjectInfoDTO> result = SortUtils.sortList(proJson.getData(), "projectName");
            request.setAttribute("proSelect", result);
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }

        fileDto.setClueId(new Long(clueId));
        JSONResult<List<ClueFileDTO>> clueFileList = myCustomerFeignClient.findClueFile(fileDto);
        if (clueFileList != null && JSONResult.SUCCESS.equals(clueFileList.getCode())
                && clueFileList.getData() != null) {
            request.setAttribute("clueFileList", clueFileList.getData());
        }
        request.setAttribute("loginUserId", user.getId());
        RepeatClueRecordQueryDTO recordQueryDTO = new RepeatClueRecordQueryDTO();
        recordQueryDTO.setClueId(Long.valueOf(clueId));
        JSONResult<List<RepeatClueRecordDTO>> repeatJson = repeatClueRecordFeignClient.queryList(recordQueryDTO);
        if (repeatJson != null && JSONResult.SUCCESS.equals(repeatJson.getCode()) && repeatJson.getData() != null && repeatJson.getData().size() > 0) {
            //TODO 业务线8 删除搜索词
            Integer businessLine = getUser().getBusinessLine();
            List<RepeatClueRecordDTO> data = repeatJson.getData();
            if(null != businessLine && businessLine.equals(8)){
                if(CollectionUtils.isNotEmpty(data)){
                    for(RepeatClueRecordDTO repeatClueRecordDTO : data){
                        repeatClueRecordDTO.getClueDTO().getClueBasic().setSearchWord(null);
                    }
                }
            }
            request.setAttribute("repeatClueList", data);
            request.setAttribute("repeatClueStatus", 1);
        } else {
            request.setAttribute("repeatClueStatus", 0);
        }
        request.setAttribute("mediumList", mediumList);
        request.setAttribute("zjFalg", request.getParameter("zjFalg"));

        //电销创建手机号审核不通过集合
        TelCreatePhoneAuditReqDTO reqDTO = new TelCreatePhoneAuditReqDTO();
        reqDTO.setClueId(Long.valueOf(clueId));
        JSONResult<List<TelCreatePhoneAuditDTO>> jsonResult = telCreatePhoneAuditFeignClient.findListByClueId(reqDTO);
        if (jsonResult != null && JSONResult.SUCCESS.equals(jsonResult.getCode()) && CollectionUtils.isNotEmpty(jsonResult.getData())) {
            request.setAttribute("telCreatePhoneAudits", jsonResult.getData());
        } else {
            request.setAttribute("telCreatePhoneAudits", new ArrayList<>());
        }
        request.setAttribute("telemarketingLayoutDTO", telemarketingLayoutDTO);

        // 添加“下一条”透传
        request.setAttribute("nextClueIds", request.getParameter("nextClueIds"));
        // hJtype 参数传递
        request.setAttribute("hJtype", request.getParameter("hJtype"));

        return "clue/addCustomerMaintenance";
    }

    /**
     * 客户详情
     *
     * @param request
     * @param clueId
     * @param  telCreatePhoneAuditFlag  电销创建手机号审核按钮是否显示标志
     * @return
     */
    @RequestMapping("/customerInfoReadOnly")
    public String customerInfoReadOnly(HttpServletRequest request, @RequestParam String clueId, @RequestParam(required = false) String commonPool,
                                       @RequestParam(required = false) String repeatFlag, @RequestParam(required = false) String telCreatePhoneAuditFlag) {
        UserInfoDTO user = getUser();
        List<Long> accountList = new ArrayList<Long>();
        TelemarketingLayoutDTO telemarketingLayoutDTO = new TelemarketingLayoutDTO();
        request.setAttribute("telemarketingLayout", telemarketingLayoutDTO);
        String role = null;
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            String roleCode = user.getRoleList().get(0).getRoleCode();
            role = roleCode;
            if (null != roleCode) {
                if (roleCode.equals(RoleCodeEnum.GLY.name())) {
                    // 管理员查看所有

                } else if (roleCode.equals(RoleCodeEnum.DXZJ.name())) {
                    UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
                    userOrgRoleReq.setOrgId(user.getOrgId());
                    userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
                    JSONResult<List<UserInfoDTO>> listByOrgAndRole = userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
                    if (listByOrgAndRole.getCode().equals(JSONResult.SUCCESS) && null != listByOrgAndRole.getData()
                            && listByOrgAndRole.getData().size() > 0) {
                        accountList = listByOrgAndRole.getData().stream().map(c -> c.getId()).collect(Collectors.toList());
                    }
                    accountList.add(user.getId());
                } else if (roleCode.equals(RoleCodeEnum.DXCYGW.name())) {
                    accountList.add(user.getId());
                }
                // 获取电销布局信息
                telemarketingLayoutDTO = getTelemarketingLayoutDTO(user,roleCode,telemarketingLayoutDTO,OrgTypeConstant.DXZ);
            }
        }
        // 获取已上传的文件数据
        ClueQueryDTO fileDto = new ClueQueryDTO();
        CallRecordReqDTO call = new CallRecordReqDTO();
        if (accountList.size() > 0) {
            call.setAccountIdList(accountList);
            fileDto.setIdList(accountList);
        }
        call.setClueId(clueId);
        JSONResult<List<CallRecordRespDTO>> callRecord = callRecordFeign.listTmCallReacordByParamsNoPage(call);
        // 资源通话记录
        if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode()) && callRecord.getData() != null) {

            request.setAttribute("callRecord", callRecord.getData());
        }
        ClueQueryDTO queryDTO = new ClueQueryDTO();

        queryDTO.setClueId(new Long(clueId));
        queryDTO.setOrgId(user.getOrgId());

        request.setAttribute("clueId", clueId);

        request.setAttribute("ossUrl", ossUrl);

        JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);

        // 维护的资源数据
        if (clueInfo != null && JSONResult.SUCCESS.equals(clueInfo.getCode()) && clueInfo.getData() != null) {

            if (null != clueInfo.getData().getClueCustomer()) {
                ClueCustomerDTO clueCustomerDTO = clueInfo.getData().getClueCustomer();
                if (StringUtils.isNotBlank(role) && role.equals(RoleCodeEnum.DXZJ.name())
                        || StringUtils.isNotBlank(role) && role.equals(RoleCodeEnum.DXCYGW.name())) {
                    setPhoneLocales(clueCustomerDTO);
                }
                if (StringUtils.isNotBlank(role) && role.equals(RoleCodeEnum.DXZJ.name())) {
                    clueCustomerDTO.setPhoneCreateTime(null);
                    clueCustomerDTO.setPhone2CreateTime(null);
                    clueCustomerDTO.setPhone3CreateTime(null);
                    clueCustomerDTO.setPhone4CreateTime(null);
                    clueCustomerDTO.setPhone5CreateTime(null);
                    request.setAttribute("customer", clueCustomerDTO);
                } else {
                    request.setAttribute("customer", clueCustomerDTO);
                }

            } else {
                request.setAttribute("customer", new ArrayList());
            }
            request.setAttribute("mediumList", new ArrayList<>());

            if (null != clueInfo.getData().getClueBasic()) {
                ClueBasicDTO clueBasic = clueInfo.getData().getClueBasic();
                if (telemarketingLayoutDTO != null && telemarketingLayoutDTO.getId() != null) {
                    if (StringUtils.isNotBlank(telemarketingLayoutDTO.getCategory()) && clueBasic.getCategory() != null
                            && ("," + telemarketingLayoutDTO.getCategory() + ",").contains("," + clueBasic.getCategory().toString() + ",")) {
                        List<DictionaryItemRespDTO> mediumList = getDictionaryByCode(Constants.MEDIUM);
                        request.setAttribute("mediumList", mediumList);
                        request.setAttribute("telemarketingLayout", telemarketingLayoutDTO);
                    }
                }
                //TODO 业务线8 删除搜索词
                Integer businessLine = user.getBusinessLine();
                if(null != businessLine && businessLine.equals(8)){
                    clueBasic.setSearchWord(null);
                }
                request.setAttribute("base", clueBasic);
            } else {
                request.setAttribute("base", new ArrayList());
            }
            if (null != clueInfo.getData().getClueIntention()) {
                request.setAttribute("intention", clueInfo.getData().getClueIntention());
            } else {
                request.setAttribute("intention", new ArrayList());
            }
            if (null != clueInfo.getData().getClueRelate()) {
                request.setAttribute("relate", clueInfo.getData().getClueRelate());
            } else {
                request.setAttribute("relate", new ArrayList());
            }
            if (null != clueInfo.getData().getClueProject()) {
                request.setAttribute("clueProject", clueInfo.getData().getClueProject());
            } else {
                request.setAttribute("clueProject", new ArrayList());
            }
            if (null != clueInfo.getData().getClueReceive()) {
                request.setAttribute("clueReceive", clueInfo.getData().getClueReceive());
            } else {
                request.setAttribute("clueReceive", new ArrayList());
            }
        }
        // 获取资源跟进记录数据
        TrackingReqDTO dto = new TrackingReqDTO();
        dto.setClueId(new Long(clueId));
        JSONResult<List<TrackingRespDTO>> trackingList = trackingFeignClient.queryList(dto);
        if (trackingList != null && JSONResult.SUCCESS.equals(trackingList.getCode()) && trackingList.getData() != null) {
            request.setAttribute("trackingList", trackingList.getData());
        } else {
            request.setAttribute("trackingList", new ArrayList());
        }

        // 获取资源流转数据
        CirculationReqDTO circDto = new CirculationReqDTO();
        circDto.setClueId(new Long(clueId));
        JSONResult<List<CirculationRespDTO>> circulationList = circulationFeignClient.queryList(circDto);
        if (circulationList != null && JSONResult.SUCCESS.equals(circulationList.getCode()) && circulationList.getData() != null) {
            request.setAttribute("circulationList", circulationList.getData());
        } else {
            request.setAttribute("circulationList", new ArrayList());
        }
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }

        // 获取已上传的文件数据
        fileDto.setClueId(new Long(clueId));
        JSONResult<List<ClueFileDTO>> clueFileList = myCustomerFeignClient.findClueFile(fileDto);
        if (clueFileList != null && JSONResult.SUCCESS.equals(clueFileList.getCode()) && clueFileList.getData() != null) {
            request.setAttribute("clueFileList", clueFileList.getData());
        }
        request.setAttribute("commonPool", commonPool);
        request.setAttribute("repeatFlag", repeatFlag);
        request.setAttribute("loginUserId", user.getId());
        request.setAttribute("telCreatePhoneAuditFlag", telCreatePhoneAuditFlag);
        RepeatClueRecordQueryDTO recordQueryDTO = new RepeatClueRecordQueryDTO();
        recordQueryDTO.setClueId(Long.valueOf(clueId));
        JSONResult<List<RepeatClueRecordDTO>> repeatJson = repeatClueRecordFeignClient.queryList(recordQueryDTO);
        if (repeatJson != null && JSONResult.SUCCESS.equals(repeatJson.getCode()) && repeatJson.getData() != null && repeatJson.getData().size() > 0) {
            //TODO 业务线8 删除搜索词
            Integer businessLine = getUser().getBusinessLine();
            List<RepeatClueRecordDTO> data = repeatJson.getData();
            if(null != businessLine && businessLine.equals(8)){
                if(CollectionUtils.isNotEmpty(data)){
                    for(RepeatClueRecordDTO repeatClueRecordDTO : data){
                        repeatClueRecordDTO.getClueDTO().getClueBasic().setSearchWord(null);
                    }
                }
            }
            request.setAttribute("repeatClueList", data);
            request.setAttribute("repeatClueStatus", 1);
        } else {
            request.setAttribute("repeatClueStatus", 0);
        }

        //电销创建手机号审核不通过集合
        TelCreatePhoneAuditReqDTO reqDTO = new TelCreatePhoneAuditReqDTO();
        reqDTO.setClueId(Long.valueOf(clueId));
        JSONResult<List<TelCreatePhoneAuditDTO>> jsonResult = telCreatePhoneAuditFeignClient.findListByClueId(reqDTO);
        if (jsonResult != null && JSONResult.SUCCESS.equals(jsonResult.getCode()) && CollectionUtils.isNotEmpty(jsonResult.getData())) {
            request.setAttribute("telCreatePhoneAudits", jsonResult.getData());
        } else {
            request.setAttribute("telCreatePhoneAudits", new ArrayList<>());
        }
        request.setAttribute("telemarketingLayoutDTO", telemarketingLayoutDTO);
        if (StringUtils.isNotBlank(role) && role.equals(RoleCodeEnum.DXZJ.name())) {
            return "clue/editBasicCustomerMaintenance";
        } else {
            return "clue/CustomerMaintenanceReadOnly";
        }
    }

    /**
     * 设置手机号归属地
     *
     * @param clueCustomerDTO
     * @return
     */
    private void setPhoneLocales(ClueCustomerDTO clueCustomerDTO) {
        if (StringUtils.isNotBlank(clueCustomerDTO.getPhone())) {
            clueCustomerDTO.setPhoneLocale(getPhoneLocale(clueCustomerDTO.getPhone()));
        }
        if (StringUtils.isNotBlank(clueCustomerDTO.getPhone2())) {
            clueCustomerDTO.setPhone2Locale(getPhoneLocale(clueCustomerDTO.getPhone2()));
        }
        if (StringUtils.isNotBlank(clueCustomerDTO.getPhone3())) {
            clueCustomerDTO.setPhone3Locale(getPhoneLocale(clueCustomerDTO.getPhone3()));
        }
        if (StringUtils.isNotBlank(clueCustomerDTO.getPhone4())) {
            clueCustomerDTO.setPhone4Locale(getPhoneLocale(clueCustomerDTO.getPhone4()));
        }
        if (StringUtils.isNotBlank(clueCustomerDTO.getPhone5())) {
            clueCustomerDTO.setPhone5Locale(getPhoneLocale(clueCustomerDTO.getPhone5()));
        }
    }

    /**
     * 获取手机号归属地
     *
     * @param phone
     * @return
     */
    private String getPhoneLocale(String phone) {
        if (StringUtils.isBlank(phone)) {
            return null;
        } else {
            String phoneLocale = null;
            QueryPhoneLocaleDTO queryPhoneLocaleDTO = new QueryPhoneLocaleDTO();
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            queryPhoneLocaleDTO.setOrgId(curLoginUser.getOrgId());
            queryPhoneLocaleDTO.setPhone(phone);
            JSONResult<JSONObject> jsonResult =
                    callRecordFeign.queryPhoneLocale(queryPhoneLocaleDTO);
            JSONObject jsonObject = jsonResult.getData();
            if (jsonObject != null && jsonObject.get("area") != null) {
                phoneLocale = jsonObject.get("area").toString();
                return phoneLocale;
            }
        }
        return null;
    }

    /**
     * 查询资源文件上传记录
     *
     * @param request
     * @return
     */
    @RequestMapping("/findClueFile")
    @ResponseBody
    public JSONResult<List<ClueFileDTO>> findClueFile(HttpServletRequest request,
                                                      @RequestBody ClueQueryDTO dto) {
        UserInfoDTO user = getUser();
        List<Long> accountList = new ArrayList<Long>();
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            String roleCode = user.getRoleList().get(0).getRoleCode();
            if (null != roleCode) {
                if (roleCode.equals(RoleCodeEnum.GLY.name())) {
                    // 管理员查看所有

                } else if (roleCode.equals(RoleCodeEnum.DXZJ.name())) {
                    UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
                    userOrgRoleReq.setOrgId(user.getOrgId());
                    userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
                    JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                            userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
                    if (listByOrgAndRole.getCode().equals(JSONResult.SUCCESS)
                            && null != listByOrgAndRole.getData()
                            && listByOrgAndRole.getData().size() > 0) {
                        accountList = listByOrgAndRole.getData().stream().map(c -> c.getId())
                                .collect(Collectors.toList());
                    }
                    accountList.add(user.getId());
                } else if (roleCode.equals(RoleCodeEnum.DXCYGW.name())) {
                    accountList.add(user.getId());
                }
            }
        }
        if (accountList.size() > 0) {
            dto.setIdList(accountList);
        }
        // 获取已上传的文件数据
        return myCustomerFeignClient.findClueFile(dto);
    }

    /**
     * 更新最后拨打时间
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateCallTime")
    @ResponseBody
    public JSONResult<String> updateCallTime(HttpServletRequest request,
                                             @RequestBody ClueQueryDTO dto) {
        // 获取已上传的文件数据
        return myCustomerFeignClient.updateCallTime(dto);
    }

    /**
     * 获取资源拨打记录
     *
     * @param request
     * @return
     */
    @RequestMapping("/findCallData")
    @ResponseBody
    public JSONResult<List<CallRecordRespDTO>> findCallData(HttpServletRequest request,
                                                            @RequestBody ClueQueryDTO dto) {
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(dto.getClueId() + "");
        JSONResult<List<CallRecordRespDTO>> callRecord =
                callRecordFeign.listTmCallReacordByParamsNoPage(call);
        return callRecord;
    }

    /**
     * 删除已上传的资源文件
     *
     * @param request
     * @return
     */
    @RequestMapping("/deleteClueFile")
    @ResponseBody
    public JSONResult<String> deleteClueFile(HttpServletRequest request,
                                             @RequestBody ClueFileDTO dto) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setDelUser(user.getId());
        }
        dto.setDelTime(new Date());
        // 删除文件
        dto.setDelStatus(1);
        // 删除已上传文件
        return myCustomerFeignClient.deleteClueFile(dto);
    }

    /**
     * 上传资源文件
     *
     * @param
     * @return
     */
    @RequestMapping("/uploadClueFile")
    @ResponseBody
    public JSONResult<String> uploadClueFile(@RequestBody ClueFileDTO dto) {
        // 获取已上传的文件数据
        if (null != dto && null != dto.getFilePath()) {
            String filepath = dto.getFilePath();
            dto.setFileName(filepath.split(";")[1]);
            dto.setFilePath(filepath.split(";")[0]);
            dto.setFileType(filepath.substring(filepath.lastIndexOf(".") + 1));
        }
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setUploadUser(user.getId());
            dto.setUploadTime(new Date());
        }
        return myCustomerFeignClient.uploadClueFile(dto);
    }
    /**
     * 批量上传资源文件
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/batchUploadClueFile")
    public JSONResult<String> batchUploadClueFile(@RequestBody ClueFileDTO dto) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setUploadUser(user.getId());
            dto.setUploadTime(new Date());
        }
        return myCustomerFeignClient.batchUploadClueFile(dto);
    }
    /**
     * 查询跟进记录数据
     *
     * @param request
     * @return
     */
    @RequestMapping("/findClueTracking")
    @ResponseBody
    public JSONResult<List<TrackingRespDTO>> findClueTracking(HttpServletRequest request,
                                                              @RequestBody TrackingInsertOrUpdateDTO dto) {
        // 获取资源流转数据
        TrackingReqDTO circDto = new TrackingReqDTO();
        circDto.setClueId(dto.getClueId());
        circDto.setStage(dto.getStage());
        return trackingFeignClient.queryList(circDto);

    }

    /**
     * 保存跟进记录数据
     *
     * @param request
     * @return
     */
    @RequestMapping("/saveClueTracking")
    @ResponseBody
    public JSONResult<List<TrackingRespDTO>> saveClueTracking(HttpServletRequest request,
                                                              @RequestBody TrackingInsertOrUpdateDTO dto) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setCreateUser(user.getId());
            dto.setOrgId(user.getOrgId());
        }
        trackingFeignClient.saveTracking(dto);
        TrackingReqDTO queryDto = new TrackingReqDTO();
        dto.setClueId(dto.getClueId());
        return trackingFeignClient.queryList(queryDto);
    }

    /**
     * 删除资源跟进记录
     *
     * @param request
     * @return
     */
    @RequestMapping("/deleteClueTracking")
    @ResponseBody
    public JSONResult<List<TrackingRespDTO>> deleteClueTracking(HttpServletRequest request,
                                                                @RequestBody IdListLongReq dto) {
        trackingFeignClient.deleteTracking(dto);
        TrackingReqDTO queryDto = new TrackingReqDTO();
        dto.setClueId(dto.getClueId());
        return trackingFeignClient.queryList(queryDto);
    }

    /**
     * 修改资源跟进记录
     *
     * @param request
     * @return
     */
    @RequestMapping("/updateClueTracking")
    @ResponseBody
    public JSONResult<List<TrackingRespDTO>> updateClueTracking(HttpServletRequest request,
                                                                @RequestBody TrackingInsertOrUpdateDTO dto) {
        trackingFeignClient.updateTracking(dto);
        TrackingReqDTO queryDto = new TrackingReqDTO();
        dto.setClueId(dto.getClueId());
        return trackingFeignClient.queryList(queryDto);
    }

    /**
     * 预约来访页面跳转
     *
     * @param request
     * @return
     */
    @RequestMapping("/inviteCustomer")
    public String inviteCustomer(HttpServletRequest request, @RequestParam String clueId,
                                 @RequestParam String cusName, @RequestParam String cusPhone, Model model) {
        request.setAttribute("clueId", clueId);
        request.setAttribute("cusName", cusName);
        request.setAttribute("cusPhone", cusPhone);
        // 传递参数加入
        request.setAttribute("hJtype", request.getParameter("hJtype"));
        request.setAttribute("nextClueIds", request.getParameter("nextClueIds"));
        JSONResult<Integer> result =
                clueBasicFeignClient.getIsInviteLetterById(Long.valueOf(clueId));
        if (result.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("isInviteLetter", result.getData());
        }
        // 查询可签约的项目(过滤掉项目属性中是否不可签约（是）的项目，否的都是可以选择的) change by fanjd 20190826
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(IS_NOT_SIGN_NO);

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if(null != user && null != user.getBusinessLine()){
            param.setBusinessLines(String.valueOf(user.getBusinessLine()));
        }
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            model.addAttribute("proSelect", proJson.getData());
        }
        return "clue/inviteCustomerForm";
    }

    /**
     * 预约来访数据保存
     *
     * @param request
     * @return
     */
    @RequestMapping("/inviteCustomerSave")
    @ResponseBody
    @LogRecord(description = "添加预约来访", operationType = OperationType.INSERT,
            menuName = MenuEnum.TM_MY_CUSTOMER)
    public JSONResult<String> inviteCustomerSave(HttpServletRequest request,
                                                 @RequestBody ClueAppiontmentDTO dto) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setCreateUser(user.getId());
            dto.setOrgId(user.getOrgId());
            // 保存流转记录
            CirculationInsertOrUpdateDTO circul = new CirculationInsertOrUpdateDTO();
            circul.setAllotUserId(user.getId());
            List<RoleInfoDTO> roleListAll = user.getRoleList();
            if (null != roleListAll && roleListAll.size() > 0) {
                circul.setAllotRoleId(roleListAll.get(0).getId());
            }
            circul.setAllotOrg(user.getOrgId());
            circul.setClueId(dto.getClueId());
            if (null != dto.getBusDirectorId()) {
                IdEntityLong id = new IdEntityLong();
                id.setId(dto.getBusDirectorId());
                JSONResult<UserInfoDTO> dirUser = userInfoFeignClient.get(id);
                if (dirUser.getCode().equals(JSONResult.SUCCESS) && null != dirUser.getData()) {
                    circul.setUserId(dirUser.getData().getId());
                    dirUser.getData().getId();
                    List<RoleInfoDTO> roleList = dirUser.getData().getRoleList();
                    if (null != roleList && roleList.size() > 0) {
                        circul.setRoleId(roleList.get(0).getId());
                    }
                    circul.setOrg(dirUser.getData().getOrgId());
                }
                // 保存流转信息
                circulationFeignClient.saveCirculation(circul);
            }
        }
        // 获取商务大区总监
        if (null != dto.getArea()) {
            UserOrgRoleReq req = new UserOrgRoleReq();
            req.setOrgId(new Long(dto.getArea()));
            req.setRoleCode(RoleCodeEnum.SWDQZJ.name());
            JSONResult<List<UserInfoDTO>> userList = userInfoFeignClient.listByOrgAndRole(req);
            if (userList.getCode().equals(JSONResult.SUCCESS) && null != userList.getData()
                    && userList.getData().size() > 0) {
                UserInfoDTO areaDir = userList.getData().get(0);
                dto.setBusAreaDirectorId(areaDir.getId());
            }
        }

        return myCustomerFeignClient.saveAppiontment(dto);

    }

    /**
     * 重单申请查询重单数据
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/queryRepeatClueTwo")
    @ResponseBody
    public JSONResult<List<RepeatClueDTO>> queryRepeatClueTwo(HttpServletRequest request,
                                                              @RequestBody RepeatClueQueryDTO dto) {
        return myCustomerFeignClient.queryRepeatClue(dto);
    }

    /**
     * 重单申请查询重单数据
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/queryRepeatClue")
    @ResponseBody
    public JSONResult<List<RepeatClueDTO>> queryRepeatClue(HttpServletRequest request,
                                                           @RequestBody RepeatClueQueryDTO dto) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setTelemarketingId(user.getId());
        }
        return myCustomerFeignClient.queryRepeatClue(dto);
    }

    /**
     * 总裁办重单申请保存
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/officesaveRepeatClue")
    @ResponseBody
    public JSONResult<String> officesaveRepeatClue(HttpServletRequest request,
                                                   @RequestBody RepeatClueSaveDTO dto) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setOrgId(user.getOrgId());
            dto.setUserId(user.getId());
        }
        if (user.getBusinessLine() != null) {
            dto.setBusinessLine(user.getBusinessLine());
        }
        if (null != dto.getRepeatUserId() && null != dto.getApplyUserId()) {
            IdListLongReq idListLongReq = new IdListLongReq();
            List<Long> idList = new ArrayList<>();
            idList.add(dto.getRepeatUserId());
            idList.add(dto.getApplyUserId());
            idListLongReq.setIdList(idList);
            JSONResult<List<UserInfoDTO>> userInfo = userInfoFeignClient.listById(idListLongReq);
            if (userInfo != null && JSONResult.SUCCESS.equals(userInfo.getCode())
                    && userInfo.getData() != null) {
                for (UserInfoDTO userInfoDTO : userInfo.getData()) {
                    if (userInfoDTO.getId().longValue() == dto.getRepeatUserId()) {
                        dto.setRepeatOrgId(userInfoDTO.getOrgId());
                    }
                    if (userInfoDTO.getId().longValue() == dto.getApplyUserId()) {
                        dto.setApplyOrgId(userInfoDTO.getOrgId());
                    }
                }
            }
        }
        return myCustomerFeignClient.saveRepeatClue(dto);
    }

    /**
     * 重单申请查询被重单数据
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/queryRepeatCurClue")
    @ResponseBody
    public JSONResult<List<RepeatClueDTO>> queryRepeatCurClue(HttpServletRequest request,
                                                              @RequestBody RepeatClueQueryDTO dto) {
        UserInfoDTO user = getUser();
        if (user.getBusinessLine() != null) {
            dto.setBusinessLine(user.getBusinessLine());
        }
        return myCustomerFeignClient.queryRepeatClue(dto);
    }

    /**
     * 查询所有电销创业顾问
     *
     * @param request
     * @return
     */

    @RequestMapping("/listByOrgAndRole")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> listByOrgAndRole(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        UserOrgRoleReq userRole = new UserOrgRoleReq();
        List<Integer> status = new ArrayList();
        status.add(1);
        status.add(3);
        userRole.setRoleCode(RoleCodeEnum.DXCYGW.name());
        userRole.setStatusList(status);
        if (user.getBusinessLine() != null) {
            userRole.setBusinessLine(user.getBusinessLine());
        }
        return userInfoFeignClient.listByOrgAndRole(userRole);
    }

    /**
     * 重单申请保存
     *
     * @param request
     * @param dto
     * @return
     */

    @RequestMapping("/saveRepeatClue")
    @ResponseBody
    @LogRecord(description = "重单申请保存", operationType = OperationType.INSERT,
            menuName = MenuEnum.TM_MY_CUSTOMER)
    public JSONResult<String> saveRepeatClue(HttpServletRequest request,
                                             @RequestBody RepeatClueSaveDTO dto) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setOrgId(user.getOrgId());
            dto.setUserId(user.getId());
            dto.setApplyUserId(user.getId());
            dto.setApplyOrgId(user.getOrgId());
            if (user.getBusinessLine() != null) {
                dto.setBusinessLine(user.getBusinessLine());
            }
        }
        if (null != dto.getRepeatUserId()) {
            IdEntityLong id = new IdEntityLong();
            id.setId(dto.getRepeatUserId());
            JSONResult<UserInfoDTO> userInfo = userInfoFeignClient.get(id);
            if (userInfo != null && JSONResult.SUCCESS.equals(userInfo.getCode())
                    && userInfo.getData() != null) {
                dto.setRepeatOrgId(userInfo.getData().getOrgId());
            }
        }
        return myCustomerFeignClient.saveRepeatClue(dto);
    }

    /**
     * 新建资源保存
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/saveCreateClue")
    @ResponseBody
    @LogRecord(description = "新建资源保存", operationType = OperationType.INSERT,
            menuName = MenuEnum.TM_MY_CUSTOMER)
    public JSONResult<String> saveCreateClue(HttpServletRequest request, @RequestBody ClueDTO dto) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            if (RoleCodeEnum.DXCYGW.name().equals(user.getRoleList().get(0).getRoleCode())) {
                if (null != user.getBusinessLine()
                        && (user.getBusinessLine().equals(BusinessLineConstant.SHANGJI)
                        || user.getBusinessLine().equals(BusinessLineConstant.XIAOWUZHONG)
                        || user.getBusinessLine().equals(BusinessLineConstant.QUDAOTUOZHAN))
                        && CollectionUtils.isEmpty(dto.getClueFiles())) {
                    return new JSONResult<String>().fail("-1", VALIDATE_PHONE_WECHAT_MSG);
                }
                //小物种电销顾问和总监在新建客户／维护客户时在录入微信1，微信2必须上传资料，否则不允许进行保存
                if(RoleCodeEnum.DXCYGW.name().equals(user.getRoleList().get(0).getRoleCode())
                        || RoleCodeEnum.DXZJ.name().equals(user.getRoleList().get(0).getRoleCode())){
                    if (null != user.getBusinessLine()
                            && user.getBusinessLine().equals(BusinessLineConstant.XIAOWUZHONG)) {

                        if((StringUtils.isNotBlank(dto.getClueCustomer().getWechat())
                                || StringUtils.isNotBlank(dto.getClueCustomer().getWechat2()))
                                && CollectionUtils.isEmpty(dto.getClueFiles())){
                            return new JSONResult<String>().fail("-1", VALIDATE_PHONE_WECHAT_MSG);
                        }
                    }
                }
                if (null != user.getBusinessLine() && (user.getBusinessLine()
                        .equals(BusinessLineConstant.SHANGJI)
                        || user.getBusinessLine().equals(BusinessLineConstant.XIAOWUZHONG)
                        || user.getBusinessLine().equals(BusinessLineConstant.QUDAOTUOZHAN))) {
                    if (!"".equals(validateRepeatPhoneAndWeChat(user, dto))) {
                        return new JSONResult<String>().fail("-1",
                                validateRepeatPhoneAndWeChat(user, dto));
                    }

                }
            }
            // 添加创建人
            if (null != dto) {
                ClueCustomerDTO cus = dto.getClueCustomer();
                if (null != cus) {
                    cus.setCreateUser(user.getId());
                    cus.setCreateTime(new Date());
                }
                ClueBasicDTO basic = dto.getClueBasic();
                if (null != basic) {
                    // 添加创建人
                    basic.setCreateUser(user.getId());
                    basic.setCreateTime(new Date());
                }
                if (user.getBusinessLine() != null) {
                    basic.setBusinessLine(user.getBusinessLine());
                    // 广州渠道拓展业务线推广所属公司为1。其他的业务线推广所属公司均为0（快道）
                    if (BusinessLineConstant.QUDAOTUOZHAN == user.getBusinessLine()) {
                        basic.setPromotionCompany(AggregationConstant.PromotionCompany.Company_1);
                    } else {
                        basic.setPromotionCompany(AggregationConstant.PromotionCompany.Company_0);
                    }
                }
            }
            // 电销关联数据
            ClueRelateDTO relation = new ClueRelateDTO();
            // 电销顾问
            relation.setTeleSaleId(user.getId());
            // 电销组
            relation.setTeleGorupId(user.getOrgId());
            dto.setClueRelate(relation);
            UserOrgRoleReq userRole = new UserOrgRoleReq();
            userRole.setRoleCode(RoleCodeEnum.DXZJ.name());
            userRole.setOrgId(user.getOrgId());
            List<Integer> statusList = new ArrayList();
            statusList.add(1);
            userRole.setStatusList(statusList);
            JSONResult<List<UserInfoDTO>> userInfoJson =
                    userInfoFeignClient.listByOrgAndRole(userRole);
            if (userInfoJson != null && JSONResult.SUCCESS.equals(userInfoJson.getCode())
                    && userInfoJson.getData() != null && userInfoJson.getData().size() > 0) {
                // 电销总监
                relation.setTeleDirectorId(userInfoJson.getData().get(0).getId());
            }

            // 查询用户的上级
            OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
            orgDto.setId(user.getOrgId());
            orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
            JSONResult<List<OrganizationDTO>> orgJson =
                    organizationFeignClient.listParentsUntilOrg(orgDto);
            if (orgJson != null && JSONResult.SUCCESS.equals(orgJson.getCode())
                    && orgJson.getData() != null && orgJson.getData().size() > 0) {
                for (OrganizationDTO org : orgJson.getData()) {

                    if (null != org.getOrgType()
                            && org.getOrgType().equals(OrgTypeConstant.DZSYB)) {
                        relation.setTeleDeptId(org.getId());

                        UserOrgRoleReq userRoleInfo = new UserOrgRoleReq();
                        userRoleInfo.setRoleCode(RoleCodeEnum.DXFZ.name());
                        userRoleInfo.setOrgId(org.getId());
                        userRoleInfo.setStatusList(statusList);
                        JSONResult<List<UserInfoDTO>> ceoUserInfoJson =
                                userInfoFeignClient.listByOrgAndRole(userRoleInfo);
                        if (ceoUserInfoJson.getCode().equals(JSONResult.SUCCESS)
                                && null != ceoUserInfoJson.getData()
                                && ceoUserInfoJson.getData().size() > 0) {
                            // 电销副总
                            relation.setTeleCeoId(ceoUserInfoJson.getData().get(0).getId());
                        }

                    }
                    if (null != org.getOrgType()
                            && org.getOrgType().equals(OrgTypeConstant.DXFGS)) {

                        UserOrgRoleReq userRoleInfo = new UserOrgRoleReq();
                        userRoleInfo.setRoleCode(RoleCodeEnum.DXZJL.name());
                        userRoleInfo.setOrgId(org.getId());
                        userRoleInfo.setStatusList(statusList);
                        JSONResult<List<UserInfoDTO>> ceoUserInfoJson =
                                userInfoFeignClient.listByOrgAndRole(userRoleInfo);
                        if (ceoUserInfoJson.getCode().equals(JSONResult.SUCCESS)
                                && null != ceoUserInfoJson.getData()
                                && ceoUserInfoJson.getData().size() > 0) {
                            // 电销总经理
                            relation.setTeleManagerId(ceoUserInfoJson.getData().get(0).getId());
                        }
                        relation.setTeleCompanyId(org.getId());
                    }

                }

            }
            // 设置角色
            dto.setRoleCode(user.getRoleList().get(0).getRoleCode());
        }

        // 保存流转记录
        CirculationInsertOrUpdateDTO circul = new CirculationInsertOrUpdateDTO();
        circul.setAllotUserId(user.getId());
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            circul.setAllotRoleId(user.getRoleList().get(0).getId());
        }
        // add_cluecirculation
        circul.setTeleReceiveSource(
                ClueCirculationConstant.TELE_RECEIVE_SOURCE.TELE_CREATE.getCode());
        circul.setServiceStaffRole(ClueCirculationConstant.SERVICE_STAFF_ROLE.TELE_SALE.getCode());
        circul.setClueId(dto.getClueId());
        circul.setAllotOrg(user.getOrgId());
        circul.setUserId(user.getId());
        // 新资源类型，电销自己创建的和话务主管转给话务的新资源类型一致
        circul.setNewResource(ClueCirculationConstant.NewResource.OTHER_RESOURCE.getCode());
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            circul.setRoleId(user.getRoleList().get(0).getId());
        }
        circul.setOrg(user.getOrgId());
        dto.setCirculationInsertOrUpdateDTO(circul);
        JSONResult<String> customerClue = myCustomerFeignClient.createCustomerClue(dto);
        return customerClue;
    }

    /**
     * 维护资源(基本信息)提交
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/updateCustomerBasicInfoClue")
    @ResponseBody
    @LogRecord(description = "维护资源(基本信息)提交", operationType = OperationType.UPDATE,
            menuName = MenuEnum.CUSTOMER_INFO)
    public JSONResult<String> updateCustomerBasicInfoClue(HttpServletRequest request,
                                                          @RequestBody ClueDTO dto) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            // 电销总监待分配新资源 微信号已存在不允许修改和删除
            List<RoleInfoDTO> roleList = user.getRoleList();
            if (RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())
                    || RoleCodeEnum.DXCYGW.name().equals(roleList.get(0).getRoleCode())) {
                if (null != dto && null != dto.getClueId()) {
                    ClueQueryDTO clueQueryDTO = new ClueQueryDTO();
                    clueQueryDTO.setClueId(dto.getClueId());
                    JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(clueQueryDTO);
                    if (JSONResult.SUCCESS.equals(clueInfo.getCode())
                            && null != clueInfo.getData()) {
                        ClueDTO data = clueInfo.getData();
                        ClueCustomerDTO clueCustomer = data.getClueCustomer();
                        if (null != clueCustomer) {
                            String res = validateWeChat(clueCustomer, dto);
                            if (!"".equals(res)) {
                                return new JSONResult<String>().fail("-1", res);
                            }
                            String res1 = validatePhone(clueCustomer, dto);
                            if (!"".equals(res1)) {
                                return new JSONResult<String>().fail("-1", res1);
                            }
                        }
                    }
                }
            }

            dto.setUpdateUser(user.getId());
            dto.setOrg(user.getOrgId());
            if (dto.getClueCustomer().getPhoneCreateTime() != null
                    && StringUtils.isNotBlank(dto.getClueCustomer().getPhone())) {
                dto.getClueCustomer().setPhoneCreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone2CreateTime() != null
                    && StringUtils.isNotBlank(dto.getClueCustomer().getPhone2())) {
                dto.getClueCustomer().setPhone2CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone3CreateTime() != null
                    && StringUtils.isNotBlank(dto.getClueCustomer().getPhone3())) {
                dto.getClueCustomer().setPhone3CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone4CreateTime() != null
                    && StringUtils.isNotBlank(dto.getClueCustomer().getPhone4())) {
                dto.getClueCustomer().setPhone4CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone5CreateTime() != null
                    && StringUtils.isNotBlank(dto.getClueCustomer().getPhone5())) {
                dto.getClueCustomer().setPhone5CreateUser(user.getId());
            }
        }
        return myCustomerFeignClient.updateCustomerBasicInfoClue(dto);
    }

    /**
     * 维护资源提交
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/updateCustomerClue")
    @ResponseBody
    @LogRecord(description = "维护客户资源提交", operationType = OperationType.UPDATE,
            menuName = MenuEnum.CUSTOMER_INFO)
    public JSONResult<String> updateCustomerClue(HttpServletRequest request, @RequestBody ClueDTO dto) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            List<RoleInfoDTO> roleList = user.getRoleList();
            if (RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())
                    || RoleCodeEnum.DXCYGW.name().equals(roleList.get(0).getRoleCode())) {
                if (null != dto && null != dto.getClueId()) {
                    ClueQueryDTO clueQueryDTO = new ClueQueryDTO();
                    clueQueryDTO.setClueId(dto.getClueId());
                    JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(clueQueryDTO);
                    if (JSONResult.SUCCESS.equals(clueInfo.getCode())
                            && null != clueInfo.getData()) {
                        ClueDTO data = clueInfo.getData();
                        ClueCustomerDTO clueCustomer = data.getClueCustomer();
                        if (null != clueCustomer) {
                            String res = validatePhone(clueCustomer, dto);
                            if (!"".equals(res)) {
                                return new JSONResult<String>().fail("-1", res);
                            }
                            String res1 = validateWeChat(clueCustomer, dto);
                            if (!"".equals(res1)) {
                                return new JSONResult<String>().fail("-2", res1);
                            }
                        }
                        if (null != user.getBusinessLine()) {
                            if (user.getBusinessLine().equals(BusinessLineConstant.SHANGJI)
                                    || user.getBusinessLine().equals(BusinessLineConstant.XIAOWUZHONG)) {
                                String res = validateClueFile(clueCustomer, dto);
                                //新增手机号 资料上传判断
                                if(!"".equals(res)){
                                    return new JSONResult<String>().fail("-3",res);
                                }
                            }
                        }
                        if (null != user.getBusinessLine()) {
                            if (user.getBusinessLine().equals(BusinessLineConstant.XIAOWUZHONG)) {
                                String res = validateClueFileWechat(clueCustomer, dto);
                                //新增微信号 资料上传判断
                                if(!"".equals(res)){
                                    return new JSONResult<String>().fail("-3",res);
                                }
                            }
                        }
                    }
                }
            }
            dto.setUpdateUser(user.getId());
            dto.setOrg(user.getOrgId());
            if (dto.getClueCustomer().getPhoneCreateTime() != null && null != dto.getClueCustomer().getPhone()) {
                dto.getClueCustomer().setPhoneCreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone2CreateTime() != null && null != dto.getClueCustomer().getPhone2()) {
                dto.getClueCustomer().setPhone2CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone3CreateTime() != null && null != dto.getClueCustomer().getPhone3()) {
                dto.getClueCustomer().setPhone3CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone4CreateTime() != null && null != dto.getClueCustomer().getPhone4()) {
                dto.getClueCustomer().setPhone4CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone5CreateTime() != null && null != dto.getClueCustomer().getPhone5()) {
                dto.getClueCustomer().setPhone5CreateUser(user.getId());
            }
            // 设置角色
            dto.setRoleCode(user.getRoleList().get(0).getRoleCode());
        }
        return myCustomerFeignClient.updateCustomerClue(dto);
    }

    /**
     * 保留客户资源
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/reserveClue")
    @ResponseBody
    public JSONResult<String> reserveClue(HttpServletRequest request,
                                          @RequestBody ClueQueryDTO dto) {
        return myCustomerFeignClient.reserveClue(dto);
    }

    /**
     * 获取当前登录账号
     *
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 查询系统参数
     *
     * @param code
     * @return
     */
    private String getSysSetting(String code) {
        SysSettingReq sysSettingReq = new SysSettingReq();
        sysSettingReq.setCode(code);
        JSONResult<SysSettingDTO> byCode = sysSettingFeignClient.getByCode(sysSettingReq);
        if (byCode != null && JSONResult.SUCCESS.equals(byCode.getCode())) {
            return byCode.getData().getValue();
        }
        return null;
    }

    public static String convertTimeToString(Long time) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    /**
     * 查询字典表
     *
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }

    /**
     * 校验手机号
     *
     * @return
     */
    private String validatePhone(ClueCustomerDTO clueCustomer, ClueDTO dto) {
        if (StringUtils.isNotBlank(clueCustomer.getPhone())
                && !clueCustomer.getPhone().equals(dto.getClueCustomer().getPhone())) {
            return "手机号已存在";
        }
        if (StringUtils.isNotBlank(clueCustomer.getPhone2())
                && !clueCustomer.getPhone2().equals(dto.getClueCustomer().getPhone2())) {
            return "手机号2已存在";
        }
        if (StringUtils.isNotBlank(clueCustomer.getPhone3())
                && !clueCustomer.getPhone3().equals(dto.getClueCustomer().getPhone3())) {
            return "手机号3已存在";
        }
        if (StringUtils.isNotBlank(clueCustomer.getPhone4())
                && !clueCustomer.getPhone4().equals(dto.getClueCustomer().getPhone4())) {
            return "手机号4已存在";
        }
        if (StringUtils.isNotBlank(clueCustomer.getPhone5())
                && !clueCustomer.getPhone5().equals(dto.getClueCustomer().getPhone5())) {
            return "手机号5已存在";
        }
        return "";
    }

    /**
     * 校验微信号
     */
    public String validateWeChat(ClueCustomerDTO clueCustomer, ClueDTO dto) {
        // 微信 微信2 存在不允许删除修改
        if (StringUtils.isNotBlank(clueCustomer.getWechat())
                && StringUtils.isBlank(dto.getClueCustomer().getWechat())) {
            return "微信已存在不允许修改和删除";
        }
        if (StringUtils.isNotBlank(clueCustomer.getWechat2())
                && StringUtils.isBlank(dto.getClueCustomer().getWechat2())) {
            return "微信已存在不允许修改和删除";
        }
        if (StringUtils.isNotBlank(clueCustomer.getWechat())
                && StringUtils.isNotBlank(dto.getClueCustomer().getWechat())
                && !clueCustomer.getWechat().equals(dto.getClueCustomer().getWechat())) {
            return "微信已存在不允许修改和删除";
        }
        if (StringUtils.isNotBlank(clueCustomer.getWechat2())
                && StringUtils.isNotBlank(dto.getClueCustomer().getWechat2())
                && !clueCustomer.getWechat2().equals(dto.getClueCustomer().getWechat2())) {
            return "微信2已存在不允许修改和删除";
        }
        return "";
    }

    /**
     * 校验 新增手机号时候 是否上传资料 判断条件 手机号的创建时间 与资料上传的时间 5分钟以内验证通过
     */
    private String validateClueFile(ClueCustomerDTO clueCustomer, ClueDTO dto) {
        String resultStr = "";
        ClueQueryDTO clueQueryDTO = new ClueQueryDTO();
        clueQueryDTO.setClueId(dto.getClueId());
        JSONResult<List<ClueFileDTO>> clueFilesRes = myCustomerFeignClient.findClueFile(clueQueryDTO);
        List<ClueFileDTO> clueFiles = clueFilesRes.getData();
        // 新增
        if (StringUtils.isBlank(clueCustomer.getPhone())
                && StringUtils.isNotBlank(dto.getClueCustomer().getPhone())) {
            if (!clueFilesRes.getCode().equals(JSONResult.SUCCESS) || null == clueFiles
                    || clueFiles.size() == 0) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
            Collections.sort(clueFiles, new Comparator<ClueFileDTO>() {
                @Override
                public int compare(ClueFileDTO o1, ClueFileDTO o2) {
                    return o2.getUploadTime().compareTo(o1.getUploadTime());
                }
            });
            Date phoneCreateTime = dto.getClueCustomer().getPhoneCreateTime();
            ClueFileDTO clueFileDTO = clueFiles.get(0);
            long diffMinuteLong =
                    Math.abs(DateUtil.diffMinuteLong(phoneCreateTime, clueFileDTO.getUploadTime()));
            if (diffMinuteLong > DIFF_MIN) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
        }
        if (StringUtils.isBlank(clueCustomer.getPhone2())
                && StringUtils.isNotBlank(dto.getClueCustomer().getPhone2())) {
            if (!clueFilesRes.getCode().equals(JSONResult.SUCCESS) || null == clueFiles
                    || clueFiles.size() == 0) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
            Collections.sort(clueFiles, new Comparator<ClueFileDTO>() {
                @Override
                public int compare(ClueFileDTO o1, ClueFileDTO o2) {
                    return o2.getUploadTime().compareTo(o1.getUploadTime());
                }
            });
            Date phoneCreateTime = dto.getClueCustomer().getPhone2CreateTime();
            ClueFileDTO clueFileDTO = clueFiles.get(0);
            long diffMinuteLong =
                    Math.abs(DateUtil.diffMinuteLong(phoneCreateTime, clueFileDTO.getUploadTime()));
            if (diffMinuteLong > DIFF_MIN) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
        }
        if (StringUtils.isBlank(clueCustomer.getPhone3())
                && StringUtils.isNotBlank(dto.getClueCustomer().getPhone3())) {
            if (!clueFilesRes.getCode().equals(JSONResult.SUCCESS) || null == clueFiles
                    || clueFiles.size() == 0) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
            Collections.sort(clueFiles, new Comparator<ClueFileDTO>() {
                @Override
                public int compare(ClueFileDTO o1, ClueFileDTO o2) {
                    return o2.getUploadTime().compareTo(o1.getUploadTime());
                }
            });
            Date phoneCreateTime = dto.getClueCustomer().getPhone3CreateTime();
            ClueFileDTO clueFileDTO = clueFiles.get(0);
            long diffMinuteLong =
                    Math.abs(DateUtil.diffMinuteLong(phoneCreateTime, clueFileDTO.getUploadTime()));
            if (diffMinuteLong > DIFF_MIN) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
        }
        if (StringUtils.isBlank(clueCustomer.getPhone4())
                && StringUtils.isNotBlank(dto.getClueCustomer().getPhone4())) {
            if (!clueFilesRes.getCode().equals(JSONResult.SUCCESS) || null == clueFiles
                    || clueFiles.size() == 0) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
            Collections.sort(clueFiles, new Comparator<ClueFileDTO>() {
                @Override
                public int compare(ClueFileDTO o1, ClueFileDTO o2) {
                    return o2.getUploadTime().compareTo(o1.getUploadTime());
                }
            });
            Date phoneCreateTime = dto.getClueCustomer().getPhone4CreateTime();
            ClueFileDTO clueFileDTO = clueFiles.get(0);
            long diffMinuteLong =
                    Math.abs(DateUtil.diffMinuteLong(phoneCreateTime, clueFileDTO.getUploadTime()));
            if (diffMinuteLong > DIFF_MIN) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
        }
        if (StringUtils.isBlank(clueCustomer.getPhone5())
                && StringUtils.isNotBlank(dto.getClueCustomer().getPhone5())) {
            if (!clueFilesRes.getCode().equals(JSONResult.SUCCESS) || null == clueFiles
                    || clueFiles.size() == 0) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
            Collections.sort(clueFiles, new Comparator<ClueFileDTO>() {
                @Override
                public int compare(ClueFileDTO o1, ClueFileDTO o2) {
                    return o2.getUploadTime().compareTo(o1.getUploadTime());
                }
            });
            Date phoneCreateTime = dto.getClueCustomer().getPhone5CreateTime();
            ClueFileDTO clueFileDTO = clueFiles.get(0);
            long diffMinuteLong =
                    Math.abs(DateUtil.diffMinuteLong(phoneCreateTime, clueFileDTO.getUploadTime()));
            if (diffMinuteLong > DIFF_MIN) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
        }
        return resultStr;
    }


    /**
     *  维护客户时在录入微信1，微信2必须上传资料，否则不允许进行保存
     */
    private String validateClueFileWechat(ClueCustomerDTO clueCustomer, ClueDTO dto) {
        String resultStr = "";
        ClueQueryDTO clueQueryDTO = new ClueQueryDTO();
        clueQueryDTO.setClueId(dto.getClueId());
        JSONResult<List<ClueFileDTO>> clueFilesRes = myCustomerFeignClient.findClueFile(clueQueryDTO);
        List<ClueFileDTO> clueFiles = clueFilesRes.getData();
        if ((StringUtils.isBlank(clueCustomer.getWechat())
                && StringUtils.isNotBlank(dto.getClueCustomer().getWechat()))
                ||(StringUtils.isBlank(clueCustomer.getWechat2())
                && StringUtils.isNotBlank(dto.getClueCustomer().getWechat2()))) {
            if (!clueFilesRes.getCode().equals(JSONResult.SUCCESS) || null == clueFiles
                    || clueFiles.size() == 0) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
            Collections.sort(clueFiles, new Comparator<ClueFileDTO>() {
                @Override
                public int compare(ClueFileDTO o1, ClueFileDTO o2) {
                    return o2.getUploadTime().compareTo(o1.getUploadTime());
                }
            });
            ClueFileDTO clueFileDTO = clueFiles.get(0);
            long diffMinuteLong = Math.abs(DateUtil.diffMinuteLong(new Date(), clueFileDTO.getUploadTime()));
            if (diffMinuteLong > DIFF_MIN) {
                return VALIDATE_PHONE_WECHAT_MSG;
            }
        }
        return resultStr;
    }

    private String validateRepeatPhoneAndWeChat(UserInfoDTO user, ClueDTO dto) {
        PushClueReq pushClueReq = new PushClueReq();
        if (null != user.getOrgId()) {
            pushClueReq.setTeleGorupId(user.getOrgId());
        }
        if (StringUtils.isNotBlank(dto.getClueCustomer().getPhone())) {
            pushClueReq.setPhone(dto.getClueCustomer().getPhone());
        }
        if (StringUtils.isNotBlank(dto.getClueCustomer().getPhone2())) {
            pushClueReq.setPhone2(dto.getClueCustomer().getPhone2());
        }
        if (StringUtils.isNotBlank(dto.getClueCustomer().getPhone3())) {
            pushClueReq.setPhone3(dto.getClueCustomer().getPhone3());
        }
        if (StringUtils.isNotBlank(dto.getClueCustomer().getPhone4())) {
            pushClueReq.setPhone4(dto.getClueCustomer().getPhone4());
        }
        if (StringUtils.isNotBlank(dto.getClueCustomer().getPhone5())) {
            pushClueReq.setPhone5(dto.getClueCustomer().getPhone5());
        }
        if (StringUtils.isNotBlank(dto.getClueCustomer().getWechat())) {
            pushClueReq.setWechat(dto.getClueCustomer().getWechat());
        }
        if (StringUtils.isNotBlank(dto.getClueCustomer().getWechat2())) {
            pushClueReq.setWechat2(dto.getClueCustomer().getWechat2());
        }
        // 小物种业务线手机号微信验证
        if (user.getBusinessLine().equals(BusinessLineConstant.XIAOWUZHONG)) {
            pushClueReq.setBusinessLine(BusinessLineConstant.XIAOWUZHONG);
            pushClueReq.setDay(DAY_15);
        }
        // 渠道拓展业务线手机号微信验证
        /*if (user.getBusinessLine().equals(BusinessLineConstant.QUDAOTUOZHAN)) {
            pushClueReq.setBusinessLine(BusinessLineConstant.QUDAOTUOZHAN);
            pushClueReq.setDay(DAY_7);
        }*/
        // 商机盒子业务线手机号微信验证
        if (user.getBusinessLine().equals(BusinessLineConstant.SHANGJI)) {
            pushClueReq.setBusinessLine(BusinessLineConstant.SHANGJI);
            pushClueReq.setDay(DAY_15);
        }
        if (user.getBusinessLine().equals(BusinessLineConstant.XIAOWUZHONG) || user.getBusinessLine().equals(BusinessLineConstant.SHANGJI)) {
            JSONResult<Boolean> clueByParamRes =
                    deduplicationDetailFeignClient.getClueByParam(pushClueReq);
            if (JSONResult.SUCCESS.equals(clueByParamRes.getCode()) && clueByParamRes.getData()) {
                return "此号码已存在，不允许进行再次进行创建";
            }
        }

        return "";
    }

    /**
     * 初始化我的客户
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/customerVisitToday")
    public String customerVisitToday(HttpServletRequest request, Model model) {

        request.setAttribute("hJtype",request.getParameter("hJtype"));
        return "visit/customerVisitToday";
    }

    /**
     * 查询当前资源对应的重复资源
     * @return
     */
    @RequestMapping("/getRepeatClueRecordDTOList")
    @ResponseBody
    public JSONResult<List<RepeatClueRecordDTO>> getRepeatClueRecordDTOList(@RequestBody RepeatClueRecordQueryDTO recordQueryDTO){
        //TODO 业务线8 删除搜索词
        JSONResult<List<RepeatClueRecordDTO>> listJSONResult = repeatClueRecordFeignClient.queryList(recordQueryDTO);
        Integer businessLine = getUser().getBusinessLine();
        if(null != businessLine && businessLine.equals(8)){
            List<RepeatClueRecordDTO> data = listJSONResult.getData();
            if(CollectionUtils.isNotEmpty(data)){
                for(RepeatClueRecordDTO repeatClueRecordDTO : data){
                    repeatClueRecordDTO.getClueDTO().getClueBasic().setSearchWord(null);
                }
            }
        }
        return listJSONResult;
    }
    /**
     * @Description:构建电销今日跟访次数下拉列表值
     * @Param:
     * @return:
     * @author: fanjd
     * @date: 2020/8/12 14:32
     * @version: V1.0
     */
    private List<String> buildTodayFollowNum(String  roleCode) {
        List<String> todayFollowNumList =    new ArrayList<>();
        // 获取外包业务线
        String todayFollowNumStr = getSysSetting(SysConstant.TODAYFOLLOWNUM);
        if (StringUtils.isBlank(todayFollowNumStr)) {
            return  todayFollowNumList;
        }
        String []  todayFollowNumArr = todayFollowNumStr.split(",");
        for (String str : todayFollowNumArr) {
            if (roleCode.equals(RoleCodeEnum.GLY.name())) {
                if (!TODAYFOLLOWNUM_ZERO.equals(str)) {
                    todayFollowNumList.add(str) ;
                }
            }else{
                todayFollowNumList.add(str) ;
            }
        }
        return todayFollowNumList;
    }

    public TelemarketingLayoutDTO getTelemarketingLayoutDTO(UserInfoDTO user,String roleCode,TelemarketingLayoutDTO telemarketingLayoutDTO,Integer orgType){
        // 获取电销布局信息
        if (roleCode.equals(RoleCodeEnum.DXZJ.name())
                || roleCode.equals(RoleCodeEnum.DXCYGW.name()) || roleCode.equals(RoleCodeEnum.DXFZ.name()) || roleCode.equals(RoleCodeEnum.DXZJL.name())) {
            if(roleCode.equals(RoleCodeEnum.DXFZ.name()) || roleCode.equals(RoleCodeEnum.DXZJL.name())){
                OrganizationQueryDTO reqDto = new OrganizationQueryDTO();
                reqDto.setParentId(user.getOrgId());
                reqDto.setOrgType(orgType);
                JSONResult<List<OrganizationDTO>> jsonResult =
                        organizationFeignClient.listDescenDantByParentId(reqDto);
                if (jsonResult.getCode().equals(JSONResult.SUCCESS)
                        && CollectionUtils.isNotEmpty(jsonResult.getData())) {
                    List<Long> teleGroupIds = jsonResult.getData().stream().map(a->a.getId()).collect(Collectors.toList());
                    telemarketingLayoutDTO.setTeleGroupIds(teleGroupIds);
                }else{
                    return telemarketingLayoutDTO;
                }
            }
            if(roleCode.equals(RoleCodeEnum.DXZJ.name())
                    || roleCode.equals(RoleCodeEnum.DXCYGW.name())){
                telemarketingLayoutDTO.setTelemarketingTeamId(user.getOrgId());
            }
            JSONResult<TelemarketingLayoutDTO> telemarketingLayoutResult =
                    telemarketingLayoutFeignClient
                            .getTelemarketingLayoutByTeamId(telemarketingLayoutDTO);
            if (telemarketingLayoutResult.getCode().equals(JSONResult.SUCCESS)
                    && telemarketingLayoutResult.getData() != null
                    && telemarketingLayoutResult.getData().getId() != null) {
                telemarketingLayoutDTO = telemarketingLayoutResult.getData();
            }
        }
        return telemarketingLayoutDTO;
    }
}
