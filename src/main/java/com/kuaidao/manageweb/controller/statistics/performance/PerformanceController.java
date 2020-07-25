package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.performance.PerformanceClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.performance.PerformanceDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
 * @author: guhuitao
 * @create: 2019-08-22 17:44
 * 电销业绩报表
 **/
@Controller
@RequestMapping("/performance")
public class PerformanceController extends BaseStatisticsController {
    private static Logger logger = LoggerFactory.getLogger(PerformanceController.class);
   @Autowired
   private OrganizationFeignClient organizationFeignClient;
   @Autowired
   private PerformanceClient performanceClient;
    /**
     * 电销组业绩
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:performance:view")
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request, @RequestParam(required = false) Integer type){
        initSaleDept(request);
        //资源类别
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            initBaseDto(request,null,curLoginUser.getOrgId(),curLoginUser.getId(),null,null,null,null);
            return "reportPerformance/managerPerformance";
        }
        request.setAttribute("type", type);
        return "reportPerformance/groupPerformance";
    }

    /**
     * 电销经理业绩排名
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:performance:view")
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request,Long deptId,Long teleGroupId,Long teleSaleId,Integer category,Long startTime,Long endTime,String searchText){
        if(null!=teleGroupId){
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            IdEntity id=new IdEntity();
            id.setId(teleGroupId+"");
            JSONResult<OrganizationDTO> result=organizationFeignClient.queryOrgById(id);
            if("0".equals(result.getCode())){
                deptId=result.getData().getParentId();
            }
        }
        initBaseDto(request,deptId,teleGroupId,teleSaleId,category,searchText,startTime,endTime);
        initSaleDept(request);
        //资源类别
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportPerformance/managerPerformance";
    }


    /**
     * 一级页面-业绩列表
     * @param baseQueryDto
     * @return
     */
    @RequiresPermissions("statistics:performance:view")
    @RequestMapping("/queryPage")
    public @ResponseBody JSONResult<Map<String,Object>>  queryByPage(@RequestBody BaseQueryDto baseQueryDto){
        initParams(baseQueryDto);
//        baseQueryDto.setTeleDeptId(null);
        return performanceClient.queryByPage(baseQueryDto);
    }

    /**
     * 二级页面-某电销组业绩排名
     * @param baseQueryDto
     * @return
     */
    @RequiresPermissions("statistics:performance:view")
    @RequestMapping("/querySalePage")
    public @ResponseBody JSONResult<Map<String,Object>>  querySaleByPage(@RequestBody BaseQueryDto baseQueryDto){
        String roleCode=getRoleCode();
        //去掉事业部参数-保留电销组参数
//        baseQueryDto.setTeleDeptId(null);
        //根据角色不同，使用查询方法不同
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            return performanceClient.querySalePageAndUser(baseQueryDto);
        }else{
//            JSONResult<List<PerformanceDto>> json= performanceClient.queryListByParams(baseQueryDto);
            return performanceClient.querySalePage(baseQueryDto);
        }
    }

    /**
     * 一级页面导出
     * @param baseQueryDto
     * @param response
     */
    @RequiresPermissions("statistics:performance:export")
    @RequestMapping("/export")
    public @ResponseBody void export(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            initParams(baseQueryDto);
            baseQueryDto.setTeleDeptId(null);
            JSONResult<List<PerformanceDto>> json=performanceClient.queryListByParams(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                PerformanceDto[] dtos = json.getData().isEmpty()?new PerformanceDto[]{}:json.getData().toArray(new PerformanceDto[0]);
                String[] keys = {"teleGroupName","culeNum","firstVisitNum","signNum","visitRate","signRate","achievement","drinkAchievement","nonDrinkAchievement","signAmount","resourceEfficiency","depositAmount","tailAmount","depositRatio","tailRecoveryRate"};
                String[] hader = {"电销组","首次分配资源数","首访数","签约数","资源来访率","签约率","业绩金额","(饮品)业绩金额","(非饮品)业绩金额","签约单笔","资源效率","定金量","尾款量","定金占比(首次)","尾款回收率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("业绩表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" preformance export error:",e);
        }
    }


    /**
     * 二级页面数据导出
     * @param baseQueryDto
     * @param response
     */
    @RequiresPermissions("statistics:performance:export")
    @RequestMapping("/saleExport")
    public @ResponseBody void saleExport(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            JSONResult<List<PerformanceDto>> json= null;
            //根据角色不同，使用查询方法不同
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            baseQueryDto.setTeleDeptId(null);
            String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
            if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
                json= performanceClient.querySaleListByUser(baseQueryDto);
            }else{
                json= performanceClient.querySaleListByParams(baseQueryDto);
            }
            if(null!=json && "0".equals(json.getCode())){
                PerformanceDto[] dtos = json.getData().isEmpty()?new PerformanceDto[]{}:json.getData().toArray(new PerformanceDto[0]);
                String[] keys = {"userName","culeNum","firstVisitNum","signNum","visitRate","signRate","achievement","drinkAchievement","nonDrinkAchievement","signAmount","resourceEfficiency","depositAmount","tailAmount","depositRatio","tailRecoveryRate"};
                String[] hader = {"电销顾问","首次分配资源数","首访数","签约数","资源来访率","签约率","业绩金额","(饮品)业绩金额","(非饮品)业绩金额","签约单笔","资源效率","定金量","尾款量","定金占比(首次)","尾款回收率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("电销顾问业绩表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" preformance export error:",e);
        }
    }

    public void initBaseDto(HttpServletRequest request,Long deptId,Long groupId,Long saleId,
                            Integer category,String searchText,Long startTime,Long endTime){
        BaseQueryDto dto=new BaseQueryDto();
        dto.setTeleDeptId(deptId);
        dto.setTeleGroupId(groupId);
        dto.setTeleSaleId(saleId);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setCategory(category);
        dto.setSearchText(searchText);
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
            List<Long> ids=Arrays.asList(baseQueryDto.getTeleGroupId());
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
        if("0".equals(json.getCode())){
            List<Long> orgids=json.getData().stream().map(c->c.getId()).collect(Collectors.toList());
            baseQueryDto.setTeleGroupIds(orgids);
        }
    }

}
