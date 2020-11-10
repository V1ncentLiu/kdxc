package com.kuaidao.manageweb.controller.sign;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.kuaidao.aggregation.dto.clue.ClueFileDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordReqDTO;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordReqDTO;
import com.kuaidao.aggregation.dto.clue.CustomerClueDTO;
import com.kuaidao.aggregation.dto.financing.RefundRebateDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailReqDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailRespDTO;
import com.kuaidao.aggregation.dto.sign.BusSignInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.sign.BusSignRespDTO;
import com.kuaidao.aggregation.dto.sign.BusinessSignDTO;
import com.kuaidao.aggregation.dto.sign.PayDetailDTO;
import com.kuaidao.aggregation.dto.sign.SignParamDTO;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordRespDTO;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.CompanyInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.JSONUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueCustomerFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.financing.RefundFeignClient;
import com.kuaidao.manageweb.feign.invitearea.InviteareaFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.paydetail.PayDetailFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.sign.BusinessSignFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.feign.visitrecord.BusVisitRecordFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description: 签约记录
 */

@Controller
@RequestMapping("/businesssign")
public class BusinessSignController {

    private static Logger logger = LoggerFactory.getLogger(BusinessSignController.class);
    @Autowired
    InviteareaFeignClient inviteareaFeignClient;
    @Autowired
    BusinessSignFeignClient businessSignFeignClient;
    @Autowired
    SysRegionFeignClient sysRegionFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private ClueBasicFeignClient clueBasicFeignClient;
    @Autowired
    private ClueCustomerFeignClient clueCustomerFeignClient;
    @Autowired
    private RefundFeignClient refundFeignClient;

    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;


    @Autowired
    private BusVisitRecordFeignClient visitRecordFeignClient;

    @Autowired
    PayDetailFeignClient payDetailFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private MyCustomerFeignClient myCustomerFeignClient;

    @Value("${oss.url.directUpload}")
    private String ossUrl;
    /**
     * 有效性签约单确认列表页面
     *
     * @return
     */
    @RequestMapping("/businessSignValidPage")
    public String businessSignValidPage(HttpServletRequest request) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.SWZ);
        orgDto.setSystemCode(SystemCodeConstant.HUI_JU);
        if (user.getBusinessLine() != null) {
            orgDto.setBusinessLine(user.getBusinessLine());
        }
        // 商务小组
        JSONResult<List<OrganizationRespDTO>> swList =
                organizationFeignClient.queryOrgByParam(orgDto);
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        // 电销小组
        JSONResult<List<OrganizationRespDTO>> dxList =
                organizationFeignClient.queryOrgByParam(orgDto);

        // 查询所有签约项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(AggregationConstant.NO);
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.queryBySign(param);
        // 获取省份
        List<SysRegionDTO> proviceslist = sysRegionFeignClient.getproviceList().getData();

        request.setAttribute("swList", swList.getData());
        request.setAttribute("dxList", dxList.getData());
        request.setAttribute("projectList", allProject.getData());
        request.setAttribute("provinceList", proviceslist);
        if (roleList.get(0).getRoleCode().equals(RoleCodeEnum.PDZG.name())) {
            List<UserInfoDTO> userInfoList =
                    getUserList(null, RoleCodeEnum.PDZY.name(), user.getBusinessLine());
            request.setAttribute("userInfoList", userInfoList);
            return "business/businessSignValidPage";
        } else {
            return "business/businessSignValidPageByPdzy";
        }
    }

    /**
     * 有效性签约单确认列表
     *
     * @return
     */
    @RequestMapping("/businessSignValidList")
    @ResponseBody
    public JSONResult<PageBean<BusinessSignDTO>> businessSignValidList(HttpServletRequest request,
            @RequestBody BusinessSignDTO businessSignDTO) {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (user.getBusinessLine() != null) {
            businessSignDTO.setBusinessLine(user.getBusinessLine());
        }
        if (roleList.get(0).getRoleCode().equals(RoleCodeEnum.PDZY.name())) {
            businessSignDTO.setPdUser(user.getId());
        }
        JSONResult<PageBean<BusinessSignDTO>> list =
                businessSignFeignClient.businessSignValidList(businessSignDTO);
        return list;
    }

    /**
     * 签约有效性判断
     *
     * @return
     */
    @RequestMapping("/updateBusinessSignDTOValidByIds")
    @LogRecord(description = "签约有效性修改", operationType = LogRecord.OperationType.UPDATE,
            menuName = MenuEnum.BUSINESSSIGNVALID)
    @ResponseBody
    public JSONResult addTelemarketingLayout(@RequestBody BusinessSignDTO businessSignDTO) {
        UserInfoDTO user = getUser();
        businessSignDTO.setLoginUserId(user.getId());
        return businessSignFeignClient.updateBusinessSignDTOValidByIds(businessSignDTO);
    }


    /**
     * 签约有效性判断
     *
     * @return
     */
    @RequestMapping("/getPaymentDetailsById")
    @ResponseBody
    public JSONResult<PayDetailDTO> getPaymentDetailsById(@RequestBody PayDetailDTO payDetailDTO) {
        return businessSignFeignClient.getPaymentDetailsById(payDetailDTO);
    }

    /**
     * 新增
     */
    @RequestMapping("/insert")
    @ResponseBody
    @LogRecord(description = "添加签约单", operationType = OperationType.INSERT,
            menuName = MenuEnum.SIGN_ORDER)
    public JSONResult<Boolean> saveSign(@Valid @RequestBody BusSignInsertOrUpdateDTO dto,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateUser(user.getId());
        if (dto.getSignType() == 1) { // 全款
            dto.setMakeUpTime(null);
            dto.setAmountBalance(null);
        }
        if (user.getBusinessLine() != null) {
            dto.setBusinessLine(user.getBusinessLine());
        }
        return businessSignFeignClient.saveSign(dto);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    @ResponseBody
    public JSONResult<Boolean> updateSign(@Valid @RequestBody BusSignInsertOrUpdateDTO dto,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setUpdateUser(user.getId());
        if (dto.getSignType() == 1) { // 全款
            dto.setMakeUpTime(null);
            dto.setAmountBalance(null);
        }
        if (user.getBusinessLine() != null) {
            dto.setBusinessLine(user.getBusinessLine());
        }
        return businessSignFeignClient.updateSign(dto);
    }

    /**
     * 查询明细
     */
    @RequestMapping("/one")
    @ResponseBody
    public JSONResult<BusSignRespDTO> queryOne(@RequestBody SignParamDTO param) throws Exception {

        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(param.getSignId());
        JSONResult<BusSignRespDTO> res = businessSignFeignClient.queryOne(idEntityLong);
        if (JSONResult.SUCCESS.equals(res.getCode())) {
            BusSignRespDTO data = res.getData();
            IdEntityLong idLong = new IdEntityLong();
            idLong.setId(param.getClueId());
            String linkPhone = linkPhone(idLong);
            data.setPhone(linkPhone);
            if (data.getGiveType() == null) {
                data.setGiveType(-1);
            }
            data.setVisitTime(new Date());
            data.setVisitType(1);
            data.setVisitNum(1);
            data.setVisitCity("");
            data.setArrVisitCity("");
            data.setVisitShopType(1);
            // 获取签约店型
            JSONResult<List<DictionaryItemRespDTO>> vistitStoreJson =
                    getShortTypeByProjectId(data.getSignProjectId());
            data.setVistitStoreTypeArr(vistitStoreJson.getData());
            if (!checkShopType(data.getSignShopType(), vistitStoreJson.getData())) {
                data.setSignShopType(null);
            }
            res.setData(data);
            data.setPerformanceAmount(data.getAmountPerformance());
        }
        return res;
    }

    /**
     * 查询签约单 不分页
     */
    @RequestMapping("/querySignList")
    @ResponseBody
    public JSONResult<List<BusSignRespDTO>> querySignList(@RequestBody SignRecordReqDTO dto)
            throws Exception {
        return businessSignFeignClient.querySignList(dto);
    }

    /**
     * 查询签约单 不分页
     */
    @RequestMapping("/queryRebuts")
    @ResponseBody
    public JSONResult<List<BusSignRespDTO>> queryRebuts(@RequestBody SignRecordReqDTO dto)
            throws Exception {
        dto.setStatus(0);
        JSONResult<List<BusSignRespDTO>> listRes = businessSignFeignClient.querySignList(dto);
        return listRes;
    }


    /**
     * 签约单，新增时候回显信息 1:回显当时提交邀约来访中填写的信息 2: 若当前用户 客户姓名，签约餐饮公司=考察公司，签约项目，签约省份，签约城市，签约区／县。
     */
    @RequestMapping("/echo")
    @ResponseBody
    public JSONResult<BusSignRespDTO> echo(@RequestBody IdEntityLong idEntityLong)
            throws Exception {
        BusSignRespDTO signDTO = new BusSignRespDTO();
        // 查询需要进行回显的信息，并进行映射
        // 获取客户信息
        String linkPhone = linkPhone(idEntityLong);

        // 查询最新一次签约记录
        JSONResult<BusSignRespDTO> newSign = businessSignFeignClient.findMaxNewOne(idEntityLong);
        // 查詢當前使用項目
        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(-1);
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        BusVisitRecordRespDTO newVisitData = vrecordInGroup(idEntityLong.getId());

        boolean signFlag = true;
        BusSignRespDTO newSignData = newSign.data();
        if ( newSignData!= null) {
            signDTO.setIdCard(newSignData.getIdCard());
            signDTO.setSignCompanyId(newSignData.getSignCompanyId());
            Long signProjectId = newSignData.getSignProjectId();
            signDTO.setSignProjectId(getProjectId(proJson, signProjectId));
            signDTO.setSignProvince(newSignData.getSignProvince());
            signDTO.setSignCity(newSignData.getSignCity());
            signDTO.setSignDictrict(newSignData.getSignDictrict());
            signDTO.setSignShopType(newSignData.getSignShopType());
            signDTO.setCustomerName(newSignData.getCustomerName());
            signDTO.setPhone(linkPhone);
            signDTO.setSignType(1);
            signDTO.setPayType("1");
            if (newVisitData != null) {
                if (newVisitData.getVisitPeopleNum() != null) { // 首次到访，设置到访人数。如果没有到访记录则认为是首次到访
                    signDTO.setVisitNum((Integer) newVisitData.getVisitPeopleNum());// 来访人数
                }
                if (newVisitData.getArrVisitCity() != null) {
                    signDTO.setArrVisitCity((String) newVisitData.getArrVisitCity());// 来访城市
                }
                if (newVisitData.getVistitStoreType() != null) {
                    signDTO.setVisitShopType(Integer.valueOf(newVisitData.getVistitStoreType()));// 到访店铺类型
                }
            }
            signFlag = false;
        }


        Boolean flag = true;
        if (signFlag) {
            if (newVisitData != null) {
                signDTO.setSignCompanyId(newVisitData.getCompanyid());
                Long signProjectId = newVisitData.getProjectId();
                signDTO.setSignProjectId(getProjectId(proJson, signProjectId));
                signDTO.setSignProvince(newVisitData.getSignProvince());
                signDTO.setSignCity(newVisitData.getSignCity());
                signDTO.setSignDictrict(newVisitData.getSignDistrict());
                signDTO.setSignShopType(newVisitData.getVistitStoreType());
                signDTO.setCustomerName(newVisitData.getCustomerName());
                signDTO.setPhone(linkPhone);
                signDTO.setSignType(1);
                signDTO.setPayType("1");

                if (newVisitData.getVisitPeopleNum() != null) { // 首次到访，设置到访人数。如果没有到访记录则认为是首次到访
                    signDTO.setVisitNum((Integer) newVisitData.getVisitPeopleNum());// 来访人数
                }
                if (newVisitData.getArrVisitCity() != null) {
                    signDTO.setArrVisitCity((String) newVisitData.getArrVisitCity());// 来访城市
                }
                if (newVisitData.getVistitStoreType() != null) {
                    signDTO.setVisitShopType(Integer.valueOf(newVisitData.getVistitStoreType()));// 到访店铺类型
                }
                flag = false;
            }
        }

        // 最新一次的邀约
        JSONResult<Map> appiontment = visitRecordFeignClient.echoAppoinment(idEntityLong);
        // 使用邀约来访
        Map appiontmentData = appiontment.data();
        if (appiontmentData != null) {
            if (flag && signFlag) {// 没有到访记录
                // signDTO.setSignCompanyId((Long) data.get("busCompany"));
                String tasteProjectId = (String) appiontmentData.get("tasteProjectId");
                if (tasteProjectId != null) {
                    String[] split = tasteProjectId.split(",");
                    if (split.length > 0 && !"".equals(split[0])) {
                        Long signProjectId = Long.valueOf(split[0]);
                        signDTO.setSignProjectId(getProjectId(proJson, signProjectId));
                    }
                }
                if (StringUtils.isBlank(signDTO.getIdCard()) && appiontmentData.get("idCard") != null) {
                    signDTO.setIdCard((String) appiontmentData.get("idCard"));
                }
                signDTO.setSignProvince((String) appiontmentData.get("signProvince"));
                signDTO.setSignCity((String) appiontmentData.get("signCity"));
                signDTO.setSignDictrict((String) appiontmentData.get("signDistrict"));
                signDTO.setCustomerName((String) appiontmentData.get("cusName"));
                signDTO.setPhone(linkPhone);
                signDTO.setSignType(1);
                signDTO.setSignShopType("");
                signDTO.setPayType("1");
                if (appiontmentData.get("cusNum") != null) { // 首次到访，设置到访人数。如果没有到访记录则认为是首次到访
                    signDTO.setVisitNum((Integer) appiontmentData.get("cusNum"));// 来访人数
                }
            }
            if (appiontmentData.get("city") != null) {
                signDTO.setVisitCity((String) appiontmentData.get("city"));// 来访城市
            }
        }

        signDTO.setIsRemoteSign(0);
        signDTO.setPayTime(new Date());
        signDTO.setVisitTime(new Date()); // 到访时间
        signDTO.setVisitType(1); // 到访类型
        signDTO.setRebutReason(null);
        signDTO.setRebutTime(null);
        signDTO.setAmountReceived(null);
        // 获取签约店型
        JSONResult<List<DictionaryItemRespDTO>> vistitStoreJson =
                getShortTypeByProjectId(signDTO.getSignProjectId());
        signDTO.setVistitStoreTypeArr(vistitStoreJson.getData());
        if (!checkShopType(signDTO.getSignShopType(), vistitStoreJson.getData())) {
            signDTO.setSignShopType(null);
        }
        if (StringUtils.isBlank(signDTO.getSignProvince())) {
            signDTO.setSignProvince("");
        }
        if (StringUtils.isBlank(signDTO.getSignCity())) {
            signDTO.setSignCity("");
        }
        if (StringUtils.isBlank(signDTO.getSignDictrict())) {
            signDTO.setSignDictrict("");
        }
        return new JSONResult<BusSignRespDTO>().success(signDTO);
    }



    private BusVisitRecordRespDTO vrecordInGroup(Long clueId){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        BusVisitRecordReqDTO recordReqDTO = new BusVisitRecordReqDTO();
        recordReqDTO.setClueId(clueId);
        recordReqDTO.setIsVisit(AggregationConstant.YES);
        recordReqDTO.setBusGroupId(curLoginUser.getOrgId());
        JSONResult<List<BusVisitRecordRespDTO>> result = visitRecordFeignClient.queryList(recordReqDTO);
        List<BusVisitRecordRespDTO> data = result.data();
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }else{
            return data.get(0);
        }
    }


    private Boolean checkShopType(String type, List<DictionaryItemRespDTO> itemList) {
        Boolean flag = false;
        if (!StringUtils.isNotBlank(type)) {
            flag = false;
        } else if (itemList != null && itemList.size() > 0) {
            String shopType =
                    itemList.stream().map(a -> a.getValue()).collect(Collectors.joining(","));
            if (shopType.contains(type)) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    private Long getProjectId(JSONResult<List<ProjectInfoDTO>> proJson, Long signProjectId) {

        Long res = null;
        boolean flag = false;
        if (JSONResult.SUCCESS.equals(proJson.getCode())) {
            List<ProjectInfoDTO> data1 = proJson.getData();
            for (ProjectInfoDTO projectInfo : data1) {
                if (projectInfo.getId().equals(signProjectId)) {
                    flag = true;
                    break;
                }
            }
        }
        if (flag) {
            res = signProjectId;
        }
        return res;
    }


    /**
     * 添加签约单时，到访记录回显
     */
    @RequestMapping("/visitEcho")
    @ResponseBody
    public JSONResult<BusSignRespDTO> visitEcho(@RequestBody IdEntityLong idEntityLong)
            throws Exception {
        BusSignRespDTO signDTO = new BusSignRespDTO();
        // 查询需要进行回显的信息，并进行映射
        // 最新一次的邀约
        JSONResult<Map> mapJSONResult = visitRecordFeignClient.echoAppoinment(idEntityLong);
        // 获取客户信息
        String linkPhone = linkPhone(idEntityLong);
        // 查询最新一次到访
        BusVisitRecordRespDTO newVisitData = vrecordInGroup(idEntityLong.getId());
        Boolean flag = true;
        if (newVisitData != null) {

            if (newVisitData.getVisitPeopleNum() != null) { // 首次到访，设置到访人数。如果没有到访记录则认为是首次到访
                signDTO.setVisitNum((Integer) newVisitData.getVisitPeopleNum());// 来访人数
            }
            if (newVisitData.getArrVisitCity() != null) {
                signDTO.setArrVisitCity((String) newVisitData.getArrVisitCity());// 来访城市
            }
            if (newVisitData.getVistitStoreType() != null) {
                signDTO.setVisitShopType(Integer.valueOf(newVisitData.getVistitStoreType()));// 到访店铺类型
            }

            flag = false;
        }

        if (JSONResult.SUCCESS.equals(mapJSONResult.getCode())) {
            Map data = mapJSONResult.getData();
            if (data != null) {
                if (flag) {// 没有签约单
                    if (data.get("cusNum") != null) { // 首次到访，设置到访人数。如果没有到访记录则认为是首次到访
                        signDTO.setVisitNum((Integer) data.get("cusNum"));// 来访人数
                    }
                }
                if (data.get("city") != null) {
                    signDTO.setVisitCity((String) data.get("city"));// 来访城市
                }
            }
        }
        return new JSONResult<BusSignRespDTO>().success(signDTO);
    }

    /**
     * 获取商务客户详情电话
     */
    private String linkPhone(IdEntityLong idEntityLong) {
        List<Long> list = new ArrayList<>();
        list.add(idEntityLong.getId());
        IdListLongReq idListLongReq = new IdListLongReq();
        idListLongReq.setIdList(list);
        JSONResult<List<CustomerClueDTO>> listJSONResult =
                clueCustomerFeignClient.findcustomersByClueIds(idListLongReq);
        String linkPhone = "";
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            List<CustomerClueDTO> data = listJSONResult.getData();
            if (data != null && data.size() > 0) {
                CustomerClueDTO customerClueDTO = data.get(0);
                linkPhone = customerClueDTO.getLinkPhone();
            }

        }
        return linkPhone;
    }

    /**
     * 跳转到 电销我的客户，客户管理签约单明细页面
     */
    @RequestMapping("/myCustomSignRecordPage")
    public String myCustomSignRecordPage(HttpServletRequest request, @RequestParam String clueId, @RequestParam String signId,
            @RequestParam String readyOnly, @RequestParam String createUser, @RequestParam(required = false) String showSignButton,
            @RequestParam String type) throws Exception {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        String roleCode = user.getRoleList().get(0).getRoleCode();
        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(Long.valueOf(signId));
        SignParamDTO paramDTO = new SignParamDTO();
        paramDTO.setClueId(Long.valueOf(clueId));
        paramDTO.setSignId(Long.valueOf(signId));
        JSONResult<BusSignRespDTO> busSign = queryOne(paramDTO);
        BusSignRespDTO sign = busSign.getData();
        List<BusSignRespDTO> signData = new ArrayList();
        signData.add(sign);
        List<BusSignRespDTO> PayAllData = new ArrayList();
        PayAllData.add(sign);
        // 签约基本信息
        request.setAttribute("signData", signData);
        request.setAttribute("payType", sign.getPayType()); // 最新一次付款类型： 用来判断显示行数
        request.setAttribute("refundStatus", sign.getRefundStatus()); // 判断退款信息是否显示
        if ("4".equals(sign.getPayType())) {
            readyOnly = "1";
        }
        PayDetailReqDTO detailReqDTO = new PayDetailReqDTO();
        detailReqDTO.setSignId(Long.valueOf(signId));
        JSONResult<List<PayDetailRespDTO>> resListJson = payDetailFeignClient.queryList(detailReqDTO);

        boolean allRepeatStatus = false;// 全款判单是否显示
        boolean oneRepeatStatus = false;// 定金判单是否显示
        boolean twoRepeatStatus = false;// 追加定金判单是否显示
        boolean threeRepeatStatus = false;// 尾款判单是否显示
        if (JSONResult.SUCCESS.equals(resListJson.getCode())) {
            List<PayDetailRespDTO> list = resListJson.getData();
            // 全款
            List<PayDetailRespDTO> all = new ArrayList();
            // 定金
            List<PayDetailRespDTO> one = new ArrayList();
            // 追加定金
            List<PayDetailRespDTO> two = new ArrayList();
            // 尾款
            List<PayDetailRespDTO> three = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                PayDetailRespDTO dto = list.get(i);
                if ("1".equals(dto.getPayType())) {
                    this.handlerData(dto, createUser);
                    all.add(dto);
                    if (dto.getRepeatStatus() != null && dto.getRepeatStatus() == 1) {
                        allRepeatStatus = true;
                    }
                }
                if ("2".equals(dto.getPayType())) {
                    this.handlerData(dto, createUser);
                    one.add(dto);
                    if (dto.getRepeatStatus() != null && dto.getRepeatStatus() == 1) {
                        oneRepeatStatus = true;
                    }
                } else if ("3".equals(dto.getPayType())) {
                    this.handlerData(dto, createUser);
                    two.add(dto);
                    if (dto.getRepeatStatus() != null && dto.getRepeatStatus() == 1) {
                        twoRepeatStatus = true;
                    }
                } else if ("4".equals(dto.getPayType())) {
                    this.handlerData(dto, createUser);
                    three.add(dto);
                    if (dto.getRepeatStatus() != null && dto.getRepeatStatus() == 1) {
                        threeRepeatStatus = true;
                    }
                }
            }
            request.setAttribute("allData", all);
            request.setAttribute("oneData", one);
            request.setAttribute("twoData", two);
            request.setAttribute("threeData", three);
            request.setAttribute("allRepeatStatus", allRepeatStatus);
            request.setAttribute("oneRepeatStatus", oneRepeatStatus);
            request.setAttribute("twoRepeatStatus", twoRepeatStatus);
            request.setAttribute("threeRepeatStatus", threeRepeatStatus);
        }
        // }
        // 查询签约单退款信息
        if (sign.getSignStatus() == 2 && (sign.getRefundStatus() == 4 || sign.getRefundStatus() == 6)) {
            Map map = new HashMap();
            map.put("signId", Long.valueOf(signId));
            map.put("type", 1);// 退款
            if (sign.getRefundStatus() == 4) {
                map.put("status", 3);// 确认退款
            }
            if (sign.getRefundStatus() == 6) {
                map.put("status", 4);// 已退款
            }
            JSONResult<RefundRebateDTO> refundRebateDTOs = refundFeignClient.getRefundInfo(map);
            if (refundRebateDTOs != null) {
                List<RefundRebateDTO> refundRebateList = new ArrayList<>();
                refundRebateList.add(refundRebateDTOs.getData());
                request.setAttribute("refundData", refundRebateList);
            }
        }

        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(-1);
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (JSONResult.SUCCESS.equals(proJson.getCode())) {
            request.setAttribute("proSelect", proJson.getData());
        }

        JSONResult<List<CompanyInfoDTO>> listJSONResult = companyInfoFeignClient.allCompany();
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            request.setAttribute("companySelect", proJson.getData());
        }

        if (showSignButton != null) {
            request.setAttribute("showSignButton", showSignButton);
        } else {
            request.setAttribute("showSignButton", "");
        }
        // 查询赠送类型集合
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        request.setAttribute("clueId", clueId);
        request.setAttribute("signId", signId);
        // readyOnly == 1 页面只读（没有添加按钮）
        request.setAttribute("readyOnly", readyOnly);
        request.setAttribute("signStatus", sign.getSignStatus());
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("type", type);
        request.setAttribute("ossUrl", ossUrl);
        if (RoleCodeEnum.DXZJ.name().equals(roleCode)) {
            //增加附件
            // 获取已上传的文件数据
            ClueQueryDTO fileDto = new ClueQueryDTO();
            // 获取已上传的文件数据
            fileDto.setSignId(Long.valueOf(signId));
            JSONResult<List<ClueFileDTO>> clueFileList = myCustomerFeignClient.findFileBySignId(fileDto);
            if (clueFileList != null && JSONResult.SUCCESS.equals(clueFileList.getCode()) && clueFileList.getData() != null) {
                request.setAttribute("clueFileList", clueFileList.getData());
            }
            return "clue/editSignAndFile";
        } else {
            return "clue/showSignAndPayDetail";
        }
    }

    /**
     * 计算判单金额
     * 
     * @param dto
     * @param createUser
     */
    private void handlerData(PayDetailRespDTO dto, String createUser) {
        Long userId = null;
        if (StringUtils.isNotBlank(createUser)) {
            userId = Long.valueOf(createUser);
        }
        if (StringUtils.isNotBlank(dto.getRepeatRatio())
                && dto.getRepeatRatio().contains(String.valueOf(userId))) {
            // dto.setRepeatStatus(1);
            String[] ratioArr = dto.getRepeatRatio().split(",", -1);
            for (String ratio : ratioArr) {
                if (ratio.contains(String.valueOf(userId))) {
                    String scale = ratio.split(":", -1)[2];
                    double ss = Double.valueOf(scale) / 100;
                    BigDecimal scaleRatio =
                            new BigDecimal(ss).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal repeatMoney = dto.getAmountPerformance().multiply(scaleRatio)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    dto.setRepeatMoney(repeatMoney.toString());
                    dto.setRepeatRatio(scale.toString() + "%");
                    break;
                }
            }
        } else {
            dto.setRepeatMoney("");
            dto.setRepeatRatio("");
            dto.setRepeatRatio("");
        }
    }

    /**
     * 跳转到 到访记录明细页面
     */
    @RequestMapping("/visitRecordPage")
    public String visitRecordPage(HttpServletRequest request, @RequestParam String clueId,
            @RequestParam String signId, @RequestParam String readyOnly,
            @RequestParam(required = false) String showSignButton,
            @RequestParam(required = false) Integer type) throws Exception {

        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(Long.valueOf(signId));
        SignParamDTO paramDTO = new SignParamDTO();
        paramDTO.setClueId(Long.valueOf(clueId));
        paramDTO.setSignId(Long.valueOf(signId));
        JSONResult<BusSignRespDTO> busSign = queryOne(paramDTO);

        logger.info("queryOne,busSign:{}", busSign);

        BusSignRespDTO sign = busSign.getData();
        List<BusSignRespDTO> signData = new ArrayList();
        signData.add(sign);
        List<BusSignRespDTO> PayAllData = new ArrayList();
        PayAllData.add(sign);
        // 签约基本信息
        request.setAttribute("signData", signData);
        request.setAttribute("payType", sign.getPayType()); // 最新一次付款类型： 用来判断显示行数
        request.setAttribute("refundStatus", sign.getRefundStatus()); // 判断退款信息是否显示
        request.setAttribute("customerName", sign.getCustomerName()); // 带出到页面客户姓名
        if ("4".equals(sign.getPayType())) {
            readyOnly = "1";
        }
        if ("1".equals(sign.getPayType())) {
            /**
             * 全款时候：不存在定金 尾款 以及 追加定金的情况
             */
            request.setAttribute("PayAllData", PayAllData);
        } else {
            PayDetailReqDTO detailReqDTO = new PayDetailReqDTO();
            detailReqDTO.setSignId(Long.valueOf(signId));
            JSONResult<List<PayDetailRespDTO>> resListJson =
                    payDetailFeignClient.queryList(detailReqDTO);
            if (JSONResult.SUCCESS.equals(resListJson.getCode())) {
                logger.info("resListJson:{}", resListJson);
                List<PayDetailRespDTO> list = resListJson.getData();
                // 定金
                List<PayDetailRespDTO> one = new ArrayList();
                // 追加定金
                List<PayDetailRespDTO> two = new ArrayList();
                // 尾款
                List<PayDetailRespDTO> three = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    PayDetailRespDTO dto = list.get(i);
                    if ("2".equals(dto.getPayType())) {
                        one.add(dto);
                    } else if ("3".equals(dto.getPayType())) {
                        two.add(dto);
                    } else if ("4".equals(dto.getPayType())) {
                        three.add(dto);
                    }
                }
                request.setAttribute("oneData", one);
                request.setAttribute("twoData", two);
                request.setAttribute("threeData", three);
            }
        }
        logger.info("sign:{}", JSONUtil.toJSon(sign));
        // 查询签约单退款信息
        if (sign.getSignStatus() == 2
                && (sign.getRefundStatus() == 4 || sign.getRefundStatus() == 6)) {
            Map map = new HashMap();
            map.put("signId", Long.valueOf(signId));
            map.put("type", 1);// 退款
            if (sign.getRefundStatus() == 4) {
                map.put("status", 3);// 确认退款
            }
            if (sign.getRefundStatus() == 6) {
                map.put("status", 4);// 已退款
            }
            JSONResult<RefundRebateDTO> refundRebateDTOs = refundFeignClient.getRefundInfo(map);
            if (refundRebateDTOs != null) {
                List<RefundRebateDTO> refundRebateList = new ArrayList<>();
                refundRebateList.add(refundRebateDTOs.getData());
                request.setAttribute("refundData", refundRebateList);
            }
        }

        // 项目
        ProjectInfoPageParam param = new ProjectInfoPageParam();
        param.setIsNotSign(-1);
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.listNoPage(param);
        if (JSONResult.SUCCESS.equals(proJson.getCode())) {
            request.setAttribute("proSelect", proJson.getData());
        }

        JSONResult<List<CompanyInfoDTO>> listJSONResult = companyInfoFeignClient.allCompany();
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            request.setAttribute("companySelect", proJson.getData());
        }


        if (showSignButton != null) {
            request.setAttribute("showSignButton", showSignButton);
        } else {
            request.setAttribute("showSignButton", "");
        }
        /*if(sign.getContractTime() ==null &&  ("1".equals(sign.getPayType()) || "2".equals(sign.getPayType()))){
            request.setAttribute("showContractTimeButton", true);
        }*/
        //确认合同暂时不用
        request.setAttribute("showContractTimeButton", false);
        // 查询赠送类型集合
        request.setAttribute("giveTypeList", getDictionaryByCode(Constants.GIVE_TYPE));
        request.setAttribute("clueId", clueId);
        request.setAttribute("signId", signId);
        request.setAttribute("readyOnly", readyOnly); // readyOnly == 1 页面只读（没有添加按钮）
        request.setAttribute("payModeItem", getDictionaryByCode(DicCodeEnum.PAYMODE.getCode()));
        request.setAttribute("type", type);
        return "bus_mycustomer/showSignAndPayDetail";
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
     * 获取当前登录账号
     *
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 查询明细
     */
    @RequestMapping("/querySignById")
    @ResponseBody
    public JSONResult<BusSignRespDTO> querySignById(@RequestBody SignParamDTO param)
            throws Exception {

        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(param.getSignId());
        JSONResult<BusSignRespDTO> res = businessSignFeignClient.querySignById(idEntityLong);
        if (JSONResult.SUCCESS.equals(res.getCode())) {
            BusSignRespDTO data = res.getData();
            IdEntityLong idLong = new IdEntityLong();
            idLong.setId(param.getClueId());
            String linkPhone = linkPhone(idLong);
            data.setPhone(linkPhone);
            if (data.getGiveType() == null) {
                data.setGiveType(-1);
            }
            // 获取签约店型
            JSONResult<List<DictionaryItemRespDTO>> vistitStoreJson =
                    getShortTypeByProjectId(data.getSignProjectId());
            data.setVistitStoreTypeArr(vistitStoreJson.getData());
            if (!checkShopType(data.getSignShopType(), vistitStoreJson.getData())) {
                data.setSignShopType(null);
            }
            res.setData(data);
            data.setPerformanceAmount(data.getAmountPerformance());
        }
        return res;
    }

    /**
     * 更新
     */
    @RequestMapping("/updateSignDetail")
    @ResponseBody
    public JSONResult<Boolean> updateSignDetail(@Valid @RequestBody BusSignInsertOrUpdateDTO dto,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateUser(user.getId());
        if (dto.getSignType() == 1) { // 全款
            dto.setMakeUpTime(null);
            dto.setAmountBalance(null);
        }
        if (user.getBusinessLine() != null) {
            dto.setBusinessLine(user.getBusinessLine());
        }
        return businessSignFeignClient.updateSignDetail(dto);
    }

    /**
     * 根据机构和角色类型获取用户
     *
     * @param
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, Integer businessLise) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        statusList.add(3);
        userOrgRoleReq.setStatusList(statusList);
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setBusinessLine(businessLise);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 分配判单用户
     *
     * @return
     */
    @RequestMapping("/distributionPdUser")
    @ResponseBody
    public JSONResult distributionPdUser(@RequestBody BusinessSignDTO businessSignDTO) {
        return businessSignFeignClient.distributionPdUser(businessSignDTO);
    }

    public JSONResult<List<DictionaryItemRespDTO>> getShortTypeByProjectId(
            @RequestBody Long projectId) {
        List<DictionaryItemRespDTO> shopList = new ArrayList<>();
        IdEntityLong idEntityLong = new IdEntityLong();
        idEntityLong.setId(projectId);
        JSONResult<ProjectInfoDTO> projectInfoDTOJSONResult =
                projectInfoFeignClient.get(idEntityLong);
        if (JSONResult.SUCCESS.equals(projectInfoDTOJSONResult.getCode())) {
            ProjectInfoDTO projectInfoDTO = projectInfoDTOJSONResult.getData();
            if (projectInfoDTO != null && StringUtils.isNotBlank(projectInfoDTO.getShopType())) {
                String[] type1 = projectInfoDTO.getShopType().split(",");
                Map<Integer, String> typeMap = new HashMap<>();
                for (int i = 0; i < type1.length; i++) {
                    typeMap.put(Integer.parseInt(type1[i]), type1[i]);
                }
                DictionaryItemQueryDTO queryDTO = new DictionaryItemQueryDTO();
                queryDTO.setGroupCode("vistitStoreType");
                JSONResult<List<DictionaryItemRespDTO>> result =
                        dictionaryItemFeignClient.queryDicItemsByGroupCode(queryDTO.getGroupCode());
                if (JSONResult.SUCCESS.equals(result.getCode())) {
                    List<DictionaryItemRespDTO> dictionaryItemRespDTOList = result.getData();
                    shopList =
                            dictionaryItemRespDTOList.stream()
                                    .filter(a -> typeMap
                                            .containsKey(Integer.parseInt(a.getValue())))
                                    .collect(Collectors.toList());
                }
            }
        }
        return new JSONResult<List<DictionaryItemRespDTO>>().success(shopList);
    }

    /**
     * 确认合同时间
     */
    @RequestMapping("/updateContractTime")
    @ResponseBody
    public JSONResult<Boolean> updateContractTime(@RequestBody BusinessSignDTO businessSignDTO){
        return businessSignFeignClient.updateContractTime(businessSignDTO);
    }

    /**
     * 更新签约单地址
     */
    @ResponseBody
    @RequestMapping("/updateSignAddress")
    public JSONResult<Boolean> updateSignAddress(@RequestBody SignParamDTO dto) {
        if (StringUtils.isBlank(dto.getSignAddress())) {
            return  new JSONResult<Boolean>().success(null);
        }
        return businessSignFeignClient.updateSignAddress(dto);
    }
}
