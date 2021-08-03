package com.linkbi.datax.api.dto;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2021-05-12 16:54:31
 *
 * @author tl
 */
@Data
public class ChartCalcul {

    private String Column;
    private String calculFunc;
    private String Type;
    private List<ChartFunc> availableFunc;
    private String name;
    private String lable;
}
