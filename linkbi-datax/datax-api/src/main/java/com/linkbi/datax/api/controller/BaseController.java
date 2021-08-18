package com.linkbi.datax.api.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.linkbi.common.utils.SecurityUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * base controller
 */
public class BaseController extends ApiController{

    public Long getCurrentUserId(HttpServletRequest request) {
        return SecurityUtils.getLoginUser().getUser().getUserId();
        //Enumeration<String> auth = request.getHeaders(JwtTokenUtils.TOKEN_HEADER);
        //String token = auth.nextElement().replace(JwtTokenUtils.TOKEN_PREFIX, STRING_BLANK);
        //return JwtTokenUtils.getUserId(token);
    }
}