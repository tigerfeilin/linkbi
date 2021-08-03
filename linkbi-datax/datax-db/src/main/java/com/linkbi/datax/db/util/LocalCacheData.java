package com.linkbi.datax.db.util;
/**
 * local cache tool
 *
 * @author xuxueli 2018-01-22 21:37:34
 */
public class LocalCacheData{
    private String key;
    private Object val;
    private long timeoutTime;

    public LocalCacheData(String key, Object val, long timeoutTime) {
        this.key = key;
        this.val = val;
        this.timeoutTime = timeoutTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public long getTimeoutTime() {
        return timeoutTime;
    }

    public void setTimeoutTime(long timeoutTime) {
        this.timeoutTime = timeoutTime;
    }
}
