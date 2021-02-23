package com.xiaogj.x3.database.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.xiaogj.x3.database.datasource.AbstractDynamicDataSource;
import com.xiaogj.x3.database.datasource.DruidDynamicDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @ClassName: DynamicDataSourceConfiguration
 * @Description: 动态多数据源
 * @author: xiaolinlin
 * @date: 2020/9/9 10:33
 **/
@Slf4j
@Configuration
@ConditionalOnProperty(name = "xiaogj.x3.dynamic.enabled", havingValue = "true")
@ComponentScan(basePackages = "com.xiaogj.x3.database")
public class DynamicDataSourceConfiguration {

    // -----------------------------------------default config-------------------------------------

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    // -----------------------------------------druid config-------------------------------------


    @Value("${spring.datasource.druid.validation-query}")
    private String validationQuery;

    @Value("${spring.datasource.druid.initial-size}")
    private int initialSize;

    @Value("${spring.datasource.druid.min-idle}")
    private int minIdle;

    @Value("${spring.datasource.druid.max-active}")
    private int maxActive;

    @Value("${spring.datasource.druid.max-wait}")
    private int maxWait;

    @Value("${spring.datasource.druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.test-while-idle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.test-on-borrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.druid.test-on-return}")
    private boolean testOnReturn;

    @Value("${spring.datasource.druid.pool-prepared-statements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.druid.max-pool-prepared-statement-per-connection-size}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.druid.filters}")
    private String filters;

    @Value("${spring.datasource.druid.connection-properties}")
    private String connectionProperties;


    /**
     * 优先使用，动态数据源
     *
     * @return javax.sql.DataSource
     * @author wuzhichang
     * @date 14:00 2020/8/20
     **/
    @Bean(name = "druidDynamicDataSource")
    @Primary
    public DataSource dataSource() {
        log.debug("==========启用动态数据源=============");
        DruidDynamicDataSource dynamicDataSource = new DruidDynamicDataSource();
        DataSource defaultSource = dataSourceDefault();
        //设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(defaultSource);
        //配置多个数据源
        Map<Object, Object> map = new ConcurrentHashMap<Object, Object>(1);
        map.put(AbstractDynamicDataSource.DEFAULT_DATASOURCE_KEY, defaultSource);
        dynamicDataSource.setTargetDataSources(map);
        return dynamicDataSource;
    }

    /**
     * dataSourceDefault
     *
     * @return javax.sql.DataSource
     * @author wuzhichang
     * @date 14:01 2020/8/20
     **/
    public DataSource dataSourceDefault() {
        log.debug("==========加载默认数据源=============");
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setValidationQuery(validationQuery);
        // 设置druid数据源的属性
        setDruidOptions(datasource);
        return datasource;
    }

    /**
     * txManager
     *
     * @return org.springframework.transaction.PlatformTransactionManager
     * @author wuzhichang
     * @date 14:00 2020/8/20
     **/

    @Bean
    public PlatformTransactionManager txManager(@Qualifier("druidDynamicDataSource") DataSource dataSource) {
        // 事务管理
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * druidServlet
     *
     * @return org.springframework.boot.web.servlet.ServletRegistrationBean
     * @author wuzhichang
     * @date 14:00 2020/8/20
     **/
    @Bean(name = "druidServlet")
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        // 白名单
        reg.addInitParameter("allow", "");
        return reg;
    }

    /**
     * filterRegistrationBean
     *
     * @return org.springframework.boot.web.servlet.FilterRegistrationBean
     * @author wuzhichang
     * @date 14:00 2020/8/20
     **/
    @Bean(name = "filterRegistrationBean")
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");
        filterRegistrationBean.addInitParameter("DruidWebStatFilter", "/*");
        return filterRegistrationBean;
    }


    private WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    private WallConfig wallConfig() {
        WallConfig config = new WallConfig();
        //允许一次执行多条语句
        config.setMultiStatementAllow(true);
        //允许非基本语句的其他语句
        config.setNoneBaseStatementAllow(true);
        return config;
    }

    private void setDruidOptions(DruidDataSource datasource) {
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        /*
         * try { datasource.setFilters(filters); } catch (SQLException e) {
         * logger.error("druid configuration initialization filter Exception", e); }
         */
        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(wallFilter());
        datasource.setProxyFilters(filterList);
    }
}
