/**
 * 
 */
package com.kuaidao.manageweb.controller.role;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kuaidao.common.entity.JSONResult;
import com.kuaidao.manageweb.feign.role.RoleManagerFeignClient;
import com.kuaidao.sys.dto.role.RoleInfoDTO;
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
public class RoleManagerController {

    @Autowired
    private RoleManagerFeignClient roleManagerFeignClient;

    /***
     * 
     * @return
     */
    @RequestMapping("/initRoleInfo")
    public String initRoleInfo() {
        return "role/roleManagePage";
    }

    /***
     * 自定义字段 首页
     * 
     * @return
     */
    @RequestMapping("/addRolePre")
    public String addRolePre() {

        return "role/addRole";
    }

    /***
     * 自定义字段 首页
     * 
     * @return
     */
    @RequestMapping("/queryRoleList")
    @ResponseBody
    public JSONResult<List<RoleInfoDTO>> queryRoleList(@RequestBody RoleQueryDTO dto,
            HttpServletRequest request, HttpServletResponse response) {
        return roleManagerFeignClient.queryRoleList(dto);
    }


}
