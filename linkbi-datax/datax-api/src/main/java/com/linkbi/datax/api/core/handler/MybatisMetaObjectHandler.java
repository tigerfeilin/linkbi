package com.linkbi.datax.api.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.linkbi.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 通用的字段填充，如createBy createDate这些字段的自动填充
 *
 * @author huzekang
 */
@Component
@Slf4j
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        setFieldValByName("createDate", new Date(), metaObject);
        setFieldValByName("createBy", getCurrentUser(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("updateDate", new Date(), metaObject);
        setFieldValByName("updateBy", getCurrentUser(), metaObject);
    }

    private String getCurrentUser() {
        return SecurityUtils.getLoginUser().getUser().getUserName();//SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}