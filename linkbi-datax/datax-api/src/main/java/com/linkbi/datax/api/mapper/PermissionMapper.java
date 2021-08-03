package com.linkbi.datax.api.mapper;

import com.linkbi.datax.api.domain.JobPermission;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PermissionMapper {

    List<JobPermission> findAll();

    List<JobPermission> findByAdminUserId(long userId);
}