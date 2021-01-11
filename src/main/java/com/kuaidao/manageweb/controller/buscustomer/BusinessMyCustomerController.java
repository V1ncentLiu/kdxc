package com.kuaidao.manageweb.controller.buscustomer;

import com.kuaidao.aggregation.constant.ClueCirculationConstant;
import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerRespDTO;
import com.kuaidao.aggregation.dto.busmycustomer.MyCustomerParamDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.clue.ClueBasicDTO;
import com.kuaidao.aggregation.dto.clue.ClueCustomerDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueRelateDTO;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.CompanyInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.busmycustomer.BusMyCustomerFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author yangbiao 接口层 Created on 2019-2-12 15:06:38 商务模块--我的客户
 */

@Controller
@RequestMapping("/aggregation/businessMyCustomer")
public class BusinessMyCustomerController {

    private static Logger logger = LoggerFactory.getLogger(BusinessMyCustomerController.class);

    @Autowired
    CompanyInfoFeignClient companyInfoFeignClient;

    @Autowired
    BusMyCustomerFeignClient busMyCustomerFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;


    @Value("${oss.url.directUpload}")
    private String ossUrl;

    /**
     *
     * 餐盟端待处理邀约来访客户跳转页面
     * @param request
     * @return
     */
    @RequestMapping("/initPendingList")
    public String initPendingList(HttpServletRequest request) {
        return "visit/customerVisitPend";
    }

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request,@RequestParam(required = false) Integer type) {
        logger.info("------------ 商务：我的客户列表 ---------------");
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 电销组
        MyCustomerParamDTO dto = new MyCustomerParamDTO();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
        } else {
            dto.setBusSaleId(user.getId());
        }
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(-1);
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (JSONResult.SUCCESS.equals(proJson.getCode())) {
            request.setAttribute("proAllSelect", proJson.getData());
            if (!CollectionUtils.isEmpty(proJson.getData())) {
                List<ProjectInfoDTO> data = proJson.getData();
                List<ProjectInfoDTO> alist = new ArrayList<>();
                for (ProjectInfoDTO infoDTO : data) {
                    if (AggregationConstant.NO.equals(infoDTO.getIsNotSign())) {
                        alist.add(infoDTO);
                    }
                }
                request.setAttribute("proSelect", alist);
            }
        }
        JSONResult<List<CompanyInfoDTO>> listJSONResult = companyInfoFeignClient.allCompany();
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            request.setAttribute("companySelect", proJson.getData());
        }
        // 查询赠送类型集合
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        // request.setAttribute("teleGroupList", teleGroupList);
        // request.setAttribute("teleSaleList", teleSaleList);
        request.setAttribute("loginUserId", user.getId());
        request.setAttribute("type",type);
        request.setAttribute("businessLines",user.getBusinessLine());
        return "bus_mycustomer/mycustomerList";
    }


    @PostMapping("/teleSaleAndGroupName")
    @ResponseBody
    public JSONResult<Map> teleSaleAndGroupName(@RequestBody MyCustomerParamDTO param) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        Map<String, List<OrganizationDTO>> map = new HashMap<>();
        Map<Long, OrganizationDTO> groupMap = new HashMap();
        List teleGroupList = new ArrayList();
        List teleSaleList = new ArrayList();
        Map<Long, OrganizationDTO> saleMap = new HashMap();

        MyCustomerParamDTO dto = new MyCustomerParamDTO();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
        } else {
            dto.setBusSaleId(user.getId());
        }

        JSONResult<List<BusMyCustomerRespDTO>> resList = busMyCustomerFeignClient.queryList(dto);
        if (JSONResult.SUCCESS.equals(resList.getCode())) {
            List<BusMyCustomerRespDTO> datas = resList.getData();
            for (BusMyCustomerRespDTO myCustomerRespDTO : datas) {

                if (myCustomerRespDTO.getTeleGorupId() != null) {
                    OrganizationDTO organizationDTO = new OrganizationDTO();
                    organizationDTO.setId(myCustomerRespDTO.getTeleGorupId());
                    organizationDTO.setName(myCustomerRespDTO.getTeleGorupName());
                    organizationDTO.setCreateTime(myCustomerRespDTO.getTeleGorupCreateTime());
                    groupMap.put(organizationDTO.getId(), organizationDTO);
                }
                // 创业顾问
                if (myCustomerRespDTO.getTeleSaleId() != null) {
                    OrganizationDTO organizationDTO = new OrganizationDTO();
                    organizationDTO.setId(myCustomerRespDTO.getTeleSaleId());
                    organizationDTO.setName(myCustomerRespDTO.getTeleSaleName());
                    saleMap.put(myCustomerRespDTO.getTeleSaleId(), organizationDTO);
                }
            }

            for (Map.Entry<Long, OrganizationDTO> entry : groupMap.entrySet()) {
                if (entry.getValue().getCreateTime() != null) {
                    teleGroupList.add(entry.getValue());
                }
            }
            for (Map.Entry<Long, OrganizationDTO> entry : saleMap.entrySet()) {
                teleSaleList.add(entry.getValue());
            }
        }
        logger.info("电销组,{{}}" + teleGroupList);
        Collections.sort(teleGroupList, Comparator.comparing(OrganizationDTO::getCreateTime));
        map.put("teleGroupList", teleGroupList);
        map.put("teleSaleList", teleSaleList);
        return new JSONResult<Map>().success(map);
    }



    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<BusMyCustomerRespDTO>> queryListPage(
            @RequestBody MyCustomerParamDTO param) {
        logger.info("============分页数据查询==================");

        Date date1 = param.getReserveTime1();
        Date date2 = param.getReserveTime2();
        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                return new JSONResult().fail("-1", "邀约来访时间，结束时间不能早于开始时间!");
            }
        }

        Date date3 = param.getAllocateTime1();
        Date date4 = param.getAllocateTime2();
        if (date3 != null && date4 != null) {
            if (date3.getTime() > date4.getTime()) {
                return new JSONResult().fail("-1", "接收客户时间，结束时间不能早于开始时间!");
            }
        }

        Date date5 = param.getAppiontmentCreateTime1();
        Date date6 = param.getAppiontmentCreateTime2();
        if (date5 != null && date6 != null) {
            if (date5.getTime() > date6.getTime()) {
                return new JSONResult().fail("-1", "提交邀约时间，结束时间不能早于开始时间!");
            }
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        /**
         * 下回代码注释掉，请记得给我改回来
         */
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
        } else {
            param.setBusSaleId(user.getId());
            param.setBusGroupId(user.getOrgId());
        }
        return busMyCustomerFeignClient.queryPageList(param);
    }

    /**
     * 标记未到访
     */
    @PostMapping("/notVisit")
    @ResponseBody
    @LogRecord(description = "标记未到访", operationType = OperationType.UPDATE,
            menuName = MenuEnum.BUS_MY_CUSTOMER)
    public JSONResult<Boolean> notVisit(@RequestBody BusMyCustomerReqDTO param) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        param.setUserId(curLoginUser.getId());
        param.setBusGroupId(curLoginUser.getOrgId());
        if (curLoginUser.getBusinessLine() != null) {
            param.setBusinessLine(curLoginUser.getBusinessLine());
        }
        return busMyCustomerFeignClient.notVisit(param);
    }

    /**
     * 未到访原因查看
     */
    @PostMapping("/notVisitReason")
    @ResponseBody
    public JSONResult<ClueBasicDTO> notVisitReason(@RequestBody IdEntityLong idEntityLong) {
        return busMyCustomerFeignClient.notVisitReason(idEntityLong);
    }

    @PostMapping("/listNoPage")
    @ResponseBody
    public JSONResult<List<CompanyInfoDTO>> listNoPage() {
        JSONResult<List<CompanyInfoDTO>> list = companyInfoFeignClient.allCompany();
        return list;
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
     *商务客户管理创建资源
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/createClue")
    @RequiresPermissions("businessMyCustomer:add")
    public String createClue(HttpServletRequest request, Model model) {
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            model.addAttribute("proSelect", proJson.getData());
        }
        model.addAttribute("ossUrl", ossUrl);
        // 系统参数优化资源类别
        String optList = getSysSetting(SysConstant.OPT_CATEGORY);
        request.setAttribute("optList", optList);
        // 系统参数非优化资源类别
        String notOptList = getSysSetting(SysConstant.NOPT_CATEGORY);
        request.setAttribute("notOptList", notOptList);
        return "clue/addCustomerResourcesBusiness";
    }

    /**
     * 新建资源保存
     *
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/saveCreateClue")
    @ResponseBody
    @LogRecord(description = "新建资源保存", operationType = OperationType.INSERT,
        menuName = MenuEnum.TM_MY_CUSTOMER)
    public JSONResult<Boolean> saveCreateClue(HttpServletRequest request, @RequestBody ClueDTO dto) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        if (null != user) {
            // 添加创建人
            if (null != dto) {
                ClueCustomerDTO cus = dto.getClueCustomer();
                if (null != cus) {
                    cus.setCreateUser(user.getId());
                    cus.setCreateTime(new Date());
                }
                ClueBasicDTO basic = dto.getClueBasic();
                if (null != basic) {
                    // 添加创建人
                    basic.setCreateUser(user.getId());
                    basic.setCreateTime(new Date());
                }
                if (user.getBusinessLine() != null) {
                    basic.setBusinessLine(user.getBusinessLine());
                }
                dto.setClueBasic(basic);
            }

            // 商务关联数据
            ClueRelateDTO relation = new ClueRelateDTO();
            // 商务顾问
            relation.setBusSaleId(user.getId());
            // 商务组
            relation.setBusGroupId(user.getOrgId());
            dto.setClueRelate(relation);

            UserOrgRoleReq userRole = new UserOrgRoleReq();
            userRole.setRoleCode(RoleCodeEnum.SWZJ.name());
            userRole.setOrgId(user.getOrgId());
            List<Integer> statusList = new ArrayList();
            statusList.add(1);
            userRole.setStatusList(statusList);
            JSONResult<List<UserInfoDTO>> userInfoJson =
                userInfoFeignClient.listByOrgAndRole(userRole);
            if (userInfoJson != null && JSONResult.SUCCESS.equals(userInfoJson.getCode())
                && userInfoJson.getData() != null && userInfoJson.getData().size() > 0) {
                // 商务总监
                relation.setBusDirectorId(userInfoJson.getData().get(0).getId());
            }
            // 查询用户的上级
            OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
            orgDto.setId(user.getOrgId());
            orgDto.setSystemCode(SystemCodeConstant.HUI_JU);

            IdEntity idEntity = new IdEntity();
            idEntity.setId(""+user.getOrgId());
            JSONResult<OrganizationDTO> ores = organizationFeignClient
                .queryOrgById(idEntity);
            if (ores != null && JSONResult.SUCCESS.equals(ores.getCode())
                && ores.getData() != null) {
                OrganizationDTO data = ores.getData();
                Long parentId = data.getParentId();
                relation.setBusAreaId(parentId);
                UserOrgRoleReq userRoleInfo = new UserOrgRoleReq();
                userRoleInfo.setRoleCode(RoleCodeEnum.SWDQZJ.name());
                userRoleInfo.setOrgId(parentId);
                JSONResult<List<UserInfoDTO>> ceoUserInfoJson =
                    userInfoFeignClient.listByOrgAndRole(userRoleInfo);
                if (ceoUserInfoJson.getCode().equals(JSONResult.SUCCESS)
                    && null != ceoUserInfoJson.getData()
                    && ceoUserInfoJson.getData().size() > 0) {
                    // 商务大区总监
                    relation.setBusAreaDirectorId(ceoUserInfoJson.getData().get(0).getId());
                }
            }
//            JSONResult<List<OrganizationDTO>> orgJson =
//                organizationFeignClient.listParentsUntilOrg(orgDto);
//            if (orgJson != null && JSONResult.SUCCESS.equals(orgJson.getCode())
//                && orgJson.getData() != null && orgJson.getData().size() > 0) {
//                for (OrganizationDTO org : orgJson.getData()) {
//                    if (null != org.getOrgType()
//                        && org.getOrgType().equals(OrgTypeConstant.SWDQ)) {
//                        //商务大区
//                        relation.setBusAreaId(org.getId());
//                        UserOrgRoleReq userRoleInfo = new UserOrgRoleReq();
//                        userRoleInfo.setRoleCode(RoleCodeEnum.SWDQZJ.name());
//                        userRoleInfo.setOrgId(org.getId());
//                        JSONResult<List<UserInfoDTO>> ceoUserInfoJson =
//                            userInfoFeignClient.listByOrgAndRole(userRoleInfo);
//                        if (ceoUserInfoJson.getCode().equals(JSONResult.SUCCESS)
//                            && null != ceoUserInfoJson.getData()
//                            && ceoUserInfoJson.getData().size() > 0) {
//                            // 商务大区总监
//                            relation.setBusAreaDirectorId(ceoUserInfoJson.getData().get(0).getId());
//                        }
//
//                    }
//                }
//            }
        }

        dto.setCirculationInsertOrUpdateDTO(getCircul(user,dto.getClueId()));
        JSONResult<Boolean> customerClue = busMyCustomerFeignClient.createCustomerClue(dto);
        return customerClue;
    }

    /**
     *  流转记录
     */
    private CirculationInsertOrUpdateDTO getCircul(UserInfoDTO user,Long clueId){
        // 保存流转记录
        CirculationInsertOrUpdateDTO circul = new CirculationInsertOrUpdateDTO();
        circul.setAllotUserId(user.getId());
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            circul.setAllotRoleId(user.getRoleList().get(0).getId());
        }
//        circul.setTeleReceiveSource(
//            ClueCirculationConstant.TELE_RECEIVE_SOURCE.TELE_CREATE.getCode());
        circul.setServiceStaffRole(ClueCirculationConstant.SERVICE_STAFF_ROLE.BUSINESS_MANAGER.getCode());
        circul.setClueId(clueId);
        circul.setAllotOrg(user.getOrgId());
        circul.setUserId(user.getId());
        // 新资源类型，电销自己创建的和话务主管转给话务的新资源类型一致
        circul.setNewResource(ClueCirculationConstant.NewResource.OTHER_RESOURCE.getCode());
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            circul.setRoleId(user.getRoleList().get(0).getId());
        }
        circul.setOrg(user.getOrgId());
        return circul;
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

}
