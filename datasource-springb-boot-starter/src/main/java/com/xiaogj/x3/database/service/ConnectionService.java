package com.xiaogj.x3.database.service;

import com.xiaogj.x3.database.entity.ConnectionInfo;

/**
 * @ClassName: ConnectionService
 * @Description: 获取数据库连接服务
 * @author: xiaolinlin
 * @date: 2020/9/9 10:32
 **/
public interface ConnectionService {

    /**
     * 通过租户获取连接信息
     *
     * @param tenant :
     * @return com.github.zuihou.database.datasource.dynamic.basic.ConnectionInfo
     * @author xiaolinlin
     * @date 10:32 2020/9/9
     **/
    ConnectionInfo getConnectionInfo(String tenant);
}
