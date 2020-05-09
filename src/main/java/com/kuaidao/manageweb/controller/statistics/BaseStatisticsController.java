package com.kuaidao.manageweb.controller.statistics;

import com.kuaidao.businessconfig.dto.project.CompanyInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.controller.statistics.performance.PerformanceController;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.dwOrganization.DwOrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.stastics.dto.dwOrganizationQueryDTO.DwOrganizationQueryDTO;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserDataAuthReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import com.rabbitmq.http.client.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: guhuitao
 * @create: 2019-08-22 14:21
 **/
@Slf4j
@Controller
public class BaseStatisticsController {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private DwOrganizationFeignClient dwOrganizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private SysRegionFeignClient sysRegionFeignClient;
    /**
     * 根据商务组id和角色查询 用户
     * @param userOrgRoleReq
     * @return
     */
    @RequestMapping("/base/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq) {
        try {
            JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                    userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
            return listByOrgAndRole;
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return new JSONResult<List<UserInfoDTO>>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"系统繁忙，请稍后再试");
    }

    /**
     * 根据参数查询组织机构
     * @param dto
     * @return
     */
    @RequestMapping("/base/getGroupList")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> getGroupList(@RequestBody OrganizationQueryDTO dto) {
        try {
            JSONResult<List<OrganizationRespDTO>> list =
                    organizationFeignClient.queryOrgByParam(dto);
            //如果没有子集-则按id查询（返回自己）
            if("0".equals(list.getCode()) &&(list.getData()==null || list.getData().isEmpty())){
                dto.setId(dto.getParentId()==null?-1:dto.getParentId());
                dto.setParentId(null);
                return organizationFeignClient.queryOrgByParam(dto);
            }
            return list;
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return new JSONResult<List<OrganizationRespDTO>>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"系统繁忙，请稍后再试");
    }

    /**
     * 查询dw组织机构
     */
    @RequestMapping("/base/getDwOrgList")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> getDwOrgList(@RequestBody DwOrganizationQueryDTO dto) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        dto.setBusinessLine(curLoginUser.getBusinessLine());
        try {
            JSONResult<List<OrganizationRespDTO>> list =
                    dwOrganizationFeignClient.getDwOrganization(dto);
            return list;
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return new JSONResult<List<OrganizationRespDTO>>().fail(SysErrorCodeEnum.ERR_SYSTEM.getCode(),"系统繁忙，请稍后再试");
    }


    /**
     * 统计三期页面-根据角色初始化电销事业部-及部分页面参数
     * @param request
     */
    protected void initSaleDept(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        request.setAttribute("roleCode",roleCode);
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        //查询电销事业部
        queryDTO.setOrgType(OrgTypeConstant.DZSYB);
        if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
            queryDTO.setParentId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
            queryDTO.setId(curLoginUser.getOrgId());
            request.setAttribute("deptId",curLoginUser.getOrgId()+"");
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode) || RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            JSONResult<OrganizationDTO> jsonResult= getOrganizationDTOById(curLoginUser.getOrgId());
            queryDTO.setId(jsonResult.getData().getParentId());
            request.setAttribute("teleGroupId",curLoginUser.getOrgId()+"");
            if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
                request.setAttribute("teleSaleId",curLoginUser.getId()+"");
            }
            JSONResult<List<OrganizationRespDTO>> jsonOrg =
                    organizationFeignClient.queryOrgByParam(queryDTO);
            //如果该用户所在组织结构没有事业部，则所在电销组补充为事业部
            if(null==jsonOrg.getData() || jsonOrg.getData().isEmpty()){
                request.setAttribute("deptList", Arrays.asList(jsonResult.getData()));
                request.setAttribute("deptId",jsonResult.getData().getId()+"");
            }else{
                request.setAttribute("deptList",jsonOrg.getData());
                request.setAttribute("deptId",jsonResult.getData().getParentId()+"");
            }
            return ;
        }else if(RoleCodeEnum.GLY.name().equals(roleCode) || RoleCodeEnum.DXZC.name().equals(roleCode)){
            //管理员可以查看全部
        }else if(RoleCodeEnum.TGZJ.name().equals(roleCode) || RoleCodeEnum.NQJL.name().equals(roleCode) || RoleCodeEnum.NQZG.name().equals(roleCode)){
            //该角色下 查询 授权的业务线数据
            List<UserDataAuthReq> authList=curLoginUser.getUserDataAuthList();
            List<OrganizationRespDTO> list=queryOrgByUserAuth(authList,OrgTypeConstant.DZSYB);
            request.setAttribute("deptList",list);
            return ;
        }else{
            //other 没权限
            queryDTO.setId(-1l);
        }
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("deptList",queryOrgByParam.getData());
    }



    /**
     * 根据code 码查询字段
     * @param code
     * @return
     */
    protected List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        try{
            JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                    dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
            if (queryDicItemsByGroupCode != null
                    && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
                return queryDicItemsByGroupCode.getData();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 按登录用户业务线查询-商务大区
     * @param request
     */
    protected void initBugOrg(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();

        //查询商务大区
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWDQ);
//        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode)){
            queryDTO.setId(curLoginUser.getOrgId());
            request.setAttribute("areaId",curLoginUser.getOrgId()+"");
        }else if(RoleCodeEnum.SWJL.name().equals(roleCode) || RoleCodeEnum.SWZJ.name().equals(roleCode)){
            if(RoleCodeEnum.SWJL.name().equals(roleCode)){
                request.setAttribute("managerId",curLoginUser.getId()+"");
            }
            OrganizationQueryDTO org = new OrganizationQueryDTO();
            org.setId(curLoginUser.getOrgId());
            request.setAttribute("busId",curLoginUser.getOrgId()+"");
            JSONResult<List<OrganizationRespDTO>> json =
                    organizationFeignClient.queryOrgByParam(org);
            if("0".equals(json.getCode())){
                Long parentId= json.getData().get(0).getParentId();
                queryDTO.setId(parentId);

                JSONResult<List<OrganizationRespDTO>> areajson=
                        organizationFeignClient.queryOrgByParam(queryDTO);
                if(areajson.getData().isEmpty()){
                    request.setAttribute("areaList",json.getData());
                    request.setAttribute("areaId",curLoginUser.getOrgId()+"");
                }else{
                    request.setAttribute("areaList",areajson.getData());
                    request.setAttribute("areaId",parentId+"");
                }
            }
            return ;
        }else if(RoleCodeEnum.SWZC.name().equals(roleCode)){//商务总裁
            queryDTO.setParentId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员查询全部
        }else{
            //other
            queryDTO.setId(curLoginUser.getOrgId());
        }
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("areaList",queryOrgByParam.getData());
        request.setAttribute("roleCode",roleCode);
    }


    /**
     * 获取当前登录用户角色码
     * @return
     */
    protected String getRoleCode(){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        return roleCode;
    }


    /**
     * 根据组织机构id 查询
     * @param orgId
     * @return
     */
    public JSONResult<OrganizationDTO> getOrganizationDTOById(Long orgId){
        IdEntity idEntity=new IdEntity();
        idEntity.setId(orgId.toString());
        JSONResult<OrganizationDTO> jsonResult= organizationFeignClient.queryOrgById(idEntity);
        return jsonResult;
    }


    /**
     * 电销报表初始化权限
     */
    public void initAuth(BaseBusQueryDto baseBusQueryDto){
        List<OrganizationDTO> teleGroupList = new ArrayList<>();
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            teleGroupList = getCurOrgGroupByOrgId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            teleGroupList = getCurOrgGroupByOrgId(curLoginUser.getOrgId());
        }else{
//            teleGroupList = getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.SWZ);
        }
        if(null != baseBusQueryDto && null != teleGroupList && teleGroupList.size() > 0){
            List<Long> orgIdList = teleGroupList.parallelStream().map(OrganizationDTO::getId).collect(Collectors.toList());
            baseBusQueryDto.setBusinessGroupIds(orgIdList);
        }
        //TODO 重新写一个方法
        //商务经理查询 考虑借调 删除组限制
        if(null != baseBusQueryDto && RoleCodeEnum.SWJL.name().equals(roleCode)){
            baseBusQueryDto.setBusinessGroupIds(null);
            baseBusQueryDto.setBusinessGroupId(null);
            baseBusQueryDto.setBusAreaId(null);
            baseBusQueryDto.setBusinessManagerId(curLoginUser.getId());
        }
        if("".equals(baseBusQueryDto.getVisitCity())){
            baseBusQueryDto.setVisitCity(null);
        }

    }
    /**
     * 获取当前 orgId所在的组
     */
    private List<OrganizationDTO> getCurOrgGroupByOrgId(Long orgId) {
        // 商务组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(String.valueOf(orgId));
        List<OrganizationDTO> data = new ArrayList<>();
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        data.add(orgJr.getData());
        return data;
    }
    private List<OrganizationDTO> getOrgGroupByOrgId(Long orgId,Integer orgType){
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(orgType);
        busGroupReqDTO.setParentId(orgId);
        JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        List<OrganizationDTO> data = listJSONResult.getData();
        return data;
    }

    public void initOrgList(HttpServletRequest request){
        String busAreaId="";// 当前商务大区
        String businessGroupId ="";//商务组
        String businessManagerId = "";//商务经理
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        //商务组
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode)){
            busAreaId = String.valueOf(curLoginUser.getOrgId());
            busGroupReqDTO.setParentId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            businessGroupId = String .valueOf(curLoginUser.getOrgId());
            busGroupReqDTO.setId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            businessGroupId = String.valueOf(curLoginUser.getOrgId());
            businessManagerId = String.valueOf(curLoginUser.getId());
            busGroupReqDTO.setId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员可以查看全部
            log.info("管理员登录");
        }else{
            //other 没权限
            busGroupReqDTO.setId(-1l);
        }
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> listJSONResult = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        List<OrganizationRespDTO> data = listJSONResult.getData();

        if(RoleCodeEnum.SWZJ.name().equals(roleCode) || RoleCodeEnum.SWJL.name().equals(roleCode)){
            busAreaId = String.valueOf(data.get(0).getParentId());
        }
        request.setAttribute("busGroupList",data);

        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());

        //餐饮集团
        List<CompanyInfoDTO> listNoPage = getCyjt();
        request.setAttribute("companyList", listNoPage);

        request.setAttribute("busAreaId",busAreaId);
        request.setAttribute("businessGroupId",businessGroupId);
        request.setAttribute("businessManagerId",businessManagerId);

        OrganizationQueryDTO busGroupReqDTO1 = new OrganizationQueryDTO();
        busGroupReqDTO1.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO1.setOrgType(OrgTypeConstant.DZSYB);
        busGroupReqDTO1.setBusinessLine(curLoginUser.getBusinessLine());
        JSONResult<List<OrganizationRespDTO>> listJSONResult1 = organizationFeignClient.queryOrgByParam(busGroupReqDTO1);
        request.setAttribute("deptList",listJSONResult1.getData());

    }



    /**
     * 页面元素初始化
     * @param type 0:country,1:province,2:city,3:district
     */
    public List<SysRegionDTO> queryProvince(Integer type){
        //省市区域
        SysRegionDTO queryDTO=new SysRegionDTO();
        queryDTO.setType(type);//province 省级别
        JSONResult<List<SysRegionDTO>> json= sysRegionFeignClient.queryOrgByParam(queryDTO);
        return json.getData();
    }

    public void initSWDQByBusiness(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        //查询商务大区
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWDQ);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("areaList",queryOrgByParam.getData());
        request.setAttribute("roleCode",roleCode);
    }

    /**
     * 根据授权，和组织机构类型查询组织机构
     * @param authList
     * @param ortType
     * @return
     */
    protected List<OrganizationRespDTO> queryOrgByUserAuth(List<UserDataAuthReq> authList,Integer ortType){
        List<OrganizationRespDTO> list=new ArrayList<>();
        if(null!=authList && authList.size()>0){
            OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
            orgDto.setOrgType(ortType);
            for(UserDataAuthReq auth:authList){
                orgDto.setBusinessLine(auth.getBusinessLine());
                JSONResult<List<OrganizationRespDTO>> json= organizationFeignClient.queryOrgByParam(orgDto);
                if("0".equals(json.getCode()) && null!=json.getData()){
                    list.addAll(json.getData());
                }
            }
        }
        return list;
    }

    /**
     * 查询餐饮集团
     */
    public List<CompanyInfoDTO> getCyjt(){
        List<CompanyInfoDTO> resultList = new ArrayList<>();
        UserInfoPageParam userInfoPageParam = new UserInfoPageParam();
        userInfoPageParam.setUserType(2);
        userInfoPageParam.setStatus(1);
        JSONResult<List<UserInfoDTO>> listJSONResult = userInfoFeignClient.listNoPage(userInfoPageParam);
        if("0".equals(listJSONResult.getCode()) && null!=listJSONResult.getData()){
            List<UserInfoDTO> data = listJSONResult.getData();
            for(UserInfoDTO userInfo : data){
                CompanyInfoDTO companyInfoDTO = new CompanyInfoDTO();
                companyInfoDTO.setId(userInfo.getId());
                companyInfoDTO.setGroupName(userInfo.getName());
                resultList.add(companyInfoDTO);
            }
        }
        return resultList;
    }
}
