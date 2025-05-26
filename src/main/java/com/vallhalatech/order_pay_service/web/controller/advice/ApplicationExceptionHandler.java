
// ApplicationExceptionHandler.java
package com.vallhalatech.order_pay_service.web.controller.advice;

import com.vallhalatech.order_pay_service.web.dtos.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        BaseResponse response = BaseResponse.builder()
                .data(errors)
                .message("Error de validación")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .success(false)
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument error: ", ex);

        BaseResponse response = BaseResponse.builder()
                .message("Parámetro inválido: " + ex.getMessage())
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error: ", ex);

        BaseResponse response = BaseResponse.builder()
                .message("Error interno del servidor")
                .success(false)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return response.buildResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);

        BaseResponse response = BaseResponse.builder()
                .message("Error interno del servidor")
                .success(false)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return response.buildResponseEntity();
    }
}