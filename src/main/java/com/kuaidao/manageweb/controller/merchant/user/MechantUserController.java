/**
 * 
 */
package com.kuaidao.manageweb.controller.merchant.user;

import com.kuaidao.aggregation.dto.changeorg.ChangeOrgRecordReqDto;
import com.kuaidao.aggregation.dto.clue.ClueRelateReq;
import com.kuaidao.aggregation.dto.clue.CustomerClueQueryDTO;
import com.kuaidao.common.constant.*;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.MD5Util;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.entity.UpdatePasswordSettingReq;
import com.kuaidao.manageweb.feign.changeorg.ChangeOrgFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueRelateFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MechantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.constant.UserErrorCodeEnum;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gpc
 *
 */

@Controller
@RequestMapping("/mechant/userManager")
public class MechantUserController {
    private static Logger logger = LoggerFactory.getLogger(MechantUserController.class);
    @Autowired
    private RoleManagerFeignClient roleManagerFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MyCustomerFeignClient myCustomerFeignClient;
    @Autowired
    private ClueRelateFeignClient clueRelateFeignClient;
    @Autowired
    private ChangeOrgFeignClient changeOrgFeignClient;
    @Autowired
    private MechantUserInfoFeignClient mechantUserInfoFeignClient;

    /***
     * 用户列表页
     *
     * @return
     */
    @RequestMapping("/initUserList")
    public String initUserList(HttpServletRequest request) {
        getSysSetting("mechantRole");
        //查询商家端配置的角色
        String roleIds = getSysSetting(SysConstant.MECHANTROLE);
        if(StringUtils.isNotBlank(roleIds)){
            List<String> list = Arrays.asList(roleIds.split(","));
            IdListReq idListReq = new IdListReq();
            idListReq.setIdList(list);
            JSONResult<List<RoleInfoDTO>> roleInfoDTOs = roleManagerFeignClient.qeuryRoleListByRoleIds(idListReq);
            request.setAttribute("roleList", roleInfoDTOs.getData());
        }
        // 查询组织机构树
        JSONResult<List<TreeData>> treeJsonRes = organizationFeignClient.query();
        // 查询组织机构树
        if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode()) && treeJsonRes.getData() != null) {
            request.setAttribute("orgData", treeJsonRes.getData());
        } else {
            logger.error("query organization tree,res{{}}", treeJsonRes);
        }
        return "mechant/user/userManagePage";
    }
    /***
     * 用户列表
     *
     * @return
     */
    @PostMapping("/merchantlist")
    @ResponseBody
    public JSONResult<PageBean<UserInfoDTO>> merchantlist(@RequestBody UserInfoPageParam userInfoPageParam, HttpServletRequest request,
                                                          HttpServletResponse response) {
        JSONResult<PageBean<UserInfoDTO>> list = mechantUserInfoFeignClient.merchantlist(userInfoPageParam);
        return list;
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

    /**
     * 保存用户
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/saveUser")
    @ResponseBody
    @RequiresPermissions("sys:userManager:add")
    @LogRecord(description = "新增用户", operationType = OperationType.INSERT, menuName = MenuEnum.USER_MANAGEMENT)
    public JSONResult saveUser(@Valid @RequestBody UserInfoReq userInfoReq, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return mechantUserInfoFeignClient.create(userInfoReq);
    }
    /**
     * 查询用户根据id
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/getMechantUserById")
    @ResponseBody
    public JSONResult<UserInfoReq> getMechantUserById(@RequestBody UserInfoReq userInfoReq) {
        return mechantUserInfoFeignClient.getMechantUserById(userInfoReq);
    }

    /**
     * 保存用户
     *
     * @param
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/updateUser")
    @ResponseBody
    @RequiresPermissions("sys:userManager:add")
    @LogRecord(description = "新增用户", operationType = OperationType.INSERT, menuName = MenuEnum.USER_MANAGEMENT)
    public JSONResult updateUser(@Valid @RequestBody UserInfoReq userInfoReq, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return mechantUserInfoFeignClient.updateUser(userInfoReq);
    }
}
