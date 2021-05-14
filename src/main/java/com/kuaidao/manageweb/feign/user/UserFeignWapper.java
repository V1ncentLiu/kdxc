package com.kuaidao.manageweb.feign.user;

import com.kuaidao.common.constant.RoleCodeEnum;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import com.kuaidao.sys.dto.user.UserInfoReq;
import com.kuaidao.sys.dto.user.UserOrgRoleReq;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: yangbiao
 * @Date: 2020/5/28 18:50
 * @Description:
 */
@Component
public class UserFeignWapper {


  @Autowired
  private UserInfoFeignClient userFeignClient;


   public List<Long> getUserIdList(List<UserInfoDTO> userList){
     if(CollectionUtils.isEmpty(userList)){
       return new ArrayList<>();
     }
     return userList.stream().map(a->a.getId()).collect(Collectors.toList());
   }

  public Map<Long,String> getUserId2Name(List<UserInfoDTO> userList){
    if(CollectionUtils.isEmpty(userList)){
      return new HashMap<>();
    }
    return userList.stream().collect(Collectors.toMap(UserInfoDTO::getId,UserInfoDTO::getUsername,(m,n)->m));
  }


  public Map<Long,String> toIdNameMap( List<UserInfoDTO> list ){
      if(CollectionUtils.isEmpty(list)){
          return new HashMap<>();
      }
     return list.stream().collect(Collectors.toMap(UserInfoDTO::getId,UserInfoDTO::getName,(m,n)->m));
  }

  public List<UserInfoDTO> listById(List<Long> idList) {
    IdListLongReq idListLongReq = new IdListLongReq();
    idListLongReq.setIdList(idList);
    JSONResult<List<UserInfoDTO>> result = userFeignClient.listById(idListLongReq);
    return result.data();
  }

  public PageBean<UserInfoDTO> list(UserInfoPageParam param) {
    JSONResult<PageBean<UserInfoDTO>> result = userFeignClient.list(param);
    return result.data();
  }
  

  
  public List<UserInfoDTO> listByOrgAndRole(UserOrgRoleReq userOrgRoleReq) {
    JSONResult<List<UserInfoDTO>> result = userFeignClient.listByOrgAndRole(userOrgRoleReq);
    return result.data();
  }


  
  public UserInfoDTO getbyUserName(UserInfoReq userInfoReq) {
    JSONResult<UserInfoDTO> result = userFeignClient.getbyUserName(userInfoReq);
    return result.data();
  }

  public List<UserInfoDTO> listNoPage(UserInfoPageParam pageParam) {
    JSONResult<List<UserInfoDTO>> result = userFeignClient.listNoPage(pageParam);
    return result.data();
  }


}
