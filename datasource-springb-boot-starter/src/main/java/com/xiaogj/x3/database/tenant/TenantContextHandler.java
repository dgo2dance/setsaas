package com.xiaogj.x3.database.tenant;

/**
 * @ClassName: TenantContextHandler
 * @Description: 租户 线程上下文
 * @author: xiaolinlin
 * @date: 2020/9/9 18:06
 **/
public class TenantContextHandler {

    /**
     * 租户线程上下文
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

    /**
     * 设置一个租户
     *
     * @param tenant
     */
    public static void setTenant(String tenant) {
        CONTEXT_HOLDER.set(tenant);
    }

    /**
     * 获取租户
     *
     * @return
     */
    public static String getTenant() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清楚线程上下文
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
