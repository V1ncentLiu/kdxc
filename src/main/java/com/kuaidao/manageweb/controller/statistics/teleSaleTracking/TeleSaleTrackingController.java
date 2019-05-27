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
//        Long orgId = trackingQueryDto.getOrgId();
//        if(null == orgId){
//            buildOrgIdList(trackingQueryDto, orgId);
//        }
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
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserId(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
                                                                          HttpServletRequest request){
        request.setAttribute("trackingQueryDto",trackingQueryDto);
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
    JSONResult<PageBean<TeleSaleTrackingDto>> getRecordByGroupLevelUserIdDate(@RequestBody TeleSaleTrackingQueryDto trackingQueryDto,
                                                                              HttpServletRequest request){
        request.setAttribute("trackingQueryDto",trackingQueryDto);
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
//        if(null == orgId){
//            buildOrgIdList(trackingQueryDto, orgId);
//        }
        JSONResult<List<TeleSaleTrackingDto>> list = teleSaleTrackingFeignClient.getRecordByGroup(trackingQueryDto);
        if(null != trackingQueryDto.getCusLevel()){
            teleSaleTrackingFeignClient.getRecordByGroupLevel(trackingQueryDto);
        }
        List<TeleSaleTrackingDto> countList = getCountTotal(trackingQueryDto);
        TeleSaleTrackingDto resTotal = countList.get(0);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        //加表头
        dataList.add(getHeadTitleList());
        //加合计
        addTotalTeportResourceAllocation(resTotal,dataList);
        List<TeleSaleTrackingDto> orderList = list.getData();
        for(int i = 0; i<orderList.size(); i++){
            TeleSaleTrackingDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            curList.add(ra.getDateId());
            curList.add(ra.getOrgName());
            curList.add(ra.getUserName());
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
    @RequiresPermissions("statistics:telemarketingFollow:export")
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
            curList.add(ra.getDateId());
            curList.add(ra.getOrgName());
            curList.add(ra.getUserName());
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
    @RequiresPermissions("statistics:telemarketingFollow:export")
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
            curList.add(ra.getDateId());
            curList.add(ra.getOrgName());
            curList.add(ra.getUserName());
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
    public String telemarketingFollowTableSum(TeleSaleTrackingQueryDto trackingQueryDto,HttpServletRequest request) {
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
    public String telemarketingFollowTableTeam(TeleSaleTrackingQueryDto trackingQueryDto,HttpServletRequest request) {
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
    public String telemarketingFollowTablePerson(HttpServletRequest request) {
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
    private List<OrganizationRespDTO> getOrgGroupByOrgId(Long orgId,Integer orgType) {
        // 电销组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationRespDTO>> orgJr = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        if(!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            return null;
        }
        return orgJr.getData();
    }

    private void addTotalTeportResourceAllocation(TeleSaleTrackingDto resTotal, List<List<Object>> dataList) {
        List<Object> totalList = new ArrayList<>();
        totalList.add("");
        totalList.add("合计");
        totalList.add("");
        totalList.add("");
        totalList.add("");
        totalList.add(resTotal.getCountResource());
        totalList.add(resTotal.getCountClueId());
        totalList.add(resTotal.getCountDistinctClue());
        totalList.add(resTotal.getDayOfPer());
        dataList.add(totalList);
    }

    private List getCountTotal(TeleSaleTrackingQueryDto trackingQueryDto){
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
        return countList;
    }

    private void buildOrgIdList(@RequestBody TeleSaleTrackingQueryDto teleSaleTrackingQueryDto, Long org_id) {
        if(null == org_id){
            UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
            List<OrganizationRespDTO> orgGroupByOrgId = getOrgGroupByOrgId(curLoginUser.getOrgId(), OrgTypeConstant.DXZ);
            List<Long> orgIdList = orgGroupByOrgId.parallelStream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
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
}
