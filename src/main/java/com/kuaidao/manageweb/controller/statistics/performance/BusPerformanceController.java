package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.busPerformance.BusPerformanceClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.stastics.dto.busPerformance.BusPerformanceDto;
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
import java.util.*;

/**
 * @author: guhuitao
 * @create: 2019-09-23 16:06
 * 商务报表-业绩报表-商务业绩表
 **/
@Controller
@RequestMapping("/busperformance")
public class BusPerformanceController extends BaseStatisticsController {

    private static Logger logger = LoggerFactory.getLogger(BusPerformanceController.class);
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private BusPerformanceClient busPerformanceClient;

    @RequiresPermissions("statistics:busperformance:view")
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request,@RequestParam(required = false) Integer type){
        initBugOrg(request);
        // 签约店型
        request.setAttribute("shopTypeList", getDictionaryByCode(Constants.PROJECT_SHOPTYPE));
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        Integer line=curLoginUser.getBusinessLine();
        request.setAttribute("line",line);
        //小物种
        if(line!=null && 3==line){
            proType(request);
        }
        request.setAttribute("type",type);
        if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            initBaseDto(request,null,curLoginUser.getOrgId(),curLoginUser.getId(),null,null,null,null,null);
            return "reportBusPerformance/performanceManager";
        }
        return "reportBusPerformance/performanceGroup";
    }

    /**
     * 商务经理业绩排名
     * @param request
     * @return
     */
    @RequiresPermissions("statistics:busperformance:view")
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request,Long busAreaId,Long businessGroupId,Long businessManagerId,Integer payTpye,
                              Integer signShop,Long projectId,Long startTime,Long endTime){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        //3 小物种
        Integer line=curLoginUser.getBusinessLine();
        request.setAttribute("line",line);
        //小物种
        if(null!=line && 3==line){
            proType(request);
        }
        // 签约店型
        request.setAttribute("shopTypeList", getDictionaryByCode(Constants.PROJECT_SHOPTYPE));
        if(null!=businessGroupId){
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            IdEntity id=new IdEntity();
            id.setId(businessGroupId+"");
            JSONResult<OrganizationDTO> result=organizationFeignClient.queryOrgById(id);
            if("0".equals(result.getCode())){
                busAreaId=result.getData().getParentId();
            }
        }

        initBaseDto(request,busAreaId,businessGroupId,businessManagerId,payTpye,projectId,signShop,startTime,endTime);
        initBugOrg(request);
        //资源类别
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportBusPerformance/performanceManager";
    }


    /**
     * 一级页面查询
     * @param dto
     * @return
     */
    @RequiresPermissions("statistics:busperformance:view")
    @RequestMapping("queryByPage")
    public @ResponseBody JSONResult<Map<String,Object>> queryByPage(@RequestBody BaseBusQueryDto dto){
        initParams(dto);
       return busPerformanceClient.queryByPage(dto);
    }

    /**
     * 二级页面分页查询
     * @param dto
     * @return
     */
    @RequiresPermissions("statistics:busperformance:view")
    @RequestMapping("/queryBusPage")
    public @ResponseBody JSONResult<Map<String,Object>> queryList(@RequestBody BaseBusQueryDto dto){
        initParams(dto);
        String roleCode=super.getRoleCode();
        if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            dto.setBusinessGroupId(null);
            dto.setBusAreaId(null);
        }
        return  busPerformanceClient.queryBusPage(dto);
    }


    /**
     * 一级页面导出
     * @param dto
     * @param response
     */
    @RequiresPermissions("statistics:busperformance:export")
    @RequestMapping("/export")
    @ResponseBody
    public void export(@RequestBody BaseBusQueryDto dto, HttpServletResponse response){
        try{
            initParams(dto);
            JSONResult<List<BusPerformanceDto>> json=busPerformanceClient.queryList(dto);
            if(null!=json && "0".equals(json.getCode())){
                BusPerformanceDto[] dtos = json.getData().isEmpty()?new BusPerformanceDto[]{}:json.getData().toArray(new BusPerformanceDto[0]);
                String[] keys = {"businessGroupName","firstSign","signNum","signRate","amount","firstMoney","signMoney","signOrderNum","payCount","handselNum","handselMoney","totalNum","totalMoney","tailNum","tailMoney","handselRate","tailRate"};

                String[] hader = {"商务组","首访数","签约数","签约率","净业绩金额","首访单笔","签约单笔","签约单数","付款笔数","定金量","定金金额","全款量","全款金额","尾款量","尾款金额","定金占比","尾款回收率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("商务业绩表_{0}_{1}.xlsx", "" + dto.getStartTime(), dto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" busPerformance export error:",e);
        }
    }

    /**
     * 二级页面导出
     * @param dto
     * @param response
     */
    @RequiresPermissions("statistics:busperformance:export")
    @RequestMapping("/exportBus")
    @ResponseBody
    public void exportBus(@RequestBody BaseBusQueryDto dto, HttpServletResponse response){
        try{
            initParams(dto);
            String roleCode=super.getRoleCode();
            if(RoleCodeEnum.SWJL.name().equals(roleCode)){
                dto.setBusinessGroupId(null);
                dto.setBusAreaId(null);
            }
            JSONResult<List<BusPerformanceDto>> json=busPerformanceClient.queryBusList(dto);
            if(null!=json && "0".equals(json.getCode())){
                BusPerformanceDto[] dtos = json.getData().isEmpty()?new BusPerformanceDto[]{}:json.getData().toArray(new BusPerformanceDto[0]);
                String[] keys = {"businessGroupName","businessManagerName","firstSign","signNum","signRate","amount","firstMoney","signMoney","signOrderNum","payCount","handselNum","handselMoney","totalNum","totalMoney","tailNum","tailMoney","handselRate","tailRate"};

                String[] hader = {"商务组","商务经理","首访数","签约数","签约率","净业绩金额","首访单笔","签约单笔","签约单数","付款笔数","定金量","定金金额","全款量","全款金额","尾款量","尾款金额","定金占比","尾款回收率"};
                Workbook wb = ExcelUtil.createWorkBook(dtos, keys, hader);
                String name = MessageFormat.format("商务经理业绩表_{0}_{1}.xlsx", "" + dto.getStartTime(), dto.getEndTime() + "");
                response.addHeader("Content-Disposition",
                        "attachment;filename=\"" + name + "\"");
                response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
                response.setContentType("application/octet-stream");
                ServletOutputStream outputStream = response.getOutputStream();
                wb.write(outputStream);
                outputStream.close();
            }
        }catch (Exception e){
            logger.error(" busPerformance export error:",e);
        }
    }


    public void initBaseDto(HttpServletRequest request,Long areaId,Long groupId,Long saleId,
                            Integer payType,Long projectId,Integer signShop,Long startTime,Long endTime){
        BaseBusQueryDto dto=new BaseBusQueryDto();
        dto.setBusAreaId(areaId);
        dto.setBusinessGroupId(groupId);
        dto.setBusinessManagerId(saleId);
        dto.setPayType(payType);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setProjectId(projectId);
        dto.setSignShop(signShop);
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

    //初始化饮品类型
    public void proType(HttpServletRequest request){
        List<Map<String,Object>> prolist=new ArrayList<>();
        //品类字典
        Map<String,Object> par=new HashMap<>();
        par.put("name","饮品");
        par.put("value",1);
        prolist.add(par);

        Map<String,Object> par1=new HashMap<>();
        par1.put("name","非饮品");
        par1.put("value",0);
        prolist.add(par1);
        request.setAttribute("prolist",prolist);
    }

}
