/**
 *
 */
package com.kuaidao.manageweb.controller.merchant.charge;

import com.alibaba.fastjson.JSON;
import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.BussReceiveSourceTypeEnum;
import com.kuaidao.common.constant.DicCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.EmailSend;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.announcement.BusReceiveFeignClient;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.merchant.charge.ClueChargeFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.merchant.dto.charge.*;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.announcement.bussReceive.BussReceiveInsertAndUpdateDTO;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.project.ProjectDTO;
import com.kuaidao.sys.dto.user.MerchantUserReq;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author zxy
 *
 */
@Slf4j
@Controller
@RequestMapping("/merchant/clueChargeManager")
public class ClueChargeController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ClueChargeFeignClient clueChargeFeignClient;
    @Autowired
    private DictionaryItemFeignClient dictionaryItemFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private EmailSend emailSend;

    @Autowired
    private Configuration configuration;
    @Autowired
    private BusReceiveFeignClient busReceiveFeignClient;

    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    /***
     * 资源资费列表页
     *
     * @return
     */
    @RequestMapping("/initClueChargeList")
    @RequiresPermissions("merchant:clueChargeManager:view")
    public String initClueChargeList(HttpServletRequest request) {
        //资源类别
        request.setAttribute("categoryList", getDictionaryByCode(DicCodeEnum.CLUECHARGECATEGORY.getCode()));
        //资源媒介
        request.setAttribute("sourceList", getDictionaryByCode(DicCodeEnum.MEDIUM.getCode()));
        //行业类别
        request.setAttribute("industryCategoryList", getDictionaryByCode(DicCodeEnum.INDUSTRYCATEGORY.getCode()));
        // 商家账号
        List<UserInfoDTO> userList = getMerchantUser(null);
        request.setAttribute("merchantUserList",userList);
        IdListLongReq idListLongReq = new IdListLongReq();
        idListLongReq.setIdList(ListUtils.emptyIfNull(userList).stream().map(UserInfoDTO::getId).collect(Collectors.toList()));
        JSONResult<List<ProjectInfoDTO>> listJSONResult = projectInfoFeignClient.queryListByGroupId(idListLongReq);
        if(JSONResult.SUCCESS.equals(listJSONResult.getCode())){
            request.setAttribute("projectList", listJSONResult.getData());

        }

        if(CollectionUtils.isNotEmpty(userList)){
            Map<String, List<ProjectInfoDTO>> userProjectList = getUserProjectList(ListUtils.emptyIfNull(userList).stream().map(UserInfoDTO::getId).collect(Collectors.toList()));
            BeanCopier beanCopier = BeanCopier.create(ProjectInfoDTO.class, ProjectDTO.class, false);
            for (UserInfoDTO userInfoDTO : userList) {
                if(userProjectList.containsKey(userInfoDTO.getId()+"")){
                    List<ProjectInfoDTO> projectInfoDTOList = userProjectList.get(userInfoDTO.getId() + "");
                    if(CollectionUtils.isNotEmpty(projectInfoDTOList)){
                        List<ProjectDTO> list = new ArrayList<>();
                        for (ProjectInfoDTO projectInfoDTO : projectInfoDTOList) {
                            ProjectDTO projectDTO = new ProjectDTO();
                            beanCopier.copy(projectInfoDTO,projectDTO,null);
                            list.add(projectDTO);
                        }
                        userInfoDTO.setChildren(list);
                    }
                }
            }
        }

        request.setAttribute("merchantUserProjectList",userList);
        return "merchant/charge/clueChargeManagerPage";
    }

    /**
     * 查询资源资费列表
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/queryPage")
    public JSONResult<PageBean<MerchantClueChargeDTO>> queryPage(@RequestBody MerchantClueChargeReq pageParam) {
        if(pageParam.getProjectId()!=null){
            JSONResult<ProjectInfoDTO> projectInfoDTOJSONResult = projectInfoFeignClient.get(new IdEntityLong(pageParam.getProjectId()));
            if(JSONResult.SUCCESS.equals(projectInfoDTOJSONResult.getCode()) && projectInfoDTOJSONResult.getData()!=null){
                String groupId = projectInfoDTOJSONResult.getData().getGroupId();
                if(pageParam.getMainAccountId()!=null &&  pageParam.getMainAccountId().longValue()!=Long.parseLong(groupId)){
                    return new JSONResult<PageBean<MerchantClueChargeDTO>>().success(null);
                }
                if(pageParam.getMainAccountId()==null){
                    pageParam.setMainAccountId(Long.parseLong(groupId));
                }
            }
        }
        JSONResult<PageBean<MerchantClueChargeDTO>> list = clueChargeFeignClient.queryPage(pageParam);
        if(JSONResult.SUCCESS.equals(list.getCode())){
            Map<String, String> userProject = getUserProject(ListUtils.emptyIfNull(list.getData().getData()).stream().map(MerchantClueChargeDTO::getMainAccountId).collect(Collectors.toList()));
            for (MerchantClueChargeDTO merchantClueChargeDTO : list.getData().getData()) {
                if(userProject.containsKey(merchantClueChargeDTO.getMainAccountId()+"")){
                    merchantClueChargeDTO.setProjectName(userProject.get(merchantClueChargeDTO.getMainAccountId()+""));
                }
            }

        }
        return list;

    }
    /**
     * 删除资源费用
     *
     * @param idListLongReq
     * @return
     */
    @ResponseBody
    @PostMapping("/delete")
    public JSONResult<String> delete(@RequestBody IdListLongReq idListLongReq) {
        MerchantClueChargeReq merchantClueChargeReq = new MerchantClueChargeReq();
        merchantClueChargeReq.setUpdateUser(getUserId());
        merchantClueChargeReq.setUpdateTime(new Date());
        merchantClueChargeReq.setIdList(idListLongReq.getIdList());
        return clueChargeFeignClient.delete(merchantClueChargeReq);
    }

    /***
     * 资源资费列表(不分页)
     *
     * @return
     */
    @PostMapping("/listNoPage")
    @ResponseBody
    public JSONResult<List<MerchantClueChargeDTO>> listNoPage(@RequestBody MerchantClueChargePageParam merchantClueChargePageParam,
                                                              HttpServletRequest request) {

        JSONResult<List<MerchantClueChargeDTO>> list = clueChargeFeignClient.listNoPage(merchantClueChargePageParam);

        return list;
    }



    /**
     * 保存资源资费
     *
     * @param orgDTO
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/saveClueCharge")
    @ResponseBody
    @RequiresPermissions("merchant:clueChargeManager:edit")
    @LogRecord(description = "编辑资源资费", operationType = OperationType.UPDATE, menuName = MenuEnum.CLUE_CHARGE_MANAGEMENT)
    public JSONResult saveClueCharge(@Valid @RequestBody MerchantClueChargeReq merchantClueChargeReq) {
        long userId = getUserId();
        if (null == merchantClueChargeReq.getId()) {
            merchantClueChargeReq.setCreateUser(userId);

        }
        IdEntityLong idEntityLong = new IdEntityLong(merchantClueChargeReq.getId());
        JSONResult<MerchantClueChargeDTO> jsonResult =  clueChargeFeignClient.get(idEntityLong);
        if(!JSONResult.SUCCESS.equals(jsonResult.getCode())){
            return new JSONResult().fail(JSONResult.FAIL,"查询失败！");
        }
        merchantClueChargeReq.setUpdateUser(userId);
        JSONResult<String> jsonResult1 = clueChargeFeignClient.insertOrUpdate(merchantClueChargeReq);
        if(JSONResult.SUCCESS.equals(jsonResult1.getCode())){
            MerchantClueChargeDTO data = jsonResult.getData();
            if(data!=null && merchantClueChargeReq.getId()!=null && merchantClueChargeReq.getCharge().compareTo(data.getCharge())!=0){
                sendEmail(merchantClueChargeReq,data.getCharge());
            }
        }

        return  jsonResult1;
    }

    /**
     * 查询资源资费操作日志列表
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/queryLogPage")
    public JSONResult<PageBean<MerchantCleuChargeOperationLogDto>> queryPage(@RequestBody MerchantCleuChargeOperationLogReq pageParam) {
        if(StringUtils.isBlank(pageParam.getUpdateField().trim())){
            pageParam.setUpdateField(null);
        }
        return clueChargeFeignClient.queryLogPage(pageParam);
    }

    private void sendEmail(MerchantClueChargeReq merchantClueChargeReq, BigDecimal odlCharge) {
        //发送邮件
        try {
            if(merchantClueChargeReq.getId()!=null && merchantClueChargeReq.getMainAccountId()!=null){
                //根据ID查询
                JSONResult<UserInfoDTO> userInfoDTOJSONResult = userInfoFeignClient.get(new IdEntityLong(merchantClueChargeReq.getMainAccountId()));
                if(JSONResult.SUCCESS.equals(userInfoDTOJSONResult.getCode()) && userInfoDTOJSONResult.getData()!=null && StringUtils.isNotBlank(userInfoDTOJSONResult.getData().getEmail())){
                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
                    MerchantClueChargeMsg merchantClueChargeMsg = new MerchantClueChargeMsg();
                    merchantClueChargeMsg.setCharge(merchantClueChargeReq.getCharge());
                    merchantClueChargeMsg.setOldCharge(odlCharge);
                    merchantClueChargeMsg.setUserName( userInfoDTOJSONResult.getData().getName());
                    merchantClueChargeMsg.setTime(time);
                    pushMessage(merchantClueChargeReq.getMainAccountId(), "资费调整通知", JSON.toJSONString(merchantClueChargeMsg), merchantClueChargeReq.getId()+"");
                    Map<String,Object> dataMap = new HashMap<>();
                    dataMap.put("name",userInfoDTOJSONResult.getData().getName());
                    dataMap.put("charge",merchantClueChargeReq.getCharge());
                    dataMap.put("time",time);
                    String emailContent = FreeMarkerTemplateUtils.processTemplateIntoString(this.configuration.getTemplate("email/huijuMerchantCharge.html"),dataMap);
                    ExecutorService executors = Executors.newSingleThreadExecutor();
                    executors.submit(()-> {
                        emailSend.sendEmail("【招商宝】资费调整通知", emailContent, userInfoDTOJSONResult.getData().getEmail());
                    });
                }
            }
        } catch (Exception e) {
            log.error("ClueChargeController sendEmail e{}",e);
        }
    }

    public void pushMessage(Long userId, String title, String content, String remark) {
        // 发送后台推送消息
        logger.info("发送推送消息:{{}},内容{{}}", title, content);
        BussReceiveInsertAndUpdateDTO rev = new BussReceiveInsertAndUpdateDTO();
        rev.setTitle(title);
        rev.setReceiveUser(userId);
        rev.setContent(content);
        rev.setRemark(remark);
        rev.setSourceType(BussReceiveSourceTypeEnum.充值优惠.getStatus());
        JSONResult insertAndSent = busReceiveFeignClient.merchantInsert(rev);
        logger.info("发送推送消息结果:" + insertAndSent);
        Long busId = (Long) insertAndSent.getData();
        updateMerchantUser( userId,busId);

    }

    /**
     * 资费标准修改状态调整
     * @param userId
     * @param busId
     */
    private void updateMerchantUser(Long userId, Long busId) {
        MerchantUserReq merchantUserReq = new MerchantUserReq();
        merchantUserReq.setUserId(userId);
        merchantUserReq.setDeductBusId(busId);
        merchantUserInfoFeignClient.addOrUpdateMerchant(merchantUserReq);
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
     * 查询字典表
     *
     * @param code
     * @return
     */
    private List<DictionaryItemRespDTO> getDictionaryByCode(String code) {
        JSONResult<List<DictionaryItemRespDTO>> queryDicItemsByGroupCode = dictionaryItemFeignClient.queryDicItemsByGroupCode(code);
        if (queryDicItemsByGroupCode != null && JSONResult.SUCCESS.equals(queryDicItemsByGroupCode.getCode())) {
            return queryDicItemsByGroupCode.getData();
        }
        return null;
    }

    /**
     * 查询商家账号
     * @param arrayList
     * @return
     */
    private List<UserInfoDTO> getMerchantUser(List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }

    /**
     * 根据用户id  返回项目名
     * @param userIds
     * @return
     */
    private Map<String,String> getUserProject(List<Long> userIds){
        IdListLongReq idListLongReq = new IdListLongReq();
        idListLongReq.setIdList(userIds);
        JSONResult<List<ProjectInfoDTO>> listJSONResult = projectInfoFeignClient.queryListByGroupId(idListLongReq);
        List<ProjectInfoDTO> projectInfoDTOList = new ArrayList<>();
        if(listJSONResult.getCode().equals(JSONResult.SUCCESS)) {
            projectInfoDTOList =listJSONResult.data();
        }
        return  ListUtils.emptyIfNull(projectInfoDTOList).stream().
                collect(Collectors.
                        groupingBy(ProjectInfoDTO::getGroupId,
                                Collectors.mapping(ProjectInfoDTO::getProjectName,
                                        Collectors.joining("、"))));

    }

    /**
     * 根据用户id  返回项目名
     * @param userIds
     * @return
     */
    private Map<String,List<ProjectInfoDTO>> getUserProjectList(List<Long> userIds){
        IdListLongReq idListLongReq = new IdListLongReq();
        idListLongReq.setIdList(userIds);
        JSONResult<List<ProjectInfoDTO>> listJSONResult = projectInfoFeignClient.queryListByGroupId(idListLongReq);
        List<ProjectInfoDTO> projectInfoDTOList = new ArrayList<>();
        if(listJSONResult.getCode().equals(JSONResult.SUCCESS)) {
            projectInfoDTOList =listJSONResult.data();
        }
        return ListUtils.emptyIfNull(projectInfoDTOList).stream().collect(Collectors.groupingBy(ProjectInfoDTO::getGroupId));
    }
}
