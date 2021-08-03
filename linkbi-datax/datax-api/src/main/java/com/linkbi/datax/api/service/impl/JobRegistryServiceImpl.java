package com.linkbi.datax.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkbi.datax.api.domain.JobRegistry;
import com.linkbi.datax.api.mapper.JobRegistryMapper;
import com.linkbi.datax.api.service.JobRegistryService;
import org.springframework.stereotype.Service;

/**
 * JobRegistryServiceImpl
 * @author
 * @since 2019-03-15
 * @version v2.1.1
 */
@Service("jobRegistryService")
public class JobRegistryServiceImpl extends ServiceImpl<JobRegistryMapper, JobRegistry> implements JobRegistryService {

}