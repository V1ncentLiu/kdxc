package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.dwOrganization.DwOrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.telePerformanceRank.TelePerformanceRankFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.constant.ReportExportEnum;
import com.kuaidao.stastics.dto.dwOrganizationQueryDTO.DwOrganizationQueryDTO;
import com.kuaidao.stastics.dto.telePerformanceRank.TelePerformanceRankDto;
import com.kuaidao.stastics.dto.telePerformanceRank.TelePerformanceRankQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: guhuitao
 * @create: 2019-08-22 18:05
 * 业绩排名
 **/
@Controller
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private TelePerformanceRankFeignClient telePerformanceRankFeignClient;
    @Autowired
    private DwOrganizationFeignClient dwOrganizationFeignClient;

    /**
     * 事业部业绩排名
     * @param request
     * @return
     */
    @RequestMapping("/deptList")
    public String deptList(HttpServletRequest request, @RequestParam(required = false) Integer type){
        initOrg(request);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        request.setAttribute("type", type);
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            request.setAttribute("curUserId",curLoginUser.getId()+"");
            return "reportPerformance/rankingPerformanceManager";
        }else if(RoleCodeEnum.DXZJ.name().equals(roleCode)){
            return "reportPerformance/rankingPerformanceGroup";
        }
        return "reportPerformance/rankingPerformanceDept";
    }

    /**
     * 电销组业绩排名
     * @param request
     * @return
     */
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request){
        initOrg(request);
        return "reportPerformance/rankingPerformanceGroup";
    }

    /**
     * 电销经理业绩排名
     * @param request
     * @return
     */
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request){
        initOrg(request);
        return "reportPerformance/rankingPerformanceManager";
    }


    private void initOrg(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();

        //查询所有电销组
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<Long> orgIdList = queryOrgByParam.getData().parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
        DwOrganizationQueryDTO dto = new DwOrganizationQueryDTO();
        dto.setOrgIdList(orgIdList);
        dto.setSelectCode("DQ");
        dto.setBusinessLine(curLoginUser.getBusinessLine());
        JSONResult<List<OrganizationRespDTO>> dwOrganization = dwOrganizationFeignClient.getDwOrganization(dto);
        request.setAttribute("areaList",dwOrganization.getData());
        request.setAttribute("roleCode",roleCode+"");

        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        // 电销事业部
        orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DZSYB);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationRespDTO>> orgDeptJson =
                organizationFeignClient.queryOrgByParam(orgDto);
        if (orgDeptJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("deptList", orgDeptJson.getData());
        }
        // 查询电销组
        orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationRespDTO>> orgJson =
                organizationFeignClient.queryOrgByParam(orgDto);
        if (orgJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("teleGroupList", orgJson.getData());
        }
    }

    /**
     * 导出
     */
    @PostMapping("/exportPerformanceRankList")
    public void exportPerformanceRankList(@RequestBody TelePerformanceRankQueryDto telePerformanceRankQueryDto, HttpServletResponse response) throws IOException {
        initAuth(telePerformanceRankQueryDto);
        String selectType = telePerformanceRankQueryDto.getSelectType();
        JSONResult<List<TelePerformanceRankDto>> performanceRankList = telePerformanceRankFeignClient.getPerformanceRankList(telePerformanceRankQueryDto);
        String[] columnTitles = ReportExportEnum.getColumnTitles(selectType);
        String[] columnValue = ReportExportEnum.getColumnValue(selectType);
        if(null!=performanceRankList && "0".equals(performanceRankList.getCode()) && !performanceRankList.getData().isEmpty()){
            TelePerformanceRankDto[] dtos = performanceRankList.getData().isEmpty()?new TelePerformanceRankDto[]{}:performanceRankList.getData().toArray(new TelePerformanceRankDto[0]);
            Workbook wb = ExcelUtil.createWorkBook(dtos, columnValue, columnTitles);
            String name = MessageFormat.format("电销报表_业绩排名_{0}.xlsx", ReportExportEnum.getValue(selectType));
            response.addHeader("Content-Disposition",
                    "attachment;filename=\"" + name + "\"");
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        }
    }

    /**
     * 分页查询排行
     */
    @PostMapping("/getPerformanceRankPage")
    @ResponseBody
    JSONResult<PageBean<TelePerformanceRankDto>> getPerformanceRankPage(@RequestBody TelePerformanceRankQueryDto telePerformanceRankQueryDto){
        initAuth(telePerformanceRankQueryDto);
        return telePerformanceRankFeignClient.getPerformanceRankPage(telePerformanceRankQueryDto);
    }

    /**
     * 加载初始化数据权限
     */
    public void initAuth(TelePerformanceRankQueryDto telePerformanceRankQueryDto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        telePerformanceRankQueryDto.setBusinessLine(curLoginUser.getBusinessLine());
    }

}
