package com.xiaogj.x3.database.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.xiaogj.x3.database.common.TenantEnum;
import com.xiaogj.x3.database.handler.MateMetaObjectHandler;
import com.xiaogj.x3.database.parsers.TenantSchemeHandler;
import com.xiaogj.x3.database.service.ConnectionService;
import com.xiaogj.x3.database.tenant.TenantContextHandler;
import com.xiaogj.x3.database.tenant.TenantWebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @ClassName: DynamicMybatisConfiguration
 * @Description: 多租户
 * @author: xiaolinlin
 * @date: 2020/9/9 13:55
 **/
@Slf4j
@Configuration
@ConditionalOnProperty(name = "xiaogj.x3.tenant.enabled", havingValue = "true")
@ComponentScan(basePackages = "com.xiaogj.x3.database")
public class TenantMybatisConfiguration {

    /**
     * 租户
     */
    @Value("${xiaogj.x3.tenant.type:schema}")
    private String tenant;

    /**
     * 租户字段
     */
    @Value("${xiaogj.x3.tenant.column:}")
    private String tenantColumn;

    /**
     * 单页分页条数限制(默认无限制,参见 插件#handlerLimit 方法)
     */
    private static final Long MAX_LIMIT = 1000L;

    /**
     * 分页插件，自动识别数据库类型 多租户，请参考官网【插件扩展】
     */
    @Order(5)
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        log.info("==========已为您开启[{}]租户模式=============", tenant);
        //动态"表名" 插件 来实现 租户schema切换 加入解析链
        if (TenantEnum.SCHEMA.eq(tenant)) {
            TenantSchemeHandler tenantSchemeHandler = new TenantSchemeHandler();
            interceptor.addInnerInterceptor(tenantSchemeHandler);
        } else if (TenantEnum.COLUMN.eq(tenant)) {
            TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor(new TenantLineHandler() {
                /**
                 * 获取租户ID
                 * @return
                 */
                @Override
                public Expression getTenantId() {
                    String tenant = TenantContextHandler.getTenant();
                    if (tenant != null) {
                        return new StringValue(tenant);
                    }
                    return new NullValue();
                }

                /**
                 * 获取多租户的字段名
                 * @return String
                 */
                @Override
                public String getTenantIdColumn() {
                    return tenantColumn;
                }

                /**
                 * 过滤不需要根据租户隔离的表
                 * 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
                 * @param tableName 表名
                 */
                @Override
                public boolean ignoreTable(String tableName) {
                    // return tenantProperties.getIgnoreTables().stream().anyMatch(
                    //     (t) -> t.equalsIgnoreCase(tableName)
                    // );
                    return false;
                }
            });
            interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        }

        //分页插件: PaginationInnerInterceptor
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setMaxLimit(MAX_LIMIT);
        BlockAttackInnerInterceptor blockAttackInnerInterceptor = new BlockAttackInnerInterceptor();
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor);
        return interceptor;
    }

    /**
     * 自动填充数据 updatetime createtime等
     */
    @Bean
    @ConditionalOnMissingBean(MateMetaObjectHandler.class)
    public MateMetaObjectHandler mateMetaObjectHandler() {
        MateMetaObjectHandler metaObjectHandler = new MateMetaObjectHandler();
        log.info("MateMetaObjectHandler [{}]", metaObjectHandler);
        return metaObjectHandler;
    }

    /**
     * gateway 网关模块需要禁用 spring-webmvc 相关配置，必须通过在类上面加限制条件方式来实现， 不能直接Bean上面加
     */
    @ConditionalOnProperty(prefix = "xiaogj.x3.tenant.webmvc", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class WebMvcConfig {

        @Bean
        @ConditionalOnProperty(prefix = "xiaogj.x3.tenant.webmvc", name = "enabled", havingValue = "true", matchIfMissing = true)
        @ConditionalOnBean(ConnectionService.class)
        public TenantWebMvcConfigurer getTenantWebMvcConfigurer(ConnectionService connectionService) {
            log.debug("==========初始化获取租户信息的拦截器=============");
            return new TenantWebMvcConfigurer(connectionService);
        }

    }

}
