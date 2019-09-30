package com.kuaidao.manageweb.controller.statistics.busGroupRanking;


import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.busGroupRanking.BusGroupRankingFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * / 商务报表 / 业绩报表 / 集团项目业绩表
 */
@Slf4j
@RequestMapping("/busGroupRanking")
@Controller
public class BusGroupRankingController extends BaseStatisticsController {

    @Autowired
    private BusGroupRankingFeignClient busGroupRankingFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;


    /**
     * 一级页面跳转
     */
    @RequestMapping("/toGroupProjectPerformance")
    public String toGroupProjectPerformance(HttpServletRequest request) {
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportBusPerformance/groupProjectPerformance";
    }

    /**
     * 二级页面跳转
     */
    @RequestMapping("/toGroupProjectPerformanceDetail ")
    public String toGroupProjectPerformanceDetail(HttpServletRequest request) {
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportBusPerformance/groupProjectPerformanceDetail";
    }


    /**
     * 一级页面分页查询
     */
    @RequestMapping("/getOnePageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getOneBusGroupRankingPageList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        JSONResult<Map<String, Object>> oneBusGroupRankingPageList = busGroupRankingFeignClient.getOneBusGroupRankingPageList(baseBusQueryDto);
        return oneBusGroupRankingPageList;
    }
    /**
     * 一级页面查询全部
     */
    @RequestMapping("/getOneList")
    public JSONResult<Map<String,Object>> getOneBusGroupRankingList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        return busGroupRankingFeignClient.getOneBusGroupRankingList(baseBusQueryDto);
    }
    /**
     * 二级页面分页查询
     */
    @RequestMapping("/getTwoPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getTwoBusGroupRankingPageList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        return busGroupRankingFeignClient.getTwoBusGroupRankingPageList(baseBusQueryDto);
    }
    /**
     * 二级页面查询全部
     */
    @RequestMapping("/getTwoList")
    public JSONResult<Map<String,Object>> getTwoBusGroupRankingList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        return busGroupRankingFeignClient.getTwoBusGroupRankingList(baseBusQueryDto);
    }
    private void initOrgList(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        //商务组
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode)){
            busGroupReqDTO.setParentId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode) || RoleCodeEnum.SWJL.name().equals(roleCode)){
            busGroupReqDTO.setId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员可以查看全部
            log.info("管理员登录");
        }else{
            //other 没权限
            busGroupReqDTO.setId(-1l);
        }
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> listJSONResult = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        List<OrganizationRespDTO> data = listJSONResult.getData();
        request.setAttribute("busGroupList",data);

        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());

        //餐饮集团
        JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        request.setAttribute("companyList", listNoPage.getData());
    }

}
