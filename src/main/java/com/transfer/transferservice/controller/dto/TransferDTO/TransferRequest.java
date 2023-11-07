package com.transfer.transferservice.controller.dto.TransferDTO;

public record TransferRequest(
         Double amount,
         DestinationAccountRequest destinationAccount
){ }
