package com.kuaidao.manageweb.controller.organization;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.OrgUserReqDTO;
import com.kuaidao.sys.dto.user.UserAndRoleRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
 * 组织机构类
 * 
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

    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    /**
     * 组织机构首页
     * 
     * @return
     */
    @RequiresPermissions("organization:view")
    @RequestMapping("/organizationPage")
    public String organizationPage(HttpServletRequest request) {
        JSONResult<List<TreeData>> treeJsonRes = organizationFeignClient.query();
        if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode())
                && treeJsonRes.getData() != null) {
            request.setAttribute("orgData", treeJsonRes.getData());
        } else {
            logger.error("query organization tree,res{{}}", treeJsonRes);
        }
        return "organization/organizationPage";
    }

    /**
     * 查询机构类型数据
     * 
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping("/queryOrgByType")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> queryOrgByType(HttpServletRequest request,
            @RequestBody OrganizationDTO dto) {
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(dto.getOrgType());
        orgDto.setParentId(dto.getParentId());
        JSONResult<List<OrganizationRespDTO>> orgJson =
                organizationFeignClient.queryOrgByParam(orgDto);
        return orgJson;
    }

    /**
     * 保存或更新组织机构信息
     * 
     * @param orgDTO
     * @param result
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = "organization:add")
    @PostMapping("/save")
    @ResponseBody
    @LogRecord(description = "添加组织机构信息", operationType = OperationType.INSERT,
            menuName = MenuEnum.ORGANIZATION_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody OrganizationAddAndUpdateDTO orgDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        orgDTO.setSystemCode(SystemCodeConstant.HUI_JU);

        Long id = orgDTO.getId();
        if (id != null) {
            return organizationFeignClient.update(orgDTO);
        } else {
            Subject subject = SecurityUtils.getSubject();
            UserInfoDTO user = (UserInfoDTO) subject.getSession().getAttribute("user");
            orgDTO.setCreateUser(user.getId());
            return organizationFeignClient.save(orgDTO);
        }

    }

    /**
     * 更新组织机构
     * 
     * @param orgDTO
     * @return
     */
    @RequiresPermissions("organization:edit")
    @PostMapping("/update")
    @ResponseBody
    @LogRecord(description = "修改组织机构信息", operationType = OperationType.UPDATE,
            menuName = MenuEnum.ORGANIZATION_MANAGEMENT)
    public JSONResult update(@Valid @RequestBody OrganizationAddAndUpdateDTO orgDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return organizationFeignClient.update(orgDTO);
    }

    /**
     * 删除组织机构
     * 
     * @param orgDTO
     * @return
     */
    @RequiresPermissions("organization:delete")
    @PostMapping("/delete")
    @ResponseBody
    @LogRecord(description = "删除组织机构信息", operationType = OperationType.DELETE,
            menuName = MenuEnum.ORGANIZATION_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if (idList == null || idList.size() == 0) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        logger.info("delete organization by id{{}}", idList);
        return organizationFeignClient.delete(idListReq);
    }

    /**
     * 分页 查询组织信息
     * 
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
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryOrgByParam")
    @ResponseBody
    public JSONResult queryOrgByParam(@RequestBody OrganizationQueryDTO queryDTO) {
        queryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationRespDTO>> orgList =
                organizationFeignClient.queryOrgByParam(queryDTO);
        if (orgList != null && JSONResult.SUCCESS.equals(orgList.getCode())) {
            List<OrganizationRespDTO> data = orgList.getData();
            if (data != null && data.size() != 0) {
                return new JSONResult<Boolean>().success(true);
            }
        } else {
            return new JSONResult<>().fail(orgList.getCode(), orgList.getMsg());
        }
        return new JSONResult().success(false);
    }

    /**
     * 查询组织机构树
     * 
     * @return
     */
    @PostMapping("/query")
    @ResponseBody
    public JSONResult<List<TreeData>> query() {
        return organizationFeignClient.query();
    }

    /**
     * 查询组织机构下是否由下级
     * 
     * @param idListReq 组织ID list
     * @return
     */
    @PostMapping("/queryOrgByParentId")
    @ResponseBody
    public JSONResult<Boolean> queryOrgByParentId(@RequestBody IdListReq idListReq) {
        return organizationFeignClient.queryOrgByParentId(idListReq);
    }

    /**
     * 根据Id 查询组织结构
     * 
     * @param idListReq 组织ID list
     * @return
     */
    @PostMapping("/queryOrgById")
    @ResponseBody
    public JSONResult<OrganizationDTO> queryOrgById(@RequestBody IdEntity idEntity) {

        return organizationFeignClient.queryOrgById(idEntity);

    }

    /**
     * 查询组织机构下，用户信息 分页
     * 
     * @param reqDTO
     * @param result
     * @return
     */
    @PostMapping("/listOrgUserInfo")
    @ResponseBody
    public JSONResult<PageBean<UserAndRoleRespDTO>> listOrgUserInfo(
            @Valid @RequestBody OrgUserReqDTO reqDTO, BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return organizationFeignClient.listOrgUserInfo(reqDTO);
    }

    /**
     * 查询组织机构下是否有员工 (包括下级的下级)
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/queryOrgStaffByParentId")
    @ResponseBody
    public JSONResult<Boolean> queryOrgStaffByParentId(@RequestBody IdListReq idListReq) {
        return organizationFeignClient.queryOrgStaffByParentId(idListReq);
    }

    /**
     * 查询 系统下 叶子节点组织机构
     * 
     * @param reqDto system_code
     * @return
     */
    @PostMapping("/listLeafOrg")
    @ResponseBody
    JSONResult<List<OrganizationDTO>> listLeafOrg(@RequestBody OrganizationQueryDTO reqDto) {
        return organizationFeignClient.listLeafOrg(reqDto);
    }

    /**
     * 组织机构 group name 查询 列表
     * 
     * @param groupCode
     * @return
     */
    @PostMapping("/queryDictionaryItemsByGroupCode")
    @ResponseBody
    public JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode() {
        return dictionaryItemFeignClient
                .queryDicItemsByGroupCode(DicCodeEnum.ORGANIZATIONTYPE.getCode());
    }
    
    /**
     * 查询所有的商务小组
     * @param request
     * @return
     */
    @RequestMapping("/queryBusGroupList")
    @ResponseBody
    public JSONResult<List<OrganizationDTO>> queryBusGroupList(){
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
     // 商务小组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setParentId(curLoginUser.getOrgId());
        busGroupReqDTO.setOrgType(OrgTypeConstant.SWZ);
        return organizationFeignClient.listDescenDantByParentId(busGroupReqDTO);
    }
    
    /**
     * 查询所有的电销组
     * @param result
     * @return
     */
    @PostMapping("/queryTeleGroupList")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> queryTeleGroupList() {
        // 电销组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.DXZ);
        return organizationFeignClient.queryOrgByParam(busGroupReqDTO);
    }
    
    
    /**
     * 查询所有的商务组
     * @return
     */
    @PostMapping("/queryAllBusGroup")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> queryAllBusGroup(){
        OrganizationQueryDTO companyDto = new OrganizationQueryDTO();
        companyDto.setSystemCode(SystemCodeConstant.HUI_JU);
        companyDto.setOrgType(OrgTypeConstant.SWZ);
        return    organizationFeignClient.queryOrgByParam(companyDto);
    }
    
    /**
     * 查询所有的商务大区
     * @param result
     * @return
     */
    @PostMapping("/queryBusinessAreaList")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> queryBusinessAreaList() {
        // 电销组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.SWDQ);
        return organizationFeignClient.queryOrgByParam(busGroupReqDTO);
    }
    
    /**
     * 查询所有的电销事业部
     * @param result
     * @return
     */
    @PostMapping("/queryTeleDeptList")
    @ResponseBody
    public JSONResult<List<OrganizationRespDTO>> queryTeleDeptList() {
        // 电销组
        OrganizationQueryDTO busGroupReqDTO = new OrganizationQueryDTO();
        busGroupReqDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        busGroupReqDTO.setOrgType(OrgTypeConstant.DZSYB);
        return organizationFeignClient.queryOrgByParam(busGroupReqDTO);
    }
    
    

}
