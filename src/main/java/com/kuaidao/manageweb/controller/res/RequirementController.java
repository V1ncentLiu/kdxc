package com.kuaidao.manageweb.controller.res;

import com.kuaidao.aggregation.dto.res.ResQueryDto;
import com.kuaidao.aggregation.dto.res.ResRequirement;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.res.RequirementFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.dupOrder.DupOrderDto;
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
import java.util.stream.Collectors;

/**
 * @author: guhuitao
 * @create: 2019-10-28 10:11
 * 请求资源
 **/
@RequestMapping("/requirment")
@Controller
public class RequirementController extends BaseStatisticsController {

    @Autowired
    private RequirementFeignClient requirementFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;


    @RequiresPermissions("resource:requirment:view")
    @RequestMapping("requirmentlist")
    public String requirmentlist(HttpServletRequest request){
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        //查询电销事业部
        queryDTO.setOrgType(OrgTypeConstant.DZSYB);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("deptList",queryOrgByParam.getData());
        return  "clue/clueRequirement";
    }

    /**
     * 分页
     * @param dto
     * @return
     */
    @RequiresPermissions("resource:requirment:view")
    @RequestMapping("queryPage")
    public @ResponseBody JSONResult<PageBean<ResRequirement>> quetyPage(@RequestBody ResQueryDto dto){
        //initDto(dto);
        return requirementFeignClient.queryPage(dto);
    }

    /**
     * 导出excel
     * @param response
     * @param dto
     */
    @RequiresPermissions("resource:requirment:export")
    @RequestMapping("/export")
    public @ResponseBody void export(HttpServletResponse response, @RequestBody ResQueryDto dto){
        try{
           // initDto(dto);
            JSONResult<List<ResRequirement>> json=requirementFeignClient.queryList(dto);
            if("0".equals(json.getCode())){//&& !json.getData().isEmpty()
                ResRequirement[] dtos = json.getData().isEmpty()?new ResRequirement[]{}:json.getData().toArray(new ResRequirement[0]);
                String[] keys = {"months","teleGroupName","projectNames","projectType","biddingPrice","optimization","information","remarks"};
                String[] hader = {"月份","电销组","电销组项目","项目类别","竞价(日均量)","优化(日均量)","信息流(日均量)","备注"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("资源需求_{0}.xlsx", "" + System.currentTimeMillis()+"");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error("ResRequirement import error");
        }
    }


    public void initDto(ResQueryDto dto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
            if(null!=dto.getTeleDeptId()){
                dto.setDeptIds(new ArrayList<>(Arrays.asList(dto.getTeleDeptId())));
            }else {
                OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
                //总监理查看下属所有事业部
                queryDTO.setOrgType(OrgTypeConstant.DZSYB);
                queryDTO.setParentId(curLoginUser.getOrgId());
                JSONResult<List<OrganizationRespDTO>> result =
                        organizationFeignClient.queryOrgByParam(queryDTO);
                List<Long> ids = result.getData().stream().map(c -> c.getId()).collect(Collectors.toList());
                dto.setDeptIds(ids);
            }
        }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
            //副总查看当前事业部
            dto.setDeptIds(new ArrayList<>(Arrays.asList(curLoginUser.getOrgId())));
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode)){
            //总监查看自己组
            dto.setTeleGroupId(curLoginUser.getOrgId());

        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员可以查看全部
            if(null!=dto.getTeleDeptId()){
                dto.setDeptIds(new ArrayList<>(Arrays.asList(dto.getTeleDeptId())));
            }
        }else{
            //other
            dto.setTeleGroupId(curLoginUser.getId());
        }
    }
}
