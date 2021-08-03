package com.linkbi.admin.dao;

import com.linkbi.common.utils.uuid.IdUtils;
import com.linkbi.datax.api.domain.JobLogGlue;
import com.linkbi.datax.api.mapper.JobLogGlueMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobLogGlueMapperTest {

    @Resource
    private JobLogGlueMapper jobLogGlueMapper;

    @Test
    public void test(){
        JobLogGlue logGlue = new JobLogGlue();
        logGlue.setJobId(1L);
        logGlue.setGlueType("1");
        logGlue.setGlueSource("1");
        logGlue.setGlueRemark("1");

        logGlue.setAddTime(new Date());
        logGlue.setUpdateTime(new Date());
        logGlue.setId(IdUtils.getId());
        int ret = jobLogGlueMapper.save(logGlue);

        List<JobLogGlue> list = jobLogGlueMapper.findByJobId(1);

        int ret2 = jobLogGlueMapper.removeOld(1, 1);

        int ret3 = jobLogGlueMapper.deleteByJobId(1);
    }

}
