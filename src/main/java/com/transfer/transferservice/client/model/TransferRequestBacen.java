package com.transfer.transferservice.client.model;

public record TransferRequestBacen(
        Double amount,
        Long accountNumber,
        Long bankCode
){ }
