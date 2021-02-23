package com.xiaogj.x3.database.entity;

import com.xiaogj.x3.database.datasource.ConnectionDriverEnum;
import lombok.Data;

/**
 * @ClassName: ConnectionInfo
 * @Description: 数据连接信息
 * @author: xiaolinlin
 * @date: 2020/9/9 10:30
 **/
@Data
public class ConnectionInfo {

    /**
     * 租户信息
     */
    private String tenant;

    /**
     * 数据连接地址
     */
    private String url;
    /**
     * 连接驱动，默认mysql数据源
     */
    private String driver = ConnectionDriverEnum.MYSQL_SERVER.getDriver();
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
