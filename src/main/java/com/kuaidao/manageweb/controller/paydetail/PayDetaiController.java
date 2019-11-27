package com.kuaidao.manageweb.controller.paydetail;

import javax.validation.Valid;
import com.kuaidao.aggregation.constant.PayDetailConstant;
import com.kuaidao.aggregation.dto.sign.BusSignInsertOrUpdateDTO;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.paydetail.PayDetailInsertOrUpdateDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.paydetail.PayDetailFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.sign.BusinessSignFeignClient;
import com.kuaidao.manageweb.feign.visitrecord.BusVisitRecordFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author yangbiao
 * @Date: 2019/1/2 15:14
 * @Description: 付款明细
 */

@Controller
@RequestMapping("/payDetail")
public class PayDetaiController {

    private static Logger logger = LoggerFactory.getLogger(PayDetaiController.class);
    @Autowired
    InviteareaFeignClient inviteareaFeignClient;
    @Autowired
    BusinessSignFeignClient businessSignFeignClient;
    @Autowired
    SysRegionFeignClient sysRegionFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private BusVisitRecordFeignClient visitRecordFeignClient;
    @Autowired
    PayDetailFeignClient payDetailFeignClient;


    /**
     * 新增
     */
    @RequestMapping("/insert")
    @ResponseBody
    public JSONResult<Boolean> saveVisitRecord(@Valid @RequestBody PayDetailInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateUser(user.getId());
        if ("4".equals(dto.getPayType())) { // 尾款
            dto.setMakeUpTime(null);
            dto.setAmountBalance(null);
        }
        return payDetailFeignClient.savePayDedail(dto);
    }

    /**
     * 更新签约单和付款明细
     */
    @ResponseBody
    @RequestMapping("/payOrSignUpdate")
    @LogRecord(operationType = LogRecord.OperationType.UPDATE, description = "编辑付款明细",
            menuName = MenuEnum.PAYANDSIGN_CHANGE)
    public JSONResult<Boolean> payOrSignUpdate(@RequestBody BusSignInsertOrUpdateDTO dto) throws Exception {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setUpdateUser(user.getId());
        // 全款
        if (PayDetailConstant.PayType.FULL_PAYMENT.getCode().equals(dto.getSignType())) {
            dto.setMakeUpTime(null);
            dto.setAmountBalance(null);
        }
        if (user.getBusinessLine() != null) {
            dto.setBusinessLine(user.getBusinessLine());
        }
        return payDetailFeignClient.payOrSignUpdate(dto);
    }


}
