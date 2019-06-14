package com.kuaidao.manageweb.controller.clue;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.aggregation.dto.clue.ClueDistributionedTaskDTO;
import com.kuaidao.aggregation.dto.clue.ClueDistributionedTaskQueryDTO;
import com.kuaidao.aggregation.dto.project.ProjectInfoDTO;
import com.kuaidao.common.constant.BusinessLineConstant;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.DateUtil;
import com.kuaidao.common.util.ExcelUtil;
import com.kuaidao.manageweb.feign.clue.ExtendClueFeignClient;
import com.kuaidao.manageweb.feign.customfield.CustomFieldFeignClient;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.project.ProjectInfoFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.customfield.CustomFieldQueryDTO;
import com.kuaidao.sys.dto.customfield.QueryFieldByRoleAndMenuReq;
import com.kuaidao.sys.dto.customfield.QueryFieldByUserAndMenuReq;
import com.kuaidao.sys.dto.customfield.UserFieldDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.organization.OrganizationRespDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;

@Controller
@RequestMapping("/exetend/distributionedTaskManager")
public class ExtendClueDistributionedTaskController {
    @Autowired
    private ExtendClueFeignClient extendClueFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    @Autowired
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private CustomFieldFeignClient customFieldFeignClient;
    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    /**
     * 初始化已审核列表数据
     * 
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/initDistributiveResource")
    public String initDistributiveResource(HttpServletRequest request, Model model) {
        UserInfoDTO user = getUser();
        // 项目
        JSONResult<List<ProjectInfoDTO>> proJson = projectInfoFeignClient.allProject();
        if (proJson.getCode().equals(JSONResult.SUCCESS)) {
            request.setAttribute("proSelect", proJson.getData());
        }

        List<UserInfoDTO> userList = this.queryUserByRole(user);

        request.setAttribute("userList", userList);
        // 根据角色查询页面字段
        QueryFieldByRoleAndMenuReq queryFieldByRoleAndMenuReq = new QueryFieldByRoleAndMenuReq();
        queryFieldByRoleAndMenuReq.setMenuCode("DistributiveResource");
        queryFieldByRoleAndMenuReq.setId(user.getRoleList().get(0).getId());
        JSONResult<List<CustomFieldQueryDTO>> queryFieldByRoleAndMenu =
                customFieldFeignClient.queryFieldByRoleAndMenu(queryFieldByRoleAndMenuReq);
        request.setAttribute("fieldList", queryFieldByRoleAndMenu.getData());
        // 根据用户查询页面字段
        QueryFieldByUserAndMenuReq queryFieldByUserAndMenuReq = new QueryFieldByUserAndMenuReq();
        queryFieldByUserAndMenuReq.setRoleId(user.getRoleList().get(0).getId());
        queryFieldByUserAndMenuReq.setId(user.getId());
        queryFieldByUserAndMenuReq.setMenuCode("DistributiveResource");
        JSONResult<List<UserFieldDTO>> queryFieldByUserAndMenu =
                customFieldFeignClient.queryFieldByUserAndMenu(queryFieldByUserAndMenuReq);
        request.setAttribute("userFieldList", queryFieldByUserAndMenu.getData());
        return "clue/distributiveResource";
    }

    @RequestMapping("/queryPageDistributionedTask")
    @ResponseBody
    public JSONResult<PageBean<ClueDistributionedTaskDTO>> queryPageDistributionedTask(
            HttpServletRequest request, @RequestBody ClueDistributionedTaskQueryDTO queryDto) {
        UserInfoDTO user = getUser();
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        List<Long> idList = new ArrayList<Long>();
        // 处理数据权限，客户经理、客户主管、客户专员；内勤经理、内勤主管、内勤专员；优化经理、优化主管、优化文员
        if (RoleCodeEnum.KFZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())) {
            // 推广客服、内勤文员 能看自己的数据
            idList.add(user.getId());
        } else if (RoleCodeEnum.KFZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZZ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            // 客服主管、内勤主管 能看自己组员数据
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.KFJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGFZC.name().equals(roleInfoDTO.getRoleCode())) {
            // 内勤经理 能看下属组的数据
            List<OrganizationRespDTO> groupList = getGroupList(user.getOrgId(), null);
            for (OrganizationRespDTO organizationRespDTO : groupList) {
                List<UserInfoDTO> userList = getUserList(organizationRespDTO.getId(), null, null);
                for (UserInfoDTO userInfoDTO : userList) {
                    idList.add(userInfoDTO.getId());
                }
            }
            // 自己组织内的人
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.GLY.name().equals(roleInfoDTO.getRoleCode())) {
            idList = null;
        } else {
            return new JSONResult<PageBean<ClueDistributionedTaskDTO>>()
                    .fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(), "角色没有权限");
        }
        queryDto.setResourceDirectorList(idList);
        queryDto.setUserDataAuthList(user.getUserDataAuthList());
        JSONResult<PageBean<ClueDistributionedTaskDTO>> pageBeanJSONResult =
                extendClueFeignClient.queryPageDistributionedTask(queryDto);
        return pageBeanJSONResult;
    }

    /**
     * 导出资源情况
     */
    // @RequiresPermissions("aggregation:truckingOrder:export")
    @PostMapping("/findClues")
    public void findClues(HttpServletRequest request, HttpServletResponse response,
            @RequestBody ClueDistributionedTaskQueryDTO queryDto) throws Exception {
        UserInfoDTO user = getUser();
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        List<Long> idList = new ArrayList<Long>();
        // 处理数据权限，客户经理、客户主管、客户专员；内勤经理、内勤主管、内勤专员；优化经理、优化主管、优化文员
        if (RoleCodeEnum.KFZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())) {
            // 推广客服、内勤文员 能看自己的数据
            idList.add(user.getId());
        } else if (RoleCodeEnum.KFZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZZ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            // 客服主管、内勤主管 能看自己组员数据
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.KFJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGFZC.name().equals(roleInfoDTO.getRoleCode())) {
            // 内勤经理 能看下属组的数据
            List<OrganizationRespDTO> groupList = getGroupList(user.getOrgId(), null);
            for (OrganizationRespDTO organizationRespDTO : groupList) {
                List<UserInfoDTO> userList = getUserList(organizationRespDTO.getId(), null, null);
                for (UserInfoDTO userInfoDTO : userList) {
                    idList.add(userInfoDTO.getId());
                }
            }
            // 自己组织内的人
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.GLY.name().equals(roleInfoDTO.getRoleCode())) {
            idList = null;
        }
        queryDto.setResourceDirectorList(idList);
        queryDto.setUserDataAuthList(user.getUserDataAuthList());
        JSONResult<List<ClueDistributionedTaskDTO>> listJSONResult =
                extendClueFeignClient.findClues(queryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList());
        if (JSONResult.SUCCESS.equals(listJSONResult.getCode()) && listJSONResult.getData() != null
                && listJSONResult.getData().size() != 0) {
            List<ClueDistributionedTaskDTO> orderList = listJSONResult.getData();
            int size = orderList.size();
            for (int i = 0; i < size; i++) {
                ClueDistributionedTaskDTO taskDTO = orderList.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(taskDTO.getClueId()); // 资源ID
                curList.add(taskDTO.getCreateTime()); // 创建时间
                curList.add(taskDTO.getSourceName()); // 媒介
                curList.add(taskDTO.getSourceTypeName()); // 广告位
                curList.add(taskDTO.getTypeName()); // 资源类型
                curList.add(taskDTO.getCategoryName()); // 资源类别
                curList.add(taskDTO.getProjectName()); // 资源项目
                curList.add(taskDTO.getCusName()); // 姓名
                curList.add(taskDTO.getPhone()); // 手机1
                curList.add(taskDTO.getEmail()); // 邮箱
                curList.add(taskDTO.getQq()); // QQ
                curList.add(taskDTO.getPhone2()); // 手机2
                curList.add(taskDTO.getWechat()); // 微信1
                curList.add(taskDTO.getWechat2()); // 微信2
                curList.add(taskDTO.getAddress()); // 地址
                curList.add(taskDTO.getMessageTime()); // 留言时间
                curList.add(taskDTO.getMessagePoint()); // 留言内容
                curList.add(taskDTO.getSearchWord()); // 搜索词
                curList.add(taskDTO.getOperationName()); // 资源专员
                curList.add(taskDTO.getIndustryCategoryName()); // 行业类别
                curList.add(taskDTO.getRemark()); // 备注
                curList.add(taskDTO.getTeleDirectorName()); // 电销组总监
                curList.add(taskDTO.getTeleGorupName()); // 电销组
                String flag = null;
                if (taskDTO.getIsRepeatPhone() != null) {
                    if (taskDTO.getIsRepeatPhone() == 0) {
                        flag = "否";
                    } else {
                        flag = "是";
                    }
                }
                curList.add(flag); // 是否重复
                dataList.add(curList);
            }
        }

        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel1(dataList);
        String name = "资源情况" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 导出资源沟通情况
     */
    // @RequiresPermissions("aggregation:truckingOrder:export")
    @PostMapping("/findCommunicateRecords")
    public void findCommunicateRecords(HttpServletRequest request, HttpServletResponse response,
            @RequestBody ClueDistributionedTaskQueryDTO queryDto) throws Exception {
        UserInfoDTO user = getUser();
        RoleInfoDTO roleInfoDTO = user.getRoleList().get(0);
        List<Long> idList = new ArrayList<Long>();
        // 处理数据权限，客户经理、客户主管、客户专员；内勤经理、内勤主管、内勤专员；优化经理、优化主管、优化文员
        if (RoleCodeEnum.KFZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQWY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZY.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHWY.name().equals(roleInfoDTO.getRoleCode())) {
            // 推广客服、内勤文员 能看自己的数据
            idList.add(user.getId());
        } else if (RoleCodeEnum.KFZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZZ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHZG.name().equals(roleInfoDTO.getRoleCode())) {
            // 客服主管、内勤主管 能看自己组员数据
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.KFJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZG.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.NQJL.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.YXZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.WLYHZJ.name().equals(roleInfoDTO.getRoleCode())
                || RoleCodeEnum.TGFZC.name().equals(roleInfoDTO.getRoleCode())) {
            // 内勤经理 能看下属组的数据
            List<OrganizationRespDTO> groupList = getGroupList(user.getOrgId(), null);
            for (OrganizationRespDTO organizationRespDTO : groupList) {
                List<UserInfoDTO> userList = getUserList(organizationRespDTO.getId(), null, null);
                for (UserInfoDTO userInfoDTO : userList) {
                    idList.add(userInfoDTO.getId());
                }
            }
            // 自己组织内的人
            List<UserInfoDTO> userList = getUserList(user.getOrgId(), null, null);
            for (UserInfoDTO userInfoDTO : userList) {
                idList.add(userInfoDTO.getId());
            }
        } else if (RoleCodeEnum.GLY.name().equals(roleInfoDTO.getRoleCode())) {
            idList = null;
        }
        queryDto.setResourceDirectorList(idList);
        queryDto.setUserDataAuthList(user.getUserDataAuthList());
        JSONResult<List<ClueDistributionedTaskDTO>> listJSONResult =
                extendClueFeignClient.findCommunicateRecords(queryDto);
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        dataList.add(getHeadTitleList1());


        if (JSONResult.SUCCESS.equals(listJSONResult.getCode()) && listJSONResult.getData() != null
                && listJSONResult.getData().size() != 0) {

            List<ClueDistributionedTaskDTO> orderList = listJSONResult.getData();
            int size = orderList.size();
            for (int i = 0; i < size; i++) {
                ClueDistributionedTaskDTO taskDTO = orderList.get(i);
                List<Object> curList = new ArrayList<>();
                curList.add(taskDTO.getClueId()); // 资源ID
                curList.add(taskDTO.getCreateTime()); // 创建时间
                curList.add(taskDTO.getCategoryName()); // 资源类别
                curList.add(taskDTO.getSourceName()); // 媒介
                curList.add(taskDTO.getProjectName()); // 资源项目
                curList.add(taskDTO.getPhone()); // 手机号
                curList.add(taskDTO.getSearchWord()); // 搜索词
                curList.add(taskDTO.getTeleGorupName()); // 电销组
                curList.add(taskDTO.getTeleSaleName()); // 电销顾问
                // 这两个要进行转换
                String isCall = null;
                if (taskDTO.getIsCall() != null) {
                    if (taskDTO.getIsCall() == 1) {
                        isCall = "是";
                    } else {
                        isCall = "否";
                    }
                }
                curList.add(isCall); // 是否接通
                String status = null;
                if (taskDTO.getStatus() != null) {
                    if (taskDTO.getStatus() == 1) {
                        status = "是";
                    } else {
                        status = "否";
                    }
                }
                curList.add(status); // 是否有效
                curList.add(taskDTO.getFirstCallTime()); // 第一次拨打时间
                curList.add(taskDTO.getFirstCommunicateTime()); // 第一次沟通时间
                curList.add(taskDTO.getFirstCommunicateContent()); // 第一次沟通内容
                curList.add(taskDTO.getSecondCommunicateTime()); // 第二次沟通时间
                curList.add(taskDTO.getSecondCommunicateContent()); // 第二次沟通内容
                curList.add(taskDTO.getThreeCommunicateTime()); // 第三次沟通时间
                curList.add(taskDTO.getThreeCommunicateContent()); // 第三次沟通内容
                dataList.add(curList);
            }
        }
        XSSFWorkbook wbWorkbook = ExcelUtil.creat2007Excel1(dataList);
        String name = "资源沟通记录" + DateUtil.convert2String(new Date(), DateUtil.ymdhms2) + ".xlsx";
        response.addHeader("Content-Disposition",
                "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO8859-1"));
        response.addHeader("fileName", URLEncoder.encode(name, "utf-8"));
        response.setContentType("application/octet-stream");
        ServletOutputStream outputStream = response.getOutputStream();
        wbWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 导出资源沟通情况
     * 
     * @return
     */
    private List<Object> getHeadTitleList1() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("资源ID");
        headTitleList.add("创建时间");
        headTitleList.add("资源类别");
        headTitleList.add("媒介");
        headTitleList.add("资源项目");
        headTitleList.add("手机号");
        headTitleList.add("搜索词");
        headTitleList.add("电销组");
        headTitleList.add("电销顾问");
        headTitleList.add("是否接通");
        headTitleList.add("是否有效");
        headTitleList.add("第一次拨打时间");
        headTitleList.add("第一次沟通时间");
        headTitleList.add("第一次沟通内容");
        headTitleList.add("第二次沟通时间");
        headTitleList.add("第二次沟通内容");
        headTitleList.add("第三次沟通时间");
        headTitleList.add("第三次沟通内容");
        return headTitleList;
    }

    /**
     * 导出资源情况
     * 
     * @return
     */
    private List<Object> getHeadTitleList() {
        List<Object> headTitleList = new ArrayList<>();
        headTitleList.add("资源ID");
        headTitleList.add("创建时间");
        headTitleList.add("媒介");
        headTitleList.add("广告位");
        headTitleList.add("资源类型");
        headTitleList.add("资源类别");
        headTitleList.add("资源项目");
        headTitleList.add("姓名");
        headTitleList.add("手机1");
        headTitleList.add("邮箱");
        headTitleList.add("QQ");
        headTitleList.add("手机2");
        headTitleList.add("微信1");
        headTitleList.add("微信2");
        headTitleList.add("地址");
        headTitleList.add("留言时间");
        headTitleList.add("留言内容");
        headTitleList.add("搜索词");
        headTitleList.add("资源专员");
        headTitleList.add("行业类别");
        headTitleList.add("备注");
        headTitleList.add("电销组总监");
        headTitleList.add("电销组");
        headTitleList.add("是否重复");
        return headTitleList;
    }

    /**
     * 查询所有资源专员
     * 
     * @return
     */

    private List<UserInfoDTO> queryUserByRole(UserInfoDTO user) {

        List<UserInfoDTO> userList = new ArrayList<UserInfoDTO>();
        String roleCode = user.getRoleList().get(0).getRoleCode();
        UserOrgRoleReq userRole = new UserOrgRoleReq();
        if (RoleCodeEnum.GLY.name().equals(roleCode)
                || RoleCodeEnum.YWGLY.name().equals(roleCode)) {
            userRole.setBusinessLine(BusinessLineConstant.TGZX);
        } else {
            userRole.setOrgId(user.getOrgId());
        }
        JSONResult<List<UserInfoDTO>> userZxzjList = userInfoFeignClient.listByOrgAndRole(userRole);
        if (JSONResult.SUCCESS.equals(userZxzjList.getCode()) && null != userZxzjList.getData()) {
            userList = userZxzjList.getData();
        }
        return userList;

    }

    /**
     * 获取当前登录账号
     *
     * @return
     */
    private UserInfoDTO getUser() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        return user;
    }

    /**
     * 根据机构和角色类型获取用户
     *
     * @return
     */
    private List<UserInfoDTO> getUserList(Long orgId, String roleCode, List<Integer> statusList) {
        UserOrgRoleReq userOrgRoleReq = new UserOrgRoleReq();
        userOrgRoleReq.setOrgId(orgId);
        userOrgRoleReq.setRoleCode(roleCode);
        userOrgRoleReq.setStatusList(statusList);
        JSONResult<List<UserInfoDTO>> listByOrgAndRole =
                userInfoFeignClient.listByOrgAndRole(userOrgRoleReq);
        return listByOrgAndRole.getData();
    }

    /**
     * 获取所有组织组
     * 
     * @return
     */
    private List<OrganizationRespDTO> getGroupList(Long parentId, Integer type) {
        OrganizationQueryDTO queryDTO = new OrganizationQueryDTO();
        queryDTO.setParentId(parentId);
        queryDTO.setOrgType(type);
        // 查询所有组织
        JSONResult<List<OrganizationRespDTO>> queryOrgByParam =
                organizationFeignClient.queryOrgByParam(queryDTO);
        List<OrganizationRespDTO> data = queryOrgByParam.getData();
        return data;
    }
}
