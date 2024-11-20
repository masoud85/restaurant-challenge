package com.midokura.restaurant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiResponse {
    private final int statusCode;
    private final String message;
    private final Object data;

}
