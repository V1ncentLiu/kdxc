package com.kuaidao.manageweb.controller.apply;

import cn.hutool.core.collection.CollectionUtil;
import com.kuaidao.aggregation.dto.apply.TeleCooperateApplyDTO;
import com.kuaidao.aggregation.dto.busmycustomer.SignRecordReqDTO;
import com.kuaidao.aggregation.dto.clue.*;
import com.kuaidao.aggregation.dto.financing.RefundRebateDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailReqDTO;
import com.kuaidao.aggregation.dto.paydetail.PayDetailRespDTO;
import com.kuaidao.aggregation.dto.sign.*;
import com.kuaidao.aggregation.dto.visitrecord.BusVisitRecordRespDTO;
import com.kuaidao.businessconfig.constant.AggregationConstant;
import com.kuaidao.businessconfig.dto.project.CompanyInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.*;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.apply.ApplyClient;
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
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description: 签约记录
 */

@Controller
@RequestMapping("/apply")
public class ApplyController {

    private static Logger logger = LoggerFactory.getLogger(ApplyController.class);
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private ApplyClient applyClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    /**
     * 有效性签约单确认列表页面
     *
     * @return
     */
    @RequestMapping("/applyPage")
    @RequiresPermissions("apply:view")
    public String applyPage(HttpServletRequest request, @RequestParam(required = false) Integer type) {
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        List<OrganizationDTO> teleGroupList = new ArrayList<>();
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            String roleCode = user.getRoleList().get(0).getRoleCode();
            if (null != roleCode) {
                if (roleCode.equals(RoleCodeEnum.DXZJ.name()) || roleCode.equals(RoleCodeEnum.DXCYGW.name())) {
                    // 如果当前登录的为电销总监,查询所有下属电销员工
                    List<Integer> statusList = new ArrayList<Integer>();
                    statusList.add(SysConstant.USER_STATUS_ENABLE);
                    statusList.add(SysConstant.USER_STATUS_LOCK);
                    List<UserInfoDTO> userList =
                            getUserList(user.getOrgId(), RoleCodeEnum.DXCYGW.name(), statusList);
                    OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(user.getOrgId().toString());
                    if (curOrgGroupByOrgId != null) {
                        teleGroupList.add(curOrgGroupByOrgId);
                    }
                    request.setAttribute("ownOrgId", user.getOrgId().toString());
                    request.setAttribute("saleList", userList);

                }else if(roleCode.equals(RoleCodeEnum.DXZJL.name()) || roleCode.equals(RoleCodeEnum.DXFZ.name()) ||  roleCode.equals(RoleCodeEnum.GLY.name())){
                    OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
                    organizationQueryDTO.setParentId(user.getOrgId());
                    organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
                    // 查询下级电销组(查询使用)
                    JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                            organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
                    if (JSONResult.SUCCESS.equals(listDescenDantByParentId.getCode()) && listDescenDantByParentId.getData() != null
                            && listDescenDantByParentId.getData().size() != 0) {
                        teleGroupList = listDescenDantByParentId.getData();
                    }
                }
            }
        }
        JSONResult<List<DictionaryItemRespDTO>> customerDefinitionListResult = dictionaryItemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.CUSTOMERDEFINITION.getCode()); // 申请客户界定
        List<DictionaryItemRespDTO> customerDefinitionList = new ArrayList<>();
        if (JSONResult.SUCCESS.equals(customerDefinitionListResult.getCode()) && customerDefinitionListResult.getData() != null
                && customerDefinitionListResult.getData().size() != 0) {
            customerDefinitionList = customerDefinitionListResult.getData();
        }
        request.setAttribute("customerDefinitionList", customerDefinitionList);
        request.setAttribute("teleGroupList", teleGroupList);
        request.setAttribute("type", type);
        return "apply/applyPage";

    }

    /**
     * 我的客户分页查询
     *
     * @param request
     * @param dto
     * @return
     */

    @RequestMapping("/findApplyList")
    @ResponseBody
    public JSONResult<PageBean<TeleCooperateApplyDTO>> findTeleClueInfo(HttpServletRequest request,
                                                                        @RequestBody TeleCooperateApplyDTO dto) {
        getApplyDTO(dto);
        JSONResult<PageBean<TeleCooperateApplyDTO>> jsonResult =
                applyClient.findApplyList(dto);
        return jsonResult;
    }

    /**
     * 查询当前登录用户能对应的信息
     *
     * @param dto
     */
    public void getApplyDTO(TeleCooperateApplyDTO dto) {
        // 数据权限处理
        Subject subject = SecurityUtils.getSubject();
        UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
        if (null != user.getRoleList() && user.getRoleList().size() > 0) {
            String roleCode = user.getRoleList().get(0).getRoleCode();
            if (null != roleCode) {
                if (roleCode.equals(RoleCodeEnum.DXFZ.name()) || roleCode.equals(RoleCodeEnum.DXZJL.name())) {

                    OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
                    organizationQueryDTO.setParentId(user.getOrgId());
                    organizationQueryDTO.setOrgType(OrgTypeConstant.DXZ);
                    // 查询下级电销组(查询使用)
                    JSONResult<List<OrganizationDTO>> listDescenDantByParentId =
                            organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);
                    if (JSONResult.SUCCESS.equals(listDescenDantByParentId.getCode()) && CollectionUtil.isNotEmpty(listDescenDantByParentId.getData())) {
                        List<Long> orgIds = listDescenDantByParentId.getData().stream().map(c -> c.getId()).collect(Collectors.toList());
                        dto.setTeleGroupIds(orgIds);
                    }
                } else if (roleCode.equals(RoleCodeEnum.DXZJ.name())) {
                    dto.setTeleGroupId(user.getOrgId());

                } else if (roleCode.equals(RoleCodeEnum.DXCYGW.name())) {
                    dto.setTeleSaleId(user.getId());

                }
            }
        }
    }

    /**
     * 根据id查询详情
     * @return
     */
    @RequestMapping("/getApplyDetail")
    @ResponseBody
    public JSONResult<TeleCooperateApplyDTO> getApplyDetail(@RequestBody TeleCooperateApplyDTO dto){
        return applyClient.getApplyDetail(dto);
    }
    /**
     * 根据机构和角色类型获取用户
     *
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
     * 转发资源
     *
     * @param
     * @return
     */
    @PostMapping("/transferApply")
    @ResponseBody
    @LogRecord(description = "转发给顾问", operationType = OperationType.DISTRIBUTION,
            menuName = MenuEnum.TELE_CUSTOMER_APPLY)
    @RequiresPermissions("apply:transferApply")
    public JSONResult transferApply(@Valid @RequestBody TeleCooperateApplyDTO teleCooperateApplyDTO,
                                    BindingResult result) {
        return applyClient.transferApply(teleCooperateApplyDTO);
    }

    /**
     * 导出合作申请表
     */
    @LogRecord(description = "导出合作申请表", operationType = OperationType.EXPORT,
            menuName = MenuEnum.TELE_CUSTOMER_APPLY)
    @PostMapping("/exportApply")
    @RequiresPermissions("apply:exportApply")
    public void exportApply(HttpServletRequest request, HttpServletResponse response,
                            @RequestBody TeleCooperateApplyDTO dto) throws Exception {
        getApplyDTO(dto);
        JSONResult<List<TeleCooperateApplyDTO>> listJSONResult = applyClient
                .exportApply(dto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadVisitPerTitleList());
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode()) && listJSONResult.getData() != null
                && listJSONResult.getData().size() != 0) {
            List<TeleCooperateApplyDTO> orderList = listJSONResult.getData();
            int size = orderList.size();
            for (TeleCooperateApplyDTO applyDTO : orderList) {
                List<Object> curList = new ArrayList<>();
                curList.add(applyDTO.getName());
                curList.add(getSex(applyDTO.getSex()));
                curList.add(applyDTO.getBirthday());
                curList.add(getPhone(applyDTO.getPhone()));
                curList.add(getMarriage(applyDTO.getMarriage()));
                curList.add(getPhone(applyDTO.getFamilyPhone()));
                curList.add(applyDTO.getFamilyAddress());
                curList.add(applyDTO.getEstimateShopArea());
                curList.add(applyDTO.getAlternateShopArea());
                curList.add(getPartnership(applyDTO.getPartnership()));
                curList.add(getPhone(applyDTO.getPartnershipPhone()));
                curList.add(applyDTO.getCooperationMode());
                curList.add(applyDTO.getIdCard());
                curList.add(applyDTO.getEducation());
                curList.add(applyDTO.getEntrepreneurshipExperience());
                curList.add(applyDTO.getWorkExperience());
                curList.add(applyDTO.getTotalMoney());
                curList.add(applyDTO.getManagement());
                curList.add(applyDTO.getOpenTime());
                curList.add(applyDTO.getInspectTime());
                curList.add(applyDTO.getInspectNum());
                curList.add(applyDTO.getInspectName());
                curList.add(getPhone(applyDTO.getInspectPhone()));
                curList.add(getIsPayAllMoney(applyDTO.getIsPayAllMoney()));
                curList.add(applyDTO.getPersonalAdvantage());
                curList.add(applyDTO.getTeleGroupName());
                curList.add(applyDTO.getConsultantName());
                curList.add(applyDTO.getCustomerDefinitionName());
                dataList.add(curList);
            }
        }

        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel1(dataList);
        String name = "在线申请表" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 导出合作申请表
     *
     * @return
     */
    private List<Object> getHeadVisitPerTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("姓名");
        headTitleList.add("性别");
        headTitleList.add("出生年月");
        headTitleList.add("联系方式");
        headTitleList.add("婚姻状况");
        headTitleList.add("家人联系方式");
        headTitleList.add("家庭地址");
        headTitleList.add("预计开店区域");
        headTitleList.add("备选开店区域");
        headTitleList.add("是否合伙");
        headTitleList.add("合伙人联系方式");
        headTitleList.add("合作模式");
        headTitleList.add("身份证号");
        headTitleList.add("最高学历");
        headTitleList.add("创业经历");
        headTitleList.add("工作经历");
        headTitleList.add("预计投入总资金");
        headTitleList.add("谁管理店面");
        headTitleList.add("何时开店");
        headTitleList.add("预约考察日期");
        headTitleList.add("考察人数");
        headTitleList.add("考察人姓名");
        headTitleList.add("考察人联系方式");
        headTitleList.add("是否全款签约");
        headTitleList.add("浅谈运作优势");
        headTitleList.add("电销组");
        headTitleList.add("您的顾问");
        headTitleList.add("客户界定");
        return headTitleList;
    }

  /**
   * 獲取性別
   * @param sex
   * @return
   */
    public String getSex(Integer sex) {
        String sexName = "";
        if (sex == 0) {
            sexName = "男";
        } else if (sex == 1) {
            sexName = "女";
        }
        return sexName;
    }

    /**
     * 手机号中间4位变为****
     * @param phone
     * @return
     */
    public String getPhone(String phone) {
        String phoneName = "";
        /*if (StringUtils.isNotBlank(phone)) {
            phoneName = phone.substring(0,9)+"**";
        }*/
        return phone;
    }

    /**
     * 是否合作
     * @param partnership
     * @return
     */
    public String getPartnership(Integer partnership) {
        String ship = "";
        if (partnership == 0) {
            ship = "否";
        } else if (partnership == 1) {
            ship = "是";
        }
        return ship;
    }

    /**
     * 是否全款
     * @param isPayAllMoney
     * @return
     */
    public String getIsPayAllMoney(Integer isPayAllMoney) {
        String ship = "";
        if (isPayAllMoney == 0) {
            ship = "否";
        } else if (isPayAllMoney == 1) {
            ship = "是";
        }
        return ship;
    }

    /**
     * 婚姻状况
     * @param
     * @return
     */
    public String getMarriage(Integer marriage) {
        String ship = "";
        if (marriage == 0) {
            ship = "未婚";
        } else if (marriage == 1) {
            ship = "已婚";
        }
        return ship;
    }
    /**
     * 获取所有组织组
     *
     * @return
     */
    private List<OrganizationRespDTO> getOrgList(Long parentId, Integer type,
                                                 Integer businessLine) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setParentId(parentId);
        queryDTO.setOrgType(type);
        queryDTO.setBusinessLine(businessLine);
        // 查询所有组织
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> data = queryOrgByParam.getData();
        return data;
    }
    /**
     * 获取当前 orgId所在的组织
     *
     * @param orgId
     * @param
     * @return
     */
    private OrganizationDTO getCurOrgGroupByOrgId(String orgId) {
        // 电销组
        IdEntity idEntity = new IdEntity();
        idEntity.setId(orgId + "");
        JSONResult<OrganizationDTO> orgJr = organizationFeignClient.queryOrgById(idEntity);
        if (!JSONResult.SUCCESS.equals(orgJr.getCode())) {
            logger.error("getCurOrgGroupByOrgId,param{{}},res{{}}", idEntity, orgJr);
            return null;
        }
        return orgJr.getData();
    }
    /**
     *
     *
     * @param
     * @return
     */
    @PostMapping("/customerDefinitionApply")
    @ResponseBody
    @LogRecord(description = "申请客户界定", operationType = OperationType.UPDATE,
            menuName = MenuEnum.TELE_CUSTOMER_APPLY)
    @RequiresPermissions("apply:customerDefinitionApply")
    public JSONResult customerDefinitionApply(@Valid @RequestBody TeleCooperateApplyDTO teleCooperateApplyDTO,
                                    BindingResult result) {
        return applyClient.transferApply(teleCooperateApplyDTO);
    }

}
