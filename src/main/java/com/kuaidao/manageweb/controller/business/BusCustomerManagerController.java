/**
 * 
 */
package com.kuaidao.manageweb.controller.business;

import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.constant.ClueCirculationConstant;
import com.kuaidao.aggregation.dto.circulation.CirculationInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.clue.BusVisitPerDTO;
import com.kuaidao.aggregation.dto.clue.ClueBasicDTO;
import com.kuaidao.aggregation.dto.clue.ClueCustomerDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueDistributionedTaskDTO;
import com.kuaidao.aggregation.dto.clue.ClueDistributionedTaskQueryDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.aggregation.dto.clue.ClueRelateDTO;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.BusCustomerDTO;
import com.kuaidao.aggregation.dto.clue.BusCustomerPageParam;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.clue.BusCustomerFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/business/busCustomerManager")
public class BusCustomerManagerController {
    private static Logger logger = LoggerFactory.getLogger(BusCustomerManagerController.class);
    @Autowired
    private BusCustomerFeignClient busCustomerFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysRegionFeignClient sysRegionFeignClient;

    /***
     * 商务客户管理页
     * 
     * @return
     */
    @RequestMapping("/initCustomerManager")
    @RequiresPermissions("business:busCustomerManager:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO user = getUser();
        String ownOrgId = "";
        // 查询所有电销组
        List<OrganizationRespDTO> teleSaleGroupList = getSaleGroupList(null, OrgTypeConstant.DXZ,null);
        request.setAttribute("teleSaleGroupList", teleSaleGroupList);
        List<RoleInfoDTO> roleList = user.getRoleList();
        // 获取业务线
        Integer businessLine = null;
        if(user.getBusinessLine() != null ){
            businessLine = user.getBusinessLine();
        }
        // 获取人员所在组织
        Long orgId =user.getOrgId();

        if (roleList != null && RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
            // 管理员 可以选择所有商务组 商务总监
            // 查询所有商务组
            List<OrganizationRespDTO> busSaleGroupList =
                    getSaleGroupList(null, OrgTypeConstant.SWZ,null);
            request.setAttribute("busSaleGroupList", busSaleGroupList);
            // 查询所有商务总监
            List<UserInfoDTO> busDirectorList = getUserList(null, RoleCodeEnum.SWZJ.name(), null,null);
            request.setAttribute("busDirectorList", busDirectorList);
        }
        else if (roleList != null
                && (RoleCodeEnum.SWDQZJ.name().equals(roleList.get(0).getRoleCode())
                        || RoleCodeEnum.BUSCENTERW.name().equals(roleList.get(0).getRoleCode())
                        || RoleCodeEnum.BUSBIGAREAW.name().equals(roleList.get(0).getRoleCode())
                        || RoleCodeEnum.SWZJ.name().equals(roleList.get(0).getRoleCode()))) {

            // 商务中心查询业务线下数据。
            if( RoleCodeEnum.BUSCENTERW.name().equals(roleList.get(0).getRoleCode())){
                orgId =null;
            }

            // 商务大区总监 可以选择本区下的商务组 商务总监
            // 商务总监 可以选择本商务组下的商务经理
            // 查询下属商务组
            List<OrganizationRespDTO> busSaleGroupList =
                    getSaleGroupList(orgId, OrgTypeConstant.SWZ,businessLine);
            // 查询本区商务总监
            List<UserInfoDTO> busDirectorList =
                    getUserList(orgId, RoleCodeEnum.SWZJ.name(), null,businessLine);
            request.setAttribute("busDirectorList", busDirectorList);
            // 查询组织下商务经理
            List<Integer> statusList = new ArrayList<Integer>();
            statusList.add(SysConstant.USER_STATUS_ENABLE);
            statusList.add(SysConstant.USER_STATUS_LOCK);
            List<UserInfoDTO> saleList =
                    getUserList(orgId, RoleCodeEnum.SWJL.name(), statusList,businessLine);
            request.setAttribute("busSaleList", saleList);

            //商务总监固定商务组筛选条件为本组
            if(RoleCodeEnum.SWZJ.name().equals(roleList.get(0).getRoleCode())){
                ownOrgId = String.valueOf(user.getOrgId());
                OrganizationDTO curOrgGroupByOrgId = getCurOrgGroupByOrgId(ownOrgId);
                if(curOrgGroupByOrgId!=null) {
                    OrganizationRespDTO organizationRespDTO = new OrganizationRespDTO();
                    organizationRespDTO.setId(curOrgGroupByOrgId.getId());
                    organizationRespDTO.setName(curOrgGroupByOrgId.getName());
                    busSaleGroupList.add(organizationRespDTO);
                }
            }
            request.setAttribute("busSaleGroupList", busSaleGroupList);
            request.setAttribute("ownOrgId", ownOrgId);
        }

        // 查询所有商务经理
        List<Map<String, Object>> allSaleList = getAllSaleList();
        request.setAttribute("allSaleList", allSaleList);
        // 查询所有签约项目
        ProjectInfoPageParam param=new ProjectInfoPageParam();
        param.setIsNotSign(AggregationConstant.NO);
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.queryBySign(param);
        request.setAttribute("projectList", allProject.getData());
        // 查询所有省
        JSONResult<List<SysRegionDTO>> getproviceList = sysRegionFeignClient.getproviceList();
        request.setAttribute("provinceList", getproviceList.getData());

        // 查询字典选址情况集合
        request.setAttribute("optionAddressList", getDictionaryByCode(Constants.OPTION_ADDRESS));
        // 查询字典合伙人集合
        request.setAttribute("partnerList", getDictionaryByCode(Constants.PARTNER));
        // 查询字典餐饮经验集合
        request.setAttribute("cateringExperienceList",
                getDictionaryByCode(Constants.CATERING_EXPERIENCE));
        // 查询字典签约店型集合
        request.setAttribute("shopTyleList", getDictionaryByCode(Constants.VISTIT_STORE_TYPE));
        // 查询字典店铺面积集合
        request.setAttribute("storefrontAreaList", getDictionaryByCode(Constants.STOREFRONT_AREA));
        // 查询字典投资金额集合
        request.setAttribute("ussmList", getDictionaryByCode(Constants.USSM));
        // 意向品类
        request.setAttribute("purTypeList", getDictionaryByCode(Constants.PROJECT_CATEGORY));
        return "business/busCustomerManagerPage";
    }

    /***
     * 商务客户管理列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("business:busCustomerManager:view")
    public JSONResult<PageBean<BusCustomerDTO>> list(@RequestBody BusCustomerPageParam pageParam,
            HttpServletRequest request) {
        UserInfoDTO user = getUser();
        // 插入当前用户、角色信息
        pageParam.setUserId(user.getId());
        pageParam.setBusinessLine(user.getBusinessLine());
        pageParam.setOrgId(user.getOrgId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        JSONResult<PageBean<BusCustomerDTO>> busCustomerList =
                busCustomerFeignClient.busCustomerList(pageParam);

        return busCustomerList;
    }





    /***
     * 下属商务经理列表
     * 
     * @return
     */
    @PostMapping("/getSaleList")
    @ResponseBody
    public JSONResult<List<UserInfoDTO>> getSaleList(@RequestBody UserOrgRoleReq userOrgRoleReq,
            HttpServletRequest request) {
        userOrgRoleReq.setRoleCode(RoleCodeEnum.SWJL.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole;
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
     * 获取所有组织组
     *
     * @return
     */
    private List<OrganizationRespDTO> getSaleGroupList(Long parentId, Integer type,Integer businessLine) {
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
     * 获取所有商务经理（组织名-大区名）
     *
     * @return
     */
    private List<Map<String, Object>> getAllSaleList() {
        UserInfoDTO userInfo = getUser();
        Integer businessLine = null;
        if(userInfo.getBusinessLine() != null){
            businessLine = userInfo.getBusinessLine();
        }
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
        List<UserInfoDTO> userList = getUserList(null, RoleCodeEnum.SWJL.name(), statusList,businessLine);

        Map<Long, OrganizationRespDTO> orgMap = new HashMap<Long, OrganizationRespDTO>();
        // 生成<机构id，机构>map
        if (groupList != null) {
            for (OrganizationRespDTO org : groupList) {
                orgMap.put(org.getId(), org);
            }
        }
        if (busAreaLsit != null) {
            for (OrganizationRespDTO org : busAreaLsit) {
                orgMap.put(org.getId(), org);
            }
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 生成结果集，匹配电销组以及电销总监
        for (UserInfoDTO user : userList) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            OrganizationRespDTO group = orgMap.get(user.getOrgId());
            if (group != null) {
                OrganizationRespDTO area = orgMap.get(group.getParentId());
                resultMap.put("id", user.getId().toString());
                if (area != null) {
                    resultMap.put("name",
                            user.getName() + "(" + area.getName() + "--" + group.getName() + ")");
                } else {
                    resultMap.put("name", user.getName() + "(" + group.getName() + ")");

                }
                result.add(resultMap);
            }
        }
        return result;
    }

    /**
     * 根据机构和角色类型获取用户
     * 
     * @param orgId
     * @param roleCode
     * @param statusList
     * @param businessLine
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList,Integer businessLine) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        userOrgRoleReq.setBusinessLine(businessLine);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
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
     * 获取当前 orgId所在的组织
     * @param orgId
     * @param
     * @return
     */
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

    /**
     * 导出到访业绩
     */
    @LogRecord(description = "导出到访业绩", operationType = OperationType.EXPORT,
        menuName = MenuEnum.BUSS_MANAGER)
    @PostMapping("/exportVisitPer")
    public void exportVisitPer(HttpServletRequest request, HttpServletResponse response,
        @RequestBody BusCustomerPageParam pageParam) throws Exception {
        UserInfoDTO user = getUser();

        pageParam.setUserId(user.getId());
        pageParam.setBusinessLine(user.getBusinessLine());
        pageParam.setOrgId(user.getOrgId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            pageParam.setRoleCode(roleList.get(0).getRoleCode());
        }
        JSONResult<List<BusVisitPerDTO>> listJSONResult = busCustomerFeignClient
            .exportVisitPer(pageParam);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadVisitPerTitleList());
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode()) && listJSONResult.getData() != null
            && listJSONResult.getData().size() != 0) {
            List<BusVisitPerDTO> orderList = listJSONResult.getData();
            int size = orderList.size();
            for (BusVisitPerDTO visitPerDTO : orderList) {
                List<Object> curList = new ArrayList<>();
                curList.add(visitPerDTO.getVisitTime());
                curList.add(visitPerDTO.getCusName());
                curList.add(visitPerDTO.getArea());
                curList.add(visitPerDTO.getVisitType());
                curList.add(visitPerDTO.getVisitNum());
                curList.add(visitPerDTO.getIsSign());
                curList.add(visitPerDTO.getSignProject());
                curList.add(visitPerDTO.getSignType());
                curList.add(visitPerDTO.getSignShopType());
                curList.add(visitPerDTO.getFirstVisitTime());
                curList.add(visitPerDTO.getAmountReceivable());
                curList.add(visitPerDTO.getAmountReceivedSum());
                curList.add(visitPerDTO.getAmountBalance());
                curList.add(visitPerDTO.getMakeUpTime());
                curList.add(visitPerDTO.getIsRemote());
                curList.add(visitPerDTO.getArrVisitCity());
                curList.add(visitPerDTO.getCompany());
                curList.add(visitPerDTO.getBusManagerName());
                curList.add(visitPerDTO.getRemark());
                curList.add(visitPerDTO.getTeleGroupName());
                curList.add(visitPerDTO.getTeleGroupProjectName());
                curList.add(visitPerDTO.getTeleSaleName());
                curList.add(visitPerDTO.getTeleDirectorName()); // 负责人
                curList.add(visitPerDTO.getTakeOverNum());
                curList.add(visitPerDTO.getSignNum());
                curList.add(visitPerDTO.getIsPreferential());
                curList.add(visitPerDTO.getConcrete());
                curList.add(visitPerDTO.getVisitProvince());
                curList.add(visitPerDTO.getProjectCategory());
                curList.add(visitPerDTO.getValue());
                dataList.add(curList);
            }
        }

        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel1(dataList);
        String name = "到访业绩" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
            "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }
    /**
     * 导出到访业绩
     * @return
     */
    private List<Object> getHeadVisitPerTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("来访日期");
        headTitleList.add("客户姓名");
        headTitleList.add("来访区域");
        headTitleList.add("到访类别");
        headTitleList.add("来访次数");
        headTitleList.add("是否成功");
        headTitleList.add("合作项目");
        headTitleList.add("签约类型");
        headTitleList.add("签约店型");
        headTitleList.add("二次来访客户首次来访洽谈日期");
        headTitleList.add("合同金额");
        headTitleList.add("已收金额");
        headTitleList.add("未收金额");
        headTitleList.add("预计补款日期");
        headTitleList.add("是否远程");
        headTitleList.add("到访城市");
        headTitleList.add("所属公司");
        headTitleList.add("洽谈人员");
        headTitleList.add("备注（未签约原因内容）");
        headTitleList.add("电销部门");
        headTitleList.add("部门项目");
        headTitleList.add("创业顾问");
        headTitleList.add("负责人");
        headTitleList.add("洽谈数量");
        headTitleList.add("签约数量");
        headTitleList.add("是否有特殊优惠以及赠送");
        headTitleList.add("具体内容");
        headTitleList.add("来访省份");
        headTitleList.add("项目类别（饮品／非饮品）");
        headTitleList.add("值");
        return headTitleList;
    }

}
