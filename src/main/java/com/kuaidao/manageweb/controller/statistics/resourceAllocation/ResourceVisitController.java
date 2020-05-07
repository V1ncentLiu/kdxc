package com.kuaidao.manageweb.controller.statistics.resourceAllocation;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.resourceAllocation.ResourceVisitFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.resourceAllocation.ResourceVisitDto;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: guhuitao
 * @create: 2019-10-17 09:38
 * 分配资源跟访时间分布表
 **/
@Controller
@RequestMapping("/resourceVisit")
public class ResourceVisitController extends BaseStatisticsController {
    @Autowired
    private ResourceVisitFeignClient resourceVisitFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /**
     * 一级页面跳转
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:resourceVisit:view")
    @RequestMapping("/groupList")
    public String  groupList(HttpServletRequest request){
        initSaleDept(request);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            initBaseDto(request,null,curLoginUser.getOrgId(),curLoginUser.getId(),null,null,null);
            return "reportResourceFollow/followGroup";
        }
        return "reportResourceFollow/followDept";
    }

    /**
     * 二级页面跳转
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:resourceVisit:view")
    @RequestMapping("/managerList")
    public String  managerList(HttpServletRequest request,Long teleDeptId,Long teleGroupId,
                               Long teleSaleId,Integer source,Long startTime,Long endTime){
        initSaleDept(request);
        if(null==teleDeptId && null!=teleGroupId){
            //查看所属事业部
            IdEntity id=new IdEntity();
            id.setId(teleGroupId+"");
            JSONResult<OrganizationDTO> result =organizationFeignClient.queryOrgById(id);
            if("0".equals(result.getCode())){
                teleDeptId=result.getData().getParentId();
            }
        }
        initBaseDto(request,teleDeptId,teleGroupId,teleSaleId,source,startTime,endTime);
        return "reportResourceFollow/followGroup";
    }

    /**
     * 三级页面跳转
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:resourceVisit:view")
    @RequestMapping("/saleList")
    public String  saleList(HttpServletRequest request,Long teleDeptId,Long teleGroupId,Long teleSaleId,
                            Integer source,Long startTime,Long endTime){
        initSaleDept(request);
        if(null==teleGroupId && null!=teleSaleId){
            IdEntityLong id=new IdEntityLong(teleSaleId);
            JSONResult<UserInfoDTO> json=userInfoFeignClient.get(id);
            if("0".equals(json.getCode())){
                teleGroupId=json.getData().getOrgId();
                //查看所属事业部
                IdEntity orgId=new IdEntity(teleGroupId+"");
                JSONResult<OrganizationDTO> result =organizationFeignClient.queryOrgById(orgId);
                if("0".equals(result.getCode())){
                    teleDeptId=result.getData().getParentId();
                }
            }
        }
        initBaseDto(request,teleDeptId,teleGroupId,teleSaleId,source,startTime,endTime);
        return "reportResourceFollow/followManager";
    }

    /**
     * 一级页面查询
     * @param dto
     * @return
     */
    @RequiresPermissions("statistics:resourceVisit:view")
    @RequestMapping("queryByPage")
    public @ResponseBody
    JSONResult<Map<String,Object>> queryByPage(@RequestBody BaseQueryDto dto){
        this.initParams(dto);
        return this.resourceVisitFeignClient.queryByPage(dto);
    }


    /**
     * 二级页面查询
     * @param dto
     * @return
     */
    @RequiresPermissions("statistics:resourceVisit:view")
    @RequestMapping("queryManagerPage")
    public @ResponseBody
    JSONResult<Map<String,Object>> queryManagerPage(@RequestBody BaseQueryDto dto){
        this.initParams(dto);
        String roleCode=super.getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            dto.setTeleGroupId(null);
            dto.setTeleDeptId(null);
        }
        return this.resourceVisitFeignClient.queryManagerPage(dto);
    }

    /**
     * 三级页面查询
     * @param dto
     * @return
     */
    @RequiresPermissions("statistics:resourceVisit:view")
    @RequestMapping("querySalePage")
    public @ResponseBody
    JSONResult<Map<String,Object>> querySalePage(@RequestBody BaseQueryDto dto){
        this.initParams(dto);
        String roleCode=super.getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            dto.setTeleGroupId(null);
            dto.setTeleDeptId(null);
        }
        return this.resourceVisitFeignClient.querySalePage(dto);
    }


    /**
     * 一级页面导出excel
     * @param baseQueryDto
     * @param response
     */
    @RequiresPermissions("statistics:resourceVisit:export")
    @RequestMapping("export")
    public @ResponseBody void export(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            //查询组权限初始化
            initParams(baseQueryDto);
            JSONResult<List<ResourceVisitDto>> json=resourceVisitFeignClient.queryListByParams(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                ResourceVisitDto[] dtos = json.getData().isEmpty()?new ResourceVisitDto[]{}:json.getData().toArray(new ResourceVisitDto[0]);
                String[] keys = {"teleGroupName","resNum","noVisitRate","halfHourRate","oneHourRate","twoHourRate","threeHourRate","sixHourRate","twelveHourRate","twentyFourRate","moreRate"};
                String[] hader = {"电销组","分配资源数","未跟访占比","30分钟内跟访占比","1小时内跟访占比","2小时内跟访占比","3小时内跟访占比","6小时内跟访占比","12小时内跟访占比","24小时内跟访占比","24小时外跟访占比"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("分配资源跟访时间分布表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" ResourceVisitDto export error:",e);
        }
    }

    /**
     * 二级页面数据导出
     * @param baseQueryDto
     * @param response
     */
    @RequiresPermissions("statistics:resourceVisit:export")
    @RequestMapping("/managerExport")
    public @ResponseBody void manaerExport(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            //查询组权限初始化
            initParams(baseQueryDto);
            if(RoleCodeEnum.DXCYGW.name().equals(super.getRoleCode())){
                baseQueryDto.setTeleDeptId(null);
                baseQueryDto.setTeleGroupId(null);
            }
            JSONResult<List<ResourceVisitDto>> json=resourceVisitFeignClient.queryManagerList(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                ResourceVisitDto[] dtos = json.getData().isEmpty()?new ResourceVisitDto[]{}:json.getData().toArray(new ResourceVisitDto[0]);
                String[] keys = {"teleSaleName","resNum","noVisitRate","halfHourRate","oneHourRate","twoHourRate","threeHourRate","sixHourRate","twelveHourRate","twentyFourRate","moreRate"};
                String[] hader = {"电销顾问","分配资源数","未跟访占比","30分钟内跟访占比","1小时内跟访占比","2小时内跟访占比","3小时内跟访占比","6小时内跟访占比","12小时内跟访占比","24小时内跟访占比","24小时外跟访占比"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("电销组员分配资源跟访时间分布表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" ResourceVisitDto export error:",e);
        }
    }

    /**
     * 三级页面数据导出
     * @param baseQueryDto
     * @param response
     */
    @RequiresPermissions("statistics:resourceVisit:export")
    @RequestMapping("saleExport")
    public @ResponseBody void saleExport(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            //查询组权限初始化
            initParams(baseQueryDto);
            if(RoleCodeEnum.DXCYGW.name().equals(super.getRoleCode())){
                baseQueryDto.setTeleDeptId(null);
                baseQueryDto.setTeleGroupId(null);
            }
            JSONResult<List<ResourceVisitDto>> json=resourceVisitFeignClient.querySaleList(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                ResourceVisitDto[] dtos = json.getData().isEmpty()?new ResourceVisitDto[]{}:json.getData().toArray(new ResourceVisitDto[0]);
                String[] keys = {"visitTime","resNum","noVisitRate","halfHourRate","oneHourRate","twoHourRate","threeHourRate","sixHourRate","twelveHourRate","twentyFourRate","moreRate"};
                String[] hader = {"日期","分配资源数","未跟访占比","30分钟内跟访占比","1小时内跟访占比","2小时内跟访占比","3小时内跟访占比","6小时内跟访占比","12小时内跟访占比","24小时内跟访占比","24小时外跟访占比"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("电销顾问分配资源跟访时间分布表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" ResourceVisitDto export error:",e);
        }
    }

    public void initBaseDto(HttpServletRequest request,Long deptId,Long groupId,Long saleId,
                            Integer source,Long startTime,Long endTime){
        BaseQueryDto dto=new BaseQueryDto();
        dto.setTeleDeptId(deptId);
        dto.setTeleGroupId(groupId);
        dto.setTeleSaleId(saleId);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setSource(source);
        request.setAttribute("baseQueryDto",dto);
    }



    /**
     * 初始化查询参数-根据角色查对应事业部
     * @param dto
     */
    public void initParams(BaseQueryDto dto){
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
