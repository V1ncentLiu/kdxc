package com.kuaidao.manageweb.controller.phonetraffic;

import com.kuaidao.aggregation.dto.busmycustomer.BusMyCustomerRespDTO;
import com.kuaidao.aggregation.dto.busmycustomer.MyCustomerParamDTO;
import com.kuaidao.aggregation.dto.clue.AllocationClueReq;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficParamDTO;
import com.kuaidao.aggregation.dto.phonetraffic.PhoneTrafficRespDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.assignrule.InfoAssignFeignClient;
import com.kuaidao.manageweb.feign.clue.ClueBasicFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.phonetraffic.PhoneTrafficFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
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
import java.util.List;

/**
 * @Auther: admin
 * @Date: 2019/3/18 17:51
 * @Description:
 */
@Controller
@RequestMapping("/phonetraffic")
public class PhoneTrafficController {
    private static Logger logger = LoggerFactory.getLogger(PhoneTrafficController.class);

    @Autowired
    private ClueBasicFeignClient clueBasicFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private InfoAssignFeignClient infoAssignFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    PhoneTrafficFeignClient phoneTrafficFeignClient;

    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest request){
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        // 话务人员
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("aggregation:PhoneTraffic");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());

        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("aggregation:appiontmentManager");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());

        return "/phonetraffic/customManagement";
    }

    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean<PhoneTrafficRespDTO>> queryListPage(@RequestBody PhoneTrafficParamDTO param){
        logger.info("============分页数据查询==================");
        return  phoneTrafficFeignClient.queryList(param);
    }

    /**
     * 分配资源
     * @return
     */
    @PostMapping("/allocationClue")
    @ResponseBody
    public JSONResult allocationClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
                                     BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        return clueBasicFeignClient.allocationClue(allocationClueReq);
    }

    /**
     * 转移资源
     * @return
     */
    @PostMapping("/transferClue")
    @ResponseBody
    public JSONResult transferClue(@Valid @RequestBody AllocationClueReq allocationClueReq,
                                   BindingResult result) {

        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        // 插入当前用户、角色信息
        allocationClueReq.setUserId(user.getId());
        List<RoleInfoDTO> roleList = user.getRoleList();
        if (roleList != null) {
            allocationClueReq.setRoleId(roleList.get(0).getId());
            allocationClueReq.setRoleCode(roleList.get(0).getRoleCode());
        }
        return clueBasicFeignClient.transferClue(allocationClueReq);
    }

    /**
     * 跳转 编辑资源页面
     */
    public String toEditPage(){
        return "/phonetraffic/editCustomerMaintenance";
    }
}
