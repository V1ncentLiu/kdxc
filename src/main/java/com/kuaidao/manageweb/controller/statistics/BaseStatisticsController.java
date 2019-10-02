package com.kuaidao.manageweb.controller.statistics;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.controller.statistics.performance.PerformanceController;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.dwOrganization.DwOrganizationFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.dwOrganizationQueryDTO.DwOrganizationQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author: guhuitao
 * @create: 2019-08-22 14:21
 **/
@Controller
public class BaseStatisticsController {

    private static Logger logger = LoggerFactory.getLogger(BaseStatisticsController.class);
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private DwOrganizationFeignClient dwOrganizationFeignClient;

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
            e.printStackTrace();
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
            e.printStackTrace();
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
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员可以查看全部
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

        //查询招商中心
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWDQ);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("areaList",queryOrgByParam.getData());
        request.setAttribute("curUserId",curLoginUser.getId()+"");
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

}
