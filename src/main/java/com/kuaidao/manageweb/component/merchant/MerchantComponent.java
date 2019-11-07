package com.kuaidao.manageweb.component.merchant;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoReq;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: admin
 * @Date: 2019/10/7 15:07
 * @Description:
 */

@Component
public class MerchantComponent {

  @Autowired
  private MerchantUserInfoFeignClient merchantUserInfoFeignClient;

  /**
   * 获取全部商家账户
   * usreType: 主账户/子账户
   * statusList：状态列表
   */
  public List<UserInfoDTO> getMerchantUser(Integer usreType,List<Integer> statusList) {
    return getMerchantUser(usreType,null,statusList);
  }


  /**
   * 获取指定商家子账户
   */
  public List<UserInfoDTO> getMerchantSubUser(Long parentId,List<Integer> statusList) {
    return getMerchantUser(SysConstant.USER_TYPE_THREE,parentId,statusList);
  }
  /**
   * 获取商家主账户
   */
  public List<UserInfoDTO> getMerchantMainUser(List<Integer> statusList) {
    return getMerchantUser(SysConstant.USER_TYPE_TWO,null,statusList);
  }

  /**
   *  获取商家账户
   */
  public List<UserInfoDTO> getMerchantUser(Integer usreType,Long parentId,List<Integer> statusList) {
    UserInfoDTO userInfoDTO = new UserInfoDTO();
    userInfoDTO.setUserType(usreType);
    userInfoDTO.setStatusList(statusList);
    userInfoDTO.setParentId(parentId);
    JSONResult<List<UserInfoDTO>> merchantUserList =
        merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
    List<UserInfoDTO> data = merchantUserList.getData();
    if(CollectionUtils.isEmpty(data)){
      data = new ArrayList<>();
    }
    return data;
  }

  /**
   * 获取商家账户
   */

  public UserInfoDTO getMerchantById(Long userId){
    UserInfoDTO user = new UserInfoDTO();
    if(userId==null){return null;}
    UserInfoReq req = new UserInfoReq();
    req.setId(userId);
    JSONResult<UserInfoReq> mechantUser = merchantUserInfoFeignClient.getMechantUserById(req);
    if(mechantUser!=null&&JSONResult.SUCCESS.equals(mechantUser.getCode())){
      UserInfoReq data = mechantUser.getData();
      BeanUtils.copyProperties(data, user);
    }
    return user;
  }

}
