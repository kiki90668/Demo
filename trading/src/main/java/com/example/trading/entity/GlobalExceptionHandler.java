package com.example.trading.entity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //處理自訂的BizException
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Object> handleBiz (BizException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    //處理其他未預期的錯誤
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(500, "Internal Server Error: " + ex.getMessage()));
    }

}

