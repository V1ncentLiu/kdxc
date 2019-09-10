package com.kuaidao.manageweb.controller.merchant.clue;

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

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.MerchantClueApplyFeignClient;
import com.kuaidao.merchant.dto.clue.ClueApplyAuditReqDto;
import com.kuaidao.merchant.dto.clue.ClueApplyPageDto;
import com.kuaidao.merchant.dto.clue.ClueApplyPageParamDto;
import com.kuaidao.merchant.dto.clue.ClueApplyReqDto;
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
        // 获取当前登录人
        UserInfoDTO user = getUser();
        reqDTO.setApplyPerson(user.getId());
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
     * 资源需求申请列表-待审核数
     *
     * @param reqDto
     * @return
     */
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
    @LogRecord(description = "审核通过", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.AUDIT_PASS)
    public JSONResult<Boolean> pass(@RequestBody ClueApplyAuditReqDto reqDto) {
        return merchantClueApplyFeignClient.pass(reqDto);
    }

    /***
     * 资源需求申请列表-审核驳回
     *
     * @param reqDto
     * @return
     */
    @PostMapping("/reject")
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
