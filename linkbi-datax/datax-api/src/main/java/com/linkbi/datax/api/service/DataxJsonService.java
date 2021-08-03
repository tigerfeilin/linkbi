package com.linkbi.datax.api.service;

import com.linkbi.datax.api.dto.DataXJsonBuildDto;

/**
 * com.linkbi.datax json构建服务层接口
 *
 * @author
 * @version 1.0
 * @since 2019/8/1
 */
public interface DataxJsonService {

    /**
     * build datax json
     *
     * @param dto
     * @return
     */
    String buildJobJson(DataXJsonBuildDto dto);
}
