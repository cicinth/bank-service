package com.transfer.transferservice.service;

import com.transfer.transferservice.controller.dto.TransferDTO.DestinationAccountRequest;
import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.exceptions.NotFoundException;
import com.transfer.transferservice.model.Account;
import com.transfer.transferservice.model.Transfer;
import com.transfer.transferservice.repository.TransferRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransferServiceTest {

    @InjectMocks
    private TransferService transferService;

    @Mock
    AccountService accountService;


    @Mock
    TransferRepository transferRepository;



    @Test
    public void shouldReturnTrue_whenBalanceIsBiggerThenTransferValue(){
        Account account = new Account(123L, "Cinthia", 2323L, 500.0, LocalDateTime.now());

        boolean result = transferService.validateTransferCanHappen(account, 100.0);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalse_whenBalanceIsLowThenTransferValue(){
        Account account = new Account(123L, "Cinthia", 2323L, 100.0, LocalDateTime.now());

        boolean result = transferService.validateTransferCanHappen(account, 200.0);
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnFalse_whenTransferValueIsZero(){
        Account account = new Account(123L, "Cinthia", 2323L, 100.0, LocalDateTime.now());
        boolean result = transferService.validateTransferCanHappen(account, 0.0);
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnSuccessTransferWithoutTaxes_whenTransferIsBetweenAccountsInTheSameBank(){
        TransferRequest transferRequest = new TransferRequest(100.0, new DestinationAccountRequest(
                123L,
                344L
        ));
        when(accountService.findByAccountNumber(1223L)).thenReturn(
                new Account(1223L, "Cinthia", 2323L, 100.0, LocalDateTime.now())
        );
        Transfer transfer = transferService.transfer(1223L, transferRequest);

        Assert.assertNotNull(transfer);
        Assert.assertEquals(100.0,transfer.getAmount(), 0.1);

        verify(accountService, times(1)).performWithdrawal(1223L, 100.0);
        verify(accountService, times(1)).deposit(344L, 100.0);
    }

    @Test
    public void shouldReturnSuccessTransferWithTaxes_whenTransferIsBetweenAccountsInTheDifferentBank(){
        TransferRequest transferRequest = new TransferRequest(100.0, new DestinationAccountRequest(
                444L,
                344L
        ));
        when(accountService.findByAccountNumber(1223L)).thenReturn(
                new Account(1223L, "Cinthia", 2323L, 200.0, LocalDateTime.now())
        );
        Transfer transfer = transferService.transfer(1223L, transferRequest);

        Assert.assertNotNull(transfer);
        Assert.assertEquals(100.0,transfer.getAmount(), 0.1);

        verify(accountService, times(1)).performWithdrawal(1223L, 101.99);
        verify(accountService, times(0)).deposit(any(), any());
    }

    @Test(expected = NotFoundException.class)
    public void shouldReturnException_whenTransferWithNonExistentOriginAccount(){
        TransferRequest transferRequest = new TransferRequest(100.0, new DestinationAccountRequest(
                444L,
                344L
        ));
        when(accountService.findByAccountNumber(1223L)).thenThrow(
                new NotFoundException("Account not found")
        );

        transferService.transfer(1223L, transferRequest);
    }

}
