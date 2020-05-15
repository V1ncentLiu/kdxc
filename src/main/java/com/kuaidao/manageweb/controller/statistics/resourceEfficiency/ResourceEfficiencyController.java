package com.kuaidao.manageweb.controller.statistics.resourceEfficiency;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.resourceEfficiency.ResourceEfficiencyFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.resourceEfficiency.ResourceEfficiencyAllDataDto;
import com.kuaidao.stastics.dto.resourceEfficiency.ResourceEfficiencyQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserDataAuthReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源接通有效率
 */
@Slf4j
@Controller
@RequestMapping("/statistics/resourceEfficiency")
public class ResourceEfficiencyController {

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private ResourceEfficiencyFeignClient resourceEfficiencyFeignClient;

    /**
     *资源接通有效率表
     * @return
     */
    @RequestMapping("/resourceEfficientTable")
    public String resourceConectEfficientTable(HttpServletRequest request) {
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        return "reportforms/resourceConnectEfficientTable";
    }

    /**
     * 获取资源有效率列表(资源有效)
     */
    @RequestMapping("/getResourceEfficientList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getResourceEfficientList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) throws Exception{
        initAuth(resourceEfficiencyQueryDto);
        return resourceEfficiencyFeignClient.getResourceEfficientPageList(resourceEfficiencyQueryDto);
    }

    /**
     * 获取资源有效率列表(首日有效)
     */
    @RequestMapping("/getFirstResourceEfficientList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getFirstResourceEfficientList(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto) throws Exception{
        initAuth(resourceEfficiencyQueryDto);
        return resourceEfficiencyFeignClient.getFirstResourceEfficientPageList(resourceEfficiencyQueryDto);
    }
    /**
     *
     *  导出资源接通有效率
     */
    @PostMapping("/exportResourceEfficiency")
    public void exportResourceEfficiency(@RequestBody ResourceEfficiencyQueryDto resourceEfficiencyQueryDto,HttpServletResponse response) throws IOException {
        initAuth(resourceEfficiencyQueryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitle());
        JSONResult<Map<String, Object>> result = resourceEfficiencyFeignClient.getAllResourceEfficientList(resourceEfficiencyQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("allListData"));
        List<ResourceEfficiencyAllDataDto> orderList = JSON.parseArray(listTxt, ResourceEfficiencyAllDataDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        ResourceEfficiencyAllDataDto sumReadd = JSON.parseObject(totalDataStr, ResourceEfficiencyAllDataDto.class);
        //添加合计头
        addTotalTexportData(sumReadd,dataList);
        for(int i = 0; i<orderList.size(); i++){
            ResourceEfficiencyAllDataDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getResourceCategoryName());
            curList.add(ra.getResourceMediumName());
            curList.add(ra.getProjectTypeName());
            curList.add(ra.getIssuedResources());
            curList.add(ra.getFollowResources());
            curList.add(ra.getFirstResources());
            curList.add(ra.getConnectResources());
            curList.add(ra.getNotConnectResources());
            curList.add(ra.getConnectEffectiveResources());
            curList.add(ra.getConnectNotEffectiveResources());
            curList.add(ra.getNotConnectEffectiveResources());
            curList.add(ra.getNotConnectNotEffectiveResources());
            curList.add(formatPercent(ra.getFollowRate()));
            curList.add(formatPercent(ra.getFirstRate()));
            curList.add(formatPercent(ra.getIssuedConnectRate()));
            curList.add(formatPercent(ra.getIssuedEffectiveRate()));
            curList.add(formatPercent(ra.getResourceConnectRate()));
            curList.add(formatPercent(ra.getResourceEffectiveRate()));
            curList.add(formatPercent(ra.getConnectionRate()));
            curList.add(ra.getFirstDayFollowResources());
            curList.add(ra.getFirstDayConnectResources());
            curList.add(ra.getFirstDayNotConnectResources());
            curList.add(ra.getFirstDayConnectEffectiveResources());
            curList.add(ra.getFirstDayConnectNotEffectiveResources());
            curList.add(ra.getFirstDayNotConnectEffectiveResources());
            curList.add(ra.getFirstDayNotConnectNotEffectiveResources());
            curList.add(formatPercent(ra.getFirstDayFollowRate()));
            curList.add(formatPercent(ra.getFirstDayIssuedConnectRate()));
            curList.add(formatPercent(ra.getFirstDayIssuedEffectiveRate()));
            curList.add(formatPercent(ra.getFirstDayResourceConnectRate()));
            curList.add(formatPercent(ra.getFirstDayResourceEffectiveRate()));
            curList.add(formatPercent(ra.getFirstDayConnectionRate()));
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = resourceEfficiencyQueryDto.getStartTime();
        Long endTime = resourceEfficiencyQueryDto.getEndTime();
        String name = "资源接通有效率表" +startTime+"-"+endTime + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }


    private List<Object> getHeadTitle() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("资源类别");
        headTitleList.add("媒介");
        headTitleList.add("资源项目");
        headTitleList.add("下发资源量");
        headTitleList.add("跟访资源量");
        headTitleList.add("首次接通资源量");
        headTitleList.add("接通资源量");
        headTitleList.add("未接通资源量");
        headTitleList.add("接通有效资源量");
        headTitleList.add("接通无效资源量");
        headTitleList.add("未接通有效资源量");
        headTitleList.add("未接通无效资源量");
        headTitleList.add("跟访率");
        headTitleList.add("首次接通率");
        headTitleList.add("下发接通率");
        headTitleList.add("下发有效率");
        headTitleList.add("跟进接通率");
        headTitleList.add("跟进有效率");
        headTitleList.add("接通有效率");
        headTitleList.add("首日跟访资源量");
        headTitleList.add("首日接通资源量");
        headTitleList.add("首日未接通资源量");
        headTitleList.add("首日接通有效资源量");
        headTitleList.add("首日接通无效资源量");
        headTitleList.add("首日未接通有效资源量");
        headTitleList.add("首日未接通无效资源量");
        headTitleList.add("首日跟访率");
        headTitleList.add("首日下发接通率");
        headTitleList.add("首日下发有效率");
        headTitleList.add("首日跟进接通率");
        headTitleList.add("首日跟进有效率");
        headTitleList.add("首日接通有效率");
        return headTitleList;
    }

    private void addTotalTexportData(ResourceEfficiencyAllDataDto ra, List<List<Object>> dataList) {
        List<Object> curList = new ArrayList<>();
        curList.add("");
        curList.add("");
        curList.add("合计");
        curList.add("");
        curList.add(ra.getIssuedResources());
        curList.add(ra.getFollowResources());
        curList.add(ra.getFirstResources());
        curList.add(ra.getConnectResources());
        curList.add(ra.getNotConnectResources());
        curList.add(ra.getConnectEffectiveResources());
        curList.add(ra.getConnectNotEffectiveResources());
        curList.add(ra.getNotConnectEffectiveResources());
        curList.add(ra.getNotConnectNotEffectiveResources());
        curList.add(formatPercent(ra.getFollowRate()));
        curList.add(formatPercent(ra.getFirstRate()));
        curList.add(formatPercent(ra.getIssuedConnectRate()));
        curList.add(formatPercent(ra.getIssuedEffectiveRate()));
        curList.add(formatPercent(ra.getResourceConnectRate()));
        curList.add(formatPercent(ra.getResourceEffectiveRate()));
        curList.add(formatPercent(ra.getConnectionRate()));
        curList.add(ra.getFirstDayFollowResources());
        curList.add(ra.getFirstDayConnectResources());
        curList.add(ra.getFirstDayNotConnectResources());
        curList.add(ra.getFirstDayConnectEffectiveResources());
        curList.add(ra.getFirstDayConnectNotEffectiveResources());
        curList.add(ra.getFirstDayNotConnectEffectiveResources());
        curList.add(ra.getFirstDayNotConnectNotEffectiveResources());
        curList.add(formatPercent(ra.getFirstDayFollowRate()));
        curList.add(formatPercent(ra.getFirstDayIssuedConnectRate()));
        curList.add(formatPercent(ra.getFirstDayIssuedEffectiveRate()));
        curList.add(formatPercent(ra.getFirstDayResourceConnectRate()));
        curList.add(formatPercent(ra.getFirstDayResourceEffectiveRate()));
        curList.add(formatPercent(ra.getFirstDayConnectionRate()));
        dataList.add(curList);
    }
    /**
     * 查询字典表
     * @param code
     * @return
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
    public void initAuth(ResourceEfficiencyQueryDto resourceEfficiencyQueryDto){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<UserDataAuthReq> userDataAuthList = curLoginUser.getUserDataAuthList();
        Map<Integer,String> map = new HashMap<>();
        if(null != userDataAuthList && userDataAuthList.size() > 0){
            for(UserDataAuthReq udar : userDataAuthList){
                map.put(udar.getBusinessLine(),udar.getDicValue());
            }
            resourceEfficiencyQueryDto.setBusinessLineMap(map);
        }
    }

    /**
     * 百分比格式化
     */
    private String formatPercent(BigDecimal callPercent) {
        if(callPercent!=null) {
            callPercent = callPercent.multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_DOWN);
        }else {
            callPercent = BigDecimal.ZERO;
        }
        return callPercent+"%";
    }

}
