package com.transfer.transferservice.controller.dto.mapper;

import com.transfer.transferservice.controller.dto.AccountDTO.AccountResponse;
import com.transfer.transferservice.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account){
        return new AccountResponse(account.getNumber(), account.getName(), account.getCpf());
    }

}
