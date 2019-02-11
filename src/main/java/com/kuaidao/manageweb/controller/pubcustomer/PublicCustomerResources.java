package com.kuaidao.manageweb.controller.pubcustomer;

import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.common.entity.PageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author  yangbiao
 * @Date: 2019/2/11 15:08
 * @Description:
 *      公共客户资源
 */
public class PublicCustomerResources {

    private static Logger logger = LoggerFactory.getLogger(PublicCustomerResources.class);

    /**
     * 分配资源
     */
    @PostMapping("/allocationResource")
    @ResponseBody
    public JSONResult<PageBean>   allocationResource(){
        return null;
    }

    /**
     * 转移资源
     */
    @PostMapping("/releaseRecord")
    @ResponseBody
    public JSONResult<PageBean>   transferOfResource(){
        return null;
    }

    /**
     * 释放记录
     */
    @PostMapping("/releaseRecord")
    @ResponseBody
    public JSONResult<PageBean> releaseRecord(){
        return null;
    }

    /**
     * 资源还原
     */
    @PostMapping("/resourceReduction")
    @ResponseBody
    public JSONResult<PageBean> resourceReduction(){
        return null;
    }
    /**
     * 分页查询
     */
    @RequestMapping("/listPage")
    public String listPage(){
        logger.info("------------ 公共客户资源列表 ---------------");
        return "dictionary/dicListPage";
    }
    @PostMapping("/queryPage")
    @ResponseBody
    public JSONResult<PageBean> queryListPage(){
        return null;
    }
    /**
     * 重复手机号
     */
    @PostMapping("/repeatPhones")
    @ResponseBody
    public JSONResult<PageBean> repeatPhones(){
        return null;
    }

    /**
     * 跟进记录
     */
    @PostMapping("/followUpRecord")
    @ResponseBody
    public JSONResult<PageBean> followUpRecord(){
        return null;
    }

}
