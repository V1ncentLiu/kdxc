package com.kuaidao.manageweb.controller.clue;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.kuaidao.aggregation.dto.clue.ClueAgendaTaskDTO;
import com.kuaidao.aggregation.dto.clue.ClueAgendaTaskQueryDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.PushClueReq;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.constant.BusinessLineConstant;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.clue.ExtendClueFeignClient;
import com.kuaidao.manageweb.feign.clue.MyCustomerFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.SysSettingFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.SysSettingDTO;
import com.kuaidao.sys.dto.user.SysSettingReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/exetend/agendaTaskManager")
public class ExtendClueAgendaTaskController {

    private static Logger logger = LoggerFactory.getLogger(ExtendClueAgendaTaskController.class);

    @Autowired
    private ExtendClueFeignClient extendClueFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private MyCustomerFeignClient myCustomerFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private DictionaryItemFeignClient itemFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;

    @Value("${oss.url.directUpload}")
    private String ossUrl;

    /**
     * 初始化待审核列表
     */
    @RequestMapping("/waitDistributResource")
    @RequiresPermissions("waitDistributResource:view")
    public String initWaitDistributResource(HttpServletRequest request, Model model) {
        UserInfoDTO user = getUser();
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        }

        List<UserInfoDTO> userList = queryUserByRole(user);
        // 查询字典分发失败原因集合
        request.setAttribute("reasonList",
                getDictionaryByCode(DicCodeEnum.ASSIGN_FAIL_REASON.getCode()));
        request.setAttribute("userList", userList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("waitDistributResource");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("waitDistributResource");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        return "clue/waitDistributResource";
    }

    /**
     * 跳转新增资源
     */
    @RequestMapping("/toAddPage")
    @RequiresPermissions("waitDistributResource:add")
    public String toAddPage(HttpServletRequest request, Model model) {
        UserInfoDTO user = getUser();
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(DicCodeEnum.CLUETYPE.getCode()));
        // 查询字典广告位集合
        request.setAttribute("adsenseList", getDictionaryByCode(DicCodeEnum.ADENSE.getCode()));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList",
                getDictionaryByCode(DicCodeEnum.INDUSTRYCATEGORY.getCode()));
        // 查询字典账户名称集合
        request.setAttribute("accountNameList",
                getDictionaryByCode(DicCodeEnum.ACCOUNT_NAME.getCode()));
        // 系统参数优化资源类别
        String optList = getSysSetting(SysConstant.OPT_CATEGORY);
        request.setAttribute("optList", optList);
        // 系统参数非优化资源类别
        String notOptList = getSysSetting(SysConstant.NOPT_CATEGORY);
        request.setAttribute("notOptList", notOptList);
        request.setAttribute("ossUrl", ossUrl);
        return "clue/addCluePage";
    }

    /**
     * 跳转编辑资源
     */
    @RequestMapping("/toUpdatePage")
    @RequiresPermissions("waitDistributResource:edit")
    public String toUpdatePage(@RequestParam long id, HttpServletRequest request, Model model) {
        UserInfoDTO user = getUser();
        ClueQueryDTO queryDTO = new ClueQueryDTO();

        queryDTO.setClueId(id);

        JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);

        request.setAttribute("clueInfo", clueInfo.getData());
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(DicCodeEnum.CLUETYPE.getCode()));
        // 查询字典广告位集合
        request.setAttribute("adsenseList", getDictionaryByCode(DicCodeEnum.ADENSE.getCode()));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList",
                getDictionaryByCode(DicCodeEnum.INDUSTRYCATEGORY.getCode()));
        // 查询字典账户名称集合
        request.setAttribute("accountNameList",
                getDictionaryByCode(DicCodeEnum.ACCOUNT_NAME.getCode()));
        request.setAttribute("ossUrl", ossUrl);
        // 系统参数优化资源类别
        String optList = getSysSetting(SysConstant.OPT_CATEGORY);
        request.setAttribute("optList", optList);
        // 系统参数非优化资源类别
        String notOptList = getSysSetting(SysConstant.NOPT_CATEGORY);
        request.setAttribute("notOptList", notOptList);
        return "clue/updateCluePage";
    }

    /**
     * 新建资源
     */
    @RequestMapping("/createClue")
    @RequiresPermissions("waitDistributResource:add")
    @ResponseBody
    @LogRecord(description = "新建资源", operationType = OperationType.INSERT,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<String> createClue(HttpServletRequest request,
            @RequestBody PushClueReq pushClueReq) {
        UserInfoDTO user = getUser();
        pushClueReq.setCreateUser(user.getId());
        // 推广所属公司 为当前账号所在机构的推广所属公司
        pushClueReq.setPromotionCompany(user.getPromotionCompany());
        JSONResult<String> clueInfo = extendClueFeignClient.createClue(pushClueReq);

        return clueInfo;
    }

    /**
     * 编辑资源
     */
    @RequestMapping("/updateClue")
    @RequiresPermissions("waitDistributResource:edit")
    @ResponseBody
    @LogRecord(description = "编辑资源", operationType = OperationType.UPDATE,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<String> updateClue(HttpServletRequest request,
            @RequestBody PushClueReq pushClueReq) {
        UserInfoDTO user = getUser();
        pushClueReq.setCreateUser(user.getId());
        JSONResult<String> clueInfo = extendClueFeignClient.createClue(pushClueReq);

        return clueInfo;
    }

    @RequestMapping("/queryPageAgendaTask")
    @RequiresPermissions("waitDistributResource:view")
    @ResponseBody
    public JSONResult<PageBean<ClueAgendaTaskDTO>> queryPageAgendaTask(HttpServletRequest request,
            @RequestBody ClueAgendaTaskQueryDTO queryDto) {
        UserInfoDTO user = getUser();
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        // 推广所属公司 为当前账号所在机构的推广所属公司
        queryDto.setPromotionCompany(user.getPromotionCompany());
        List<Long> idList = new ArrayList<Long>();
        // 推广总监，优化主管，优化文员，内勤经理可以在查看待分配资源列表中资源专员为管理员的数据
        if (RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode())) {
            idList.add(SysConstant.GLY_USER_ID);
        }
        // 处理数据权限，客户经理、客户主管、客户专员；内勤经理、内勤主管、内勤专员；优化经理、优化主管、优化文员
        if (RoleCodeEnum.KFZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.HWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())) {
            // 推广客服、内勤文员 能看自己的数据
            idList.add(user.getId());
        } else if (RoleCodeEnum.KFZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZZ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YYZJ.name().equals(roleInfoDTO.getRoleCode())) {
            // 客服主管、内勤主管 能看自己组员数据
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.KFJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGFZC.name().equals(roleInfoDTO.getRoleCode())
                ) {
            // 内勤经理 能看下属组的数据
            List<OrganizationRespDTO> groupList = getGroupList(user.getOrgId(), null);
            for (OrganizationRespDTO organizationRespDTO : groupList) {
                List<UserInfoDTO> userList = getUserList(organizationRespDTO.getId(), null, null);
                for (UserInfoDTO userInfoDTO : userList) {
                    idList.add(userInfoDTO.getId());
                }
            }
            idList.add(user.getId());

        } else if (RoleCodeEnum.GLY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode())) {
            idList = null;
        } else {
            return new JSONResult<PageBean<ClueAgendaTaskDTO>>()
                    .fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }
        queryDto.setResourceDirectorList(idList);
        return extendClueFeignClient.queryPageAgendaTask(queryDto);

    }

    /**
     * 单条撤回资源
     */
    @RequestMapping("/recallClue")
    @ResponseBody
    @LogRecord(description = "撤回资源", operationType = OperationType.UPDATE,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<String> recallClue(HttpServletRequest request,
            @RequestBody IdEntityLong idEntityLong) {

        JSONResult<String> clueInfo = extendClueFeignClient.recallClue(idEntityLong);

        return clueInfo;
    }

    /**
     * 批量删除资源
     */
    @RequestMapping("/deleteResource")
    @ResponseBody
    @LogRecord(description = "删除资源", operationType = OperationType.UPDATE,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<String> deleteResource(HttpServletRequest request,
            @RequestBody IdListLongReq clueIds) {

        JSONResult<String> clueInfo = extendClueFeignClient.deleteResource(clueIds);

        return clueInfo;
    }

    /**
     * 撤回资源
     */
    @RequestMapping("/recallClues")
    @ResponseBody
    @LogRecord(description = "批量撤回资源", operationType = OperationType.UPDATE,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<String> recallClues(HttpServletRequest request,
            @RequestBody IdListLongReq clueIds) {

        JSONResult<String> clueInfo = extendClueFeignClient.recallClues(clueIds);

        return clueInfo;
    }

    /**
     * 查询所有资源专员
     */

    private List<UserInfoDTO> queryUserByRole(UserInfoDTO user) {

        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        String roleCode = user.getRoleList().get(0).getRoleCode();
        UserOrgRoleReq userRole = new UserOrgRoleReq();
        if (RoleCodeEnum.HWY.name().equals(roleCode)) {
            userList.add(user);
            return userList;
        } else if (RoleCodeEnum.GLY.name().equals(roleCode)
                || RoleCodeEnum.YWGLY.name().equals(roleCode)
                || RoleCodeEnum.TGZJ.name().equals(roleCode)) {
            userRole.setBusinessLine(BusinessLineConstant.TGZX);
        } else {
            userRole.setOrgId(user.getOrgId());
        }
        JSONResult<List<UserInfoDTO>> userZxzjList = userInfoFeignClient.listByOrgAndRole(userRole);
        if (JSONResult.SUCCESS.equals(userZxzjList.getCode()) && null != userZxzjList.getData()) {
            userList = userZxzjList.getData();
        }
        // 查询管理员放入user集合
        List<UserInfoDTO> userAdminList = new ArrayList<UserInfoDTO>();
        UserOrgRoleReq userRoleAdmin = new UserOrgRoleReq();
        userRoleAdmin.setRoleCode(RoleCodeEnum.GLY.name());
        JSONResult<List<UserInfoDTO>> userAdminJson =
                userInfoFeignClient.listByOrgAndRole(userRoleAdmin);
        if (JSONResult.SUCCESS.equals(userAdminJson.getCode()) && null != userAdminJson.getData()) {
            userAdminList = userAdminJson.getData();
        }
        userList.addAll(userAdminList);
        return userList;
    }

    /**
     * 客户详情
     */
    @RequestMapping("/customerInfoView")
    @ResponseBody
    public JSONResult<ClueDTO> customerInfoReadOnly(HttpServletRequest request,
            @RequestBody ClueAgendaTaskQueryDTO queryDto) {

        ClueQueryDTO queryDTO = new ClueQueryDTO();

        queryDTO.setClueId(queryDto.getClueId());

        JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);

        return clueInfo;
    }

    /**
     * 自动分配
     */
    @RequestMapping("/autoAllocationTask")
    @RequiresPermissions("waitDistributResource:distribute")
    @ResponseBody
    @LogRecord(description = "待分发资源自动分配", operationType = OperationType.DISTRIBUTION,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<Integer> autoAllocationTask(HttpServletRequest request,
            @RequestBody IdListLongReq queryDto) {

        JSONResult<Integer> clueInfo = extendClueFeignClient.autoAllocationTask(queryDto);

        return clueInfo;
    }

    /**
     * 获取当前登录账号
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 查询字典表
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
     * 预览
     */
    // @RequiresPermissions("customfield:batchSaveField")
    @PostMapping("/uploadCustomField")
    @ResponseBody
    public JSONResult uploadCustomField(@RequestParam("file") MultipartFile file) throws Exception {
        List<List<Object>> excelDataList = ExcelUtil.read2007Excel(file.getInputStream());
        logger.info("customfield upload size:{{}}", excelDataList.size());

        if (excelDataList == null || excelDataList.size() == 0) {
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_DATA.getCode(),
                    SysErrorCodeEnum.ERR_EXCLE_DATA.getMessage());
        }
        if (excelDataList.size() > 1000) {
            logger.error("上传自定义字段,大于1000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),
                    "导入数据过多，已超过1000条！");
        }

        // 存放合法的数据
        List<ClueAgendaTaskDTO> dataList = new ArrayList<ClueAgendaTaskDTO>();
        for (int i = 1; i < excelDataList.size(); i++) {
            List<Object> rowList = excelDataList.get(i);
            ClueAgendaTaskDTO rowDto = new ClueAgendaTaskDTO();
            for (int j = 0; j < rowList.size(); j++) {
                Object object = rowList.get(j);
                String value = (String) object;
                if (j == 0) {// 日期
                    rowDto.setDate(value);
                } else if (j == 1) {// 媒介
                    rowDto.setSourceName(value);
                } else if (j == 2) {// 广告位
                    rowDto.setSourceTypeName(value);
                } else if (j == 3) {// 资源类型
                    rowDto.setTypeName(value);
                } else if (j == 4) {// 资源类别
                    rowDto.setCategoryName(value);
                } else if (j == 5) {// 项目
                    rowDto.setProjectName(value);
                } else if (j == 6) {// 编码
                    rowDto.setCode(value);
                } else if (j == 7) {// 姓名
                    rowDto.setCusName(value);
                } else if (j == 8) {// 手机
                    rowDto.setPhone(value);
                } else if (j == 9) {// 邮箱
                    rowDto.setEmail(value);
                } else if (j == 10) {// QQ
                    rowDto.setQq(value);
                } else if (j == 11) {// 手机2
                    rowDto.setPhone2(value);
                } else if (j == 12) {// 微信
                    rowDto.setWechat(value);
                } else if (j == 13) {// 地址
                    rowDto.setAddress(value);
                } else if (j == 14) {// 留言时间
                    rowDto.setMessageTime1(value);
                } else if (j == 15) {// 留言内容
                    rowDto.setMessagePoint(value);
                } else if (j == 16) {// 搜索词
                    rowDto.setSearchWord(value);
                }else if (j == 17) {// 词根
                    rowDto.setRootWord(value);
                } else if (j == 18) {// 行业类别
                    rowDto.setIndustryCategoryName(value);
                } else if (j == 19) {// 备注
                    rowDto.setRemark(value);
                } else if (j == 20) {// 微信2
                    rowDto.setWechat2(value);
                } else if (j == 21) {// 预约回访时间
                    rowDto.setReserveTime1(value);
                } else if (j == 22) {// url地址
                    rowDto.setUrlAddress(value);
                } else if (j == 23) {// 账户名称
                    rowDto.setAccountName(value);
                } else if (j == 24) {// 性别
                    rowDto.setSex1(value);
                } else if (j == 25) {// 年龄
                    rowDto.setAge1(value);
                }
            } // inner foreach end
            dataList.add(rowDto);
        }
        // outer foreach end
        logger.info("upload custom filed, valid success num{{}}", dataList.size());
        /*
         * JSONResult uploadRs = customFieldFeignClient.saveBatchCustomField(dataList);
         * if(uploadRs==null || !JSONResult.SUCCESS.equals(uploadRs.getCode())) { return uploadRs; }
         */

        return new JSONResult<>().success(dataList);
    }


    /**
     * 导入资源
     */
    @PostMapping("/importClue")
    @RequiresPermissions("waitDistributResource:importExcel")
    @LogRecord(description = "导入资源", operationType = LogRecord.OperationType.IMPORTS,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    @ResponseBody
    public JSONResult importClue(@RequestBody ClueAgendaTaskDTO clueAgendaTaskDTO)
            throws Exception {
        UserInfoDTO user =
                (UserInfoDTO) SecurityUtils.getSubject().getSession().getAttribute("user");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 存放合法的数据
        List<ClueAgendaTaskDTO> dataList = new ArrayList<ClueAgendaTaskDTO>();
        // 存放非法的数据
        List<ClueAgendaTaskDTO> illegalDataList = new ArrayList<ClueAgendaTaskDTO>();
        // 存放结果
        Map<String, Object> result = new HashMap<>();
        // 项目处理
        ProjectInfoPageParam projectInfoPageParam = new ProjectInfoPageParam();
        List<ProjectInfoDTO> proList =
                projectInfoFeignClient.listNoPage(projectInfoPageParam).getData();
        Map<String, Long> projectMap = new HashMap<String, Long>();
        Map<Long, String> projectMap2 = new HashMap<Long, String>();
        // 遍历项目list集生成<name,id>map
        if (null != proList && proList.size() > 0) {
            for (ProjectInfoDTO projectInfoDTO : proList) {
                projectMap.put(projectInfoDTO.getProjectName().toUpperCase(),
                        projectInfoDTO.getId());
            }
            // 遍历项目list集生成<id,name>map
            for (ProjectInfoDTO projectInfoDTO : proList) {
                projectMap2.put(projectInfoDTO.getId(), projectInfoDTO.getProjectName());
            }
        }
        List<ClueAgendaTaskDTO> list = clueAgendaTaskDTO.getList();
        List<PushClueReq> list1 = new ArrayList<PushClueReq>();
        // 总条数
        result.put("total", list.size());
        // 初始化
        result.put("trash", 0);
        result.put("assign", 0);
        result.put("notAssign", 0);
        result.put("success", 0);
        result.put("fail", 0);
        // 匹配字典数据
        // 资源类型<name,value>
        Map<String, String> typeMap =
                dicMap(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.CLUETYPE.getCode()));
        // 资源类型<value,name>
        Map<String, String> typeMap2 =
                dicMapTwo(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.CLUETYPE.getCode()));
        // 资源类别<name,value>
        Map<String, String> categoryMap = dicMap(
                itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 资源类型<value,name>
        Map<String, String> categoryMap2 = dicMapTwo(
                itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 广告位<name,value>
        Map<String, String> sourceTypeMap =
                dicMapUpper(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.ADENSE.getCode()));
        // 资源类型<value,name>
        Map<String, String> sourceTypeMap2 =
                dicMapTwo(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.ADENSE.getCode()));
        // 媒介<name,value>
        Map<String, String> sourceMap =
                dicMapUpper(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.MEDIUM.getCode()));
        // 资源类型<value,name>
        Map<String, String> sourceMap2 =
                dicMapTwo(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.MEDIUM.getCode()));
        // 行业类别<name,value>
        Map<String, String> industryCategoryMap = dicMap(
                itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.INDUSTRYCATEGORY.getCode()));
        // 资源类型<value,name>
        Map<String, String> industryCategoryMap2 = dicMapTwo(
                itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.INDUSTRYCATEGORY.getCode()));
        // 账户名称<name,value>
        Map<String, String> accountNameMap = dicMap(
                itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.ACCOUNT_NAME.getCode()));
        // 资源类型<value,name>
        Map<String, String> accountNameMap2 = dicMapTwo(
                itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.ACCOUNT_NAME.getCode()));
        // 系统参数优化资源类别
        String opt = getSysSetting(SysConstant.OPT_CATEGORY);
        List<String> optList = stringToList(opt);
        // 系统参数非优化资源类别
        String notOpt = getSysSetting(SysConstant.NOPT_CATEGORY);
        List<String> notOptList = stringToList(notOpt);
        if (list != null && list.size() > 0) {

            for (ClueAgendaTaskDTO clueAgendaTaskDTOReq : list) {
                logger.info("clue import list:{{}}", clueAgendaTaskDTOReq);
                // true合法 false不合法
                boolean islegal = true;
                // 存放失败原因
                StringBuilder failReason = new StringBuilder();
                // 时间格式错误原因
                StringBuilder reasonInTime = new StringBuilder();
                // 判断时间格式是否正确
                if (clueAgendaTaskDTOReq.getReserveTime1() != null
                        && !"".equals(clueAgendaTaskDTOReq.getReserveTime1())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq
                            .setReserveTime1(clueAgendaTaskDTOReq.getReserveTime1().trim());
                    // 去掉前后空格后是否为空
                    if (clueAgendaTaskDTOReq.getReserveTime1() != null
                            && !"".equals(clueAgendaTaskDTOReq.getReserveTime1())) {
                        try {
                            Date date = format.parse(clueAgendaTaskDTOReq.getReserveTime1());
                        } catch (ParseException e) {
                            islegal = false;
                            reasonInTime.append("预约回访时间");
                        }
                    }
                }
                if (clueAgendaTaskDTOReq.getMessageTime1() != null
                        && !"".equals(clueAgendaTaskDTOReq.getMessageTime1())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq
                            .setMessageTime1(clueAgendaTaskDTOReq.getMessageTime1().trim());
                    // 去掉前后空格后是否为空
                    if (clueAgendaTaskDTOReq.getMessageTime1() != null
                            && !"".equals(clueAgendaTaskDTOReq.getMessageTime1())) {
                        try {
                            Date date = format.parse(clueAgendaTaskDTOReq.getMessageTime1());
                        } catch (ParseException e) {
                            islegal = false;
                            if (StringUtils.isBlank(reasonInTime)) {
                                reasonInTime.append("留言时间");
                            } else {
                                reasonInTime.append("、留言时间");
                            }
                        }
                    }
                }
                if (StringUtils.isNotBlank(reasonInTime)) {
                    failReason.append(reasonInTime + "时间格式错误；");
                }
                // 判断字典表数据是否匹配
                // 导入失败原因：必填项为空
                StringBuilder reasonIsNull = new StringBuilder();
                // 导入失败原因：字典匹配失败
                StringBuilder reasonIsNotMatch = new StringBuilder();
                if (clueAgendaTaskDTOReq.getTypeName() != null
                        && !"".equals(clueAgendaTaskDTOReq.getTypeName())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq.setTypeName(clueAgendaTaskDTOReq.getTypeName().trim());
                    // 去掉前后空格后是否为空
                    if (clueAgendaTaskDTOReq.getTypeName() != null
                            && !"".equals(clueAgendaTaskDTOReq.getTypeName())) {
                        String type = typeMap.get(clueAgendaTaskDTOReq.getTypeName());
                        if (StringUtils.isNotBlank(type)) {
                            clueAgendaTaskDTOReq.setType(Integer.valueOf(type));
                        } else {
                            islegal = false;
                            reasonIsNotMatch.append("资源类型");
                        }
                    } else {
                        islegal = false;
                        reasonIsNull.append("资源类型");
                    }
                } else {
                    islegal = false;
                    reasonIsNull.append("资源类型");
                }
                if (clueAgendaTaskDTOReq.getCategoryName() != null
                        && !"".equals(clueAgendaTaskDTOReq.getCategoryName())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq
                            .setCategoryName(clueAgendaTaskDTOReq.getCategoryName().trim());
                    // 去掉前后空格后是否为空
                    if (clueAgendaTaskDTOReq.getCategoryName() != null
                            && !"".equals(clueAgendaTaskDTOReq.getCategoryName())) {
                        String category = categoryMap.get(clueAgendaTaskDTOReq.getCategoryName());
                        if (StringUtils.isNotBlank(category)) {
                            clueAgendaTaskDTOReq.setCategory(Integer.valueOf(category));
                        } else {
                            islegal = false;
                            if (StringUtils.isBlank(reasonIsNotMatch)) {
                                reasonIsNotMatch.append("资源类别");
                            } else {
                                reasonIsNotMatch.append("、资源类别");
                            }
                        }
                    } else {
                        islegal = false;
                        if (StringUtils.isBlank(reasonIsNull)) {
                            reasonIsNull.append("资源类别");
                        } else {
                            reasonIsNull.append("、资源类别");
                        }
                    }
                } else {
                    islegal = false;
                    if (StringUtils.isBlank(reasonIsNull)) {
                        reasonIsNull.append("资源类别");
                    } else {
                        reasonIsNull.append("、资源类别");
                    }
                }
                if (clueAgendaTaskDTOReq.getSourceTypeName() != null
                        && !"".equals(clueAgendaTaskDTOReq.getSourceTypeName())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq
                            .setSourceTypeName(clueAgendaTaskDTOReq.getSourceTypeName().trim());
                    // 去掉前后空格后是否为空
                    if (clueAgendaTaskDTOReq.getSourceTypeName() != null
                            && !"".equals(clueAgendaTaskDTOReq.getSourceTypeName())) {
                        String sourceType = sourceTypeMap
                                .get(clueAgendaTaskDTOReq.getSourceTypeName().toUpperCase());
                        if (StringUtils.isNotBlank(sourceType)) {
                            clueAgendaTaskDTOReq.setSourceType(Integer.valueOf(sourceType));
                        } else {
                            islegal = false;
                            if (StringUtils.isBlank(reasonIsNotMatch)) {
                                reasonIsNotMatch.append("广告位");
                            } else {
                                reasonIsNotMatch.append("、广告位");
                            }
                        }
                    }
                }
                if (clueAgendaTaskDTOReq.getSourceName() != null
                        && !"".equals(clueAgendaTaskDTOReq.getSourceName())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq.setSourceName(clueAgendaTaskDTOReq.getSourceName().trim());
                    // 去掉前后空格后是否为空
                    if (clueAgendaTaskDTOReq.getSourceName() != null
                            && !"".equals(clueAgendaTaskDTOReq.getSourceName())) {
                        String source =
                                sourceMap.get(clueAgendaTaskDTOReq.getSourceName().toUpperCase());
                        if (StringUtils.isNotBlank(source)) {
                            clueAgendaTaskDTOReq.setSource(Integer.valueOf(source));
                        } else {
                            islegal = false;
                            if (StringUtils.isBlank(reasonIsNotMatch)) {
                                reasonIsNotMatch.append("媒介");
                            } else {
                                reasonIsNotMatch.append("、媒介");
                            }
                        }
                    } else {
                        islegal = false;
                        if (StringUtils.isBlank(reasonIsNull)) {
                            reasonIsNull.append("媒介");
                        } else {
                            reasonIsNull.append("、媒介");
                        }
                    }
                } else {
                    islegal = false;
                    if (StringUtils.isBlank(reasonIsNull)) {
                        reasonIsNull.append("媒介");
                    } else {
                        reasonIsNull.append("、媒介");
                    }
                }
                // 判断是否具有合格的资源类别,若没有则不进行项目校验
                if (null != clueAgendaTaskDTOReq.getCategory()) {
                    // 判断是否存在该项目
                    if (clueAgendaTaskDTOReq.getProjectName() != null
                            && !"".equals(clueAgendaTaskDTOReq.getProjectName())) {
                        // 去掉前后空格
                        clueAgendaTaskDTOReq
                                .setProjectName(clueAgendaTaskDTOReq.getProjectName().trim());
                        // 去掉前后空格后是否为空
                        if (clueAgendaTaskDTOReq.getProjectName() != null
                                && !"".equals(clueAgendaTaskDTOReq.getProjectName())) {
                            // 判断资源是否优化类，优化类则不进行项目匹配
                            if (notOptList
                                    .contains(String.valueOf(clueAgendaTaskDTOReq.getCategory()))) {
                                clueAgendaTaskDTOReq.setProjectId(projectMap
                                        .get(clueAgendaTaskDTOReq.getProjectName().toUpperCase()));
                                if (clueAgendaTaskDTOReq.getProjectId() == null) {
                                    islegal = false;
                                    if (StringUtils.isBlank(reasonIsNotMatch)) {
                                        reasonIsNotMatch.append("资源项目(项目名称)");
                                    } else {
                                        reasonIsNotMatch.append("、资源项目(项目名称)");
                                    }
                                }
                            }
                        } else {
                            islegal = false;
                            if (StringUtils.isBlank(reasonIsNull)) {
                                reasonIsNull.append("资源项目(项目名称)");
                            } else {
                                reasonIsNull.append("、资源项目(项目名称)");
                            }
                        }
                    } else {
                        islegal = false;
                        if (StringUtils.isBlank(reasonIsNull)) {
                            reasonIsNull.append("资源项目(项目名称)");
                        } else {
                            reasonIsNull.append("、资源项目(项目名称)");
                        }
                    }
                }
                if (clueAgendaTaskDTOReq.getIndustryCategoryName() != null
                        && !"".equals(clueAgendaTaskDTOReq.getIndustryCategoryName())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq.setIndustryCategoryName(
                            clueAgendaTaskDTOReq.getIndustryCategoryName().trim());
                    // 去掉前后空格后是否为空
                    if (clueAgendaTaskDTOReq.getIndustryCategoryName() != null
                            && !"".equals(clueAgendaTaskDTOReq.getIndustryCategoryName())) {
                        String industryCategory = industryCategoryMap
                                .get(clueAgendaTaskDTOReq.getIndustryCategoryName());
                        if (StringUtils.isNotBlank(industryCategory)) {
                            clueAgendaTaskDTOReq
                                    .setIndustryCategory(Integer.valueOf(industryCategory));
                        } else {
                            islegal = false;
                            if (StringUtils.isBlank(reasonIsNotMatch)) {
                                reasonIsNotMatch.append("行业类别");
                            } else {
                                reasonIsNotMatch.append("、行业类别");
                            }
                        }
                    }
                }
                if (clueAgendaTaskDTOReq.getAccountName() != null
                        && !"".equals(clueAgendaTaskDTOReq.getAccountName())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq
                            .setAccountName(clueAgendaTaskDTOReq.getAccountName().trim());
                    // 去掉前后空格后是否为空
                    if (clueAgendaTaskDTOReq.getAccountName() != null
                            && !"".equals(clueAgendaTaskDTOReq.getAccountName())) {
                        String account = accountNameMap.get(clueAgendaTaskDTOReq.getAccountName());
                        if (StringUtils.isNotBlank(account)) {
                            clueAgendaTaskDTOReq.setAccountNameVaule(account);
                        } else {
                            islegal = false;
                            if (StringUtils.isBlank(reasonIsNotMatch)) {
                                reasonIsNotMatch.append("账户名称");
                            } else {
                                reasonIsNotMatch.append("、账户名称");
                            }
                        }
                    }
                }
                if (StringUtils.isNotBlank(reasonIsNull)) {
                    failReason.append(reasonIsNull + "为必填项；");
                }
                if (StringUtils.isNotBlank(reasonIsNotMatch)) {
                    failReason.append(reasonIsNotMatch + "与数据字典字段不匹配；");
                }

                // 判断性别
                if (clueAgendaTaskDTOReq.getSex1() != null
                        && !"".equals(clueAgendaTaskDTOReq.getSex1())) {
                    // 去掉前后空格
                    clueAgendaTaskDTOReq.setSex1(clueAgendaTaskDTOReq.getSex1().trim());
                    if ("男".equals(clueAgendaTaskDTOReq.getSex1())) {
                        clueAgendaTaskDTOReq.setSex(1);
                    } else if ("女".equals(clueAgendaTaskDTOReq.getSex1())) {
                        clueAgendaTaskDTOReq.setSex(2);
                    }
                }
                // 判断联系方式
                if ((StringUtils.isBlank(clueAgendaTaskDTOReq.getPhone())
                        || (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getPhone())
                                && StringUtils.isBlank(clueAgendaTaskDTOReq.getPhone().trim())))
                        && (StringUtils.isBlank(clueAgendaTaskDTOReq.getPhone2()) || (StringUtils
                                .isNotBlank(clueAgendaTaskDTOReq.getPhone2())
                                && StringUtils.isBlank(clueAgendaTaskDTOReq.getPhone2().trim())))
                        && (StringUtils.isBlank(clueAgendaTaskDTOReq.getWechat()) || (StringUtils
                                .isNotBlank(clueAgendaTaskDTOReq.getWechat())
                                && StringUtils.isBlank(clueAgendaTaskDTOReq.getWechat().trim())))
                        && (StringUtils.isBlank(clueAgendaTaskDTOReq.getWechat2()) || (StringUtils
                                .isNotBlank(clueAgendaTaskDTOReq.getWechat2())
                                && StringUtils.isBlank(clueAgendaTaskDTOReq.getWechat2().trim())))
                        && (StringUtils.isBlank(clueAgendaTaskDTOReq.getQq()) || (StringUtils
                                .isNotBlank(clueAgendaTaskDTOReq.getQq())
                                && StringUtils.isBlank(clueAgendaTaskDTOReq.getQq().trim())))
                        && (StringUtils.isBlank(clueAgendaTaskDTOReq.getEmail()) || (StringUtils
                                .isNotBlank(clueAgendaTaskDTOReq.getEmail())
                                && StringUtils.isBlank(clueAgendaTaskDTOReq.getEmail().trim())))) {
                    islegal = false;
                    failReason.append("联系方式需至少填写一项；");
                }
                // 全部符合则进行匹配站点、去重、分发，不符合进入导入失败列表
                if (islegal) {
                    PushClueReq pushClueReq = new PushClueReq();
                    // 拼接地址和备注
                    StringBuilder addressAndRemark = new StringBuilder();
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getAddress())) {
                        addressAndRemark.append(clueAgendaTaskDTOReq.getAddress());
                        if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getRemark())) {
                            addressAndRemark.append(";" + clueAgendaTaskDTOReq.getRemark());
                        }
                    } else {
                        if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getRemark())) {
                            addressAndRemark.append(clueAgendaTaskDTOReq.getRemark());
                        }
                    }
                    pushClueReq.setCategory(String.valueOf(clueAgendaTaskDTOReq.getCategory()));
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getCusName())) {
                        pushClueReq.setCusName(clueAgendaTaskDTOReq.getCusName());
                    } else {
                        pushClueReq.setCusName("未知");
                    }
                    pushClueReq.setSex(clueAgendaTaskDTOReq.getSex());
                    // 手机号相同则只存储phone1
                    if ((StringUtils.isNotBlank(clueAgendaTaskDTOReq.getPhone()) && StringUtils
                            .isNotBlank(clueAgendaTaskDTOReq.getPhone().replaceAll(" ", "")))
                            && (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getPhone2())
                                    && StringUtils.isNotBlank(
                                            clueAgendaTaskDTOReq.getPhone2().replaceAll(" ", "")))
                            && clueAgendaTaskDTOReq.getPhone().replaceAll(" ", "")
                                    .equals(clueAgendaTaskDTOReq.getPhone2().replaceAll(" ", ""))) {
                        pushClueReq.setPhone(clueAgendaTaskDTOReq.getPhone().replaceAll(" ", ""));
                    } else {
                        if ((StringUtils.isNotBlank(clueAgendaTaskDTOReq.getPhone()) && StringUtils
                                .isNotBlank(clueAgendaTaskDTOReq.getPhone().replaceAll(" ", "")))) {
                            pushClueReq
                                    .setPhone(clueAgendaTaskDTOReq.getPhone().replaceAll(" ", ""));
                        }
                        if ((StringUtils.isNotBlank(clueAgendaTaskDTOReq.getPhone2())
                                && StringUtils.isNotBlank(
                                        clueAgendaTaskDTOReq.getPhone2().replaceAll(" ", "")))) {
                            pushClueReq.setPhone2(
                                    clueAgendaTaskDTOReq.getPhone2().replaceAll(" ", ""));
                        }
                    }
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getWechat())
                            && StringUtils.isNotBlank(clueAgendaTaskDTOReq.getWechat().trim())) {
                        pushClueReq.setWechat(clueAgendaTaskDTOReq.getWechat().trim());
                    }
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getWechat2())
                            && StringUtils.isNotBlank(clueAgendaTaskDTOReq.getWechat2().trim())) {
                        pushClueReq.setWechat2(clueAgendaTaskDTOReq.getWechat2().trim());
                    }
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getQq())
                            && StringUtils.isNotBlank(clueAgendaTaskDTOReq.getQq().trim())) {
                        pushClueReq.setQq(clueAgendaTaskDTOReq.getQq().trim());
                    }
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getEmail())
                            && StringUtils.isNotBlank(clueAgendaTaskDTOReq.getEmail().trim())) {
                        pushClueReq.setEmail(clueAgendaTaskDTOReq.getEmail().trim());
                    }
                    if (StringUtils.isNotBlank(addressAndRemark)) {
                        pushClueReq.setRemark(addressAndRemark.toString());
                    }
                    pushClueReq.setSearchWord(clueAgendaTaskDTOReq.getSearchWord());
                    pushClueReq.setSource(String.valueOf(clueAgendaTaskDTOReq.getSource()));
                    pushClueReq.setSourceName(clueAgendaTaskDTOReq.getSourceName());
                    if (null != clueAgendaTaskDTOReq.getSourceType()) {
                        pushClueReq.setSourceType(
                                String.valueOf(clueAgendaTaskDTOReq.getSourceType()));
                    }
                    pushClueReq.setType(String.valueOf(clueAgendaTaskDTOReq.getType()));
                    pushClueReq.setMessagePoint(clueAgendaTaskDTOReq.getMessagePoint());
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getMessageTime1())) {

                        pushClueReq.setMessageTime(DateUtil.convert2Date(
                                clueAgendaTaskDTOReq.getMessageTime1(), DateUtil.ymdhms));
                    }
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getReserveTime1())) {
                        pushClueReq.setReserveTime(DateUtil.convert2Date(
                                clueAgendaTaskDTOReq.getReserveTime1(), DateUtil.ymdhms));
                    }
                    pushClueReq.setCreateTime(format.format(new Date()));
                    pushClueReq.setInputType(4);
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getAccountNameVaule())) {
                        pushClueReq.setAccountName(
                                String.valueOf(clueAgendaTaskDTOReq.getAccountNameVaule()));
                    }
                    pushClueReq.setUrlAddress(clueAgendaTaskDTOReq.getUrlAddress());
                    if (clueAgendaTaskDTOReq.getIndustryCategory() != null) {
                        pushClueReq.setIndustryCategory(
                                String.valueOf(clueAgendaTaskDTOReq.getIndustryCategory()));
                    }
                    pushClueReq.setProjectId(clueAgendaTaskDTOReq.getProjectId());
                    pushClueReq.setProjectName(clueAgendaTaskDTOReq.getProjectName());
                    pushClueReq.setCreateUser(user.getId());
                    if (StringUtils.isNotBlank(clueAgendaTaskDTOReq.getAge1())) {
                        pushClueReq.setAge(Integer.valueOf(clueAgendaTaskDTOReq.getAge1()));
                    }
                    // 推广所属公司 为当前账号所在机构的推广所属公司
                    pushClueReq.setPromotionCompany(user.getPromotionCompany());
                    list1.add(pushClueReq);
                } else {
                    clueAgendaTaskDTOReq.setImportFailReason(failReason.toString());
                    illegalDataList.add(clueAgendaTaskDTOReq);
                }
            }
        }
        logger.info("clue import:{{}}", list1);
        logger.info("clue not import:{{}}", illegalDataList);
        if (list1 != null && list1.size() > 0) {
            JSONResult<List<PushClueReq>> jsonResult = extendClueFeignClient.importclue(list1);
        }
        result.put("success", (list.size() - illegalDataList.size()));
        result.put("fail", illegalDataList.size());
        result.put("illegalDataList", illegalDataList);
        return new JSONResult<>().success(result);
    }

    /**
     * 数据字典-词条转换Map（name-value）
     */
    public Map dicMap(JSONResult<List<DictionaryItemRespDTO>> result) {
        Map map = new HashMap();
        if (JSONResult.SUCCESS.equals(result.getCode())) {
            List<DictionaryItemRespDTO> data = result.getData();
            for (DictionaryItemRespDTO itemRespDTO : data) {
                map.put(itemRespDTO.getName(), itemRespDTO.getValue());
            }
        }
        return map;
    }

    /**
     * 数据字典-词条转换Map（name-value）(全大写)
     */
    public Map dicMapUpper(JSONResult<List<DictionaryItemRespDTO>> result) {
        Map map = new HashMap();
        if (JSONResult.SUCCESS.equals(result.getCode())) {
            List<DictionaryItemRespDTO> data = result.getData();
            for (DictionaryItemRespDTO itemRespDTO : data) {
                map.put(itemRespDTO.getName().toUpperCase(), itemRespDTO.getValue());
            }
        }
        return map;
    }

    /**
     * 数据字典-词条转换Map（value-name）
     */
    public Map dicMapTwo(JSONResult<List<DictionaryItemRespDTO>> result) {
        Map map = new HashMap();
        if (JSONResult.SUCCESS.equals(result.getCode())) {
            List<DictionaryItemRespDTO> data = result.getData();
            for (DictionaryItemRespDTO itemRespDTO : data) {
                map.put(itemRespDTO.getValue(), itemRespDTO.getName());
            }
        }
        return map;
    }

    /**
     * 导出
     */
    @PostMapping("/exportFaultClue")
    @LogRecord(description = "下载导入失败资源", operationType = OperationType.EXPORT,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public void exportFaultClue(@RequestBody ClueAgendaTaskDTO dto, HttpServletResponse response)
            throws Exception {

        List<ClueAgendaTaskDTO> list = dto.getList();
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());

        if (list != null && list.size() != 0) {

            int size = list.size();
            for (int i = 0; i < size; i++) {
                ClueAgendaTaskDTO clueAgendaTaskDTO = list.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(clueAgendaTaskDTO.getDate());
                curList.add(clueAgendaTaskDTO.getSourceName());
                curList.add(clueAgendaTaskDTO.getSourceTypeName());
                curList.add(clueAgendaTaskDTO.getTypeName());
                curList.add(clueAgendaTaskDTO.getCategoryName());
                curList.add(clueAgendaTaskDTO.getProjectName());
                curList.add(clueAgendaTaskDTO.getCode());
                curList.add(clueAgendaTaskDTO.getCusName());
                curList.add(clueAgendaTaskDTO.getPhone());
                curList.add(clueAgendaTaskDTO.getEmail());
                curList.add(clueAgendaTaskDTO.getQq());
                curList.add(clueAgendaTaskDTO.getPhone2());
                curList.add(clueAgendaTaskDTO.getWechat());
                curList.add(clueAgendaTaskDTO.getAddress());
                curList.add(clueAgendaTaskDTO.getMessageTime1());
                curList.add(clueAgendaTaskDTO.getMessagePoint());
                curList.add(clueAgendaTaskDTO.getSearchWord());
                curList.add(clueAgendaTaskDTO.getIndustryCategoryName());
                curList.add(clueAgendaTaskDTO.getRemark());
                curList.add(clueAgendaTaskDTO.getWechat2());
                curList.add(clueAgendaTaskDTO.getReserveTime1());
                curList.add(clueAgendaTaskDTO.getUrlAddress());
                curList.add(clueAgendaTaskDTO.getAccountName());
                curList.add(clueAgendaTaskDTO.getSex1());
                curList.add(clueAgendaTaskDTO.getAge1());
                curList.add(clueAgendaTaskDTO.getImportFailReason());

                dataList.add(curList);
            }

        } else {
            logger.error("export trucking_order param{{}},res{{}}", dto, list);
        }
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            XSSFWorkbook wbWorkbook = ExcelUtil.creatFailClueExcel(dataList);

            String name = DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            wbWorkbook.write(outputStream);
        }

    }


    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("创建时间");
        headTitleList.add("媒介");
        headTitleList.add("广告位");
        headTitleList.add("资源类型");
        headTitleList.add("资源类别");
        headTitleList.add("项目");
        headTitleList.add("编码");
        headTitleList.add("姓名");
        headTitleList.add("手机");
        headTitleList.add("邮箱");
        headTitleList.add("QQ");
        headTitleList.add("手机2");
        headTitleList.add("微信");
        headTitleList.add("地址");
        headTitleList.add("留言时间");
        headTitleList.add("留言内容");
        headTitleList.add("搜索词");
        headTitleList.add("行业类别");
        headTitleList.add("备注");
        headTitleList.add("微信2");
        headTitleList.add("预约时间");
        headTitleList.add("url地址");
        headTitleList.add("账户名称");
        headTitleList.add("性别");
        headTitleList.add("年龄");
        headTitleList.add("导入失败原因");
        return headTitleList;
    }

    /**
     * 根据机构和角色类型获取用户
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
     * 获取所有组织组
     */
    private List<OrganizationRespDTO> getGroupList(Long parentId, Integer type) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setParentId(parentId);
        queryDTO.setOrgType(type);
        // 查询所有组织
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> data = queryOrgByParam.getData();
        return data;
    }

    /**
     * 查询系统参数
     */
    private String getSysSetting(String code) {
        SysSettingReq sysSettingReq = new SysSettingReq();
        sysSettingReq.setCode(code);
        JSONResult<SysSettingDTO> byCode = sysSettingFeignClient.getByCode(sysSettingReq);
        if (byCode != null && JSONResult.SUCCESS.equals(byCode.getCode())) {
            return byCode.getData().getValue();
        }
        return null;
    }

    /**
     * 优化非优化系统参数字符串转list
     */
    private List<String> stringToList(String setting) {
        List<String> categoryList = new ArrayList<String>();
        if (StringUtils.isNotBlank(setting)) {
            String[] split = setting.split(",");
            for (String string : split) {
                categoryList.add(string);
            }
        }
        return categoryList;
    }
}
