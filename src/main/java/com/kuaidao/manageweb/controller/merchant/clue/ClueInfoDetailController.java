package com.kuaidao.manageweb.controller.merchant.clue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import com.kuaidao.aggregation.dto.clue.ClueCustomerDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.merchant.dto.clue.ClueFileDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.SortUtils;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.merchant.clue.ClueInfoDetailFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.merchant.dto.clue.ClueDTO;
import com.kuaidao.merchant.dto.clue.ClueQueryDTO;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 维护资源信息
 * 
 * @author fanjd
 */
@Slf4j
@Controller
@RequestMapping("/clueInfo")
public class ClueInfoDetailController {
    @Value("${oss.url.directUpload}")
    private String ossUrl;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private ClueInfoDetailFeignClient clueInfoDetailFeignClient;

    @Autowired
    private CallRecordFeign callRecordFeign;


    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    /** 不可签约 **/
    private static final Integer IS_NOT_SIGN_NO = -1;

    /**
     * 进入详情页面
     * 跟进记录主账号看自己和所有的子账号的 子账号看自己和主账号的
     * 通话记录主账号看自己和所有的子账号的 子账号自能看自己的
     *
     * @param request
     * @return
     */
    @RequestMapping("/init/{clueId}")
    public String init(HttpServletRequest request, @PathVariable Long clueId) {
        log.info("ClueInfoDetailController_init_clueId {}", clueId);
        UserInfoDTO user = getUser();
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            List<ProjectInfoDTO> result = SortUtils.sortList(proJson.getData(), "projectName");
            request.setAttribute("proSelect", result);
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }
        request.setAttribute("loginUserId", user.getId());
        request.setAttribute("clueId", clueId);
        request.setAttribute("ossUrl", ossUrl);

        List<Long> userList = new ArrayList<>();
        // 通话记录用户集合
        List<Long> callUserList = new ArrayList<>();
        // 商家主账户能看商家子账号所有的记录
        if (SysConstant.USER_TYPE_TWO.equals(user.getUserType())) {
            getSubAccountIds(userList, user.getId());
            callUserList.addAll(userList);
        }
        // 商家子账号看自己和主账号的记录
        if (SysConstant.USER_TYPE_THREE.equals(user.getUserType())) {
            userList.add(user.getParentId());
            userList.add(user.getId());
            callUserList.add(user.getId());
        }
        ClueQueryDTO queryDTO = new ClueQueryDTO();
        queryDTO.setClueId(clueId);
        // 获取已上传的文件数据
        ClueQueryDTO fileDto = new ClueQueryDTO();
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(clueId + "");
        if (CollectionUtils.isNotEmpty(userList)) {
            fileDto.setIdList(userList);
        }
        if (CollectionUtils.isNotEmpty(callUserList)) {
            call.setAccountIdList(callUserList);
        }
        JSONResult<List<CallRecordRespDTO>> callRecord = callRecordFeign.listTmCallReacordByParamsNoPage(call);
        // 资源通话记录
        if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode()) && callRecord.getData() != null) {
            request.setAttribute("callRecord", callRecord.getData());
        } else {
            request.setAttribute("callRecord", new ArrayList());
        }
        queryDTO.setIdList(userList);
        JSONResult<ClueDTO> clueInfo = clueInfoDetailFeignClient.findClueInfo(queryDTO);
        if (clueInfo != null && JSONResult.SUCCESS.equals(clueInfo.getCode())) {
            // 客户基本信息
            if (null != clueInfo.getData().getClueCustomer()) {
                ClueCustomerDTO clueCustomerDTO = clueInfo.getData().getClueCustomer();
                clueCustomerDTO.setPhoneCreateTime(null);
                clueCustomerDTO.setPhone2CreateTime(null);
                clueCustomerDTO.setPhone3CreateTime(null);
                clueCustomerDTO.setPhone4CreateTime(null);
                clueCustomerDTO.setPhone5CreateTime(null);
                request.setAttribute("customer", clueCustomerDTO);
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            // 基本信息
            if (null != clueInfo.getData().getClueBasic()) {
                request.setAttribute("base", clueInfo.getData().getClueBasic());
            } else {
                request.setAttribute("base", new ArrayList());
            }
            // 意向信息
            if (null != clueInfo.getData().getClueIntention()) {
                request.setAttribute("intention", clueInfo.getData().getClueIntention());
            } else {
                request.setAttribute("intention", new ArrayList());
            }
            // 跟进记录
            // 意向信息
            if (CollectionUtils.isNotEmpty(clueInfo.getData().getTrackList())) {
                request.setAttribute("trackingList", clueInfo.getData().getTrackList());
            } else {
                request.setAttribute("trackingList", new ArrayList());
            }

        }
        fileDto.setClueId(clueId);
        // 上传文件
        JSONResult<List<ClueFileDTO>> clueFileList = clueInfoDetailFeignClient.findClueFile(fileDto);
        if (clueFileList != null && JSONResult.SUCCESS.equals(clueFileList.getCode()) && clueFileList.getData() != null) {
            request.setAttribute("clueFileList", clueFileList.getData());
        }

        return "merchant/resourceManagement/resourceManagementInfo";
    }

    /**
     * 邀约来访页面跳转
     *
     * @param request
     * @return
     */
    @RequestMapping("/inviteCustomer")
    public String inviteCustomer(HttpServletRequest request, @RequestParam String clueId, @RequestParam String projectId,
            @RequestParam String cusName, @RequestParam String cusPhone, Model model) {
        UserInfoDTO userInfoDTO = getUser();
        request.setAttribute("clueId", clueId);
        request.setAttribute("cusName", cusName);
        request.setAttribute("cusPhone", cusPhone);
        request.setAttribute("projectId", projectId);
        // 查询可签约的项目(过滤掉项目属性中是否不可签约（是）的项目，否的都是可以选择的) change by fanjd 20190826
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(IS_NOT_SIGN_NO);
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            model.addAttribute("proSelect", proJson.getData());
        }
        // 查询用户集合（邀约使用）
        UserInfoDTO userInfoInvite = new UserInfoDTO();
        if (SysConstant.USER_TYPE_TWO.equals(userInfoDTO.getUserType())) {
            userInfoInvite = buildQueryReqDto(SysConstant.USER_TYPE_THREE, userInfoDTO.getId());
        } else if (SysConstant.USER_TYPE_THREE.equals(userInfoDTO.getUserType())) {
            userInfoInvite = buildQueryReqDto(SysConstant.USER_TYPE_THREE, userInfoDTO.getParentId());
        }
        JSONResult<List<UserInfoDTO>> merchantAppiontUserList = merchantUserInfoFeignClient.merchantUserList(userInfoInvite);
        if (merchantAppiontUserList.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("merchantAppiontUserList", merchantAppiontUserList.getData());
        }
        return "merchant/resourceManagement/resourceInvite";
    }

    /**
     * 进入详情页面 跟进记录主账号看自己和所有的子账号的 子账号看自己和主账号的 通话记录主账号看自己和所有的子账号的 子账号自能看自己的
     *
     * @param idEntityLong
     * @return
     */
    @ResponseBody
    @PostMapping("/detail")
    public JSONResult<ClueDTO> detail(@RequestBody IdEntityLong idEntityLong) {
        Long clueId = idEntityLong.getId();
        log.info("ClueInfoDetailController.customerEditInfo_clueId {{}}", clueId);
        UserInfoDTO user = getUser();
        List<Long> userList = new ArrayList<>();
        // 通话记录用户集合
        List<Long> callUserList = new ArrayList<>();
        // 商家主账户能看商家子账号所有的记录
        if (SysConstant.USER_TYPE_TWO.equals(user.getUserType())) {
            getSubAccountIds(userList, user.getId());
            callUserList.addAll(userList);
        }
        // 跟进记录商家子账号看自己和主账号的记录
        // 通话记录子账号只看自己的
        if (SysConstant.USER_TYPE_THREE.equals(user.getUserType())) {
            userList.add(user.getId());
            userList.add(user.getParentId());
            callUserList.add(user.getId());
        }
        ClueQueryDTO queryDTO = new ClueQueryDTO();
        queryDTO.setClueId(clueId);
        // 获取已上传的文件数据
        ClueQueryDTO fileDto = new ClueQueryDTO();
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(clueId + "");
        if (CollectionUtils.isNotEmpty(userList)) {
            fileDto.setIdList(userList);
        }
        if (CollectionUtils.isNotEmpty(callUserList)) {
            call.setAccountIdList(callUserList);
        }
        JSONResult<ClueDTO> clueInfo = clueInfoDetailFeignClient.findClueInfo(queryDTO);
        if (clueInfo != null && JSONResult.SUCCESS.equals(clueInfo.getCode()) && clueInfo.getData() != null) {
            JSONResult<List<CallRecordRespDTO>> callRecord = callRecordFeign.listTmCallReacordByParamsNoPage(call);
            // 资源通话记录
            if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode()) && callRecord.getData() != null) {
                clueInfo.getData().setCallRecorList(callRecord.getData());
            }
        }
        return clueInfo;
    }

    /**
     * 维护资源提交
     *
     * @param dto
     * @return
     */
    @RequestMapping("/updateCustomerClue")
    @ResponseBody
    @LogRecord(description = "维护客户资源提交", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.CUSTOMER_INFO)
    public JSONResult<String> updateCustomerClue(@RequestBody ClueDTO dto) {

        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user) {
            dto.setUpdateUser(user.getId());
            dto.setOrg(user.getOrgId());
            if (dto.getClueCustomer().getPhoneCreateTime() != null && StringUtils.isNotBlank(dto.getClueCustomer().getPhone())) {
                dto.getClueCustomer().setPhoneCreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone2CreateTime() != null && StringUtils.isNotBlank(dto.getClueCustomer().getPhone2())) {
                dto.getClueCustomer().setPhone2CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone3CreateTime() != null && StringUtils.isNotBlank(dto.getClueCustomer().getPhone3())) {
                dto.getClueCustomer().setPhone3CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone4CreateTime() != null && StringUtils.isNotBlank(dto.getClueCustomer().getPhone4())) {
                dto.getClueCustomer().setPhone4CreateUser(user.getId());
            }
            if (dto.getClueCustomer().getPhone5CreateTime() != null && StringUtils.isNotBlank(dto.getClueCustomer().getPhone5())) {
                dto.getClueCustomer().setPhone5CreateUser(user.getId());
            }
        }
        dto.setUserList(getUserList());
        return clueInfoDetailFeignClient.updateCustomerClue(dto);
    }

    /**
     * 获取最后一次拨打时间
     * 
     * @author: Fanjd
     * @param idEntityLong clueId 线索id
     * @return: com.kuaidao.common.entity.JSONResult<java.lang.String>
     * @Date: 2019/10/14 20:05
     * @since: 1.0.0
     **/
    @ResponseBody
    @PostMapping("/getLastCallTime")
    public JSONResult<String> getLastCallTime(@RequestBody IdEntityLong idEntityLong) {
        UserInfoDTO user = getUser();
        List<Long> userList = new ArrayList<>();
        // 商家主账户能看商家子账号所有的记录
        if (SysConstant.USER_TYPE_TWO.equals(user.getUserType())) {
            getSubAccountIds(userList, user.getId());
        }
        // 商家子账号看自己和主账号的记录
        if (SysConstant.USER_TYPE_THREE.equals(user.getUserType())) {
            userList.add(user.getId());
            userList.add(user.getParentId());
        }
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(String.valueOf(idEntityLong.getId()));
        if (CollectionUtils.isNotEmpty(userList)) {
            call.setAccountIdList(userList);
        }
        // 当前时间
        String now = DateUtil.getCurrentDate(DateUtil.ymdhms);
        JSONResult<List<CallRecordRespDTO>> callRecord = callRecordFeign.listTmCallReacordByParamsNoPage(call);
        // 资源通话记录
        if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode()) && callRecord.getData() != null) {
            List<CallRecordRespDTO> list = callRecord.getData();
            // 按照拨打时间倒序
            Comparator<CallRecordRespDTO> comparator = Comparator.nullsLast(Comparator.comparing(CallRecordRespDTO::getStartTime));
            list.sort(comparator.reversed());
            CallRecordRespDTO callRecordRespDTO = list.get(0);
            now = callRecordRespDTO.getStartTime();
            if (StringUtils.isNotBlank(now)) {
                now = DateUtil.timeStamp2Str(now, DateUtil.ymdhms);
            }
        }
        return new JSONResult<String>().success(now);
    }

    /**
     * 获取登录人集合 如果登录人为主账号 则集合为所有子账号和主账号本身 如果登录人为子账号 则集合为主账号和子账号本身
     *
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/10/18 14:46
     * @since: 1.0.0
     **/
    private List<Long> getUserList() {
        UserInfoDTO user = getUser();
        List<Long> userList = new ArrayList<>();
        // 商家主账户能看商家子账号所有的记录
        if (SysConstant.USER_TYPE_TWO.equals(user.getUserType())) {
            getSubAccountIds(userList, user.getId());
        }
        // 商家子账号看自己和主账号的记录
        if (SysConstant.USER_TYPE_THREE.equals(user.getUserType())) {
            userList.add(user.getId());
            userList.add(user.getParentId());
        }
        return userList;

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
     * 获取商家主账户下的子账号
     * 
     * @author: Fanjd
     * @param subIds 用户集 合userId 用户id
     * @return: void
     * @Date: 2019/10/10 20:30
     * @since: 1.0.0
     **/
    private void getSubAccountIds(List<Long> subIds, Long userId) {
        subIds.add(userId);
        // 获取商家主账号下的子账号列表
        UserInfoDTO userReqDto = buildQueryReqDto(SysConstant.USER_TYPE_THREE, userId);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userReqDto);
        if (merchantUserList.getCode().equals(JSONResult.SUCCESS)) {
            if (CollectionUtils.isNotEmpty(merchantUserList.getData())) {
                // 获取子账号id放入子账号集合中
                subIds.addAll(merchantUserList.getData().stream().map(UserInfoDTO::getId).collect(Collectors.toList()));
            }
        }

    }

    /**
     * 构建商家子账户查询实体
     * 
     * @author: Fanjd
     * @param userType 用户类型
     * @param id 用户id
     * @return: com.kuaidao.sys.dto.user.UserInfoDTO
     * @Date: 2019/10/10 20:28
     * @since: 1.0.0
     **/
    private UserInfoDTO buildQueryReqDto(Integer userType, Long id) {

        // 获取商家主账号下的子账号列表
        UserInfoDTO userReqDto = new UserInfoDTO();
        // 商家主账户
        userReqDto.setUserType(userType);
        // 启用和锁定
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        userReqDto.setStatusList(statusList);
        // 商家主账号id
        userReqDto.setParentId(id);
        return userReqDto;
    }
}
