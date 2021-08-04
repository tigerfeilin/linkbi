package com.linkbi.framework.config;

import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement(proxyTargetClass = true)
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {

        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        return paginationInterceptor.setOverflow(true);
    }

    /**
     * MyBatisPlus逻辑删除 ，需要在 yml 中配置开启
     * 3.0.7.1版本的LogicSqlInjector里面什么都没做只是 extends DefaultSqlInjector
     * 以后版本直接去的了LogicSqlInjector
     *
     * @return
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new DefaultSqlInjector();
    }
    /*
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        // 乐观锁插件
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        // 阻断插件
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor());
        return interceptor;
    }

     */

    /**
     * 分页插件，自动识别数据库类型
     * https://baomidou.com/guide/interceptor-pagination.html
     */
    /*
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型为mysql
        paginationInnerInterceptor.setDbType(DbType.POSTGRE_SQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        return paginationInnerInterceptor;
    }

     */

    /**
     * 乐观锁插件
     * https://baomidou.com/guide/interceptor-optimistic-locker.html
     */
//    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
//        return new OptimisticLockerInnerInterceptor();
//    }

    /**
     * 如果是对全表的删除或更新操作，就会终止该操作
     * https://baomidou.com/guide/interceptor-block-attack.html
     */
//    public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
//        return new BlockAttackInnerInterceptor();
//    }

    /**
     * sql性能规范插件(垃圾SQL拦截)
     * 如有需要可以启用
     */
//	public IllegalSQLInnerInterceptor illegalSQLInnerInterceptor() {
//		return new IllegalSQLInnerInterceptor();
//	}


    /**
     * 自定义主键策略
     * https://baomidou.com/guide/id-generator.html
     */
//	@Bean
//	public IdentifierGenerator idGenerator() {
//		return new CustomIdGenerator();
//	}

    /**
     * 元对象字段填充控制器
     * https://baomidou.com/guide/auto-fill-metainfo.html
     */
//	@Bean
//	public MetaObjectHandler metaObjectHandler() {
//		return new MyMetaObjectHandler();
//	}

    /**
     * sql注入器配置
     * https://baomidou.com/guide/sql-injector.html
     */
//	@Bean
//	public ISqlInjector sqlInjector() {
//		return new DefaultSqlInjector();
//	}

    /**
     * TenantLineInnerInterceptor 多租户插件
     * https://baomidou.com/guide/interceptor-tenant-line.html
     * DynamicTableNameInnerInterceptor 动态表名插件
     * https://baomidou.com/guide/interceptor-dynamic-table-name.html
     */

}

