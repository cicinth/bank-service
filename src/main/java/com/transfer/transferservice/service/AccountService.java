package com.transfer.transferservice.service;

import com.transfer.transferservice.controller.dto.AccountDTO.AccountRequest;
import com.transfer.transferservice.exceptions.NotFoundException;
import com.transfer.transferservice.model.Account;
import com.transfer.transferservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    public Account saveAccount(AccountRequest accountRequest){
        Account account = new Account();
        account.setName(accountRequest.name());
        account.setCpf(accountRequest.cpf());
       return accountRepository.save(account);
    }

    public Account findByAccountNumber(Long number){
        return accountRepository.findById(number).orElseThrow(() ->
                new NotFoundException("Account not found")
        );
    }

    public void performWithdrawal(Long number, Double amount){
        Account account = accountRepository.findById(number).orElseThrow(() ->
                new NotFoundException("Account not found")
        );
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
    }

    public void  deposit(Long number, Double amount){
        Account account = accountRepository.findById(number).orElseThrow(() ->
                new NotFoundException("Account not found")
        );
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }

}
