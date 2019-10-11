package com.kuaidao.manageweb.controller.merchant.clue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.clue.ClueCustomerDTO;
import com.kuaidao.aggregation.dto.clue.ClueFileDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.SortUtils;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.circulation.CirculationFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.RepeatClueRecordFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.ClueInfoDetailFeignClient;
import com.kuaidao.manageweb.feign.merchant.tracking.TrackingMerchantFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.merchant.dto.clue.ClueDTO;
import com.kuaidao.merchant.dto.tracking.TrackingReqDTO;
import com.kuaidao.merchant.dto.tracking.TrackingRespDTO;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
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



    /**
     * 进入详情页面
     *
     * @param request
     * @param clueId
     * @return
     */
    @RequestMapping("/init")
    public String detail(HttpServletRequest request, @RequestParam String clueId) {
        log.info("ClueInfoDetailController.customerEditInfo_clueId {{}}", clueId);
        UserInfoDTO user = getUser();
        List<Long> userList = new ArrayList<>();
        // 商家主账户能看商家子账号所有的记录
        if (SysConstant.USER_TYPE_TWO.equals(user.getUserType())) {
            getSubAccountIds(userList, user.getId());
        }
        // 商家子账号看自己的记录
        if (SysConstant.USER_TYPE_THREE.equals(user.getUserType())) {
            userList.add(user.getId());
        }
        // 获取资源跟进记录数据
        TrackingReqDTO dto = new TrackingReqDTO();
        // 获取已上传的文件数据
        ClueQueryDTO fileDto = new ClueQueryDTO();
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(clueId);
        if (CollectionUtils.isNotEmpty(userList)) {
            call.setAccountIdList(userList);
            fileDto.setIdList(userList);
        }
        JSONResult<List<CallRecordRespDTO>> callRecord = callRecordFeign.listTmCallReacordByParamsNoPage(call);
        // 资源通话记录
        if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode()) && callRecord.getData() != null) {
            request.setAttribute("callRecord", callRecord.getData());
        }
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            List<ProjectInfoDTO> result = SortUtils.sortList(proJson.getData(), "projectName");
            request.setAttribute("proSelect", result);
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }
        request.setAttribute("loginUserId", user.getId());
        return "";
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
