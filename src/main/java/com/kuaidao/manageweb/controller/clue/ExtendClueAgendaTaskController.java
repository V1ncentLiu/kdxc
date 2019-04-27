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
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
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
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
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

    @Value("${oss.url.directUpload}")
    private String ossUrl;

    /**
     * 初始化待审核列表
     * 
     * @param request
     * @param model
     * @return
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

        List<UserInfoDTO> userList = this.queryUserByRole();
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
     * 
     * @param request
     * @param model
     * @return
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

        request.setAttribute("ossUrl", ossUrl);
        return "clue/addCluePage";
    }

    /**
     * 跳转编辑资源
     * 
     * @param request
     * @param model
     * @return
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
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",
                getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(DicCodeEnum.CLUETYPE.getCode()));
        // 查询字典广告位集合
        request.setAttribute("adsenseList", getDictionaryByCode(DicCodeEnum.ADENSE.getCode()));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        request.setAttribute("ossUrl", ossUrl);
        return "clue/updateCluePage";
    }

    /**
     * 新建资源
     * 
     * @param request
     * @param clueId
     * @return
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
        JSONResult<String> clueInfo = extendClueFeignClient.createClue(pushClueReq);

        return clueInfo;
    }

    /**
     * 编辑资源
     * 
     * @param request
     * @param clueId
     * @return
     */
    @RequestMapping("/updateClue")
    @RequiresPermissions("waitDistributResource:edit")
    @ResponseBody
    @LogRecord(description = "编辑资源", operationType = OperationType.UPDATE,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<String> updateClue(HttpServletRequest request,
            @RequestBody PushClueReq pushClueReq) {

        JSONResult<String> clueInfo = extendClueFeignClient.createClue(pushClueReq);

        return clueInfo;
    }

    @RequestMapping("/queryPageAgendaTask")
    @RequiresPermissions("waitDistributResource:view")
    @ResponseBody
    public JSONResult<PageBean<ClueAgendaTaskDTO>> queryPageAgendaTask(HttpServletRequest request,
            @RequestBody ClueAgendaTaskQueryDTO queryDto) {

        return extendClueFeignClient.queryPageAgendaTask(queryDto);

    }

    /**
     * 撤回资源
     * 
     * @param request
     * @param clueId
     * @return
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
     * 查询所有资源专员
     * 
     * @param request
     * @return
     */

    private List<UserInfoDTO> queryUserByRole() {

        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();

        UserOrgRoleReq userRole = new UserOrgRoleReq();
        userRole.setRoleCode(RoleCodeEnum.TGZXZJ.name());
        JSONResult<List<UserInfoDTO>> userZxzjList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userZxzjList.getCode()) && null != userZxzjList.getData()
                && userZxzjList.getData().size() > 0) {
            userList.addAll(userZxzjList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.YHZG.name());
        JSONResult<List<UserInfoDTO>> userYhZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userYhZgList.getCode()) && null != userYhZgList.getData()
                && userYhZgList.getData().size() > 0) {
            userList.addAll(userYhZgList.getData());
        }
        userRole.setRoleCode(RoleCodeEnum.TGYHWY.name());
        JSONResult<List<UserInfoDTO>> userYhWyList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userYhWyList.getCode()) && null != userYhWyList.getData()
                && userYhWyList.getData().size() > 0) {
            userList.addAll(userYhWyList.getData());
        }
        userRole.setRoleCode(RoleCodeEnum.TGKF.name());
        JSONResult<List<UserInfoDTO>> userKfList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKfList.getCode()) && null != userKfList.getData()
                && userKfList.getData().size() > 0) {
            userList.addAll(userKfList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.KFZG.name());
        JSONResult<List<UserInfoDTO>> userKfZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKfZgList.getCode()) && null != userKfZgList.getData()
                && userKfZgList.getData().size() > 0) {
            userList.addAll(userKfZgList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.TGNQWY.name());
        JSONResult<List<UserInfoDTO>> userKNqWyList =
                userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userKNqWyList.getCode()) && null != userKNqWyList.getData()
                && userKNqWyList.getData().size() > 0) {
            userList.addAll(userKNqWyList.getData());
        }

        userRole.setRoleCode(RoleCodeEnum.NQZG.name());
        JSONResult<List<UserInfoDTO>> userNqZgList = userInfoFeignClient.listByOrgAndRole(userRole);

        if (JSONResult.SUCCESS.equals(userNqZgList.getCode()) && null != userNqZgList.getData()
                && userNqZgList.getData().size() > 0) {
            userList.addAll(userNqZgList.getData());
        }

        return userList;

    }

    /**
     * 客户详情
     * 
     * @param request
     * @param clueId
     * @return
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
     * 
     * @param request
     * @param clueId
     * @return
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
     * 
     * @param orgDTO
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
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
     * 预览
     *
     * @param result
     * @return
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
        if (excelDataList.size() > 10000) {
            logger.error("上传自定义字段,大于10000条，条数{{}}", excelDataList.size());
            return new JSONResult<>().fail(SysErrorCodeEnum.ERR_EXCLE_OUT_SIZE.getCode(),
                    "导入数据过多，已超过10000条！");
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
                } else if (j == 1) {// 资源类型
                    rowDto.setTypeName(value);
                } else if (j == 2) {// 资源类别
                    rowDto.setCategoryName(value);
                } else if (j == 3) {// 广告位
                    rowDto.setSourceTypeName(value);
                } else if (j == 4) {// 媒介
                    rowDto.setSourceName(value);
                } else if (j == 5) {// 资源项目
                    rowDto.setProjectName(value);
                } else if (j == 6) {// 姓名
                    rowDto.setCusName(value);
                } else if (j == 7) {// 手机
                    rowDto.setPhone(value);
                } else if (j == 8) {// 手机2
                    rowDto.setPhone2(value);
                } else if (j == 9) {// 微信
                    rowDto.setWechat(value);
                } else if (j == 10) {// 微信2
                    rowDto.setWechat2(value);
                } else if (j == 11) {// QQ
                    rowDto.setQq(value);
                } else if (j == 12) {// 邮箱
                    rowDto.setEmail(value);
                } else if (j == 13) {// 性别
                    rowDto.setSex1(value);
                } else if (j == 14) {// 年龄
                    rowDto.setAge1(value);
                } else if (j == 15) {// 地址
                    rowDto.setAddress(value);
                } else if (j == 16) {// 留言时间
                    rowDto.setMessageTime1(value);
                } else if (j == 17) {// 留言内容
                    rowDto.setMessagePoint(value);
                } else if (j == 18) {// 搜索词
                    rowDto.setSearchWord(value);
                } else if (j == 19) {// 预约时间
                    rowDto.setReserveTime1(value);
                }
            } // inner foreach end
            dataList.add(rowDto);
        } // outer foreach end
        logger.info("upload custom filed, valid success num{{}}", dataList.size());
        /*
         * JSONResult uploadRs = customFieldFeignClient.saveBatchCustomField(dataList);
         * if(uploadRs==null || !JSONResult.SUCCESS.equals(uploadRs.getCode())) { return uploadRs; }
         */

        return new JSONResult<>().success(dataList);
    }


    /**
     * 导入线索
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/importInvitearea")
    @LogRecord(description = "导入线索", operationType = LogRecord.OperationType.IMPORTS,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    @ResponseBody
    public JSONResult importInvitearea(@RequestBody ClueAgendaTaskDTO clueAgendaTaskDTO)
            throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 存放合法的数据
        List<ClueAgendaTaskDTO> dataList = new ArrayList<ClueAgendaTaskDTO>();
        // 存放非法的数据
        List<ClueAgendaTaskDTO> illegalDataList = new ArrayList<ClueAgendaTaskDTO>();
        // 查询项目列表
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        List<ClueAgendaTaskDTO> list = clueAgendaTaskDTO.getList();
        List<PushClueReq> list1 = new ArrayList<PushClueReq>();
        PushClueReq pushClueReq = new PushClueReq();

        // 匹配字典数据
        // 资源类型
        Map<String, String> typeMap =
                dicMap(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.CLUETYPE.getCode()));
        // 资源类别
        Map<String, String> categoryMap = dicMap(
                itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 广告位
        Map<String, String> sourceTypeMap =
                dicMap(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.ADENSE.getCode()));
        // 媒介
        Map<String, String> sourceMap =
                dicMap(itemFeignClient.queryDicItemsByGroupCode(DicCodeEnum.MEDIUM.getCode()));

        if (list != null && list.size() > 0) {

            for (ClueAgendaTaskDTO clueAgendaTaskDTO1 : list) {
                boolean islegal = true;// true合法 false不合法
                if (islegal && clueAgendaTaskDTO1.getProjectName() != null
                        && !"".equals(clueAgendaTaskDTO1.getProjectName())) {
                    for (ProjectInfoDTO projectInfoDTO : allProject.getData()) {
                        if ((projectInfoDTO.getProjectName().trim())
                                .equals(clueAgendaTaskDTO1.getProjectName())) {
                            clueAgendaTaskDTO1.setProjectId(projectInfoDTO.getId());
                            break;
                        }
                    }
                    if (clueAgendaTaskDTO1.getProjectId() == null) {
                        islegal = false;
                    }
                } else {
                    islegal = false;
                }
                // 判断时间格式是否正确,如果正确则添加秒
                if (islegal && (clueAgendaTaskDTO1.getReserveTime1() != null
                        && !"".equals(clueAgendaTaskDTO1.getReserveTime1()))) {
                    try {
                        Date date = format.parse(clueAgendaTaskDTO1.getReserveTime1());
                    } catch (ParseException e) {
                        islegal = false;
                    }
                    // if(islegal){
                    // clueAgendaTaskDTO1.setReserveTime1(clueAgendaTaskDTO1.getReserveTime1() +
                    // ":00");
                    // }
                }
                if (islegal && (clueAgendaTaskDTO1.getDate() != null
                        && !"".equals(clueAgendaTaskDTO1.getDate()))) {
                    try {
                        Date date = format.parse(clueAgendaTaskDTO1.getDate());
                    } catch (ParseException e) {
                        islegal = false;
                    }
                    // if(islegal){
                    // clueAgendaTaskDTO1.setDate(clueAgendaTaskDTO1.getDate() + ":00");
                    // }
                }
                if (islegal && (clueAgendaTaskDTO1.getMessageTime1() != null
                        && !"".equals(clueAgendaTaskDTO1.getMessageTime1()))) {
                    try {
                        Date date = format.parse(clueAgendaTaskDTO1.getMessageTime1());
                    } catch (ParseException e) {
                        islegal = false;
                    }
                    // if(islegal){
                    // clueAgendaTaskDTO1.setMessageTime1(clueAgendaTaskDTO1.getMessageTime1() +
                    // ":00");
                    // }
                }
                // 判断必填项
                if (islegal && (clueAgendaTaskDTO1.getTypeName() == null
                        || "".equals(clueAgendaTaskDTO1.getTypeName())
                        || "".equals(clueAgendaTaskDTO1.getCategoryName())
                        || clueAgendaTaskDTO1.getCategoryName() == null
                        || clueAgendaTaskDTO1.getSourceTypeName() == null
                        || "".equals(clueAgendaTaskDTO1.getSourceTypeName())
                        || clueAgendaTaskDTO1.getTypeName() == null
                        || "".equals(clueAgendaTaskDTO1.getTypeName())
                        || clueAgendaTaskDTO1.getCusName() == null
                        || "".equals(clueAgendaTaskDTO1.getCusName()))) {
                    islegal = false;
                }
                // 判断字典表数据是否匹配
                if (islegal && (clueAgendaTaskDTO1.getTypeName() != null
                        && !"".equals(clueAgendaTaskDTO1.getTypeName()))) {
                    clueAgendaTaskDTO1.setType(
                            Integer.valueOf(typeMap.get(clueAgendaTaskDTO1.getTypeName())));
                    if (clueAgendaTaskDTO1.getType() == null) {
                        islegal = false;
                    }
                }
                if (islegal && (clueAgendaTaskDTO1.getCategoryName() != null
                        && !"".equals(clueAgendaTaskDTO1.getCategoryName()))) {
                    clueAgendaTaskDTO1.setCategory(
                            Integer.valueOf(categoryMap.get(clueAgendaTaskDTO1.getCategoryName())));
                    if (clueAgendaTaskDTO1.getCategory() == null) {
                        islegal = false;
                    }
                }
                if (islegal && (clueAgendaTaskDTO1.getSourceTypeName() != null
                        && !"".equals(clueAgendaTaskDTO1.getSourceTypeName()))) {
                    clueAgendaTaskDTO1.setSourceType(Integer
                            .valueOf(sourceTypeMap.get(clueAgendaTaskDTO1.getSourceTypeName())));
                    if (clueAgendaTaskDTO1.getSourceType() == null) {
                        islegal = false;
                    }
                }
                if (islegal && (clueAgendaTaskDTO1.getSourceName() != null
                        && !"".equals(clueAgendaTaskDTO1.getSourceName()))) {
                    clueAgendaTaskDTO1.setSource(
                            Integer.valueOf(sourceMap.get(clueAgendaTaskDTO1.getSourceName())));
                    if (clueAgendaTaskDTO1.getSource() == null) {
                        islegal = false;
                    }
                }
                // 判断性别
                if (islegal && (clueAgendaTaskDTO1.getSex1() != null
                        && !"".equals(clueAgendaTaskDTO1.getSex1()))) {
                    if (clueAgendaTaskDTO1.getSex1().equals("男")) {
                        clueAgendaTaskDTO1.setSex(1);
                    } else if (clueAgendaTaskDTO1.getSex1().equals("女")) {
                        clueAgendaTaskDTO1.setSex(2);
                    } else {
                        islegal = false;
                    }
                }

                if (islegal) {
                    pushClueReq.setCategory(String.valueOf(clueAgendaTaskDTO1.getCategory()));
                    pushClueReq.setCusName(clueAgendaTaskDTO1.getCusName());
                    pushClueReq.setSex(clueAgendaTaskDTO1.getSex());
                    pushClueReq.setPhone(clueAgendaTaskDTO1.getPhone());
                    pushClueReq.setPhone2(clueAgendaTaskDTO1.getPhone2());
                    pushClueReq.setWechat(clueAgendaTaskDTO1.getWechat());
                    pushClueReq.setWechat2(clueAgendaTaskDTO1.getWechat2());
                    pushClueReq.setQq(clueAgendaTaskDTO1.getQq());
                    pushClueReq.setEmail(clueAgendaTaskDTO1.getEmail());
                    pushClueReq.setProvince(clueAgendaTaskDTO1.getAddress());
                    pushClueReq.setSearchWord(clueAgendaTaskDTO1.getSearchWord());
                    pushClueReq.setSource(String.valueOf(clueAgendaTaskDTO1.getSource()));
                    pushClueReq.setSourceType(String.valueOf(clueAgendaTaskDTO1.getSourceType()));
                    pushClueReq.setType(String.valueOf(clueAgendaTaskDTO1.getType()));
                    pushClueReq.setMessagePoint(clueAgendaTaskDTO1.getMessagePoint());
                    pushClueReq.setMessageTime(clueAgendaTaskDTO1.getMessageTime1());
                    pushClueReq.setAppointTime(clueAgendaTaskDTO1.getReserveTime1());
                    pushClueReq.setCreateTime(clueAgendaTaskDTO1.getDate());
                    list1.add(pushClueReq);
                } else {
                    illegalDataList.add(clueAgendaTaskDTO1);
                }
            }
        }
        if (dataList != null && dataList.size() > 0) {
            JSONResult jsonResult = extendClueFeignClient.importclue(list1);
            if (!jsonResult.getCode().equals("0")) {
                return new JSONResult<>().success(list);
            }
        }
        return new JSONResult<>().success(illegalDataList);
    }

    /**
     * 数据字典-词条转换Map
     *
     * @return
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
     * 导出
     *
     * @param
     * @return
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
                curList.add(clueAgendaTaskDTO.getTypeName());
                curList.add(clueAgendaTaskDTO.getCategoryName());
                curList.add(clueAgendaTaskDTO.getSourceTypeName());
                curList.add(clueAgendaTaskDTO.getSource());
                curList.add(clueAgendaTaskDTO.getProjectName());
                curList.add(clueAgendaTaskDTO.getCusName());
                curList.add(clueAgendaTaskDTO.getPhone());
                curList.add(clueAgendaTaskDTO.getPhone2());
                curList.add(clueAgendaTaskDTO.getWechat());
                curList.add(clueAgendaTaskDTO.getWechat2());
                curList.add(clueAgendaTaskDTO.getQq());
                curList.add(clueAgendaTaskDTO.getEmail());
                curList.add(clueAgendaTaskDTO.getSex1());
                curList.add(clueAgendaTaskDTO.getAge1());
                curList.add(clueAgendaTaskDTO.getAddress());
                curList.add(clueAgendaTaskDTO.getMessageTime1());
                curList.add(clueAgendaTaskDTO.getMessagePoint());
                curList.add(clueAgendaTaskDTO.getSearchWord());
                curList.add(clueAgendaTaskDTO.getReserveTime1());
                dataList.add(curList);
            }

        } else {
            logger.error("export trucking_order param{{}},res{{}}", dto, list);
        }

        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel(dataList);


        String name = DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();

    }


    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("日期");
        headTitleList.add("资源类型");
        headTitleList.add("资源类别");
        headTitleList.add("广告位");
        headTitleList.add("媒介");
        headTitleList.add("资源项目");
        headTitleList.add("姓名");
        headTitleList.add("手机");
        headTitleList.add("手机2");
        headTitleList.add("微信");
        headTitleList.add("微信2");
        headTitleList.add("QQ");
        headTitleList.add("邮箱");
        headTitleList.add("性别");
        headTitleList.add("年龄");
        headTitleList.add("地址");
        headTitleList.add("留言时间");
        headTitleList.add("留言内容");
        headTitleList.add("搜索词");
        headTitleList.add("预约时间");
        return headTitleList;
    }
}
