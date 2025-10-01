package com.stas_kozh.recommendationservice.exception;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final Integer statusCode;

    public GeneralException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}