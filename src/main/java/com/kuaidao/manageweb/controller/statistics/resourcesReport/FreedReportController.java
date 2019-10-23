package com.kuaidao.manageweb.controller.statistics.resourcesReport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.controller.statistics.BaseStatisticsController;
import com.kuaidao.manageweb.feign.statistics.resourceFreeReceive.ResourceFreeReceiveFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.resourceFreeReceive.ResourceFreeReceiveDto;
import com.kuaidao.stastics.dto.resourceFreeReceive.ResourceFreeReceiveQueryDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: guhuitao
 * @create: 2019-08-30 16:59
 * 资源释放统计表
 **/
@Controller
@RequestMapping("/freedReport")
public class FreedReportController extends BaseStatisticsController {

    @Autowired
    private ResourceFreeReceiveFeignClient resourceFreeReceiveFeignClient;

    private static final Integer GROUP = 1;
    private static final Integer PERSON = 2;
    private static final Integer DAY = 3;



    /**
     * 事业部统计
     * @param request
     * @return
     */
    @RequestMapping("/deptList")
    public String deptList(HttpServletRequest request){
        initSaleDept(request);
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.DXCYGW.name().equals(roleCode)){
            return "reportResources/resourceFreedGroup";
        }
        return "reportResources/resourceFreedDept";
    }

    /**
     * 电销组统计
     * @param request
     * @return
     */
    @RequestMapping("/groupList")
    public String teamList(HttpServletRequest request,Long teleDeptId,Long teleGroupId,Long category,Long teleSaleId,Long startTime,Long endTime){
        pageParams(teleDeptId,teleGroupId,category,teleSaleId,startTime,endTime,request);
        initSaleDept(request);
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportResources/resourceFreedGroup";
    }

    /**
     * 电销顾问统计
     * @param request
     * @return
     */
    @RequestMapping("/managerList")
    public String managerList(HttpServletRequest request,Long teleDeptId,Long teleGroupId,Long category,Long teleSaleId,Long startTime,Long endTime){
        pageParams(teleDeptId,teleGroupId,category,teleSaleId,startTime,endTime,request);
        initSaleDept(request);
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        return "reportResources/resourceFreedManager";
    }


    /**
     * 组分页
     */
    @RequestMapping("/getGroupPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getGroupPageList(@RequestBody ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto){
        return resourceFreeReceiveFeignClient.getGroupPageList(resourceFreeReceiveQueryDto);
    }

    /**
     * 组不分页
     */
    @RequestMapping("/exportGroupAllList")
    public void exportGroupAllList(@RequestBody ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto,HttpServletResponse response) throws IOException {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getResourceFreedTitleList(GROUP));
        JSONResult<Map<String, Object>> result = resourceFreeReceiveFeignClient.getGroupAllList(resourceFreeReceiveQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<ResourceFreeReceiveDto> orderList = JSON.parseArray(listTxt, ResourceFreeReceiveDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        ResourceFreeReceiveDto sumReadd = JSON.parseObject(totalDataStr, ResourceFreeReceiveDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,GROUP);
        buildList(dataList, orderList,GROUP);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "资源释放领取统计表" +resourceFreeReceiveQueryDto.getStartTime()+"-"+resourceFreeReceiveQueryDto.getEndTime() + ".xlsx";
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
     * 二级页面查询人（不分页）
     */
    @RequestMapping("/exportPersonAllList")
    public void  exportPersonAllList(@RequestBody ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto,HttpServletResponse response) throws IOException {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getResourceFreedTitleList(PERSON));
        JSONResult<Map<String, Object>> result = resourceFreeReceiveFeignClient.getPersonAllList(resourceFreeReceiveQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<ResourceFreeReceiveDto> orderList = JSON.parseArray(listTxt, ResourceFreeReceiveDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        ResourceFreeReceiveDto sumReadd = JSON.parseObject(totalDataStr, ResourceFreeReceiveDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,PERSON);
        buildList(dataList, orderList,PERSON);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "资源释放领取统计表" +resourceFreeReceiveQueryDto.getStartTime()+"-"+resourceFreeReceiveQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
    }
    /**
     *
     * 二级页面查询人（分页）
     */
    @RequestMapping("/getPersonPageList")
    @ResponseBody
    public JSONResult<Map<String, Object>> getPersonPageList(@RequestBody ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto){
        return resourceFreeReceiveFeignClient.getPersonPageList(resourceFreeReceiveQueryDto);
    }
    /**
     *
     * 三级页面查询人+天（不分页）
     */
    @RequestMapping("/exportPersonDayAllList")
    public void  exportPersonDayAllList(@RequestBody ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto,HttpServletResponse response) throws IOException {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getResourceFreedTitleList(DAY));
        JSONResult<Map<String, Object>> result = resourceFreeReceiveFeignClient.getPersonDayAllList(resourceFreeReceiveQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<ResourceFreeReceiveDto> orderList = JSON.parseArray(listTxt, ResourceFreeReceiveDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        ResourceFreeReceiveDto sumReadd = JSON.parseObject(totalDataStr, ResourceFreeReceiveDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList,DAY);
        buildList(dataList, orderList,DAY);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "资源释放领取统计表" +resourceFreeReceiveQueryDto.getStartTime()+"-"+resourceFreeReceiveQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
    }
    /**
     *
     * 三级页面查询人+天（分页）
     */
    @RequestMapping("/getPersonDayPageList")
    @ResponseBody
    public JSONResult<Map<String, Object>> getPersonDayPageList(@RequestBody ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto){
        return resourceFreeReceiveFeignClient.getPersonDayPageList(resourceFreeReceiveQueryDto);
    }

    private List<Object> getResourceFreedTitleList(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        if(type.equals(GROUP)){
            headTitleList.add("电销组");
        }
        if(type.equals(PERSON)){
            headTitleList.add("电销顾问");
        }
        if(type.equals(DAY)){
            headTitleList.add("日期");
        }
        headTitleList.add("不跟进释放公共资源数");
        headTitleList.add("其他释放公共资源数");
        headTitleList.add("无效释放数");
        headTitleList.add("总释放数");
        if(type.equals(GROUP)){
            headTitleList.add("总监领取数");
        }
        headTitleList.add("顾问领取数");
        if(type.equals(GROUP)){
            headTitleList.add("总领取数");
        }
        headTitleList.add("释放/领取比例");
        return headTitleList;
    }

    private void addTotalExportData(ResourceFreeReceiveDto ra, List<List<Object>> dataList, Integer type) {
        List<Object> curList = new ArrayList<>();
        curList.add("");
        curList.add("合计");
        curList.add(ra.getUnfollowCount());
        curList.add(ra.getOtherCount());
        curList.add(ra.getUselessCount());
        curList.add(ra.getTotalFreeCount());
        if(type.equals(GROUP)){
            curList.add(ra.getTeleManagerCount());
        }
        curList.add(ra.getTeleSaleCount());
        if(type.equals(GROUP)){
            curList.add(ra.getTotalReceiveCount());
        }
        curList.add(ra.getFreeRate());
        dataList.add(curList);
    }

    private void buildList(List<List<Object>> dataList, List<ResourceFreeReceiveDto> orderList, Integer type) {
        for(int i = 0; i<orderList.size(); i++){
            ResourceFreeReceiveDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            if(type.equals(GROUP)){
                curList.add(ra.getOrgName());
            }
            if(type.equals(PERSON)){
                curList.add(ra.getUserName());
            }
            if(type.equals(DAY)){
                curList.add(ra.getDateId());
            }
            curList.add(ra.getUnfollowCount());
            curList.add(ra.getOtherCount());
            curList.add(ra.getUselessCount());
            curList.add(ra.getTotalFreeCount());
            if(type.equals(GROUP)){
                curList.add(ra.getTeleManagerCount());
            }
            curList.add(ra.getTeleSaleCount());
            if(type.equals(GROUP)){
                curList.add(ra.getTotalReceiveCount());
            }
            curList.add(ra.getFreeRate());
            dataList.add(curList);
        }
    }


    private void pageParams(Long teleDeptId,Long teleGroupId,Long category,Long teleSaleId,Long startTime,Long endTime,HttpServletRequest request){
        ResourceFreeReceiveQueryDto resourceFreeReceiveQueryDto = new ResourceFreeReceiveQueryDto();
        resourceFreeReceiveQueryDto.setTeleGroupId(teleGroupId);
        resourceFreeReceiveQueryDto.setStartTime(startTime);
        resourceFreeReceiveQueryDto.setEndTime(endTime);
        resourceFreeReceiveQueryDto.setTeleSaleId(teleSaleId);
        resourceFreeReceiveQueryDto.setTeleDeptId(teleDeptId);
        resourceFreeReceiveQueryDto.setCategory(category);
        request.setAttribute("resourceFreeReceiveQueryDto",resourceFreeReceiveQueryDto);
    }


}
