package com.xiaogj.x3.database.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 自动填充时间字段
 *
 * @author xiaolinlin
 */
@Slf4j
public class MateMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("Insert 自动补全 createTime,updateTime");
        this.strictUpdateFill(metaObject, "createTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("Update 自动更新 updateTime");
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
