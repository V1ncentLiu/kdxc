package com.kuaidao.manageweb.controller.financing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kuaidao.aggregation.dto.financing.FinanceLayoutDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.telemarkting.TelemarketingLayoutDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.financing.FinanceLayoutFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.telemarketing.TelemarketingLayoutFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description: 财务电销布局管理
 */

@Controller
@RequestMapping("/financing/financelayout")
public class FinanceLayoutController {

    private static Logger logger = LoggerFactory.getLogger(FinanceLayoutController.class);

    @Autowired
    private FinanceLayoutFeignClient financeLayoutFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /**
     * 财务布局列表
     * 
     * @return
     */
    @RequestMapping("/financeLayoutPage")
    public String financeLayoutList(HttpServletRequest request) {

        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> swList =
                organizationFeignClient.queryOrgByParam(orgDto);
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setRoleCode(RoleCodeEnum.CYCW.name());
     // 商务小组
        JSONResult<List<UserInfoDTO>> cwList =
        		userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        request.setAttribute("swList", swList.getData());
        request.setAttribute("cwList", cwList.getData());
        return "financing/financeLayoutPage";
    }

    /**
     *  财务布局列表
     * 
     * @return
     */
    @RequestMapping("/getFinanceLayoutList")
    @ResponseBody
    public JSONResult<PageBean<FinanceLayoutDTO>> getFinanceLayoutList(
            HttpServletRequest request,
            @RequestBody FinanceLayoutDTO financeLayoutDTO) {
        return financeLayoutFeignClient.getFinanceLayoutList(financeLayoutDTO);
    }

    /**
     * 根据id查询 财务布局
     * 
     * @return
     */
    @RequestMapping("/findFinanceLayoutById")
    @ResponseBody
    public JSONResult<FinanceLayoutDTO> findFinanceLayoutById(HttpServletRequest request,
            @RequestBody FinanceLayoutDTO financeLayoutDTO) {
        return financeLayoutFeignClient.findFinanceLayoutById(financeLayoutDTO);
    }

    /**
     * 添加 财务布局
     * 
     * @return
     */
    @RequestMapping("/addFinanceLayout")
    @LogRecord(description = "添加财务布局", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.FINANCELAYOUT)
    @ResponseBody
    public JSONResult addFinanceLayout(
            @RequestBody FinanceLayoutDTO financeLayoutDTO) {
        UserInfoDTO user =
                (UserInfoDTO) SecurityUtils.getSubject().getSession().getAttribute("user");
        financeLayoutDTO.setCreateUser(user.getId());
        return financeLayoutFeignClient
                .addOrUpdateFinanceLayout(financeLayoutDTO);
    }

    /**
     * 修改 财务布局
     * 
     * @return
     */
    @RequestMapping("/updateFinanceLayout")
    @LogRecord(description = "修改财务布局", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.FINANCELAYOUT)
    @ResponseBody
    public JSONResult updateFinanceLayout(
            @RequestBody FinanceLayoutDTO financeLayoutDTO) {
        return financeLayoutFeignClient
                .addOrUpdateFinanceLayout(financeLayoutDTO);
    }

    /**
     * 删除 财务布局
     * 
     * @return
     */
    @RequestMapping("/deleFinanceLayout")
    @LogRecord(description = "删除财务布局", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.FINANCELAYOUT)
    @ResponseBody
    public JSONResult deleFinanceLayout(
            @RequestBody FinanceLayoutDTO financeLayoutDTO) {
        return financeLayoutFeignClient.deleFinanceLayout(financeLayoutDTO);
    }

 
}
