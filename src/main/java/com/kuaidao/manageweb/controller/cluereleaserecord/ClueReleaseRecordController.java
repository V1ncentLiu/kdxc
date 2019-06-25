package com.kuaidao.manageweb.controller.cluereleaserecord;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordReqDTO;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordRespDTO;
import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.constant.SystemCodeConstant;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.organization.OrganizationFeignClient;
import com.kuaidao.manageweb.feign.releaserecord.ReleaseRecordFeignClient;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.organization.OrganizationDTO;
import com.kuaidao.sys.dto.organization.OrganizationQueryDTO;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;

/**
 * @author yangbiao 接口层 Created on 2019-2-12 15:06:38 资源释放记录 对外接口类
 */

@RestController
@RequestMapping("/aggregation/releaserecord")
public class ClueReleaseRecordController {

    private static Logger logger = LoggerFactory.getLogger(ClueReleaseRecordController.class);

    @Autowired
    private ReleaseRecordFeignClient releaseRecordFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    @Autowired
    RoleManagerFeignClient roleManagerFeignClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    /**
     * 新增
     */
    @RequestMapping("/insert")
    public JSONResult<Boolean> saveReleaseRecord(
            @Valid @RequestBody ReleaseRecordInsertOrUpdateDTO dto, BindingResult result)
            throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        logger.info("插入一条资源释放记录");
        return new JSONResult().success(releaseRecordFeignClient.saveReleaseRecord(dto));
    }

    /**
     * 分页查询
     */
    @RequestMapping("/queryPageList")
    public JSONResult<PageBean<ReleaseRecordRespDTO>> queryPageList(
            @RequestBody ReleaseRecordReqDTO dto) {

        UserInfoDTO user = CommUtil.getCurLoginUser();
        List<RoleInfoDTO> roleList = user.getRoleList();
        List dxList = new ArrayList();
        /**
         * 数据权限说明： 管理员权限： 能够看见全部数据 电销顾问： 能够看见自己以及所在电销组下电销创业顾问创建的数据 电销副总： 能够看见事业部下所有人的释放记录
         *
         * 角色说明： 事业部下： 电销副总 管理：多个电销组 电销组: 电销总监 管理多个电销创业顾问
         *
         */
        if (roleList != null && roleList.get(0) != null) {
            if (RoleCodeEnum.GLY.name().equals(roleList.get(0).getRoleCode())) {
            } else if (RoleCodeEnum.DXFZ.name().equals(roleList.get(0).getRoleCode())) { // 电销副总，查看整个事业部。
                //
                dxList = allUser();
                dto.setUserIdsList(dxList);
            } else if (RoleCodeEnum.DXZJL.name().equals(roleList.get(0).getRoleCode())) { // 电销总经理，查看整个分公司。
                //
                dxList = allUser();
                dto.setUserIdsList(dxList);
            } else { // 创业顾问查看自己
                dxList.add(user.getId());
                dto.setUserIdsList(dxList);
            }
        }
        JSONResult<PageBean<ReleaseRecordRespDTO>> result =
                releaseRecordFeignClient.queryPageList(dto);
        return result;
    }

    /**
     * 根据资源id
     */
    @RequestMapping("/listNoPage")
    public JSONResult<List<ReleaseRecordRespDTO>> listNoPage(@RequestBody ReleaseRecordReqDTO dto) {

        JSONResult<List<ReleaseRecordRespDTO>> result = releaseRecordFeignClient.listNoPage(dto);
        return result;
    }

    /**
     * 电销副总： 管理多个电销组 一个电销组中： 一个电销总监，多个电销创业顾问 电销副总：能够看见整个事业部的释放记录 就是能够看见：当前组织 以及 当前组织的全部下属组织机构
     *
     * 当前释放记录中：没有组织机构。 故而，查询，当前组织以及下属组织的全部人员
     *
     *
     * 下面方法： 查询的是：当前组织下 包括 全部下属组织的 所有用户
     *
     * @return
     */
    private List allUser() {

        List<UserInfoDTO> resList = new ArrayList();
        UserInfoDTO user = CommUtil.getCurLoginUser();
        OrganizationQueryDTO organizationQueryDTO = new OrganizationQueryDTO();
        organizationQueryDTO.setParentId(user.getOrgId());
        organizationQueryDTO.setSystemCode(SystemCodeConstant.HUI_JU);
        JSONResult<List<OrganizationDTO>> orgResult =
                organizationFeignClient.listDescenDantByParentId(organizationQueryDTO);

        if (JSONResult.SUCCESS.equals(orgResult.getCode())) {
            List<OrganizationDTO> orgList = orgResult.getData();
            List<Long> idList = new ArrayList<Long>();
            for (OrganizationDTO orgDTO : orgList) {
                idList.add(orgDTO.getId());
            }
            idList.add(user.getOrgId());
            UserInfoPageParam userparam = new UserInfoPageParam();
            userparam.setPageSize(10000);
            userparam.setPageNum(1);
            userparam.setOrgIdList(idList);
            JSONResult<PageBean<UserInfoDTO>> userResult = userInfoFeignClient.list(userparam);
            if (JSONResult.SUCCESS.equals(userResult.getCode())) {
                resList = userResult.getData().getData();
            }
        }
        return resList;
    }

    /**
     * 根据
     * 
     * @param releaseRecordRespDTO
     * @return
     */
    @RequestMapping("/getReleaseRecordListByCludId")
    public JSONResult<List<ReleaseRecordRespDTO>> getReleaseRecordListByCludId(
            @RequestBody ReleaseRecordRespDTO releaseRecordRespDTO) {
        return releaseRecordFeignClient.getReleaseRecordListByCludId(releaseRecordRespDTO);
    }

    /**
     * 释放记录
     */
    @RequestMapping("/getReleaseRecordRespByClueId")
    public JSONResult<List<ReleaseRecordRespDTO>> getReleaseRecordRespByClueId(
            @RequestBody ReleaseRecordReqDTO dto) {
        return releaseRecordFeignClient.listNoPage(dto);
    }
}
