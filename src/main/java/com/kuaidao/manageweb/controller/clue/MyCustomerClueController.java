package com.kuaidao.manageweb.controller.clue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
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
import com.kuaidao.aggregation.constant.ClueCirculationConstant;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationReqDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationRespDTO;
import com.kuaidao.aggregation.dto.clue.ClueBasicDTO;
import com.kuaidao.aggregation.dto.clue.ClueCustomerDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueFileDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.ClueRelateDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.ReleaseClueDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.RepeatClueSaveDTO;
import com.kuaidao.aggregation.dto.clueappiont.ClueAppiontmentDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingReqDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingRespDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.SortUtils;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.circulation.CirculationFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.tracking.TrackingFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/tele/clueMyCustomerInfo")
public class MyCustomerClueController {
    private static Logger logger = LoggerFactory.getLogger(MyCustomerClueController.class);
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

    @Value("${oss.url.directUpload}")
    private String ossUrl;

    /**
     * 初始化我的客户
     * 
     * @param request
     * @param model
     * @return
     */
    @RequiresPermissions("myCustomerInfo:view")
    @RequestMapping("/initmyCustomer")
    public String initmyCustomer(HttpServletRequest request, Model model) {
        UserInfoDTO user = getUser();
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("myCustomerInfo");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setMenuCode("myCustomerInfo");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        request.setAttribute("ossUrl", ossUrl);
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
        long time1 = new Date().getTime();
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

        JSONResult<PageBean<CustomerClueDTO>> jsonResult =
                myCustomerFeignClient.findTeleClueInfo(dto);
        long time2 = new Date().getTime();
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
    public String customerEditInfo(HttpServletRequest request, @RequestParam String clueId) {
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
                } else if (roleCode.equals(RoleCodeEnum.DXCYGW.name())) {
                    accountList.add(user.getId());
                }
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

        dto.setClueId(new Long(clueId));
        JSONResult<List<TrackingRespDTO>> trackingList = trackingFeignClient.queryList(dto);
        if (trackingList != null && trackingList.SUCCESS.equals(trackingList.getCode())
                && trackingList.getData() != null) {
            List<TrackingRespDTO> trackingRespDTOList = trackingList.getData();
            for(TrackingRespDTO trackingRespDTO : trackingRespDTOList){
                if(trackingRespDTO.getCallTime() == null ){
                    trackingRespDTO.setCallTime(new Date());
                }
            }
            request.setAttribute("trackingList", trackingList.getData());
        } else {
            request.setAttribute("trackingList", new ArrayList());
        }

        // 获取资源流转数据
        CirculationReqDTO circDto = new CirculationReqDTO();
        circDto.setClueId(new Long(clueId));
        JSONResult<List<CirculationRespDTO>> circulationList =
                circulationFeignClient.queryList(circDto);
        if (circulationList != null && circulationList.SUCCESS.equals(circulationList.getCode())
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
        if (clueFileList != null && clueFileList.SUCCESS.equals(clueFileList.getCode())
                && clueFileList.getData() != null) {
            request.setAttribute("clueFileList", clueFileList.getData());
        }
        request.setAttribute("loginUserId", user.getId());
        return "clue/addCustomerMaintenance";
    }

    /**
     * 客户详情
     * 
     * @param request
     * @param clueId
     * @return
     */
    @RequestMapping("/customerInfoReadOnly")
    public String customerInfoReadOnly(HttpServletRequest request, @RequestParam String clueId,
            @RequestParam(required = false) String commonPool) {
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
                } else if (roleCode.equals(RoleCodeEnum.DXCYGW.name())) {
                    accountList.add(user.getId());
                }
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
        JSONResult<List<TrackingRespDTO>> trackingList = trackingFeignClient.queryList(dto);
        if (trackingList != null && trackingList.SUCCESS.equals(trackingList.getCode())
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
        logger.error("流转记录返回值：" + circulationList);
        if (circulationList != null && circulationList.SUCCESS.equals(circulationList.getCode())
                && circulationList.getData() != null) {
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
        if (clueFileList != null && clueFileList.SUCCESS.equals(clueFileList.getCode())
                && clueFileList.getData() != null) {
            request.setAttribute("clueFileList", clueFileList.getData());
        }
        request.setAttribute("commonPool", commonPool);
        request.setAttribute("loginUserId", user.getId());
        return "clue/CustomerMaintenanceReadOnly";
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
     * @param request
     * @return
     */
    @RequestMapping("/uploadClueFile")
    @ResponseBody
    public JSONResult<String> uploadClueFile(HttpServletRequest request,
            @RequestBody ClueFileDTO dto) {
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
        JSONResult<Integer> result = clueBasicFeignClient.getIsInviteLetterById(Long.valueOf(clueId));
        if (result.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("isInviteLetter", result.getData());
        }
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
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
        if(user.getBusinessLine() != null){
            dto.setBusinessLine(user.getBusinessLine());
        }
        if (null != dto.getRepeatUserId() && null != dto.getApplyUserId()) {
            IdListLongReq idListLongReq = new IdListLongReq();
            List<Long> idList = new ArrayList<>();
            idList.add(dto.getRepeatUserId());
            idList.add(dto.getApplyUserId());
            idListLongReq.setIdList(idList);
            JSONResult<List<UserInfoDTO>> userInfo = userInfoFeignClient.listById(idListLongReq);
            if (userInfo != null && userInfo.SUCCESS.equals(userInfo.getCode())
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
        if(user.getBusinessLine() != null ){
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
            if(dto.getBusinessLine() != null){
                dto.setBusinessLine(user.getBusinessLine());
            }
        }
        if (null != dto.getRepeatUserId()) {
            IdEntityLong id = new IdEntityLong();
            id.setId(dto.getRepeatUserId());
            JSONResult<UserInfoDTO> userInfo = userInfoFeignClient.get(id);
            if (userInfo != null && userInfo.SUCCESS.equals(userInfo.getCode())
                    && userInfo.getData() != null) {
                dto.setRepeatOrgId(userInfo.getData().getId());
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
                if(user.getBusinessLine() != null){
                    basic.setBusinessLine(user.getBusinessLine());
                }
                dto.setClueBasic(basic);
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
    public JSONResult<String> updateCustomerClue(HttpServletRequest request,
            @RequestBody ClueDTO dto) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setUpdateUser(user.getId());
            dto.setOrg(user.getOrgId());
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
}
