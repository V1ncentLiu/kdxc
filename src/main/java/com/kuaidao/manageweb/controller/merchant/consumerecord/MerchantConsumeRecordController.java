/**
 *
 */
package com.kuaidao.manageweb.controller.merchant.consumerecord;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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

    /***
     * 消费记录列表页
     *
     * @return
     */
    @RequestMapping("/initRecordList")
    @RequiresPermissions("merchant:merchantConsumeRecord:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO user = getUser();

        // 当前人员id
        request.setAttribute("userId", user.getId() + "");
        // 当前人员角色code
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.size() != 0) {
            request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        }
        // 商家账号(当前登录商家主账号加子账号)
        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        userList.add(user);
        userList.addAll(getMerchantUser(user.getId(), null));
        request.setAttribute("userList", userList);

        return "merchant/merchantConsumeRecord/merchantConsumeRecord";
    }



    /***
     * 消费记录列表
     *
     * @return
     */
    @PostMapping("/countListMerchant")
    @ResponseBody
    @RequiresPermissions("merchant:merchantConsumeRecord:view")
    public JSONResult<PageBean<CountConsumeRecordDTO>> countListMerchant(
            @RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setMainAccountId(user.getId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        // 消费记录
        JSONResult<PageBean<CountConsumeRecordDTO>> countListMerchant =
                merchantConsumeRecordFeignClient.countListMerchant(pageParam);

        return countListMerchant;
    }

    /***
     * 消费明细列表页
     *
     * @return
     */
    @RequestMapping("/initInfoList")
    @RequiresPermissions("merchant:merchantConsumeRecord:view")
    public String initInfoList(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 当前人员id
        request.setAttribute("userId", user.getId() + "");
        // 当前人员角色code
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && roleList.size() != 0) {
            request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        }
        // 商家账号(当前登录商家主账号加子账号)
        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        userList.add(user);
        userList.addAll(getMerchantUser(user.getId(), null));
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
    public JSONResult<PageBean<MerchantConsumeRecordDTO>> list(
            @RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setMainAccountId(user.getId());

        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        // 消费记录
        JSONResult<PageBean<MerchantConsumeRecordDTO>> list =
                merchantConsumeRecordFeignClient.list(pageParam);

        return list;
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
     * 查询商家账号
     *
     * @param code
     * @return
     */
    private List<UserInfoDTO> getMerchantUser(Long parentId, List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
        userInfoDTO.setParentId(parentId);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList =
                merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }
}
