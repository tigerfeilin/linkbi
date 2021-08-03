package com.linkbi.datax.api.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class JobLogReport {

    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private Date triggerDay;

    private int runningCount;
    private int sucCount;
    private int failCount;
}
