package com.linkbi.common.exception.user;

import com.linkbi.common.exception.BaseException;

/**
 * 用户信息异常类
 * 
 * @author
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
