package com.transfer.transferservice.controller.dto;

public record ErrorResponse (
        String status,
        String message
){ }
