package com.kuaidao.manageweb.controller.statistics.reportInvitation;

import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.invitation.InvitationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.stastics.dto.invitation.InvitationDto;
import com.kuaidao.sys.constant.RegionTypeEnum;
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
 * @create: 2019-10-07 14:11
 * 自邀约跟踪表
 **/
@Controller
@RequestMapping("/invitation")
public class InvitationController extends BaseStatisticsController {

    @Autowired
    private InvitationFeignClient invitationFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    /**
     * 一级页面
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:invitation:view")
    @RequestMapping("/groupList")
    public String groupList(HttpServletRequest request){
        //初始化页面部门
        initSaleDept(request);
        //初始化页面数据
        initModel(request);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            initBaseDto(request,null,curLoginUser.getOrgId(),curLoginUser.getId(),null,null,null,null,null);
            return "reportResources/selfVisitFollowTableTeam";
        }
        return "reportResources/selfVisitFollowTable";
    }

    /**
     * 二级页面
     * @param request
     * @param deptId
     * @param teleGroupId
     * @param teleSaleId
     * @param category
     * @param cusLevel
     * @param province
     * @param startTime
     * @param endTime
     * @return
     */
    @RequiresPermissions("statistics:invitation:view")
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request,Long deptId,Long teleGroupId,Long teleSaleId,
                              Integer category,Integer cusLevel,String province,Long startTime,Long endTime){
        //初始化页面部门
        initSaleDept(request);
        ////初始化页面数据
        initModel(request);
        if(null==deptId && null!=teleGroupId){
            //查看所属事业部
            IdEntity id=new IdEntity();
            id.setId(teleGroupId+"");
            JSONResult<OrganizationDTO> result =organizationFeignClient.queryOrgById(id);
            if("0".equals(result.getCode())){
                deptId=result.getData().getParentId();
            }
        }
        initBaseDto(request,deptId,teleGroupId,teleSaleId,category,cusLevel,province,startTime,endTime);
        return "reportResources/selfVisitFollowTableTeam";
    }


    /**
     * 一级页面-业绩列表
     * @param baseQueryDto
     * @return
     */
    @RequiresPermissions("statistics:invitation:view")
    @RequestMapping("/queryPage")
    public @ResponseBody
    JSONResult<Map<String,Object>> queryByPage(@RequestBody BaseQueryDto baseQueryDto){
        initParams(baseQueryDto);
        return invitationFeignClient.queryByPage(baseQueryDto);
    }

    /**
     * 二级页面-某电销组业绩排名
     * @param baseQueryDto
     * @return
     */
    @RequiresPermissions("statistics:invitation:view")
    @RequestMapping("/querySalePage")
    public @ResponseBody JSONResult<Map<String,Object>>  querySaleByPage(@RequestBody BaseQueryDto baseQueryDto){
        String roleCode=getRoleCode();
        //根据角色不同，使用查询方法不同
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            //顾问查询去掉部门参数
            baseQueryDto.setTeleDeptId(null);
            baseQueryDto.setTeleGroupId(null);
            return invitationFeignClient.querySalePage(baseQueryDto);
        }else{
            return invitationFeignClient.queryManagerPage(baseQueryDto);
        }
    }


    /**
     * 一级页面导出
     * @param baseQueryDto
     * @param response
     */
    @RequiresPermissions("statistics:invitation:export")
    @RequestMapping("/export")
    public @ResponseBody void export(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            initParams(baseQueryDto);
            JSONResult<List<InvitationDto>> json=invitationFeignClient.queryListByParams(baseQueryDto);
            if(null!=json && "0".equals(json.getCode())){
                InvitationDto[] dtos = json.getData().isEmpty()?new InvitationDto[]{}:json.getData().toArray(new InvitationDto[0]);
                String[] keys = {"teleGroupName","invite","cancelInvite","delInvite","visitCus","noVisitCus","visit","signCus","signOrder","visitRate","signRate"};
                String[] hader = {"电销组","正常邀约数","取消邀约数","删除邀约数","来访客户数","未来访客户数","来访次数","签约客户数","签约单数","邀约来访率","邀约签约率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("自邀约跟踪表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
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
    @RequiresPermissions("statistics:invitation:export")
    @RequestMapping("/saleExport")
    public @ResponseBody void saleExport(@RequestBody BaseQueryDto baseQueryDto, HttpServletResponse response){
        try{
            JSONResult<List<InvitationDto>> json= null;
            //根据角色不同，使用查询方法不同
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
            if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
                //顾问查询去掉部门参数
                baseQueryDto.setTeleDeptId(null);
                baseQueryDto.setTeleGroupId(null);
                json= invitationFeignClient.querySaleList(baseQueryDto);
            }else{
                json= invitationFeignClient.queryManagerList(baseQueryDto);
            }
            if(null!=json && "0".equals(json.getCode())){
                InvitationDto[] dtos = json.getData().isEmpty()?new InvitationDto[]{}:json.getData().toArray(new InvitationDto[0]);
                String[] keys = {"saleName","invite","cancelInvite","delInvite","visitCus","noVisitCus","visit","signCus","signOrder","visitRate","signRate"};
                String[] hader = {"电销顾问","正常邀约数","取消邀约数","删除邀约数","来访客户数","未来访客户数","来访次数","签约客户数","签约单数","邀约来访率","邀约签约率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("顾问自邀约跟踪表_{0}_{1}.xlsx", "" + baseQueryDto.getStartTime(), baseQueryDto.getEndTime() + "");
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
                            Integer category,Integer cusLevel,String province,Long startTime,Long endTime){
        BaseQueryDto dto=new BaseQueryDto();
        dto.setTeleDeptId(deptId);
        dto.setTeleGroupId(groupId);
        dto.setTeleSaleId(saleId);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setCategory(category);
        dto.setCusLevel(cusLevel);
        dto.setProvince(province);
        request.setAttribute("baseQueryDto",dto);
    }

    /**
     * 初始化页面搜索参数
     * @param request
     */
    private void initModel(HttpServletRequest request){
        //查询省列表
        request.setAttribute("provinceList",queryProvince(RegionTypeEnum.省.getValue()));
        //客户级别
        request.setAttribute("cusLevelList",super.getDictionaryByCode(DicCodeEnum.CUSLEVEL.getCode()));
        //资源类别
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
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
