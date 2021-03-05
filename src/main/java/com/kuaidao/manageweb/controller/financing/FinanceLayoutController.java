package com.kuaidao.manageweb.controller.financing;

import com.kuaidao.aggregation.dto.financing.FinanceLayoutDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.financing.FinanceLayoutFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private SysRegionFeignClient sysRegionFeignClient;

    /**
     * 财务布局列表
     * 
     * @return
     */
    @RequestMapping("/financeLayoutPage")
    public String financeLayoutList(HttpServletRequest request) {
        UserInfoDTO userInfoDTO = getUser();
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
//        if(userInfoDTO.getBusinessLine() !=null){
//            orgDto.setBusinessLine(userInfoDTO.getBusinessLine());
//        }
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> swList = organizationFeignClient.queryOrgByParam(orgDto);

        // 部门类型为电销组组织
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> dxzList = organizationFeignClient.queryOrgByParam(orgDto);

        if( null == swList.getData()){

            swList.setData(Collections.emptyList());
        }
        if( null == dxzList.getData()){

            dxzList.setData(Collections.emptyList());
        }
        swList.getData().addAll(dxzList.getData());
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("projectList", proJson.getData());
        }
        // 查询所有省
        JSONResult<List<SysRegionDTO>> getProviceList = sysRegionFeignClient.getproviceList();
        if (getProviceList.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("provinceList", getProviceList.getData());
        }
        List<UserInfoDTO> userList = getUserList();
        request.setAttribute("swList", swList.getData() );
        request.setAttribute("userList", userList);
        return "financing/financeLayoutPage";
    }

    /**
     *  财务布局列表
     * 
     * @return
     */
    @RequestMapping("/getFinanceLayoutList")
    @ResponseBody
    public JSONResult<PageBean<FinanceLayoutDTO>> getFinanceLayoutList(HttpServletRequest request, @RequestBody FinanceLayoutDTO financeLayoutDTO) {
        return financeLayoutFeignClient.getFinanceLayoutList(financeLayoutDTO);
    }

    public List<UserInfoDTO> getUserList() {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setRoleCode(RoleCodeEnum.CYCW.name());
        // 商务小组
        JSONResult<List<UserInfoDTO>> cycwList = userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        userOrgRoleReq.setRoleCode(RoleCodeEnum.QDSJCW.name());
        // 商务小组
        JSONResult<List<UserInfoDTO>> qdsjcwList = userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        userOrgRoleReq.setRoleCode(RoleCodeEnum.SJHZCW.name());
        // 商务小组
        JSONResult<List<UserInfoDTO>> shgzcwList = userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        List<UserInfoDTO> userList = new ArrayList<>();
        if (JSONResult.SUCCESS.equals(cycwList.getCode()) && cycwList.getData() != null && cycwList.getData().size() > 0) {
            for (UserInfoDTO userInfoDTO : cycwList.getData()) {
                userList.add(userInfoDTO);
            }
        }
        if (JSONResult.SUCCESS.equals(qdsjcwList.getCode()) && qdsjcwList.getData() != null && qdsjcwList.getData().size() > 0) {
            for (UserInfoDTO userInfoDTO : qdsjcwList.getData()) {
                userList.add(userInfoDTO);
            }
        }
        if (JSONResult.SUCCESS.equals(shgzcwList.getCode()) && shgzcwList.getData() != null && shgzcwList.getData().size() > 0) {
            for (UserInfoDTO userInfoDTO : shgzcwList.getData()) {
                userList.add(userInfoDTO);
            }
        }
        return userList;
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
    @LogRecord(description = "添加财务布局", operationType = LogRecord.OperationType.INSERT, menuName = MenuEnum.FINANCELAYOUT)
    @ResponseBody
    public JSONResult addFinanceLayout(@RequestBody FinanceLayoutDTO financeLayoutDTO) {
        UserInfoDTO user = (UserInfoDTO) SecurityUtils.getSubject().getSession().getAttribute("user");
        financeLayoutDTO.setCreateUser(user.getId());
        return financeLayoutFeignClient.addOrUpdateFinanceLayout(financeLayoutDTO);
    }

    /**
     * 修改 财务布局
     * 
     * @return
     */
    @RequestMapping("/updateFinanceLayout")
    @LogRecord(description = "修改财务布局", operationType = LogRecord.OperationType.UPDATE, menuName = MenuEnum.FINANCELAYOUT)
    @ResponseBody
    public JSONResult updateFinanceLayout(@RequestBody FinanceLayoutDTO financeLayoutDTO) {
        return financeLayoutFeignClient.addOrUpdateFinanceLayout(financeLayoutDTO);
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
