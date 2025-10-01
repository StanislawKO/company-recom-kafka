package com.stas_kozh.recommendationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequestException(BadRequestException e) {
        return new ErrorResponse(e.getMessage(), 400, new Date().toString());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(ResourceNotFoundException e) {
        return new ErrorResponse(e.getMessage(), 404, new Date().toString());
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorResponse> generalException(GeneralException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), e.getStatusCode(), new Date().toString());
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }
}
