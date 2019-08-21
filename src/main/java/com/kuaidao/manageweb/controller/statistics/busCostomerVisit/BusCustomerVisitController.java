package com.kuaidao.manageweb.controller.statistics.busCostomerVisit;

import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.busCoustomerVisit.BusCousomerVisitFeignClient;
import com.kuaidao.manageweb.feign.statistics.busCoustomerVisit.BusManagerVisitFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.bussCoustomerVisit.CustomerVisitDto;
import com.kuaidao.stastics.dto.bussCoustomerVisit.CustomerVisitQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import com.netflix.ribbon.proxy.annotation.Http;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: guhuitao
 * @create: 2019-08-20 14:22
 * 商务-来访签约统计
 **/
@RequestMapping("/customerVisitSign")
@Controller
public class BusCustomerVisitController {
    private static Logger logger = LoggerFactory.getLogger(BusCustomerVisitController.class);
    @Autowired
    private BusCousomerVisitFeignClient busCousomerVisitFeignClient;
//    @Autowired
//    private BusManagerVisitFeignClient busManagerVisitFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @RequestMapping("/list")
    public String cusomerVisit(HttpServletRequest request){
        initOrgList(request);
        return "reportformsBusiness/businessSignTableTeam";
    }



    /**
     * 分页查询拜访
     * @param customerVisitQueryDto
     * @return
     */
    @RequestMapping("/queryByPage")
    @ResponseBody
    public JSONResult<PageBean<CustomerVisitDto>> queryByPage(@RequestBody CustomerVisitQueryDto customerVisitQueryDto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        //商务大区总监 查询所有商务组
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode)){
            OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
            busGroupReqDTO.setParentId(curLoginUser.getOrgId());
            busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
            busGroupReqDTO.setOrgType(OrgTypeConstant.SWZ);
            JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
            List<OrganizationDTO> data = listJSONResult.getData();
            List<Long> ids=data.stream().map(c->c.getId()).collect(Collectors.toList());
            customerVisitQueryDto.setBusinessGroupIds(ids);
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            //商务总监只能查当前组
            customerVisitQueryDto.setBusinessGroupIds(new ArrayList<>(Arrays.asList(curLoginUser.getOrgId())));
        }else if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            //商务经理只能查看自己
            customerVisitQueryDto.setBusinessGroupIds(new ArrayList<>(Arrays.asList(curLoginUser.getOrgId())));
            customerVisitQueryDto.setBusinessManagerId(curLoginUser.getId());
        }
        //如果有商务组筛选
        if(null!=customerVisitQueryDto.getBusinessGroupId()){
            customerVisitQueryDto.setBusinessGroupIds(new ArrayList<>(Arrays.asList(customerVisitQueryDto.getBusinessGroupId())));
        }
        return busCousomerVisitFeignClient.queryByPage(customerVisitQueryDto);
    }

    /**
     * 导出excel
     * @param request
     * @param customerVisitQueryDto
     */
    @RequestMapping("/exportExcel")
    @ResponseBody
    public void exportExcel(HttpServletRequest request,
            HttpServletResponse response, @RequestBody CustomerVisitQueryDto customerVisitQueryDto){
        try{
            JSONResult<List<CustomerVisitDto>> result=busCousomerVisitFeignClient.queryListByParams(customerVisitQueryDto);
            CustomerVisitDto [] dtos=result.getData().toArray(new CustomerVisitDto[0]);
            String [] keys={"userName","firstVisit","secondVisit","manyVisit","sumSign","firstSign","secondSign","manySign","otherSign","signRate","visitRate","secondSignRate","manySignRate"};
            String [] hader={"商务经理","首访数","二次来访数","2+次来访数","签约数","首访签约数","二次来访签约数","2+次来访签约数","其他签约","签约率","首访签约率","二次来访签约率","2+次来访签约率"};
            Workbook wb=ExcelUtil.createWorkBook(dtos,keys,hader);
            String name = "签约来访表.xlsx";

            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        }catch (Exception e){
         logger.error("exportExcel error:",e);
       }
    }


//    /**
//     * 来访签约表-- 商务经理 签约列表
//     * @param request
//     * @param managerId
//     * @return
//     */
//    @RequestMapping("/signDetailList")
//    public String managerVisit(HttpServletRequest request,Long managerId){
//        // 查询所有项目
//        initOrgList(request);
//        request.setAttribute("managerId",managerId);
//        return "reportformsBusiness/businessSignTable";
//    }



    /*public  JSONResult<PageBean<CustomerVisitDto>> queryPageByManagerId(@RequestBody CustomerVisitQueryDto customerVisitQueryDto){
        if(null==customerVisitQueryDto.getBusinessManagerId()){
            return new JSONResult<PageBean<CustomerVisitDto>>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),"必填参数为空");
        }
        JSONResult<PageBean<CustomerVisitDto>> jsonResult=busManagerVisitFeignClient.queryByPage(customerVisitQueryDto);
        return jsonResult;
    }*/


    private void initOrgList(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        //商务组
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode)){
            busGroupReqDTO.setParentId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode) ||
                RoleCodeEnum.SWJL.name().equals(roleCode)){
            busGroupReqDTO.setId(curLoginUser.getOrgId());
        }else {
            busGroupReqDTO.setId(-1l);
        }

        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        List<OrganizationDTO> data = listJSONResult.getData();
        request.setAttribute("busGroupList",data);

        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());

        //商务经理列表
        UserOrgRoleReq infoDTO=new UserOrgRoleReq();
        infoDTO.setRoleCode(RoleCodeEnum.SWJL.name());
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode)){
            List<Long> orgidList= data.stream().map(c->c.getId()).collect(Collectors.toList());
            infoDTO.setOrgIdList(orgidList);
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            List<Long> list= Arrays.asList(curLoginUser.getOrgId());
            infoDTO.setOrgIdList(list);
        }else{
            List<UserInfoDTO> list= Arrays.asList(curLoginUser);
            request.setAttribute("userList", list);
            return;
        }
        JSONResult<List<UserInfoDTO>> jsonResult=userInfoFeignClient.getUserInfoListByParam(infoDTO);
        request.setAttribute("userList", jsonResult.getData());
    }

}
