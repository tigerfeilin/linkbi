package com.linkbi.datax.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linkbi.datax.api.domain.DataViewChart;
import com.linkbi.datax.api.domain.DataViewDashboard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * metadata配置表数据库访问层
 *
 * @author
 * @version v1.0
 * @since 2021-04-22
 */
@Mapper
public interface DataViewDashboardMapper extends BaseMapper<DataViewDashboard> {
    List<DataViewDashboard> pageDashboardList(@Param("offset") int offset,
                                      @Param("pagesize") int pagesize,
                                      @Param("name") String name,
                                      @Param("isPrivate") String isPrivate,
                                      @Param("usrId") Long usrId);

    int pageDashboardListCount(@Param("name") String name,
                           @Param("isPrivate") String isPrivate,
                           @Param("usrId") Long usrId);
    DataViewDashboard getDashboardById(@Param("id") Long id);
}