package com.kuaidao.manageweb.controller.statistics.businessAreaVisitSign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.statistics.BusinessAreaVisitSign.BusinessAreaVisitSignFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.stastics.dto.base.BaseBusQueryDto;
import com.kuaidao.stastics.dto.busAreaVisitSign.BusAreaVisitSignDto;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: BusinessAreaBisitSignController
 * @date: 19-11-21 下午3:57
 * @author: xuyunfeng
 * @version: 1.0
 */
@RequestMapping("/businessAreaBisitSign")
@Controller
public class BusinessAreaVisitSignController {
    private static Logger logger = LoggerFactory.getLogger(BusinessAreaVisitSignController.class);

    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private BusinessAreaVisitSignFeignClient businessAreaVisitSignFeignClient;
    /**
    * @Description 商务大区来访签约业绩表页面初始化
    * @param request
    * @Return java.lang.String
    * @Author xuyunfeng
    * @Date 19-11-26 上午10:44
    **/
    @RequiresPermissions("businessAreaBisitSign:initBusinessAreaBisitSign:view")
    @RequestMapping("/initBusinessAreaBisitSign")
    public String initBusinessAreaBisitSign(HttpServletRequest request){
        //商务组
        initOrgList(request);
        //商务大区
        initBugOrg(request);
        return "reportformsBusiness/businessAreaVisitSign";
    }

    /**
    * @Description 商务大区来访签约业绩表查询
    * @param baseBusQueryDto
    * @Return com.kuaidao.common.entity.JSONResult<java.util.Map<java.lang.String,java.lang.Object>>
    * @Author xuyunfeng
    * @Date 19-11-26 下午4:37
    **/
    @RequestMapping("/getBusinessAreaSignList")
    public JSONResult<Map<String,Object>> getBusinessAreaSignList(@RequestBody BaseBusQueryDto baseBusQueryDto){
        JSONResult<Map<String, Object>> businessAreaSignList = businessAreaVisitSignFeignClient.getBusinessAreaSignList(baseBusQueryDto);
        return businessAreaSignList;
    }

    /**
     *商务大区来访签约业绩表导出
     *
     */
    @RequestMapping("/exportAllList")
    public void exportAllList(HttpServletResponse response, @RequestBody BaseBusQueryDto baseBusQueryDto) throws IOException {
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getTitleList(1));
        JSONResult<Map<String, Object>> result =   businessAreaVisitSignFeignClient.getBusinessAreaSignList(baseBusQueryDto);
        Map<String, Object> dataMap = result.getData();
        String listTxt = JSONArray.toJSONString(dataMap.get("tableData"));
        List<BusAreaVisitSignDto> orderList = JSON.parseArray(listTxt, BusAreaVisitSignDto.class);
        String totalDataStr = JSON.toJSONString(dataMap.get("totalData"));
        //合计
        List<BusAreaVisitSignDto>  sumReadd = JSON.parseArray(totalDataStr, BusAreaVisitSignDto.class);
        buildList(dataList, orderList,sumReadd,1);
        addSerialNum(dataList);
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);
        String name = "商务大区业绩表" +baseBusQueryDto.getStartTime()+"-"+baseBusQueryDto.getEndTime() + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    private List<Object> getTitleList(Integer type) {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("商务大区");
        headTitleList.add("商务组");
        headTitleList.add("首访数");
        headTitleList.add("签约数");
        headTitleList.add("净业绩金额");
        headTitleList.add("签约率");
        headTitleList.add("单笔签约");
        headTitleList.add("单笔来访");
        return headTitleList;
    }

    private void addTotalExportData(BusAreaVisitSignDto ra, List<List<Object>> dataList, Integer type) {
        List<Object> curList = new ArrayList<>();
        curList.add("合计");
        curList.add("");
        curList.add(ra.getBusinessGroupName());
        curList.add(ra.getFirstVisitNum());
        curList.add(ra.getSignNum());
        curList.add(ra.getAmount());
        curList.add(ra.getSignRate());
        curList.add(ra.getSignSingle());
        curList.add(ra.getFirstVisitMoney());
        dataList.add(curList);
    }

    private void buildList(List<List<Object>> dataList, List<BusAreaVisitSignDto> sourceDataList,List<BusAreaVisitSignDto> sumList,
                           Integer type) {
        Map<String, BusAreaVisitSignDto> sumMap = sumList.stream().collect(Collectors.toMap(BusAreaVisitSignDto::getGroupId, Function.identity()));
        TreeMap<String, List<BusAreaVisitSignDto>> sourceDataListTreeMap =
                sourceDataList.stream().collect(Collectors.groupingBy(BusAreaVisitSignDto::getBusinessAreaId, TreeMap::new, Collectors.toList()));
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

    private void buildData(List<List<Object>> dataList,List<BusAreaVisitSignDto> list,Integer type){
        for(int j = 0; j<list.size(); j++){
            List<Object> curList = new ArrayList<>();
            BusAreaVisitSignDto raInner = list.get(j);
            curList.add(raInner.getBusinessAreaName());
            curList.add(raInner.getBusinessGroupName());
            curList.add(raInner.getFirstVisitNum());
            curList.add(raInner.getSignNum());
            curList.add(raInner.getAmount());
            curList.add(raInner.getSignRate());
            curList.add(raInner.getSignSingle());
            curList.add(raInner.getFirstVisitMoney());
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

    public void initOrgList(HttpServletRequest request){
        String busAreaId="";// 当前商务大区
        String businessGroupId ="";//商务组
        String businessManagerId = "";//商务经理
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        //商务组
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        //TODO 后边修改为商务总经理
        if(RoleCodeEnum.SWZJ.name().equals(roleCode)){
            busAreaId = String.valueOf(curLoginUser.getOrgId());
            busGroupReqDTO.setParentId(curLoginUser.getOrgId());
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员可以查看全部
            logger.info("管理员登录");
        }else{
            //other 没权限
            busGroupReqDTO.setId(-1l);
        }
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> listJSONResult = organizationFeignClient.queryOrgByParam(busGroupReqDTO);
        List<OrganizationRespDTO> data = listJSONResult.getData();
        //TODO 后边修改为商务总经理
        if(RoleCodeEnum.SWZJ.name().equals(roleCode) || RoleCodeEnum.SWJL.name().equals(roleCode)){
            busAreaId = String.valueOf(data.get(0).getParentId());
        }
        request.setAttribute("busGroupList",data);


        //餐饮集团
        JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        request.setAttribute("companyList", listNoPage.getData());

        request.setAttribute("busAreaId",busAreaId);
        request.setAttribute("businessGroupId",businessGroupId);
        request.setAttribute("businessManagerId",businessManagerId);



    }
    /**
     * 按登录用户业务线查询-商务大区
     * @param request
     */
    protected void initBugOrg(HttpServletRequest request){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();

        //查询商务大区
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWDQ);
//        queryDTO.setBusinessLine(curLoginUser.getBusinessLine());
        if(RoleCodeEnum.SWDQZJ.name().equals(roleCode)){
            queryDTO.setId(curLoginUser.getOrgId());
            request.setAttribute("areaId",curLoginUser.getOrgId()+"");
        }else if(RoleCodeEnum.SWJL.name().equals(roleCode) || RoleCodeEnum.SWZJ.name().equals(roleCode)){
            if(RoleCodeEnum.SWJL.name().equals(roleCode)){
                request.setAttribute("managerId",curLoginUser.getId()+"");
            }
            OrganizationQueryDTO org = new OrganizationQueryDTO();
            org.setId(curLoginUser.getOrgId());
            request.setAttribute("busId",curLoginUser.getOrgId()+"");
            JSONResult<List<OrganizationRespDTO>> json =
                    organizationFeignClient.queryOrgByParam(org);
            if("0".equals(json.getCode())){
                Long parentId= json.getData().get(0).getParentId();
                queryDTO.setId(parentId);

                JSONResult<List<OrganizationRespDTO>> areajson=
                        organizationFeignClient.queryOrgByParam(queryDTO);
                if(areajson.getData().isEmpty()){
                    request.setAttribute("areaList",json.getData());
                    request.setAttribute("areaId",curLoginUser.getOrgId()+"");
                }else{
                    request.setAttribute("areaList",areajson.getData());
                    request.setAttribute("areaId",parentId+"");
                }
            }
            return ;
        }else if(RoleCodeEnum.GLY.name().equals(roleCode)){
            //管理员查询全部
        }else{
            //other
            queryDTO.setId(curLoginUser.getOrgId());
        }
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        request.setAttribute("areaList",queryOrgByParam.getData());
        request.setAttribute("roleCode",roleCode);
    }
}
