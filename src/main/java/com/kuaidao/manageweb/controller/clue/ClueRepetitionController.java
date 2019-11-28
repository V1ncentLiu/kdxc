package com.kuaidao.manageweb.controller.clue;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.kuaidao.common.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kuaidao.aggregation.dto.clue.ClueRepetitionDTO;
import com.kuaidao.aggregation.dto.sign.BusinessSignDTO;
import com.kuaidao.aggregation.dto.sign.PayDetailDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueRepetitionFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.sign.BusinessSignFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 重单管理 Created on 2019-2-18 16:15:59
 */
@Controller
@RequestMapping("/clue/cluerepetition")
public class ClueRepetitionController {
    private static Logger logger = LoggerFactory.getLogger(ClueRepetitionController.class);

    @Autowired
    ClueRepetitionFeignClient clueRepetitionFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    SysRegionFeignClient sysRegionFeignClient;

    @Autowired
    BusinessSignFeignClient businessSignFeignClient;

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    /**
     * 重单列表页面
     * 
     * @return
     */
    @RequestMapping("/queryRepeatPage")
    public String queryRepeatPage(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
     // 添加重单字段限制的业务线
        boolean isShowRepetition = false;
        String repetitionBusinessLine = getSysSetting(SysConstant.REPETITION_BUSINESSLINE);
        if((","+repetitionBusinessLine+",").contains(","+user.getBusinessLine()+",")) {
        	isShowRepetition = true;
        }
        request.setAttribute("isShowRepetition", isShowRepetition);
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("userId", user.getId().toString());
        request.setAttribute("roleCode", roleList.get(0).getRoleCode());
        request.setAttribute("orgId", user.getOrgId().toString());
        return "clue/repetition/customerrePetitionList";
    }

    /**
     * 查询字典表
     *
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }

    /**
     * 重单列表
     * 
     * @return
     */
    @RequestMapping("/queryRepeatList")
    @ResponseBody
    public JSONResult<PageBean<ClueRepetitionDTO>> queryRepeatList(HttpServletRequest request,
            @RequestBody ClueRepetitionDTO clueRepetitionDTO) {
        UserInfoDTO user = getUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        List<Long> idList = new ArrayList<Long>();
        if (roleList != null && RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
            // 如果当前登录的为电销总监,查询所有下属电销员工
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), RoleCodeEnum.DXCYGW.name(),user.getBusinessLine());
            idList = userList.stream().map(c -> c.getId()).collect(Collectors.toList());
        } else if (roleList != null
                && RoleCodeEnum.DXCYGW.name().equals(roleList.get(0).getRoleCode())) {
            idList.add(user.getId());
        } else if (roleList != null
                && RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) {
            OrganizationQueryDTO reqDto = new OrganizationQueryDTO();
            reqDto.setParentId(user.getOrgId());
            JSONResult<List<OrganizationDTO>> jsonResult =
                    organizationFeignClient.listDescenDantByParentId(reqDto);
            if (JSONResult.SUCCESS.equals(jsonResult.getCode())) {
                List<Long> idLists = jsonResult.getData().stream().map(c -> c.getId())
                        .collect(Collectors.toList());
                List<UserInfoDTO> userList = getUserLists(idLists, RoleCodeEnum.DXCYGW.name());
                idList = userList.stream().map(c -> c.getId()).collect(Collectors.toList());
            }
        } else if (roleList != null
                && RoleCodeEnum.DXZJL.name().equals(roleList.get(0).getRoleCode())) {
            OrganizationQueryDTO reqDto = new OrganizationQueryDTO();
            reqDto.setParentId(user.getOrgId());
            JSONResult<List<OrganizationDTO>> jsonResult =
                    organizationFeignClient.listDescenDantByParentId(reqDto);
            if (JSONResult.SUCCESS.equals(jsonResult.getCode())) {
                List<Long> idLists = jsonResult.getData().stream().map(c -> c.getId())
                        .collect(Collectors.toList());
                List<UserInfoDTO> userList = getUserLists(idLists, RoleCodeEnum.DXCYGW.name());
                idList = userList.stream().map(c -> c.getId()).collect(Collectors.toList());
            }
        }
        JSONResult<PageBean<ClueRepetitionDTO>> list = new JSONResult<>();
        if (idList != null && idList.size() > 0) {
            clueRepetitionDTO.setIdList(idList);
            list = clueRepetitionFeignClient.queryRepeatList(clueRepetitionDTO);
        } else {
            list = new JSONResult<PageBean<ClueRepetitionDTO>>()
                    .success(new PageBean<ClueRepetitionDTO>());
        }

        return list;
    }

    /**
     * 根据机构和角色类型获取用户
     * 
     * @param
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode,Integer businessLise) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        statusList.add(3);
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setBusinessLine(businessLise);
        userOrgRoleReq.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 根据机构和角色类型获取用户
     * 
     * @param
     * @return
     */
    private List<UserInfoDTO> getUserLists(List<Long> orgIds, String roleCode) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgIdList(orgIds);
        userOrgRoleReq.setRoleCode(roleCode);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 获取当前登录账号
     * 
     * @param
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 重单详情
     * 
     * @return
     */
    @RequestMapping("/queryRepeatById")
    @ResponseBody
    public JSONResult<ClueRepetitionDTO> queryRepeatById(HttpServletRequest request,
            @RequestBody ClueRepetitionDTO clueRepetitionDTO) {
        return clueRepetitionFeignClient.queryRepeatById(clueRepetitionDTO);
    }

    /**
     * 撤销重单申请
     * 
     * @return
     */
    @RequestMapping("/delRepeatByIds")
    @ResponseBody
    public JSONResult<PageBean<ClueRepetitionDTO>> delRepeatByIds(HttpServletRequest request,
            @RequestBody ClueRepetitionDTO clueRepetitionDTO) {
        JSONResult<PageBean<ClueRepetitionDTO>> list =
                clueRepetitionFeignClient.queryRepeatList(clueRepetitionDTO);
        return list;
    }


    /**
     * 重单处理列表页面
     * 
     * @return
     */
    @RequestMapping("/dealPetitionListPage")
    public String dealPetitionListPage(HttpServletRequest request) {
    	UserInfoDTO user = getUser();
    	List<RoleInfoDTO> roleList = user.getRoleList();
    	// 添加重单字段限制的业务线
        String repetitionBusinessLine = getSysSetting(SysConstant.REPETITION_BUSINESSLINE);
    	boolean isShowRepetition = false;
        if((","+repetitionBusinessLine+",").contains(","+user.getBusinessLine()+",")) {
        	isShowRepetition = true;
        }
        request.setAttribute("isShowRepetition", isShowRepetition);
        if(roleList.get(0).getRoleCode().equals(RoleCodeEnum.PDZG.name())){
            List<UserInfoDTO> userInfoList = getUserList(null,RoleCodeEnum.PDZY.name(),user.getBusinessLine());
            request.setAttribute("userInfoList", userInfoList);
            return "clue/repetition/dealPetitionList";
        }else{
            return "clue/repetition/dealPetitionListByPdzy";
        }


    }

    /**
     * 重单处理列表
     * 
     * @return
     */
    @RequestMapping("/dealPetitionList")
    @ResponseBody
    public JSONResult<PageBean<ClueRepetitionDTO>> dealPetitionList(HttpServletRequest request,
            @RequestBody ClueRepetitionDTO clueRepetitionDTO) {
        UserInfoDTO userInfoDTO = getUser();
        List<RoleInfoDTO> roleList = userInfoDTO.getRoleList();
        if (userInfoDTO.getBusinessLine() != null) {
            clueRepetitionDTO.setBusinessLine(userInfoDTO.getBusinessLine());
        }
        if(roleList.get(0).getRoleCode().equals(RoleCodeEnum.PDZY.name())){
            clueRepetitionDTO.setPdUser(userInfoDTO.getId());
        }
        JSONResult<PageBean<ClueRepetitionDTO>> list =
                clueRepetitionFeignClient.dealPetitionList(clueRepetitionDTO);
        return list;
    }

    /**
     * 处理重单
     * 
     * @return
     */
    @RequestMapping("/dealPetitionById")
    @ResponseBody
    public JSONResult<ClueRepetitionDTO> dealPetitionById(HttpServletRequest request,
            @RequestBody ClueRepetitionDTO clueRepetitionDTO) {
        JSONResult<ClueRepetitionDTO> list =
                clueRepetitionFeignClient.dealPetitionById(clueRepetitionDTO);
        return list;
    }

    /**
     * 重单审核
     * 
     * @return
     */
    @RequestMapping("/updatePetitionById")
    @ResponseBody
    @LogRecord(description = "重单审核", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.REPETITION)
    public JSONResult updatePetitionById(HttpServletRequest request,
            @RequestBody ClueRepetitionDTO clueRepetitionDTO) {
        UserInfoDTO user = getUser();
        clueRepetitionDTO.setLoginUserId(user.getId());
        return clueRepetitionFeignClient.updatePetitionById(clueRepetitionDTO);
    }


    /**
     * 重单处理列表页面
     * 
     * @return
     */
    @RequestMapping("/businessSignDealListPage")
    public String businessSignDealListPage(HttpServletRequest request) {
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> swList =
                organizationFeignClient.queryOrgByParam(orgDto);
        // 获取商务经理
        UserOrgRoleReq userRole = new UserOrgRoleReq();
        userRole.setRoleCode(RoleCodeEnum.SWJL.name());
        JSONResult<List<UserInfoDTO>> jsonResult = userInfoFeignClient.listByOrgAndRole(userRole);
        request.setAttribute("swList", swList.getData());
        request.setAttribute("businessManagerList", jsonResult.getData());
        return "clue/repetition/businessSignDealListPage";
    }

    /**
     * 重单处理列表
     * 
     * @return
     */
    @RequestMapping("/businessSignDealList")
    @ResponseBody
    public JSONResult<PageBean<BusinessSignDTO>> businessSignDealList(HttpServletRequest request,
            @RequestBody BusinessSignDTO businessSignDTO) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (user.getBusinessLine() != null) {
            businessSignDTO.setBusinessLine(user.getBusinessLine());
        }
        if(roleList.get(0).getRoleCode().equals(RoleCodeEnum.PDZY.name())){
            businessSignDTO.setPdUser(user.getId());
        }
        JSONResult<PageBean<BusinessSignDTO>> list =
                businessSignFeignClient.businessSignDealList(businessSignDTO);
        return list;
    }

    /**
     * 重单处理列表页面
     *
     * @return
     */
    @RequestMapping("/repeatPaymentDetails")
    public String repeatPaymentDetails(HttpServletRequest request, @RequestParam String signId) {
        BusinessSignDTO businessSignDTO = new BusinessSignDTO();
        businessSignDTO.setId(Long.parseLong(signId));
        JSONResult<BusinessSignDTO> jsonResult =
                businessSignFeignClient.repeatPaymentDetails(businessSignDTO);
        request.setAttribute("businessSignDetail", jsonResult.getData());
        request.setAttribute("signId", signId);
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        return "clue/repetition/repeatPaymentDetails";
    }

    /**
     * 重单处理列表页面
     *
     * @return
     */
    @RequestMapping("/getPaymentDetailsById")
    @ResponseBody
    public JSONResult<PayDetailDTO> getPaymentDetailsById(HttpServletRequest request,
            @RequestBody PayDetailDTO payDetailDTO) {
        return businessSignFeignClient.getPaymentDetailsById(payDetailDTO);
    }


    @RequestMapping("/updatePayDetailById")
    @ResponseBody
    @LogRecord(description = "付款明细重单比例修改", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.PAYDETAILREPETITION)
    public JSONResult updatePayDetailById(HttpServletRequest request,
            @RequestBody PayDetailDTO payDetailDTO) {
        UserInfoDTO user = getUser();
        payDetailDTO.setLoginUserId(user.getId());
        return clueRepetitionFeignClient.updatePayDetailById(payDetailDTO);
    }


    /**
     * 签约重单审核
     * 
     * @return
     */
    @RequestMapping("/updateBusinessPetitionById")
    @ResponseBody
    @LogRecord(description = "签约重单审核", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.BUSINESSSIGNREPETITION)
    public JSONResult updateBusinessPetitionById(HttpServletRequest request,
            @RequestBody BusinessSignDTO businessSignDTO) {
        UserInfoDTO user = getUser();
        businessSignDTO.setLoginUserId(user.getId());
        return clueRepetitionFeignClient.updateBusinessPetitionById(businessSignDTO);
    }

    /**
     * 根据重单主表id查询重单详细信
     *
     * @return
     */
    @RequestMapping("/getRepeatDetailsByRepeatId")
    @ResponseBody
    public JSONResult<ClueRepetitionDTO> getRepeatDetailsByRepeatId(
            @RequestBody ClueRepetitionDTO clueRepetitionDTO) {
        return clueRepetitionFeignClient.getRepeatDetailsByRepeatId(clueRepetitionDTO);
    }
    /**
     * 查询系统参数
     * 
     * @param code
     * @return
     */
    private String getSysSetting(String code) {
        SysSettingReq sysSettingReq = new SysSettingReq();
        sysSettingReq.setCode(code);
        JSONResult<SysSettingDTO> byCode = sysSettingFeignClient.getByCode(sysSettingReq);
        if (byCode != null && JSONResult.SUCCESS.equals(byCode.getCode())) {
            return byCode.getData().getValue();
        }
        return null;
    }

    /**
     * 分配判单用户
     *
     * @return
     */
    @RequestMapping("/distributionPdUser")
    @ResponseBody
    public JSONResult distributionPdUser(@RequestBody ClueRepetitionDTO clueRepetitionDTO){
        List<String> pdUserList = clueRepetitionDTO.getPdUsers();
        if (CollectionUtils.isEmpty(pdUserList)) {
            return CommonUtil.getParamIllegalJSONResult();
        }

        return clueRepetitionFeignClient.distributionPdUser(clueRepetitionDTO);
    }
}
