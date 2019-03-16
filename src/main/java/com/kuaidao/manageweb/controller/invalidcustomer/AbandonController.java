package com.kuaidao.manageweb.controller.invalidcustomer;

import com.kuaidao.aggregation.dto.invalidcustomer.*;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.InvalidCustomer.AbandonFeignClient;
import com.kuaidao.manageweb.feign.InvalidCustomer.InvalidCustomerFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author  yangbiao
 * @Date: 2019/2/11 15:13
 * @Description:
 *      废弃池
 */
@Controller
@RequestMapping("/abandonsource")
public class AbandonController {


    private static Logger logger = LoggerFactory.getLogger(AbandonController.class);

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    AbandonFeignClient abandonFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<AbandonRespDTO>> queryListPage(@RequestBody AbandonParamDTO dto){

        Date date1 =dto.getCreateTime1();
        Date date2 =dto.getCreateTime2();
        if(date1!=null && date2!=null ){
            if(date1.getTime()>date2.getTime()){
                return new JSONResult().fail("-1","创建时间，结束时间不能早于开始时间!");
            }
        }

        return abandonFeignClient.queryListPage(dto);
    }

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request){
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if(JSONResult.SUCCESS.equals(proJson.getCode())){
            request.setAttribute("proSelect", proJson.getData());
        }
        request.setAttribute("zyzyList",queryUserByRole());
        return "invalidcustomer/abandonList";
    }

    /**
     * 查询所有资源专员
     * @return
     */

    private List<UserInfoDTO> queryUserByRole() {

        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();

        UserOrgRoleReq userRole = new UserOrgRoleReq();
        userRole.setRoleCode(RoleCodeEnum.TGZXZJ.name());
        JSONResult<List<UserInfoDTO>> userZxzjList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userZxzjList.getCode()) && null != userZxzjList.getData()
                && userZxzjList.getData().size() > 0) {
            userList.addAll(userZxzjList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.YHZG.name());
        JSONResult<List<UserInfoDTO>> userYhZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userYhZgList.getCode()) && null != userYhZgList.getData()
                && userYhZgList.getData().size() > 0) {
            userList.addAll(userYhZgList.getData());
        }
        userRole.setRoleCode(RoleCodeEnum.TGYHWY.name());
        JSONResult<List<UserInfoDTO>> userYhWyList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userYhWyList.getCode()) && null != userYhWyList.getData()
                && userYhWyList.getData().size() > 0) {
            userList.addAll(userYhWyList.getData());
        }
        userRole.setRoleCode(RoleCodeEnum.TGKF.name());
        JSONResult<List<UserInfoDTO>> userKfList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKfList.getCode()) && null != userKfList.getData()
                && userKfList.getData().size() > 0) {
            userList.addAll(userKfList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.KFZG.name());
        JSONResult<List<UserInfoDTO>> userKfZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKfZgList.getCode()) && null != userKfZgList.getData()
                && userKfZgList.getData().size() > 0) {
            userList.addAll(userKfZgList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.TGNQWY.name());
        JSONResult<List<UserInfoDTO>> userKNqWyList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKNqWyList.getCode()) && null != userKNqWyList.getData()
                && userKNqWyList.getData().size() > 0) {
            userList.addAll(userKNqWyList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.NQZG.name());
        JSONResult<List<UserInfoDTO>> userNqZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userNqZgList.getCode()) && null != userNqZgList.getData()
                && userNqZgList.getData().size() > 0) {
            userList.addAll(userNqZgList.getData());
        }

        return userList;

    }
}
