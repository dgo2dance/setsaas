package com.xiaogj.x3.database.common;

/**
 * @ClassName: TenantEum
 * @Description: 租户模式
 * @author: xiaolinlin
 * @date: 2020/9/9 20:27
 **/
public enum TenantEnum {
    /**
     * 非租户模式
     */
    NONE("非租户模式"),
    /**
     * 字段模式 在sql中拼接 tenant_code 字段
     */
    COLUMN("字段模式"),
    /**
     * 独立schema模式 在sql中拼接 数据库 schema
     */
    SCHEMA("独立schema模式"),
    /**
     * 独立数据源模式
     * <p>
     * 该模式不开源，购买咨询作者。
     */
    DATASOURCE("独立数据源模式"),
    ;
    String describe;

    TenantEnum(String tenantDesc) {
        this.describe = tenantDesc;
    }

    /**
     * 获取租户描述
     *
     * @return
     */
    public String getDescribe() {
        return describe;
    }

    /**
     * 判断租户模式
     *
     * @param val
     * @return
     */
    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }
}
