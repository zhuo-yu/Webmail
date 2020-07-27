package com.zy.webmail.product.exception;

import com.zy.common.exception.exceptionenum;
import com.zy.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice("com.zy.webmail.product.controller")
//@ControllerAdvice("com.zy.webmail.product.controller")
public class validexception {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{},异常类型{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> map=new HashMap<>();
        bindingResult.getFieldErrors().forEach((item)->{
            String defaultMessage = item.getDefaultMessage();
            String field = item.getField();
            map.put(field,defaultMessage);
        });
        return R.error(exceptionenum.VAILD_EXCEPTION.getCode(),exceptionenum.VAILD_EXCEPTION.getMsg()).put("data",map);
    }

//    @ExceptionHandler(Throwable.class)
//    public R handleException(Throwable throwable){
//        return R.error(exceptionenum.UNKNOW_EXCEPTION.getCode(),exceptionenum.UNKNOW_EXCEPTION.getMsg());
//    }
}
