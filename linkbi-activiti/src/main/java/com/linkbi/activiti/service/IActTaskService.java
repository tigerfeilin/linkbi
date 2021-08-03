package com.linkbi.activiti.service;

import com.github.pagehelper.Page;
import com.linkbi.activiti.domain.dto.ActWorkflowFormDataDTO;
import com.linkbi.activiti.domain.dto.ActTaskDTO;
import com.linkbi.common.core.page.PageDomain;

import java.text.ParseException;
import java.util.List;

public interface IActTaskService {
    public Page<ActTaskDTO> selectProcessDefinitionList(PageDomain pageDomain);
    public List<String>formDataShow(String taskID);
    public int formDataSave(String taskID, List<ActWorkflowFormDataDTO> awfs) throws ParseException;
}
