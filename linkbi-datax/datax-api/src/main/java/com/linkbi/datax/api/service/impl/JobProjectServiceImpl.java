package com.linkbi.datax.api.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkbi.datax.api.domain.JobProject;
import com.linkbi.datax.api.mapper.JobProjectMapper;
import com.linkbi.datax.api.service.JobProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JobProjectServiceImpl
 * @author
 * @since 2019-05-30
 * @version v2.1.2
 */
@Service("jobProjectService")
public class JobProjectServiceImpl extends ServiceImpl<JobProjectMapper, JobProject> implements JobProjectService {

    @Autowired
    private JobProjectMapper jobProjectMapper;

    @Override
    public IPage<JobProject> getProjectListPaging(Integer pageSize, Integer pageNo, String searchName) {
        Page<JobProject> page = new Page(pageNo, pageSize);
        IPage<JobProject> lp = jobProjectMapper.getProjectListPaging(page, searchName);
        return lp;
    }
}