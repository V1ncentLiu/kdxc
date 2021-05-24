package com.kuaidao.manageweb.controller.merchant.charge;

import com.kuaidao.account.dto.recharge.MerchantChargePreferentialDto;
import com.kuaidao.account.dto.recharge.MerchantChargePreferentialReq;
import com.kuaidao.common.constant.MerchantChargeStatusEnum;
import com.kuaidao.common.constant.MerchantChargeTypeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.manageweb.feign.merchant.charge.MerchantChargePreferentialFeignClient;
import com.kuaidao.manageweb.feign.merchant.user.MerchantUserInfoFeignClient;
import com.kuaidao.sys.constant.SysConstant;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* 接口层
* Created  on 2021-5-19 9:21:49
*/
@Controller
@RequestMapping("/merchant/chargePreFerential")
@Slf4j
public class MerchantChargePreferentialController {

    @Autowired
    private MerchantChargePreferentialFeignClient merchantChargePreferentialFeignClient;
    @Autowired
    private MerchantUserInfoFeignClient merchantUserInfoFeignClient;
    /**
     * 查询优惠列表
     */
    @PostMapping("/findPageList")
    @ResponseBody
    public JSONResult<PageBean<MerchantChargePreferentialDto>> findPageList(@RequestBody MerchantChargePreferentialReq merchantChargePreferentialReq) {
        // 管理端查询申请状态为已申请和已开具的发票记录
        log.info("查询客户管理申请列表参数" + merchantChargePreferentialReq.toString());
        JSONResult<PageBean<MerchantChargePreferentialDto>> jsonResult = merchantChargePreferentialFeignClient.findPageList(merchantChargePreferentialReq);
        return jsonResult;
    }


    /**
     * 新增修改充值会优惠
     */
    @PostMapping("/addOrUpdate")
    @ResponseBody

    public JSONResult<Boolean> addOrUpdate(@RequestBody MerchantChargePreferentialReq merchantChargePreferentialReq) {
        // 管理端查询申请状态为已申请和已开具的发票记录
        log.info("新增修改充值会优惠" + merchantChargePreferentialReq.toString());
        Date date = new Date();
        //判断是否全部
        if(merchantChargePreferentialReq.getUserIds().equals("all")){
            List<UserInfoDTO> merchantUserList = getMerchantUser(null);
            String userIds = String.join(",", ListUtils.emptyIfNull(merchantUserList).stream().map(UserInfoDTO::getId).map(String::valueOf).collect(Collectors.toList()));
            merchantChargePreferentialReq.setUserIds(userIds);
        }
        if(merchantChargePreferentialReq.getId()!=null){
            merchantChargePreferentialReq.setUpdateTime(date);
            merchantChargePreferentialReq.setUpdateUser(getUserId());
        }else{
            merchantChargePreferentialReq.setCreateTime(date);
            merchantChargePreferentialReq.setCreateUser(getUserId());
            merchantChargePreferentialReq.setUpdateTime(date);
            merchantChargePreferentialReq.setUpdateUser(getUserId());
            merchantChargePreferentialReq.setStatus(MerchantChargeStatusEnum.有效中.getStatus());
        }
        if(merchantChargePreferentialReq.getType().intValue()== MerchantChargeTypeEnum.时间有效.getName()){
            if(merchantChargePreferentialReq.getStartTime()==null || merchantChargePreferentialReq.getEndTime()==null){
                return new JSONResult<Boolean>().fail(JSONResult.FAIL,"请选择时间");
            }
        }
        JSONResult<Boolean> jsonResult = merchantChargePreferentialFeignClient.addOrUpdate(merchantChargePreferentialReq);
        return jsonResult;
    }

    /**
     * 批量删除充值优惠
     */
    @PostMapping("/batchDel")
    @ResponseBody
    public JSONResult<Boolean> batchDel(@RequestBody MerchantChargePreferentialReq  merchantChargePreferentialReq) {
        // 管理端查询申请状态为已申请和已开具的发票记录
        log.info("批量删除充值优惠" + merchantChargePreferentialReq.toString());
        merchantChargePreferentialReq.setUpdateTime(new Date());
        merchantChargePreferentialReq.setUpdateUser(getUserId());
        JSONResult<Boolean> jsonResult = merchantChargePreferentialFeignClient.batchDel(merchantChargePreferentialReq);
        return jsonResult;
    }



    /**
     * 获取当前登录账号ID
     *
     * @return
     */
    private long getUserId() {
        Object attribute = SecurityUtils.getSubject().getSession().getAttribute("user");
        UserInfoDTO user = (UserInfoDTO) attribute;
        if(user==null){
          return   1085777377507803136L;
        }
        return user.getId();
    }

    /**
     * 查询商家账号
     *
     * @param arrayList
     * @return
     */
    private List<UserInfoDTO> getMerchantUser(List<Integer> arrayList) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserType(SysConstant.USER_TYPE_TWO);
        userInfoDTO.setStatusList(arrayList);
        JSONResult<List<UserInfoDTO>> merchantUserList =
                merchantUserInfoFeignClient.merchantUserList(userInfoDTO);
        return merchantUserList.getData();
    }


}
