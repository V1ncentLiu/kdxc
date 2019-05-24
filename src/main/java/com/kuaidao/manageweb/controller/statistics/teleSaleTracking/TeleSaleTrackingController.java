package com.kuaidao.manageweb.controller.statistics.teleSaleTracking;


import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.teleSaleTracking.TeleSaleTrackingFeignClient;
import com.kuaidao.stastics.dto.teleSaleTracking.TeleSaleTrackingDto;
import com.kuaidao.stastics.dto.teleSaleTracking.TeleSaleTrackingQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/statistics/teleStatement")
public class TeleSaleTrackingController {

    @Autowired
    private TeleSaleTrackingFeignClient teleSaleTrackingFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    /**
     * 一级页面查询
     */
    @RequestMapping("/getRecordByGroupPageOne")
    @ResponseBody
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupPageOne(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto){
        if(null != trackingQueryDto.getCusLevel()){
            return teleSaleTrackingFeignClient.getRecordByGroupLevelPage(trackingQueryDto);
        }else{
            return teleSaleTrackingFeignClient.getRecordByGroupPage(trackingQueryDto);
        }
    }

    /**
     * 组+级别+用户
     */
    @RequestMapping("/getRecordByGroupLevelUserId")
    @ResponseBody
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserId(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto){
        if(null != trackingQueryDto.getCusLevel()){
            return teleSaleTrackingFeignClient.getRecordByGroupLevelUserIdPage(trackingQueryDto);
        }else{
            return teleSaleTrackingFeignClient.getRecordByGroupUserIdPage(trackingQueryDto);
        }
    }

    /**
     * 组+日期+级别+用户
     */
    @RequestMapping("/getRecordByGroupLevelUserIdDate")
    @ResponseBody
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdDate(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto){
        if(null != trackingQueryDto.getCusLevel()){
            return teleSaleTrackingFeignClient.getRecordByGroupLevelUserIdDatePage(trackingQueryDto);
        }else{
            return teleSaleTrackingFeignClient.getRecordByGroupUserIdDatePage(trackingQueryDto);
        }
    }

    /**
     * 合计列
     * @return
     */
    @RequestMapping("/getRecordByGroupPageOneCount")
    @ResponseBody
    public JSONResult<List<TeleSaleTrackingDto>> getRecordByGroupPageOneCount(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto){
        List<TeleSaleTrackingDto> list = teleSaleTrackingFeignClient.getRecordByGroup(trackingQueryDto).getData();
        if(null != trackingQueryDto.getCusLevel()){
            list = teleSaleTrackingFeignClient.getRecordByGroupLevel(trackingQueryDto).getData();
        }
        //资源数
        Integer countResouce = list.stream().mapToInt(TeleSaleTrackingDto::getCountResource).sum();
        //回访次数
        Integer countClueId = list.stream().mapToInt(TeleSaleTrackingDto::getCountClueId).sum();
        //回访资源数
        Integer countDistinctClue = list.stream().mapToInt(TeleSaleTrackingDto::getCountDistinctClue).sum();
        //人均天回访次数
        Integer dayOfper = list.stream().mapToInt(TeleSaleTrackingDto::getDayOfPer).sum();
        List<TeleSaleTrackingDto> countList = new ArrayList<>();
        TeleSaleTrackingDto res = new TeleSaleTrackingDto();
        res.setCountResource(countResouce);
        res.setCountClueId(countClueId);
        res.setCountDistinctClue(countDistinctClue);
        res.setDayOfPer(dayOfper);
        res.setOrgName("合计");
        countList.add(res);
        return new JSONResult<List<TeleSaleTrackingDto>>().success(countList);
    }

    /**
     * 一级页面查询
     */
    @PostMapping("/exportRecordByGroupPageOne")
    public void exportRecordByGroupPageOne(
            @RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
            HttpServletResponse response) throws Exception {
        JSONResult<List<TeleSaleTrackingDto>> list = teleSaleTrackingFeignClient.getRecordByGroup(trackingQueryDto);
        if(null != trackingQueryDto.getCusLevel()){
            teleSaleTrackingFeignClient.getRecordByGroupLevel(trackingQueryDto);
        }
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());
        List<TeleSaleTrackingDto> orderList = list.getData();
        for(int i = 0; i<orderList.size(); i++){
            TeleSaleTrackingDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(ra.getCusLevel());
            curList.add(ra.getCountResource());
            curList.add(ra.getCountClueId());
            curList.add(ra.getCountDistinctClue());
            curList.add(ra.getDayOfPer());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "电销跟踪记录" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }


    /**
     * 组+级别+用户
     */
    @PostMapping("/exportRecordByGroupLevelUserId")
    public void exportRecordByGroupLevelUserId(
            @RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
            HttpServletResponse response) throws Exception {
        JSONResult<List<TeleSaleTrackingDto>> list = teleSaleTrackingFeignClient.getRecordByGroupUserId(trackingQueryDto);
        if(null != trackingQueryDto.getCusLevel()){
            teleSaleTrackingFeignClient.getRecordByGroupLevelUserId(trackingQueryDto);
        }
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());
        List<TeleSaleTrackingDto> orderList = list.getData();
        for(int i = 0; i<orderList.size(); i++){
            TeleSaleTrackingDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(ra.getCusLevel());
            curList.add(ra.getCountResource());
            curList.add(ra.getCountClueId());
            curList.add(ra.getCountDistinctClue());
            curList.add(ra.getDayOfPer());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "电销跟踪记录" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }


    /**
     * 组+级别+用户+日期
     */
    @PostMapping("/exportRecordByGroupLevelUserIdDate")
    public void exportRecordByGroupLevelUserIdDate(
            @RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
            HttpServletResponse response) throws Exception {
        JSONResult<List<TeleSaleTrackingDto>> list = teleSaleTrackingFeignClient.getRecordByGroupUserIdDate(trackingQueryDto);
        if(null != trackingQueryDto.getCusLevel()){
            teleSaleTrackingFeignClient.getRecordByGroupLevelUserIdDate(trackingQueryDto);
        }
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());
        List<TeleSaleTrackingDto> orderList = list.getData();
        for(int i = 0; i<orderList.size(); i++){
            TeleSaleTrackingDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(ra.getCusLevel());
            curList.add(ra.getCountResource());
            curList.add(ra.getCountClueId());
            curList.add(ra.getCountDistinctClue());
            curList.add(ra.getDayOfPer());
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "电销跟踪记录" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }


    /**
     * 电销顾问跟踪表页面
     * @return
     */
    @RequestMapping("/telemarketingFollowTable")
    public String telemarketingFollowTable(HttpServletRequest request) {
        // 查询所有电销组
        List<OrganizationRespDTO> saleGroupList = getSaleGroupList();
        request.setAttribute("saleGroupList", saleGroupList);
        JSONResult<List<DictionaryItemRespDTO>> customerLevel = dictionaryItemFeignClient.queryDicItemsByGroupCode("customerLevel");
        request.setAttribute("cusLevelListArray",customerLevel.getData());
        return "reportforms/telemarketingFollowTable";
    }


    /**
     * 电销顾问跟踪表页面 合计
     * @return
     */
    @RequestMapping("/telemarketingFollowTableSum")
    public String telemarketingFollowTableSum(HttpServletRequest request) {
        // 查询所有电销组
        List<OrganizationRespDTO> saleGroupList = getSaleGroupList();
        request.setAttribute("saleGroupList", saleGroupList);
        JSONResult<List<DictionaryItemRespDTO>> customerLevel = dictionaryItemFeignClient.queryDicItemsByGroupCode("customerLevel");
        request.setAttribute("cusLevelListArray",customerLevel.getData());
        return "reportforms/telemarketingFollowTableSum";
    }


    /**
     * 电销顾问跟踪表页面 组
     * @return
     */
    @RequestMapping("/telemarketingFollowTableTeam")
    public String telemarketingFollowTableTeam(HttpServletRequest request) {
        // 查询所有电销组
        List<OrganizationRespDTO> saleGroupList = getSaleGroupList();
        request.setAttribute("saleGroupList", saleGroupList);
        JSONResult<List<DictionaryItemRespDTO>> customerLevel = dictionaryItemFeignClient.queryDicItemsByGroupCode("customerLevel");
        request.setAttribute("cusLevelListArray",customerLevel.getData());
        return "reportforms/telemarketingFollowTableTeam";
    }

    /**
     * 电销顾问跟踪表页面 个人
     * @return
     */
    @RequestMapping("/telemarketingFollowTablePerson")
    public String telemarketingFollowTablePerson(HttpServletRequest request) {
        // 查询所有电销组
        List<OrganizationRespDTO> saleGroupList = getSaleGroupList();
        request.setAttribute("saleGroupList", saleGroupList);
        JSONResult<List<DictionaryItemRespDTO>> customerLevel = dictionaryItemFeignClient.queryDicItemsByGroupCode("customerLevel");
        request.setAttribute("cusLevelListArray",customerLevel.getData());
        return "reportforms/telemarketingFollowTablePerson";
    }

    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("客户级别");
        headTitleList.add("资源数");
        headTitleList.add("回访次数");
        headTitleList.add("回访资源数");
        headTitleList.add("人均天回访次数");
        return headTitleList;
    }

    /**
     * 获取电销组
     */
    private List<OrganizationRespDTO> getSaleGroupList() {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.DXZ);
        // 查询下级电销组
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> data = queryOrgByParam.getData();
        return data;
    }
}
