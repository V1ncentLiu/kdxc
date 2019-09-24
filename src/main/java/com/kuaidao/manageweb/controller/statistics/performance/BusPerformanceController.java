package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: guhuitao
 * @create: 2019-09-23 16:06
 * 商务报表-业绩报表-商务业绩表
 **/
@Controller
@RequestMapping("/busperformance")
public class BusPerformanceController extends BaseStatisticsController {

    @Autowired
    private OrganizationFeignClient organizationFeignClient;


    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request){
        initBugOrg(request);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            initBaseDto(request,null,curLoginUser.getOrgId(),curLoginUser.getId(),null,null,null,null);
            return "busPerformance/performanceManager";
        }
        return "busPerformance/performanceGroup";
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
        initBugOrg(request);
        //资源类别
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportPerformance/performanceManager";
    }




    public void initBaseDto(HttpServletRequest request,Long deptId,Long groupId,Long saleId,
                            Integer category,String searchText,Long startTime,Long endTime){

    }
}
