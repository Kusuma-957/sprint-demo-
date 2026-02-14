package com.hotelManagement.system.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

@Value
@Builder
public class ApiError {
    ApiCode code;
    String message;
    //@Builder.Default OffsetDateTime timestamp = OffsetDateTime.now();
    //String path;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<FieldError> errors;

    @Value
    @Builder
    public static class FieldError {
        String field;
        String error;
        Object rejectedValue;
    }
}
