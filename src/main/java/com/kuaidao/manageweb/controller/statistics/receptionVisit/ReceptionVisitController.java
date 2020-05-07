package com.kuaidao.manageweb.controller.statistics.receptionVisit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.statistics.receptionVisit.ReceptionVisitFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.receptionVisit.ReceptionVisitDto;
import com.kuaidao.stastics.dto.receptionVisit.ReceptionVisitQueryDto;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商务来访接待工作表
 */
@Controller
@RequestMapping("/statistics/receptionVisit")
public class ReceptionVisitController {

    @Autowired
    private ReceptionVisitFeignClient receptionVisitFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;


    private static final Integer PERSON = 1;
    private static final Integer PERSON_DAY = 2;

    /**
     * 组一级页面跳转
     */
    @RequestMapping("/businessVisitReceptionTable")
    public String businessVisitReceptionTable(Long userId,Long orgId,Long startTime,Long endTime,HttpServletRequest request) {
        pageParams(userId,orgId,startTime,endTime,request);
        initAuth(null,request);
        return "reportformsBusiness/businessVisitReceptionTable";
    }
    /**
     * 天二级页面跳转
     */
    @RequestMapping("/businessVisitReceptionTablePerson")
    public String businessVisitReceptionTablePerson(Long userId,Long orgId,Long startTime,Long endTime,HttpServletRequest request) {
        if(null==orgId){
            IdEntityLong idEntity=new IdEntityLong();
            idEntity.setId(userId);
            JSONResult<UserInfoDTO> result=userInfoFeignClient.get(idEntity);
            if("0".equals(result.getCode()) && null!=result.getData()){
                orgId=result.getData().getOrgId();
            }
        }
        pageParams(userId,orgId,startTime,endTime,request);
        initAuth(null,request);
        return "reportformsBusiness/businessVisitReceptionTablePerson";
    }

    /**
     *
     * 一级页面查询人（不分页）
     */
    @PostMapping("/exportPersonAllList")
    public void exportPersonAllList(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto,HttpServletResponse response) throws IOException {
        initAuth(receptionVisitQueryDto,null);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getReceptionVisitTitleList(PERSON));
        JSONResult<Map<String, Object>> result = receptionVisitFeignClient.getPersonAllList(receptionVisitQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<ReceptionVisitDto> orderList = JSON.parseArray(listTxt, ReceptionVisitDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        ReceptionVisitDto sumReadd = JSON.parseObject(totalDataStr, ReceptionVisitDto.class);
        //添加合计头
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = curLoginUser.getRoleList().get(0).getRoleCode();
        if(!RoleCodeEnum.SWJL.name().equals(roleCode)){
            addTotalExportData(sumReadd,dataList);
        }
        buildList(dataList, orderList,PERSON);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "来访接待工作表" +receptionVisitQueryDto.getStartTime()+"-"+receptionVisitQueryDto.getEndTime() + ".xlsx";
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
     * 一级页面查询人（分页）
     */
    @PostMapping("/getPersonPageList")
    @ResponseBody
    public JSONResult<Map<String,Object>> getPersonPageList(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto){
        initAuth(receptionVisitQueryDto,null);
        return receptionVisitFeignClient.getPersonPageList(receptionVisitQueryDto);
    }

    /**
     *
     * 二级页面查询人+天（不分页）
     */
    @PostMapping("/exportPersonDayAllList")
    public void exportPersonDayAllList(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto,HttpServletResponse response) throws IOException {
        initAuth(receptionVisitQueryDto,null);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getReceptionVisitTitleList(PERSON_DAY));
        JSONResult<Map<String, Object>> result = receptionVisitFeignClient.getPersonDayAllList(receptionVisitQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<ReceptionVisitDto> orderList = JSON.parseArray(listTxt, ReceptionVisitDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        ReceptionVisitDto sumReadd = JSON.parseObject(totalDataStr, ReceptionVisitDto.class);
        //添加合计头
        addTotalExportData(sumReadd,dataList);
        buildList(dataList, orderList,PERSON_DAY);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "来访接待工作表" +receptionVisitQueryDto.getStartTime()+"-"+receptionVisitQueryDto.getEndTime() + ".xlsx";
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
     * 二级页面查询人+天（分页）
     */
    @PostMapping("/getPersonDayPageList")
    @ResponseBody
    JSONResult<Map<String,Object>> getPersonDayPageList(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto){
        initAuth(receptionVisitQueryDto,null);
        return receptionVisitFeignClient.getPersonDayPageList(receptionVisitQueryDto);
    }

    private List<Object> getReceptionVisitTitleList(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        if(PERSON_DAY.equals(type)){
            headTitleList.add("来访日期");
        }
        if(PERSON.equals(type)){
            headTitleList.add("商务经理");
        }
        headTitleList.add("来访次数");
        headTitleList.add("来访客户数");
        headTitleList.add("首访数占比");
        headTitleList.add("签约单数");
        headTitleList.add("定金率");
        headTitleList.add("退款单数");
        headTitleList.add("退款率");
        headTitleList.add("平均每客户来访次数");
        headTitleList.add("人均天接待来访次数");
        return headTitleList;
    }
    private void addTotalExportData(ReceptionVisitDto ra, List<List<Object>> dataList) {
        List<Object> curList = new ArrayList<>();
        curList.add("");
        curList.add("合计");
        curList.add(ra.getVisitNum());
        curList.add(ra.getVisitClueNum());
        curList.add(ra.getFirstVisitRate());
        curList.add(ra.getBusinessSignNum());
        curList.add(ra.getEarnestRate());
        curList.add(ra.getRefundNum());
        curList.add(ra.getRefundRate());
        curList.add(ra.getAvgCustomRate());
        curList.add(ra.getAvgReceptionRate());
        dataList.add(curList);
    }

    private void buildList(List<List<Object>> dataList, List<ReceptionVisitDto> orderList, Integer type) {
        for(int i = 0; i<orderList.size(); i++){
            ReceptionVisitDto ra = orderList.get(i);
            List<Object> curList = new ArrayList<>();
            curList.add(i + 1);
            if(PERSON_DAY.equals(type)){
                StringBuilder sb = new StringBuilder(ra.getDateId());
                sb.insert(6,"-");
                sb.insert(4,"-");
                curList.add(sb);
            }
            if(PERSON.equals(type)){
                curList.add(ra.getBusinessManagerName());
            }
            curList.add(ra.getVisitNum());
            curList.add(ra.getVisitClueNum());
            curList.add(ra.getFirstVisitRate());
            curList.add(ra.getBusinessSignNum());
            curList.add(ra.getEarnestRate());
            curList.add(ra.getRefundNum());
            curList.add(ra.getRefundRate());
            curList.add(ra.getAvgCustomRate());
            curList.add(ra.getAvgReceptionRate());
            dataList.add(curList);
        }
    }

    /**
     *  返回页面携带参数
     */
    private void pageParams(Long userId,Long orgId,Long startTime,Long endTime,HttpServletRequest request){
        ReceptionVisitQueryDto receptionVisitQueryDto = new ReceptionVisitQueryDto();
        receptionVisitQueryDto.setOrgId(orgId);
        receptionVisitQueryDto.setStartTime(startTime);
        receptionVisitQueryDto.setEndTime(endTime);
        receptionVisitQueryDto.setUserId(userId);
        request.setAttribute("receptionVisitQueryDto",receptionVisitQueryDto);
    }

    private List<OrganizationDTO> getOrgGroupByOrgId(Long orgId,Integer orgType){
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setParentId(orgId);
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(orgType);
        JSONResult<List<OrganizationDTO>> listJSONResult = organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
        List<OrganizationDTO> data = listJSONResult.getData();
        return data;
    }
    /**
     * 获取当前 orgId所在的组
     */
    private List<OrganizationDTO> getCurOrgGroupByOrgId(Long orgId) {
        List<OrganizationDTO> data = new ArrayList<>();
        // 商务组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(String.valueOf(orgId));
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        data.add(orgJr.getData());
        return data;
    }
    /**
     * 查询商务经理
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq) {
        userOrgRoleReq.setRoleCode(RoleCodeEnum.SWJL.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
    }

    @PostMapping("/getUserInfo")
    @ResponseBody
    public JSONResult<UserInfoDTO> getUserInfo(@RequestBody ReceptionVisitQueryDto receptionVisitQueryDto){
        IdEntityLong idEntity=new IdEntityLong();
        idEntity.setId(receptionVisitQueryDto.getUserId());
        JSONResult<UserInfoDTO> userInfoDTOJSONResult = userInfoFeignClient.get(idEntity);
        return userInfoDTOJSONResult;
    }

    /**
     * 初始化权限
     */
    private void initAuth(ReceptionVisitQueryDto receptionVisitQueryDto,HttpServletRequest request){
        List<OrganizationDTO> teleGroupList = new ArrayList<>();
        String curOrgId = "";
        String curUserId = "";
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode = curLoginUser.getRoleList().get(0).getRoleCode();
        if(RoleCodeEnum.SWJL.name().equals(roleCode)){
            curOrgId = String.valueOf(curLoginUser.getOrgId());
            curUserId = String.valueOf(curLoginUser.getId());
            teleGroupList = getCurOrgGroupByOrgId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            curOrgId = String.valueOf(curLoginUser.getOrgId());
            teleGroupList = getCurOrgGroupByOrgId(curLoginUser.getOrgId());
        }else{
            teleGroupList = getOrgGroupByOrgId(curLoginUser.getOrgId(),OrgTypeConstant.SWZ);
        }
        if(null != receptionVisitQueryDto && null != teleGroupList && teleGroupList.size() > 0){
            List<Long> orgIdList = teleGroupList.parallelStream().map(OrganizationDTO::getId).collect(Collectors.toList());
            receptionVisitQueryDto.setOrgIdList(orgIdList);
        }
        //商务经理查询 考虑借调 删除组限制
        if(null != receptionVisitQueryDto && RoleCodeEnum.SWJL.name().equals(roleCode)){
            receptionVisitQueryDto.setOrgIdList(null);
            receptionVisitQueryDto.setOrgId(null);
        }
        if(null != request){
            request.setAttribute("teleGroupList",teleGroupList);
            request.setAttribute("curOrgId",curOrgId);
            request.setAttribute("curUserId",curUserId);
        }
    }
}
