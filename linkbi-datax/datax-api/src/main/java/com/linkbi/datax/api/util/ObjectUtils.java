package com.linkbi.datax.api.util;

/**
 * ObjectUtils
 *
 * @author
 * @ClassName ObjectUtils
 * @Version 1.0
 * @since 2019/7/31 14:54
 */
public class ObjectUtils {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }
}
