package com.vallhalatech.order_pay_service.client.auth;

import com.vallhalatech.order_pay_service.client.dtos.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @GetMapping("/users/user/email/{email}")
    ResponseEntity<BaseResponse> findUserByEmail(@PathVariable("email") String email);
}