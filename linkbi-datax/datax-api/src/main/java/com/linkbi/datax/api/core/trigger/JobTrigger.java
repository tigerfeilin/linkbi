package com.linkbi.datax.api.core.trigger;

import com.linkbi.common.utils.uuid.IdUtils;
import com.linkbi.datatx.core.biz.ExecutorBiz;
import com.linkbi.datatx.core.biz.model.ReturnT;
import com.linkbi.datatx.core.biz.model.TriggerParam;
import com.linkbi.datatx.core.enums.ExecutorBlockStrategyEnum;
import com.linkbi.datatx.core.enums.IncrementTypeEnum;
import com.linkbi.datatx.core.glue.GlueTypeEnum;
import com.linkbi.datax.api.core.conf.JobAdminConfig;
import com.linkbi.datax.api.core.route.ExecutorRouteStrategyEnum;
import com.linkbi.datax.api.core.scheduler.JobScheduler;
import com.linkbi.datax.api.core.util.I18nUtil;
import com.linkbi.datax.api.domain.MetaDataSource;
import com.linkbi.datax.api.domain.JobGroup;
import com.linkbi.datax.api.domain.JobInfo;
import com.linkbi.datax.api.domain.JobLog;
import com.linkbi.datax.api.util.AESUtil;
import com.linkbi.datax.api.util.JSONUtils;
import com.linkbi.datax.db.constant.DBTypeEnum;
import com.linkbi.datax.db.model.JdbcSourceData;
import com.linkbi.datax.db.service.IMetaDataService;
import com.linkbi.datax.db.service.impl.CommonMetaDataServiceImpl;
import com.linkbi.datax.db.util.JdbcUrlUtils;
import com.linkbi.datax.rpc.util.IpUtil;
import com.linkbi.datax.rpc.util.ThrowableUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * xxl-job trigger
 * Created by xuxueli on 17/7/13.
 */
public class JobTrigger {
    private static Logger logger = LoggerFactory.getLogger(JobTrigger.class);
    private static IMetaDataService metaDataService = new CommonMetaDataServiceImpl();
    /**
     * trigger job
     *
     * @param jobId
     * @param triggerType
     * @param failRetryCount        >=0: use this param
     *                              <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param
     *                              not null: cover job param
     */
    public static void trigger(long jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam) {
        JobInfo jobInfo = JobAdminConfig.getAdminConfig().getJobInfoMapper().loadById(jobId);
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (GlueTypeEnum.BEAN.getDesc().equals(jobInfo.getGlueType())) {
            //解密账密
            String json = JSONUtils.changeJson(jobInfo.getJobJson(), JSONUtils.decrypt);
            jobInfo.setJobJson(json);
        }
        if (StringUtils.isNotBlank(executorParam)) {
            jobInfo.setExecutorParam(executorParam);
        }
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        JobGroup group = JobAdminConfig.getAdminConfig().getJobGroupMapper().load(jobInfo.getJobGroup());

        // sharding param
        int[] shardingParam = null;
        if (executorShardingParam != null) {
            String[] shardingArr = executorShardingParam.split("/");
            if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.parseInt(shardingArr[0]);
                shardingParam[1] = Integer.parseInt(shardingArr[1]);
            }
        }
        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null)
                && group.getRegistryList() != null && !group.getRegistryList().isEmpty()
                && shardingParam == null) {
            for (int i = 0; i < group.getRegistryList().size(); i++) {
                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size());
            }
        } else {
            if (shardingParam == null) {
                shardingParam = new int[]{0, 1};
            }
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
        }

    }

    private static boolean isNumeric(String str) {
        try {
            int result = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param group               job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     * @param index               sharding index
     * @param total               sharding index
     */
    private static void processTrigger(JobGroup group, JobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {

        TriggerParam triggerParam = new TriggerParam();

        // param
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);  // block strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);    // route strategy
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) ? String.valueOf(index).concat("/").concat(String.valueOf(total)) : null;

        // 1、save log-id
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        Date triggerTime = calendar.getTime();
        JobLog jobLog = new JobLog();
        jobLog.setId(IdUtils.getId());
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(triggerTime);
        jobLog.setJobDesc(jobInfo.getJobDesc());
        jobLog.setAlarmStatus(0);
        JobAdminConfig.getAdminConfig().getJobLogMapper().save(jobLog);
        logger.debug(">>>>>>>>>>> datax-admin trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);
        triggerParam.setJobJson(jobInfo.getJobJson());

        //increment parameter
        Integer incrementType = jobInfo.getIncrementType();
        if (incrementType != null) {
            triggerParam.setIncrementType(incrementType);
            if (IncrementTypeEnum.ID.getCode() == incrementType) {
                long maxId = getMaxId(jobInfo);
                jobLog.setMaxId(maxId);
                triggerParam.setEndId(maxId);
                triggerParam.setStartId(jobInfo.getIncStartId());
            } else if (IncrementTypeEnum.TIME.getCode() == incrementType) {
                triggerParam.setStartTime(jobInfo.getIncStartTime());
                triggerParam.setTriggerTime(triggerTime);
                triggerParam.setReplaceParamType(jobInfo.getReplaceParamType());
            } else if (IncrementTypeEnum.PARTITION.getCode() == incrementType) {
                triggerParam.setPartitionInfo(jobInfo.getPartitionInfo());
            }
            triggerParam.setReplaceParam(jobInfo.getReplaceParam());
        }
        //jvm parameter
        triggerParam.setJvmParam(jobInfo.getJvmParam());

        // 3、init address
        String address = null;
        ReturnT<String> routeAddressResult = null;
        if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                if (index < group.getRegistryList().size()) {
                    address = group.getRegistryList().get(index);
                } else {
                    address = group.getRegistryList().get(0);
                }
            } else {
                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
                if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
                    address = routeAddressResult.getContent();
                }
            }
        } else {
            routeAddressResult = new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobconf_trigger_address_empty"));
        }

        // 4、trigger remote executor
        ReturnT<String> triggerResult = null;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = new ReturnT<String>(ReturnT.FAIL_CODE, null);
        }

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append(I18nUtil.getString("jobconf_trigger_type")).append("：").append(triggerType.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_admin_adress")).append("：").append(IpUtil.getIp());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regtype")).append("：")
                .append((group.getAddressType() == 0) ? I18nUtil.getString("jobgroup_field_addressType_0") : I18nUtil.getString("jobgroup_field_addressType_1"));
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobconf_trigger_exe_regaddress")).append("：").append(group.getRegistryList());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorRouteStrategy")).append("：").append(executorRouteStrategyEnum.getTitle());
        if (shardingParam != null) {
            triggerMsgSb.append("(" + shardingParam + ")");
        }
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorBlockStrategy")).append("：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_timeout")).append("：").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append(I18nUtil.getString("jobinfo_field_executorFailRetryCount")).append("：").append(finalFailRetryCount);

        triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_run") + "<<<<<<<<<<< </span><br>")
                .append((routeAddressResult != null && routeAddressResult.getMsg() != null) ? routeAddressResult.getMsg() + "<br><br>" : "").append(triggerResult.getMsg() != null ? triggerResult.getMsg() : "");

        // 6、save log trigger-info
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        JobAdminConfig.getAdminConfig().getJobLogMapper().updateTriggerInfo(jobLog);

        logger.debug(">>>>>>>>>>> datax-admin trigger end, jobId:{}", jobLog.getId());
    }

    private static long getMaxId(JobInfo jobInfo) {
        MetaDataSource datasource = JobAdminConfig.getAdminConfig().getMetaDatasourceMapper().selectById(jobInfo.getDatasourceId());
        JdbcSourceData jdbcSourceData = new JdbcSourceData(datasource.getId().toString(),datasource.getDatasourceName(),datasource.getJdbcDriverClass(),datasource.getJdbcUrl(), AESUtil.decrypt(datasource.getJdbcUsername()),
                AESUtil.decrypt(datasource.getJdbcPassword()),datasource.getDatabaseName());
        return metaDataService.queryTableMaxId(jdbcSourceData,jobInfo.getReaderTable(), jobInfo.getPrimaryKey());
        //BaseQueryTool qTool = QueryToolFactory.getByDbType(datasource);
        //return qTool.getMaxIdVal(jobInfo.getReaderTable(), jobInfo.getPrimaryKey());
    }

    /**
     * run executor
     *
     * @param triggerParam
     * @param address
     * @return
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = JobScheduler.getExecutorBiz(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> datax-admin trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<String>(ReturnT.FAIL_CODE, ThrowableUtil.toString(e));
        }

        StringBuffer runResultSB = new StringBuffer(I18nUtil.getString("jobconf_trigger_run") + "：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        runResult.setMsg(runResultSB.toString());
        return runResult;
    }

}
