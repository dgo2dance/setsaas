package com.xiaogj.x3.tenant.demo.mapper;

import com.xiaogj.x3.tenant.demo.entity.UserInfo;
import java.util.List;

/**
 * @ClassName: UserMapper
 * @Description: 测试
 * @author: xiaolinlin
 * @date: 2020/9/10 9:55
 **/
public interface UserMapper {

    /**
     * 查询所有
     *
     * @return java.util.List<com.xiaogj.x3.tenant.demo.entity.UserInfo>
     * @throws
     * @author xiaolinlin
     * @date 9:57 2020/9/10
     **/
    List<UserInfo> selectAll();
}
