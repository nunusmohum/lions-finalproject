package com.ll.ebook.app.base.exceptionHandler;

import com.ll.ebook.app.base.dto.RsData;
import com.ll.ebook.util.Ut;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<String>> errorHandler(MethodArgumentNotValidException exception) {
        String msg = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("/"));

        String data = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getCode)
                .collect(Collectors.joining("/"));

        return Ut.spring.responseEntityOf(RsData.of("F-MethodArgumentNotValidException", msg, data));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RsData<String>> errorHandler(RuntimeException exception) {
        String msg = exception.getClass().toString();

        String data = exception.getMessage();

        return Ut.spring.responseEntityOf(RsData.of("F-RuntimeException", msg, data));
    }
}
