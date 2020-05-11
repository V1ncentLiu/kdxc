package com.kuaidao.manageweb.feign.ip;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.ip.IpAccessManagerQueryDTO;
import com.kuaidao.sys.dto.ip.IpPackageInfoDTO;
import com.kuaidao.sys.dto.ip.IpRepositoryInfoDTO;

@FeignClient(name = "sys-service", path = "/sys/ip/accessManager",
        fallback = IpAccessFeignClient.HystrixClientFallback.class)
public interface IpAccessFeignClient {

    /**
     * IP库分页查询
     * 
     * @param queryDTO
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/querytIpPageList")
    public JSONResult<PageBean<IpRepositoryInfoDTO>> querytIpPageList(
            @RequestBody IpAccessManagerQueryDTO queryDTO);

    /**
     * IP包分页查询
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/querytPackagePageList")
    public JSONResult<PageBean<IpPackageInfoDTO>> querytPackagePageList(
            @RequestBody IpAccessManagerQueryDTO dto);

    /**
     * 查询所有IP包供角色使用
     * 
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/querytAllPackageList")
    public JSONResult<PageBean<IpPackageInfoDTO>> querytAllPackageList();

    @RequestMapping(method = RequestMethod.POST, value = "/saveIpRepository")
    public JSONResult<String> saveIpRepository(@RequestBody IpRepositoryInfoDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/deleteIpRepository")
    public JSONResult<String> deleteIpRepository(@RequestBody IpRepositoryInfoDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/saveIpPackage")
    public JSONResult<String> saveIpPackage(@RequestBody IpPackageInfoDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/updateIpPackage")
    public JSONResult<String> updateIpPackage(@RequestBody IpPackageInfoDTO dto);

    @RequestMapping(method = RequestMethod.POST, value = "/deleteIppackage")
    public JSONResult<String> deleteIppackage(@RequestBody IpPackageInfoDTO dto);

    /**
     * IP包重名判断
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/queryIpPackageByParam")
    public JSONResult<List<IpPackageInfoDTO>> queryIpPackageByParam(IpAccessManagerQueryDTO dto);

    /**
     * 删除IP时判断是否有IP包对应
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/findListByRepository")
    public JSONResult<List<IpRepositoryInfoDTO>> findListByRepository(IpAccessManagerQueryDTO dto);

    /**
     * 修改IP包数据查询
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/findPackageById")
    public JSONResult<IpPackageInfoDTO> findPackageById(IpAccessManagerQueryDTO dto);

    /**
     * 修改IP包数据查询
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/findListByPackage")
    public JSONResult<List<IpPackageInfoDTO>> findListByPackage(IpAccessManagerQueryDTO dto);

    /**
     * 添加IP时判断当前IP是否存在
     * 
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/findListByIp")
    public JSONResult<List<IpRepositoryInfoDTO>> findListByIp(IpAccessManagerQueryDTO dto);


    @Component
    static class HystrixClientFallback implements IpAccessFeignClient {

        private static Logger logger = LoggerFactory.getLogger(IpAccessFeignClient.class);

        @SuppressWarnings("rawtypes")
        private JSONResult fallBackError(String name) {
            logger.error(name + "接口调用失败：无法获取目标服务");
            return new JSONResult().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),
                    SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<PageBean<IpPackageInfoDTO>> querytPackagePageList(
                IpAccessManagerQueryDTO queryDTO) {
            return fallBackError("查询IP包列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<PageBean<IpRepositoryInfoDTO>> querytIpPageList(
                IpAccessManagerQueryDTO queryDTO) {
            return fallBackError("查询IP库列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<String> saveIpRepository(IpRepositoryInfoDTO dto) {
            return fallBackError("新增IP库列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<String> deleteIpRepository(IpRepositoryInfoDTO dto) {
            return fallBackError("删除IP库列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<String> saveIpPackage(IpPackageInfoDTO dto) {
            return fallBackError("保存IP包列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<String> updateIpPackage(IpPackageInfoDTO dto) {
            return fallBackError("修改IP包列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<String> deleteIppackage(IpPackageInfoDTO dto) {
            return fallBackError("删除IP包列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<PageBean<IpPackageInfoDTO>> querytAllPackageList() {
            // TODO Auto-generated method stub
            return fallBackError("查询所有IP包列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<List<IpPackageInfoDTO>> findListByPackage(IpAccessManagerQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询所有IP包列表数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<List<IpPackageInfoDTO>> queryIpPackageByParam(
                IpAccessManagerQueryDTO dto) {

            return fallBackError("查询所有IP包数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<List<IpRepositoryInfoDTO>> findListByRepository(
                IpAccessManagerQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询所有IP库数据失败");
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONResult<IpPackageInfoDTO> findPackageById(IpAccessManagerQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询所有IP包数据失败");
        }

        @Override
        public JSONResult<List<IpRepositoryInfoDTO>> findListByIp(IpAccessManagerQueryDTO dto) {
            // TODO Auto-generated method stub
            return fallBackError("查询所有IP数据失败");
        }



    }
}
