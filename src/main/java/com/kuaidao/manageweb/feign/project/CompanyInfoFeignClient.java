package com.kuaidao.manageweb.feign.project;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.aggregation.dto.project.CompanyInfoPageParam;
import com.kuaidao.aggregation.dto.project.CompanyInfoReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;

/**
 * 公司
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "aggregation-service", path = "/aggregation/companyInfo",
        fallback = CompanyInfoFeignClient.HystrixClientFallback.class)
public interface CompanyInfoFeignClient {
    /**
     * 根据id查询公司信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/get")
    public JSONResult<CompanyInfoDTO> get(@RequestBody IdEntityLong id);

    /**
     * 根据id查询公司信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListLongReq idList);

    /**
     * 查询公司集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<CompanyInfoDTO>> list(@RequestBody CompanyInfoPageParam param);

    /**
     * 查询公司集合.
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/allCompany")
    public JSONResult<List<CompanyInfoDTO>> allCompany();
    
    /**
     * 查询集团
     * @return
     */
    @PostMapping("/getCompanyList")
    public JSONResult<List<CompanyInfoDTO>> getCompanyList();
    

    /**
     * 修改公司信息
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/update")
    public JSONResult<String> update(@RequestBody CompanyInfoReq req);

    /**
     * 新增公司
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult<String> create(@RequestBody CompanyInfoReq req);

    /**
     * 角色列表
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/roleList")
    public JSONResult<List<RoleInfoDTO>> roleList(@RequestBody RoleQueryDTO roleQueryDTO);


    @PostMapping("/getCompanyListByParam")
    public JSONResult<List<CompanyInfoDTO>> getCompanyListByParam(@RequestBody CompanyInfoPageParam param);


    @Component
    static class HystrixClientFallback implements CompanyInfoFeignClient {

        private static Logger logger = LoggerFactory.getLogger(CompanyInfoFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<CompanyInfoDTO> get(@RequestBody IdEntityLong id) {
            return fallBackError("根据id查询公司信息");
        }

        @Override
        public JSONResult delete(@RequestBody IdListLongReq idList) {
            return fallBackError("删除公司信息");
        }


        @Override
        public JSONResult<String> update(@RequestBody CompanyInfoReq req) {
            return fallBackError("修改公司信息");
        }

        @Override
        public JSONResult<String> create(@RequestBody CompanyInfoReq req) {
            return fallBackError("新增公司");
        }


        @Override
        public JSONResult<PageBean<CompanyInfoDTO>> list(@RequestBody CompanyInfoPageParam param) {
            return fallBackError("查询公司集合");
        }

        @Override
        public JSONResult<List<CompanyInfoDTO>> allCompany() {
            return fallBackError("查询公司集合.");
        }

        @Override
        public JSONResult<List<RoleInfoDTO>> roleList(@RequestBody RoleQueryDTO roleQueryDTO) {
            return fallBackError("查询角色列表");
        }

        @Override
        public JSONResult<List<CompanyInfoDTO>> getCompanyListByParam(CompanyInfoPageParam param) {
            return fallBackError("根据条件查询公司集合");
        }

        @Override
		public JSONResult<List<CompanyInfoDTO>> getCompanyList() {
			// TODO Auto-generated method stub
			return fallBackError("查询集团列表失败");
		}



    }


}
