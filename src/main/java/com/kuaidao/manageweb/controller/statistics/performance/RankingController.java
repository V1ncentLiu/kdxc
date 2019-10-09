package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 事业部业绩排名
     * @param request
     * @return
     */
    @RequestMapping("/deptList")
    public String deptList(HttpServletRequest request){
        initOrg(request);
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

        //查询招商中心
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.ZSZX);
        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("areaList",queryOrgByParam.getData());
        request.setAttribute("curUserId",curLoginUser.getId()+"");
        request.setAttribute("roleCode",roleCode);
    }


}
