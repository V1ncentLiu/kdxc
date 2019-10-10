package com.kuaidao.manageweb.controller.merchant.client;

import com.kuaidao.callcenter.dto.merchantClient.merchantClientLoginReq;
import com.kuaidao.aggregation.dto.client.ClientLoginReCordDTO;
import com.kuaidao.aggregation.dto.client.QimoClientRespDTO;
import com.kuaidao.callcenter.dto.TrAxbOutCallReqDTO;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerReq;
import com.kuaidao.callcenter.dto.seatmanager.SeatManagerResp;
import com.kuaidao.common.constant.SysErrorCodeEnum;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.config.LogRecord;
import com.kuaidao.manageweb.config.LogRecord.OperationType;
import com.kuaidao.manageweb.constant.Constants;
import com.kuaidao.manageweb.constant.MenuEnum;
import com.kuaidao.manageweb.controller.homepage.HomePageController;
import com.kuaidao.manageweb.feign.client.ClientFeignClient;
import com.kuaidao.manageweb.feign.merchant.seatmanager.SeatManagerFeignClient;
import com.kuaidao.manageweb.util.CommUtil;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on: 2019-10-10-15:45
 */
@Controller
@RequestMapping("/merchantClient")
public class MerchantClientController {
    private static Logger logger = LoggerFactory.getLogger(MerchantClientController.class);
    @Autowired
    private SeatManagerFeignClient seatManagerFeignClient;
    @Autowired
    private ClientFeignClient clientFeignClient;

    /**
     *  坐席自动登录
     * @param reqDTO
     * @return
     */
    @PostMapping("/clientLogin")
    @ResponseBody
    @LogRecord(description = "坐席自动登录", operationType = OperationType.CLIENT_LOGIN,
        menuName = MenuEnum.MERCHANT_CLIENT_LOGIN)
    public JSONResult clientLogin(@RequestBody merchantClientLoginReq reqDTO) {
        //根据登录账号查询绑定坐席
        UserInfoDTO curLoginUser = CommUtil.getCurLoginUser();
        Long accountId = curLoginUser.getId();
        SeatManagerReq seatManagerReq = new SeatManagerReq();
        seatManagerReq.setSubMerchant(accountId);
        JSONResult<SeatManagerResp> seatManagerJr =  seatManagerFeignClient.queryListBySubMerchant(seatManagerReq);
        if(JSONResult.SUCCESS.equals(seatManagerJr.getCode())) {
            SeatManagerResp seatManagerResp = seatManagerJr.getData();
            if(seatManagerResp==null) {
                return new JSONResult<>().fail(SysErrorCodeEnum.ERR_NOTEXISTS_DATA.getCode(),"此用户未绑定坐席");
            }else {
                //根据绑定服务商家类型进行不同登录逻辑（后续补充）
//                if(seatManagerResp.getType() == 1){ }
//                else if(seatManagerResp.getType() == 2){ }
//                else if(seatManagerResp.getType() == 3){ }
//                else { }
                //第一阶段开发只考虑七陌，七陌商家版默认为手机外显（小号）登录，bindType = 2
                String bindType = Constants.BIND_TYPE_TWO;
                //服务商家1天润2七陌3合力，目前默认七陌，后续补充后可直接使用seatManagerResp.getType值
                reqDTO.setClientType(2);
                Session session = SecurityUtils.getSubject().getSession();
                session.setAttribute("seatNo", seatManagerResp.getSeatNo());
                session.setAttribute("bindType", bindType);

                ClientLoginReCordDTO clientLoginRecord = new ClientLoginReCordDTO();
                clientLoginRecord.setAccountId(curLoginUser.getId());
                clientLoginRecord.setAccountType(reqDTO.getAccountType());
                clientLoginRecord.setOrgId(curLoginUser.getOrgId());
                clientLoginRecord.setCno(seatManagerResp.getSeatNo());
                clientLoginRecord.setClientType(reqDTO.getClientType());
                clientLoginRecord.setAccountNo(seatManagerResp.getAcountNo());
                JSONResult<Boolean> loginRecordJr = clientFeignClient.clientLoginRecord(clientLoginRecord);
                if(!JSONResult.SUCCESS.equals(loginRecordJr.getCode())) {
                    logger.error("merchant_login_put_redis,param{{}},res{{}}",clientLoginRecord,loginRecordJr);
                    return  new JSONResult<>().fail(loginRecordJr.getCode(),loginRecordJr.getMsg());
                }
                return new JSONResult<>().success(true);
            }
        }else {
            logger.error("商家坐席自动登录，通过账号查询,param{{}},res{{}}",seatManagerReq,seatManagerJr);
        }
        return new JSONResult<>().fail(SysErrorCodeEnum.ERR_REST_FAIL.getCode(),SysErrorCodeEnum.ERR_REST_FAIL.getMessage());
    }

}
