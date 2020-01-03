package com.kuaidao.manageweb.controller.merchant.clue;

import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.SortUtils;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.merchant.clue.MerchantAppiontmentFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.merchant.dto.clue.ClueAssignReqDto;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;
import com.kuaidao.merchant.dto.clue.MerchantAppiontmentDTO;
import com.kuaidao.merchant.dto.clue.MerchantAppiontmentReq;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on: 2019-10-03-20:13
 */
@Slf4j
@Controller
@RequestMapping("/merchant/merchantAppiontment")
public class MerchantAppiontmentController {

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private MerchantAppiontmentFeignClient merchantAppiontmentFeignClient;

    /**
     * 商家邀约来访记录页面
     *
     * @param
     * @return
     */
    @RequestMapping("/init")
    @RequiresPermissions("merchant:merchantAppiontment:view")
    public String init(HttpServletRequest request) {
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            List<ProjectInfoDTO> result = SortUtils.sortList(proJson.getData(), "projectName");
            request.setAttribute("proSelect", result);
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }
        return "merchant/inviteRecord/inviteRecord";
    }

    /**
     * 查询邀约来访记录
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/queryPage")
    //@RequiresPermissions("merchant:merchantAppiontment:view")
    public JSONResult<PageBean<MerchantAppiontmentDTO>> queryPage(@RequestBody MerchantAppiontmentReq req) {
        UserInfoDTO userInfoDTO = getUser();
        req.setUserId(userInfoDTO.getId());
        List<RoleInfoDTO> roleList = userInfoDTO.getRoleList();
        if (roleList != null) {
            req.setRoleCode(roleList.get(0).getRoleCode());
        }
        return merchantAppiontmentFeignClient.queryPage(req);
    }

    /**
     * 新增邀约来访记录
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/insertMerchantAppiontment")
    public JSONResult<Boolean> insertMerchantAppiontment(@RequestBody MerchantAppiontmentDTO dto) {
        UserInfoDTO userInfoDTO = getUser();
        dto.setCreateUserId(userInfoDTO.getId());
        //存储主账号id
        if(SysConstant.USER_TYPE_TWO.equals(userInfoDTO.getUserType())) {
            dto.setPrimaryAccountId(userInfoDTO.getId());
        } else if(SysConstant.USER_TYPE_THREE.equals(userInfoDTO.getUserType())){
            dto.setPrimaryAccountId(userInfoDTO.getParentId());
        }
        //验证项目字段第一个字符是否是“,”
        if(StringUtils.isNotBlank(dto.getProjectId())) {
            String project = dto.getProjectId();
            String head = project.substring(0, 1);
            if (",".equals(head)){
                dto.setProjectId(project.substring(1));
            }
        }
        return merchantAppiontmentFeignClient.saveMerchantAppiontment(dto);
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
