package com.linkbi.datax.api.tool.datax;

import com.linkbi.datax.api.domain.MetaDataSource;

public class DataxJsonHelperTest {

    private MetaDataSource getReaderDatasource() {
        MetaDataSource readerDatasource = new MetaDataSource();
        readerDatasource.setDatasourceName("z01_mysql_3306");
        readerDatasource.setJdbcUsername("root");
        readerDatasource.setJdbcPassword("root");
        readerDatasource.setJdbcUrl("jdbc:mysql://localhost:3306/datax_web?serverTimezone=Asia/Shanghai&useLegacyDatetimeCode=false&useSSL=false&nullNamePatternMatchesAll=true&useUnicode=true&characterEncoding=UTF-8");
        readerDatasource.setJdbcDriverClass("com.mysql.jdbc.Driver");
        return readerDatasource;
    }

    private MetaDataSource getWriterDatasource() {
        MetaDataSource writerDatasource = new MetaDataSource();
        writerDatasource.setDatasourceName("z01_mysql_3306");
        writerDatasource.setJdbcUsername("root");
        writerDatasource.setJdbcPassword("root");
        writerDatasource.setJdbcUrl("jdbc:mysql://localhost:3306/datax_web_demo?serverTimezone=Asia/Shanghai&useLegacyDatetimeCode=false&useSSL=false&nullNamePatternMatchesAll=true&useUnicode=true&characterEncoding=UTF-8");
        writerDatasource.setJdbcDriverClass("com.mysql.jdbc.Driver");
        return writerDatasource;
    }

}
