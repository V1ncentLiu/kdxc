package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.businessconfig.dto.project.CompanyInfoDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.busPerformance.BusVisitSignFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.busPerformance.BusVisitSignDto;
import com.kuaidao.sys.constant.RegionTypeEnum;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: guhuitao
 * @create: 2019-09-23 17:02
 * 商务报表-业绩报表-来访签约区域表
 **/
@Controller
@RequestMapping("/busVisitSign")
public class BusVisitSignController extends BaseStatisticsController {


    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private SysRegionFeignClient sysRegionFeignClient;
    @Autowired
    private BusVisitSignFeignClient busVisitSignFeignClient;

    /**
     * 一级页面-按区域统计
     * @return
     */
    @RequiresPermissions("statistics:busVisitSign:view")
    @RequestMapping("/areaList")
    public String areaList(HttpServletRequest request){
        initBugOrg(request);
        request.setAttribute("provinceList",queryProvince(RegionTypeEnum.省.getValue()));

        return "reportBusPerformance/busAreaList";
    }


    /**
     * 二级页面-按集团统计
     * @return
     */
    @RequiresPermissions("statistics:busVisitSign:view")
    @RequestMapping("/groupList")
    public String gruopList(HttpServletRequest request,Long busAreaId,Long businessGroupId,Long businessManagerId,
                            String province,Long startTime,Long endTime){
        initBugOrg(request);
        initBaseDto(request,busAreaId,businessGroupId,businessManagerId,null,province,startTime,endTime);
        request.setAttribute("provinceList",queryProvince(RegionTypeEnum.省.getValue()));
        //签约集团
        JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        request.setAttribute("companyList", listNoPage.getData());
        return "reportBusPerformance/busGroupList";
    }

    /**
     * 一级页面
     * @param baseQueryDto
     * @return
     */
    @RequiresPermissions("statistics:busVisitSign:view")
    @RequestMapping("/queryPage")
    public @ResponseBody JSONResult<Map<String,Object>> queryPage(@RequestBody BaseBusQueryDto baseQueryDto){
        initParams(baseQueryDto);
        if(RoleCodeEnum.SWJL.name().equals(getRoleCode())){
            baseQueryDto.setBusinessGroupId(null);
            baseQueryDto.setBusAreaId(null);
        }
        return busVisitSignFeignClient.queryByPage(baseQueryDto);
    }

    /**
     * 二级页面查询
     * @param baseQueryDto
     * @return
     */
    @RequiresPermissions("statistics:busVisitSign:view")
    @RequestMapping("/queryBusPage")
    public @ResponseBody JSONResult<Map<String,Object>> queryBusPage(@RequestBody BaseBusQueryDto baseQueryDto){
        initParams(baseQueryDto);
        String roleCode=super.getRoleCode();
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode) || RoleCodeEnum.GLY.name().equals(roleCode)){
            return busVisitSignFeignClient.queryBusPage(baseQueryDto);
        }else if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            baseQueryDto.setBusinessGroupId(null);
            baseQueryDto.setBusAreaId(null);
        }
        return busVisitSignFeignClient.queryBusPageForSale(baseQueryDto);
    }

    /**
     * 一级页面导出
     * @param dto
     * @param response
     */
    @RequiresPermissions("statistics:busVisitSign:export")
    @RequestMapping("/export")
    @ResponseBody
    public void export(@RequestBody BaseBusQueryDto dto, HttpServletResponse response){
        try{
            initParams(dto);
            JSONResult<List<BusVisitSignDto>> json=busVisitSignFeignClient.queryList(dto);
            if(null!=json && "0".equals(json.getCode())){
                BusVisitSignDto[] dtos = json.getData().isEmpty()?new BusVisitSignDto[]{}:json.getData().toArray(new BusVisitSignDto[0]);
                String[] keys = {"regionName","firstVisit","signNum","signRate","amount","sigleAmount"};
                String[] hader = {"区域","首访数","签约数","签约率","净业绩金额","签约单笔"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("来访签约区域表_{0}_{1}.xlsx", "" + dto.getStartTime(), dto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" BusVisitSignDto export error:",e);
        }
    }


    @RequiresPermissions("statistics:busVisitSign:export")
    @RequestMapping("/exportCompany")
    @ResponseBody
    public void saleExport(@RequestBody BaseBusQueryDto dto, HttpServletResponse response){
        try{
            initParams(dto);
            String roleCode=super.getRoleCode();
            JSONResult<List<BusVisitSignDto>> json= null;
            if(RoleCodeEnum.SWDQZJ.name().equals(roleCode) || RoleCodeEnum.GLY.name().equals(roleCode)){
                json = busVisitSignFeignClient.queryBusList(dto);
            }else{
                json= busVisitSignFeignClient.queryBusListSale(dto);
            }
            if(null!=json && "0".equals(json.getCode())){
                BusVisitSignDto[] dtos = json.getData().isEmpty()?new BusVisitSignDto[]{}:json.getData().toArray(new BusVisitSignDto[0]);
                String[] keys = {"companyName","regionName","firstVisit","signNum","signRate","amount","sigleAmount"};
                String[] hader = {"餐饮集团","省份","首访数","签约数","签约率","净业绩金额","签约单笔"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("集团来访签约区域表_{0}_{1}.xlsx", "" + dto.getStartTime(), dto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" BusVisitSignDto export error:",e);
        }
    }


    private void initBaseDto(HttpServletRequest request,Long areaId,Long groupId,Long saleId,
                            Long companyId,String province,Long startTime,Long endTime){
        BaseBusQueryDto dto=new BaseBusQueryDto();
        dto.setBusAreaId(areaId);
        dto.setBusinessGroupId(groupId);
        dto.setBusinessManagerId(saleId);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setProvince(province);
        dto.setCompanyId(companyId);
        request.setAttribute("busDto",dto);
    }

    public void initParams(BaseBusQueryDto dto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode)){
            dto.setBusAreaId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode) || RoleCodeEnum.SWJL.name().equals(roleCode)){
            dto.setBusinessGroupId(curLoginUser.getOrgId());
            if(RoleCodeEnum.SWJL.name().equals(roleCode)){
                dto.setBusinessManagerId(curLoginUser.getId());
            }
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            if(null!=dto.getBusAreaId()){
                dto.setBusAreaIds(new ArrayList<>(Arrays.asList(dto.getBusAreaId())));
            }
        }else{
            dto.setBusinessGroupId(curLoginUser.getOrgId());
        }
    }


}
