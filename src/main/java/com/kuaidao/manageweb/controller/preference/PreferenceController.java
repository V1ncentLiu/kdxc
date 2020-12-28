package com.kuaidao.manageweb.controller.preference;

import com.kuaidao.aggregation.dto.automodel.AutoDisModelDTO;
import com.kuaidao.aggregation.dto.telepreference.TelePreferenceSetDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.autodismodel.AutoDisModelFeignClient;
import com.kuaidao.manageweb.feign.preference.PreferenceFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/preference")
public class PreferenceController {


    @Autowired
    AutoDisModelFeignClient autoDisModelFeignClient;


    @Autowired
    PreferenceFeignClient preferenceFeignClient;

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request) {
        return "assignrule/telemarketingPreferences";
    }


    @RequestMapping(method = RequestMethod.POST,value = "/update")
    JSONResult<Boolean> update(@RequestBody TelePreferenceSetDTO dto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setUpdateUser(curLoginUser.getId());
        return preferenceFeignClient.update(dto);
    }


    @RequestMapping(method = RequestMethod.POST,value = "/updateInfo")
    JSONResult<Boolean> updateInfo(@RequestBody TelePreferenceSetDTO dto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setUpdateUser(curLoginUser.getId());
        return preferenceFeignClient.update(dto);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/queryByParams")
    JSONResult<PageBean<TelePreferenceSetDTO>> queryByParams(@RequestBody TelePreferenceSetDTO dto){
        return preferenceFeignClient.queryByParams(dto);
    }


    @RequestMapping(method = RequestMethod.POST,value = "/updateBusyStatus")
    JSONResult<Boolean> updateBusyStatus(@RequestBody TelePreferenceSetDTO  dto){
        return preferenceFeignClient.updateBusyStatus(dto);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/queryBusyStatus")
    public JSONResult<Integer> queryBusyStatus(){

        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        log.info("curLoginUser::{}",curLoginUser);
        String roleCode = curLoginUser.getRoleCode();
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

        Integer status = 0;
        if(result.data()!=null){
            TelePreferenceSetDTO data = result.data();
            status = data.getBustStatus();
        }
        return new JSONResult().success(status);
    }


}