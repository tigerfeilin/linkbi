package com.linkbi.datax.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linkbi.datax.api.domain.JobProject;

/**
 * Job project
 *
 * @author
 * @version v2.1.2
 * @since 2020-05-24
 */
public interface JobProjectService extends IService<JobProject> {

    /**
     * project page
     * @param pageSize
     * @param pageNo
     * @param searchName
     * @return
     */

    IPage<JobProject> getProjectListPaging(Integer pageSize, Integer pageNo, String searchName);
}