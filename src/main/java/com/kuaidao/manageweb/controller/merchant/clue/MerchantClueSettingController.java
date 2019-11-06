package com.kuaidao.manageweb.controller.merchant.clue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.MerchantClueApplyFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.merchant.dto.clue.*;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;


/**
 * 资源申请审批
 */
@Controller
@RequestMapping("/merchant/clue/setting")
public class MerchantClueSettingController {

    @Autowired
    private MerchantClueApplyFeignClient merchantClueApplyFeignClient;
    @Autowired
    private SysRegionFeignClient sysRegionFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;

    /***
     * 资源需求申请跳转页面
     *
     * @return
     */
    @RequestMapping("/applyIndex")
    @RequiresPermissions("merchant:clueApply:view")
    public String applyIndex(HttpServletRequest request) {
        // 获取当前登录人
        UserInfoDTO user = getUser();
        request.setAttribute("user", user);
        // 查询所有省
        JSONResult<List<SysRegionDTO>> getProviceList = sysRegionFeignClient.getproviceList();
        if (getProviceList.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("provinceList", getProviceList.getData());
        }
        return "merchant/clueSetting/clueApply";
    }

    /***
     * 资源需求申请跳转页面
     *
     * @return
     */
    @RequestMapping("/applyPageInit")
    public String applyPageInit(HttpServletRequest request) {
        // 获取当前登录人
        UserInfoDTO user = getUser();
        request.setAttribute("user", user);
        // 获取商家主账号列表
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        // 商家主账户
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        // 启用和锁定
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        userInfoDTO.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        if (merchantUserList.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("merchantUserList", merchantUserList.getData());
        }
        return "merchant/resourceApplyCheck/resourceApplyCheck";
    }

    /***
     * 资源需求保存
     *
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("merchant:clueApply:add")
    public JSONResult<Boolean> save(@Valid @RequestBody ClueApplyReqDto reqDTO, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        //防止审核通过之后再编辑将上一条审核信息带过来
        reqDTO.setAuditPerson(null);
        reqDTO.setAuditStatus(null);
        reqDTO.setAuditTime(null);
        // 获取当前登录人
        UserInfoDTO user = getUser();
        reqDTO.setApplyPerson(user.getId());
        reqDTO.setApplyPersonName(user.getName());
        reqDTO.setApplyTime(new Date());
        return merchantClueApplyFeignClient.save(reqDTO);
    }

    /***
     * 资源需求申请列表
     *
     * @return
     */
    @PostMapping("/applyPage")
    @ResponseBody
    public JSONResult<PageBean<ClueApplyPageDto>> applyPage(@RequestBody ClueApplyPageParamDto reqDto) {
        return merchantClueApplyFeignClient.applyPage(reqDto);
    }

    /***
     * 获取最新申请数据
     *
     * @return
     */
    @PostMapping("/getByUserId")
    @ResponseBody
    public JSONResult<MerchantClueApplyDto> getByUserId(HttpServletRequest request) {
        IdEntityLong reqDto = new IdEntityLong();
        // 获取当前登录人
        UserInfoDTO user = getUser();
        reqDto.setId(user.getId());
        return merchantClueApplyFeignClient.getByUserId(reqDto);
    }

    /***
     * 资源需求申请列表-待审核数
     *
     * @param reqDto
     * @return
     */
    @ResponseBody
    @PostMapping("/getPendingReview")
    public JSONResult<Integer> getPendingReview(@RequestBody ClueApplyPageParamDto reqDto) {
        return merchantClueApplyFeignClient.getPendingReview(reqDto);
    }

    /***
     * 资源需求申请列表-审核通过
     *
     * @param reqDto
     * @return
     */
    @PostMapping("/pass")
    @ResponseBody
    @LogRecord(description = "审核通过", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.AUDIT_PASS)
    public JSONResult<Boolean> pass(@RequestBody ClueApplyAuditReqDto reqDto) {
        // 获取当前登录人
        UserInfoDTO user = getUser();
        // 审核人
        reqDto.setAuditPerson(user.getId());
        // 审核时间
        reqDto.setAuditTime(new Date());
        return merchantClueApplyFeignClient.pass(reqDto);
    }

    /***
     * 资源需求申请列表-审核驳回
     *
     * @param reqDto
     * @return
     */
    @PostMapping("/reject")
    @ResponseBody
    @LogRecord(description = "审核驳回", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.AUDIT_REJECT)
    public JSONResult<Boolean> reject(@RequestBody ClueApplyAuditReqDto reqDto) {
        return merchantClueApplyFeignClient.reject(reqDto);
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
}
