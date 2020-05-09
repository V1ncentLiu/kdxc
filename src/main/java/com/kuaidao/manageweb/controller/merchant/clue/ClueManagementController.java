package com.kuaidao.manageweb.controller.merchant.clue;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.omg.CORBA.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.common.util.SortUtils;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.merchant.clue.ClueManagementFeignClient;
import com.kuaidao.manageweb.feign.merchant.publiccustomer.PubcustomerFeignClient;
import com.kuaidao.manageweb.feign.merchant.rule.RuleAssignRecordFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.merchant.constant.MerchantConstant;
import com.kuaidao.merchant.dto.clue.ClueAssignReqDto;
import com.kuaidao.merchant.dto.clue.ClueManagementDto;
import com.kuaidao.merchant.dto.clue.ClueManagementParamDto;
import com.kuaidao.merchant.dto.clue.ResourceStatisticsDto;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源管理
 *
 * @author:fanjd
 * @date:2019/9/10
 * @since 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/clue/management")
public class ClueManagementController {
    @Autowired
    private ClueManagementFeignClient clueManagementFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    @Autowired
    private PubcustomerFeignClient pubcustomerFeignClient;
    @Autowired
    private RuleAssignRecordFeignClient ruleAssignRecordFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;

    /**
     * 资源管理页面初始化
     *
     * @param
     * @return
     */
    @RequestMapping("/init")
    public String init(HttpServletRequest request) {
        // 获取当前登录人
        UserInfoDTO user = getUser();
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(Constants.MEDIUM));
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
        UserInfoDTO userInfoDTO = buildQueryReqDto(SysConstant.USER_TYPE_THREE, user.getId());
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        if (merchantUserList.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("merchantUserList", merchantUserList.getData());
        }
        // 查询用户集合（邀约使用）
        UserInfoDTO userInfoInvite = new UserInfoDTO();
        if (SysConstant.USER_TYPE_TWO.equals(user.getUserType())) {
            userInfoInvite = buildQueryReqDto(SysConstant.USER_TYPE_THREE, user.getId());
        } else if (SysConstant.USER_TYPE_THREE.equals(user.getUserType())) {
            userInfoInvite = buildQueryReqDto(SysConstant.USER_TYPE_THREE, user.getParentId());
        }
        JSONResult<List<UserInfoDTO>> merchantAppiontUserList = merchantUserInfoFeignClient.merchantUserList(userInfoInvite);
        if (merchantAppiontUserList.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("merchantAppiontUserList", merchantAppiontUserList.getData());
        }
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList", getDictionaryByCode(Constants.INDUSTRY_CATEGORY));
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            List<ProjectInfoDTO> result = SortUtils.sortList(proJson.getData(), "projectName");
            request.setAttribute("proSelect", result);
        } else {
            request.setAttribute("proSelect", new ArrayList());
        }
        return "merchant/resourceManagement/resourceManagement";
    }


    /**
     * 查询资源列表
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/queryPage")
    public JSONResult<PageBean<ClueManagementDto>> queryPage(@RequestBody ClueManagementParamDto pageParam) {
        UserInfoDTO userInfoDTO = getUser();
        // 用户类型
        pageParam.setUserType(userInfoDTO.getUserType());
        // 用户id
        pageParam.setUserId(userInfoDTO.getId());
        List<Long> userList = new ArrayList<>();
        // 商家主账户能看商家子账号所有的记录
        if (SysConstant.USER_TYPE_TWO.equals(userInfoDTO.getUserType())) {
            getSubAccountIds(userList, userInfoDTO.getId());
        }
        // 商家子账号看自己和主账号的记录
        if (SysConstant.USER_TYPE_THREE.equals(userInfoDTO.getUserType())) {
            userList.add(userInfoDTO.getId());
            userList.add(userInfoDTO.getParentId());
        }
        pageParam.setUserList(userList);
        return clueManagementFeignClient.queryPage(pageParam);
    }

    /**
     * 资源分配
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequiresPermissions("clue:management:allocation")
    @PostMapping("/clueAssign")
    public JSONResult<String> clueAssign(@RequestBody ClueAssignReqDto reqDto) {
        UserInfoDTO userInfoDTO = getUser();
        reqDto.setAllotUserId(userInfoDTO.getId());
        return clueManagementFeignClient.clueAssign(reqDto);
    }

    /**
     * 获取资源统计
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/getResourceStatistics")
    public JSONResult<ResourceStatisticsDto> getResourceStatistics(@RequestBody IdEntityLong req) {
        ResourceStatisticsDto dto = new ResourceStatisticsDto();
        // 获取当前登录信息
        UserInfoDTO userInfoDTO = getUser();
        // 子账号集合
        List<Long> subIds = new ArrayList<>();
        JSONResult<ResourceStatisticsDto> assignDto = null;
        // 查询主账号信息
        if (SysConstant.USER_TYPE_TWO.equals(userInfoDTO.getUserType())) {
            // 获取主账号分发相关
            IdEntityLong reqDto = new IdEntityLong();
            reqDto.setId(userInfoDTO.getId());
            assignDto = ruleAssignRecordFeignClient.countAssginNum(reqDto);
            // 获取商家主账号下的子账号列表
            UserInfoDTO userReqDto = buildQueryReqDto(SysConstant.USER_TYPE_THREE, userInfoDTO.getId());
            JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userReqDto);
            if (merchantUserList.getCode().equals(JSONResult.SUCCESS)) {
                if (CollectionUtils.isNotEmpty(merchantUserList.getData())) {
                    // 获取子账号id放入子账号集合中
                    subIds.addAll(merchantUserList.getData().stream().map(UserInfoDTO::getId).collect(Collectors.toList()));
                }
            }
        }

        // 查询子账号信息
        if (SysConstant.USER_TYPE_THREE.equals(userInfoDTO.getUserType())) {
            IdEntityLong reqDto = new IdEntityLong();
            reqDto.setId(userInfoDTO.getId());
            // 获取子账号分发相关
            assignDto = clueManagementFeignClient.getAssignResourceStatistics(reqDto);
            // 子账号id
            subIds.add(userInfoDTO.getId());
        }
        // 获取分发
        if (null != assignDto && assignDto.getCode().equals(JSONResult.SUCCESS)) {
            // 今日分发资源
            dto.setTodayAssignClueNum(assignDto.getData().getTodayAssignClueNum());
            // 累计分发
            dto.setTotalAssignClueNum(assignDto.getData().getTotalAssignClueNum());
        }

        // 获取领取相关
        if (CollectionUtils.isNotEmpty(subIds)) {
            IdListLongReq ids = new IdListLongReq();
            ids.setIdList(subIds);
            // 主账号也需要将自己领取的查出来
            if (SysConstant.USER_TYPE_TWO.equals(userInfoDTO.getUserType())) {
                subIds.add(userInfoDTO.getId());
            }
            JSONResult<ResourceStatisticsDto> receiveResourceList = pubcustomerFeignClient.getReceiveResourceStatistics(ids);
            if (receiveResourceList.getCode().equals(JSONResult.SUCCESS)) {
                ResourceStatisticsDto receiveResource = receiveResourceList.getData();
                // 今日领取资源
                dto.setTodayReceiveClueNum(receiveResource.getTodayReceiveClueNum());
                // 累计领取资源
                dto.setTotalReceiveClueNum(receiveResource.getTotalReceiveClueNum());
            }
        }

        return new JSONResult<ResourceStatisticsDto>().success(dto);
    }

    /**
     * 资源管理-导出
     *
     * @param
     * @return
     */
    @PostMapping("/export")
    @RequiresPermissions("clue:management:export")
    @LogRecord(description = "导出", operationType = LogRecord.OperationType.EXPORT, menuName = MenuEnum.CLUE_MANAGEMENT)
    public void export(@RequestBody ClueManagementParamDto reqDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long start = System.currentTimeMillis();
        log.info("clueManagement start,param{}", reqDto);
        UserInfoDTO userInfoDTO = getUser();
        // 用户类型
        reqDto.setUserType(userInfoDTO.getUserType());
        // 用户id
        reqDto.setUserId(userInfoDTO.getId());
        JSONResult<List<ClueManagementDto>> listNoPage = clueManagementFeignClient.listNoPage(reqDto);
        List<List<Object>> dataList = new ArrayList<>();
        dataList.add(getHeadTitleList());
        if (JSONResult.SUCCESS.equals(listNoPage.getCode()) && listNoPage.getData() != null && listNoPage.getData().size() != 0) {
            List<ClueManagementDto> resultList = listNoPage.getData();
            int size = resultList.size();
            for (int i = 0; i < size; i++) {
                ClueManagementDto dto = resultList.get(i);
                List<Object> curList = new ArrayList<>();
                // 序号
                curList.add(i + 1);
                // 媒介
                curList.add(dto.getSourceName());
                // 资源项目
                curList.add(dto.getProjectName());
                // 客户姓名
                curList.add(dto.getCusName());
                // 搜索词
                curList.add(dto.getSearchWord());
                // 留言时间
                curList.add(getTimeStr(dto.getMessageTime()));
                // 留言内容
                curList.add(dto.getMessagePoint());
                // 联系电话
                curList.add(dto.getPhone());
                // 资源区域
                curList.add(dto.getAddress());
                // 获取时间
                curList.add(getTimeStr(dto.getReceiveTime()));
                // 价格(元)/条
                curList.add(dto.getCluePrice());
                // 是否分发
                String str = MerchantConstant.ASSIGN_SUB_ACCOUNT_YES.equals(dto.getIsAssignSubAccount()) ? "是" : "否";
                //子账号导出  是否分发 为空
                if (SysConstant.USER_TYPE_THREE.equals(userInfoDTO.getUserType())) {
                    str = "";
                }
                // 公有池领取并且是否分发为否 此字段为空
                if (MerchantConstant.CPOOLRECEIVE_FLAG_YES.equals(dto.getCpoolReceiveFlag())&& MerchantConstant.ASSIGN_SUB_ACCOUNT_NO.equals(dto.getIsAssignSubAccount())) {
                    str = "";
                }
                curList.add(str);
                dataList.add(curList);
            }
        } else {
            log.error("export rule_report res{{}}", listNoPage);
        }
        // 创建一个工作薄
        XSSFWorkbook workBook = new XSSFWorkbook();
        // 创建一个工作薄对象sheet
        XSSFSheet sheet = workBook.createSheet();
        // 设置宽度
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 8000);
        sheet.setColumnWidth(6, 8000);
        sheet.setColumnWidth(7, 10000);
        sheet.setColumnWidth(8, 4000);
        sheet.setColumnWidth(9, 8000);
        sheet.setColumnWidth(10, 8000);
        sheet.setColumnWidth(11, 4000);

        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007ExcelWorkbook(workBook, dataList);
        String name = "资源列表" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
        Long end = System.currentTimeMillis();
        Long spend = end - start;
        log.info("clueManagement export success,spend:{} ", spend);
    }


    /**
     * 设置EXCEL表头
     * 
     * @return
     */
    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("序号");
        headTitleList.add("媒介");
        headTitleList.add("资源项目");
        headTitleList.add("客户姓名");
        headTitleList.add("搜索词");
        headTitleList.add("留言时间");
        headTitleList.add("留言内容");
        headTitleList.add("联系电话");
        headTitleList.add("资源地域");
        headTitleList.add("获取时间");
        headTitleList.add("价格(元)/条");
        headTitleList.add("是否分发");
        return headTitleList;
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
     * 转换时间
     * 
     * @author: Fanjd
     * @param date 日期
     * @return: java.lang.String
     * @Date: 2019/9/11 10:57
     * @since: 1.0.0
     **/
    private String getTimeStr(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.convert2String(date, DateUtil.ymdhms);
    }

    /**
     * 查询字典表
     *
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode = dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }

    /**
     * 构建商家子账户查询实体
     *
     * @return
     */
    private UserInfoDTO buildQueryReqDto(Integer userType, Long id) {
        // 获取商家主账号下的子账号列表
        UserInfoDTO userReqDto = new UserInfoDTO();
        // 商家主账户
        userReqDto.setUserType(userType);
        // 启用和锁定
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        userReqDto.setStatusList(statusList);
        // 商家主账号id
        userReqDto.setParentId(id);
        return userReqDto;
    }
    /**
     * 获取商家主账户下的子账号
     *
     * @author: Fanjd
     * @param subIds 用户集 合userId 用户id
     * @return: void
     * @Date: 2019/10/10 20:30
     * @since: 1.0.0
     **/
    private void getSubAccountIds(List<Long> subIds, Long userId) {
        subIds.add(userId);
        // 获取商家主账号下的子账号列表
        UserInfoDTO userReqDto = buildQueryReqDto(SysConstant.USER_TYPE_THREE, userId);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userReqDto);
        if (merchantUserList.getCode().equals(JSONResult.SUCCESS)) {
            if (CollectionUtils.isNotEmpty(merchantUserList.getData())) {
                // 获取子账号id放入子账号集合中
                subIds.addAll(merchantUserList.getData().stream().map(UserInfoDTO::getId).collect(Collectors.toList()));
            }
        }

    }


}
