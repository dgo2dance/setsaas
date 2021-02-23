package com.xiaogj.x3.database.datasource;

import com.xiaogj.x3.database.entity.ConnectionInfo;

/**
 * 当前正在使用的数据源信息的线程上线文
 *
 * @author: xiaolinlin
 * @date: 2020/9/9 18:06
 */
public class DbContextHolder {

    private static final ThreadLocal<ConnectionInfo> CONTEXT_HOLDER = new ThreadLocal<ConnectionInfo>();

    public static void setDbType(ConnectionInfo connectionInfo) {
        CONTEXT_HOLDER.set(connectionInfo);
    }

    public static ConnectionInfo getDbType() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
