/**
 * 
 */
package com.kuaidao.manageweb.controller.role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.sys.dto.role.RoleQueryDTO;

/**
 * @author gpc
 *
 */

/**
 * 自定义字段
 * 
 * @author: Chen Chengxue
 * @date: 2018年12月28日 下午1:45:12
 * @version V1.0
 */
@Controller
@RequestMapping("/role/roleManager")
public class RoleController {

    private RoleManagerFeignClient roleManagerFeignClient;

    /***
     * 自定义字段 首页
     * 
     * @return
     */
    @RequestMapping("/initRoleInfo")
    public String initRoleInfo() {

        return "role/roleList";
    }

    /***
     * 自定义字段 首页
     * 
     * @return
     */
    @RequestMapping("/queryRoleList")
    public String queryRoleList(@RequestParam RoleQueryDTO dto, HttpServletRequest request,
            HttpServletResponse response) {

        roleManagerFeignClient.queryRoleList(dto);

        return "role/roleList";
    }
}
