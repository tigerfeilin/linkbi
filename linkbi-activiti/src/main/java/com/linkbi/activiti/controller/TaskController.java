package com.linkbi.activiti.controller;


import com.github.pagehelper.Page;
import com.linkbi.activiti.domain.dto.ActTaskDTO;
import com.linkbi.activiti.domain.dto.ActWorkflowFormDataDTO;
import com.linkbi.activiti.service.IActTaskService;
import com.linkbi.common.core.controller.BaseController;
import com.linkbi.common.core.domain.AjaxResult;
import com.linkbi.common.core.page.PageDomain;
import com.linkbi.common.core.page.TableDataInfo;
import com.linkbi.common.core.page.TableSupport;
import org.activiti.api.task.runtime.TaskRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;


@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {
    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private IActTaskService actTaskService;



    //获取我的代办任务
    @GetMapping(value = "/list")
    public TableDataInfo getTasks() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Page<ActTaskDTO> hashMaps = actTaskService.selectProcessDefinitionList(pageDomain);
         return getDataTable(hashMaps);


    }


    //渲染表单
    @GetMapping(value = "/formDataShow/{taskID}")
    public AjaxResult formDataShow(@PathVariable("taskID") String taskID) {

        return AjaxResult.success(actTaskService.formDataShow(taskID));
    }

    //保存表单
    @PostMapping(value = "/formDataSave/{taskID}")
    public AjaxResult formDataSave(@PathVariable("taskID") String taskID,
                                   @RequestBody   List<ActWorkflowFormDataDTO> formData ) throws ParseException {
        return toAjax(actTaskService.formDataSave(taskID, formData));

    }

}
