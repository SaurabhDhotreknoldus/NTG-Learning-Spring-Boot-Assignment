package com.nashtech.employeeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Generic paginated response envelope")
public record PagedResponse<T>(
        @Schema(description = "List of items in the current page")
        List<T> content,

        @Schema(description = "Current page index (0-based)", example = "0")
        int pageNumber,

        @Schema(description = "Number of items per page", example = "10")
        int pageSize,

        @Schema(description = "Total number of items across all pages", example = "42")
        long totalElements,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages,

        @Schema(description = "Whether this is the last page", example = "false")
        boolean isLast
) {
    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
