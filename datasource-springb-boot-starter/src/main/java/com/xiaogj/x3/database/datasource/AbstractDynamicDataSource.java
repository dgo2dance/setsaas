package com.xiaogj.x3.database.datasource;

import com.xiaogj.x3.database.entity.ConnectionInfo;
import com.xiaogj.x3.database.tenant.TenantContextHandler;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源父类
 *
 * @author: xiaolinlin
 * @date: 2020/9/9 18:06
 */
@Slf4j
public abstract class AbstractDynamicDataSource<T extends DataSource> extends AbstractRoutingDataSource
    implements ApplicationContextAware {

    /** 默认的数据源KEY，和spring配置文件中的id=druidDynamicDataSource的bean中配置的默认数据源key保持一致 */
    public static final String DEFAULT_DATASOURCE_KEY = "defaultDataSource";

    /** 数据源KEY-VALUE键值对 */
    public Map<Object, Object> targetDataSources;

    /** spring容器上下文 */
    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public static Object getBean(String name) {
        return ctx.getBean(name);
    }

    /**
     * @param targetDataSources the targetDataSources to set
     */
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
        super.setTargetDataSources(targetDataSources);
        // afterPropertiesSet()方法调用时用来将targetDataSources的属性写入resolvedDataSources中的
        super.afterPropertiesSet();
    }

    /**
     * 创建数据源
     *
     * @param driverClassName 数据库驱动名称
     * @param url 连接地址
     * @param username 用户名
     * @param password 密码
     * @return 数据源{@link T}
     */
    public abstract T createDataSource(String driverClassName, String url, String username, String password);

    /**
     * 设置系统当前使用的数据源
     * <p>
     * 数据源为空或者为0时，自动切换至默认数据源，即在配置文件中定义的默认数据源
     *
     * @see AbstractRoutingDataSource#determineCurrentLookupKey()
     */
    @Override
    protected Object determineCurrentLookupKey() {
        ConnectionInfo connectionInfo = DbContextHolder.getDbType();
        if (null == connectionInfo) {
            log.debug("==========未设置数据源，使用系统默认数据源");
            // 使用默认数据源
            return DEFAULT_DATASOURCE_KEY;
        }
        // 判断数据源是否需要初始化
        this.verifyAndInitDataSource(connectionInfo);
        return connectionInfo.getTenant();
    }

    /**
     * 判断数据源是否需要初始化
     */
    private void verifyAndInitDataSource(ConnectionInfo connectionInfo) {
        // 数据源已初始化
        Object obj = this.targetDataSources.get(connectionInfo.getTenant());
        if (obj != null) {
            // 已找到数据源
            return;
        }
        log.debug("==========数据源未初始化，开始初始化租户：[{}]的数据源", TenantContextHandler.getTenant());
        // DruidDataSource
        T datasource = this.createDataSource(connectionInfo.getDriver(),
            connectionInfo.getUrl(),
            connectionInfo.getUsername(),
            connectionInfo.getPassword());
        this.addTargetDataSource(connectionInfo.getTenant(), datasource);
    }

    /**
     * 往数据源key-value键值对集合添加新的数据源
     *
     * @param key 新的数据源键
     * @param dataSource 新的数据源
     */
    private void addTargetDataSource(String key, T dataSource) {
        log.debug("==========添加当前租户：[{}]的数据源到容器", key);
        this.targetDataSources.put(key, dataSource);
        super.setTargetDataSources(this.targetDataSources);
        // afterPropertiesSet()方法调用时用来将targetDataSources的属性写入resolvedDataSources中的
        // 重建现有的多个数据源
        super.afterPropertiesSet();
    }


}
