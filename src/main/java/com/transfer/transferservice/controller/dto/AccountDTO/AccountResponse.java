package com.transfer.transferservice.controller.dto.AccountDTO;

import java.util.UUID;

public record AccountResponse(
        Long number,
        String name,
        Long cpf
) { }
