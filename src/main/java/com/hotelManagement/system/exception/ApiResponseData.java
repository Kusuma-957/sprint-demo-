package com.hotelManagement.system.exception;

import java.time.OffsetDateTime;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiResponseData<T> {
    T data;
}