package com.kuaidao.manageweb.controller.merchant.tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.merchant.tracking.TrackingMerchantFeignClient;
import com.kuaidao.merchant.dto.tracking.TrackingInsertOrUpdateDTO;
import com.kuaidao.merchant.dto.tracking.TrackingRespDTO;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;


/**
 * 跟进记录
 * 
 * @author fanjd
 */
@Slf4j
@RestController
@RequestMapping("/tracking")
public class ClueTrackingController {

    @Autowired
    private TrackingMerchantFeignClient trackingMerchantFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;

    /**
     * 新增
     */
    @RequestMapping("/insert")
    public JSONResult<Boolean> saveTracking(@Valid @RequestBody TrackingInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        // 获取当前登录人
        UserInfoDTO user = getUser();
        dto.setCreateUser(user.getId());
        dto.setOrgId(user.getOrgId());
        // 用户集合
        dto.setUserList(getUserList());
        return trackingMerchantFeignClient.saveTracking(dto);
    }

    /**
     * 根据资源id查询跟进记录
     * 
     * @param idEntity
     * @return
     */
    @PostMapping("/findByClueId")
    public JSONResult<List<TrackingRespDTO>> findByClueId(@RequestBody IdEntityLong idEntity) {
        if (null == idEntity.getId()) {
            return new JSONResult<List<TrackingRespDTO>>().fail(SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getCode(),
                    SysErrorCodeEnum.ERR_ILLEGAL_PARAM.getMessage());
        }
        IdListLongReq reqDto = new IdListLongReq();
        reqDto.setClueId(idEntity.getId());
        List<Long> userList = getUserList();
        reqDto.setIdList(userList);
        return trackingMerchantFeignClient.findByClueId(reqDto);
    }

    /**
     * 获取登录人集合 如果登录人为主账号 则集合为所有子账号和主账号本身 如果登录人为子账号 则集合为主账号和子账号本身
     * 
     * @author: Fanjd
     * @param
     * @return:
     * @Date: 2019/10/18 14:46
     * @since: 1.0.0
     **/
    private List<Long> getUserList() {
        UserInfoDTO user = getUser();
        List<Long> userList = new ArrayList<>();
        // 商家主账户能看商家子账号所有的记录
        if (SysConstant.USER_TYPE_TWO.equals(user.getUserType())) {
            getSubAccountIds(userList, user.getId());
        }
        // 商家子账号看自己和主账号的记录
        if (SysConstant.USER_TYPE_THREE.equals(user.getUserType())) {
            userList.add(user.getId());
            userList.add(user.getParentId());
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
     * 获取商家主账户下的子账号
     *
     * @author: Fanjd
     * @param subIds 用户集 合userId 用户id
     * @return: void
     * @Date: 2019/10/10 20:30
     * @since: 1.0.0
     **/
    private void getSubAccountIds(List<Long> subIds, Long userId) {
        subIds.add(userId);
        // 获取商家主账号下的子账号列表
        UserInfoDTO userReqDto = buildQueryReqDto(SysConstant.USER_TYPE_THREE, userId);
        JSONResult<List<UserInfoDTO>> merchantUserList = merchantUserInfoFeignClient.merchantUserList(userReqDto);
        if (merchantUserList.getCode().equals(JSONResult.SUCCESS)) {
            if (CollectionUtils.isNotEmpty(merchantUserList.getData())) {
                // 获取子账号id放入子账号集合中
                subIds.addAll(merchantUserList.getData().stream().map(UserInfoDTO::getId).collect(Collectors.toList()));
            }
        }

    }

    /**
     * 构建商家子账户查询实体
     *
     * @author: Fanjd
     * @param userType 用户类型
     * @param id 用户id
     * @return: com.kuaidao.sys.dto.user.UserInfoDTO
     * @Date: 2019/10/10 20:28
     * @since: 1.0.0
     **/
    private UserInfoDTO buildQueryReqDto(Integer userType, Long id) {
        // 获取商家主账号下的子账号列表
        UserInfoDTO userReqDto = new UserInfoDTO();
        // 商家主账户
        userReqDto.setUserType(userType);
        // 启用和锁定
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SysConstant.USER_STATUS_ENABLE);
        statusList.add(SysConstant.USER_STATUS_LOCK);
        userReqDto.setStatusList(statusList);
        // 商家主账号id
        userReqDto.setParentId(id);
        return userReqDto;
    }
}
