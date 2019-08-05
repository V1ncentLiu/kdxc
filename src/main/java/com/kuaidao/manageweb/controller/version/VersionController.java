package com.kuaidao.manageweb.controller.version;

import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.version.VersionFeignClient;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.version.dto.*;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;

/**
 * Created on: 2019-08-01-14:58
 */
@Controller
@RequestMapping(value = "/version")
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
    request.setAttribute("ossUrl", ossUrl);
    return "update/iosUpdate";
  }

  /**
   * 获取版本列表
   *
   * @param map
   * @return
   */
  @PostMapping(value = "/list")
  @ResponseBody
  public PageBean<VersionManageDTO> list(@RequestBody VersionManageListDTO versionManageListDTO, ModelMap map,
      int pageNum, int pageSize) {
    // 查询账号列表信息
    versionManageListDTO.setPageNum(pageNum);
    versionManageListDTO.setPageSize(pageSize);
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
  @LogRecord(description="设置最新版本",operationType=OperationType.DELETE,menuName=MenuEnum.VERSION_LIST)
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
   * 创建android版本信息
   *
   * @param request
   * @param model
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/addAndroid" }, method = RequestMethod.POST)
  @ResponseBody
  @LogRecord(description="创建android版本信息",operationType=OperationType.INSERT,menuName=MenuEnum.ANDROID_VERSION_LIST)
  public JSONResult addAndroid(HttpServletRequest request, @RequestBody VersionCreateReq versionCreateReq,
      Model model) throws Exception {
    return versionFeignClient.create(versionCreateReq);
  }

  /**
   * 修改android版本信息
   *
   * @param request
   * @param versionUpdateReq
   * @param model
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/editAndroid" }, method = RequestMethod.POST)
  @ResponseBody
  @LogRecord(description="修改android版本信息",operationType=OperationType.UPDATE,menuName=MenuEnum.ANDROID_VERSION_LIST)
  public JSONResult editAndroid(HttpServletRequest request, @RequestBody VersionUpdateReq versionUpdateReq,
      Model model) throws Exception {
    return versionFeignClient.update(versionUpdateReq);
  }


  /**
   * 创建iOS版本信息
   *
   * @param request
   * @param model
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/addIOS" }, method = RequestMethod.POST)
  @ResponseBody
  @LogRecord(description="创建iOS版本信息",operationType=OperationType.INSERT,menuName=MenuEnum.IOS_VERSION_LIST)
  public JSONResult addIOS(HttpServletRequest request, @RequestBody VersionCreateReq versionCreateReq, Model model)
      throws Exception {
    return versionFeignClient.create(versionCreateReq);
  }

  /**
   * 修改iOS版本信息
   *
   * @param request
   * @param versionUpdateReq
   * @param model
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/editIOS" }, method = RequestMethod.POST)
  @ResponseBody
  @LogRecord(description="修改iOS版本信息",operationType=OperationType.UPDATE,menuName=MenuEnum.IOS_VERSION_LIST)
  public JSONResult editIOS(HttpServletRequest request, @RequestBody VersionUpdateReq versionUpdateReq, Model model)
      throws Exception {
    return versionFeignClient.update(versionUpdateReq);
  }

  /**
   * 校验版本号是否重复
   *
   * @param request
   * @param
   * @param model
   * @return
   * @throws Exception
   */
  @RequestMapping(value = { "/checkNum" }, method = RequestMethod.POST)
  @ResponseBody
  public void checkNum(HttpServletRequest request, HttpServletResponse response,@RequestParam(required = false) String curVersionNum, @RequestParam String versionNum,@RequestParam Integer type, Model model)
      throws Exception {
    //判断如果是编辑页面判断重复，并且版本号没有改变，则直接返回成功
    if(StringUtils.isNotEmpty(curVersionNum) && versionNum.equals(curVersionNum)){
      return;
    }
    VersionNumCheckReq versionNumCheckReq = new VersionNumCheckReq();
    versionNumCheckReq.setType(type);
    versionNumCheckReq.setVersionNum(versionNum);
    JSONResult<Boolean> result =  versionFeignClient.checkNum(versionNumCheckReq);
    //如果重复返回响应状态为409
    if (null != result && result.getCode().equals(Constants.SUCCESS) && !result.getData()) {
      response.setStatus(HttpStatus.CONFLICT.value());
    }
  }
}
