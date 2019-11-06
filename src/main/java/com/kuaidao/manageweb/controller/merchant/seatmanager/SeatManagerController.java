package com.kuaidao.manageweb.controller.merchant.seatmanager;

import com.kuaidao.callcenter.dto.seatmanager.SeatInsertOrUpdateDTO;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerReq;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerResp;
import com.kuaidao.common.constant.ComConstant.USER_STATUS;
import com.kuaidao.common.entity.IdEntityLong;
import com.kuaidao.common.entity.IdListLongReq;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.component.merchant.MerchantComponent;
import com.kuaidao.manageweb.feign.merchant.seatmanager.SeatManagerFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.manageweb.util.IdUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: admin
 * @Date: 2019/10/4 10:22
 * @Description: 坐席管理
 */
@Controller
@RequestMapping("/merchant/seatManager")
public class SeatManagerController {
  private static Logger logger = LoggerFactory.getLogger(SeatManagerController.class);


  @Autowired
  private SeatManagerFeignClient seatManagerFeignClient;


  @Autowired
  private MerchantComponent merchantComponent;
  /**
   * 跳转作息管理列表
   * @param request
   * @return
   */
  @RequestMapping("/toPage")
//  @RequiresPermissions("merchant:seatMagager:view")
  public String toPage(HttpServletRequest request, @RequestParam Long userId,@RequestParam Long buyPackageId,@RequestParam Long packageId) {
    // 商家主账号
    UserInfoDTO merchantById = merchantComponent.getMerchantById(userId);
    // 商家子账号
    List<UserInfoDTO> userList = merchantComponent.getMerchantSubUser(userId,null);
    if(merchantById!=null){
      userList.add(merchantById);
    }
    request.setAttribute("userList", userList);
    request.setAttribute("userId",userId); // 商家ID
    request.setAttribute("userName",merchantById.getName()); // 商家ID
    request.setAttribute("buyPackageId",buyPackageId); // 当前购买服务记录ID
    request.setAttribute("packageId",buyPackageId); // 服务ID
    return "merchant/serviceManagement/seatsManagement";
  }

  /**
   *  获取商家子账户+商家账户
   */
  @ResponseBody
  @PostMapping("/merchantUsers")
  public  List<UserInfoDTO> merchantUsers(HttpServletRequest request,@RequestBody IdEntityLong id) {
    UserInfoDTO merchantUser = merchantComponent.getMerchantById(id.getId());
    List<Integer> statusList = new ArrayList<>();
    statusList.add(USER_STATUS.ENABLE);
    statusList.add(USER_STATUS.LOCK);
    List<UserInfoDTO> userList = merchantComponent.getMerchantSubUser(id.getId(),statusList);
    if(merchantUser!=null){
      userList.add(merchantUser);
    }
    return userList;
  }

  /**
   *  新增
   */
  @ResponseBody
  @PostMapping("/create")
//  @RequiresPermissions("merchant:seatMagager:add")
  public JSONResult<Boolean> create(@RequestBody SeatInsertOrUpdateDTO insertOrUpdateDTO) {
    UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
    insertOrUpdateDTO.setCreateUser(curLoginUser.getId());
    insertOrUpdateDTO.setId(IdUtil.getUUID());
    return seatManagerFeignClient.create(insertOrUpdateDTO);
  }

  /**
   * 更新
   */
  @ResponseBody
  @PostMapping("/update")
//  @RequiresPermissions("merchant:seatMagager:update")
  public JSONResult<Boolean> update(@RequestBody SeatInsertOrUpdateDTO insertOrUpdateDTO) {
    UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
    insertOrUpdateDTO.setUpdateUser(curLoginUser.getId());
    return seatManagerFeignClient.update(insertOrUpdateDTO);
  }

  /**
   * 删除
   */
  @ResponseBody
  @PostMapping("/delete")
//  @RequiresPermissions("merchant:seatMagager:delete")
  public JSONResult<Boolean> delete(@RequestBody IdListLongReq idList) {
    if(CollectionUtils.isEmpty(idList.getIdList())){
      return new JSONResult<Boolean>().fail("1","idList不能为空");
    }
    return seatManagerFeignClient.delete(idList);
  }
  /**
   * 查询
   */
  @ResponseBody
  @PostMapping("/queryList")
  public JSONResult<PageBean<SeatManagerResp>> queryList(@RequestBody SeatManagerReq seatManagerReq) {
    return seatManagerFeignClient.queryList(seatManagerReq);
  }

  /**
   * 查询一条
   */
  @ResponseBody
  @PostMapping("/findOne")
  public JSONResult<SeatManagerResp> findOne(@RequestBody IdEntityLong idEntityLong) {
    
    return seatManagerFeignClient.findOne(idEntityLong);
  }

  /**
   * 查询当前用户是否绑定坐席
   */
  @ResponseBody
  @PostMapping("/queryListBySubMerchant")
  public JSONResult<SeatManagerResp> queryListBySubMerchant(HttpServletRequest request) {
    UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
    Long accountId = curLoginUser.getId();
    SeatManagerReq seatManagerReq = new SeatManagerReq();
    seatManagerReq.setSubMerchant(accountId);
    return seatManagerFeignClient
        .queryListBySubMerchant(seatManagerReq);
  }

}
