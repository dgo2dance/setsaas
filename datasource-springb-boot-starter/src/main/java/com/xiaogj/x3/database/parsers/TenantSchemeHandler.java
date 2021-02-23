package com.xiaogj.x3.database.parsers;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.xiaogj.x3.database.tenant.TenantContextHandler;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @ClassName: TenantSchameTableNameHandler
 * @Description: 动态表名
 * @author: xiaolinlin
 * @date: 2020/9/9 21:12
 **/
@Slf4j
public class TenantSchemeHandler implements InnerInterceptor {

    @Override
    public void beforeQuery(
        Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler,
        BoundSql boundSql) throws SQLException {
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
            return;
        }
        mpBs.sql(this.changeTable(mpBs.sql()));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(this.changeTable(mpBs.sql()));
        }
    }

    private String changeTable(String sql) {
        String tenant = TenantContextHandler.getTenant();
        if (StrUtil.isNotEmpty(tenant)) {
            String schemaSql = ReplaceSql.replaceSql(tenant, sql);
            return schemaSql;
        }
        return sql;
    }
}
