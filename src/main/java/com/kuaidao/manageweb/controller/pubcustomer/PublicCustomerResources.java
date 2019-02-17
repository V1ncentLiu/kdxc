package com.kuaidao.manageweb.controller.pubcustomer;

import com.kuaidao.aggregation.dto.pubcusres.ClueQueryParamDTO;
import com.kuaidao.aggregation.dto.pubcusres.PublicCustomerResourcesReqDTO;
import com.kuaidao.aggregation.dto.pubcusres.PublicCustomerResourcesRespDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.publiccustomer.PublicCustomerFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author  yangbiao
 * @Date: 2019/2/11 15:08
 * @Description:
 *      公共客户资源
 */
@Controller
@RequestMapping("/aggregation/publiccustomer")
public class PublicCustomerResources {

    private static Logger logger = LoggerFactory.getLogger(PublicCustomerResources.class);

    @Autowired
    private  DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    RoleManagerFeignClient roleManagerFeignClient;

    @Autowired
    PublicCustomerFeignClient publicCustomerFeignClient;


    /**
     * 分配资源
     */
    @PostMapping("/allocationResource")
    @ResponseBody
    public JSONResult<Boolean>   allocationResource(@RequestBody PublicCustomerResourcesReqDTO dto){
        return publicCustomerFeignClient.allocationResource(dto);
    }

    /**
     * 转移资源
     */
    @PostMapping("/transferOfResource")
    @ResponseBody
    public JSONResult<Boolean>   transferOfResource(@RequestBody PublicCustomerResourcesReqDTO dto){
        return publicCustomerFeignClient.transferOfResource(dto);
    }

    /**
     * 释放记录
     */
    @PostMapping("/releaseRecord")
    @ResponseBody
    public JSONResult<PageBean> releaseRecord(@RequestBody PublicCustomerResourcesReqDTO dto){
        return null;
    }

    /**
     * 资源还原
     */
    @PostMapping("/resourceReduction")
    @ResponseBody
    public JSONResult<Boolean> resourceReduction(@RequestBody PublicCustomerResourcesReqDTO dto){
        return publicCustomerFeignClient.resourceReduction(dto);
    }
    /**
     * 分页查询
     */
    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request){
        logger.info("------------ 公共客户资源列表 ---------------");
        OrganizationQueryDTO orgDto = new OrganizationQueryDTO();
        orgDto.setOrgType(OrgTypeConstant.DXZ);
        //电销组
        JSONResult<List<OrganizationRespDTO>> dzList = organizationFeignClient.queryOrgByParam(orgDto);
        request.setAttribute("dzList", dzList.getData());
        request.setAttribute("dxgwList", dxcygws());
        request.setAttribute("dxzjList", dxzjs());

        return "pubcustomer/publicCustomer";
    }

    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<PublicCustomerResourcesRespDTO>> queryListPage(@RequestBody ClueQueryParamDTO dto){
        Date date1 = dto.getCreateTime1();
        Date date2 = dto.getCreateTime2();
        if(date1!=null && date2!=null ){
            if(date1.getTime()>date2.getTime()){
                return new JSONResult().fail("-1","创建时间，开始时间大于结束时间!");
            }
        }

        Date date3 = dto.getReleaseTime1();
        Date date4 = dto.getReleaseTime2();
        if(date3!=null && date4!=null ){
            if(date3.getTime()>date4.getTime()){
                return new JSONResult().fail("-1","释放时间，开始时间大于结束时间!");
            }
        }
        return publicCustomerFeignClient.queryListPage(dto);
    }

    /**
     * 重复手机号
     */
    @PostMapping("/repeatPhones")
    @ResponseBody
    public JSONResult<PageBean> repeatPhones(){
        return null;
    }

    /**
     * 跟进记录
     */
    @PostMapping("/followUpRecord")
    @ResponseBody
    public JSONResult<PageBean> followUpRecord(){
        return null;
    }


    private List dxcygws(){
        RoleQueryDTO query = new RoleQueryDTO();
        query.setRoleCode(RoleCodeEnum.DXCYGW.name());
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
        List<UserInfoDTO> resList = new ArrayList();
        if (JSONResult.SUCCESS.equals(roleJson.getCode())) {
            List<RoleInfoDTO> roleList = roleJson.getData();
            if (null != roleList && roleList.size() > 0) {
                RoleInfoDTO roleDto = roleList.get(0);
                UserInfoPageParam param = new UserInfoPageParam();
                param.setRoleId(roleDto.getId());
                param.setOrgId(user.getOrgId());
                param.setPageSize(10000);
                param.setPageNum(1);
                JSONResult<PageBean<UserInfoDTO>> userListJson = userInfoFeignClient.list(param);
                if (JSONResult.SUCCESS.equals(userListJson.getCode())) {
                    PageBean<UserInfoDTO> pageList = userListJson.getData();
                    resList = pageList.getData();
                }
            }
        }
        return resList;
    }


    private List dxzjs(){
        // 这个查询是不对的啊
        RoleQueryDTO query = new RoleQueryDTO();
        query.setRoleCode(RoleCodeEnum.DXZJ.name());
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
        List<UserInfoDTO> resList = new ArrayList();
        if (JSONResult.SUCCESS.equals(roleJson.getCode())) {
            List<RoleInfoDTO> roleList = roleJson.getData();
            if (null != roleList && roleList.size() > 0) {
                RoleInfoDTO roleDto = roleList.get(0);
                UserInfoPageParam param = new UserInfoPageParam();
                param.setRoleId(roleDto.getId());
                param.setOrgId(user.getOrgId());
                param.setPageSize(10000);
                param.setPageNum(1);
                JSONResult<PageBean<UserInfoDTO>> userListJson = userInfoFeignClient.list(param);
                if (JSONResult.SUCCESS.equals(userListJson.getCode())) {
                    PageBean<UserInfoDTO> pageList = userListJson.getData();
                    resList = pageList.getData();
                }
            }
        }
        return resList;
    }
}
