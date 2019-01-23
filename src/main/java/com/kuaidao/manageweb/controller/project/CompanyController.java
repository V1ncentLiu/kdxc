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
import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.aggregation.dto.project.CompanyInfoPageParam;
import com.kuaidao.aggregation.dto.project.CompanyInfoReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;

/**
 * @author zxy
 *
 */

@Controller
@RequestMapping("/aggregation/companyManager")
public class CompanyController {
    private static Logger logger = LoggerFactory.getLogger(CompanyController.class);
    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;

    /***
     * 公司列表页
     * 
     * @return
     */
    @RequestMapping("/initCompanyList")
    @RequiresPermissions("aggregation:companyManager:view")
    public String initCompanyList(HttpServletRequest request) {

        return "company/companyManagerPage";
    }

    /***
     * 查询公司详情
     * 
     * @return
     */
    @RequestMapping("/getCompany")
    @RequiresPermissions("aggregation:companyManager:view")
    public JSONResult<CompanyInfoDTO> getCompany(@RequestParam long id,
            HttpServletRequest request) {
        // 查询公司信息
        return companyInfoFeignClient.get(new IdEntityLong(id));
    }

    /***
     * 公司列表
     * 
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresPermissions("aggregation:companyManager:view")
    public JSONResult<PageBean<CompanyInfoDTO>> list(
            @RequestBody CompanyInfoPageParam companyInfoPageParam, HttpServletRequest request) {

        JSONResult<PageBean<CompanyInfoDTO>> list =
                companyInfoFeignClient.list(companyInfoPageParam);

        return list;
    }

    /***
     * 公司列表(不分页)
     * 
     * @return
     */
    @PostMapping("/listNoPage")
    @ResponseBody
    @RequiresPermissions("aggregation:companyManager:view")
    public JSONResult<List<CompanyInfoDTO>> listNoPage(
            @RequestBody CompanyInfoPageParam companyInfoPageParam, HttpServletRequest request) {

        JSONResult<List<CompanyInfoDTO>> list =
                companyInfoFeignClient.listNoPage(companyInfoPageParam);

        return list;
    }



    /**
     * 保存公司
     * 
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/saveCompany")
    @ResponseBody
    @RequiresPermissions("aggregation:CompanyManager:add")
    @LogRecord(description = "新增公司", operationType = OperationType.INSERT,
            menuName = MenuEnum.COMPANY_MANAGEMENT)
    public JSONResult saveMenu(@Valid @RequestBody CompanyInfoReq companyInfoReq,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        return companyInfoFeignClient.create(companyInfoReq);
    }

    /**
     * 修改公司信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/updateCompany")
    @ResponseBody
    @RequiresPermissions("aggregation:companyManager:edit")
    @LogRecord(description = "修改公司信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.COMPANY_MANAGEMENT)
    public JSONResult updateMenu(@Valid @RequestBody CompanyInfoReq companyInfoReq,
            BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }

        Long id = companyInfoReq.getId();
        if (id == null) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }

        return companyInfoFeignClient.update(companyInfoReq);
    }


    /**
     * 删除公司信息
     * 
     * @param orgDTO
     * @return
     */
    @PostMapping("/deleteCompany")
    @ResponseBody
    public JSONResult deleteCompany(@RequestBody IdEntityLong idEntity) {

        return companyInfoFeignClient.delete(idEntity);
    }



}
