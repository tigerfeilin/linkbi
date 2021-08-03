package com.linkbi.datax.api.service;


import com.linkbi.datatx.core.biz.model.ReturnT;
import com.linkbi.datax.api.dto.DataXBatchJsonBuildDto;
import com.linkbi.datax.api.domain.JobInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * core job action for datax-admin
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface JobService {

    /**
     * page list
     *
     * @param start
     * @param length
     * @param jobGroup
     * @param jobDesc
     * @param glueType
     * @param userId
     * @return
     */
    Map<String, Object> pageList(int start, int length, long jobGroup, int triggerStatus, String jobDesc, String glueType, long userId,long projectIds);

    List<JobInfo> list();

    /**
     * add job
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> add(JobInfo jobInfo);

    /**
     * update job
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> update(JobInfo jobInfo);

    /**
     * remove job
     * *
     *
     * @param id
     * @return
     */
    ReturnT<String> remove(long id);

    /**
     * start job
     *
     * @param id
     * @return
     */
    ReturnT<String> start(long id);

    /**
     * stop job
     *
     * @param id
     * @return
     */
    ReturnT<String> stop(long id);

    /**
     * dashboard info
     *
     * @return
     */
    Map<String, Object> dashboardInfo();

    /**
     * chart info
     *
     * @return
     */
    ReturnT<Map<String, Object>> chartInfo();

    /**
     * batch add
     * @param dto
     * @return
     */
    ReturnT<String> batchAdd(DataXBatchJsonBuildDto dto) throws IOException;
}
