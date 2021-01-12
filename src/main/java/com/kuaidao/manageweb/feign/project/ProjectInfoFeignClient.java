package com.kuaidao.manageweb.feign.project;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

/**
 * 项目
 * 
 * @author: zxy
 * @date: 2019年1月4日
 * @version V1.0
 */
@FeignClient(name = "business-config-service-wyp", path = "/businessConfig/projectInfo",
        fallback = ProjectInfoFeignClient.HystrixClientFallback.class)
public interface ProjectInfoFeignClient {
    /**
     * 根据id查询项目信息
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/get")
    public JSONResult<ProjectInfoDTO> get(@RequestBody IdEntityLong id);


    /**
     * 查询项目集合
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/list")
    public JSONResult<PageBean<ProjectInfoDTO>> list(@RequestBody ProjectInfoPageParam param);

    /**
     * 查询项目集合（不分页）
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/listNoPage")
    public JSONResult<List<ProjectInfoDTO>> listNoPage(@RequestBody ProjectInfoPageParam param);

    /**
     * 查询所有项目 adw
     * 
     * @param menuDTO
     * @return
     */
    @PostMapping("/allProject")
    public JSONResult<List<ProjectInfoDTO>> allProject();

    /**
     * 修改项目信息
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/update")
    public JSONResult update(@RequestBody ProjectInfoReq req);

    /**
     * 新增项目
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/create")
    public JSONResult<String> create(@RequestBody ProjectInfoReq req);

    /**
     * 删除项目
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/delete")
    public JSONResult delete(@RequestBody IdListLongReq idList);

    /**
     * 品牌库品类列表
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/getCategoryList")
    public JSONResult<List<CategoryDTO>> getCategoryList();

    /**
     * 品牌库列表
     * 
     * @param queryDTO
     * @return
     */
    @PostMapping("/getBrandList")
    public JSONResult<PageBean<BrandListDTO>> getBrandList(
            @RequestBody BrandListPageParam pageParam);

    /**
     * 根据签约状态查询项
     *
     * @param dto
     * @return
     */
    @PostMapping("/queryBySign")
    public JSONResult<List<ProjectInfoDTO>> queryBySign(@RequestBody ProjectInfoPageParam dto);

    @Component
    static class HystrixClientFallback implements ProjectInfoFeignClient {

        private static Logger logger = LoggerFactory.getLogger(ProjectInfoFeignClient.class);


        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @Override
        public JSONResult<ProjectInfoDTO> get(@RequestBody IdEntityLong id) {
            return fallBackError("根据id查询项目信息");
        }

        @Override
        public JSONResult update(@RequestBody ProjectInfoReq req) {
            return fallBackError("修改项目信息");
        }

        @Override
        public JSONResult<String> create(@RequestBody ProjectInfoReq req) {
            return fallBackError("新增项目");
        }

        @Override
        public JSONResult delete(@RequestBody IdListLongReq idList) {
            return fallBackError("删除项目");
        }


        @Override
        public JSONResult<PageBean<ProjectInfoDTO>> list(@RequestBody ProjectInfoPageParam param) {
            return fallBackError("查询项目集合");
        }

        @Override
        public JSONResult<List<ProjectInfoDTO>> listNoPage(
                @RequestBody ProjectInfoPageParam param) {
            return fallBackError("查询项目集合");
        }

        @Override
        public JSONResult<List<ProjectInfoDTO>> allProject() {
            return fallBackError("查询所有项目1");
        }

        @Override
        public JSONResult<List<CategoryDTO>> getCategoryList() {
            return fallBackError("查询品牌库品类列表");
        }

        @Override
        public JSONResult<PageBean<BrandListDTO>> getBrandList(
                @RequestBody BrandListPageParam pageParam) {
            return fallBackError("查询品牌库列表");
        }

        @Override
        public JSONResult<List<ProjectInfoDTO>> queryBySign(ProjectInfoPageParam dto) {
            return fallBackError("查询签约项目列表");
        }


    }


}
