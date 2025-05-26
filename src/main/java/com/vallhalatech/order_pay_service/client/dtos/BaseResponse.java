package com.vallhalatech.order_pay_service.client.dtos;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseResponse {
    private Object data;
    private String message;
    private Boolean success;
    private HttpStatus httpStatus;
}
