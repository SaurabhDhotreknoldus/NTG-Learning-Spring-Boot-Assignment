package com.nashtech.employeeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Standardized error response envelope")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        @Schema(description = "Timestamp when error occurred", example = "2024-01-15T12:00:00")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "HTTP error reason phrase", example = "Bad Request")
        String error,

        @Schema(description = "Descriptive error message", example = "Validation failed for request object")
        String message,

        @Schema(description = "Request endpoint URI", example = "/api/v1/employees")
        String path,

        @Schema(description = "Map of field-level validation errors, if applicable")
        Map<String, String> validationErrors
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ApiErrorResponse of(int status, String error, String message, String path, Map<String, String> validationErrors) {
        return new ApiErrorResponse(LocalDateTime.now(), status, error, message, path, validationErrors);
    }
}
