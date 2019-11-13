package com.kuaidao.manageweb.controller.abnormal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
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
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserAddAndUpdateDTO;
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserQueryDTO;
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserRespDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.abnormal.AbnormalUserFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

/**
 * @author yangbiao
 * @Date: 2019/1/2 15:14
 * @Description: 标记异常客户
 */

@Controller
@RequestMapping("/abnoramluser")
public class AbnormalController {

    private static Logger logger = LoggerFactory.getLogger(AbnormalController.class);

    @Autowired
    AbnormalUserFeignClient abnormalUserFeignClient;

    @Autowired
    DictionaryItemFeignClient dictionaryItemFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    RoleManagerFeignClient roleManagerFeignClient;


    /**
     * 获取数据字典异常类型
     * 
     * @return
     */
    @RequestMapping("/AbnoramlType")
    @ResponseBody
    public JSONResult<List<DictionaryItemRespDTO>> abnoramlType() {
        JSONResult result = dictionaryItemFeignClient
                .queryDicItemsByGroupCode(DicCodeEnum.DIC_ABNORMALUSER.getCode());
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        String roleCode=curLoginUser.getRoleList().get(0).getRoleCode();
        if(!roleCode.equals(RoleCodeEnum.GLY.name())){
            List<DictionaryItemRespDTO> list = (List<DictionaryItemRespDTO>) result.getData();
            list.removeIf(dictionaryItemRespDTO -> dictionaryItemRespDTO.getName().equals("黑名单"));
        }
        return result;
    }

    /**
     * 标记异常客户新增
     * 
     * @param dto
     * @param result
     * @return
     */
    @RequiresPermissions("AbnormalUser:add")
    @LogRecord(description = "标记异常客户新增", operationType = LogRecord.OperationType.INSERT,
            menuName = MenuEnum.ABNORMALUSER_MANAGENT)
    @RequestMapping("/saveOne")
    @ResponseBody
    public JSONResult insertOne(@Valid @RequestBody AbnomalUserAddAndUpdateDTO dto,
            BindingResult result) {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateTime(new Date());
        dto.setCreateUser(user.getId());
        dto.setStatus(0);
        return abnormalUserFeignClient.saveAbnomalUser(dto);
    }


    /**
     * 标记异常客户删除
     * 
     * @param map
     * @return
     */
    @RequiresPermissions("AbnormalUser:delete")
    @LogRecord(description = "标记异常客户-删除", operationType = LogRecord.OperationType.DELETE,
            menuName = MenuEnum.ABNORMALUSER_MANAGENT)
    @RequestMapping("/deleteAbnoramlUser")
    @ResponseBody
    public JSONResult deleteAbnoramlUser(@RequestBody Map map) {
        return abnormalUserFeignClient.deleteAbnomalUsers((List<Long>) map.get("ids"));
    }


    /**
     * 异常客户分页查询
     * 
     * @param dto
     * @return
     */
    @RequiresPermissions("AbnormalUser:view")
    @PostMapping("/queryAbnoramlUsers")
    @ResponseBody
    public JSONResult<PageBean<AbnomalUserRespDTO>> queryAbnoramlUsers(
            @RequestBody AbnomalUserQueryDTO dto) {
        logger.info("====================列表查询==================");

        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        List<Long> dxList = new ArrayList();
        /**
         * 数据权限说明： 管理员权限： 能够看见全部数据 电销顾问： 能够看见自己以及所在电销组下电销创业顾问创建的数据 其他的只能够看见自己创建的数据
         */
        if (roleList != null && roleList.get(0) != null) {
            if (RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
            } else if (RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())) {
                dxList = dxcygws();
                dxList.add(user.getId());
                dto.setCreateUserList(dxList);
            } else {
                dxList.add(user.getId());
                dto.setCreateUserList(dxList);
            }
        }

        Date date1 = dto.getTime1();
        Date date2 = dto.getTime2();
        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                return new JSONResult().fail("-1", "创建时间，开始时间大于结束时间!");
            }
        }

        JSONResult<PageBean<AbnomalUserRespDTO>> resList =
                abnormalUserFeignClient.queryAbnomalUserList(dto);
        return resList;
    }

    /**
     * 跳转标记异常客户页面
     * 
     * @return
     */
    @RequestMapping("/abnormalUserPage")
    public String pageIndex() {
        logger.info("====================跳转列表页面==================");
        return "abnormal/abnormalUserList";
    }

    /**
     * 组内电销顾问查询
     * 
     * @return
     */
    private List dxcygws() {
        UserInfoDTO user = CommUtil.getCurLoginUser();
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(user.getOrgId());
        userOrgRoleReq.setRoleCode(RoleCodeEnum.DXCYGW.name());
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        List<Long> list = new ArrayList<>();
        if(JSONResult.SUCCESS.equals(listByOrgAndRole.getCode())){
            List<UserInfoDTO> data = listByOrgAndRole.getData();
            if(data!=null&&data.size()>0){
                for(UserInfoDTO infoDTO:data){
                    list.add(infoDTO.getId());
                }
            }
        }
        return list;
    }

    /**
     * 数据映射map
     * 
     * @return
     */
    private Map<String, String> dicMap() {
        JSONResult<List<DictionaryItemRespDTO>> res = abnoramlType();
        Map map = new HashMap(10);
        if (JSONResult.SUCCESS.equals(res.getCode())) {
            List<DictionaryItemRespDTO> data = res.getData();
            for (DictionaryItemRespDTO dto : data) {
                map.put(dto.getValue(), dto.getName());
            }
        }
        return map;
    }

    /**
     * 数据映射map
     * 
     * @return
     */
    private Map<Long, String> userMap() {
        UserInfoPageParam param = new UserInfoPageParam();
        param.setPageNum(1);
        param.setPageSize(99999);
        JSONResult<PageBean<UserInfoDTO>> userlist = userInfoFeignClient.list(param);
        Map map = new HashMap(10);
        if (JSONResult.SUCCESS.equals(userlist.getCode())) {
            List<UserInfoDTO> userData = userlist.getData().getData();
            for (UserInfoDTO dto : userData) {
                map.put(dto.getId(), dto.getUsername());
            }
        }
        return map;
    }

}
