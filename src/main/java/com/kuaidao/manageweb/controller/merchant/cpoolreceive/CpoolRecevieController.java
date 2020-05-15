package com.kuaidao.manageweb.controller.merchant.cpoolreceive;

import com.kuaidao.businessconfig.dto.project.ProjectInfoDTO;
import com.kuaidao.businessconfig.dto.project.ProjectInfoPageParam;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.feign.dictionary.DictionaryItemFeignClient;
import com.kuaidao.manageweb.feign.merchant.cpoolrecevie.CpoolRecevieFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleInsertOrUpdateDTO;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleReqDTO;
import com.kuaidao.merchant.dto.cpoolreceiverule.CpoolReceivelRuleRespDTO;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.dictionary.DictionaryItemRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: admin
 * @Date: 2019/9/6 11:36
 * @Description:
 */
@Controller
@RequestMapping("/merchant/recevieRule")
public class CpoolRecevieController {
  private static Logger logger = LoggerFactory.getLogger(CpoolRecevieController.class);

  @Autowired
  private CpoolRecevieFeignClient cpoolRecevieFeignClient;
  @Autowired
  private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
  @Autowired
  DictionaryItemFeignClient dictionaryItemFeignClient;
  @Autowired
  ProjectInfoFeignClient projectInfoFeignClient;

  /**
   * 设置商家
   */
  private void setMerchant(HttpServletRequest request,String type){
    UserInfoDTO infoParam = new UserInfoDTO();
    infoParam.setUserType(SysConstant.USER_TYPE_TWO);
    if(StringUtils.isNotBlank(type)&& StringUtils.equals("addOrUpdate",type)){
      List<Integer> statusIdList = new ArrayList<>();
      statusIdList.add(SysConstant.USER_STATUS_ENABLE);
      statusIdList.add(SysConstant.USER_STATUS_LOCK);
      infoParam.setStatusList(statusIdList);
    }
    JSONResult<List<UserInfoDTO>> listJSONResult = merchantUserInfoFeignClient.merchantUserList(infoParam);
    if(JSONResult.SUCCESS.equals(listJSONResult.getCode())){
      if(CollectionUtils.isNotEmpty(listJSONResult.getData())){
        request.setAttribute("merchantNames",listJSONResult.getData());
      }
    }
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

  private void projectList(HttpServletRequest request){
    ProjectInfoPageParam param = new ProjectInfoPageParam();
    JSONResult<List<ProjectInfoDTO>> listJSONResult = projectInfoFeignClient.listNoPage(param);
    if(JSONResult.SUCCESS.equals(listJSONResult.getCode())){
      if(CollectionUtils.isNotEmpty(listJSONResult.getData())){
        request.setAttribute("projectList", listJSONResult.getData());
      }
    }
  }
  /**
   * 跳转：list页面
   */
  @RequestMapping("/toList")
  public String toList( HttpServletRequest request) {
    // 获取全部商家名称
    setMerchant(request,null);
    projectList(request);
    // 查询字典类别集合
    request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
    // 查询字典类别集合
    request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
    return "merchant/getResourceSetting/getResourceSetting";
  }
  /**
   * 跳转：add页面
   */
  @RequestMapping("/toAdd")
  public String toAdd( HttpServletRequest request) {
    String type ="addOrUpdate";
    setMerchant(request,type);
    projectList(request);
    // 查询字典类别集合
    request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
    // 查询字典类别集合
    request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
    return "merchant/getResourceSetting/addGetResourceSetting";
  }
  /**
   * 跳转：update页面
   */
  @RequestMapping("/toUpdate")
  public String toUpdate(HttpServletRequest request) {
    String type ="addOrUpdate";
    setMerchant(request,type);
    projectList(request);
    // 查询字典类别集合
    request.setAttribute("clueCategoryList", getDictionaryByCode(Constants.CLUE_CATEGORY));
    // 查询字典类别集合
    request.setAttribute("clueTypeList", getDictionaryByCode(Constants.CLUE_TYPE));
    IdEntityLong idEntity = new IdEntityLong();
    String id = request.getParameter("id");
    idEntity.setId(Long.valueOf(id));
    JSONResult<CpoolReceivelRuleRespDTO> josnResult = cpoolRecevieFeignClient.getbyId(idEntity);
    if(JSONResult.SUCCESS.equals(josnResult.getCode())){
      CpoolReceivelRuleRespDTO data = josnResult.getData();
      data.setId(idEntity.getId());
      request.setAttribute("cpoolReceiveRule",data);
    }
    return "merchant/getResourceSetting/updateGetResourceSetting";
  }
  /**
   * 创建共有池领取规则
   */
  @ResponseBody
  @PostMapping("/create")
  public JSONResult<Long> create(@RequestBody CpoolReceivelRuleInsertOrUpdateDTO cpoolReceivelRule
     ) throws NoSuchAlgorithmException {
    UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
    cpoolReceivelRule.setCreateTime(new java.util.Date());
    cpoolReceivelRule.setCreateUser(curLoginUser.getId());
    if(StringUtils.isBlank(cpoolReceivelRule.getSource())){
      cpoolReceivelRule.setSource(Constants.NO_LIMIT);
    }
    if(StringUtils.isBlank(cpoolReceivelRule.getSourceType())){
      cpoolReceivelRule.setSourceType(Constants.NO_LIMIT);
    }
    if(StringUtils.isBlank(cpoolReceivelRule.getProjectId())){
      cpoolReceivelRule.setProjectId(Constants.NO_LIMIT);
    }
    logger.info("创建参数{}",cpoolReceivelRule);
    JSONResult<Long> josnResult = cpoolRecevieFeignClient.create(cpoolReceivelRule);
    return josnResult;
  }

  /**
   * 修改共有池领取规则
   */
  @ResponseBody
  @PostMapping("/update")
  public JSONResult<String> update(@RequestBody CpoolReceivelRuleInsertOrUpdateDTO cpoolReceivelRule) throws NoSuchAlgorithmException {
    logger.info("更新参数{}",cpoolReceivelRule);
    UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
    cpoolReceivelRule.setUpdateTime(new java.util.Date());
    cpoolReceivelRule.setUpdateUser(curLoginUser.getId());
    if(StringUtils.isBlank(cpoolReceivelRule.getSource())){
      cpoolReceivelRule.setSource("-1");
    }
    if(StringUtils.isBlank(cpoolReceivelRule.getSourceType())){
      cpoolReceivelRule.setSourceType("-1");
    }
    if(StringUtils.isBlank(cpoolReceivelRule.getProjectId())){
      cpoolReceivelRule.setProjectId(Constants.NO_LIMIT);
    }
    JSONResult<String> josnResult =
        cpoolRecevieFeignClient.update(cpoolReceivelRule);
    return josnResult;
  }

  /**
   * 删除共有池领取规则
   */
  @ResponseBody
  @PostMapping("/delete")
  public JSONResult<String> delete(@RequestBody IdListLongReq idList) {
    logger.info("删除参数{}",idList);
    JSONResult<String> delete = cpoolRecevieFeignClient.delete(idList);
    return delete;
  }


  /**
   * 查询共有池分配规则
   * @return
   */
  @PostMapping("/get")
  public JSONResult<CpoolReceivelRuleRespDTO> getbyId(@RequestBody IdEntityLong idEntity) {
    logger.info("查询参数{}",idEntity);
    JSONResult<CpoolReceivelRuleRespDTO> josnResult = cpoolRecevieFeignClient.getbyId(idEntity);
    return josnResult;
  }

  /**
   * 更新状态接口
   */
  @ResponseBody
  @PostMapping("/updateStatus")
  public JSONResult<String> updateStatus(@RequestBody CpoolReceivelRuleReqDTO param) throws NoSuchAlgorithmException {
    logger.info("更新状态参数{}",param);
    UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
    param.setUpdateTime(new java.util.Date());
    param.setUpdateUser(curLoginUser.getId());
    JSONResult<String> stringJSONResult = cpoolRecevieFeignClient.updateStatus(param);
    return stringJSONResult;
  }

  /**
   * 查询共有池分配规则列表
   * @return
   */
  @ResponseBody
  @PostMapping("/list")
  public JSONResult<PageBean<CpoolReceivelRuleRespDTO>> list(@RequestBody CpoolReceivelRuleReqDTO pageParam) {
    logger.info("列表查询参数{}",pageParam);
    // 业务逻辑处理
    JSONResult<PageBean<CpoolReceivelRuleRespDTO>> josnResult = cpoolRecevieFeignClient.list(pageParam);
    return josnResult;
  }

}
