package com.innowise.paymentservice.dto.exception;

public record Validation(
        String fieldName,
        String message
) {}
