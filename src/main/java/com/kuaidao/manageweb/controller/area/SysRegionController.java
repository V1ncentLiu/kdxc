package com.kuaidao.manageweb.controller.area;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
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
import com.kuaidao.manageweb.controller.module.ModuleManagerController;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.module.ModuleQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;

/**
* 区域管理
* Created  on 2019-1-30 11:38:57
*/
@Controller
@RequestMapping("/area/sysregion")
public class SysRegionController {
	
	private static Logger logger = LoggerFactory.getLogger(SysRegionController.class);
	@Autowired
	SysRegionFeignClient sysRegionFeignClient ;
	/***
	 * 初始化菜单管理页面
	 * 
	 * @return
	 */
	@RequestMapping("/sysRegionManager")
	public String querySysRegionTree(HttpServletRequest request) {
		SysRegionDTO dto = new SysRegionDTO();
		JSONResult<List<TreeData>> treeJsonRes = sysRegionFeignClient.querySysRegionTree(dto);
		if (treeJsonRes != null && JSONResult.SUCCESS.equals(treeJsonRes.getCode()) && treeJsonRes.getData() != null) {
			request.setAttribute("orgData", treeJsonRes.getData());
		} else {
			logger.error("query module tree,res{{}}", treeJsonRes);
		}
		return "area/sysRegionManager";
	}
	
	/**
     * 分页 查询组织信息 
     * @param pageNum
     * @param pageSize
     * @param queryDTO
     * @return
     */
    @PostMapping("/querySysRegionDTOByParam")
    @ResponseBody
    public JSONResult<PageBean<SysRegionDTO>> queryOrgDataByParam(
            @RequestBody SysRegionDTO queryDTO) {
        return sysRegionFeignClient.querySysRegionDTOByParam(queryDTO);
    }
    /**
     * 查询区域名称，父级ID
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryOrgByParam")
    @ResponseBody
    public JSONResult queryOrgByParam(
            @RequestBody SysRegionDTO queryDTO) {
        JSONResult<List<SysRegionDTO>> orgList = sysRegionFeignClient.queryOrgByParam(queryDTO);
        if(orgList!=null && JSONResult.SUCCESS.equals(orgList.getCode())) {
            List<SysRegionDTO> data = orgList.getData();
            if(data!=null &&data.size()!=0) {
               return new JSONResult<Boolean>().success(true);
            }
        }else {
            return new JSONResult<>().fail(orgList.getCode(), orgList.getMsg());
        }
        return new JSONResult().success(false) ;
    }
    
    /**
     * 查询区域名称
     * @param queryDTO
     * @return
     */
    @PostMapping("/querySysRegionByParam")
    @ResponseBody
    public JSONResult<List<SysRegionDTO>> querySysRegionByParam(
            @RequestBody SysRegionDTO queryDTO) {
        JSONResult<List<SysRegionDTO>> orgList = sysRegionFeignClient.querySysRegionByParam(queryDTO);
        return new JSONResult().success(orgList) ;
    }
    
    /**
     * 查询组织机构树
     * @return
     */
    @PostMapping("/query")
    @ResponseBody
    public JSONResult<List<TreeData>> query(){
        return  sysRegionFeignClient.query();
    }
    /**
     * 保存或更新组织机构信息
     * @param orgDTO
     * @param result
     * @return
     * @throws Exception
     */
    @PostMapping("/save")
    @ResponseBody
    @LogRecord(description="添加区域信息",operationType=OperationType.INSERT,menuName=MenuEnum.AREA_MANAGEMENT)
    public JSONResult save(@Valid @RequestBody SysRegionDTO orgDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return sysRegionFeignClient.save(orgDTO);

    }


    /**
     *   更新组织机构
     * @param orgDTO
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    @LogRecord(description="修改区域信息",operationType=OperationType.UPDATE,menuName=MenuEnum.AREA_MANAGEMENT)
    public JSONResult update(@Valid @RequestBody SysRegionDTO orgDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        return sysRegionFeignClient.update(orgDTO);
    }


    /**
     *   删除组织机构
     * @param orgDTO
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    @LogRecord(description="删除区域信息",operationType=OperationType.DELETE,menuName=MenuEnum.AREA_MANAGEMENT)
    public JSONResult delete(@RequestBody IdListReq idListReq) {
        List<String> idList = idListReq.getIdList();
        if(idList==null || idList.size()==0) {
            return new JSONResult().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(), SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage()); 
        }
        logger.info("delete organization by id{{}}", idList);
        return sysRegionFeignClient.delete(idListReq);
    }
    
    /**
     * 根据Id 查询区域信息
     * @param idListReq  
     * @return
     */
    @PostMapping("/queryOrgById")
    @ResponseBody
    public JSONResult<SysRegionDTO> queryOrgById(@RequestBody IdEntity idEntity){
       return sysRegionFeignClient.queryOrgById(idEntity);
      
    }
}


