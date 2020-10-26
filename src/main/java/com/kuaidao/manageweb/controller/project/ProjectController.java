/**
 * 
 */
package com.kuaidao.manageweb.controller.project;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.businessconfig.dto.project.BrandListDTO;
import com.kuaidao.businessconfig.dto.project.BrandListPageParam;
import com.kuaidao.businessconfig.dto.project.CategoryDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.businessconfig.dto.project.ProjectInfoReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.common.util.SortUtils;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/aggregation/projectManager")
public class ProjectController {
    private static Logger logger = LoggerFactory.getLogger(ProjectController.class);
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;

    /***
     * 项目列表页
     * 
     * @return
     */
    @RequestMapping("/initProjectList")
    @RequiresPermissions("aggregation:projectManager:view")
    public String initProjectList(HttpServletRequest request) {
        // 查询字典品类集合
        request.setAttribute("categoryList", getDictionaryByCode(Constants.PROJECT_CATEGORY));
        // 查询字典类别集合
        request.setAttribute("classificationList",
                getDictionaryByCode(Constants.PROJECT_CLASSIFICATION));
        // 查询字典店型集合
        request.setAttribute("shopTypeList", getDictionaryByCode(Constants.PROJECT_SHOPTYPE));
        // 查询字典项目归属集合
        request.setAttribute("projectAttributiveList", getDictionaryByCode(Constants.PROJECT_ATTRIBUTIVE));
        // // 查询所属集团列表
        // JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        // 获取商家账号集合，倒叙
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoDTO.setStatusList(null);
        List<UserInfoDTO> userInfoList = getMerchantUser(userInfoDTO);
        request.setAttribute("userInfoList", userInfoList);
        // request.setAttribute("companyList", listNoPage.getData());
        return "project/projectManagerPage";
    }

    /***
     * 新增项目页
     * 
     * @return
     */
    @RequestMapping("/initCreateProject")
    @RequiresPermissions("aggregation:projectManager:add")
    public String initCreateProject(HttpServletRequest request) {
        // 查询字典品类集合
        request.setAttribute("categoryList", getDictionaryByCode(Constants.PROJECT_CATEGORY));
        // 查询字典类别集合
        request.setAttribute("classificationList",
                getDictionaryByCode(Constants.PROJECT_CLASSIFICATION));
        // 查询字典店型集合
        request.setAttribute("shopTypeList", getDictionaryByCode(Constants.PROJECT_SHOPTYPE));
        // 查询字典项目归属集合
        request.setAttribute("projectAttributiveList",
                getDictionaryByCode(Constants.PROJECT_ATTRIBUTIVE));
        // 查询字典集团分配比例归属集合
        List<DictionaryItemRespDTO> ratioList = getDictionaryByCode(Constants.SETTLEMENT_RATIO);
        if (ratioList != null && ratioList.size() > 0) {
            for (DictionaryItemRespDTO dictionaryItemRespDTO : ratioList) {
                dictionaryItemRespDTO.setName(dictionaryItemRespDTO.getValue() + "%");
            }
        }
        request.setAttribute("ratioList", ratioList);
        // 查询公司列表
        // JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.allCompany();
        // request.setAttribute("companyList", listNoPage.getData());
        // JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        // request.setAttribute("companyList", listNoPage.getData());
        // 查询品牌品类集合
        JSONResult<List<CategoryDTO>> categoryList = projectInfoFeignClient.getCategoryList();
        request.setAttribute("brandCategoryList", categoryList.getData());

        // 获取商家账号集合，倒叙
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        UserInfoDTO userInfoAddDTO = new UserInfoDTO();
        userInfoAddDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoAddDTO.setStatusList(statusList);
        List<UserInfoDTO> userInfoAddList = getMerchantUser(userInfoAddDTO);
        request.setAttribute("userInfoAddList", userInfoAddList);
        return "project/addProjectPage";
    }

    /***
     * 编辑项目页
     * 
     * @return
     */
    @RequestMapping("/initUpdateProject")
    @RequiresPermissions("aggregation:projectManager:edit")
    public String initUpdateProject(@RequestParam long id, HttpServletRequest request) {
        // 查询项目信息
        JSONResult<ProjectInfoDTO> jsonResult = projectInfoFeignClient.get(new IdEntityLong(id));
        request.setAttribute("project", jsonResult.getData());
        // // 查询公司列表
        // JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.allCompany();
        //
        // request.setAttribute("companyList", listNoPage.getData());
        List<DictionaryItemRespDTO> ratioList = getDictionaryByCode(Constants.SETTLEMENT_RATIO);
        if (ratioList != null && ratioList.size() > 0) {
            for (DictionaryItemRespDTO dictionaryItemRespDTO : ratioList) {
                dictionaryItemRespDTO.setName(dictionaryItemRespDTO.getValue() + "%");
            }
        }

        request.setAttribute("ratioList", ratioList);
        // JSONResult<List<CompanyInfoDTO>> listNoPage = companyInfoFeignClient.getCompanyList();
        // request.setAttribute("companyList", listNoPage.getData());

        // 查询字典品类集合
        request.setAttribute("categoryList", getDictionaryByCode(Constants.PROJECT_CATEGORY));
        // 查询字典类别集合
        request.setAttribute("classificationList",
                getDictionaryByCode(Constants.PROJECT_CLASSIFICATION));
        // 查询字典店型集合
        request.setAttribute("shopTypeList", getDictionaryByCode(Constants.PROJECT_SHOPTYPE));
        // 查询字典项目归属集合
        request.setAttribute("projectAttributiveList",
                getDictionaryByCode(Constants.PROJECT_ATTRIBUTIVE));
        // 查询品牌品类集合
        JSONResult<List<CategoryDTO>> categoryList = projectInfoFeignClient.getCategoryList();
        request.setAttribute("brandCategoryList", categoryList.getData());

        // 获取商家账号集合，倒叙
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        UserInfoDTO userInfoAddDTO = new UserInfoDTO();
        userInfoAddDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoAddDTO.setStatusList(statusList);
        List<UserInfoDTO> userInfoAddList = getMerchantUser(userInfoAddDTO);
        request.setAttribute("userInfoAddList", userInfoAddList);
        return "project/addProjectPage";
    }

    /***
     * 项目列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("aggregation:projectManager:view")
    public JSONResult<PageBean<ProjectInfoDTO>> list(
            @RequestBody ProjectInfoPageParam projectInfoPageParam, HttpServletRequest request) {

        JSONResult<PageBean<ProjectInfoDTO>> list =
                projectInfoFeignClient.list(projectInfoPageParam);

        return list;
    }

    /***
     * 品牌库列表
     * 
     * @return
     */
    @PostMapping("/brandList")
    @ResponseBody
    public JSONResult<PageBean<BrandListDTO>> list(
            @RequestBody BrandListPageParam brandListPageParam, HttpServletRequest request) {

        JSONResult<PageBean<BrandListDTO>> list =
                projectInfoFeignClient.getBrandList(brandListPageParam);

        return list;
    }
    /***
     * 根据id获取项目详情
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/getById")
    public JSONResult<ProjectInfoDTO> getById(@RequestBody IdEntityLong idEntityLong) {
        JSONResult<ProjectInfoDTO> projectInfo = projectInfoFeignClient.get(idEntityLong);
        return projectInfo;
    }

    /***
     * 项目列表(不分页)
     * 
     * @return
     */
    @PostMapping("/listNoPage")
    @ResponseBody
    public JSONResult<List<ProjectInfoDTO>> listNoPage(
            @RequestBody ProjectInfoPageParam projectInfoPageParam, HttpServletRequest request) {

        JSONResult<List<ProjectInfoDTO>> list =
                projectInfoFeignClient.listNoPage(projectInfoPageParam);
        if (list.getCode().equals(JSONResult.SUCCESS)) {
            List<ProjectInfoDTO> result = SortUtils.sortList(list.getData(), "projectName");
            return new JSONResult<List<ProjectInfoDTO>>().success(result);
        }
        return list;
    }



    /**
     * 保存项目
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/saveProject")
    @ResponseBody
    @RequiresPermissions("aggregation:projectManager:add")
    @LogRecord(description = "新增项目", operationType = OperationType.INSERT,
            menuName = MenuEnum.PROJECT_MANAGEMENT)
    public JSONResult saveMenu(@Valid @RequestBody ProjectInfoReq projectInfoReq,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        long userId = getUserId();
        projectInfoReq.setCreateUser(userId);
        projectInfoReq.setUpdateUser(userId);
        return projectInfoFeignClient.create(projectInfoReq);
    }

    /**
     * 修改项目信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateProject")
    @ResponseBody
    @RequiresPermissions("aggregation:projectManager:edit")
    @LogRecord(description = "修改项目信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.PROJECT_MANAGEMENT)
    public JSONResult updateMenu(@Valid @RequestBody ProjectInfoReq projectInfoReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = projectInfoReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        long userId = getUserId();
        projectInfoReq.setUpdateUser(userId);
        return projectInfoFeignClient.update(projectInfoReq);
    }


    /**
     * 查询项目信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/deleteProject")
    @ResponseBody
    @RequiresPermissions("aggregation:projectManager:delete")
    @LogRecord(description = "删除项目信息", operationType = OperationType.DELETE,
            menuName = MenuEnum.PROJECT_MANAGEMENT)
    public JSONResult deleteMenu(@RequestBody IdListLongReq idList) {

        return projectInfoFeignClient.delete(idList);
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
     * 获取当前登录账号ID
     * 
     * @param orgDTO
     * @return
     */
    private long getUserId() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user.getId();
    }

    /**
     * @Description 查询商家账号
     * @param userInfoDTO
     * @Return java.util.List<com.kuaidao.sys.dto.user.UserInfoDTO>
     * @Author xuyunfeng
     * @Date 2019/10/15 17:19
     **/
    private List<UserInfoDTO> getMerchantUser(UserInfoDTO userInfoDTO) {
        JSONResult<List<UserInfoDTO>> merchantUserList =
                merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        List<UserInfoDTO> userInfoDTOList = merchantUserList.getData();
        if (userInfoDTOList != null & userInfoDTOList.size() > 0) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            userInfoDTOList.sort((a1, a2) -> {
                try {
                    return df.parse(sdf.format(a2.getCreateTime()))
                            .compareTo(df.parse(sdf.format(a1.getCreateTime())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            });
        }
        return userInfoDTOList;
    }

}
