package com.jixianda.handler;

import com.jixianda.constant.MessageConstant;
import com.jixianda.exception.BaseException;
import com.jixianda.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * sql异常信息捕获
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '昝国秀' for key 'idx_username'

        String message = ex.getMessage();
        if (message.contains("Duplicate entry")){
            String[] s = message.split(" ");
            String username = s[2];
            String msg = username + MessageConstant.ACCOUNT_HAVE_FOUND;
            return Result.error(msg);
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }

    }
}
