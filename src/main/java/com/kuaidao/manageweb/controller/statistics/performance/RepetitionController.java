package com.kuaidao.manageweb.controller.statistics.performance;


import com.kuaidao.businessconfig.dto.project.CompanyInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.performance.DupOrderClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.dupOrder.DupOrderDto;
import com.kuaidao.stastics.dto.dupOrder.DupOrderQueryDto;
import com.kuaidao.stastics.dto.performance.PerformanceDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.netflix.client.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 业绩 重单
 */
@Controller
@RequestMapping("/repetition")
public class RepetitionController extends BaseStatisticsController {
    private static Logger logger = LoggerFactory.getLogger(RepetitionController.class);
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private DupOrderClient dupOrderClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    /**
     * 一级页面
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:repetition:view")
    @RequestMapping("/repetitionTable")
    public String repetitionTable(HttpServletRequest request){
        initSaleDept(request);
        //页面查询数据初始化
        initModel(request);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            initBaseDto(request,null,curLoginUser.getOrgId(),curLoginUser.getId(),null,null,null,null,null,null);
            return "reportPerformance/repetitionTableTeam";
        }
        return "reportPerformance/repetitionTable";
    }

    /**
     * 二级页面
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:repetition:view")
    @RequestMapping("/repetitionTableTeam")
    public String selfVisitFollowTableTeam(HttpServletRequest request,Long teleDeptId,Long teleGroupId,Long teleSaleId,
                                           Long startTime,Long endTime,String strSignStore,Integer signType,Long projectId,Long companyId){
        initSaleDept(request);
        //页面查询数据初始化
        initModel(request);
        if(null!=teleGroupId && null==teleDeptId){
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            IdEntity id=new IdEntity();
            id.setId(teleGroupId+"");
            JSONResult<OrganizationDTO> result=organizationFeignClient.queryOrgById(id);
            if("0".equals(result.getCode())){
                teleDeptId=result.getData().getParentId();
            }
        }
        initBaseDto(request,teleDeptId,teleGroupId,teleSaleId,startTime,endTime, strSignStore,signType,projectId,companyId);
        return "reportPerformance/repetitionTableTeam";
    }

    /**
     * 一级页面数据
     * @param baseQueryDto
     * @return
     */
    @RequiresPermissions("statistics:repetition:view")
    @RequestMapping("/queryPage")
    public @ResponseBody JSONResult<Map<String,Object>> queryByPage(@RequestBody DupOrderQueryDto baseQueryDto){
        //查询组权限初始化
        initParams(baseQueryDto);
        baseQueryDto.setTeleDeptId(null);
        return dupOrderClient.queryByPage(baseQueryDto);
    }


    /**
     * 二级页面数据
     * @param baseQueryDto
     * @return
     */
    @RequiresPermissions("statistics:repetition:view")
    @RequestMapping("/querySalePage")
    public @ResponseBody JSONResult<Map<String,Object>> querySaleByPage(@RequestBody DupOrderQueryDto baseQueryDto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            baseQueryDto.setTeleSaleId(curLoginUser.getId());
            baseQueryDto.setTeleDeptId(null);
            baseQueryDto.setTeleGroupId(null);
            return  dupOrderClient.queryByPageBySale(baseQueryDto);
        }
        return  dupOrderClient.queryByPageByGroup(baseQueryDto);
    }


    /**
     * 组级别导出excel
     * @param baseQueryDto
     * @param response
     */
    @RequiresPermissions("statistics:repetition:export")
    @RequestMapping("/export")
    public @ResponseBody void export(@RequestBody DupOrderQueryDto baseQueryDto,HttpServletResponse response){
        try{
            //查询组权限初始化
            initParams(baseQueryDto);
            JSONResult<List<DupOrderDto>> json=dupOrderClient.queryListByParams(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                DupOrderDto[] dtos = json.getData().isEmpty()?new DupOrderDto[]{}:json.getData().toArray(new DupOrderDto[0]);
                String[] keys = {"teleGroupName","signNum","selfSign","dupSign","dupRate"};
                String[] hader = {"电销组","签约单数","其中:自签约","其中:重单签约","重单率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("重单表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" DupOrderDto export error:",e);
        }
    }

    /**
     * 二级页面导出
     * @param baseQueryDto
     * @param response
     */
    @RequiresPermissions("statistics:repetition:export")
    @RequestMapping("/saleExport")
    public @ResponseBody void exportSale(@RequestBody DupOrderQueryDto baseQueryDto,HttpServletResponse response){
        try{
            JSONResult<List<DupOrderDto>> json=null;
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
            if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
                baseQueryDto.setTeleSaleId(curLoginUser.getId());
                json=dupOrderClient.queryListBySale(baseQueryDto);
            }else{
                json=dupOrderClient.queryListByGroup(baseQueryDto);
            }
            if(null!=json && "0".equals(json.getCode())){
                DupOrderDto[] dtos = json.getData().isEmpty()?new DupOrderDto[]{}:json.getData().toArray(new DupOrderDto[0]);
                String[] keys = {"userName","signNum","selfSign","dupSign","dupRate"};
                String[] hader = {"电销顾问","签约单数","其中:自签约","其中:重单签约","重单率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("顾问重单表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" DupOrderDto export error:",e);
        }
    }

    public void initBaseDto(HttpServletRequest request,Long deptId,Long groupId,Long saleId,
                            Long startTime,Long endTime,String  shopTypes,
                            Integer signType,Long projectId,Long companyId){
        DupOrderQueryDto dto=new DupOrderQueryDto();
        dto.setTeleDeptId(deptId);
        dto.setTeleGroupId(groupId);
        dto.setTeleSaleId(saleId);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setStrSignStore(shopTypes);
        dto.setSignType(signType);
        dto.setCompanyId(companyId);
        dto.setProjectId(projectId);
        request.setAttribute("baseQueryDto",dto);
    }


    private void initModel(HttpServletRequest request){
        // 签约店型
        request.setAttribute("shopTypeList", getDictionaryByCode(Constants.PROJECT_SHOPTYPE));
        //签约项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        //签约集团
        JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        request.setAttribute("companyList", listNoPage.getData());
    }


    /**
     * 参数控制权限-已经显示结果
     * 一级列表所有权限筛选由 组id控制
     * @param baseQueryDto
     */
    public void initParams(DupOrderQueryDto baseQueryDto){
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
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode) || RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            baseQueryDto.setTeleGroupIds(Arrays.asList(curLoginUser.getOrgId()));
            return;
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
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
        if("0".equals(json.getCode()) && json.getData().size()>0){
            List<Long> orgids=json.getData().stream().map(c->c.getId()).collect(Collectors.toList());
            baseQueryDto.setTeleGroupIds(orgids);
        }else{
            //没有电销组的情况
            baseQueryDto.setTeleGroupIds(Arrays.asList(-1l));
        }
    }
}
