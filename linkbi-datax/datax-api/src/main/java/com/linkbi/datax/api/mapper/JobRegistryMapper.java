package com.linkbi.datax.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linkbi.datax.api.domain.JobRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created 2019/11/17
 */
@Mapper
public interface JobRegistryMapper extends BaseMapper<JobRegistry> {

    public List<Long> findDead(@Param("timeout") int timeout,
                                  @Param("nowTime") Date nowTime);

    public int removeDead(@Param("ids") List<Long> ids);

    public List<JobRegistry> findAll(@Param("timeout") int timeout,
                                     @Param("nowTime") Date nowTime);

    public int registryUpdate(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue,
                              @Param("cpuUsage") double cpuUsage,
                              @Param("memoryUsage") double memoryUsage,
                              @Param("loadAverage") double loadAverage,
                              @Param("updateTime") Date updateTime);

    public int registrySave(@Param("id") long id,
                            @Param("registryGroup") String registryGroup,
                            @Param("registryKey") String registryKey,
                            @Param("registryValue") String registryValue,
                            @Param("cpuUsage") double cpuUsage,
                            @Param("memoryUsage") double memoryUsage,
                            @Param("loadAverage") double loadAverage,
                            @Param("updateTime") Date updateTime);

    public int registryDelete(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue);

}
