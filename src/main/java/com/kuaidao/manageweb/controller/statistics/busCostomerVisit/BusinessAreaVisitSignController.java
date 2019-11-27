package com.kuaidao.manageweb.controller.statistics.busCostomerVisit;

import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.BusinessAreaVisitSign.BusinessAreaVisitSignFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description: BusinessAreaBisitSignController
 * @date: 19-11-21 下午3:57
 * @author: xuyunfeng
 * @version: 1.0
 */
@RequestMapping("/businessAreaBisitSign")
@Controller
public class BusinessAreaVisitSignController {
    private static Logger logger = LoggerFactory.getLogger(BusinessAreaVisitSignController.class);

    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private BusinessAreaVisitSignFeignClient businessAreaVisitSignFeignClient;
    /**
    * @Description 商务大区来访签约业绩表页面初始化
    * @param request
    * @Return java.lang.String
    * @Author xuyunfeng
    * @Date 19-11-26 上午10:44
    **/
    @RequiresPermissions("businessAreaBisitSign:initBusinessAreaBisitSign:view")
    @RequestMapping("/initBusinessAreaBisitSign")
    public String initBusinessAreaBisitSign(HttpServletRequest request){
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportformsBusiness/businessAreaVisitSign";
    }

    /**
    * @Description 商务大区来访签约业绩表查询
    * @param baseBusQueryDto
    * @Return com.kuaidao.common.entity.JSONResult<java.util.Map<java.lang.String,java.lang.Object>>
    * @Author xuyunfeng
    * @Date 19-11-26 下午4:37
    **/
    @RequestMapping("/getBusinessAreaSignList")
    public JSONResult<Map<String,Object>> getBusinessAreaSignList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        JSONResult<Map<String, Object>> businessAreaSignList = businessAreaVisitSignFeignClient.getBusinessAreaSignList(baseBusQueryDto);
        return businessAreaSignList;
    }

    public void initOrgList(HttpServletRequest request){
        String busAreaId="";// 当前商务大区
        String businessGroupId ="";//商务组
        String businessManagerId = "";//商务经理
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        //商务组
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        //TODO 后边修改为商务总经理
        if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            busAreaId = String.valueOf(curLoginUser.getOrgId());
            busGroupReqDTO.setParentId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员可以查看全部
            logger.info("管理员登录");
        }else{
            //other 没权限
            busGroupReqDTO.setId(-1l);
        }
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> listJSONResult = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        List<OrganizationRespDTO> data = listJSONResult.getData();
        //TODO 后边修改为商务总经理
        if(RoleCodeEnum.SWZJ.name().equals(roleCode) || RoleCodeEnum.SWJL.name().equals(roleCode)){
            busAreaId = String.valueOf(data.get(0).getParentId());
        }
        request.setAttribute("busGroupList",data);


        //餐饮集团
        JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        request.setAttribute("companyList", listNoPage.getData());

        request.setAttribute("busAreaId",busAreaId);
        request.setAttribute("businessGroupId",businessGroupId);
        request.setAttribute("businessManagerId",businessManagerId);



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
}
