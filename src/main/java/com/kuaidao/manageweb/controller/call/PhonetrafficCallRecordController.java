package com.kuaidao.manageweb.controller.call;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.kuaidao.aggregation.dto.call.CallRecordCountDTO;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.call.QueryPhoneLocaleDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.manageweb.config.BusinessCallrecordLimit;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trafficCall/callRecord")
public class PhonetrafficCallRecordController {

    private static Logger logger = LoggerFactory.getLogger(PhonetrafficCallRecordController.class);

    @Autowired
    CallRecordFeign callRecordFeign;


    /**
     * 我的通话记录
     *
     * @return
     */
    @RequiresPermissions("aggregation:trafficMyCallRecord:view")
    @RequestMapping("/myCallRecord")
    public String myCallRecord(HttpServletRequest request) {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        List<RoleInfoDTO> roleList = user.getRoleList();
        request.setAttribute("userId", user.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", user.getOrgId().toString());
        return "phonetraffic/myCallRecord";
    }

    /**
     * 获取我的通话记录 分页展示 ，参数模糊匹配
     *
     * @return
     */
    @RequiresPermissions("aggregation:trafficMyCallRecord:view")
    @PostMapping("/listMyCallRecord")
    @ResponseBody
    public JSONResult<Map<String, Object>> listMyCallRecord(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getId();
        List<Long> accountIdList = new ArrayList<Long>();
        accountIdList.add(id);
        myCallRecordReqDTO.setAccountIdList(accountIdList);
        return callRecordFeign.listMyCallRecord(myCallRecordReqDTO);
    }

    /**
     * 我的通话记录 统计总时长
     *
     * @param myCallRecordReqDTO
     * @return
     */
    @PostMapping("/countMyCallRecordTalkTime")

    @ResponseBody
    public JSONResult<Integer> countMyCallRecordTalkTime(
            @RequestBody CallRecordReqDTO myCallRecordReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getId();
        List<Long> accountIdList = new ArrayList<Long>();
        accountIdList.add(id);
        myCallRecordReqDTO.setAccountIdList(accountIdList);
        return callRecordFeign.countMyCallRecordTalkTime(myCallRecordReqDTO);
    }


    /**
     *  获取天润通话记录地址 根据 记录Id
     * @param idEntity
     * @return
     */
    @PostMapping("/getRecordFile")
    @ResponseBody
    public JSONResult<String> getRecordFile(@RequestBody IdEntity idEntity) {
        return callRecordFeign.getRecordFile(idEntity);
    }

}
