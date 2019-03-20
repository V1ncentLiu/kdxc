package com.kuaidao.manageweb.controller.phonetraffic;

import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerRespDTO;
import com.kuaidao.aggregation.dto.busmycustomer.MyCustomerParamDTO;
import com.kuaidao.aggregation.dto.call.CallRecordReqDTO;
import com.kuaidao.aggregation.dto.call.CallRecordRespDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationReqDTO;
import com.kuaidao.aggregation.dto.circulation.CirculationRespDTO;
import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueFileDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficParamDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficRespDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.aggregation.dto.tracking.TrackingReqDTO;
import com.kuaidao.aggregation.dto.tracking.TrackingRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.StageContant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;
import com.kuaidao.manageweb.feign.call.CallRecordFeign;
import com.kuaidao.manageweb.feign.circulation.CirculationFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.phonetraffic.PhoneTrafficFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.tracking.TrackingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: admin
 * @Date: 2019/3/18 17:51
 * @Description:
 */
@Controller
@RequestMapping("/phonetraffic")
public class PhoneTrafficController {
    private static Logger logger = LoggerFactory.getLogger(PhoneTrafficController.class);

    @Autowired
    private ClueBasicFeignClient clueBasicFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private InfoAssignFeignClient infoAssignFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    PhoneTrafficFeignClient phoneTrafficFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private CallRecordFeign callRecordFeign;

    @Autowired
    private TrackingFeignClient trackingFeignClient;

    @Autowired
    private CirculationFeignClient circulationFeignClient;

    @Autowired
    private MyCustomerFeignClient myCustomerFeignClient;

    @Autowired
    RoleManagerFeignClient roleManagerFeignClient;


    @Value("${oss.url.directUpload}")
    private String ossUrl;

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request){
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }
        // 话务人员

        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("aggregation:PhoneTraffic");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());

        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:appiontmentManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());

        request.setAttribute("phtrafficList", phTrafficList());

        return "/phonetraffic/customManagement";
    }

    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<PhoneTrafficRespDTO>> queryListPage(@RequestBody PhoneTrafficParamDTO param){
        logger.info("============分页数据查询==================");
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();

        /**
         * 数据权限说明：
         *    管理员权限：
         *      能够看见全部数据
         *    电销顾问：
         *      能够看见自己以及所在电销组下电销创业顾问创建的数据
         *    其他的只能够看见自己创建的数据
         */
//            List dxList = new ArrayList();
//        if(roleList!=null&&roleList.get(0)!=null) {
//            if (RoleCodeEnum.HWZG.name().equals(roleList.get(0).getRoleCode())) {
//                dxList = phoneTrafficUser();
//                dxList.add(user.getId());
//                param.setUserList(dxList);
//            } else {
//                dxList.add(user.getId());
//                param.setUserList(dxList);
//            }
//        }
        if(roleList!=null&&roleList.get(0)!=null) {
            if (RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
            }else if (RoleCodeEnum.HWZG.name().equals(roleList.get(0).getRoleCode())) {
                param.setPhTraDirectorId(user.getId());
            } else {
                param.setOperatorId(user.getId());
            }
        }

        String defineColumn = param.getDefineColumn();
        String defineValue = param.getDefineValue();
        if(StringUtils.isNotBlank(defineColumn)&&StringUtils.isNotBlank(defineValue)){
            if("phone".equals(defineColumn)){
                param.setPhone(defineValue);
            }else  if("cusName".equals(defineColumn)){
                param.setCusName(defineValue);
            }else if("qq".equals(defineColumn)){
                param.setQq(defineValue);
            }else if("wx".equals(defineColumn)){
                param.setWx(defineValue);
            }else if("email".equals(defineColumn)){
                param.setEmail(defineValue);
            }
        }

//      时间判断：
        Date date1 = param.getCreateTime1();
        Date date2 = param.getCreateTime2();
        if(date1!=null && date2!=null ){
            if(date1.getTime()>date2.getTime()){
                return new JSONResult().fail("-1","创建时间，开始时间大于结束时间!");
            }
        }
        return  phoneTrafficFeignClient.queryList(param);
    }

    /**
     * 分配资源
     * @return
     */
    @PostMapping("/allocationClue")
    @ResponseBody
    public JSONResult allocationClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
                                     BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        return phoneTrafficFeignClient.allocationClue(allocationClueReq);
    }

    /**
     * 转移资源
     * @return
     */
    @PostMapping("/transferClue")
    @ResponseBody
    public JSONResult transferClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
                                   BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        return phoneTrafficFeignClient.transferClue(allocationClueReq);
    }

    /**
     * 跳转 编辑资源页面
     */
    @RequestMapping("/toEditPage")
    public String toEditPage(HttpServletRequest request, @RequestParam String clueId){
        CallRecordReqDTO call = new CallRecordReqDTO();
        call.setClueId(clueId);
        JSONResult<List<CallRecordRespDTO>> callRecord = callRecordFeign.listTmCallReacordByParamsNoPage(call);

        // 资源通话记录
        if (callRecord != null && JSONResult.SUCCESS.equals(callRecord.getCode()) && callRecord.getData() != null) {
            request.setAttribute("callRecord", callRecord.getData());
        }
        ClueQueryDTO queryDTO = new ClueQueryDTO();

        queryDTO.setClueId(new Long(clueId));

        request.setAttribute("clueId", clueId);

        request.setAttribute("ossUrl", ossUrl);

        JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);

        // 维护的资源数据
        if (clueInfo != null && JSONResult.SUCCESS.equals(clueInfo.getCode()) && clueInfo.getData() != null) {

            if (null != clueInfo.getData().getClueCustomer()) {
                request.setAttribute("customer", clueInfo.getData().getClueCustomer());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueBasic()) {
                request.setAttribute("base", clueInfo.getData().getClueBasic());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
            if (null != clueInfo.getData().getClueIntention()) {
                request.setAttribute("intention", clueInfo.getData().getClueIntention());
            } else {
                request.setAttribute("customer", new ArrayList());
            }
        }
        // 获取资源跟进记录数据
        TrackingReqDTO dto = new TrackingReqDTO();
        dto.setClueId(new Long(clueId));
        dto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
        JSONResult<List<TrackingRespDTO>> trackingList = trackingFeignClient.queryList(dto);
        if (trackingList != null && trackingList.SUCCESS.equals(trackingList.getCode())
                && trackingList.getData() != null) {
            request.setAttribute("trackingList", trackingList.getData());
        } else {
            request.setAttribute("trackingList", new ArrayList());
        }

        // 获取资源流转数据
        CirculationReqDTO circDto = new CirculationReqDTO();
        circDto.setClueId(new Long(clueId));
        circDto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
        JSONResult<List<CirculationRespDTO>> circulationList = circulationFeignClient.queryList(circDto);
        if (circulationList != null && circulationList.SUCCESS.equals(circulationList.getCode())
                && circulationList.getData() != null) {
            request.setAttribute("circulationList", circulationList.getData());
        } else {
            request.setAttribute("circulationList", new ArrayList());
        }
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }

        // 获取已上传的文件数据
        ClueQueryDTO fileDto = new ClueQueryDTO();
        fileDto.setClueId(new Long(clueId));
        fileDto.setStage(StageContant.STAGE_PHONE_TRAFFIC);
        JSONResult<List<ClueFileDTO>> clueFileList = myCustomerFeignClient.findClueFile(fileDto);
        if (clueFileList != null && clueFileList.SUCCESS.equals(clueFileList.getCode())
                && clueFileList.getData() != null) {
            request.setAttribute("clueFileList", clueFileList.getData());
        }
        return "/phonetraffic/editCustomerMaintenance";
    }

    /**
     * 转电销
     */
    public void toTele(){
//    通过规则，转到电销
    }

    /**
     * 话务人员：当前人员，所属组织同级 以及 下属组织的人员
     */
    /**
     * 组内电销顾问查询
     * @return
     */
    private List phoneTrafficUser(){
        RoleQueryDTO query = new RoleQueryDTO();
        query.setRoleCode(RoleCodeEnum.HWY.name());
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
        ArrayList resList = new ArrayList();
        if (JSONResult.SUCCESS.equals(roleJson.getCode())) {
            List<RoleInfoDTO> roleList = roleJson.getData();
            if (null != roleList && roleList.size() > 0) {
                RoleInfoDTO roleDto = roleList.get(0);
                UserInfoPageParam param = new UserInfoPageParam();
//                param.setRoleId(roleDto.getId());
                param.setOrgId(user.getOrgId());
                param.setPageSize(10000);
                param.setPageNum(1);
                JSONResult<PageBean<UserInfoDTO>> userListJson = userInfoFeignClient.list(param);
                if (JSONResult.SUCCESS.equals(userListJson.getCode())) {
                    PageBean<UserInfoDTO> pageList = userListJson.getData();
                    List<UserInfoDTO> userList = pageList.getData();
                    for(UserInfoDTO dto:userList){
                        resList.add(dto.getId());
                    }
                }
            }
        }
        return resList;
    }

    private List<UserInfoDTO> phTrafficList(){
        RoleQueryDTO query = new RoleQueryDTO();
        query.setRoleCode(RoleCodeEnum.HWY.name());
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
        List<UserInfoDTO> userList = new ArrayList();
        if (JSONResult.SUCCESS.equals(roleJson.getCode())) {
            List<RoleInfoDTO> roleList = roleJson.getData();
            if (null != roleList && roleList.size() > 0) {
                RoleInfoDTO roleDto = roleList.get(0);
                UserInfoPageParam param = new UserInfoPageParam();
//                param.setRoleId(roleDto.getId());  // 查询该组织下，该角色的全部员工。去掉就是查询全部该组织下的员工
                param.setOrgId(user.getOrgId());
                param.setPageSize(10000);
                param.setPageNum(1);
                JSONResult<PageBean<UserInfoDTO>> userListJson = userInfoFeignClient.list(param);
                if (JSONResult.SUCCESS.equals(userListJson.getCode())) {
                    PageBean<UserInfoDTO> pageList = userListJson.getData();
                    userList = pageList.getData();
                }
            }
        }
        userList.add(user);
        return userList;
    }



}
