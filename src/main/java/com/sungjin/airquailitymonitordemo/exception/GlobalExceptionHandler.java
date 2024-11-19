package com.sungjin.airquailitymonitordemo.exception;

import com.sungjin.airquailitymonitordemo.dto.response.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSearchCriteriaException.class)
    public ResponseEntity<ApiResponseDto> handleInvalidSearchCriteria(InvalidSearchCriteriaException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponseDto("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(new ApiResponseDto("error", "Error searching sensor data: " + e.getMessage()));
    }
}