package com.kuaidao.manageweb.controller.statistics.nextDayInvitation;

import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.nextDayInvitation.NextInvitationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.dupOrder.DupOrderQueryDto;
import com.kuaidao.stastics.dto.invitation.NextInvitationDto;
import com.kuaidao.stastics.dto.performance.PerformanceDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 次日邀约
 * guhuitao
 * 2019-11-25
 */
@Controller
@RequestMapping("/nextInvitation")
public class NextInvitationController extends BaseStatisticsController {


    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private NextInvitationFeignClient nextInvitationFeignClient;

    public String deptList(HttpServletRequest request){
        initSaleDept(request);
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> json =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("busList", json.getData());
        //签约集团
        JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        request.setAttribute("companyList", listNoPage.getData());
        return "";
    }


    public String  groupList(HttpServletRequest request, @RequestBody Long companyId,Long busAreaId,Long busGroupId,Long deptGroupId,
    Long teleGroupId,Long dateTime){
        initSaleDept(request);
        initBaseDto(request,dateTime,busAreaId,busGroupId,companyId,deptGroupId,teleGroupId);
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> json =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("busList", json.getData());
        //签约集团
        JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        request.setAttribute("companyList", listNoPage.getData());
        return "";
    }

    /**
     * 一级页面请求
     * @param baseQueryDto
     * @return
     */
    @RequestMapping("/queryPage")
    public @ResponseBody  JSONResult<Map<String,Object>> queryByPage(@RequestBody BaseQueryDto baseQueryDto){
      //权限控制
      initParams(baseQueryDto);
      return nextInvitationFeignClient.queryByPage(baseQueryDto);
    }

    /**
     * 二级页面请求
     * @param baseQueryDto
     * @return
     */
    @RequestMapping("/queryDeptPage")
    public @ResponseBody JSONResult<Map<String,Object>> queryDeptByPage(@RequestBody BaseQueryDto baseQueryDto){
        //权限控制
        initParams(baseQueryDto);
        return nextInvitationFeignClient.queryDeptPage(baseQueryDto);
    }


    /**
     * 一级页面数据导出
     * @param baseQueryDto
     * @param response
     */
    @RequestMapping("/invitationExport")
    public @ResponseBody void invitationExport(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            initParams(baseQueryDto);
            JSONResult<List<NextInvitationDto>> json= nextInvitationFeignClient.queryListByParams(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                NextInvitationDto[] dtos = json.getData().isEmpty()?new NextInvitationDto[]{}:json.getData().toArray(new NextInvitationDto[0]);
                String[] keys = {"companyName","busGroupName","invitationCount"};
                String[] hader = {"餐饮集团","商务组","次日邀约数"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("次日邀约统计表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" nextInvitation export error:",e);
        }
    }

    /**
     * 一级页面数据导出
     * @param baseQueryDto
     * @param response
     */
    @RequestMapping("/invitationExport")
    public @ResponseBody void deptExport(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            initParams(baseQueryDto);
            JSONResult<List<NextInvitationDto>> json= nextInvitationFeignClient.queryDeptList(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                NextInvitationDto[] dtos = json.getData().isEmpty()?new NextInvitationDto[]{}:json.getData().toArray(new NextInvitationDto[0]);
                String[] keys = {"deptGroupName","teleGroupName","invitationCount"};
                String[] hader = {"事业部","电销组","次日邀约数"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("大区次日邀约统计表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" nextInvitation export error:",e);
        }
    }



    public void initBaseDto(HttpServletRequest request,Long dateTime,Long busAreaId,Long groupId,Long companyId,
                            Long deptGroupId,Long teleGroupId){
        BaseQueryDto dto=new BaseQueryDto();
        dto.setTeleDeptId(deptGroupId);
        dto.setTeleGroupId(groupId);
        dto.setDateTime(dateTime);
        dto.setCompanyId(companyId);
        dto.setBusAreaId(busAreaId);
        dto.setBusGroupId(groupId);
        request.setAttribute("baseQueryDto",dto);
    }

    /**
     * 参数控制权限-已经显示结果
     * 一级列表所有权限筛选由 组id控制
     * @param baseQueryDto
     */
    public void initParams(BaseQueryDto baseQueryDto){
        //筛选组
        if(null!=baseQueryDto.getTeleGroupId()){
            List<Long> ids= Arrays.asList(baseQueryDto.getTeleGroupId());
            baseQueryDto.setTeleGroupIds(ids);
            return ;
        }
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        //电销组
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();

        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
            //如果有事业部筛选
            if(null!=baseQueryDto.getTeleDeptId()){
                queryDTO.setParentId(baseQueryDto.getTeleDeptId());
            }else{
                queryDTO.setParentId(curLoginUser.getOrgId());
            }
        }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
            queryDTO.setParentId(curLoginUser.getOrgId());
            if(null!=baseQueryDto.getTeleDeptId()){
                queryDTO.setParentId(baseQueryDto.getTeleDeptId());
            }
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode)){
            baseQueryDto.setTeleGroupIds(Arrays.asList(curLoginUser.getOrgId()));
            return;
        }else if(RoleCodeEnum.GLY.name().equals(roleCode) || RoleCodeEnum.DXZC.name().equals(roleCode)){
            //管理员可以查看全部
            queryDTO.setParentId(curLoginUser.getOrgId());
            if(null!=baseQueryDto.getTeleDeptId()){
                queryDTO.setParentId(baseQueryDto.getTeleDeptId());
            }
        }else{
            //other 没权限
            queryDTO.setId(curLoginUser.getOrgId());
        }
        JSONResult<List<OrganizationDTO>> json= organizationFeignClient.listDescenDantByParentId(queryDTO);
        if("0".equals(json.getCode())){
            List<Long> orgids=json.getData().stream().map(c->c.getId()).collect(Collectors.toList());
            baseQueryDto.setTeleGroupIds(orgids);
        }
    }


}
