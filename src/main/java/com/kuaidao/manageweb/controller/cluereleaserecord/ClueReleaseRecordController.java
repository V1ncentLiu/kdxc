package com.kuaidao.manageweb.controller.cluereleaserecord;

import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordInsertOrUpdateDTO;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordReqDTO;
import com.kuaidao.aggregation.dto.cluereleaserecord.ReleaseRecordRespDTO;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import com.kuaidao.common.util.CommonUtil;
import com.kuaidao.manageweb.feign.releaserecord.ReleaseRecordFeignClient;
import com.kuaidao.manageweb.feign.user.UserInfoFeignClient;
import com.kuaidao.sys.dto.user.UserInfoDTO;
import com.kuaidao.sys.dto.user.UserInfoPageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @author yangbiao
 * 接口层
 * Created  on 2019-2-12 15:06:38
 *  资源释放记录 对外接口类
 */

@RestController
@RequestMapping("/aggregation/releaserecord")
public class ClueReleaseRecordController {

    private static Logger logger = LoggerFactory.getLogger(ClueReleaseRecordController.class);

    @Autowired
    private ReleaseRecordFeignClient releaseRecordFeignClient;

    @Autowired
    UserInfoFeignClient userInfoFeignClient;

    /**
     * 新增
     */
    @RequestMapping("/insert")
    public JSONResult<Boolean> saveReleaseRecord(@Valid @RequestBody ReleaseRecordInsertOrUpdateDTO dto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return CommonUtil.validateParam(result);
        }
        logger.info("插入一条资源释放记录");
        return new JSONResult().success( releaseRecordFeignClient.saveReleaseRecord(dto));
    }

    /**
     * 分页查询
     */
    @RequestMapping("/queryPageList")
    public JSONResult<PageBean<ReleaseRecordRespDTO>> queryPageList(@RequestBody ReleaseRecordReqDTO dto){
        JSONResult<PageBean<ReleaseRecordRespDTO>> result = releaseRecordFeignClient.queryPageList(dto);
        return result;
    }

}
