package com.linkbi.datax.api.dto;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2021-05-12 16:54:31
 *
 * @author tl
 */
@Data
public class ChartContent {

    private String querySql;
    private String sourceId;
    private List<String> orderByStrs;
    private int limit;
    private List<ChartCalcul> selectedCalcul;
    private List<ChartDimension> selectedDimension;
    private String chartType;
    private List<String> filters;
}
