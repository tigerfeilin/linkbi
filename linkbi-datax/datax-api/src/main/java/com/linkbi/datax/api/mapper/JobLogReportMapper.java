package com.linkbi.datax.api.mapper;

import com.linkbi.datax.api.domain.JobLogReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * job log
 *
 * @author xuxueli 2019-11-22
 */
@Mapper
public interface JobLogReportMapper {

    int save(JobLogReport xxlJobLogReport);

    int update(JobLogReport xxlJobLogReport);

    List<JobLogReport> queryLogReport(@Param("triggerDayFrom") Date triggerDayFrom,
                                      @Param("triggerDayTo") Date triggerDayTo);

    JobLogReport queryLogReportTotal();

}
