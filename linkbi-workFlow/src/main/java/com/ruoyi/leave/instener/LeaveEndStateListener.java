package com.linkbi.leave.instener;

import com.linkbi.common.utils.spring.SpringUtils;
import com.linkbi.leave.domain.WorkflowLeave;
import com.linkbi.leave.service.IWorkflowLeaveService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;


public class LeaveEndStateListener implements ExecutionListener {
    private Expression state;

    @Override
    public void notify(DelegateExecution delegateExecution) {
        WorkflowLeave workflowLeave = new WorkflowLeave();
        workflowLeave.setId(delegateExecution.getProcessInstanceBusinessKey());
        workflowLeave.setState(state.getValue(delegateExecution).toString());
        SpringUtils.getBean(IWorkflowLeaveService.class).updateWorkflowLeave(workflowLeave);
    }
}
