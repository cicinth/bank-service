package com.transfer.transferservice.service;

import com.transfer.transferservice.client.BacenClient;
import com.transfer.transferservice.client.model.TransferRequestBacen;
import com.transfer.transferservice.controller.dto.TransferDTO.DestinationAccountRequest;
import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.exceptions.BankNotFound;
import com.transfer.transferservice.exceptions.ValueNotAllowed;
import com.transfer.transferservice.model.Account;
import com.transfer.transferservice.model.Transfer;
import com.transfer.transferservice.repository.TransferRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class TransferService {

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    BacenClient bacenClient;

    private final Long bankCode = 123L;

    public Transfer transfer(Long accountAuthor, TransferRequest transferRequest) {

        Account originAccount = accountService.findByAccountNumber(accountAuthor);

        Double amount = applyTaxes(transferRequest);

        validateTransferCanHappen(originAccount, amount);

        makeTransfer(accountAuthor, amount, transferRequest.destinationAccount());
        return saveTransfer(transferRequest, originAccount);
    }

    protected void validateTransferCanHappen(Account originAccount, Double amount) {
        if(amount <= 0.0) throw new ValueNotAllowed("Amount can not be zero");
        if(originAccount.getBalance() < amount) throw new ValueNotAllowed("Insufficient funds");
    }

    protected Double applyTaxes(TransferRequest transferRequest){
        if(Objects.equals(transferRequest.destinationAccount().bank(), bankCode)) return transferRequest.amount();
        return transferRequest.amount() + ((transferRequest.amount() / 100) * 1.99);
    }

    protected void makeTransfer(Long accountAuthor, Double amount, DestinationAccountRequest destinationAccount){
        accountService.performWithdrawal(accountAuthor, amount);
        if(Objects.equals(destinationAccount.bank(), bankCode)){
            accountService.deposit(destinationAccount.accountNumber(), amount);
        } else {
            try {
                TransferRequestBacen transferRequest = new TransferRequestBacen(amount, destinationAccount.accountNumber(), destinationAccount.bank());
                bacenClient.transfer(transferRequest);
            } catch (FeignException e){
                if(e.status()  == HttpStatus.NOT_FOUND.value()) throw new BankNotFound("Banco do destinatario não encontrado");
                throw e;
            }
        }
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
