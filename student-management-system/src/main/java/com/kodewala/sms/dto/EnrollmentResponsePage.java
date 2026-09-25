package com.kodewala.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated enrollment response")
public class EnrollmentResponsePage {

    @Schema(description = "List of enrollments")
    private List<EnrollmentResponse> content;

    @Schema(description = "Current page number", example = "0")
    private int number;

    @Schema(description = "Number of enrollments per page", example = "10")
    private int size;

    @Schema(description = "Total number of enrollments", example = "25")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "3")
    private int totalPages;

    @Schema(description = "Whether this is the first page", example = "true")
    private boolean first;

    @Schema(description = "Whether this is the last page", example = "false")
    private boolean last;
}