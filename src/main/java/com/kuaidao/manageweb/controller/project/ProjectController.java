/**
 * 
 */
package com.kuaidao.manageweb.controller.project;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoPageParam;
import com.kuaidao.aggregation.dto.project.ProjectInfoReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;

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

    /***
     * 项目列表页
     * 
     * @return
     */
    @RequestMapping("/initProjectList")
    @RequiresPermissions("aggregation:projectManager:view")
    public String initProjectList(HttpServletRequest request) {


        return "project/projectManagePage";
    }

    /***
     * 新增项目页
     * 
     * @return
     */
    @RequestMapping("/initCreateProject")
    @RequiresPermissions("aggregation:projectManager:add")
    public String initCreateProject(HttpServletRequest request) {

        // 查询公司列表
        JSONResult<List<ProjectInfoDTO>> listNoPage =
                projectInfoFeignClient.listNoPage(new ProjectInfoPageParam());

        request.setAttribute("companyList", listNoPage.getData());
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
        // 查询公司列表
        JSONResult<List<ProjectInfoDTO>> listNoPage =
                projectInfoFeignClient.listNoPage(new ProjectInfoPageParam());

        request.setAttribute("companyList", listNoPage.getData());

        return "project/editProjectPage";
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
     * 项目列表(不分页)
     * 
     * @return
     */
    @PostMapping("/listNoPage")
    @ResponseBody
    @RequiresPermissions("aggregation:projectManager:view")
    public JSONResult<List<ProjectInfoDTO>> listNoPage(
            @RequestBody ProjectInfoPageParam projectInfoPageParam, HttpServletRequest request) {

        JSONResult<List<ProjectInfoDTO>> list =
                projectInfoFeignClient.listNoPage(projectInfoPageParam);

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
    public JSONResult deleteMenu(@RequestBody IdEntityLong idEntity) {

        return projectInfoFeignClient.delete(idEntity);
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
