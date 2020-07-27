package com.kuaidao.manageweb.controller.statistics.performance;

import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.statistics.busPerformanceRank.BusPerformanceRankFeignClinet;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.constant.ReportExportEnum;
import com.kuaidao.stastics.dto.busPerformanceRank.BusPerformanceRankDto;
import com.kuaidao.stastics.dto.busPerformanceRank.BusPerformanceRankQueryDto;
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

/**
 * @author: guhuitao
 * @create: 2019-09-23 15:39
 * 商务报表-业绩表-业绩排名
 **/
@Controller
@RequestMapping("/busRanking")
public class BusRankingController extends BaseStatisticsController {

  @Autowired
  private BusPerformanceRankFeignClinet busPerformanceRankFeignClinet;

    /**
     * 商务组排名
     * @param request
     * @return
     */
    @RequestMapping("/busRankingList")
    public String  busTeamList(HttpServletRequest request,@RequestParam(required = false) Integer type){
        initSWDQByBusiness(request);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        request.setAttribute("type",type);
        if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            request.setAttribute("curUserId",curLoginUser.getId()+"");
            return "reportBusPerformance/rankingManager";
        }else{
            return "reportBusPerformance/rankingGroup";
        }
    }

    /**
     * 商务经理排名
     * @param request
     * @return
     */
    @RequestMapping("/busManageRankingList")
    public String busSaleList(HttpServletRequest request){
        initSWDQByBusiness(request);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            request.setAttribute("curUserId",curLoginUser.getId()+"");
        }
        return "reportBusPerformance/rankingManager";
    }

    /**
     * 分页查询排行
     */
    @PostMapping("/getBusPerformanceRankPage")
    @ResponseBody
    JSONResult<PageBean<BusPerformanceRankDto>> getBusPerformanceRankPage(@RequestBody BusPerformanceRankQueryDto busPerformanceRankQueryDto){
        initAuth(busPerformanceRankQueryDto);
        return busPerformanceRankFeignClinet.getBusPerformanceRankPage(busPerformanceRankQueryDto);
    }

    /**
     * 导出
     */
    @PostMapping("/exportBusPerformanceRankList")
    public void exportPerformanceRankList(@RequestBody BusPerformanceRankQueryDto busPerformanceRankQueryDto, HttpServletResponse response) throws IOException {
        initAuth(busPerformanceRankQueryDto);
        String selectType = busPerformanceRankQueryDto.getSelectType();
        JSONResult<List<BusPerformanceRankDto>> performanceRankList = busPerformanceRankFeignClinet.getBusPerformanceRankList(busPerformanceRankQueryDto);
        String[] columnTitles = ReportExportEnum.getColumnTitles(selectType);
        String[] columnValue = ReportExportEnum.getColumnValue(selectType);
        if(null!=performanceRankList && "0".equals(performanceRankList.getCode()) && !performanceRankList.getData().isEmpty()){
            BusPerformanceRankDto[] dtos = performanceRankList.getData().isEmpty()?new BusPerformanceRankDto[]{}:performanceRankList.getData().toArray(new BusPerformanceRankDto[0]);
            Workbook wb = ExcelUtil.createWorkBook(dtos, columnValue, columnTitles);
            String name = MessageFormat.format("商务_业绩排名_{0}.xlsx", ReportExportEnum.getValue(selectType));
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
     * 加载初始化数据权限
     */
    public void initAuth(BusPerformanceRankQueryDto busPerformanceRankQueryDto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        busPerformanceRankQueryDto.setBusinessLine(curLoginUser.getBusinessLine());
    }


}





