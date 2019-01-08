package com.kuaidao.manageweb.controller.organization;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import lombok.val;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    public String organizationPage(HttpServletRequest request) {
        JSONResult<List<TreeData>> treeJsonRes = organizationFeignClient.query();
        if(treeJsonRes!=null&& JSONResult.SUCCESS.equals(treeJsonRes.getCode()) && treeJsonRes.getData()!=null) {
            request.setAttribute("orgData",treeJsonRes.getData());
        }else {
            logger.error("query organization tree,res{{}}",treeJsonRes);
        }
        return "organization/organizationPage";
    }

    @PostMapping("/saveOrUpdate")
    @ResponseBody
    public JSONResult save(@Valid @RequestBody OrganizationAddAndUpdateDTO orgDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
      //TODO devin
        orgDTO.setSystemCode("huiju");
        
        Long id = orgDTO.getId();
        if(id!=null) {
            return organizationFeignClient.update(orgDTO);
        }else {
            //TODO devin
            orgDTO.setCreateUser(1111);
            return organizationFeignClient.save(orgDTO);
        }

    }


    /**
     *   更新组织机构
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
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
    public JSONResult delete(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if(idList==null || idList.size()==0) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage()); 
        }
        logger.info("delete organization by id{{}}", idList);
        return organizationFeignClient.delete(idListReq);
    }

    /**
     * 分页 查询组织信息 
     * @param pageNum
     * @param pageSize
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryOrgDataByParam")
    @ResponseBody
    public JSONResult<PageBean<OrganizationRespDTO>> queryOrgDataByParam(int pageNum, int pageSize,
            @RequestBody OrganizationQueryDTO queryDTO) {
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        return organizationFeignClient.queryOrgDataByParam(queryDTO);
    }

    /**
     * 查询组织信息 根据组织机构代码，组织名称，父级ID
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryOrgByParam")
    @ResponseBody
    public JSONResult<Boolean> queryOrgByParam(
            @RequestBody OrganizationQueryDTO queryDTO) {
        //TODO devin
        queryDTO.setSystemCode("huiju");
        JSONResult<List<OrganizationRespDTO>> orgList = organizationFeignClient.queryOrgByParam(queryDTO);
        if(orgList!=null && JSONResult.SUCCESS.equals(orgList.getCode())) {
            List<OrganizationRespDTO> data = orgList.getData();
            if(data!=null &&data.size()!=0) {
               return new JSONResult<Boolean>().success(true);
            }
        }else {
            
        }
        return new JSONResult<Boolean>().success(false) ;
    }
    
    /**
     * 查询组织机构树
     * @return
     */
    @PostMapping("/query")
    @ResponseBody
    public JSONResult<List<TreeData>> query(){
        return  organizationFeignClient.query();
    }
    
    
    /**
     * 查询组织机构下是否由下级
     * @param idListReq  组织ID list
     * @return
     */
    @PostMapping("/queryOrgByParentId")
    @ResponseBody
    public JSONResult<Boolean> queryOrgByParentId(@RequestBody IdListReq idListReq){
        return organizationFeignClient.queryOrgByParentId(idListReq);
    }
    
    /**
     * 根据Id 查询组织结构
     * @param idListReq  组织ID list
     * @return
     */
    @PostMapping("/queryOrgById")
    @ResponseBody
    public JSONResult<OrganizationDTO> queryOrgById(@RequestBody IdEntity idEntity){
        
       return organizationFeignClient.queryOrgById(idEntity);
      
    }
    
    

}
