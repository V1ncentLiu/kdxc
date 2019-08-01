package com.kuaidao.manageweb.controller.version;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on: 2019-08-01-14:58
 */
@Controller
@RequestMapping(value = "/version")
public class VersionController {

  @Value("${oss.url.directUpload}")
  private String ossUrl;

  /**
   * android版本列表页
   *
   * @param map
   * @return
   */
  @GetMapping(value = "/androidVersionListInit")
  @RequiresPermissions("versionList:view")
  public String androidVersionListInit(ModelMap map) {
    return "update/androidUpdate";
  }

  /**
   * IOS版本列表页
   *
   * @param map
   * @return
   */
  @GetMapping(value = "/iosVersionListInit")
  @RequiresPermissions("versionList:view")
  public String iosVersionListInit(ModelMap map) {
    return "update/iosUpdate";
  }
}
