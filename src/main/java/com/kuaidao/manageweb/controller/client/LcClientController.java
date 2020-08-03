package com.kuaidao.manageweb.controller.client;

import com.kuaidao.aggregation.dto.client.QimoDataRespDTO;
import com.kuaidao.aggregation.dto.client.QueryQimoDTO;
import com.kuaidao.callcenter.dto.LcClient.AddOrUpdateLcClientDTO;
import com.kuaidao.callcenter.dto.QimoOutboundCallDTO;
import com.kuaidao.callcenter.dto.QimoOutboundCallRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.client.LcClientFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/client/lcClient")
public class LcClientController {

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private LcClientFeignClient lcClientFeignClient;

    /**
     *  跳转乐创页面
     */
//    @RequiresPermissions("aggregation:lcClient:view")
    @RequestMapping("/toLcClientPage")
    public String qimoClientIndex(HttpServletRequest request) {
        List<OrganizationDTO> orgList = new ArrayList<>();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(String.valueOf(curLoginUser.getOrgId()));
            if(curOrgGroupByOrgId!=null) {
                orgList.add(curOrgGroupByOrgId);
            }
            request.setAttribute("orgList", orgList);
        } else {
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            JSONResult<List<OrganizationRespDTO>> orgListJr = organizationFeignClient.queryOrgByParam(queryDTO);
            if (orgListJr == null || !JSONResult.SUCCESS.equals(orgListJr.getCode())) {
                log.error("跳转乐创坐席时，查询组织机构列表报错,res{{}}", orgListJr);
            } else {
                request.setAttribute("orgList", orgListJr.getData());
            }
        }
        return "client/lcClientPage";
    }


//    @RequiresPermissions("aggregation:lcClient:view")
    @PostMapping("/listLcClientPage")
    @ResponseBody
    public JSONResult<PageBean<QimoDataRespDTO>> listLcClientPage(
            @RequestBody QueryQimoDTO queryClientDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long orgId = curLoginUser.getOrgId();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //电销总监查他自己的组
            queryClientDTO.setOrgId(orgId);
        }
//        return clientFeignClient.listQimoClientPage(queryClientDTO);
        return null;
    }


//    @RequiresPermissions("aggregation:lcClient:add")
    @PostMapping("/saveLcClient")
    @ResponseBody
    public JSONResult<Boolean> saveLcClient(@Valid @RequestBody AddOrUpdateLcClientDTO reqDTO,
                                              BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setCreateUser(curLoginUser.getId());
        Long userId = reqDTO.getUserId();
        if(null != userId){
            JSONResult jsonResult = assignmentOrg(reqDTO, userId);
            if(!JSONResult.SUCCESS.equals(jsonResult.getCode())){
                return jsonResult;
            }
        }
        JSONResult<Boolean> booleanJSONResult = lcClientFeignClient.saveLcClient(reqDTO);
        return booleanJSONResult;
    }


//    @RequiresPermissions("aggregation:lcClient:edit")
    @PostMapping("/updateLcClient")
    @ResponseBody
    public JSONResult<Boolean> updateLcClient(@Valid @RequestBody AddOrUpdateLcClientDTO reqDTO,
                                                BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        Long id = reqDTO.getId();
        if (id == null) {
            return new JSONResult<Boolean>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        Long userId = reqDTO.getUserId();
        if(null != userId){
            JSONResult jsonResult = assignmentOrg(reqDTO, userId);
            if(null != result){
                return jsonResult;
            }
        }
        return lcClientFeignClient.updateLcClient(reqDTO);
    }

    /**
     * 七陌 外呼
     */
    @PostMapping("/lcOutboundCall")
    @ResponseBody
    public JSONResult<QimoOutboundCallRespDTO> lcOutboundCall(@RequestBody QimoOutboundCallDTO  callDTO){

        
        return null;
    }

    /**
     * 获取当前 orgId所在的组织
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(String.valueOf(orgId));
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            log.error("getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }

    /**
     * 赋值org
     */
    private JSONResult assignmentOrg(AddOrUpdateLcClientDTO reqDTO,Long userId){
        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(userId);
        JSONResult<UserInfoDTO> userInfoJr = userInfoFeignClient.get(idEntityLong);
        String code = userInfoJr.getCode();
        if(!JSONResult.SUCCESS.equals(code)){
            return new JSONResult<Boolean>().fail(code,userInfoJr.getMsg());
        }
        UserInfoDTO userInfo = userInfoJr.getData();
        reqDTO.setOrgId(userInfo.getOrgId());
        return new JSONResult().success(reqDTO);
    }
}
