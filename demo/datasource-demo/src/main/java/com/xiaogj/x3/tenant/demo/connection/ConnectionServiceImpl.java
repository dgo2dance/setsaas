package com.xiaogj.x3.tenant.demo.connection;

import com.xiaogj.x3.database.datasource.ConnectionDriverEnum;
import com.xiaogj.x3.database.entity.ConnectionInfo;
import com.xiaogj.x3.database.service.ConnectionService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ConnectionServiceImpl
 * @Description:
 * @author: xiaolinlin
 * @date: 2020/9/9 21:53
 **/
@Service
public class ConnectionServiceImpl implements ConnectionService {

    /**
     * 通过租户获取连接信息
     *
     * @param tenant :
     * @return com.github.zuihou.database.datasource.dynamic.basic.ConnectionInfo
     * @author xiaolinlin
     * @date 10:32 2020/9/9
     **/
    @Override
    public ConnectionInfo getConnectionInfo(String tenant) {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDriver(ConnectionDriverEnum.MYSQL_SERVER.getDriver());
        if ("pms".equals(tenant)) {
            connectionInfo.setUrl(
                "jdbc:mysql://116.62.162.235:3306/mall_pms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true");
            connectionInfo.setUsername("mall");
            connectionInfo.setPassword("xiaogj2020");
            connectionInfo.setTenant("pms");
        } else if ("cms".equals(tenant)) {
            connectionInfo.setUrl(
                "jdbc:mysql://116.62.162.235:3306/mall_cms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true");
            connectionInfo.setUsername("mall");
            connectionInfo.setPassword("xiaogj2020");
            connectionInfo.setTenant("cms");
        } else {
            connectionInfo = null;
        }
        return connectionInfo;
    }
}
