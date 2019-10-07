package com.kuaidao.manageweb.component.merchant;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import java.util.List;
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
   * 获取商家账户
   * usreType: 主账户/子账户
   * statusList：状态列表
   */
  public List<UserInfoDTO> getMerchantUser(Integer usreType,List<Integer> statusList) {
    UserInfoDTO userInfoDTO = new UserInfoDTO();
    userInfoDTO.setUserType(usreType);
    userInfoDTO.setStatusList(statusList);
    JSONResult<List<UserInfoDTO>> merchantUserList =
        merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
    return merchantUserList.getData();
  }


}
