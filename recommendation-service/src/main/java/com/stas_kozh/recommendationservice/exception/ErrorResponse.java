package com.stas_kozh.recommendationservice.exception;

public record ErrorResponse(
        String message,
        Integer statusCode,
        String date
) {
}
