package com.kuaidao.manageweb.controller.version;

import com.alibaba.fastjson.JSON;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.version.VersionFeignClient;
import com.kuaidao.version.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on: 2019-08-01-14:58
 */
@Slf4j
@Controller
@RequestMapping("/version")
public class VersionController {

  @Autowired
  private VersionFeignClient versionFeignClient;

  @Value("${oss.url.directUpload}")
  private String ossUrl;

  /**
   * android版本列表页
   *
   * @return
   */
  @GetMapping(value = "/androidVersionListInit")
  @RequiresPermissions("versionList:view")
  public String androidVersionListInit(HttpServletRequest request, Model model) {
    request.setAttribute("type",request.getParameter("type"));
    request.setAttribute("systemCode",request.getParameter("systemCode"));
    request.setAttribute("ossUrl", ossUrl);
    return "update/androidUpdate";
  }

  /**
   * IOS版本列表页
   *
   * @return
   */
  @GetMapping(value = "/iosVersionListInit")
  @RequiresPermissions("versionList:view")
  public String iosVersionListInit(HttpServletRequest request, Model model) {
    request.setAttribute("type",request.getParameter("type"));
    request.setAttribute("systemCode",request.getParameter("systemCode"));
    request.setAttribute("ossUrl", ossUrl);
    return "update/iosUpdate";
  }

  /**
   * 获取版本列表
   *
   * @param versionManageListDTO
   * @return
   */
  @PostMapping("/list")
  @ResponseBody
  public PageBean<VersionManageDTO> list(@RequestBody VersionManageListDTO versionManageListDTO) {
    log.info("list-version=versionManageListDTO", JSON.toJSONString(versionManageListDTO));
    // 查询账号列表信息
    JSONResult<PageBean<VersionManageDTO>> listResult = versionFeignClient.list(versionManageListDTO);
    if (null != listResult && listResult.getCode().equals(Constants.SUCCESS)) {
      return listResult.getData();
    } else {
      return null;
    }
  }

  /**
   * 删除版本信息
   *
   * @param idEntity
   * @return
   */
  @PostMapping(value = "/delete")
  @ResponseBody
  @LogRecord(description="删除版本信息",operationType=OperationType.DELETE,menuName= MenuEnum.VERSION_LIST)
  public JSONResult<Void> delete(@RequestBody IdEntity idEntity) {
    // 查询账号列表信息
    JSONResult<Void> listResult = versionFeignClient.delete(idEntity);
    if (null != listResult && listResult.getCode().equals(Constants.SUCCESS)) {
      return listResult;
    } else {
      return null;
    }
  }

  /**
   * 设置最新版本
   *
   * @param versionManageSetNewDTO
   * @return
   */
  @PostMapping(value = "/setNewVersion")
  @ResponseBody
  @LogRecord(description="设置最新版本",operationType=OperationType.UPDATE,menuName=MenuEnum.VERSION_LIST)
  public JSONResult<Void> setNewVersion(@RequestBody VersionManageSetNewDTO versionManageSetNewDTO) {
    // 查询账号列表信息
    JSONResult<Void> listResult = versionFeignClient.setNewVersion(versionManageSetNewDTO);
    if (null != listResult && listResult.getCode().equals(Constants.SUCCESS)) {
      return listResult;
    } else {
      return null;
    }
  }

  /**
   * 设置下架
   *
   * @param versionManageSetNewDTO
   * @return
   */
  @PostMapping(value = "/setOneOldVersion")
  @ResponseBody
  @LogRecord(description="设置下架",operationType=OperationType.UPDATE,menuName=MenuEnum.VERSION_LIST)
  public JSONResult<Void> setOneOldVersion(@RequestBody VersionManageSetNewDTO versionManageSetNewDTO) {
    // 查询账号列表信息
    JSONResult<Void> listResult = versionFeignClient.setOneOldVersion(versionManageSetNewDTO);
    if (null != listResult && listResult.getCode().equals(Constants.SUCCESS)) {
      return listResult;
    } else {
      return null;
    }
  }

  /**
   * 创建android版本信息
   *
   * @param versionCreateReq
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/addAndroid" }, method = RequestMethod.POST)
  @ResponseBody
  @LogRecord(description="创建android版本信息",operationType=OperationType.INSERT,menuName=MenuEnum.ANDROID_VERSION_LIST)
  public JSONResult addAndroid(@RequestBody VersionCreateReq versionCreateReq) throws Exception {
    return versionFeignClient.create(versionCreateReq);
  }

  /**
   * 修改android版本信息
   *
   * @param versionUpdateReq
   * @param versionUpdateReq
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/editAndroid" }, method = RequestMethod.POST)
  @ResponseBody
  @LogRecord(description="修改android版本信息",operationType=OperationType.UPDATE,menuName=MenuEnum.ANDROID_VERSION_LIST)
  public JSONResult editAndroid(@RequestBody VersionUpdateReq versionUpdateReq) throws Exception {
    return versionFeignClient.update(versionUpdateReq);
  }


  /**
   * 创建iOS版本信息
   *
   * @param versionCreateReq
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/addIOS" }, method = RequestMethod.POST)
  @ResponseBody
  @LogRecord(description="创建iOS版本信息",operationType=OperationType.INSERT,menuName=MenuEnum.IOS_VERSION_LIST)
  public JSONResult addIOS(@RequestBody VersionCreateReq versionCreateReq)
      throws Exception {
    return versionFeignClient.create(versionCreateReq);
  }

  /**
   * 修改iOS版本信息
   *
   * @param versionUpdateReq
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/editIOS" }, method = RequestMethod.POST)
  @ResponseBody
  @LogRecord(description="修改iOS版本信息",operationType=OperationType.UPDATE,menuName=MenuEnum.IOS_VERSION_LIST)
  public JSONResult editIOS(@RequestBody VersionUpdateReq versionUpdateReq)
      throws Exception {
    return versionFeignClient.update(versionUpdateReq);
  }

  /**
   * 查询版本信息
   *
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = { "/queryVersion" })
  public JSONResult<VersionManageDTO> queryVersion(@RequestBody IdEntity idEntity){
    JSONResult<VersionManageDTO> result = versionFeignClient.getVersion(idEntity);
    return result;
  }

  /**
   * 校验版本号是否重复
   *
   * @param
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/checkNum" }, method = RequestMethod.POST)
  @ResponseBody
  public void checkNum(HttpServletResponse response,@RequestBody VersionNumCheckReq versionNumCheckReq)
      throws Exception {
    JSONResult<Boolean> result =  versionFeignClient.checkNum(versionNumCheckReq);
    //如果重复返回响应状态为409
    if (null != result && result.getCode().equals(Constants.SUCCESS) && !result.getData()) {
      response.setStatus(HttpStatus.CONFLICT.value());
    }
  }
}
