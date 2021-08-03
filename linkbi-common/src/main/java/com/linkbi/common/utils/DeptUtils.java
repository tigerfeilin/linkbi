package com.linkbi.common.utils;

import java.util.Collection;
import java.util.List;
import com.linkbi.common.constant.Constants;
import com.linkbi.common.core.domain.entity.SysDept;
import com.linkbi.common.core.domain.entity.SysDictData;
import com.linkbi.common.core.redis.RedisCache;
import com.linkbi.common.utils.spring.SpringUtils;

/**
 * 部门工具类
 *
 * @author
 */
public class DeptUtils
{
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ",";

    /**
     * 设置字典缓存
     *
     * @param key 参数键
     * @param deptDatas 字典数据列表
     */
    public static void setDeptCache(String key, List<SysDept> deptDatas)
    {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), deptDatas);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return deptDatas 部门数据列表
     */
    public static List<SysDept> getDeptCache(String key)
    {
        Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(cacheObj))
        {
            List<SysDept> deptDatas = StringUtils.cast(cacheObj);
            return deptDatas;
        }
        return null;
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache()
    {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(Constants.SYS_DEPT_KEY + "*");
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey)
    {
        return Constants.SYS_DEPT_KEY + configKey;
    }
}

