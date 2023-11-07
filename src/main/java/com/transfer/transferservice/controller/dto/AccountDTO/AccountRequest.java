package com.transfer.transferservice.controller.dto.AccountDTO;


public record AccountRequest(
        String name,
        Long cpf
) { }
