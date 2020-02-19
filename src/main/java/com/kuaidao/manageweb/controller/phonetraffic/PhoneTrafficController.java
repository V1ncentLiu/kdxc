package com.kuaidao.manageweb.controller.phonetraffic;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationReqDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationRespDTO;
import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.clue.ClueAppiontmentReq;
import com.kuaidao.aggregation.dto.clue.ClueBasicDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueFileDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.ClueRepeatPhoneDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficParamDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficRespDTO;
import com.kuaidao.aggregation.dto.phonetraffic.TrafficParam;
import com.kuaidao.aggregation.dto.tracking.TrackingReqDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingRespDTO;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.CluePhase;
import com.kuaidao.common.constant.PhTraCustomerStatusEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.StageContant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.circulation.CirculationFeignClient;
import com.kuaidao.manageweb.feign.clue.AppiontmentFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.phonetraffic.PhoneTrafficFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.tracking.TrackingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;

/**
 * @Auther: admin
 * @Date: 2019/3/18 17:51
 * @Description:
 */
@Controller
@RequestMapping("/phonetraffic")
public class PhoneTrafficController {
    private static Logger logger = LoggerFactory.getLogger(PhoneTrafficController.class);

    @Autowired
    private ClueBasicFeignClient clueBasicFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private InfoAssignFeignClient infoAssignFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    PhoneTrafficFeignClient phoneTrafficFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private CallRecordFeign callRecordFeign;

    @Autowired
    private TrackingFeignClient trackingFeignClient;

    @Autowired
    private CirculationFeignClient circulationFeignClient;

    @Autowired
    private MyCustomerFeignClient myCustomerFeignClient;

    @Autowired
    RoleManagerFeignClient roleManagerFeignClient;
    @Autowired
    private AppiontmentFeignClient appiontmentFeignClient;


    @Value("${oss.url.directUpload}")
    private String ossUrl;

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }
        // 话务人员

        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("aggregation:PhoneTraffic");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());

        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:PhoneTraffic");
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());



        Integer flag = AggregationConstant.YES;
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.get(0) != null) {
            if (RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())
                    || RoleCodeEnum.HWZG.name().equals(roleList.get(0).getRoleCode())
                    ||RoleCodeEnum.HWJL.name().equals(roleList.get(0).getRoleCode())) {
                request.setAttribute("phtrafficList", phTrafficList());
                flag = AggregationConstant.NO;
            } else {
                List<UserInfoDTO> list = new ArrayList<>();
                list.add(user);
                Map<String, List<UserInfoDTO>> map = new HashMap<>();
                map.put("phUsers", list);
                map.put("phAllUsers", list);
                request.setAttribute("phtrafficList", map);
            }
        }
        request.setAttribute("roleCode",roleList.get(0).getRoleCode());
        request.setAttribute("editflag", flag);
        return "phonetraffic/customManagement";
    }

    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<PhoneTrafficRespDTO>> queryListPage(
            @RequestBody PhoneTrafficParamDTO param) {
        logger.info("============分页数据查询==================");
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();

        // 【话务主管】—待处理，指阶段为“待分配话务”的阶段；已处理—指待分配话务之后的流转阶段。
        // 【话务专员/信息流专员】——待处理，指阶段为“话务跟进中”的阶段；已处理—指话务跟进中之后的流转阶段。
        // 话务主管还能够看见：走了优化类分配规则+但是分配不成功的数据
        // 权限相关代码
        if (roleList != null && roleList.get(0) != null) {
            if (RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())
                    || RoleCodeEnum.HWZG.name().equals(roleList.get(0).getRoleCode()) ||
                    RoleCodeEnum.HWJL.name().equals(roleList.get(0).getRoleCode())) {
                // 这样的逻辑 下管理员能够看见电销的数据。
                if (RoleCodeEnum.HWZG.name().equals(roleList.get(0).getRoleCode())) {
                    param.setPhTraDirectorId(user.getId());
                    param.setRoleCode(RoleCodeEnum.HWZG.name());
                } else if(RoleCodeEnum.HWJL.name().equals(roleList.get(0).getRoleCode())){
                    param.setRoleCode(RoleCodeEnum.HWZG.name());
                }
                Integer dealStatus = param.getDealStatus();
                if (dealStatus != null && dealStatus == 0) {
                    param.setPhase(CluePhase.PHASE_1ST.getCode());
                } else if (dealStatus != null && dealStatus == 1) {
                    List<Integer> phases = new ArrayList<>();
                    phases.add(CluePhase.PHAE_2ND.getCode());
                    phases.add(CluePhase.PHAE_3RD.getCode());
                    phases.add(CluePhase.PHAE_4TH.getCode());
                    phases.add(CluePhase.PHAE_5TH.getCode());
                    phases.add(CluePhase.PHAE_6TH.getCode());
                    phases.add(CluePhase.PHAE_10TH.getCode());
                    phases.add(CluePhase.PHAE_11TH.getCode());
                    phases.add(CluePhase.PHAE_12TH.getCode());
                    param.setPhases(phases);
                } else {
                    List<Integer> phases = new ArrayList<>();
                    phases.add(CluePhase.PHASE_1ST.getCode());
                    phases.add(CluePhase.PHAE_2ND.getCode());
                    phases.add(CluePhase.PHAE_3RD.getCode());
                    phases.add(CluePhase.PHAE_4TH.getCode());
                    phases.add(CluePhase.PHAE_5TH.getCode());
                    phases.add(CluePhase.PHAE_6TH.getCode());
                    phases.add(CluePhase.PHAE_10TH.getCode());
                    phases.add(CluePhase.PHAE_11TH.getCode());
                    phases.add(CluePhase.PHAE_12TH.getCode());
                    param.setPhases(phases);
                }
            } else {
                param.setOperatorId(user.getId());
                Integer dealStatus = param.getDealStatus();
                if (dealStatus != null && dealStatus == 0) {
                    param.setPhase(CluePhase.PHAE_2ND.getCode());
                } else if (dealStatus != null && dealStatus == 1) {
                    List<Integer> phases = new ArrayList<>();
                    phases.add(CluePhase.PHAE_3RD.getCode());
                    phases.add(CluePhase.PHAE_4TH.getCode());
                    phases.add(CluePhase.PHAE_5TH.getCode());
                    phases.add(CluePhase.PHAE_6TH.getCode());
                    phases.add(CluePhase.PHAE_10TH.getCode());
                    phases.add(CluePhase.PHAE_11TH.getCode());
                    phases.add(CluePhase.PHAE_12TH.getCode());
                    param.setPhases(phases);
                } else {
                    List<Integer> phases = new ArrayList<>();
                    phases.add(CluePhase.PHAE_2ND.getCode());
                    phases.add(CluePhase.PHAE_3RD.getCode());
                    phases.add(CluePhase.PHAE_4TH.getCode());
                    phases.add(CluePhase.PHAE_5TH.getCode());
                    phases.add(CluePhase.PHAE_6TH.getCode());
                    phases.add(CluePhase.PHAE_10TH.getCode());
                    phases.add(CluePhase.PHAE_11TH.getCode());
                    phases.add(CluePhase.PHAE_12TH.getCode());
                    param.setPhases(phases);
                }
            }
        }

        String defineColumn = param.getDefineColumn();
        String defineValue = param.getDefineValue();
        if (StringUtils.isNotBlank(defineColumn) && StringUtils.isNotBlank(defineValue)) {
            if ("phone".equals(defineColumn)) {
                param.setPhone(defineValue);
            } else if ("cusName".equals(defineColumn)) {
                param.setCusName(defineValue);
            } else if ("qq".equals(defineColumn)) {
                param.setQq(defineValue);
            } else if ("wx".equals(defineColumn)) {
                param.setWx(defineValue);
            } else if ("email".equals(defineColumn)) {
                param.setEmail(defineValue);
            }
        }

        // 时间判断：
        Date date1 = param.getCreateTime1();
        Date date2 = param.getCreateTime2();
        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                return new JSONResult().fail("-1", "创建时间，开始时间大于结束时间!");
            }
        }
        return phoneTrafficFeignClient.queryList(param);
    }

    /**
     * 分配资源
     * 
     * @return
     */
    @PostMapping("/allocationClue")
    @ResponseBody
    @RequiresPermissions("aggregation:PhoneTraffic:allocation")
    @LogRecord(description = "分配资源", operationType = LogRecord.OperationType.DISTRIBUTION,
            menuName = MenuEnum.PHONETRAFFIC_MANAGER)
    public JSONResult allocationClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        allocationClueReq.setOrg(user.getOrgId());
        return phoneTrafficFeignClient.allocationClue(allocationClueReq);
    }

    /**
     * 分配资源
     * 
     * @return
     */
    @PostMapping("/toTele")
    @ResponseBody
    @LogRecord(description = "话务转电销/保存", operationType = LogRecord.OperationType.DISTRIBUTION,
            menuName = MenuEnum.PHONETRAFFIC_MANAGER)
    public JSONResult toTele(@RequestBody ClueDTO clueDTO) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        clueDTO.setOrg(user.getOrgId());
        clueDTO.setUpdateUser(user.getId());
        clueDTO.setPhUpdateTime(new Date());
        if(clueDTO.getClueCustomer().getPhone2CreateTime() != null){
            clueDTO.getClueCustomer().setPhone2CreateUser(user.getId());
        }
        if(clueDTO.getClueCustomer().getPhone3CreateTime() != null){
            clueDTO.getClueCustomer().setPhone3CreateUser(user.getId());
        }
        if(clueDTO.getClueCustomer().getPhone4CreateTime() != null){
            clueDTO.getClueCustomer().setPhone4CreateUser(user.getId());
        }
        if(clueDTO.getClueCustomer().getPhone5CreateTime() != null){
            clueDTO.getClueCustomer().setPhone5CreateUser(user.getId());
        }
        return phoneTrafficFeignClient.toTele(clueDTO);
    }



    /**
     * 跳转 编辑资源页面
     */
    @RequestMapping("/toEditPage")
    public String toEditPage(HttpServletRequest request, @RequestParam String clueId) {
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(clueId);
        JSONResult<List<CallRecordRespDTO>> callRecord =
                callRecordFeign.listTmCallReacordByParamsNoPage(call);

        // 资源通话记录
        if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode())
                && callRecord.getData() != null) {
            request.setAttribute("callRecord", callRecord.getData());
            CallRecordRespDTO callRecordRespDTO = callRecord.getData().stream()
                    .filter(a -> StringUtils.isNotBlank(a.getStartTime()))
                    .max(Comparator.comparing(CallRecordRespDTO::getStartTime)).get();
            if (callRecordRespDTO != null) {
                String date =
                        convertTimeToString(Long.valueOf(callRecordRespDTO.getStartTime()) * 1000L);
                request.setAttribute("teleEndTime", date);
            } else {
                request.setAttribute("teleEndTime", new Date());
            }
        } else {
            request.setAttribute("teleEndTime", new Date());
        }
        ClueQueryDTO queryDTO = new ClueQueryDTO();

        queryDTO.setClueId(new Long(clueId));

        request.setAttribute("clueId", clueId);

        request.setAttribute("ossUrl", ossUrl);

        JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);


        // 维护的资源数据
        if (clueInfo != null && JSONResult.SUCCESS.equals(clueInfo.getCode())
                && clueInfo.getData() != null) {

            if (null != clueInfo.getData().getClueCustomer()) {
                request.setAttribute("customer", clueInfo.getData().getClueCustomer());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueBasic()) {
                request.setAttribute("base", clueInfo.getData().getClueBasic());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueIntention()) {
                request.setAttribute("intention", clueInfo.getData().getClueIntention());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
        }
        // 获取资源跟进记录数据
        TrackingReqDTO dto = new TrackingReqDTO();
        dto.setClueId(new Long(clueId));
        dto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
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
        circDto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
        JSONResult<List<CirculationRespDTO>> circulationList =
                circulationFeignClient.queryList(circDto);

        ClueBasicDTO clueBasic = clueInfo.getData().getClueBasic();
        if (circulationList != null && JSONResult.SUCCESS.equals(circulationList.getCode())
                && circulationList.getData() != null) {

            List<CirculationRespDTO> data = circulationList.getData();
            JSONResult<List<CirculationRespDTO>> cDxcygwList = getCDxcygwList(clueBasic);
            if (cDxcygwList != null && JSONResult.SUCCESS.equals(cDxcygwList.getCode())) {
                data.addAll(cDxcygwList.getData());
            }
            request.setAttribute("circulationList", data);
        } else {
            request.setAttribute("circulationList", new ArrayList());
        }
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            List<ProjectInfoDTO> projectInfoDTOList = proJson.getData().parallelStream().filter(pro -> pro.getIsNotSign() !=null && pro.getIsNotSign() == 0).collect( Collectors.toList());
            request.setAttribute("proSelect", projectInfoDTOList);
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }

        // 获取已上传的文件数据
        ClueQueryDTO fileDto = new ClueQueryDTO();
        fileDto.setClueId(new Long(clueId));
        fileDto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
        JSONResult<List<ClueFileDTO>> clueFileList = myCustomerFeignClient.findClueFile(fileDto);
        if (clueFileList != null && JSONResult.SUCCESS.equals(clueFileList.getCode())
                && clueFileList.getData() != null) {
            request.setAttribute("clueFileList", clueFileList.getData());
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        request.setAttribute("loginUserId", user.getId());
        //话务经理 调整到查看页
        if(RoleCodeEnum.HWJL.name().equals(user.getRoleList().get(0).getRoleCode())){
//             return "phonetraffic/viewCustomerMaintenance";
             return "phonetraffic/viewCustomerMaintenance";
        }
        return "phonetraffic/editCustomerMaintenance";
    }


    private JSONResult<List<CirculationRespDTO>> getCDxcygwList(ClueBasicDTO clueBasic) {
        JSONResult<List<CirculationRespDTO>> jsonResult = null;
        Integer phtraCustomerStatus = clueBasic.getPhCustomerStatus();
        if (phtraCustomerStatus == Integer.valueOf(PhTraCustomerStatusEnum.STATUS__4TH.getCode())) {
            // 如果== 转电销
            CirculationReqDTO circDto = new CirculationReqDTO();
            circDto.setClueId(new Long(clueBasic.getId()));
            circDto.setStage(StageContant.STAGE_TELE);
            // 获取电销创业顾问 RoleID
            RoleQueryDTO queryDTO1 = new RoleQueryDTO();
            queryDTO1.setRoleCode(RoleCodeEnum.DXCYGW.name());
            JSONResult<List<RoleInfoDTO>> listJSONResult =
                    roleManagerFeignClient.qeuryRoleByName(queryDTO1);
            if (JSONResult.SUCCESS.equals(listJSONResult.getCode())
                    && listJSONResult.getData() != null) {
                List<RoleInfoDTO> data = listJSONResult.getData();
                circDto.setRoleId(data.get(0).getId());
                jsonResult = circulationFeignClient.queryList(circDto);

            }
        }
        return jsonResult;
    }

    /**
     * 跳转 编辑资源页面
     */
    @RequestMapping("/toReadyOnlyPage")
    public String toReadyOnlyPage(HttpServletRequest request, @RequestParam String clueId) {
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(clueId);
        JSONResult<List<CallRecordRespDTO>> callRecord =
                callRecordFeign.listTmCallReacordByParamsNoPage(call);

        // 资源通话记录
        if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode())
                && callRecord.getData() != null) {
            request.setAttribute("callRecord", callRecord.getData());
        }

        ClueQueryDTO queryDTO = new ClueQueryDTO();

        queryDTO.setClueId(new Long(clueId));

        request.setAttribute("clueId", clueId);

        request.setAttribute("ossUrl", ossUrl);

        JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);

        // 维护的资源数据
        if (clueInfo != null && JSONResult.SUCCESS.equals(clueInfo.getCode())
                && clueInfo.getData() != null) {

            if (null != clueInfo.getData().getClueCustomer()) {
                request.setAttribute("customer", clueInfo.getData().getClueCustomer());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueBasic()) {
                request.setAttribute("base", clueInfo.getData().getClueBasic());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueIntention()) {
                request.setAttribute("intention", clueInfo.getData().getClueIntention());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
        }
        // 获取资源跟进记录数据
        TrackingReqDTO dto = new TrackingReqDTO();
        dto.setClueId(new Long(clueId));
        dto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
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
        circDto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
        JSONResult<List<CirculationRespDTO>> circulationList =
                circulationFeignClient.queryList(circDto);

        ClueBasicDTO clueBasic = clueInfo.getData().getClueBasic();
        if (circulationList != null && JSONResult.SUCCESS.equals(circulationList.getCode())
                && circulationList.getData() != null) {
            List<CirculationRespDTO> data = circulationList.getData();
            JSONResult<List<CirculationRespDTO>> cDxcygwList = getCDxcygwList(clueBasic);
            if (cDxcygwList != null && JSONResult.SUCCESS.equals(cDxcygwList.getCode())) {
                data.addAll(cDxcygwList.getData());
            }
            request.setAttribute("circulationList", data);
        } else {
            request.setAttribute("circulationList", new ArrayList());
        }
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }

        // 获取已上传的文件数据
        ClueQueryDTO fileDto = new ClueQueryDTO();
        fileDto.setClueId(new Long(clueId));
        fileDto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
        JSONResult<List<ClueFileDTO>> clueFileList = myCustomerFeignClient.findClueFile(fileDto);
        if (clueFileList != null && JSONResult.SUCCESS.equals(clueFileList.getCode())
                && clueFileList.getData() != null) {
            request.setAttribute("clueFileList", clueFileList.getData());
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        request.setAttribute("loginUserId", user.getId());
        return "phonetraffic/readOnlyCustomerMaintenance";
    }


    /**
     * 转电销
     */
    public void toTele() {
        // 通过规则，转到电销
    }

    /**
     *
     */



    /**
     * 查询非禁用账户
     * 
     * @return
     */
    private Map<String, List<UserInfoDTO>> phTrafficList() {
        List<UserInfoDTO> userList = new ArrayList();
        UserInfoDTO user = CommUtil.getCurLoginUser();
        //如果是话务经理
        if(RoleCodeEnum.HWJL.name().equals(user.getRoleList().get(0).getRoleCode())){
            OrganizationQueryDTO dto=new OrganizationQueryDTO();
            dto.setParentId(user.getOrgId());
            //查询话务经理下所有话务组
            JSONResult<List<OrganizationRespDTO>> orgListDto= organizationFeignClient.queryOrgByParam(dto);
            if (JSONResult.SUCCESS.equals(orgListDto.getCode())) {
                List<OrganizationRespDTO> orglist=orgListDto.getData();
                for(OrganizationRespDTO org:orglist){
                    //查询话务组下面的话务员
                    UserOrgRoleReq req=new UserOrgRoleReq();
                    req.setRoleCode(RoleCodeEnum.HWY.name());
                    req.setOrgId(org.getId());
                    JSONResult<List<UserInfoDTO>> jsonIfo= userInfoFeignClient.listByOrgAndRole(req);
                    userList.addAll(jsonIfo.getData());
                }
            }
        }else{
            RoleQueryDTO query = new RoleQueryDTO();
            query.setRoleCode(RoleCodeEnum.HWZG.name());
            JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
            if (JSONResult.SUCCESS.equals(roleJson.getCode())) {
                List<RoleInfoDTO> roleList = roleJson.getData();
                if (null != roleList && roleList.size() > 0) {
                    RoleInfoDTO roleDto = roleList.get(0);
                    UserInfoPageParam param = new UserInfoPageParam();
                    // param.setRoleId(roleDto.getId()); // 查询该组织下，该角色的全部员工。去掉就是查询全部该组织下的员工
                    param.setOrgId(user.getOrgId());
                    param.setPageSize(10000);
                    param.setPageNum(1);
                    JSONResult<PageBean<UserInfoDTO>> userListJson = userInfoFeignClient.list(param);
                    if (JSONResult.SUCCESS.equals(userListJson.getCode())) {
                        PageBean<UserInfoDTO> pageList = userListJson.getData();
                        userList = pageList.getData();
                    }
                }
            }
        }
        // 查询话务总监ID
        Long roleId = null;
        RoleQueryDTO query1 = new RoleQueryDTO();
        query1.setRoleCode(RoleCodeEnum.HWZG.name());
        JSONResult<List<RoleInfoDTO>> roleJson1 = roleManagerFeignClient.qeuryRoleByName(query1);
        if (JSONResult.SUCCESS.equals(roleJson1.getCode())) {
            List<RoleInfoDTO> data = roleJson1.getData();
            if (null != data && data.size() > 0) {
                RoleInfoDTO roleInfoDTO = data.get(0);
                roleId = roleInfoDTO.getId();
            }
        }
        List<UserInfoDTO> user1List = new ArrayList();
        List<UserInfoDTO> user2List = new ArrayList();
        boolean falg = true;
        for (UserInfoDTO userInfo : userList) {
            Integer status = userInfo.getStatus();
            Long id = userInfo.getId();
            if (roleId != null && !roleId.equals(userInfo.getRoleId())) { // 剔除话务总监
                if (status != 2) { // 提出禁用
                    user1List.add(userInfo);
                }
                user2List.add(userInfo);
            }
        }

        Map<String, List<UserInfoDTO>> map = new HashMap<>();
        map.put("phUsers", user1List);
        map.put("phAllUsers", user2List);
        return map;
    }

    /**
     * 释放资源到共有池以及废弃池
     */
    @RequestMapping("/release")
    @ResponseBody
    public JSONResult<Boolean> releaseClue(@RequestBody TrafficParam trafficParam) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        trafficParam.setCreateUser(user.getId());
        return phoneTrafficFeignClient.releaseClue(trafficParam);
    }

    @PostMapping("/repeatPhonelist")
    @ResponseBody
    public JSONResult<List<ClueRepeatPhoneDTO>> repeatPhonelist(
            @RequestBody ClueAppiontmentReq param, HttpServletRequest request) {
        JSONResult<List<ClueRepeatPhoneDTO>> list = appiontmentFeignClient.repeatPhonelist(param);
        return list;
    }

    public static String convertTimeToString(Long time) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }
}
