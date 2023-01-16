package com.linkbi.datax.api.controller;


import com.linkbi.common.annotation.Log;
import com.linkbi.common.enums.BusinessType;
import com.linkbi.common.utils.StringUtils;
import com.linkbi.datatx.core.biz.model.ReturnT;
import com.linkbi.datatx.core.util.DateUtil;
import com.linkbi.datax.api.core.cron.CronExpression;
import com.linkbi.datax.api.core.thread.JobTriggerPoolHelper;
import com.linkbi.datax.api.core.trigger.TriggerTypeEnum;
import com.linkbi.datax.api.core.util.I18nUtil;
import com.linkbi.datax.api.dto.DataXBatchJsonBuildDto;
import com.linkbi.datax.api.dto.TriggerJobDto;
import com.linkbi.datax.api.domain.JobInfo;
import com.linkbi.datax.api.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Api(tags = "任务配置接口")
@RestController
@RequestMapping("/api/job")
public class JobInfoController extends BaseController{

    @Resource
    private JobService jobService;


    @GetMapping("/pageList")
    @ApiOperation("任务列表")
    public ReturnT<Map<String, Object>> pageList(@RequestParam(required = false, defaultValue = "0") int current,
                                        @RequestParam(required = false, defaultValue = "10") int size,
                                        long jobGroup, long projectId, int triggerStatus, String jobDesc, String glueType) {
        //long projectId = 0;
        //if(!StringUtils.isEmpty(projectIds))
        //    projectId = Long.parseLong(projectIds);
        Map<String, Object> lm = jobService.pageList((current-1)*size, size, jobGroup, triggerStatus, jobDesc, glueType, getCurrentUserId(null),projectId );
        return new ReturnT<>(lm);
    }

    @GetMapping("/list")
    @ApiOperation("全部任务列表")
    public ReturnT<List<JobInfo>> list(){
        return new ReturnT<>(jobService.list());
    }

    @PostMapping("/add")
    @ApiOperation("添加任务")
    @Log(title = "DataX任务管理", businessType = BusinessType.INSERT)
    public ReturnT<String> add(HttpServletRequest request, @RequestBody JobInfo jobInfo) {
        jobInfo.setUserId(getCurrentUserId(request));
        return jobService.add(jobInfo);
    }

    @PostMapping("/update")
    @ApiOperation("更新任务")
    @Log(title = "DataX任务管理", businessType = BusinessType.UPDATE)
    public ReturnT<String> update(HttpServletRequest request,@RequestBody JobInfo jobInfo) {
        jobInfo.setUserId(getCurrentUserId(request));
        return jobService.update(jobInfo);
    }

    @PostMapping(value = "/remove/{id}")
    @ApiOperation("移除任务")
    @Log(title = "DataX任务管理", businessType = BusinessType.DELETE)
    public ReturnT<String> remove(@PathVariable(value = "id") long id) {
        return jobService.remove(id);
    }

    @RequestMapping(value = "/stop",method = RequestMethod.POST)
    @ApiOperation("停止任务")
    public ReturnT<String> pause(long id) {
        return jobService.stop(id);
    }

    @RequestMapping(value = "/start",method = RequestMethod.POST)
    @ApiOperation("开启任务")
    public ReturnT<String> start(long id) {
        return jobService.start(id);
    }

    @PostMapping(value = "/trigger")
    @ApiOperation("触发任务")
    public ReturnT<String> triggerJob(@RequestBody TriggerJobDto dto) {
        // force cover job param
        String executorParam=dto.getExecutorParam();
        if (executorParam == null) {
            executorParam = "";
        }
        JobTriggerPoolHelper.trigger(dto.getJobId(), TriggerTypeEnum.MANUAL, -1, null, executorParam);
        return ReturnT.SUCCESS;
    }

    @RequestMapping(value = "/batchstop",method = RequestMethod.POST)
    @ApiOperation("批量下线任务")
    public ReturnT<String> batchstop(Long[] ids) {
        //String[] jobList = ids.split(",");
        for (Long jobId:ids) {
            jobService.stop(jobId);
        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping(value = "/batchstart",method = RequestMethod.POST)
    @ApiOperation("批量上线任务")
    public ReturnT<String> batchstart(Long[] ids) {
        //String[] jobList = ids.split(",");
        for (Long jobId:ids) {
            jobService.start(jobId);
        }
        return ReturnT.SUCCESS;
    }

    @PostMapping(value = "/batchtrigger")
    @ApiOperation("批量触发任务")
    public ReturnT<String> batchtrigger(Long[] ids) {
        // force cover job param
        for (Long jobId:ids) {
            JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.MANUAL, -1, null, "");
        }
        return ReturnT.SUCCESS;
    }
    @GetMapping("/nextTriggerTime")
    @ApiOperation("获取近5次触发时间")
    public ReturnT<List<String>> nextTriggerTime(String cron) {
        List<String> result = new ArrayList<>();
        try {
            CronExpression cronExpression = new CronExpression(cron);
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = cronExpression.getNextValidTimeAfter(lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_invalid"));
        }
        return new ReturnT<>(result);
    }

    @PostMapping("/batchAdd")
    @ApiOperation("批量创建任务")
    @Log(title = "DataX任务管理", businessType = BusinessType.INSERT)
    public ReturnT<String> batchAdd(@RequestBody DataXBatchJsonBuildDto dto) throws IOException {
        if (dto.getTemplateId() ==0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_choose") + I18nUtil.getString("jobinfo_field_temp")));
        }
        return jobService.batchAdd(dto);
    }
}
