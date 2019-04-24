package com.kuaidao.manageweb.controller.clue;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.ClueAgendaTaskDTO;
import com.kuaidao.aggregation.dto.clue.ClueAgendaTaskQueryDTO;
import com.kuaidao.aggregation.dto.clue.ClueDTO;
import com.kuaidao.aggregation.dto.clue.ClueQueryDTO;
import com.kuaidao.aggregation.dto.clue.PushClueReq;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
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
        request.setAttribute("ossUrl", ossUrl);
        return "clue/addCluePage";
    }

    /**
     * 跳转新增资源
     * 
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/toUpdatePage")
    @RequiresPermissions("waitDistributResource:update")
    public String toUpdatePage(@RequestParam long id, HttpServletRequest request, Model model) {
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
        request.setAttribute("ossUrl", ossUrl);
        return "clue/addCluePage";
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
    @RequiresPermissions("waitDistributResource:update")
    @ResponseBody
    @LogRecord(description = "编辑资源", operationType = OperationType.UPDATE,
            menuName = MenuEnum.WAIT_DISTRIBUT_RESOURCE)
    public JSONResult<String> updateClue(HttpServletRequest request,
            @RequestBody PushClueReq pushClueReq) {

        JSONResult<String> clueInfo = extendClueFeignClient.updateClue(pushClueReq);

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
}
