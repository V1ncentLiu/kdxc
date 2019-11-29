package com.kuaidao.manageweb.feign.area;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kuaidao.aggregation.dto.assignrule.InfoAssignDTO;
import com.kuaidao.aggregation.dto.assignrule.InfoAssignQueryDTO;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.IdListReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.TreeData;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.organization.OrganizationAddAndUpdateDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;

@FeignClient(name = "sys-service", path = "/sys/area/sysregion", fallback = SysRegionFeignClient.HystrixClientFallback.class)
public interface SysRegionFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "/getproviceList")
	JSONResult<List<SysRegionDTO>> getproviceList();

	@RequestMapping(method = RequestMethod.POST, value = "/querySysRegionTree")
	JSONResult<List<TreeData>> querySysRegionTree(SysRegionDTO sysRegionDTO);
	
	 /***
     * 组织机构 数据查询 分页
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/querySysRegionDTOByParam")
    public JSONResult<PageBean<SysRegionDTO>> querySysRegionDTOByParam(
            @RequestBody SysRegionDTO queryDTO);
    
    /**
     * 查询区域 树
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/query")
    JSONResult<List<TreeData>> query();
	/**
     * 查询区域
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/queryOrgByParam")
    public JSONResult<List<SysRegionDTO>> queryOrgByParam(
            @RequestBody SysRegionDTO queryDTO);
    /**
     * 查询区域
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/querySysRegionByParam")
    public JSONResult<List<SysRegionDTO>> querySysRegionByParam(
            @RequestBody SysRegionDTO queryDTO);
    
    @PostMapping("/save")
    public JSONResult save(@RequestBody SysRegionDTO orgDTO);

    @PostMapping("/update")
    public JSONResult update(@RequestBody SysRegionDTO orgDTO);

    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListReq idListReq);
    
    /**
     * 根据ID ,查询区域信息
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/queryOrgById")
    JSONResult<SysRegionDTO> queryOrgById(@RequestBody IdEntity idEntity);
	@Component
	static class HystrixClientFallback implements SysRegionFeignClient {

		private static Logger logger = LoggerFactory.getLogger(SysRegionFeignClient.class);

		private JSONResult fallBackError(String name) {
			logger.error(name + "接口调用失败：无法获取目标服务");
			return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
					SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
		}


		@Override
		public JSONResult<List<SysRegionDTO>> getproviceList() {
			// TODO Auto-generated method stub
			return fallBackError("查询省份列表数据失败");
		}


		@Override
		public JSONResult<List<TreeData>> querySysRegionTree(SysRegionDTO sysRegionDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询树状区域列表数据失败");
		}


		@Override
		public JSONResult<PageBean<SysRegionDTO>> querySysRegionDTOByParam(SysRegionDTO queryDTO) {
			// TODO Auto-generated method stub
			return fallBackError("根据id查询其下多少个区域列表数据失败");
		}


		@Override
		public JSONResult<List<SysRegionDTO>> queryOrgByParam(SysRegionDTO queryDTO) {
			// TODO Auto-generated method stub
			return fallBackError("根据参数查询区域列表数据失败");
		}


		@Override
		public JSONResult save(SysRegionDTO orgDTO) {
			// TODO Auto-generated method stub
			return fallBackError("保存区域数据失败");
		}


		@Override
		public JSONResult update(SysRegionDTO orgDTO) {
			// TODO Auto-generated method stub
			return fallBackError("修改区域数据失败");
		}


		@Override
		public JSONResult delete(IdListReq idListReq) {
			// TODO Auto-generated method stub
			return fallBackError("删除区域数据失败");
		}


		@Override
		public JSONResult<List<TreeData>> query() {
			// TODO Auto-generated method stub
			return fallBackError("查询区域树数据失败");
		}


		@Override
		public JSONResult<SysRegionDTO> queryOrgById(IdEntity idEntity) {
			// TODO Auto-generated method stub
			return fallBackError("根据ID ,查询区域信息");
		}


		@Override
		public JSONResult<List<SysRegionDTO>> querySysRegionByParam(SysRegionDTO queryDTO) {
			// TODO Auto-generated method stub
			return fallBackError("查询区域信息失败");
		}

	}
}
