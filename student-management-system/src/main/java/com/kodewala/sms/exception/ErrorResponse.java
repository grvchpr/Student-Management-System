package com.kodewala.sms.exception;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Standard API error response")
public class ErrorResponse {

	@Schema(
		    description = "HTTP status code",
		    example = "404"
		)
		private int status;

		@Schema(
		    description = "HTTP status description",
		    example = "Not Found"
		)
		private String error;

		@Schema(
		    description = "Detailed error message",
		    example = "Student not found with id: 1"
		)
		private String message;

		@Schema(
		    description = "Request path that generated the error",
		    example = "/api/v1/students/1"
		)
		private String path;

		@Schema(
		    description = "Time when the error occurred",
		    example = "2026-09-23T23:15:00"
		)
		private LocalDateTime timestamp;
}