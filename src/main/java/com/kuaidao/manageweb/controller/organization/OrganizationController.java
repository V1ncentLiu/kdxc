package com.kuaidao.manageweb.controller.organization;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageHelper;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.sys.dto.organization.OrganitionRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

/**
 * 组织机构类
 * @author: Chen Chengxue
 * @date: 2019年1月3日 下午3:04:53   
 * @version V1.0
 */
@Controller
@RequestMapping("/organization/organization")
public class OrganizationController {

    private static Logger logger = LoggerFactory.getLogger(OrganizationController.class);
    @Autowired
    OrganizationFeignClient organizationFeignClient;

    /**
     * 组织机构首页
     * @return
     */
    @RequestMapping("/organizationPage")
    public String organizationPage() {
        return "organization/organizationPage";
    }

    @PostMapping("/save")
    public JSONResult save(@Valid @RequestBody OrganizationAddAndUpdateDTO orgDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return organizationFeignClient.save(orgDTO);
    }


    /**
     *   更新组织机构
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@Valid @RequestBody OrganizationAddAndUpdateDTO orgDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return organizationFeignClient.update(orgDTO);
    }


    /**
     *   删除组织机构
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public JSONResult delete(@RequestBody IdEntity idEntity) {
        String id = idEntity.getId();
        logger.info("delete organization by id{{}}", id);
        return organizationFeignClient.delete(idEntity);
    }


    @PostMapping("/queryOrgDataByParam")
    @ResponseBody
    public JSONResult<PageBean<OrganitionRespDTO>> queryOrgDataByParam(int pageNum, int pageSize,
            @RequestBody OrganizationQueryDTO queryDTO) {
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        return organizationFeignClient.queryOrgDataByParam(queryDTO);
    }

    /**
     * 查询组织信息 
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryOrgByParam")
    public JSONResult<List<OrganitionRespDTO>> queryOrgByParam(
            @RequestBody OrganizationQueryDTO queryDTO) {
        return organizationFeignClient.queryOrgByParam(queryDTO);
    }
    
    @PostMapping("/query")
    @ResponseBody
    public JSONResult<TreeData> query(){
        return null;
    }
    

}
