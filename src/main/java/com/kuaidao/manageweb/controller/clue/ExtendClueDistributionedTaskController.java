package com.kuaidao.manageweb.controller.clue;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kuaidao.common.constant.*;
import com.kuaidao.common.entity.*;
import com.kuaidao.manageweb.feign.organization.OrganitionWapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.kuaidao.aggregation.constant.AggregationConstant;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueDistributionedTaskDTO;
import com.kuaidao.aggregation.dto.clue.ClueDistributionedTaskQueryDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.PushClueReq;
import com.kuaidao.businessconfig.constant.BusinessConfigConstant;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.util.DateUtil;
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
@RequestMapping("/exetend/distributionedTaskManager")
public class ExtendClueDistributionedTaskController {
    @Autowired
    private ExtendClueFeignClient extendClueFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private OrganitionWapper organitionWapper;

    @Autowired
    private MyCustomerFeignClient myCustomerFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private SysSettingFeignClient sysSettingFeignClient;
    @Value("${oss.url.directUpload}")
    private String ossUrl;

    /**
     * 初始化已审核列表数据
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/initDistributiveResource")
    public String initDistributiveResource(HttpServletRequest request, Model model) {
        UserInfoDTO user = getUser();
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        }

        List<UserInfoDTO> userList = this.queryUserByRole(user);

        request.setAttribute("userList", userList);
        // 话务员集合
        List<UserInfoDTO> hwzyUserList = queryUserByRole(RoleCodeEnum.HWY.name());
        request.setAttribute("hwzyUserList", hwzyUserList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("DistributiveResource");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu = customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("DistributiveResource");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu = customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());

        List<OrganizationRespDTO> allDXZ = organitionWapper.findAllDXZ();
        List<OrganizationRespDTO> allHWZ = organitionWapper.findAllHWZ();
        List<OrganizationRespDTO> orgs = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(allDXZ)){
            orgs.addAll(allDXZ);
        }
        if(CollectionUtils.isNotEmpty(allHWZ)){
            orgs.addAll(allHWZ);
        }
        request.setAttribute("orgs",orgs);

        return "clue/distributiveResource";
    }

    @RequestMapping("/queryPageDistributionedTask")
    @ResponseBody
    public JSONResult<PageBean<ClueDistributionedTaskDTO>> queryPageDistributionedTask(HttpServletRequest request,
            @RequestBody ClueDistributionedTaskQueryDTO queryDto) {
        UserInfoDTO user = getUser();
        queryDto.setPromotionCompany(user.getPromotionCompany());
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        List<Long> idList = new ArrayList<Long>();
        // 处理数据权限，客户经理、客户主管、客户专员；内勤经理、内勤主管、内勤专员；优化经理、优化主管、优化文员
        if (RoleCodeEnum.KFZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.NQWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())) {
            // 推广客服、内勤文员 能看自己的数据
            idList.add(user.getId());
        } else if (RoleCodeEnum.KFZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHZZ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YYZJ.name().equals(roleInfoDTO.getRoleCode())) {
            // 客服主管、内勤主管 能看自己组员数据
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.KFJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.TGFZC.name().equals(roleInfoDTO.getRoleCode())) {
            // 内勤经理 能看下属组的数据
            List<OrganizationRespDTO> groupList = getGroupList(user.getOrgId(), null);
            for (OrganizationRespDTO organizationRespDTO : groupList) {
                List<UserInfoDTO> userList = getUserList(organizationRespDTO.getId(), null, null);
                for (UserInfoDTO userInfoDTO : userList) {
                    idList.add(userInfoDTO.getId());
                }
            }
            // 自己组织内的人
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.GLY.name().equals(roleInfoDTO.getRoleCode())) {
            idList = null;
        } else {
            return new JSONResult<PageBean<ClueDistributionedTaskDTO>>().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }
        if (RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            queryDto.setShowTrafficClue(true);
        }
        if (RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())) {
            queryDto.setOperatorIdList(idList);
        }
        queryDto.setResourceDirectorList(idList);
        queryDto.setUserDataAuthList(user.getUserDataAuthList());
        JSONResult<PageBean<ClueDistributionedTaskDTO>> pageBeanJSONResult = extendClueFeignClient.queryPageDistributionedTask(queryDto);
        return pageBeanJSONResult;
    }

    /**
     * 跳转编辑资源
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/toUpdatePage")
    public String toUpdatePage(@RequestParam long id, HttpServletRequest request, Model model) {
        UserInfoDTO user = getUser();
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        ClueQueryDTO queryDTO = new ClueQueryDTO();

        queryDTO.setClueId(id);

        JSONResult<ClueDTO> clueInfo = myCustomerFeignClient.findClueInfo(queryDTO);

        request.setAttribute("clueInfo", clueInfo.getData());

        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询字典资源类别集合
        request.setAttribute("clueCategoryList", getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(DicCodeEnum.CLUETYPE.getCode()));
        // 查询字典广告位集合
        request.setAttribute("adsenseList", getDictionaryByCode(DicCodeEnum.ADENSE.getCode()));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList", getDictionaryByCode(DicCodeEnum.INDUSTRYCATEGORY.getCode()));
        // 查询字典账户名称集合
        request.setAttribute("accountNameList", getDictionaryByCode(DicCodeEnum.ACCOUNT_NAME.getCode()));
        request.setAttribute("ossUrl", ossUrl);
        // 系统参数优化资源类别
        String optList = getSysSetting(SysConstant.OPT_CATEGORY);
        request.setAttribute("optList", optList);
        // 系统参数非优化资源类别
        String notOptList = getSysSetting(SysConstant.NOPT_CATEGORY);
        request.setAttribute("notOptList", notOptList);
        request.setAttribute("roleCode", roleInfoDTO.getRoleCode());
        return "clue/distributedUpdateClue";
    }

    /**
     * 编辑资源
     * @param request
     * @return
     */
    @RequestMapping("/distributedUpdateClue")
    @ResponseBody
    @LogRecord(description = "编辑资源", operationType = OperationType.UPDATE, menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<String> updateClue(HttpServletRequest request, @RequestBody PushClueReq pushClueReq) {
        UserInfoDTO user = getUser();
        pushClueReq.setUpdateUser(user.getId());
        JSONResult<String> clueInfo = extendClueFeignClient.distributedUpdateClue(pushClueReq);

        return clueInfo;
    }

    /**
     * 导出资源情况
     */
    // @RequiresPermissions("aggregation:truckingOrder:export")
    @LogRecord(description = "导出资源情况", operationType = OperationType.EXPORT, menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    @PostMapping("/findClues")
    public void findClues(HttpServletRequest request, HttpServletResponse response, @RequestBody ClueDistributionedTaskQueryDTO queryDto)
            throws Exception {
        UserInfoDTO user = getUser();
        queryDto.setPromotionCompany(user.getPromotionCompany());
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        List<Long> idList = new ArrayList<Long>();
        // 处理数据权限，客户经理、客户主管、客户专员；内勤经理、内勤主管、内勤专员；优化经理、优化主管、优化文员
        if (RoleCodeEnum.KFZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.NQWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())) {
            // 推广客服、内勤文员 能看自己的数据
            idList.add(user.getId());
        } else if (RoleCodeEnum.KFZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHZZ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            // 客服主管、内勤主管 能看自己组员数据
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.KFJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.TGFZC.name().equals(roleInfoDTO.getRoleCode())) {
            // 内勤经理 能看下属组的数据
            List<OrganizationRespDTO> groupList = getGroupList(user.getOrgId(), null);
            for (OrganizationRespDTO organizationRespDTO : groupList) {
                List<UserInfoDTO> userList = getUserList(organizationRespDTO.getId(), null, null);
                for (UserInfoDTO userInfoDTO : userList) {
                    idList.add(userInfoDTO.getId());
                }
            }
            // 自己组织内的人
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.GLY.name().equals(roleInfoDTO.getRoleCode())) {
            idList = null;
        }
        if (RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            queryDto.setShowTrafficClue(true);
        }
        if (RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())) {
            queryDto.setOperatorIdList(idList);
        }
        queryDto.setResourceDirectorList(idList);
        queryDto.setUserDataAuthList(user.getUserDataAuthList());
        JSONResult<List<ClueDistributionedTaskDTO>> listJSONResult = extendClueFeignClient.findClues(queryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        // dataList.add(getHeadTitleList());
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode()) && listJSONResult.getData() != null && listJSONResult.getData().size() != 0) {
            List<DictionaryItemRespDTO> dictionaryItemRespDTOs = getDictionaryByCode(DicCodeEnum.PHASE.getCode());
            List<ClueDistributionedTaskDTO> orderList = listJSONResult.getData();
            int size = orderList.size();
            for (int i = 0; i < size; i++) {
                ClueDistributionedTaskDTO taskDTO = orderList.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(taskDTO.getClueId() + ""); // 资源ID
                curList.add(DateUtil.convert2String(taskDTO.getCreateTime(), "yyyy/MM/dd HH:mm:ss")); // 创建时间
                curList.add(taskDTO.getSourceName()); // 媒介
                curList.add(taskDTO.getSourceTypeName()); // 广告位
//                //如果资源类别为餐盟平台，则将留言重点放入渠道字段
//                String channel = "";
//                if(CategoryConstant.CMPT.equals(taskDTO.getCategory())
//                        && !String.valueOf(AggregationConstant.CLUE_SOURCE.SOURCE_4).equals(taskDTO.getSourceFrom())){
//                    channel = taskDTO.getMessagePoint();
//                }

                //慧聚 【资源管理-已分配资源】「导出资源情况」导出的表格中，需要将有「注册时间」的资源的「留言重点」内容，赋值给「渠道」（直接赋值），否则不需要赋值；
                String channel = "";
                if(null != taskDTO.getRegisterTime()){
                    channel = taskDTO.getMessagePoint();
                }
                curList.add(channel);

                curList.add(taskDTO.getTypeName()); // 资源类型
                curList.add(taskDTO.getCategoryName()); // 资源类别
                curList.add(taskDTO.getProjectName()); // 资源项目
                curList.add(taskDTO.getCusName()); // 姓名
                curList.add(taskDTO.getPhone()); // 手机1
                curList.add(taskDTO.getPhoneLocale());
                curList.add(taskDTO.getEmail()); // 邮箱
                curList.add(taskDTO.getQq()); // QQ
                curList.add(taskDTO.getPhone2()); // 手机2
                curList.add(taskDTO.getPhone2Locale());
                curList.add(taskDTO.getWechat()); // 微信1
                curList.add(taskDTO.getWechat2()); // 微信2
                curList.add(taskDTO.getAddress()); // 地址
                curList.add(DateUtil.convert2String(taskDTO.getMessageTime(), "yyyy/MM/dd HH:mm:ss")); // 留言时间
                curList.add(taskDTO.getMessagePoint()); // 留言内容
                curList.add(taskDTO.getSearchWord()); // 搜索词
                curList.add(taskDTO.getOperationName()); // 资源专员
                curList.add(taskDTO.getSourcetwo()); // 所属组
                curList.add(taskDTO.getIndustryCategoryName()); // 行业类别
                curList.add(taskDTO.getRemark()); // 备注
                curList.add(taskDTO.getTeleCompanyName()); // 电销分公司
                curList.add(taskDTO.getTeleDirectorName()); // 电销组总监

                curList.add(taskDTO.getTeleGorupName()); // 电销组
                // 这两个要进行转换
                String isCall = null;
                if (taskDTO.getIsCall() != null) {
                    if (taskDTO.getIsCall() == 1) {
                        isCall = "是";
                    } else {
                        isCall = "否";
                    }
                }
                // 是否接通
                curList.add(isCall);
                String status = null;
                if (taskDTO.getStatus() != null) {
                    if (taskDTO.getStatus() == 1) {
                        status = "是";
                    } else {
                        status = "否";
                    }
                }
                // 是否有效
                curList.add(status);
                // 只要下发的肯定都是否（产品定的，都是否）
                curList.add("否"); // 是否重复
                // 是否自建
                String inputName = "否 ";
                if (taskDTO.getInputType() != null && (AggregationConstant.INPUT_TYPE.TYPE_1 == taskDTO.getInputType()
                        || AggregationConstant.INPUT_TYPE.TYPE_6 == taskDTO.getInputType())) {
                    inputName = "是";
                }
                curList.add(inputName);
                // 首次分配话务组
                curList.add(taskDTO.getFirstAsssignTrafficGroupName());
                // 首次分配电销组
                curList.add(taskDTO.getFirstAsssignTeleGroupName());
                // 首次分配电销总监
                curList.add(taskDTO.getFirstAsssignTeleDirectorName());
                // 首次分配电销顾问
                curList.add(taskDTO.getFirstAssignTeleSaleName());
                String phase = "";
                // 添加资源阶段
                if (taskDTO.getPhase() != null) {
                    if (dictionaryItemRespDTOs != null && dictionaryItemRespDTOs.size() > 0) {
                        for (DictionaryItemRespDTO dictionaryItemRespDTO : dictionaryItemRespDTOs) {
                            if (dictionaryItemRespDTO.getValue().equals(taskDTO.getPhase().toString())) {
                                phase = dictionaryItemRespDTO.getName();
                            }
                        }
                    }
                }
                curList.add(phase);
                String phtraIsCall = "";
                if (BusinessConfigConstant.YES.equals(taskDTO.getPhtraIsCall())) {
                    phtraIsCall = "是";
                } else if (BusinessConfigConstant.NO.equals(taskDTO.getPhtraIsCall())) {
                    phtraIsCall = "否";
                }
                curList.add(phtraIsCall);
                String phstatus = "";
                if (BusinessConfigConstant.YES.equals(taskDTO.getPhstatus())) {
                    phstatus = "是";
                } else if (BusinessConfigConstant.NO.equals(taskDTO.getPhstatus())) {
                    phstatus = "否";
                }
                curList.add(phstatus);
                curList.add(taskDTO.getCusLevelName());
                curList.add(taskDTO.getOperatorName());
                String sourceFrom = "";
                if (null != taskDTO.getSourceFrom()) {
                    if (taskDTO.getSourceFrom().equals(String.valueOf(com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_6))) {
                        sourceFrom = com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_6_NAME;
                    } else if (taskDTO.getSourceFrom()
                            .equals(String.valueOf(com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_7))) {
                        sourceFrom = com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_7_NAME;
                    } else if (taskDTO.getSourceFrom()
                            .equals(String.valueOf(com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_8))) {
                        sourceFrom = com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_8_NAME;
                    }else if (taskDTO.getSourceFrom()
                            .equals(String.valueOf(com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_9))) {
                        sourceFrom = com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_9_NAME;
                    }else if (taskDTO.getSourceFrom()
                            .equals(String.valueOf(com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_10))) {
                        sourceFrom = com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_10_NAME;
                    }
                }

                curList.add(sourceFrom);
                // 如果「资源项目归属」为空，那么读取「咨询项目」字段内容
                if (StringUtils.isBlank(taskDTO.getAscriptionProjectName())) {
                    curList.add(taskDTO.getConsultProjectTurn());
                } else {
                    curList.add(taskDTO.getAscriptionProjectName());
                }
                curList.add(taskDTO.getRootWord());
                if (null != taskDTO.getConsultProjectIsShow() && BusinessConfigConstant.YES.equals(taskDTO.getConsultProjectIsShow())) {
                    curList.add(taskDTO.getConsultProjectTurn());
                } else {
                    curList.add("");
                }
                curList.add(taskDTO.getSaleLadder());
                curList.add(taskDTO.getClueLadder());

                // 经纪人字段处理
                if (null != taskDTO.getBusinessLine() && taskDTO.getBusinessLine().equals(15)) {
                    // 首次分配经纪组
                    curList.add(taskDTO.getFirstAsssignTeleGroupName());
                    // 首次分配经纪人
                    curList.add(taskDTO.getFirstAssignTeleSaleName());
                    // 现负责经纪组
                    curList.add(taskDTO.getAgentGroupNames());
                    // 现负责经纪人
                    curList.add(taskDTO.getAgentNames());
                    // 经纪顾问是否接通
                    String consultantIsCall = "";
                    if (BusinessConfigConstant.YES.equals(taskDTO.getConsultantIsCall())) {
                        consultantIsCall = "是";
                    } else if(BusinessConfigConstant.NO.equals(taskDTO.getConsultantIsCall())){
                        consultantIsCall = "否";
                    }
                    // 经纪顾问是否有效
                    String consultantStatus = "";
                    if (BusinessConfigConstant.YES.equals(taskDTO.getConsultantStatus())) {
                        consultantStatus = "是";
                    } else if(BusinessConfigConstant.NO.equals(taskDTO.getConsultantStatus())) {
                        consultantStatus = "否";
                    }
                    // 经纪是否接通
                    String agentIsCall = "";
                    if (BusinessConfigConstant.YES.equals(taskDTO.getAgentIsCall())) {
                        agentIsCall = "是";
                    } else if(BusinessConfigConstant.NO.equals(taskDTO.getAgentIsCall())){
                        agentIsCall = "否";
                    }
                    // 经纪是否有效
                    String agentStatus = "";
                    if (BusinessConfigConstant.YES.equals(taskDTO.getAgentStatus())) {
                        agentStatus = "是";
                    } else if(BusinessConfigConstant.NO.equals(taskDTO.getAgentStatus())){
                        agentStatus = "否";
                    }
                    curList.add(consultantIsCall);
                    curList.add(consultantStatus);
                    curList.add(agentIsCall);
                    curList.add(agentStatus);
                    curList.add(taskDTO.getRegisterTime());
                } else {
                    curList.add("");
                    curList.add("");
                    curList.add("");
                    curList.add("");
                    curList.add("");
                    curList.add("");
                    curList.add("");
                    curList.add("");
                    curList.add("");
                }
                curList.add(taskDTO.getPid() == null ? "" : taskDTO.getPid() + "");
                String isMatch = "";
                if (taskDTO.getPid() != null) {
                    // 子资源不展示
                    isMatch = "";
                } else if (Integer.valueOf(CustomerStatusEnum.STATUS__8TH.getCode()).equals(taskDTO.getCustomerStatus())) {
                    isMatch = "是";
                } else {
                    isMatch = "否";
                }
                curList.add(isMatch);
                // 子资源不展示
                if (taskDTO.getPid() == null) {
                    curList.add(taskDTO.getAgentBrandNames());
                } else {
                    curList.add("");
                }
                // 子资源不展示
                if (taskDTO.getPid() == null) {
                    curList.add(taskDTO.getAdBrandNames());
                } else {
                    curList.add("");
                }
                String GroupTypeName = "";
                if (ComConstant.FIRST_ASSIGN_GROUP_TYPE.TYPE1.equals(taskDTO.getFirstAssignGroupType())) {
                    GroupTypeName = "电销顾问";
                } else if (ComConstant.FIRST_ASSIGN_GROUP_TYPE.TYPE2.equals(taskDTO.getFirstAssignGroupType())) {
                    GroupTypeName = "话务";
                } else if (ComConstant.FIRST_ASSIGN_GROUP_TYPE.TYPE3.equals(taskDTO.getFirstAssignGroupType())) {
                    GroupTypeName = "加盟经纪";
                } else if (ComConstant.FIRST_ASSIGN_GROUP_TYPE.TYPE4.equals(taskDTO.getFirstAssignGroupType())) {
                    GroupTypeName = "加盟顾问";
                }
                curList.add(GroupTypeName);
                curList.add(taskDTO.getFirstAssignGroupName());
                if (taskDTO.getPid() == null) {
                    curList.add("直发资源");
                } else {
                    curList.add("顾问匹配");
                }
                //首次响应间隔
                curList.add(taskDTO.getFirstResponseInterval());
                //加盟顾问
                curList.add(taskDTO.getConsultantName());

                curList.add(taskDTO.getFirstAssignConsultantName());
                curList.add(taskDTO.getUrlAddress());
                curList.add(taskDTO.getReleaseReasonName());
                curList.add(taskDTO.getReleaseTime());
                curList.add(taskDTO.getRelateFocusPoint());

                curList.add(taskDTO.getClueFirstAssignTime());
                curList.add(taskDTO.getClueFirstTrackTime());
                curList.add(taskDTO.getClueFowEndTime());
                curList.add(taskDTO.getClueFowNum());
                dataList.add(curList);

            }
        }

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            // XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel1(dataList);
            String name = "资源情况" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ExcelWriter excelWriter = EasyExcel.write(outputStream, ClueExportModel.class).build();
            List<List<List<Object>>> partition = Lists.partition(dataList, 50000);
            for (int i = 0; i < partition.size(); i++) {
                // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                WriteSheet writeSheet = EasyExcel.writerSheet(i, "资源情况" + i).build();
                // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                excelWriter.write(partition.get(i), writeSheet);
            }
            excelWriter.finish();
        }

    }

    /**
     * 导出资源沟通情况
     * @TODOif判断修改
     */
    @LogRecord(description = "导出资源沟通情况", operationType = OperationType.EXPORT, menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    @PostMapping("/findCommunicateRecords")
    public void findCommunicateRecords(HttpServletRequest request, HttpServletResponse response, @RequestBody ClueDistributionedTaskQueryDTO queryDto)
            throws Exception {
        UserInfoDTO user = getUser();
        queryDto.setPromotionCompany(user.getPromotionCompany());
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        List<Long> idList = new ArrayList<Long>();
        // 处理数据权限，客户经理、客户主管、客户专员；内勤经理、内勤主管、内勤专员；优化经理、优化主管、优化文员
        if (RoleCodeEnum.KFZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.NQWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.HWY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())) {
            // 推广客服、内勤文员 能看自己的数据
            idList.add(user.getId());
        } else if (RoleCodeEnum.KFZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHZZ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            // 客服主管、内勤主管 能看自己组员数据
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.KFJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.TGFZC.name().equals(roleInfoDTO.getRoleCode())) {
            // 内勤经理 能看下属组的数据
            List<OrganizationRespDTO> groupList = getGroupList(user.getOrgId(), null);
            for (OrganizationRespDTO organizationRespDTO : groupList) {
                List<UserInfoDTO> userList = getUserList(organizationRespDTO.getId(), null, null);
                for (UserInfoDTO userInfoDTO : userList) {
                    idList.add(userInfoDTO.getId());
                }
            }
            // 自己组织内的人
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.GLY.name().equals(roleInfoDTO.getRoleCode())) {
            idList = null;
        }
        if (RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            queryDto.setShowTrafficClue(true);
        }
        if (RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())) {
            queryDto.setOperatorIdList(idList);
        }
        queryDto.setResourceDirectorList(idList);
        queryDto.setUserDataAuthList(user.getUserDataAuthList());
        // 查询资源沟通情况集合
        JSONResult<List<ClueDistributionedTaskDTO>> listJSONResult = extendClueFeignClient.findCommunicateRecords(queryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        // 获取资源导出情况Excel表头
        // dataList.add(getCommunicateRecordsHeadTitleList());

        if (JSONResult.SUCCESS.equals(listJSONResult.getCode()) && listJSONResult.getData() != null && listJSONResult.getData().size() != 0) {

            List<ClueDistributionedTaskDTO> orderList = listJSONResult.getData();
            int size = orderList.size();
            for (int i = 0; i < size; i++) {
                ClueDistributionedTaskDTO taskDTO = orderList.get(i);
                List<Object> curList = new ArrayList<>();
                // 资源ID
                curList.add(taskDTO.getClueId() + "");
                //客户姓名
                if(queryDto.getFalg() == 4){
                    curList.add(taskDTO.getCusName());
                }
                // 客户级别
                curList.add(taskDTO.getCusLevelName());
                // 创建时间
                curList.add(DateUtil.convert2String(taskDTO.getCreateTime(), "yyyy/MM/dd HH:mm:ss"));
                // 资源类别
                curList.add(taskDTO.getCategoryName());
                // 资源类型
                curList.add(taskDTO.getTypeName());
                // 媒介
                curList.add(taskDTO.getSourceName());

                if(queryDto.getFalg() == 4){
                    //广告位
                    curList.add(taskDTO.getSourceTypeName());
                    //资源项目归属
                    curList.add(taskDTO.getAscriptionProjectName());
                }
                // 资源项目
                curList.add(taskDTO.getProjectName());
                // 手机号
                curList.add(taskDTO.getPhone());
                // 手机号归属地
                curList.add(taskDTO.getPhoneLocale());
                // 手机号2
                curList.add(taskDTO.getPhone2());
                // 手机号2归属地
                curList.add(taskDTO.getPhone2Locale());
                // QQ
                curList.add(taskDTO.getQq());
                // 微信
                curList.add(taskDTO.getWechat());
                // 搜索词
                curList.add(taskDTO.getSearchWord());
                if (queryDto.getPhtraExport()) {
                    // 首次分配话务组
                    curList.add(taskDTO.getFirstAsssignTrafficGroupName());
                    // 首次分配话务主管
                    curList.add(taskDTO.getPhtraDirectorName());
                    // 首次分配话务员
                    curList.add(taskDTO.getOperatorName());
                } else if(!queryDto.getPhtraExport() && queryDto.getFalg() !=4 ){
                    // 首次分配电销组
                    curList.add(taskDTO.getFirstAsssignTeleGroupName());
                    // 首次分配电销总监
                    curList.add(taskDTO.getFirstAsssignTeleDirectorName());
                    // 电销组
                    curList.add(taskDTO.getTeleGorupName());
                    // 电销顾问
                    curList.add(taskDTO.getTeleSaleName());
                }
                if(queryDto.getFalg() == 4){
                    //客户类别
                    if (taskDTO.getPid() == null) {
                        curList.add("直发资源");
                    } else {
                        curList.add("顾问匹配");
                    }
                    //是否顾问匹配
                    String isMatch = "";
                    if (taskDTO.getPid() != null) {
                        isMatch = "是";
                    }else {
                        isMatch = "否";
                    }
                    curList.add(isMatch);
                    //首次下发类别
                    curList.add(taskDTO.getFirstAssignGroupTypeName());
                    //首次分配部门
                    curList.add(taskDTO.getFirstAssignGroupName());
                }
                // 首次响应间隔
                curList.add(taskDTO.getFirstResponseInterval());
                if(queryDto.getFalg()==4){
                    //转换经纪和顾问接通和有效状态
                    curList.add(transTrueAndFalse(taskDTO.getAgentIsCall()));
                    curList.add(transTrueAndFalse(taskDTO.getAgentStatus()));
                }else{
                    // 这两个要进行转换
                    String isCall = null;
                    Integer call;
                    if (queryDto.getPhtraExport()) {
                        call = taskDTO.getPhtraIsCall();
                    } else {
                        call = taskDTO.getIsCall();
                    }
                    if (null != call && BusinessConfigConstant.YES.equals(call)) {
                        isCall = "是";
                    } else {
                        isCall = "否";
                    }
                    // 是否接通
                    curList.add(isCall);
                    String statusStr = null;
                    Integer status;
                    if (queryDto.getPhtraExport()) {
                        status = taskDTO.getPhstatus();
                    } else {
                        status = taskDTO.getStatus();
                    }
                    if (null != status && BusinessConfigConstant.YES.equals(status)) {
                        statusStr = "是";
                    } else {
                        statusStr = "否";
                    }
                    // 是否有效
                    curList.add(statusStr);
                }
                if (queryDto.getPhtraExport()) {
                    // 第一次拨打时间
                    curList.add(DateUtil.convert2String(taskDTO.getPhtraFirstCallTime(), "yyyy/MM/dd HH:mm:ss"));
                } else {
                    // 第一次拨打时间
                    curList.add(DateUtil.convert2String(taskDTO.getFirstCallTime(), "yyyy/MM/dd HH:mm:ss"));
                }

                // 第一次沟通时间
                curList.add(DateUtil.convert2String(taskDTO.getFirstCommunicateTime(), "yyyy/MM/dd HH:mm:ss"));
                // 第一次沟通内容
                curList.add(taskDTO.getFirstCommunicateContent());
                // 第二次沟通时间
                curList.add(DateUtil.convert2String(taskDTO.getSecondCommunicateTime(), "yyyy/MM/dd HH:mm:ss"));
                // 第二次沟通内容
                curList.add(taskDTO.getSecondCommunicateContent());
                // 第三次沟通时间
                curList.add(DateUtil.convert2String(taskDTO.getThreeCommunicateTime(), "yyyy/MM/dd HH:mm:ss"));
                // 第三次沟通内容
                curList.add(taskDTO.getThreeCommunicateContent());
                // 留言时间
                curList.add(DateUtil.convert2String(taskDTO.getMessageTime(), "yyyy/MM/dd HH:mm:ss"));
                // 资源专员
                curList.add(taskDTO.getOperationName());
                // 所属组
                curList.add(taskDTO.getSourcetwo());
                String isSelfBuild;
                Integer inputType = taskDTO.getInputType();
                if (inputType == 1) {
                    isSelfBuild = "是";
                } else {
                    isSelfBuild = "否";
                }
                if (!queryDto.getPhtraExport()) {
                    // 是否自建
                    curList.add(isSelfBuild);
                }
                // ip
                curList.add(taskDTO.getIp());
                // 手机号1异常标签
                curList.add(taskDTO.getPhone1AbnormalLabel());
                // 手机号2异常标签
                curList.add(taskDTO.getPhone2AbnormalLabel());
                // 投资金额
                curList.add(taskDTO.getAdvanceAmountValue());
                // 是否重复
                Integer isRepeatPhone = taskDTO.getIsRepeatPhone();
                String repeatPhone = "";
                if (null != isRepeatPhone && isRepeatPhone == 1) {
                    repeatPhone = "是";
                } else {
                    repeatPhone = "否";
                }
                curList.add(repeatPhone);
                String sourceFrom = "";
                // 合并代码后替换成枚举类
                if (null != taskDTO.getSourceFrom()) {
                    if (taskDTO.getSourceFrom().equals(String.valueOf(com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_6))) {
                        sourceFrom = com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_6_NAME;
                    } else if (taskDTO.getSourceFrom()
                            .equals(String.valueOf(com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_7))) {
                        sourceFrom = com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_7_NAME;
                    } else if (taskDTO.getSourceFrom()
                            .equals(String.valueOf(com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_8))) {
                        sourceFrom = com.kuaidao.aggregation.constant.AggregationConstant.CLUE_SOURCE.SOURCE_8_NAME;
                    }
                }
                if (!queryDto.getPhtraExport()) {
                    curList.add(sourceFrom);
                }
                // url地址
                curList.add(taskDTO.getUrlAddress());
                if (!queryDto.getPhtraExport()) {
                    // 词根
                    curList.add(taskDTO.getRootWord());
                    // 咨询项目
                    if (null != taskDTO.getConsultProjectIsShow() && BusinessConfigConstant.YES.equals(taskDTO.getConsultProjectIsShow())) {
                        curList.add(taskDTO.getConsultProjectTurn());
                    } else {
                        curList.add("");
                    }
                }
                if (!queryDto.getPhtraExport()) {
                    // 阶段名称
                    curList.add(taskDTO.getPhaseName());
                }
                // 经纪人字段处理
                if (!queryDto.getPhtraExport() && null != taskDTO.getBusinessLine() && taskDTO.getBusinessLine().equals(15)) {
                    // 首次分配经纪组
                    curList.add(taskDTO.getFirstAsssignTeleGroupName());
                    // 首次分配经纪人
                    curList.add(taskDTO.getFirstAssignTeleSaleName());
                    // 现负责经纪组
                    curList.add(taskDTO.getAgentGroupNames());
                    // 现负责经纪人
                    curList.add(taskDTO.getAgentNames());
                }

                if(queryDto.getFalg()==4){
                    curList.add(transTrueAndFalse(taskDTO.getConsultantIsCall()));
                    curList.add(transTrueAndFalse(taskDTO.getConsultantStatus()));
                    curList.add(taskDTO.getConsultantName());
                }

                dataList.add(curList);
            }
        }
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            String title = "";
            if (queryDto != null && queryDto.getPhtraExport() != null) {
                if(queryDto.getFalg()==4){
                    title = "顾问∕经纪跟进记录";
                }else{
                    if (queryDto.getPhtraExport()) {
                        title = "话务资源沟通记录";
                    } else {
                        title = "电销资源沟通记录";
                    }
                }
            }
            String name = title +""+ DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
            response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
            response.setContentType("application/octet-stream");
            ExcelWriter excelWriter = null;
            if(queryDto.getFalg()==4){
                excelWriter = EasyExcel.write(outputStream, ClueCommunicateAgentExportModel.class).build();
            }else{
                if (queryDto.getPhtraExport()) {
                    excelWriter = EasyExcel.write(outputStream, ClueCommunicatePhtraExportModel.class).build();
                } else {
                    excelWriter = EasyExcel.write(outputStream, ClueCommunicateExportModel.class).build();
                }

            }

            if (CollectionUtils.isNotEmpty(dataList)) {
                List<List<List<Object>>> partition = Lists.partition(dataList, 50000);
                for (int i = 0; i < partition.size(); i++) {
                    // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, "Sheet" + i).build();
                    // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                    excelWriter.write(partition.get(i), writeSheet);
                }
            } else {
                // 实例化表单
                WriteSheet writeSheet = EasyExcel.writerSheet(0, "Sheet").build();
                excelWriter.write(dataList, writeSheet);
            }
            excelWriter.finish();
        }
    }

    private String transTrueAndFalse(Integer i){
        if (i==null) {
            return "";
        }
        if(i==BusinessConfigConstant.YES){
            return "是";
        }else{
            return "否";
        }
    }


    /**
     * 查询所有资源专员
     * @return
     */

    private List<UserInfoDTO> queryUserByRole(UserInfoDTO user) {

        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        String roleCode = user.getRoleList().get(0).getRoleCode();
        UserOrgRoleReq userRole = new UserOrgRoleReq();
        if (RoleCodeEnum.HWY.name().equals(roleCode)) {
            userList.add(user);
            return userList;
        } else if (RoleCodeEnum.GLY.name().equals(roleCode) || RoleCodeEnum.YWGLY.name().equals(roleCode)
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
        JSONResult<List<UserInfoDTO>> userAdminJson = userInfoFeignClient.listByOrgAndRole(userRoleAdmin);
        if (JSONResult.SUCCESS.equals(userAdminJson.getCode()) && null != userAdminJson.getData()) {
            userAdminList = userAdminJson.getData();
        }
        userList.addAll(userAdminList);
        return userList;

    }

    /**
     * 获取当前登录账号
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 根据机构和角色类型获取用户
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole = userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 获取所有组织组
     * @return
     */
    private List<OrganizationRespDTO> getGroupList(Long parentId, Integer type) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setParentId(parentId);
        queryDTO.setOrgType(type);
        // 查询所有组织
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam = organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> data = queryOrgByParam.getData();
        return data;
    }

    /**
     * 查询字典表
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
     * 查询系统参数
     * @param code
     * @return
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
     * 导出资源情况数量，用于导出前提示
     */
    // @RequiresPermissions("aggregation:truckingOrder:export")
    @PostMapping("/findCluesCount")
    @ResponseBody
    public JSONResult<Long> findCluesCount(@RequestBody ClueDistributionedTaskQueryDTO queryDto) throws Exception {
        UserInfoDTO user = getUser();
        queryDto.setPromotionCompany(user.getPromotionCompany());
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        List<Long> idList = new ArrayList<Long>();
        // 处理数据权限，客户经理、客户主管、客户专员；内勤经理、内勤主管、内勤专员；优化经理、优化主管、优化文员
        if (RoleCodeEnum.KFZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.NQWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZY.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())) {
            // 推广客服、内勤文员 能看自己的数据
            idList.add(user.getId());
        } else if (RoleCodeEnum.KFZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHZZ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            // 客服主管、内勤主管 能看自己组员数据
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.KFJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.WLYHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YXZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.TGFZC.name().equals(roleInfoDTO.getRoleCode())) {
            // 内勤经理 能看下属组的数据
            List<OrganizationRespDTO> groupList = getGroupList(user.getOrgId(), null);
            for (OrganizationRespDTO organizationRespDTO : groupList) {
                List<UserInfoDTO> userList = getUserList(organizationRespDTO.getId(), null, null);
                for (UserInfoDTO userInfoDTO : userList) {
                    idList.add(userInfoDTO.getId());
                }
            }
            // 自己组织内的人
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.GLY.name().equals(roleInfoDTO.getRoleCode())) {
            idList = null;
        }
        if (RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            queryDto.setShowTrafficClue(true);
        }
        if (RoleCodeEnum.HWZG.name().equals(roleInfoDTO.getRoleCode()) || RoleCodeEnum.HWJL.name().equals(roleInfoDTO.getRoleCode())) {
            queryDto.setOperatorIdList(idList);
        }
        queryDto.setResourceDirectorList(idList);
        queryDto.setUserDataAuthList(user.getUserDataAuthList());
        JSONResult<Long> listJSONResult = extendClueFeignClient.findCluesCount(queryDto);
        Long count = 0L;
        JSONResult<Long> jsonResult = new JSONResult<Long>().success(count);
        if (listJSONResult != null && JSONResult.SUCCESS.equals(listJSONResult.getCode())) {
            return listJSONResult;
        }
        return jsonResult;
    }

    /**
     * 根据角色查询用户
     * @return
     */

    private List<UserInfoDTO> queryUserByRole(String roleCode) {
        List<UserInfoDTO> userList = new ArrayList<>();
        UserOrgRoleReq userRole = new UserOrgRoleReq();
        userRole.setRoleCode(RoleCodeEnum.HWY.name());
        JSONResult<List<UserInfoDTO>> listJSONResult = userInfoFeignClient.listByOrgAndRole(userRole);
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode()) && CollectionUtils.isNotEmpty(listJSONResult.getData())) {
            userList.addAll(listJSONResult.getData());
        }
        return userList;
    }
}
