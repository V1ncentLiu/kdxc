package com.kuaidao.manageweb.controller.bus;

import com.kuaidao.aggregation.dto.clue.BusArrangeDTO;
import com.kuaidao.aggregation.dto.clue.BusArrangeParam;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.OrgTypeConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.IdEntity;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.feign.area.SysRegionFeignClient;
import com.kuaidao.manageweb.feign.clue.BusArrangeFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.area.SysRegionDTO;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家版我
 */
@Controller
@RequestMapping("/bus/userManager")
public class BusController {
  private static Logger logger = LoggerFactory.getLogger(BusController.class);
  @Autowired
  private OrganizationFeignClient organizationFeignClient;
  @Autowired
  private ProjectInfoFeignClient projectInfoFeignClient;
  @Autowired
  SysRegionFeignClient sysRegionFeignClient;
  @Autowired
  private BusArrangeFeignClient busArrangeFeignClient;
  @Autowired
  UserInfoFeignClient userInfoFeignClient;

  /**
   *
   * @param request
   * @return
   */
  @RequestMapping("/accountManagement")
  public String accountManagement(HttpServletRequest request) {
    return "merchant/accountManagement/accountManagement";
  }
  /**
   *
   * @param request
   * @return
   */
  @RequestMapping("/subAccountManagement")
  public String subAccountManagement(HttpServletRequest request) {
    return "merchant/accountManagement/subAccountManagement";
  }
}
