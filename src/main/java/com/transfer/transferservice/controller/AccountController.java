package com.transfer.transferservice.controller;


import com.transfer.transferservice.controller.dto.AccountDTO.AccountRequest;
import com.transfer.transferservice.controller.dto.AccountDTO.AccountResponse;
import com.transfer.transferservice.controller.dto.mapper.AccountMapper;
import com.transfer.transferservice.model.Account;
import com.transfer.transferservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<AccountResponse> saveAccount(@RequestBody AccountRequest accountRequest){
         AccountResponse account = accountMapper.toResponse(accountService.saveAccount(accountRequest));
        return ResponseEntity.created(URI.create("/account/"+account.number())).body(account);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long accountNumber){
        AccountResponse accountResponse = accountMapper.toResponse(accountService.findByAccountNumber(accountNumber));
        return ResponseEntity.ok().body(accountResponse);
    }

    @PostMapping("/deposite")
    @ResponseStatus(HttpStatus.OK)
    public void deposite(
            @RequestHeader Long accountAuthor,
            @RequestBody Double value
    ){
        accountService.deposit(accountAuthor, value);
    }
}
