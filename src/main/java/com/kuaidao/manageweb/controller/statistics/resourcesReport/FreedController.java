package com.kuaidao.manageweb.controller.statistics.resourcesReport;

import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.resourceAllocation.StatisticsFreeFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.resourceFree.ResourceFreeDto;
import com.kuaidao.stastics.dto.resourceFree.ResourceFreeQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
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
import java.util.stream.Collectors;

/**
 * @author: guhuitao
 * @create: 2019-08-22 17:50
 * 资源释放原因
 **/
@Controller
@RequestMapping("/resourceFreed")
public class FreedController {

    private static Logger logger = LoggerFactory.getLogger(FreedController.class);

    @Autowired
    private StatisticsFreeFeignClient statisticsFreeFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    /**
     * 资源释放页面
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:resourceFreed:view")
    @RequestMapping("/releasePie")
    public String freedView(HttpServletRequest request){
        initOrg(request);
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
       return "reportResources/releasePie";
    }

    /**
     * 查询资源释放原因数据
     * @param dto
     * @return
     */
    @RequiresPermissions("statistics:resourceFreed:view")
    @RequestMapping("/queryList")
    public @ResponseBody JSONResult<List<ResourceFreeDto>> queryList(@RequestBody ResourceFreeQueryDto dto){
        initParams(dto);
        return statisticsFreeFeignClient.queryList(dto);
    }

    /**
     * 根据搜索条件查询并导出数据
     * @param dto
     * @param response
     */
    //jia 日志
    @RequiresPermissions("statistics:resourceFreed:export")
    @RequestMapping("/export")
    public @ResponseBody void export(@RequestBody ResourceFreeQueryDto dto, HttpServletResponse response){
        try {
            // 参数打印。。。。
            initParams(dto);
            JSONResult<List<ResourceFreeDto>> result = statisticsFreeFeignClient.queryList(dto);
            if ("0".equals(result.getCode())) {
                ResourceFreeDto[] dtos = result.getData().isEmpty()?new ResourceFreeDto[]{}:result.getData().toArray(new ResourceFreeDto[0]);
                String[] keys = {"name", "value"};
                String[] hader = {"原因", "数量"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("资源释放原因统计表_{0}_{1}.xlsx", "" + dto.getStartTime(), dto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 初始化页面参数
     * @param request
     */
    public void initOrg(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();

        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        //查询电销事业部
        queryDTO.setOrgType(OrgTypeConstant.DZSYB);
        if(RoleCodeEnum.DXZJL.name().equals(roleCode)){
            queryDTO.setParentId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.DXFZ.name().equals(roleCode)){
            queryDTO.setId(curLoginUser.getOrgId());
            request.setAttribute("deptId",curLoginUser.getOrgId()+"");
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode) || RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            IdEntity idEntity=new IdEntity();
            idEntity.setId(curLoginUser.getOrgId().toString());
            JSONResult<OrganizationDTO> jsonResult= organizationFeignClient.queryOrgById(idEntity);
            queryDTO.setId(jsonResult.getData().getParentId());
            request.setAttribute("deptId",jsonResult.getData().getParentId()+"");
            request.setAttribute("teleGroupId",curLoginUser.getOrgId()+"");
            if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
                request.setAttribute("teleSaleId",curLoginUser.getId()+"");
            }
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员可以查看全部
        }else{
            //other 没权限
            queryDTO.setId(-1l);
        }
        request.setAttribute("roleCode",roleCode);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("deptList",queryOrgByParam.getData());

    }

    /**
     * 根据角色设置权限参数
     * @param dto
     */
    public void initParams(ResourceFreeQueryDto dto){
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
            //总监查看上去组
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



    /**
     * 查询字典表
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode =
                dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null
                && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }


}
