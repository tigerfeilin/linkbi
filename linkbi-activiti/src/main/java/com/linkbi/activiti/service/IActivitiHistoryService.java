package com.linkbi.activiti.service;

import com.linkbi.activiti.domain.dto.ActivitiHighLineDTO;

public interface IActivitiHistoryService {
    public ActivitiHighLineDTO gethighLine(String instanceId);
}
