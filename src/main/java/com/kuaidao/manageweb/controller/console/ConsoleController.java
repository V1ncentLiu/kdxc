package com.kuaidao.manageweb.controller.console;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.kuaidao.manageweb.service.NextRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerRespDTO;
import com.kuaidao.aggregation.dto.busmycustomer.MyCustomerParamDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordReqDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordRespDTO;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationDTO;
import com.kuaidao.aggregation.dto.clue.BusPendingAllocationPageParam;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationClueDTO;
import com.kuaidao.aggregation.dto.clue.PendingAllocationCluePageParam;
import com.kuaidao.aggregation.dto.console.BusinessConsolePanelRespDTO;
import com.kuaidao.aggregation.dto.console.BusinessConsoleReqDTO;
import com.kuaidao.aggregation.dto.console.BusinessDirectorConsolePanelRespDTO;
import com.kuaidao.aggregation.dto.console.TeleConsoleReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordReqDTO;
import com.kuaidao.aggregation.dto.visitrecord.VisitRecordRespDTO;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.CluePhase;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.dashboard.dto.bussale.BusGroupDTO;
import com.kuaidao.dashboard.dto.bussale.BusSaleDTO;
import com.kuaidao.dashboard.dto.tele.DashboardTeleGroupDto;
import com.kuaidao.dashboard.dto.tele.DashboardTeleSaleDto;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.BusReceiveFeignClient;
import com.kuaidao.manageweb.feign.busgroup.BusGroupDashboardFeignClient;
import com.kuaidao.manageweb.feign.busmycustomer.BusMyCustomerFeignClient;
import com.kuaidao.manageweb.feign.clue.AppiontmentFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.clue.PendingVisitFeignClient;
import com.kuaidao.manageweb.feign.console.sale.DashboardSaleFeignClient;
import com.kuaidao.manageweb.feign.console.sale.DashboardTeleSaleFeignClient;
import com.kuaidao.manageweb.feign.dashboard.DashboardTeleGroupFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.sign.SignRecordFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.feign.visit.VisitRecordFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveRespDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveQueryDTO;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveRespDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * 控制台
 *
 * @author Chen
 * @date 2019年3月16日 下午2:27:31
 * @version V1.0
 */

@Controller
@RequestMapping("/console/console")
public class ConsoleController {
    private static Logger logger = LoggerFactory.getLogger(ConsoleController.class);

    @Autowired
    AnnReceiveFeignClient annReceiveFeignClient;

    @Autowired
    BusReceiveFeignClient busReceiveFeignClient;

    @Autowired
    ClueBasicFeignClient clueBasicFeignClient;

    @Autowired
    AppiontmentFeignClient appiontmentFeignClient;


    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    MyCustomerFeignClient myCustomerFeignClient;

    @Autowired
    VisitRecordFeignClient visitRecordFeignClient;

    @Autowired
    SignRecordFeignClient signRecordFeignClient;

    @Autowired
    PendingVisitFeignClient pendingVisitFeignClient;

    @Autowired
    BusMyCustomerFeignClient busMyCustomerFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private BusGroupDashboardFeignClient busGroupDashboardFeignClient;
    @Autowired
    DashboardSaleFeignClient dashboardSaleFeignClient;
    @Autowired
    DashboardTeleSaleFeignClient dashboardTeleSaleFeignClient;
    @Autowired
    private DashboardTeleGroupFeignClient dashboardTeleGroupFeignClient;
    @Autowired
    private NextRecordService nextRecordService;

    /***
     * 跳转控制台页面
     *
     * @return
     */
    @RequestMapping("/index")
    public String index(String type, @RequestParam(required = false) Integer sourceType, HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        String path = "";

        if (RoleCodeEnum.DXCYGW.name().equals(roleCode)) {
            IdEntityLong idEntityLong = new IdEntityLong();
            idEntityLong.setId(curLoginUser.getId());
            JSONResult<DashboardTeleSaleDto> jsonResult =
                    dashboardTeleSaleFeignClient.findDashboardTeleSaleByUserId(idEntityLong);
            DashboardTeleSaleDto dashboardTeleSaleDto = new DashboardTeleSaleDto();
            if (JSONResult.SUCCESS.equals(jsonResult.getCode()) && jsonResult.getData() != null) {
                dashboardTeleSaleDto = jsonResult.getData();
            }
            request.setAttribute("dashboardTelSale", dashboardTeleSaleDto);
            // 电销顾问
            path = "console/consoleTelemarketing";
        } else if (RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            // 电销总监
            // 如果当前登录的为电销总监,查询所有下属电销员工
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            statusList.add(SysConstant.USER_STATUS_LOCK);
            List<UserInfoDTO> userList = getUserList(orgId, RoleCodeEnum.DXCYGW.name(), statusList);
            request.setAttribute("saleList", userList);
            IdEntityLong idEntityLong = new IdEntityLong();
            idEntityLong.setId(curLoginUser.getOrgId());
            JSONResult<DashboardTeleGroupDto> dashboard =
                    dashboardTeleGroupFeignClient.findTeleGroupDataByOrgId(idEntityLong);
            DashboardTeleGroupDto data = dashboard.getData();
            if (data == null) {
                data = new DashboardTeleGroupDto();
            }
            request.setAttribute("dashboardTeleGroup", data);
            // 查询字典类别集合
            request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
            // 查询字典类别集合
            request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));

            path = "console/consoleTelMajordomo";
        } else if (RoleCodeEnum.SWJL.name().equals(roleCode)) {
            // 商务经理
            // 项目
            ProjectInfoPageParam param = new ProjectInfoPageParam();
            param.setIsNotSign(-1);
            JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
            if (JSONResult.SUCCESS.equals(proJson.getCode())) {
                request.setAttribute("proSelect", proJson.getData());
            }

            IdEntityLong idEntityLong = new IdEntityLong();
            idEntityLong.setId(curLoginUser.getId());
            JSONResult<BusSaleDTO> dashboard =
                    dashboardSaleFeignClient.findDataByUserId(idEntityLong);
            BusSaleDTO data = dashboard.getData();
            if (data == null) {
                data = new BusSaleDTO();
            }
            request.setAttribute("dashboardSale", data);
            // 查询赠送类型集合
            request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
            path = "console/consoleBusinessManager";
        } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {
            // 商务总监
            // 查询所有商务经理
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            statusList.add(SysConstant.USER_STATUS_LOCK);
            List<UserInfoDTO> saleList = getUserList(orgId, RoleCodeEnum.SWJL.name(), statusList);
            request.setAttribute("busSaleList", saleList);
            // 查询所有项目
            JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
            request.setAttribute("projectList", allProject.getData());
            // 查询字典选址情况集合
            request.setAttribute("optionAddressList",
                    getDictionaryByCode(Constants.OPTION_ADDRESS));
            // 查询字典店铺面积集合
            request.setAttribute("storefrontAreaList",
                    getDictionaryByCode(Constants.STOREFRONT_AREA));
            // 查询字典投资金额集合
            request.setAttribute("ussmList", getDictionaryByCode(Constants.USSM));
            request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
            request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
            path = "console/consoleBusinessMajordomo";
        }



        /*
         * if(type.equals("1")) { path = "console/consoleTelemarketing"; }else if(type.equals("2"))
         * { List<Integer> statusList = new ArrayList<Integer>();
         * statusList.add(SysConstant.USER_STATUS_ENABLE);
         * statusList.add(SysConstant.USER_STATUS_LOCK); List<UserInfoDTO> userList =
         * getUserList(orgId, RoleCodeEnum.DXCYGW.name(), statusList);
         * request.setAttribute("saleList", userList); // 查询字典类别集合
         * request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
         * // 查询字典类别集合 request.setAttribute("clueTypeList",
         * getDictionaryByCode(Constants.CLUE_TYPE)); path="console/consoleTelMajordomo"; }else
         * if(type.equals("3")) { // 项目 ProjectInfoPageParam param = new ProjectInfoPageParam();
         * JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
         * if(JSONResult.SUCCESS.equals(proJson.getCode())){ request.setAttribute("proSelect",
         * proJson.getData()); }
         *
         * path="console/consoleBusinessManager"; }else if(type.equals("4")) { // 查询所有商务经理
         * List<Map<String, Object>> allSaleList = getAllSaleList();
         * request.setAttribute("allSaleList", allSaleList); // 查询组织下商务经理 List<Integer> statusList =
         * new ArrayList<Integer>(); statusList.add(SysConstant.USER_STATUS_ENABLE);
         * statusList.add(SysConstant.USER_STATUS_LOCK); List<UserInfoDTO> saleList =
         * getUserList(orgId, RoleCodeEnum.SWJL.name(), statusList);
         * request.setAttribute("busSaleList", saleList); // 查询所有项目 JSONResult<List<ProjectInfoDTO>>
         * listNoPage = projectInfoFeignClient.listNoPage(new ProjectInfoPageParam());
         * request.setAttribute("projectList", listNoPage.getData()); // 查询字典选址情况集合
         * request.setAttribute("optionAddressList", getDictionaryByCode(Constants.OPTION_ADDRESS));
         * // 查询字典店铺面积集合 request.setAttribute("storefrontAreaList",
         * getDictionaryByCode(Constants.STOREFRONT_AREA)); // 查询字典投资金额集合
         * request.setAttribute("ussmList", getDictionaryByCode(Constants.USSM));
         *
         * path="console/consoleBusinessMajordomo"; }
         */
        request.setAttribute("type", type);
        request.setAttribute("sourceType", sourceType);
        return path;
    }



    /**
     * 查询公告 --不 带分页
     *
     * @param
     * @return
     */
    @PostMapping("/queryAnnReceiveNoPage")
    @ResponseBody
    public JSONResult<List<AnnReceiveRespDTO>> queryAnnReceiveNoPage() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        AnnReceiveQueryDTO queryDTO = new AnnReceiveQueryDTO();
        queryDTO.setReceiveUser(curLoginUser.getId());
        Date curDate = new Date();
        Date addDays = DateUtil.addDays(curDate, -7);
        queryDTO.setDate1(addDays);
        queryDTO.setDate2(curDate);
        return annReceiveFeignClient.queryAnnReceiveNoPage(queryDTO);
    }

    /**
     * 控制台使用 查询业务消息 -- 不带分页
     *
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryBussReceiveNoPage")
    @ResponseBody
    public JSONResult<List<BussReceiveRespDTO>> queryBussReceiveNoPage(
            @RequestBody BussReceiveQueryDTO queryDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDTO.setReceiveUser(curLoginUser.getId());
        return busReceiveFeignClient.queryBussReceiveNoPage(queryDTO);
    }


    /**
     * 统计电销人员今日分配资源数
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/countAssignClueNum")
    @ResponseBody
    public JSONResult<Integer> countAssignClueNum(@RequestBody TeleConsoleReqDTO reqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setTeleSaleId(curLoginUser.getId());
        reqDTO.setType(1);
        Date curDate = new Date();
        reqDTO.setEndTime(curDate);
        reqDTO.setStartTime(DateUtil.getCurStartDate());
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add(AggregationConstant.SALE_RECEIVE_SOURCE.SOURCE1);
        sourceList.add(AggregationConstant.SALE_RECEIVE_SOURCE.SOURCE2);
        reqDTO.setPhase(CluePhase.PHAE_4TH.getCode());
        reqDTO.setSourceList(sourceList);
        return clueBasicFeignClient.countAssignClueNum(reqDTO);
    }


    /**
     * 统计电销人员今日領取数
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/countReceiveClueNum")
    @ResponseBody
    public JSONResult<Integer> countReceiveClueNum(@RequestBody TeleConsoleReqDTO reqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setTeleSaleId(curLoginUser.getId());
        Date curDate = new Date();
        reqDTO.setEndTime(curDate);
        // reqDTO.setStartTime(DateUtil.getCurStartDate());
        reqDTO.setStartTime(DateUtil.getTodayStartTime());
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add(AggregationConstant.SALE_RECEIVE_SOURCE.SOURCE3);
        reqDTO.setSourceList(sourceList);
        // type 1：日期字段用电销的
        reqDTO.setType(1);
        reqDTO.setPhase(CluePhase.PHAE_4TH.getCode());
        return clueBasicFeignClient.countAssignClueNum(reqDTO);
    }



    /***
     * 控制台 今日邀约单 不包括 删除的
     *
     * @param
     * @return
     */
    @PostMapping("/countTodayAppiontmentNum")
    @ResponseBody
    public JSONResult<Integer> countTodayAppiontmentNum() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        TeleConsoleReqDTO teleConsoleReqDTO = new TeleConsoleReqDTO();
        Long id = curLoginUser.getId();
        teleConsoleReqDTO.setTeleSaleId(id);
        teleConsoleReqDTO.setStartTime(DateUtil.getTodayStartTime());
        teleConsoleReqDTO.setEndTime(new Date());
        return appiontmentFeignClient.countTodayAppiontmentNum(teleConsoleReqDTO);
    }


    /**
     * 查询电销人员 待跟进客户资源
     *
     * @param queryDto
     * @return
     */
    @PostMapping("/listTodayFollowClue")
    @ResponseBody
    public JSONResult<PageBean<CustomerClueDTO>> listTodayFollowClue(
            @RequestBody CustomerClueQueryDTO queryDto) throws Exception {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDto.setTeleSale(curLoginUser.getId());
        Date curDate = new Date();
        queryDto.setAssignTimeStart(DateUtil.getStartOrEndOfDay(curDate, LocalTime.MIN));
        queryDto.setAssignTimeEnd(DateUtil.getStartOrEndOfDay(curDate, LocalTime.MAX));
        /*
         * queryDto.setTeleSale(33336666L); queryDto.setAssignTimeStart(DateUtil.convert2Date(
         * "2019-01-14 00:00:00", DateUtil.ymdhms));
         * queryDto.setAssignTimeEnd(DateUtil.convert2Date("2019-04-14 00:00:00", DateUtil.ymdhms));
         */
        return myCustomerFeignClient.listTodayFollowClue(queryDto);
    }

    /**
     * 查询电销人员 待跟进客户资源
     *
     * @param queryDto
     * @return
     */
    @PostMapping("/telelistTodayFollowClue")
    @ResponseBody
    public JSONResult<PageBean<CustomerClueDTO>> telelistTodayFollowClue(
            @RequestBody CustomerClueQueryDTO queryDto) throws Exception {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        queryDto.setTeleSale(curLoginUser.getId());
        /*
         * queryDto.setTeleSale(33336666L); queryDto.setAssignTimeStart(DateUtil.convert2Date(
         * "2019-01-14 00:00:00", DateUtil.ymdhms));
         * queryDto.setAssignTimeEnd(DateUtil.convert2Date("2019-04-14 00:00:00", DateUtil.ymdhms));
         */
        JSONResult<PageBean<CustomerClueDTO>> jsonResult = myCustomerFeignClient.telelistTodayFollowClue(queryDto);

        nextRecordService.pushList(curLoginUser.getId() , null != jsonResult.getData() ? jsonResult.getData().getData() : null);

        return jsonResult ;
    }

    /**
     * 电销总监未分配资源数
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/countTeleDircortoerUnAssignClueNum")
    @ResponseBody
    public JSONResult<Integer> countTeleDircortoerUnAssignClueNum(
            @RequestBody TeleConsoleReqDTO reqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setTeleDirectorId(curLoginUser.getId());
        reqDTO.setPhase(CluePhase.PHAE_3RD.getCode());
        /*
         * Date curDate = new Date(); reqDTO.setEndTime(curDate);
         * reqDTO.setStartTime(DateUtil.getTodayStartTime()); //type 为2 表示查询时字段用电销总监的时间字段 为1
         * 用电销顾问时间字段 reqDTO.setType(2);
         */
        return clueBasicFeignClient.countAssignClueNum(reqDTO);
    }


    /**
     * 电销总监今日接受资源数
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/countTeleDircortoerReceiveClueNum")
    @ResponseBody
    public JSONResult<Integer> countTeleDircortoerReceiveClueNum(
            @RequestBody TeleConsoleReqDTO reqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setTeleDirectorId(curLoginUser.getId());
        Date curDate = new Date();
        reqDTO.setEndTime(curDate);
        reqDTO.setStartTime(DateUtil.getTodayStartTime());
        reqDTO.setType(2);
        List<Integer> teleDirectorSourceList = new ArrayList<>();
        teleDirectorSourceList.add(AggregationConstant.DIRECTOR_RECEIVE_SOURCE.SOURCE2);
        teleDirectorSourceList.add(AggregationConstant.DIRECTOR_RECEIVE_SOURCE.SOURCE3);
        reqDTO.setTeleDirectorSourceList(teleDirectorSourceList);
        reqDTO.setPhase(CluePhase.PHAE_3RD.getCode());
        return clueBasicFeignClient.countAssignClueNum(reqDTO);
    }

    /**
     * 电销总监今日领取资源数
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/countTeleDircortoerGetClueNum")
    @ResponseBody
    public JSONResult<Integer> countTeleDircortoerGetClueNum(
            @RequestBody TeleConsoleReqDTO reqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        reqDTO.setTeleDirectorId(curLoginUser.getId());
        Date curDate = new Date();
        reqDTO.setEndTime(curDate);
        reqDTO.setStartTime(DateUtil.getTodayStartTime());
        reqDTO.setType(2);
        List<Integer> teleDirectorSourceList = new ArrayList<>();
        teleDirectorSourceList.add(AggregationConstant.DIRECTOR_RECEIVE_SOURCE.SOURCE1);
        reqDTO.setTeleDirectorSourceList(teleDirectorSourceList);
        return clueBasicFeignClient.countAssignClueNum(reqDTO);
    }

    /**
     * 电销总监 今日邀约数
     *
     * @return
     */
    @PostMapping("/countTeleDirectorTodayAppiontmentNum")
    @ResponseBody
    public JSONResult<Integer> countTeleDirectorTodayAppiontmentNum() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        TeleConsoleReqDTO teleConsoleReqDTO = new TeleConsoleReqDTO();
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setOrgId(curLoginUser.getOrgId());
        req.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> userInfoJr = userInfoFeignClient.listByOrgAndRole(req);
        if (!JSONResult.SUCCESS.equals(userInfoJr.getCode())) {
            return new JSONResult<Integer>().fail(userInfoJr.getCode(), userInfoJr.getMsg());
        }
        List<UserInfoDTO> data = userInfoJr.getData();
        if (CollectionUtils.isEmpty(data)) {
            return new JSONResult<Integer>().success(0);
        }
        List<Long> teleSaleIdList =
                data.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
        teleConsoleReqDTO.setTeleSaleIdList(teleSaleIdList);
        teleConsoleReqDTO.setStartTime(DateUtil.getTodayStartTime());
        teleConsoleReqDTO.setEndTime(new Date());
        return appiontmentFeignClient.countTodayAppiontmentNum(teleConsoleReqDTO);
    }


    /**
     * 电销总监 预计明日到访数
     *
     * @return
     */
    @PostMapping("/countTeleDirecotorTomorrowArriveTime")
    @ResponseBody
    public JSONResult<Integer> countTeleDirecotorTomorrowArriveTime(
            @RequestBody TeleConsoleReqDTO reqDTO) {

        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        TeleConsoleReqDTO teleConsoleReqDTO = new TeleConsoleReqDTO();
        Long id = curLoginUser.getOrgId();
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setOrgId(id);
        req.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> userInfoJr = userInfoFeignClient.listByOrgAndRole(req);
        if (!JSONResult.SUCCESS.equals(userInfoJr.getCode())) {
            return new JSONResult<Integer>().fail(userInfoJr.getCode(), userInfoJr.getMsg());
        }
        List<UserInfoDTO> data = userInfoJr.getData();
        if (CollectionUtils.isEmpty(data)) {
            return new JSONResult<Integer>().success(0);
        }
        List<Long> teleSaleIdList =
                data.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
        teleConsoleReqDTO.setTeleSaleIdList(teleSaleIdList);
        Date curDate = new Date();
        Date nextDate = DateUtil.addDays(curDate, 1);
        teleConsoleReqDTO.setStartTime(DateUtil.getStartOrEndOfDay(nextDate, LocalTime.MIN));
        teleConsoleReqDTO.setEndTime(DateUtil.getStartOrEndOfDay(nextDate, LocalTime.MAX));
        return appiontmentFeignClient.countTeleDirecotorTomorrowArriveTime(teleConsoleReqDTO);
    }

    /**
     * 电销总监 查询待分配资源
     *
     * @param pageParam
     * @return
     */
    @PostMapping("/listUnAssignClue")
    @ResponseBody
    public JSONResult<PageBean<PendingAllocationClueDTO>> listUnAssignClue(
            @RequestBody PendingAllocationCluePageParam pageParam) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        pageParam.setUserId(curLoginUser.getId());
        pageParam.setOrgId(curLoginUser.getOrgId());
        return clueBasicFeignClient.listUnAssignClue(pageParam);
    }



    /**
     * 商务经理 看板统计 当月到访数 当月签约数 当月二次到访数 当月二次来访签约数 未收齐尾款笔数
     *
     * @return
     */
    @RequestMapping("/countCurMonthNum")
    @ResponseBody
    public JSONResult<BusinessConsolePanelRespDTO> countCurMonthVisitedNum(
            @RequestBody BusinessConsoleReqDTO businessConsoleReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        List<Long> accountIdList = new ArrayList<Long>();
        accountIdList.add(curLoginUser.getId());
        businessConsoleReqDTO.setAccountIdList(accountIdList);
        Date curDate = new Date();
        businessConsoleReqDTO.setEndTime(curDate);
        businessConsoleReqDTO.setStartTime(DateUtil.getCurStartDate());
        return visitRecordFeignClient.countCurMonthNum(businessConsoleReqDTO);
    }


    /**
     * 商务经理控制台 待处理邀约来访记录
     *
     * @param param
     * @return
     */
    @PostMapping("/listPendingInviteCustomer")
    @ResponseBody
    public JSONResult<PageBean<BusMyCustomerRespDTO>> listPendingInviteCustomer(
            @RequestBody MyCustomerParamDTO param) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        param.setBusSaleId(user.getId());
        // param.setBusSaleId(1084621842175623168L);

        return busMyCustomerFeignClient.listPendingInviteCustomer(param);

    }

    /*   *//**
            * 商务经理当月签约数
            *
            * @param businessConsoleReqDTO
            * @return
            *//*
              * @RequestMapping("/countCurMonthSignedNum")
              *
              * @ResponseBody public JSONResult<BusinessConsolePanelRespDTO>
              * countCurMonthSignedNum(@RequestBody BusinessConsoleReqDTO businessConsoleReqDTO){
              * UserInfoDTO curLoginUser = CommUtil.getCurLoginUser(); List<Long> accountIdList =
              * new ArrayList<Long>(); accountIdList.add(curLoginUser.getId()); Date curDate = new
              * Date(); businessConsoleReqDTO.setEndTime(curDate);
              * businessConsoleReqDTO.setStartTime(DateUtil.getCurStartDate()); return
              * signRecordFeignClient.countCurMonthSignedNum(businessConsoleReqDTO); }
              */


    /**
     * 商务总监 1. 待分配任务数 9当月二次到访数 10 当月二次来访签约数： 看板统计
     *
     * @return
     */
    @RequestMapping("/countBusinessDirectorCurMonthNum")
    @ResponseBody
    public JSONResult<BusinessDirectorConsolePanelRespDTO> countBusinessDirectorCurMonthNum(
            @RequestBody BusinessConsoleReqDTO businessConsoleReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();

        List<Long> businessGroupIdList = new ArrayList<>();
        businessGroupIdList.add(curLoginUser.getOrgId());
        // reqDTO.setBusinessGroupIdList(businessGroupIdList);
        List<Long> accountIdList =
                getAccountIdList(curLoginUser.getOrgId(), RoleCodeEnum.SWJL.name());
        if (CollectionUtils.isEmpty(accountIdList)) {
            return new JSONResult<BusinessDirectorConsolePanelRespDTO>().success(null);
        }
        businessConsoleReqDTO.setAccountIdList(accountIdList);
        businessConsoleReqDTO.setBusinessGroupId(curLoginUser.getOrgId());
        businessConsoleReqDTO.setBusDirectorId(curLoginUser.getId());
        Date curDate = new Date();
        businessConsoleReqDTO.setEndTime(curDate);
        // 本月第一天 00
        businessConsoleReqDTO.setStartTime(DateUtil.getCurStartDate());
        return visitRecordFeignClient.countBusinessDirectorCurMonthNum(businessConsoleReqDTO);
    }

    /**
     * 商务总监 4预计明日到访数
     *
     * @param businessConsoleReqDTO
     * @return
     */
    @PostMapping("/countBusiDirecotorTomorrowArriveTime")
    @ResponseBody
    public JSONResult<Integer> countBusiDirecotorTomorrowArriveTime(
            @RequestBody BusinessConsoleReqDTO businessConsoleReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long id = curLoginUser.getOrgId();
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setOrgId(id);
        req.setRoleCode(RoleCodeEnum.SWJL.name());
        JSONResult<List<UserInfoDTO>> userInfoJr = userInfoFeignClient.listByOrgAndRole(req);
        if (!JSONResult.SUCCESS.equals(userInfoJr.getCode())) {
            logger.error(
                    "countBusiDirecotorTomorrowArriveTime  userInfoFeignClient.listByOrgAndRole({}),res{{}}",
                    req, userInfoJr);
            return new JSONResult<Integer>().fail(userInfoJr.getCode(), userInfoJr.getMsg());
        }
        logger.info("countBusiDirecotorTomorrowArriveTime UserOrgRoleReq_{} {{}}", id, userInfoJr);
        List<UserInfoDTO> data = userInfoJr.getData();
        if (CollectionUtils.isEmpty(data)) {
            return new JSONResult<Integer>().success(0);
        }
        List<Long> teleSaleIdList =
                data.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
        businessConsoleReqDTO.setAccountIdList(teleSaleIdList);
        businessConsoleReqDTO.setBusDirectorId(curLoginUser.getId());
        Date curDate = new Date();
        Date nextDate = DateUtil.addDays(curDate, 1);
        businessConsoleReqDTO.setStartTime(DateUtil.getStartOrEndOfDay(nextDate, LocalTime.MIN));
        businessConsoleReqDTO.setEndTime(DateUtil.getStartOrEndOfDay(nextDate, LocalTime.MAX));

        return appiontmentFeignClient.countBusiDirecotorTomorrowArriveTime(businessConsoleReqDTO);
    }


    /***
     * 商务总监 待分配来访客户列表
     *
     * @return
     */
    @PostMapping("/pendingVisitListNoPage")
    @ResponseBody
    public JSONResult<List<BusPendingAllocationDTO>> pendingVisitListNoPage(
            @RequestBody BusPendingAllocationPageParam pageParam, HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(curLoginUser.getId());
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }

        pageParam.setDelFalg(AggregationConstant.INVITIT_DEL_FALG.NORMAL);

        JSONResult<List<BusPendingAllocationDTO>> pendingAllocationList =
                pendingVisitFeignClient.pendingVisitListNoPage(pageParam);

        return pendingAllocationList;
    }


    /**
     * 商务总监 待审批到访记录 待审批未到访记录
     *
     * @param visitRecordReqDTO isVisit:是否到访
     * @return
     */
    @PostMapping("/listVisitRecord")
    @ResponseBody
    public JSONResult<List<VisitRecordRespDTO>> listVisitRecord(
            @RequestBody VisitRecordReqDTO visitRecordReqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        /*
         * List<Long> busGroupIdList = new ArrayList<>();
         * busGroupIdList.add(curLoginUser.getOrgId());
         * visitRecordReqDTO.setBusGroupIdList(busGroupIdList);
         */

        /*
         * List<Long> accountIdList = getAccountIdList(curLoginUser.getOrgId(),
         * RoleCodeEnum.SWJL.name()); if (CollectionUtils.isEmpty(accountIdList)) { return new
         * JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "该用户下没有下属"); }
         * visitRecordReqDTO.setBusManagerIdList(accountIdList); visitRecordReqDTO.setStatus(1);
         */

        // 商务经理外调，发起外调的商务总监进行审核,根据组id查询
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        List<Long> busGroupIdList = new ArrayList<>();
        if (RoleCodeEnum.SWDQZJ.name().equals(roleCode)) {
            // 查询下级所有商务组
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setParentId(orgId);
            queryDTO.setOrgType(OrgTypeConstant.SWZ);
            JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                    organizationFeignClient.queryOrgByParam(queryDTO);
            List<OrganizationRespDTO> data = queryOrgByParam.getData();
            if (CollectionUtils.isEmpty(data)) {
                return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                        "该用户下没有下属");
            }
            busGroupIdList =
                    data.stream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
            visitRecordReqDTO.setBusGroupIdList(busGroupIdList);
            visitRecordReqDTO.setStatus(1);
        } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {
            // 商务经理外调，发起外调的商务总监进行审核,根据组id查询
            List<Long> accountIdList = getAccountIdList(orgId, RoleCodeEnum.SWJL.name());
            if (CollectionUtils.isEmpty(accountIdList)) {
                return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                        "该用户下没有下属");
            }
            busGroupIdList.add(orgId);
            visitRecordReqDTO.setBusGroupIdList(busGroupIdList);
            visitRecordReqDTO.setStatus(1);
        } else {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }

        // 待审核
        // visitRecordReqDTO.setVisitStatus(1);
        return visitRecordFeignClient.listVisitRecordNoPage(visitRecordReqDTO);
    }



    /**
     * 商务总监 待审批签约记录
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/listSignRecord")
    @ResponseBody
    public JSONResult<List<SignRecordRespDTO>> listSignRecord(
            @RequestBody SignRecordReqDTO reqDTO) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        /*
         * List<Long> businessGroupIdList = new ArrayList<>();
         * businessGroupIdList.add(curLoginUser.getOrgId()); //
         * reqDTO.setBusinessGroupIdList(businessGroupIdList); List<Long> accountIdList =
         * getAccountIdList(curLoginUser.getOrgId(), RoleCodeEnum.SWJL.name()); if
         * (CollectionUtils.isEmpty(accountIdList)) { // return new //
         * JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"该用户下没有下属"); return new
         * JSONResult<List<SignRecordRespDTO>>().success(new ArrayList<>()); }
         * reqDTO.setBusinessManagerIdList(accountIdList);
         */

        // 商务经理外调，发起外调的商务总监进行审核,根据组id查询
        Long orgId = curLoginUser.getOrgId();
        List<RoleInfoDTO> roleList = curLoginUser.getRoleList();
        RoleInfoDTO roleInfoDTO = roleList.get(0);
        String roleCode = roleInfoDTO.getRoleCode();
        Long businessGroupId = reqDTO.getBusinessGroupId();
        List<Long> businessGroupIdList = new ArrayList<>();
        if (RoleCodeEnum.SWDQZJ.name().equals(roleCode)) {
            // 查询下级所有商务组
            OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
            queryDTO.setParentId(orgId);
            queryDTO.setOrgType(OrgTypeConstant.SWZ);
            JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                    organizationFeignClient.queryOrgByParam(queryDTO);
            List<OrganizationRespDTO> data = queryOrgByParam.getData();
            if (CollectionUtils.isEmpty(data)) {
                return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                        "该用户下没有下属");
            }
            businessGroupIdList =
                    data.stream().map(OrganizationRespDTO::getId).collect(Collectors.toList());
            reqDTO.setBusinessGroupIdList(businessGroupIdList);
        } else if (RoleCodeEnum.SWZJ.name().equals(roleCode)) {
            List<Long> accountIdList = getAccountIdList(orgId, RoleCodeEnum.SWJL.name());
            if (CollectionUtils.isEmpty(accountIdList)) {
                return new JSONResult().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),
                        "该用户下没有下属");
            }
            businessGroupIdList.add(orgId);
            reqDTO.setBusinessGroupIdList(businessGroupIdList);
        }
        reqDTO.setStatus(AggregationConstant.SIGN_ORDER_STATUS.AUDITING);
        return signRecordFeignClient.listSignRecordNoPage(reqDTO);
    }

    /**
     * 根据机构和角色类型获取用户
     *
     * @param
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 获取所有商务经理（组织名-大区名）
     *
     * @param
     * @return
     */
    private List<Map<String, Object>> getAllSaleList() {
        // 查询所有商务组
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setOrgType(OrgTypeConstant.SWZ);
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> groupList = queryOrgByParam.getData();
        // 查询所有商务大区
        queryDTO.setOrgType(OrgTypeConstant.SWDQ);
        JSONResult<List<OrganizationRespDTO>> busArea =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> busAreaLsit = busArea.getData();
        // 查询所有商务经理
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.SWJL.name(), statusList);

        Map<Long, OrganizationRespDTO> orgMap = new HashMap<Long, OrganizationRespDTO>();
        // 生成<机构id，机构>map
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (OrganizationRespDTO org : groupList) {
                orgMap.put(org.getId(), org);
            }
        }

        if (CollectionUtils.isNotEmpty(busAreaLsit)) {
            for (OrganizationRespDTO org : busAreaLsit) {
                orgMap.put(org.getId(), org);
            }
        }

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 生成结果集，匹配电销组以及电销总监
        if (CollectionUtils.isNotEmpty(userList)) {
            for (UserInfoDTO user : userList) {
                Map<String, Object> resultMap = new HashMap<String, Object>();
                OrganizationRespDTO group = orgMap.get(user.getOrgId());
                if (group != null) {
                    OrganizationRespDTO area = orgMap.get(group.getParentId());
                    resultMap.put("id", user.getId().toString());
                    if (area != null) {
                        resultMap.put("name", user.getName() + "(" + area.getName() + "--"
                                + group.getName() + ")");
                    } else {
                        resultMap.put("name", user.getName() + "(" + group.getName() + ")");

                    }
                    result.add(resultMap);
                }
            }
        }

        return result;
    }


    /**
     * 获取工作天数
     *
     * @param request
     * @return
     */
    @PostMapping("/getWorkDay")
    @ResponseBody
    public JSONResult<String> getWorkDay(HttpServletRequest request) {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(curLoginUser.getId());
        JSONResult<UserInfoDTO> jsonResult = userInfoFeignClient.get(idEntityLong);
        if (!JSONResult.SUCCESS.equals(jsonResult.getCode())) {
            return new JSONResult<String>().fail(jsonResult.getCode(), jsonResult.getMsg());
        }
        UserInfoDTO data = jsonResult.getData();
        String workDay = "0";
        if (data == null || data.getCreateTime() == null) {
            return new JSONResult<String>().success(workDay);
        }

        Date createTime = data.getCreateTime();
        logger.info(DateUtil.getStartOrEndOfDay(createTime, LocalTime.MIN).toString());
        logger.info(DateUtil.getStartOrEndOfDay(new Date(), LocalTime.MAX).toString());
        // int diffDay = DateUtil.diffDay(createTime,new Date())+1;
        int diffDay = DateUtil.differentDays(createTime, new Date()) + 1;
        return new JSONResult<String>().success(diffDay + "");

    }

    /**
     * 查询字典表
     *
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


    /**
     * 获取当前组织机构下 角色信息
     *
     * @param orgId
     * @param roleCode
     * @return
     */
    private List<Long> getAccountIdList(Long orgId, String roleCode) {
        UserOrgRoleReq req = new UserOrgRoleReq();
        req.setOrgId(orgId);
        req.setRoleCode(roleCode);
        JSONResult<List<UserInfoDTO>> userJr = userInfoFeignClient.listByOrgAndRole(req);
        if (userJr == null || !JSONResult.SUCCESS.equals(userJr.getCode())) {
            logger.error(
                    "查询电销通话记录-获取电销顾问-userInfoFeignClient.listByOrgAndRole(req),param{{}},res{{}}",
                    req, userJr);
            return null;
        }
        List<UserInfoDTO> userInfoDTOList = userJr.getData();
        if (userInfoDTOList != null && userInfoDTOList.size() != 0) {
            List<Long> idList =
                    userInfoDTOList.stream().map(UserInfoDTO::getId).collect(Collectors.toList());
            return idList;
        }
        return null;
    }

    /**
     * 查询今日看板计数
     *
     */
    @PostMapping("/busGroupDayQuery")
    @ResponseBody
    public JSONResult<BusGroupDTO> busGroupDayQuery() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Map map = new HashMap();
        map.put("busDirectorId", curLoginUser.getId());
        map.put("flag", 1);
        JSONResult<BusGroupDTO> jsonResult = busGroupDashboardFeignClient.busGroupDataQuery(map);
        if (!JSONResult.SUCCESS.equals(jsonResult.getCode())) {
            return new JSONResult<BusGroupDTO>().fail(jsonResult.getCode(), jsonResult.getMsg());
        }
        BusGroupDTO data = jsonResult.getData();
        return new JSONResult<BusGroupDTO>().success(data);
    }


    /**
     * 查询非今日看板计数
     *
     */
    @PostMapping("/busGroupNotDayQuery")
    @ResponseBody
    public JSONResult<BusGroupDTO> busGroupNotDayQuery() {
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Map map = new HashMap();
        map.put("busDirectorId", curLoginUser.getId());
        map.put("flag", 2);
        JSONResult<BusGroupDTO> jsonResult = busGroupDashboardFeignClient.busGroupDataQuery(map);
        if (!JSONResult.SUCCESS.equals(jsonResult.getCode())) {
            return new JSONResult<BusGroupDTO>().fail(jsonResult.getCode(), jsonResult.getMsg());
        }
        BusGroupDTO data = jsonResult.getData();
        return new JSONResult<BusGroupDTO>().success(data);
    }
}
