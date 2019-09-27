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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.account.dto.consume.ConsumeRecordNumDTO;
import com.kuaidao.account.dto.consume.CountConsumeRecordDTO;
import com.kuaidao.account.dto.consume.MerchantConsumeRecordDTO;
import com.kuaidao.account.dto.consume.MerchantConsumeRecordPageParam;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.consumerecord.MerchantConsumeRecordFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/merchant/consumeRecord")
public class ConsumeRecordController {
    private static Logger logger = LoggerFactory.getLogger(MerchantConsumeRecordController.class);
    @Autowired
    private MerchantConsumeRecordFeignClient merchantConsumeRecordFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;

    /***
     * 消费记录列表页(管理端)
     *
     * @return
     */
    @RequestMapping("/initRecordList")
    @RequiresPermissions("merchant:consumeRecord:view")
    public String initCompanyList(HttpServletRequest request) {

        // 商家账号(当前登录商家主账号加子账号)
        List<UserInfoDTO> userList = getMerchantUser(null);
        request.setAttribute("userList", userList);

        return "merchant/consumeRecord/consumeRecord";
    }

    /***
     * 消费记录列表(管理段)
     *
     * @return
     */
    @PostMapping("/countList")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:view")
    public JSONResult<PageBean<CountConsumeRecordDTO>> countList(
            @RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
        // 消费记录
        JSONResult<PageBean<CountConsumeRecordDTO>> countList =
                merchantConsumeRecordFeignClient.countList(pageParam);

        return countList;
    }

    /***
     * 查询今日、昨日消费统计（管理端）
     *
     * @return
     */
    @PostMapping("/countNum")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:view")
    public JSONResult<ConsumeRecordNumDTO> countNum(
            @RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
        // 消费记录
        JSONResult<ConsumeRecordNumDTO> countNum =
                merchantConsumeRecordFeignClient.countNum(pageParam);

        return countNum;
    }

    /***
     * 消费记录列表页(管理端)
     *
     * @return
     */
    @RequestMapping("/initSingleMerchant")
    @RequiresPermissions("merchant:consumeRecord:view")
    public String initSingleMerchant(@RequestParam Long mainAccountId, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        userList.add(user);
        // 商家账号(当前登录商家主账号加子账号)
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_THREE);
        userInfoDTO.setParentId(user.getId());
        JSONResult<List<UserInfoDTO>> merchantUserList =
                merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        userList.addAll(merchantUserList.getData());
        request.setAttribute("userList", userList);
        request.setAttribute("mainAccountId", mainAccountId);

        return "merchant/consumeRecord/consumeRecord";
    }

    /***
     * 单个商家消费记录(管理段)
     *
     * @return
     */
    @PostMapping("/countListMerchant")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:view")
    public JSONResult<PageBean<CountConsumeRecordDTO>> countListMerchant(
            @RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
        // 消费记录
        JSONResult<PageBean<CountConsumeRecordDTO>> countListMerchant =
                merchantConsumeRecordFeignClient.countListMerchant(pageParam);

        return countListMerchant;
    }

    /***
     * 消费明细列表页(管理段)
     *
     * @return
     */
    @RequestMapping("/initInfoList")
    @RequiresPermissions("merchant:consumeRecord:view")
    public String initInfoList(HttpServletRequest request) {
        // 商家账号(当前登录商家主账号加子账号)
        List<UserInfoDTO> userList = getMerchantUser(null);

        request.setAttribute("userList", userList);

        return "merchant/consumeRecord/merchantConsumeRecordInfo";
    }


    /***
     * 消费明细列表(管理段)
     *
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("merchant:consumeRecord:view")
    public JSONResult<PageBean<MerchantConsumeRecordDTO>> list(
            @RequestBody MerchantConsumeRecordPageParam pageParam, HttpServletRequest request) {
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
    private List<UserInfoDTO> getMerchantUser(List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList =
                merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }
}
