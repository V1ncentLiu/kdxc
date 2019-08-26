package com.kuaidao.manageweb.controller.statistics.teleSaleTracking;


import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.teleSaleTracking.TeleSaleTrackingFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.teleSaleTracking.TeleSaleTrackingDto;
import com.kuaidao.stastics.dto.teleSaleTracking.TeleSaleTrackingQueryDto;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics/teleStatement")
public class TeleSaleTrackingController {

    @Autowired
    private TeleSaleTrackingFeignClient teleSaleTrackingFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    private static Logger logger = LoggerFactory.getLogger(TeleSaleTrackingController.class);

    /**
     * 一级页面查询
     */
    @RequestMapping("/getRecordByGroupPageOne")
    @ResponseBody
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupPageOne(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
                                                                      HttpServletRequest request){
        String strCusLevelList = trackingQueryDto.getStrCusLevelList();
        trackingQueryDto.setStrCusLevelList(strCusLevelList);
        request.setAttribute("trackingQueryDto",trackingQueryDto);
        Long orgId = trackingQueryDto.getOrgId();
        if(null == orgId){
            buildOrgIdList(trackingQueryDto, orgId);
            List<Long> orgIdList = trackingQueryDto.getOrgIdList();
            if(orgIdList == null || orgIdList.size() == 0){
                PageBean emptyDataPageBean = PageBean.getEmptyListDataPageBean(trackingQueryDto.getPageNum(), trackingQueryDto.getPageSize());
                return new JSONResult<PageBean<TeleSaleTrackingDto>>().success(emptyDataPageBean);
            }
        }
        if(null != trackingQueryDto.getCusLevelList() && trackingQueryDto.getCusLevelList().size() > 0){
            return  teleSaleTrackingFeignClient.getRecordByGroupLevelPage(trackingQueryDto);
        }else{
            return  teleSaleTrackingFeignClient.getRecordByGroupPage(trackingQueryDto);
        }
    }

    /**
     * 组+级别+用户
     */
    @RequestMapping("/getRecordByGroupLevelUserId")
    @ResponseBody
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserId(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
                                                                          HttpServletRequest request){
        Long orgId = trackingQueryDto.getOrgId();
        if(null == orgId){
            buildOrgIdList(trackingQueryDto, orgId);
        }
        request.setAttribute("trackingQueryDto",trackingQueryDto);
        if(null != trackingQueryDto.getCusLevelList() && trackingQueryDto.getCusLevelList().size() > 0){
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
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdDate(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
                                                                              HttpServletRequest request){
        logger.info("电销跟踪合计查询参数：{}",trackingQueryDto.toString());
        Long orgId = trackingQueryDto.getOrgId();
        if(null == orgId){
            buildOrgIdList(trackingQueryDto, orgId);
        }
        logger.info("赋值后电销跟踪合计查询参数：{}",trackingQueryDto.toString());
        request.setAttribute("trackingQueryDto",trackingQueryDto);
        if(null != trackingQueryDto.getCusLevelList() && trackingQueryDto.getCusLevelList().size() > 0){
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
        Long orgId = trackingQueryDto.getOrgId();
        if(null == orgId){
            buildOrgIdList(trackingQueryDto, orgId);
        }
        List countList = getCountTotal(trackingQueryDto);
        return new JSONResult<List<TeleSaleTrackingDto>>().success(countList);
    }

    /**
     * 一级页面导出
     */
    @RequiresPermissions("statistics:telemarketingFollow:export")
    @PostMapping("/exportRecordByGroupPageOne")
    public void exportRecordByGroupPageOne(
            @RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
            HttpServletResponse response) throws Exception {
        Long orgId = trackingQueryDto.getOrgId();
        if(null == orgId){
            buildOrgIdList(trackingQueryDto, orgId);
        }
        JSONResult<List<TeleSaleTrackingDto>> list = teleSaleTrackingFeignClient.getRecordByGroup(trackingQueryDto);
        if(null != trackingQueryDto.getCusLevelList() && trackingQueryDto.getCusLevelList().size() > 0){
            list = teleSaleTrackingFeignClient.getRecordByGroupLevel(trackingQueryDto);
        }
        List<TeleSaleTrackingDto> countList = getCountTotal(trackingQueryDto);
        TeleSaleTrackingDto resTotal = countList.get(0);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        //加表头
        dataList.add(getHeadOneTitleList());
        //加合计
        addTotalTeportResourceAllocation(resTotal,dataList);
        List<TeleSaleTrackingDto> orderList = list.getData();
        for(int i = 0; i<orderList.size(); i++){
            TeleSaleTrackingDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(getCusLeveName(ra.getCusLevel()));
            curList.add(ra.getCountResource());
            curList.add(ra.getCountClueId());
            curList.add(ra.getCountDistinctClue());
            Double dayOfPer = ra.getDayOfPer();
            if(dayOfPer == 0){
               curList.add(0);
            }else{
                Double abc = Math.floor(dayOfPer*100)/100;
                curList.add(String.valueOf(abc));
            }
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = trackingQueryDto.getStartTime();
        Long endTime = trackingQueryDto.getEndTime();
        String name = "电销跟踪记录" +startTime+"-"+endTime + ".xlsx";
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
    @RequiresPermissions("statistics:telemarketingFollow:export")
    @PostMapping("/exportRecordByGroupLevelUserId")
    public void exportRecordByGroupLevelUserId(
            @RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
            HttpServletResponse response) throws Exception {
        Long orgId = trackingQueryDto.getOrgId();
        if(null == orgId){
            buildOrgIdList(trackingQueryDto, orgId);
        }
        JSONResult<List<TeleSaleTrackingDto>> list = teleSaleTrackingFeignClient.getRecordByGroupUserId(trackingQueryDto);
        if(null != trackingQueryDto.getCusLevelList() && trackingQueryDto.getCusLevelList().size() > 0){
            list = teleSaleTrackingFeignClient.getRecordByGroupLevelUserId(trackingQueryDto);
        }
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadSumTitleList());
        List<TeleSaleTrackingDto> orderList = list.getData();
        for(int i = 0; i<orderList.size(); i++){
            TeleSaleTrackingDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getOrgName());
            curList.add(ra.getUserName());
            curList.add(getCusLeveName(ra.getCusLevel()));
            curList.add(ra.getCountResource());
            curList.add(ra.getCountClueId());
            curList.add(ra.getCountDistinctClue());
            Double dayOfPer = ra.getDayOfPer();
            if(dayOfPer == 0){
                curList.add(0);
            }else{
                Double abc = Math.floor(dayOfPer*100)/100;
                curList.add(String.valueOf(abc));
            }
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = trackingQueryDto.getStartTime();
        Long endTime = trackingQueryDto.getEndTime();
        String name = "电销跟踪记录" +startTime+"-"+endTime + ".xlsx";
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
    @RequiresPermissions("statistics:telemarketingFollow:export")
    @PostMapping("/exportRecordByGroupLevelUserIdDate")
    public void exportRecordByGroupLevelUserIdDate(
            @RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
            HttpServletResponse response) throws Exception {
        Long orgId = trackingQueryDto.getOrgId();
        if(null == orgId){
            buildOrgIdList(trackingQueryDto, orgId);
        }
        JSONResult<List<TeleSaleTrackingDto>> list = teleSaleTrackingFeignClient.getRecordByGroupUserIdDate(trackingQueryDto);
        if(null != trackingQueryDto.getCusLevelList() && trackingQueryDto.getCusLevelList().size() > 0){
            list = teleSaleTrackingFeignClient.getRecordByGroupLevelUserIdDate(trackingQueryDto);
        }
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());
        List<TeleSaleTrackingDto> orderList = list.getData();
        for(int i = 0; i<orderList.size(); i++){
            TeleSaleTrackingDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            String str = null;
            if(ra.getDateId() != null){
                StringBuilder sb = new StringBuilder(ra.getDateId().toString());
                sb.insert(6,"-");
                sb.insert(4,"-");
                str = sb.toString();
            }
            curList.add(str);
            curList.add(ra.getOrgName());
            curList.add(ra.getUserName());
            curList.add(getCusLeveName(ra.getCusLevel()));
            curList.add(ra.getCountResource());
            curList.add(ra.getCountClueId());
            curList.add(ra.getCountDistinctClue());
            Double dayOfPer = ra.getDayOfPer();
            if(dayOfPer == 0){
                curList.add(0);
            }else{
                Double abc = Math.floor(dayOfPer*100)/100;
                curList.add(String.valueOf(abc));
            }
            dataList.add(curList);
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        Long startTime = trackingQueryDto.getStartTime();
        Long endTime = trackingQueryDto.getEndTime();
        String name = "电销跟踪记录" +startTime+"-"+endTime + ".xlsx";
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
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        JSONResult<List<DictionaryItemRespDTO>> customerLevel = dictionaryItemFeignClient.queryDicItemsByGroupCode("customerLevel");
        request.setAttribute("cusLevelListArray",customerLevel.getData());
        return "reportforms/telemarketingFollowTable";
    }


    /**
     * 电销顾问跟踪表页面 合计
     * @return
     */
    @RequestMapping("/telemarketingFollowTableSum")
    public String telemarketingFollowTableSum(Long orgId,Long startTime,Long endTime,String strCusLevelList,HttpServletRequest request) {
        TeleSaleTrackingQueryDto trackingQueryDto = new TeleSaleTrackingQueryDto();
        trackingQueryDto.setOrgId(orgId);
        trackingQueryDto.setStartTime(startTime);
        trackingQueryDto.setEndTime(endTime);
        trackingQueryDto.setStrCusLevelList(strCusLevelList);
        request.setAttribute("trackingQueryDto",trackingQueryDto);
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        JSONResult<List<DictionaryItemRespDTO>> customerLevel = dictionaryItemFeignClient.queryDicItemsByGroupCode("customerLevel");
        request.setAttribute("cusLevelListArray",customerLevel.getData());
        return "reportforms/telemarketingFollowTableSum";
    }


    /**
     * 电销顾问跟踪表页面 组
     * @return
     */
    @RequestMapping("/telemarketingFollowTableTeam")
    public String telemarketingFollowTableTeam(Long orgId,Long startTime,Long endTime,String strCusLevelList,HttpServletRequest request) {
        TeleSaleTrackingQueryDto trackingQueryDto = new TeleSaleTrackingQueryDto();
        trackingQueryDto.setOrgId(orgId);
        trackingQueryDto.setStartTime(startTime);
        trackingQueryDto.setEndTime(endTime);
        trackingQueryDto.setStrCusLevelList(strCusLevelList);
        request.setAttribute("trackingQueryDto",trackingQueryDto);
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        JSONResult<List<DictionaryItemRespDTO>> customerLevel = dictionaryItemFeignClient.queryDicItemsByGroupCode("customerLevel");
        request.setAttribute("cusLevelListArray",customerLevel.getData());
        return "reportforms/telemarketingFollowTableTeam";
    }

    /**
     * 电销顾问跟踪表页面 个人
     * @return
     */
    @RequestMapping("/telemarketingFollowTablePerson")
    public String telemarketingFollowTablePerson(Long userId,Long orgId,Long startTime,Long endTime,String strCusLevelList,HttpServletRequest request) {
        TeleSaleTrackingQueryDto trackingQueryDto = new TeleSaleTrackingQueryDto();
        trackingQueryDto.setOrgId(orgId);
        trackingQueryDto.setStartTime(startTime);
        trackingQueryDto.setEndTime(endTime);
        trackingQueryDto.setStrCusLevelList(strCusLevelList);
        trackingQueryDto.setUserId(userId);
        request.setAttribute("trackingQueryDto",trackingQueryDto);
        // 查询所有电销组
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String curOrgId = "";
        List<OrganizationRespDTO>  teleGroupList = new ArrayList<>();
        if(RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            curOrgId =  String.valueOf(curLoginUser.getOrgId());
            //电销总监查他自己的组
            OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(curOrgId);
            if(curOrgGroupByOrgId!=null) {
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                teleGroupList.add(organizationRespDTO);
            }
        }else {
            teleGroupList =  getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.DXZ);
        }
        OrganizationQueryDTO organizationQueryDTO  = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(curLoginUser.getOrgId());
        request.setAttribute("curOrgId",curOrgId);
        request.setAttribute("saleGroupList",teleGroupList);
        JSONResult<List<DictionaryItemRespDTO>> customerLevel = dictionaryItemFeignClient.queryDicItemsByGroupCode("customerLevel");
        request.setAttribute("cusLevelListArray",customerLevel.getData());
        return "reportforms/telemarketingFollowTablePerson";
    }

    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("日期");
        headTitleList.add("电销组");
        headTitleList.add("电销顾问");
        headTitleList.add("客户级别");
        headTitleList.add("资源数");
        headTitleList.add("回访次数");
        headTitleList.add("回访资源数");
        headTitleList.add("人均天回访次数");
        return headTitleList;
    }

    private List<Object> getHeadSumTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("电销组");
        headTitleList.add("电销顾问");
        headTitleList.add("客户级别");
        headTitleList.add("资源数");
        headTitleList.add("回访次数");
        headTitleList.add("回访资源数");
        headTitleList.add("人均天回访次数");
        return headTitleList;
    }

    private List<Object> getHeadOneTitleList() {
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
    private List<OrganizationRespDTO> getOrgGroupByOrgId(Long orgId,Integer orgType) {
        // 电销组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        if (!JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            return null;
        }
        List<OrganizationRespDTO> list = new ArrayList<>();
        if(listJSONResult != null && listJSONResult.getData().size() > 0){
            List<OrganizationDTO> data = listJSONResult.getData();
            for(OrganizationDTO organizationDTO : data){
                OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                organizationRespDTO.setId(organizationDTO.getId());
                organizationRespDTO.setName(organizationDTO.getName());
                list.add(organizationRespDTO);
            }
        }
        return list;
    }

    private void addTotalTeportResourceAllocation(TeleSaleTrackingDto resTotal, List<List<Object>> dataList) {
        List<Object> totalList = new ArrayList<>();
        totalList.add("");
        totalList.add("合计");
        totalList.add("");
        totalList.add(resTotal.getCountResource());
        totalList.add(resTotal.getCountClueId());
        totalList.add(resTotal.getCountDistinctClue());
        Double dayOfPer = resTotal.getDayOfPer();
        if(dayOfPer == 0){
            totalList.add(0);
        }else{
            Double abc = Math.floor(dayOfPer*100)/100;
            totalList.add(String.valueOf(abc));
        }
        dataList.add(totalList);
    }

    private List getCountTotal(TeleSaleTrackingQueryDto trackingQueryDto){
        logger.info("跟踪查询参数"+trackingQueryDto.toString());
        List<TeleSaleTrackingDto> list = teleSaleTrackingFeignClient.getRecordByGroup(trackingQueryDto).getData();
        if(null != trackingQueryDto.getCusLevelList() && trackingQueryDto.getCusLevelList().size() > 0){
            list = teleSaleTrackingFeignClient.getRecordByGroupLevel(trackingQueryDto).getData();
        }
        //资源数
        Integer countResouce = list.stream().mapToInt(TeleSaleTrackingDto::getCountResource).sum();
        //回访次数
        Integer countClueId = list.stream().mapToInt(TeleSaleTrackingDto::getCountClueId).sum();
        //回访资源数
        Integer countDistinctClue = list.stream().mapToInt(TeleSaleTrackingDto::getCountDistinctClue).sum();
        //人均天回访次数
        Double dayOfper = list.stream().mapToDouble(TeleSaleTrackingDto::getDayOfPer).sum();
        List<TeleSaleTrackingDto> countList = new ArrayList<>();
        TeleSaleTrackingDto res = new TeleSaleTrackingDto();
        res.setCountResource(countResouce);
        res.setCountClueId(countClueId);
        res.setCountDistinctClue(countDistinctClue);
        res.setDayOfPer(dayOfper);
        res.setOrgName("合计");
        countList.add(res);
        return countList;
    }

    private void buildOrgIdList(@RequestBody TeleSaleTrackingQueryDto teleSaleTrackingQueryDto, Long org_id) {
        if(null == org_id){
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            List<OrganizationRespDTO> orgGroupByOrgId = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
            logger.info("查询结果OrganizationRespDTO{}",orgGroupByOrgId.toArray());
            List<Long> orgIdList = orgGroupByOrgId.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
            logger.info("查询出电销组：{}",orgIdList.toArray());
            teleSaleTrackingQueryDto.setOrgIdList(orgIdList);
        }
    }
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId+"");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}",idEntity,orgJr);
            return null;
        }
        return orgJr.getData();
    }

    private static String getCusLeveName(Long code){
        if(code == null){
            return "";
        }
        if(code == 1){
            return "VIP";
        }
        if(code == 2){
            return "重要";
        }
        if (code == 3){
            return "普通";
        }
        return "";
    }
}
