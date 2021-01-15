package com.kuaidao.manageweb.controller.autodistribution;

import com.kuaidao.businessconfig.dto.automodel.AutoDisModelDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.telepreference.TelePreferenceSetDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.autodismodel.AutoDisModelFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DicItemWapper;
import com.kuaidao.manageweb.feign.organization.OrganitionWapper;
import com.kuaidao.manageweb.feign.preference.PreferenceFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectWapper;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/preference")
public class PreferenceController {


    @Autowired
    AutoDisModelFeignClient autoDisModelFeignClient;


    @Autowired
    PreferenceFeignClient preferenceFeignClient;


    @Autowired
    OrganitionWapper organitionWapper;

    @Autowired
    DicItemWapper dicItemWapper;

    @Autowired
    ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    ProjectWapper projectWapper;



    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {

        // 副总能看本事业部的数据，总经理看本中心数据，管理员查看全系统的
        /**
         * 电销组
         */
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = curLoginUser.getRoleList().get(0).getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        if(RoleCodeEnum.GLY.name().equals(roleCode)){
            // 查看全部电销组
            request.setAttribute("dxzList", organitionWapper.findAllDXZ());
        }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
            // 查看本事业部的
            request.setAttribute("dxzList",organitionWapper.findDxzListByParentId(orgId));
        }else if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
            request.setAttribute("dxzList",organitionWapper.findDxzListByParentId(orgId));
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode)){
            // 查看本组
            request.setAttribute("dxzList",Arrays.asList(organitionWapper.findOrgById(orgId)));
        }else{
            // 电销顾问
            request.setAttribute("dxzList",Arrays.asList(organitionWapper.findOrgById(orgId)));
        }

        List<ProjectInfoDTO> projectList = projectWapper.allProject();
        request.setAttribute("projectList",projectWapper.signProject(projectList));
        //  项目品类
        request.setAttribute("projectCategory",dicItemWapper.findDicItemsByCode(DicCodeEnum.PROJECTCATEGORY.getCode()));

        // 获取数据字典：媒介
        request.setAttribute("mediumList",dicItemWapper.findDicItemsByCode(DicCodeEnum.MEDIUM.getCode()));;
        return "assignrule/telemarketingPreferences";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value = "/update")
    JSONResult<Boolean> update(@RequestBody TelePreferenceSetDTO dto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setUpdateUser(curLoginUser.getId());
        return preferenceFeignClient.update(dto);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value = "/updateInfo")
    JSONResult<Boolean> updateInfo(@RequestBody TelePreferenceSetDTO dto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setUpdateUser(curLoginUser.getId());
        return preferenceFeignClient.update(dto);
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value = "/queryByParams")
    JSONResult<PageBean<TelePreferenceSetDTO>> queryByParams(@RequestBody TelePreferenceSetDTO dto){

        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = curLoginUser.getRoleList().get(0).getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        dto.setOrgId(orgId);
        dto.setRoleCode(roleCode);

        return preferenceFeignClient.queryByParams(dto);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value = "/updateBusyStatus")
    JSONResult<Boolean> updateBusyStatus(@RequestBody TelePreferenceSetDTO  dto){
        return preferenceFeignClient.updateBusyStatus(dto);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value = "/queryBusyStatus")
    public JSONResult<String> queryBusyStatus(){

        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        log.info("curLoginUser::{}",curLoginUser);
        String roleCode = curLoginUser.getRoleList().get(0).getRoleCode();
        /**
         *  顾问角色电销顾问
         */
        if(!RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            return new JSONResult().fail("-1","非电销顾问没有忙碌状态");
        }
        Integer businessLine = curLoginUser.getBusinessLine();
        AutoDisModelDTO autoDisModel = new AutoDisModelDTO();
        autoDisModel.setBussinessLine(businessLine);
        JSONResult<AutoDisModelDTO> autoResult = autoDisModelFeignClient.queryByParams(autoDisModel);

        if(autoResult.data()==null){
            return new JSONResult().fail("-2","当前业务线下没有设置自动分配规则");
        }

        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(curLoginUser.getId());
        JSONResult<TelePreferenceSetDTO> result = preferenceFeignClient.queryBusyStatus(idEntityLong);

        String status = "0";
        if(result.data()!=null){
            TelePreferenceSetDTO data = result.data();
            if(data.getBustStatus()!=null){
                status = data.getBustStatus().toString();
            }
         }
        return new JSONResult().success(status);
    }


}