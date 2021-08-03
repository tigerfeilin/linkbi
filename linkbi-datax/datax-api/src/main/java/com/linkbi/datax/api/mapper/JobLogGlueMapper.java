package com.linkbi.datax.api.mapper;

import com.linkbi.datax.api.domain.JobLogGlue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * job log for glue
 *
 * @author xuxueli 2016-5-19 18:04:56
 */
@Mapper
public interface JobLogGlueMapper {

    int save(JobLogGlue jobLogGlue);

    List<JobLogGlue> findByJobId(@Param("jobId") long jobId);

    int removeOld(@Param("jobId") long jobId, @Param("limit") int limit);

    int deleteByJobId(@Param("jobId") long jobId);

}
