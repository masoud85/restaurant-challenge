package com.midokura.restaurant.exception;

import com.midokura.restaurant.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RestaurantRestControllerAdvice {

    @ExceptionHandler(GroupHasAlreadyLeftException.class)
    public ApiResponse handle(GroupHasAlreadyLeftException ex) {
        log.warn("An exception occurred", ex);
        return ApiResponse.builder()
                .message(ex.getMessage())
                .build();
    }

}
