package com.kuaidao.manageweb.controller.abnormal;

import com.kuaidao.aggregation.dto.abnormal.AbnomalUserAddAndUpdateDTO;
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserQueryDTO;
import com.kuaidao.aggregation.dto.abnormal.AbnomalUserRespDTO;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.abnormal.AbnormalUserFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnReceiveFeignClient;
import com.kuaidao.manageweb.feign.announcement.AnnouncementFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.msgpush.MsgPushFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.announcement.AnnouncementAddAndUpdateDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementQueryDTO;
import com.kuaidao.sys.dto.announcement.AnnouncementRespDTO;
import com.kuaidao.sys.dto.announcement.annReceive.AnnReceiveAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryAddAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryQueryDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.role.RoleQueryDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.*;

/**
 * @Auther: admin
 * @Date: 2019/1/2 15:14
 * @Description:
 *      标记异常客户
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
    
    

    @RequestMapping("/AbnoramlType")
    @ResponseBody
    public JSONResult<List<DictionaryItemRespDTO>> AbnoramlType(){
        JSONResult result = dictionaryItemFeignClient.queryDicItemsByGroupCode( DicCodeEnum.DIC_ABNORMALUSER.getCode());
        return result;
    }

    @RequiresPermissions("AbnormalUser:add")
    @LogRecord(description = "标记异常客户新增",operationType = LogRecord.OperationType.INSERT,menuName = MenuEnum.ABNORMALUSER_MANAGENT)
    @RequestMapping("/saveOne")
    @ResponseBody
    public JSONResult insertOne(@Valid @RequestBody AbnomalUserAddAndUpdateDTO dto  , BindingResult result){
        if (result.hasErrors()) return  CommonUtil.validateParam(result);
        UserInfoDTO user = CommUtil.getCurLoginUser();
        dto.setCreateTime(new Date());
        dto.setCreateUser(user.getId());
        dto.setStatus(0);
        return  abnormalUserFeignClient.saveAbnomalUser(dto);
    }


    @RequiresPermissions("AbnormalUser:delete")
    @LogRecord(description = "标记异常客户-删除",operationType = LogRecord.OperationType.DELETE,menuName = MenuEnum.ABNORMALUSER_MANAGENT)
    @RequestMapping("/deleteAbnoramlUser")
    @ResponseBody
    public JSONResult deleteAbnoramlUser(@RequestBody Map map){
        return abnormalUserFeignClient.deleteAbnomalUsers((List<Long>)map.get("ids"));
    }


    @RequiresPermissions("AbnormalUser:view")
    @PostMapping("/queryAbnoramlUsers")
    @ResponseBody
    public JSONResult<PageBean<AbnomalUserRespDTO>> queryAbnoramlUsers(@RequestBody AbnomalUserQueryDTO dto){
        logger.info("====================列表查询==================");
        Date date1 = dto.getTime1();
        Date date2 = dto.getTime2();
        if(date1!=null && date2!=null ){
            if(date1.getTime()>date2.getTime()){
                return new JSONResult().fail("-1","时间选项，开始时间大于结束时间!");
            }
        }
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        List dxList = new ArrayList();
        /**
         * 数据权限说明：
         *    管理员权限：
         *      能够看见全部数据
         *    电销顾问：
         *      能够看见自己以及所在电销组下电销创业顾问创建的数据
         *    其他的只能够看见自己创建的数据
         */
        if(roleList!=null&&roleList.get(0)!=null){
            if(RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())){
            }else if(RoleCodeEnum.DXZJ.name().equals(roleList.get(0).getRoleCode())){
                dxList = dxcygws();
                dxList.add(user.getId());
                dto.setCreateUserList(dxList);
            }else{
                dxList.add(user.getId());
                dto.setCreateUserList(dxList);
            }
        }
        JSONResult<PageBean<AbnomalUserRespDTO>> resList = abnormalUserFeignClient.queryAbnomalUserList(dto);

//      数据转换
        List<AbnomalUserRespDTO> resdata = resList.getData().getData();
        Map<String, String> dicMap = DicMap();
        Map<Long, String> vuserMap = userMap();
        List<AbnomalUserRespDTO> list2 = new ArrayList<>();
        for(int i = 0 ; i < resdata.size() ; i++){
            AbnomalUserRespDTO tempDto = resdata.get(i);
            tempDto.setCreateUserName(vuserMap.get(tempDto.getCreateUser()));
            tempDto.setTypeName(dicMap.get(tempDto.getType()));
            list2.add(tempDto);
        }
        resList.getData().setData(list2);
        return resList;
    }

    @RequestMapping("/abnormalUserPage")
    public String pageIndex(){
        logger.info("====================跳转列表页面==================");
        return "abnormal/abnormalUserList";
    }

    private List dxcygws(){
        RoleQueryDTO query = new RoleQueryDTO();
        query.setRoleCode(RoleCodeEnum.DXCYGW.name());
        UserInfoDTO user =  CommUtil.getCurLoginUser();
        JSONResult<List<RoleInfoDTO>> roleJson = roleManagerFeignClient.qeuryRoleByName(query);
        ArrayList resList = new ArrayList();
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
                    List<UserInfoDTO> userList = pageList.getData();
                    for(UserInfoDTO dto:userList){
                        resList.add(dto.getId());
                    }
                }
            }
        }
        return resList;
    }

    private Map<String,String> DicMap(){
        JSONResult<List<DictionaryItemRespDTO>> res = AbnoramlType();
        Map map = new HashMap();
        if(JSONResult.SUCCESS.equals(res.getCode())){
            List<DictionaryItemRespDTO> data = res.getData();
            for(DictionaryItemRespDTO dto:data){
                map.put(dto.getValue(),dto.getName());
            }
        }
        return map;
    }

    private Map<Long,String> userMap(){
        UserInfoPageParam param = new UserInfoPageParam();
        param.setPageNum(1);
        param.setPageSize(99999);
        JSONResult<PageBean<UserInfoDTO>> userlist = userInfoFeignClient.list(param);
        Map map = new HashMap();
        if(JSONResult.SUCCESS.equals(userlist.getCode())){
            List<UserInfoDTO> userData = userlist.getData().getData();
            for(UserInfoDTO dto:userData){
                map.put(dto.getId(),dto.getUsername());
            }
        }
        return map;
    }

}
