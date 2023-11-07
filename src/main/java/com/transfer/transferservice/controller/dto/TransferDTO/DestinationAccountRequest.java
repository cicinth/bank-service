package com.transfer.transferservice.controller.dto.TransferDTO;


public record DestinationAccountRequest(
        Long bank,
        Long accountNumber
){ }
