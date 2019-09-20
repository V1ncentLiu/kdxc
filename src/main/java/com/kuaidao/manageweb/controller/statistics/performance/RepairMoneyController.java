package com.kuaidao.manageweb.controller.statistics.performance;


import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.performance.DupOrderClient;
import com.kuaidao.manageweb.feign.statistics.performance.TailOrderClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.dupOrder.DupOrderDto;
import com.kuaidao.stastics.dto.dupOrder.DupOrderQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 补齐尾款重单表
 */
@Controller
@RequestMapping("/repairMoney")
public class RepairMoneyController extends BaseStatisticsController {

    private static Logger logger = LoggerFactory.getLogger(RepairMoneyController.class);
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private TailOrderClient tailOrderClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @RequestMapping("/repairMoneyRepetitionTable")
    public String repairMoneyRepetition(HttpServletRequest request){
        //事业部初始化
        super.initSaleDept(request);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            initBaseDto(request,null,curLoginUser.getOrgId(),curLoginUser.getId(),null,null,null,null,null,null);
            return "reportPerformance/repetitionTableTeam";
        }
        return "reportPerformance/repairMoneyRepetitionTable";
    }

    @RequestMapping("/repairMoneyRepetitionTableTeam")
    public String selfVisitFollowTableTeam(HttpServletRequest request,Long teleDeptId,Long teleGroupId,
                                           Long startTime,Long endTime,String strSignStore,Long projectId,Long companyId){
        super.initSaleDept(request);
        initBaseDto(request,teleDeptId,teleGroupId,null,startTime,endTime,strSignStore,null,projectId,companyId);
        return "reportPerformance/repairMoneyRepetitionTableTeam";
    }

    /**
     * 一级页面数据
     * @param baseQueryDto
     * @return
     */
    @RequestMapping("/queryPage")
    public @ResponseBody
    JSONResult<Map<String,Object>> queryByPage(@RequestBody DupOrderQueryDto baseQueryDto){
        //查询组权限初始化
        initParams(baseQueryDto);
        return tailOrderClient.queryByPage(baseQueryDto);
    }


    /**
     * 二级页面数据
     * @param baseQueryDto
     * @return
     */
    @RequestMapping("/querySalePage")
    public @ResponseBody JSONResult<Map<String,Object>> querySaleByPage(@RequestBody DupOrderQueryDto baseQueryDto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            baseQueryDto.setTeleSaleId(curLoginUser.getId());
        }
        return  tailOrderClient.queryByPageBySale(baseQueryDto);
    }


    /**
     * 导出excel
     * @param baseQueryDto
     * @param response
     */
    @RequestMapping("export")
    public @ResponseBody void export(@RequestBody DupOrderQueryDto baseQueryDto, HttpServletResponse response){
        try{
            //查询组权限初始化
            initParams(baseQueryDto);
            JSONResult<List<DupOrderDto>> json=tailOrderClient.queryListByParams(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                DupOrderDto[] dtos = json.getData().isEmpty()?new DupOrderDto[]{}:json.getData().toArray(new DupOrderDto[0]);
                String[] keys = {"teleGroupName","tailNum","selfSign","dupSign","dupRate"};
                String[] hader = {"电销组","补尾款单数","其中:自签约","其中:重单签约","补尾款重单率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("补尾款重单表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" TailOrder export error:",e);
        }
    }


    /**
     * 二级页面导出
     * @param baseQueryDto
     * @param response
     */
    @RequestMapping("/saleExport")
    public @ResponseBody void exportSale(@RequestBody DupOrderQueryDto baseQueryDto,HttpServletResponse response){
        try{
            JSONResult<List<DupOrderDto>> json=tailOrderClient.queryListBySale(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                DupOrderDto[] dtos = json.getData().isEmpty()?new DupOrderDto[]{}:json.getData().toArray(new DupOrderDto[0]);
                String[] keys = {"userName","tailNum","selfSign","dupSign","dupRate"};
                String[] hader = {"电销顾问","补尾款单数","其中:自签约","其中:重单签约","补尾款重单率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("顾问补尾款重单表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" TailOrder export error:",e);
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

    public void initParams(DupOrderQueryDto dto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
            if(null!=dto.getTeleDeptId()){
                dto.setTeleDeptIds(new ArrayList<>(Arrays.asList(dto.getTeleDeptId())));
            }else {
                OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
                //总监理查看下属所有事业部
                queryDTO.setOrgType(OrgTypeConstant.DZSYB);
                queryDTO.setParentId(curLoginUser.getOrgId());
                JSONResult<List<OrganizationRespDTO>> result =
                        organizationFeignClient.queryOrgByParam(queryDTO);
                List<Long> ids = result.getData().stream().map(c -> c.getId()).collect(Collectors.toList());
                dto.setTeleDeptIds(ids);
            }
        }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
            //副总查看当前事业部
            dto.setTeleDeptIds(new ArrayList<>(Arrays.asList(curLoginUser.getOrgId())));
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode)){
            //总监查看自己组
            dto.setTeleGroupId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            //顾问查看自己
            dto.setTeleSaleId(curLoginUser.getId());
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员可以查看全部
            if(null!=dto.getTeleDeptId()){
                dto.setTeleDeptIds(new ArrayList<>(Arrays.asList(dto.getTeleDeptId())));
            }
        }else{
            //other
            dto.setTeleSaleId(curLoginUser.getId());
        }
    }

}
