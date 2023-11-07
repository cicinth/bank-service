package com.transfer.transferservice.service;

import com.transfer.transferservice.controller.dto.TransferDTO.DestinationAccountRequest;
import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.exceptions.ValueNotAllowed;
import com.transfer.transferservice.model.Account;
import com.transfer.transferservice.model.Transfer;
import com.transfer.transferservice.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class TransferService {

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    AccountService accountService;

    private final Long bankCode = 123L;

    public Transfer transfer(Long accountAuthor, TransferRequest transferRequest) {

        Account originAccount = accountService.findByAccountNumber(accountAuthor);

        Double amount = applyTaxes(transferRequest);

        if(!validateTransferCanHappen(originAccount, amount)) throw new ValueNotAllowed("Insufficient funds");

        makeTransfer(accountAuthor, amount, transferRequest.destinationAccount());
        return saveTransfer(transferRequest, originAccount);
    }

    protected boolean validateTransferCanHappen(Account originAccount, Double amount) {
        return originAccount.getBalance() >= amount && amount > 0.0;
    }

    protected Double applyTaxes(TransferRequest transferRequest){
        if(Objects.equals(transferRequest.destinationAccount().bank(), bankCode)) return transferRequest.amount();
        return transferRequest.amount() + ((transferRequest.amount() / 100) * 1.99);
    }

    protected void makeTransfer(Long accountAuthor, Double amount, DestinationAccountRequest destinationAccount){
        accountService.performWithdrawal(accountAuthor, amount);

        if(Objects.equals(destinationAccount.bank(), bankCode)){
            accountService.deposit(destinationAccount.accountNumber(), amount);
        }
        //TODO enviar para operadora
    }

    protected Transfer saveTransfer(TransferRequest transferRequest, Account originAccount){
        Transfer transfer = new Transfer();
        transfer.setAmount(transferRequest.amount());
        transfer.setDestinationAccount(transferRequest.destinationAccount().accountNumber());
        transfer.setDestinationBank(transferRequest.destinationAccount().bank());
        transfer.setOriginAccount(originAccount);
        transferRepository.save(transfer);
        return transfer;
    }

}
