package com.kuaidao.manageweb.controller.statistics.busTeleDistribution;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.statistics.busTeleDistribution.BusTeleDistributionFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.stastics.dto.base.BaseBusinessDto;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商务报表 / 业绩报表 / 电销组织分布表
 */
@RequestMapping("/busTeleDistribution")
@Controller
public class BusTeleDistributionController extends BaseStatisticsController {

    @Autowired
    private BusTeleDistributionFeignClient busTeleDistributionFeignClient;


    /**
     * 一级页面跳转
     */
    @RequestMapping("/toTeleOrganizeDistributed")
    public String toTeleOrganizationDistributed(HttpServletRequest request) {
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportBusPerformance/teleOrganizeDistributed";
    }

    /**
     * 二级页面跳转
     */
    @RequestMapping("/toTeleOrganizeDistributedDetail")
    public String toTeleOrganizationDistributedDetail(Long busAreaId,Long businessGroupId,
                                                      Long startTime,Long endTime,Long businessManagerId,Long groupId,Long teleDeptId,BaseBusQueryDto baseBusQueryDto ,HttpServletRequest request) {
        initParam(busAreaId,businessGroupId,
                startTime,endTime,businessManagerId,groupId,teleDeptId,request);
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportBusPerformance/teleOrganizeDistributedDetail";
    }

    /**
     *
     * 一级页面查询（不分页）
     */
    @RequestMapping("/exportOneAllList")
    public void exportOneAllList(HttpServletResponse response, @RequestBody BaseBusQueryDto baseBusQueryDto) throws IOException {
        initAuth(baseBusQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTitleList(1));
        JSONResult<Map<String, Object>> result =   busTeleDistributionFeignClient.getOneAllList(baseBusQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<BaseBusinessDto> orderList = JSON.parseArray(listTxt, BaseBusinessDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        List<BaseBusinessDto>  sumReadd = JSON.parseArray(totalDataStr, BaseBusinessDto.class);
        buildList(dataList, orderList,sumReadd,1);
        addSerialNum(dataList);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "商务报表电销组织分布表" +baseBusQueryDto.getStartTime()+"-"+baseBusQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    /**
     *
     * 一级页面查询（分页）
     */
    @RequestMapping("/getOnePageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getOnePageList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        initAuth(baseBusQueryDto);
        JSONResult<Map<String, Object>> onePageList = busTeleDistributionFeignClient.getOnePageList(baseBusQueryDto);
        return onePageList;
    }

    /**
     *
     * 二级页面查询（不分页）
     */
    @RequestMapping("/exportTwoAllList")
    public void exportTwoAllList(HttpServletResponse response, @RequestBody BaseBusQueryDto baseBusQueryDto) throws IOException {
        initAuth(baseBusQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTitleList(2));
        JSONResult<Map<String, Object>> result =  busTeleDistributionFeignClient.getTwoAllList(baseBusQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<BaseBusinessDto> orderList = JSON.parseArray(listTxt, BaseBusinessDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        List<BaseBusinessDto> sumReadd = JSON.parseArray(totalDataStr, BaseBusinessDto.class);
        buildList(dataList, orderList,sumReadd,2);
        addSerialNum(dataList);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "商务报表电销组织分布表" +baseBusQueryDto.getStartTime()+"-"+baseBusQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    /**
     *
     * 二级页面查询（分页）
     */
    @RequestMapping("/getTwoPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getTwoPageList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        initAuth(baseBusQueryDto);
        JSONResult<Map<String, Object>> twoPageList = busTeleDistributionFeignClient.getTwoPageList(baseBusQueryDto);
        return twoPageList;
    }
    private List<Object> getTitleList(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("餐饮集团");
        headTitleList.add("电销事业部");
        if(type == 2){
            headTitleList.add("电销组");
        }
        headTitleList.add("首访数");
        headTitleList.add("签约数");
        headTitleList.add("签约率");
        headTitleList.add("净业绩金额");
        headTitleList.add("签约单笔");
        return headTitleList;
    }

    private void addTotalExportData(BaseBusinessDto ra, List<List<Object>> dataList, Integer type) {
        List<Object> curList = new ArrayList<>();
        curList.add("合计");
        curList.add("");
        if(type.equals(2)){
            curList.add("");
        }
        curList.add(ra.getFirstVisitNum());
        curList.add(ra.getSignNum());
        curList.add(ra.getSignRate());
        curList.add(ra.getAmount());
        curList.add(ra.getSignSingle());
        dataList.add(curList);
    }

    private void buildList(List<List<Object>> dataList, List<BaseBusinessDto> sourceDataList,List<BaseBusinessDto> sumList,
                           Integer type) {
        Map<String, BaseBusinessDto> sumMap = sumList.stream().collect(Collectors.toMap(BaseBusinessDto::getGroupId, Function.identity()));
        TreeMap<String, List<BaseBusinessDto>> sourceDataListTreeMap =
                sourceDataList.stream().collect(Collectors.groupingBy(BaseBusinessDto::getGroupId, TreeMap::new, Collectors.toList()));
        //添加总合计
        addTotalExportData(sumMap.get("99999"),dataList,type);
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = curLoginUser.getRoleList().get(0).getRoleCode();
        sourceDataListTreeMap.forEach((key,value)->{
            //封装数据
            buildData(dataList,value,type);
            if(!RoleCodeEnum.SWJL.name().equals(roleCode)
                    && !RoleCodeEnum.SWZJ.name().equals(roleCode)) {
                //添加每组合计
                addTotalExportData(sumMap.get(key), dataList, type);
            }
        });
    }

    private void buildData(List<List<Object>> dataList,List<BaseBusinessDto> list,Integer type){
        for(int j = 0; j<list.size(); j++){
            List<Object> curList = new ArrayList<>();
            BaseBusinessDto raInner = list.get(j);
            curList.add(raInner.getGroupName());
            curList.add(raInner.getSellDepartName());
            if(type == 2){
                curList.add(raInner.getTeleGroupName());
            }
            curList.add(raInner.getFirstVisitNum());
            curList.add(raInner.getSignNum());
            curList.add(raInner.getSignRate());
            curList.add(raInner.getAmount());
            curList.add(raInner.getSignSingle());
            dataList.add(curList);
        }
    }

    private void addSerialNum(List<List<Object>> dataList){
        int j = 0;
        for(int i=0;i<dataList.size();i++){
            List<Object> objects = dataList.get(i);
            if(i == 1){
                objects.add(0,"");
            }else if(i > 1){
                j++;
                objects.add(0,j);
            }
        }
    }
    private void initParam(Long busAreaId,Long businessGroupId,
                           Long startTime,Long endTime,Long businessManagerId,Long groupId,Long teleDeptId,HttpServletRequest request){
        BaseBusQueryDto baseBusQueryDto = new BaseBusQueryDto();
        baseBusQueryDto.setBusAreaId(busAreaId);
        baseBusQueryDto.setBusinessGroupId(businessGroupId);
        baseBusQueryDto.setStartTime(startTime);
        baseBusQueryDto.setEndTime(endTime);
        baseBusQueryDto.setBusinessManagerId(businessManagerId);
        baseBusQueryDto.setGroupId(groupId);
        baseBusQueryDto.setTeleDeptId(teleDeptId);
        request.setAttribute("baseBusQueryDto",baseBusQueryDto);
    }
}
