package com.kuaidao.manageweb.controller.merchant.callRecord;

import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.common.constant.BusinessLineConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.controller.call.CallRecordController;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on: 2019-10-11-11:02
 */
@Controller
@RequestMapping("/merchant/merchantCallRecord")
public class MerchantCallRecordController {

    private static Logger logger = LoggerFactory.getLogger(MerchantCallRecordController.class);

    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    @Autowired
    private CallRecordFeign callRecordFeign;

    /**
     * 跳转通话记录页
     */
    @RequiresPermissions("merchant:callRecord:view")
    @RequestMapping("/init")
    public String init(HttpServletRequest request) {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        List<RoleInfoDTO> roleList = user.getRoleList();
        Integer userType = user.getUserType();
        //查询用户集合（邀约使用）
        UserInfoDTO userInfo = new UserInfoDTO();
        List<UserInfoDTO> list = new ArrayList<>();
        if (SysConstant.USER_TYPE_TWO.equals(user.getUserType())) {
            userInfo = buildQueryReqDto(SysConstant.USER_TYPE_THREE, user.getId());
            JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient
                .merchantUserList(userInfo);
            if (merchantUserList.getCode().equals(JSONResult.SUCCESS)) {
                list.addAll(merchantUserList.getData());
                list.add(user);
                request.setAttribute("merchantUserList", list);
            }
        } else if (SysConstant.USER_TYPE_THREE.equals(user.getUserType())) {
            list.add(user);
            request.setAttribute("merchantUserList", list);
        } else {

        }
        request.setAttribute("user", user);
        request.setAttribute("userId", user.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", user.getOrgId().toString());
        if (null != userType) {
            request.setAttribute("userType", userType.toString());
        }
        return "merchant/bussinessCall/callRecord";
    }

    /**
     * 商家通话记录 分页展示 ，参数模糊匹配
     */
    @RequiresPermissions("merchant:callRecord:view")
    @PostMapping("/listMerchantCallRecord")
    @ResponseBody
    public JSONResult<Map<String, Object>> listMerchantCallRecord(
        @RequestBody CallRecordReqDTO callRecordReqDTO) {
        // 根据角色查询
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Integer userType = curLoginUser.getUserType();
        if (null == userType) {
            return new JSONResult<Map<String, Object>>().success(null);
        }
        // 商家子账号
        List<Long> accountIdList = callRecordReqDTO.getAccountIdList();
        UserInfoDTO userInfo = new UserInfoDTO();
        if (CollectionUtils.isEmpty(accountIdList)) {
            if (SysConstant.USER_TYPE_THREE.equals(userType)) {
                List<Long> idList = new ArrayList<>();
                idList.add(curLoginUser.getId());
                callRecordReqDTO.setAccountIdList(idList);
            } else if (SysConstant.USER_TYPE_TWO.equals(userType)) {//商家主账号
                userInfo = buildQueryReqDto(SysConstant.USER_TYPE_THREE, curLoginUser.getId());
                JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient
                    .merchantUserList(userInfo);
                if (merchantUserList.getCode().equals(JSONResult.SUCCESS) && CollectionUtils
                    .isNotEmpty(merchantUserList.getData())) {
                    List<Long> userIdList = merchantUserList.getData().parallelStream()
                        .map(UserInfoDTO::getId).collect(Collectors.toList());
                    userIdList.add(curLoginUser.getId());
                    callRecordReqDTO.setAccountIdList(userIdList);
                }
            }
        }

        return callRecordFeign.listAllTmCallRecord(callRecordReqDTO);

    }

    /**
     * 构建商家子账户查询实体
     */
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
