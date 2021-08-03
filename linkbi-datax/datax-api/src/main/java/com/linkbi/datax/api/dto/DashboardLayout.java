package com.linkbi.datax.api.dto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2021-05-12 16:54:31
 *
 * @author tl
 */
@Data
public class DashboardLayout {

    private String id;
    private int x;
    private int y;
    private int w;
    private int h;
    private String i;
    private int yy;
    private int xx;

    private List<List<Integer>> bottomLine;
    private List<List<Integer>> topLine;
    private boolean moved;

    public DashboardLayout()
    {
        x=0;
        y=9;
        w=6;
        h=8;
        xx=12;
        yy=9;
        bottomLine = Arrays.asList(Arrays.asList(0,9), Arrays.asList(12,9));
        topLine = Arrays.asList(Arrays.asList(0,0), Arrays.asList(12,0));
    }
}
