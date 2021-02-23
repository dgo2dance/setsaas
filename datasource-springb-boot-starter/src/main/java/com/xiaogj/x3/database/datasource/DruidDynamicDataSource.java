package com.xiaogj.x3.database.datasource;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.xiaogj.x3.database.tenant.TenantContextHandler;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Druid数据源
 * <p>
 * 摘抄自http://www.68idc.cn/help/buildlang/java/20160606618505.html
 *
 * @author: xiaolinlin
 * @date: 2020/9/9 18:06
 */
@Slf4j
public class DruidDynamicDataSource extends AbstractDynamicDataSource<DruidDataSource> {

    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;

    /** 是否打开连接泄露自动检测 */
    private boolean removeAbandoned = false;
    /** 连接长时间没有使用，被认为发生泄露时长 */
    private long removeAbandonedTimeoutMillis = 300 * 1000;
    /** 发生泄露时是否需要输出 log，建议在开启连接泄露检测时开启，方便排错 */
    private boolean logAbandoned = false;

    // 只要maxPoolPreparedStatementPerConnectionSize>0,poolPreparedStatements就会被自动设定为true，使用oracle时可以设定此值。
    // private int maxPoolPreparedStatementPerConnectionSize = -1;

    /** 配置监控统计拦截的filters */
    private String filters;
    /** 监控统计："stat" 防SQL注入："wall" 组合使用： "stat,wall" */
    private List<Filter> filterList;

    /**
     * 创建数据源，这里创建的数据源是带有连接池属性的
     *
     * @see
     */
    @Override
    public DruidDataSource createDataSource(String driverClassName, String url, String username, String password) {
        log.warn("==========开始创建租户：[{}]数据源连接", TenantContextHandler.getTenant());
        DruidDataSource defaultDataSource = (DruidDataSource) targetDataSources.get(DEFAULT_DATASOURCE_KEY);
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        ds.setInitialSize(defaultDataSource.getInitialSize());
        ds.setMinIdle(defaultDataSource.getMinIdle());
        ds.setMaxActive(defaultDataSource.getMaxActive());
        ds.setMaxWait(defaultDataSource.getMaxWait());
        ds.setTimeBetweenConnectErrorMillis(defaultDataSource.getTimeBetweenConnectErrorMillis());
        ds.setTimeBetweenEvictionRunsMillis(defaultDataSource.getTimeBetweenEvictionRunsMillis());
        ds.setMinEvictableIdleTimeMillis(defaultDataSource.getMinEvictableIdleTimeMillis());

        ds.setValidationQuery(defaultDataSource.getValidationQuery());
        ds.setTestWhileIdle(testWhileIdle);
        ds.setTestOnBorrow(testOnBorrow);
        ds.setTestOnReturn(testOnReturn);

        ds.setRemoveAbandoned(removeAbandoned);
        ds.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis);
        ds.setLogAbandoned(logAbandoned);

        ds.setMaxPoolPreparedStatementPerConnectionSize(defaultDataSource.getMaxPoolPreparedStatementPerConnectionSize());

        if (StrUtil.isNotBlank(filters)) {
            try {
                ds.setFilters(filters);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        addFilterList(ds);
        return ds;
    }

    private void addFilterList(DruidDataSource ds) {
        if (filterList != null) {
            List<Filter> targetList = ds.getProxyFilters();
            for (Filter add : filterList) {
                boolean found = false;
                for (Filter target : targetList) {
                    if (add.getClass().equals(target.getClass())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    targetList.add(add);
                }
            }
        }
    }

    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    public WallConfig wallConfig() {
        WallConfig config = new WallConfig();
        // 允许一次执行多条语句
        config.setMultiStatementAllow(true);
        // 允许非基本语句的其他语句
        config.setNoneBaseStatementAllow(true);
        return config;
    }
}
