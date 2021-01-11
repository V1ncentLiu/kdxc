/**
 *
 */
package com.kuaidao.manageweb.controller.rule;

import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitDTO;
import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitPageParam;
import com.kuaidao.businessconfig.dto.clueassign.ClueAssignLimitReq;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.rule.ClueAssignLimitFegin;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserDataAuthReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
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
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/clueAssign/clueAssignLimit")
public class ClueAssignLimitController {
    private static Logger logger = LoggerFactory.getLogger(ClueAssignLimitController.class);
    @Autowired
    private ClueAssignLimitFegin clueAssignLimitFegin;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    /***
     * 资源分配量列表页
     *
     * @return
     */
    @RequestMapping("/initListPage")
    @RequiresPermissions("clueAssignLimit:clueAssignLimitManager:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        List<UserDataAuthReq> userDataAuthList = user.getUserDataAuthList();
        if (userDataAuthList != null && userDataAuthList.size() > 0) {
            List<Integer> businessLineList = userDataAuthList.stream().map(a -> a.getBusinessLine())
                    .collect(Collectors.toList());
            OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
            orgDto.setOrgType(OrgTypeConstant.DXZ);
            orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
            orgDto.setBusinessLineList(businessLineList);
            // 电销小组
            JSONResult<List<OrganizationRespDTO>> dzList =
                    organizationFeignClient.queryOrgByParam(orgDto);
            List<OrganizationRespDTO> data = dzList.getData();
            request.setAttribute("teleGroupList", data);
        }
        return "rule/clueAssignLimitManagerPage";
    }


    /***
     * 编辑资源分配量页
     *
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    @RequiresPermissions("clueAssignLimit:clueAssignLimitManager:view")
    public JSONResult<ClueAssignLimitDTO> get(@RequestBody IdEntityLong idEntityLong) {
        JSONResult<ClueAssignLimitDTO> jsonResult = clueAssignLimitFegin.get(idEntityLong);
        return jsonResult;
    }

    /**
     * 删除资源分配量设置
     * @param idListLongReq
     * @return
     */
    @ResponseBody
    @PostMapping("/delete")
    @RequiresPermissions("clueAssignLimit:clueAssignLimitManager:delete")
    public JSONResult<String> delete(@RequestBody IdListLongReq idListLongReq) {
        return clueAssignLimitFegin.delete(idListLongReq);
    }
    /***
     * 资源分配量列表
     *
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("clueAssignLimit:clueAssignLimitManager:view")
    public JSONResult<PageBean<ClueAssignLimitDTO>> list(
            @RequestBody ClueAssignLimitPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 获取当前账号能查看的业务线权限
        JSONResult<PageBean<ClueAssignLimitDTO>> list =
                new JSONResult<PageBean<ClueAssignLimitDTO>>().success(null);
        List<UserDataAuthReq> userDataAuthList = user.getUserDataAuthList();
        if (userDataAuthList != null && userDataAuthList.size() > 0) {
            List<Integer> businessLineList = userDataAuthList.stream().map(a -> a.getBusinessLine())
                    .collect(Collectors.toList());
            pageParam.setBusinessLineList(businessLineList);

            list = clueAssignLimitFegin.list(pageParam);
        }
        return list;
    }


    /**
     * 保存资源分配量
     *
     * @param orgDTO
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("clueAssignLimit:clueAssignLimitManager:add")
    @LogRecord(description = "新增资源分配量", operationType = OperationType.INSERT,
            menuName = MenuEnum.ASSIGN_LIMIT_MANAGEMENT)
    public JSONResult<Long> save(@Valid @RequestBody ClueAssignLimitReq clueAssignLimitReq) {
        // 插入创建人信息
        UserInfoDTO user = getUser();
        clueAssignLimitReq.setCreateUser(user.getId());
        clueAssignLimitReq.setUpdateUser(user.getId());
        return clueAssignLimitFegin.create(clueAssignLimitReq);
    }

    /**
     * 修改资源分配量
     *
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("clueAssignLimit:clueAssignLimitManager:edit")
    @LogRecord(description = "修改资源分配量信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.ASSIGN_LIMIT_MANAGEMENT)
    public JSONResult<Long> update(@Valid @RequestBody ClueAssignLimitReq clueAssignLimitReq) {
        // 插入修改人信息
        UserInfoDTO user = getUser();
        clueAssignLimitReq.setUpdateUser(user.getId());
        Long id = clueAssignLimitReq.getId();
        if (id == null) {
            return new JSONResult<Long>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return clueAssignLimitFegin.update(clueAssignLimitReq);
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

}
