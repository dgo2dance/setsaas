package com.xiaogj.x3.database.tenant;

import com.xiaogj.x3.database.service.ConnectionService;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 多租户配置
 *
 * @author: xiaolinlin
 * @date: 2020/9/9 18:06
 */
public class TenantWebMvcConfigurer implements WebMvcConfigurer {


    private ConnectionService connectionService;

    public TenantWebMvcConfigurer(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public TenantWebMvcConfigurer() {

    }

    /**
     * 注册 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (getHandlerInterceptor() != null) {
            registry.addInterceptor(getHandlerInterceptor())
                .addPathPatterns("/**")
                .order(-19);
        }
    }

    protected HandlerInterceptor getHandlerInterceptor() {
        return new TenantContextHandlerInterceptor(connectionService);
    }

}
