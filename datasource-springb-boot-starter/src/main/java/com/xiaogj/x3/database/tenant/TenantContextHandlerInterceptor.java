package com.xiaogj.x3.database.tenant;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.xiaogj.x3.database.common.TenantConstants;
import com.xiaogj.x3.database.datasource.DbContextHolder;
import com.xiaogj.x3.database.entity.ConnectionInfo;
import com.xiaogj.x3.database.service.ConnectionService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 租户信息解析器 用于将请求头中的租户编码和数据库名 封装到当前请求的线程变量中
 *
 * @author: xiaolinlin
 * @date: 2020/9/9 18:06
 */
@Slf4j
public class TenantContextHandlerInterceptor extends HandlerInterceptorAdapter {

    private static Map<String, ConnectionInfo> connectionInfoMap = new ConcurrentHashMap<String, ConnectionInfo>();

    private ConnectionService connectionService;

    public TenantContextHandlerInterceptor(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        String tenant = this.getHeader(request, TenantConstants.TENANT);
        if (StrUtil.isNotEmpty(tenant) && null != connectionService) {
            TenantContextHandler.setTenant(tenant);
            log.debug("==========从请求中获取到租户信息：{}", tenant);
            ConnectionInfo connectionInfo = connectionInfoMap.get(tenant);
            if (null == connectionInfo) {
                log.debug("==========未从当前容器中获取到租户[{}]对应的数据源，到业务中获取数据连接信息", tenant);
                connectionInfo = connectionService.getConnectionInfo(tenant);
                if (null != connectionInfo) {
                    connectionInfoMap.put(tenant, connectionInfo);
                }
            }
            DbContextHolder.setDbType(connectionInfo);
        }
        return super.preHandle(request, response, handler);
    }

    private String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StrUtil.isEmpty(value)) {
            value = request.getParameter(name);
        }
        if (StrUtil.isEmpty(value)) {
            return StrUtil.EMPTY;
        }
        return URLUtil.decode(value);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        TenantContextHandler.clear();
        DbContextHolder.clear();
        super.afterCompletion(request, response, handler, ex);
    }

}
