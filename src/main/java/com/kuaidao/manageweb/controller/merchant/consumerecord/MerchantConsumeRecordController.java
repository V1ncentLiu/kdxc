/**
 *
 */
package com.kuaidao.manageweb.controller.merchant.consumerecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.kuaidao.account.dto.consume.CountConsumeRecordDTO;
import com.kuaidao.account.dto.consume.MerchantConsumeRecordDTO;
import com.kuaidao.account.dto.consume.MerchantConsumeRecordPageParam;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.consumerecord.MerchantConsumeRecordFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/merchant/merchantConsumeRecord")
public class MerchantConsumeRecordController {
    private static Logger logger = LoggerFactory.getLogger(MerchantConsumeRecordController.class);
    @Autowired
    private MerchantConsumeRecordFeignClient merchantConsumeRecordFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    @Autowired
    private TelemarketingLayoutFeignClient telemarketingLayoutFeignClient;

    /***
     * 消费记录列表（商家端）
     * 消费记录列表页 外部商家-商家账号：当前登录商家主账号加子账号 内部商家-商家账户：电销布局里绑定的电销组
     *
     * @return
     */
    @RequestMapping("/initRecordList")
    @RequiresPermissions("merchant:merchantConsumeRecord:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 构建商家账户集合
        List<UserInfoDTO> userList = buildUserList();
        // 当前人员id
        request.setAttribute("userId", user.getId() + "");
        // 当前人员角色code
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.size() != 0) {
            request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        }

        userList.add(user);
        request.setAttribute("userList", userList);

        return "merchant/merchantConsumeRecord/merchantConsumeRecord";
    }



    /***
     * 消费记录列表（商家端）
     *
     * @return
     */
    @PostMapping("/countListMerchant")
    @ResponseBody
    @RequiresPermissions("merchant:merchantConsumeRecord:view")
    public JSONResult<PageBean<CountConsumeRecordDTO>> countListMerchant(@RequestBody MerchantConsumeRecordPageParam pageParam,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setMainAccountId(user.getId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        List<UserInfoDTO> userInfoDTOS = buildUserList();
        userInfoDTOS.add(user);
        if (CollectionUtils.isNotEmpty(userInfoDTOS)) {
            // 构建用户id集合
            List<Long> userIds = userInfoDTOS.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
            pageParam.setUserList(userIds);
        }
        // 商家所属
        pageParam.setMerchantType(user.getMerchantType());
        // 消费记录
        JSONResult<PageBean<CountConsumeRecordDTO>> countListMerchant = merchantConsumeRecordFeignClient.countListMerchant(pageParam);

        return countListMerchant;
    }

    /***
     * 消费明细列表页
     *
     * @return
     */
    @RequestMapping("/initInfoList")
    @RequiresPermissions("merchant:merchantConsumeRecord:view")
    public String initInfoList(HttpServletRequest request, @RequestParam("createDate") String createDate) {
        UserInfoDTO user = getUser();
        // 当前人员id
        request.setAttribute("userId", user.getId() + "");
        if (StringUtils.isNotBlank(createDate)) {
            // 消费日期
            request.setAttribute("createDate", createDate);
        }
        // 消费日期
        request.setAttribute("merchantType", user.getMerchantType());
        // 当前人员角色code
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.size() != 0) {
            request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        }
        // 商家账号(当前登录商家主账号加子账号)
        List<UserInfoDTO> userList = buildUserList();
        userList.add(user);
        request.setAttribute("userList", userList);

        return "merchant/merchantConsumeRecord/merchantConsumeRecordInfo";
    }



    /***
     * 消费明细列表
     *
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("merchant:merchantConsumeRecord:view")
    public JSONResult<PageBean<MerchantConsumeRecordDTO>> list(@RequestBody MerchantConsumeRecordPageParam pageParam) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setMainAccountId(user.getId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        // 消费记录
        JSONResult<PageBean<MerchantConsumeRecordDTO>> list = merchantConsumeRecordFeignClient.list(pageParam);

        return list;
    }

    /**
     * 获取当前登录账号
     *
     * @param
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }


    /**
     * 查询商家账号
     *
     * @param
     * @return
     */
    private List<UserInfoDTO> getMerchantUser(Long parentId, List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
        userInfoDTO.setParentId(parentId);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }

    /**
     * * 外部商家-商家账号：当前登录商家主账号加子账号 * 内部商家-商家账户：电销布局里绑定的电销组
     * 
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/10/23 10:55
     * @since: 1.0.0
     **/
    private List<UserInfoDTO> buildUserList() {
        UserInfoDTO user = getUser();
        List<UserInfoDTO> userList = new ArrayList<>();
        // 商家所属
        Integer merchantType = user.getMerchantType();
        // 内部商家
        if (SysConstant.MerchantType.TYPE1 == merchantType) {
            TelemarketingLayoutDTO reqDto = new TelemarketingLayoutDTO();
            reqDto.setCompanyGroupId(user.getId());
            JSONResult<List<OrganizationDTO>> result = telemarketingLayoutFeignClient.getdxListByCompanyGroupId(reqDto);
            List<OrganizationDTO> orgList = result.getData();
            if (result.getCode().equals(JSONResult.SUCCESS) && CollectionUtils.isNotEmpty(result.getData())) {
                for (OrganizationDTO organizationDTO : orgList) {
                    UserInfoDTO userInfoDTO = new UserInfoDTO();
                    BeanUtils.copyProperties(organizationDTO,userInfoDTO);
                    userList.add(userInfoDTO);
                }

            }
        }
        // 外部商家
        if (SysConstant.MerchantType.TYPE2 == merchantType) {
            userList.add(user);
            // 状态集合
            List<Integer> status = new ArrayList<>();
            // 启用
            status.add(SysConstant.USER_STATUS_ENABLE);
            // 锁定
            status.add(SysConstant.USER_STATUS_LOCK);
            userList = getMerchantUser(user.getId(), status);
        }
        return userList;
    }
}
