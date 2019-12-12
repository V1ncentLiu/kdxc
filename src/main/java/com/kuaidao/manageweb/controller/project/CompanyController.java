/**
 * 
 */
package com.kuaidao.manageweb.controller.project;

import com.kuaidao.aggregation.dto.project.CompanyInfoDTO;
import com.kuaidao.aggregation.dto.project.CompanyInfoPageParam;
import com.kuaidao.aggregation.dto.project.CompanyInfoReq;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.CompanyInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    /***
     * 公司列表页
     * 
     * @return
     */
    @RequestMapping("/initCompanyList")
    @RequiresPermissions("aggregation:companyManager:view")
    public String initCompanyList(HttpServletRequest request) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoDTO.setStatusList(null);
        List<UserInfoDTO> userInfoList = getMerchantUser(userInfoDTO);
        request.setAttribute("userInfoList",userInfoList);

        //新增修改页面使用，排除禁用商家
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        UserInfoDTO userInfoAddDTO = new UserInfoDTO();
        userInfoAddDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoAddDTO.setStatusList(statusList);
        List<UserInfoDTO> userInfoAddList = getMerchantUser(userInfoAddDTO);
        request.setAttribute("userInfoAddList",userInfoAddList);
        return "project/companyManagerPage";
    }

    /***
     * 查询公司详情
     * 
     * @return
     */
    @RequestMapping("/getCompany")
    @ResponseBody
    @RequiresPermissions("aggregation:companyManager:view")
    public JSONResult<CompanyInfoDTO> getCompany(@RequestBody IdEntityLong id,
            HttpServletRequest request) {
        // 查询公司信息
        return companyInfoFeignClient.get(id);
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
    public JSONResult<List<CompanyInfoDTO>> listNoPage(
            @RequestBody CompanyInfoPageParam companyInfoPageParam, HttpServletRequest request) {

        JSONResult<List<CompanyInfoDTO>> list = companyInfoFeignClient.allCompany();

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
        long userId = getUserId();
        companyInfoReq.setCreateUser(userId);
        companyInfoReq.setUpdateUser(userId);
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
        long userId = getUserId();
        companyInfoReq.setUpdateUser(userId);
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
    @RequiresPermissions("aggregation:companyManager:delete")
    @LogRecord(description = "删除公司信息", operationType = OperationType.DELETE,
            menuName = MenuEnum.COMPANY_MANAGEMENT)
    public JSONResult deleteCompany(@RequestBody IdListLongReq idList) {

        return companyInfoFeignClient.delete(idList);
    }

    /**
     * 获取当前登录账号ID
     * 
     * @param orgDTO
     * @return
     */
    private long getUserId() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user.getId();
    }
    /**
     * @Description 查询商家账号
     * @param userInfoDTO
     * @Return java.util.List<com.kuaidao.sys.dto.user.UserInfoDTO>
     * @Author xuyunfeng
     * @Date 2019/10/15 17:19
     **/
    private List<UserInfoDTO> getMerchantUser(UserInfoDTO userInfoDTO) {
        JSONResult<List<UserInfoDTO>> merchantUserList =
                merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        List<UserInfoDTO> userInfoDTOList = merchantUserList.getData();
        if(userInfoDTOList != null & userInfoDTOList.size() >0){
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            userInfoDTOList.sort((a1,a2)->{
                try {
                    return df.parse(sdf.format(a2.getCreateTime())).compareTo(df.parse(sdf.format(a1.getCreateTime())));
                }catch (Exception e){
                    e.printStackTrace();
                }
                return 1;
            });
        }
        return userInfoDTOList;
    }
}
