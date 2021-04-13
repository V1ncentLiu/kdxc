/**
 * 
 */
package com.kuaidao.manageweb.controller.clue;

import java.lang.reflect.InvocationTargetException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.businessconfig.dto.clueTemplate.ClueTemplateDTO;
import com.kuaidao.businessconfig.dto.clueTemplate.ClueTemplatePageParam;
import com.kuaidao.businessconfig.dto.clueTemplate.ClueTemplateReq;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.clue.ClueTemplateFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/aggregation/clueTemplate")
public class ClueTemplateController {
    private static Logger logger = LoggerFactory.getLogger(ClueTemplateController.class);
    @Autowired
    private ClueTemplateFeignClient clueTemplateFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;

    /***
     * 资源模板列表页
     * 
     * @return
     */
    @RequestMapping("/initList")
    @RequiresPermissions("aggregation:clueTemplate:view")
    public String initCompanyList(HttpServletRequest request) {
        // 查询所有项目
        JSONResult<List<ProjectInfoDTO>> allProject = projectInfoFeignClient.allProject();
        request.setAttribute("projectList", allProject.getData());
        // 查询非优化字典资源类别集合
        request.setAttribute("clueCategoryList",getDictionaryByCode(DicCodeEnum.CLUECATEGORY.getCode()));
        // 查询字典资源类型集合
        request.setAttribute("clueTypeList", getDictionaryByCode(DicCodeEnum.CLUETYPE.getCode()));
        // 查询字典广告位集合
        request.setAttribute("adsenseList", getDictionaryByCode(DicCodeEnum.ADENSE.getCode()));
        // 查询字典媒介集合
        request.setAttribute("mediumList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        // 查询字典行业类别集合
        request.setAttribute("industryCategoryList", getDictionaryByCode(DicCodeEnum.INDUSTRYCATEGORY.getCode()));
        // 当前账号id
        request.setAttribute("userId", getUserId());

        return "clue/clueTemplateManagerPage";
    }

    /***
     * 查询资源模板详情
     * 
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    @RequiresPermissions("aggregation:clueTemplate:view")
    public JSONResult<ClueTemplateDTO> getCompany(@RequestBody IdEntityLong id,
            HttpServletRequest request) {
        // 查询资源模板信息
        return clueTemplateFeignClient.get(id);
    }

    /***
     * 资源模板列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public JSONResult<PageBean<ClueTemplateDTO>> list(
            @RequestBody ClueTemplatePageParam clueTemplatePageParam, HttpServletRequest request) {

        JSONResult<PageBean<ClueTemplateDTO>> list =
                clueTemplateFeignClient.list(clueTemplatePageParam);

        return list;
    }

    /***
     * 资源模板列表(不分页)
     *
     * @return
     */
    @PostMapping("/listNoPage")
    @ResponseBody
    public JSONResult<List<ClueTemplateDTO>> listNoPage(
            @RequestBody ClueTemplatePageParam clueTemplatePageParam, HttpServletRequest request) {

        JSONResult<List<ClueTemplateDTO>> list =
                clueTemplateFeignClient.listNoPage(clueTemplatePageParam);

        return list;
    }



    /**
     * 保存资源模板
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/save")
    @ResponseBody
    @RequiresPermissions("aggregation:clueTemplate:add")
    @LogRecord(description = "新增资源模板", operationType = OperationType.INSERT,
            menuName = MenuEnum.CLUE_TEMPLATE_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody ClueTemplateReq clueTemplateReq,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        long userId = getUserId();
        clueTemplateReq.setCreateUser(userId);
        return clueTemplateFeignClient.create(clueTemplateReq);
    }

    /**
     * 修改资源模板信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresPermissions("aggregation:clueTemplate:edit")
    @LogRecord(description = "修改资源模板信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.CLUE_TEMPLATE_MANAGEMENT)
    public JSONResult update(@Valid @RequestBody ClueTemplateReq clueTemplateReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = clueTemplateReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        return clueTemplateFeignClient.update(clueTemplateReq);
    }


    /**
     * 删除资源模板信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @RequiresPermissions("aggregation:clueTemplate:delete")
    @LogRecord(description = "删除资源模板信息", operationType = OperationType.DELETE,
            menuName = MenuEnum.CLUE_TEMPLATE_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListLongReq idList) {

        return clueTemplateFeignClient.delete(idList);
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
