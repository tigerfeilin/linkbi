package com.linkbi.datax.api.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
public class JobRole {

    @TableId
    private Long id;
    @ApiModelProperty("账号")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
