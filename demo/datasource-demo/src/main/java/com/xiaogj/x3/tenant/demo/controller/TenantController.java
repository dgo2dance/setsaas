package com.xiaogj.x3.tenant.demo.controller;

import com.xiaogj.x3.database.tenant.TenantContextHandler;
import com.xiaogj.x3.tenant.demo.entity.UserInfo;
import com.xiaogj.x3.tenant.demo.mapper.UserMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TenantController
 * @Description:
 * @author: xiaolinlin
 * @date: 2020/9/9 21:56
 **/
@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/test")
    public String test() {
        return TenantContextHandler.getTenant();
    }

    @RequestMapping("/getUsers")
    public List<UserInfo> getUsers() {
        return userMapper.selectAll();
    }
}
