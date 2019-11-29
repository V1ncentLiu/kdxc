package com.kuaidao.manageweb.controller.visit;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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

import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.visit.TrackingOrderReqDTO;
import com.kuaidao.aggregation.dto.visit.TrackingOrderRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.feign.visit.TrackingOrderFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 邀約來訪派車單
 * 
 * @author Chen
 * @date 2019年3月5日 上午9:09:07
 * @version V1.0
 */
@Controller
@RequestMapping("/truckingOrder")
public class TruckingOrderController {

    private static Logger logger = LoggerFactory.getLogger(TruckingOrderController.class);

    @Autowired
    TrackingOrderFeignClient trackingOrderFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    /**
     * 邀約來訪派車單
     * 
     * @return
     */
    @RequiresPermissions("aggregation:truckingOrder:view")
    @RequestMapping("/truckingOrderPage")
    public String visitRecordPage(HttpServletRequest request) {
        // 电销人员
        /*List<UserInfoDTO> teleSaleList = getUserInfo(null, RoleCodeEnum.DXCYGW.name());
        request.setAttribute("teleSaleList", teleSaleList);*/
        // 查询所有项目
       /* JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());*/
        return "visit/truckingOrder";
    }

    private List<UserInfoDTO> getUserInfo(Long orgId, String roleName) {
        UserOrgRoleReq req = new UserOrgRoleReq();
        if (orgId != null) {
            req.setOrgId(orgId);
        }
        req.setRoleCode(roleName);
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error(
                    "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    req, userJr);
            return null;
        }
        return userJr.getData();
    }

    /**
     * 查询邀约来访派车单
     * 
     * @param reqDTO
     * @return
     */
    @RequiresPermissions("aggregation:truckingOrder:view")
    @PostMapping("/listTrackingOrder")
    @ResponseBody
    public JSONResult<PageBean<TrackingOrderRespDTO>> listTrackingOrder(
            @RequestBody TrackingOrderReqDTO reqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if (roleList != null && roleList.size() != 0) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            String roleCode = roleInfoDTO.getRoleCode();
            if (RoleCodeEnum.SWDQZJ.name().equals(roleCode) || RoleCodeEnum.SWZC.name().equals(roleCode)) {
                UserOrgRoleReq req = new UserOrgRoleReq();
                req.setOrgId(orgId);
                req.setRoleCode(RoleCodeEnum.SWZJ.name());
                JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
                    logger.error(
                            "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                            req, userJr);
                }
                List<UserInfoDTO> userInfoDTOList = userJr.getData();
                if (userInfoDTOList != null && userInfoDTOList.size() != 0) {
                    List<Long> idList = userInfoDTOList.stream().map(UserInfoDTO::getId)
                            .collect(Collectors.toList());
                    reqDTO.setBusDirectorIdList(idList);
                }
                Long accountId = reqDTO.getAccountId();
                if (accountId != null) {
                    List<Long> accountIdList = new ArrayList<>();
                    accountIdList.add(accountId);
                    reqDTO.setAccountIdList(accountIdList);
                }

            } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {// 商务总监
                List<Long> idList = new ArrayList<>();
                idList.add(curLoginUser.getId());
                reqDTO.setBusDirectorIdList(idList);
                Long accountId = reqDTO.getAccountId();
                if (accountId != null) {
                    List<Long> accountIdList = new ArrayList<>();
                    accountIdList.add(accountId);
                    reqDTO.setAccountIdList(accountIdList);
                }
            } else {
                return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                        "角色没有权限");
            }
        }
        
         /* List<Long> busList = new ArrayList<>(); busList.add(1098210933718835200L);
          reqDTO.setBusDirectorIdList(busList);*/
         
        return trackingOrderFeignClient.listTrackingOrder(reqDTO);
    }



    /**
     * 导出
     * 
     * @param reqDTO
     * @return
     */
    @RequiresPermissions("aggregation:truckingOrder:export")
    @PostMapping("/exportTrackingOrder")
    @LogRecord(description = "邀约来访派车单导出", operationType = OperationType.EXPORT,
        menuName = MenuEnum.TRUCKING_ORDER_PAGE)
    public void exportTrackingOrder(@RequestBody TrackingOrderReqDTO reqDTO,
            HttpServletResponse response) throws Exception {
        /*
         * List<Long> busList = new ArrayList<>(); busList.add(1098210933718835200L);
         * reqDTO.setBusDirectorIdList(busList);
         */
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if (roleList != null && roleList.size() != 0) {
            RoleInfoDTO roleInfoDTO = roleList.get(0);
            String roleCode = roleInfoDTO.getRoleCode();
            if (RoleCodeEnum.SWDQZJ.name().equals(roleCode)) {
                UserOrgRoleReq req = new UserOrgRoleReq();
                req.setOrgId(orgId);
                req.setRoleCode(RoleCodeEnum.SWZJ.name());
                JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
                if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
                    logger.error(
                            "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                            req, userJr);
                }
                List<UserInfoDTO> userInfoDTOList = userJr.getData();
                if (userInfoDTOList != null && userInfoDTOList.size() != 0) {
                    List<Long> idList = userInfoDTOList.stream().map(UserInfoDTO::getId)
                            .collect(Collectors.toList());
                    reqDTO.setBusDirectorIdList(idList);
                }
                Long accountId = reqDTO.getAccountId();
                if (accountId != null) {
                    List<Long> accountIdList = new ArrayList<>();
                    accountIdList.add(accountId);
                    reqDTO.setAccountIdList(accountIdList);
                }

            } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {// 商务总监
                List<Long> idList = new ArrayList<>();
                idList.add(curLoginUser.getId());
                reqDTO.setBusDirectorIdList(idList);
                Long accountId = reqDTO.getAccountId();
                if (accountId != null) {
                    List<Long> accountIdList = new ArrayList<>();
                    accountIdList.add(accountId);
                    reqDTO.setAccountIdList(accountIdList);
                }
            }


        }

        JSONResult<List<TrackingOrderRespDTO>> trackingOrderListJr =
                trackingOrderFeignClient.exportTrackingOrder(reqDTO);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());

        if (JSONResult.SUCCESS.equals(trackingOrderListJr.getCode())
                && trackingOrderListJr.getData() != null
                && trackingOrderListJr.getData().size() != 0) {

            List<TrackingOrderRespDTO> orderList = trackingOrderListJr.getData();
            int size = orderList.size();

            List<ProjectInfoDTO> allProjectList = getAllProjectList();
            for (int i = 0; i < size; i++) {
                TrackingOrderRespDTO trackingOrderRespDTO = orderList.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(i + 1);
                curList.add(getTimeStr(trackingOrderRespDTO.getCreateTime()));
                curList.add(trackingOrderRespDTO.getSubmitGroup());
                curList.add(trackingOrderRespDTO.getTelSaleName());
                curList.add(trackingOrderRespDTO.getAccountPhone());
                curList.add(getTimeStr(trackingOrderRespDTO.getReserveTime()));
                curList.add(trackingOrderRespDTO.getCustomerName());
                curList.add(trackingOrderRespDTO.getCusPhone());
                curList.add(trackingOrderRespDTO.getCusNum());
                curList.add(trackingOrderRespDTO.getCusName());
                curList.add(getTimeStr(trackingOrderRespDTO.getDepartTime()));
                curList.add(trackingOrderRespDTO.getCity());
                curList.add(trackingOrderRespDTO.getFlight());
                curList.add(getTimeStr(trackingOrderRespDTO.getArrivalTime()));
                curList.add(trackingOrderRespDTO.getPickUpPlace());
                curList.add(getTimeStr(trackingOrderRespDTO.getPickUpTime()));
                curList.add(trackingOrderRespDTO.getDelayMark());
                curList.add(trackingOrderRespDTO.getBusCompanyName());
                curList.add(getProjectNameStr(allProjectList,
                        trackingOrderRespDTO.getTasteProjectId()));
                curList.add(trackingOrderRespDTO.getBusDirectorName());
        /*        curList.add(trackingOrderRespDTO.getTerminal());
                curList.add(trackingOrderRespDTO.getReceivedPlace());*/
                dataList.add(curList);
            }

        } else {
            logger.error("export trucking_order param{{}},res{{}}", reqDTO, trackingOrderListJr);
        }

        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);


        String name = "派车单" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }

    /*
     * 獲取所有的項目
     */
    private List<ProjectInfoDTO> getAllProjectList() {
        JSONResult<List<ProjectInfoDTO>> projectInfoJr = projectInfoFeignClient.allProject();
        return projectInfoJr.getData();
    }

    private String getProjectNameStr(List<ProjectInfoDTO> projectInfoList, String ids) {
        if (StringUtils.isBlank(ids) || projectInfoList == null) {
            return "";
        }
        String text = "";
        String[] idArr = ids.split(",");
        for (int i = 0; i < idArr.length; i++) {
            for (int j = 0; j < projectInfoList.size(); j++) {
                ProjectInfoDTO projectInfoDTO = projectInfoList.get(j);
                if (Long.valueOf(idArr[i]).equals(projectInfoDTO.getId())) {
                    if (i == 0) {
                        text = projectInfoDTO.getProjectName();
                    } else {
                        text += "," + projectInfoDTO.getProjectName();
                    }
                }

            }
        }

        return text;

    }


    private String getTimeStr(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.convert2String(date, DateUtil.ymdhms);
    }


    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("申请提交时间");
        headTitleList.add("申请电销组");
        headTitleList.add("申请电销顾问");
        headTitleList.add("申请顾问电话");
        headTitleList.add("邀约来访时间");
        headTitleList.add("客户姓名");
        headTitleList.add("联系方式");
        headTitleList.add("客户人数");
        headTitleList.add("联系人姓名");
        headTitleList.add("出发时间");
        headTitleList.add("出车城市");
        headTitleList.add("车次／航班");
        headTitleList.add("到站时间");
        headTitleList.add("接站地");
        headTitleList.add("接站时间");
        headTitleList.add("延误说明");
        headTitleList.add("餐饮公司");
        headTitleList.add("品尝项目");
        headTitleList.add("商务总监");
//        headTitleList.add("航站楼");
//        headTitleList.add("接到地（酒店／公司）");
        return headTitleList;
    }
    
    /**
     * 查询所有的电销人员
     * @param result
     * @return
     */
    @PostMapping("/queryAllTeleSales")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> queryAllTeleSales() {
     // 电销人员
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setRoleCode(RoleCodeEnum.DXCYGW.name());
        return userInfoFeignClient.listByOrgAndRole(req);
    }


}
