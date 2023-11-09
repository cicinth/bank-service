package com.transfer.transferservice.service;

import com.transfer.transferservice.controller.dto.TransferDTO.DestinationAccountRequest;
import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.exceptions.NotFoundException;
import com.transfer.transferservice.exceptions.ValueNotAllowed;
import com.transfer.transferservice.model.Account;
import com.transfer.transferservice.model.Transfer;
import com.transfer.transferservice.repository.TransferRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferServiceTest {

    @InjectMocks
    private TransferService transferService;

    @Mock
    AccountService accountService;


    @Mock
    TransferRepository transferRepository;

    @BeforeAll
    public void setup(){
        System.out.println("antes da classe - Antigo BeforeClass");
    }

    @AfterAll
    public void after(){
        System.out.println("depois da classe - Antigo AfterClass");
    }

    @BeforeEach
    public void setupMethod(){
        System.out.println("antes do metodo - Antigo Before");
    }

    @AfterEach
    public void afterMethod(){
        System.out.println("depois do metodo - Antigo After");
    }


    @Test
    public void shouldReturnTrue_whenBalanceIsBiggerThenTransferValue(){
        Account account = new Account(123L, "Cinthia", 2323L, 500.0, LocalDateTime.now());
        transferService.validateTransferCanHappen(account, 100.0);
    }

    @Test
    @Disabled
    public void shouldReturnFalse_whenBalanceIsLowThenTransferValue(){
        Account account = new Account(123L, "Cinthia", 2323L, 100.0, LocalDateTime.now());
        transferService.validateTransferCanHappen(account, 200.0);
    }

    @Test
    @Disabled
    public void shouldReturnFalse_whenTransferValueIsZero(){
        Account account = new Account(123L, "Cinthia", 2323L, 100.0, LocalDateTime.now());
        transferService.validateTransferCanHappen(account, 0.0);
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

        Assertions.assertAll(
                "Group Assertions",
                () -> Assertions.assertEquals(100.0,transfer.getAmount(), 0.1),
                () -> Assertions.assertNotNull(transfer)
        );

        verify(accountService, times(1)).performWithdrawal(1223L, 100.0);
        verify(accountService, times(1)).deposit(344L, 100.0);
    }

    @Test
    @DisplayName("Transfer Between Accounts In The Different Bank")
    public void shouldReturnSuccessTransferWithTaxes_whenTransferIsBetweenAccountsInTheDifferentBank(){
        TransferRequest transferRequest = new TransferRequest(100.0, new DestinationAccountRequest(
                444L,
                344L
        ));
        when(accountService.findByAccountNumber(1223L)).thenReturn(
                new Account(1223L, "Cinthia", 2323L, 200.0, LocalDateTime.now())
        );
        Transfer transfer = transferService.transfer(1223L, transferRequest);

        Assertions.assertNotNull(transfer);
        Assertions.assertEquals(100.0,transfer.getAmount(), 0.1);

        verify(accountService, times(1)).performWithdrawal(1223L, 101.99);
        verify(accountService, times(0)).deposit(any(), any());
    }

    @Test()
    public void shouldReturnException_whenTransferWithNonExistentOriginAccount(){
        TransferRequest transferRequest = new TransferRequest(100.0, new DestinationAccountRequest(
                444L,
                344L
        ));
        when(accountService.findByAccountNumber(1223L)).thenThrow(
                new NotFoundException("Account not found")
        );
        Assertions.assertThrows(NotFoundException.class, ()->{
            transferService.transfer(1223L, transferRequest);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "0.0, 100.0, 'Amount can not be zero'",
            "-1.0, 100.0, 'Amount can not be zero'",
            "100.0, 50.0, 'Insufficient funds'"
    })
    public void shouldReturnException_whenTransferValueIsZeroOrBalanceIsLowThenTransfer(Double amount, Double balance, String expectedMessage){
        TransferRequest transferRequest = new TransferRequest(amount, new DestinationAccountRequest(
                123L,
                344L
        ));

        when(accountService.findByAccountNumber(1223L)).thenReturn(
                new Account(1223L, "Cinthia", 2323L, balance, LocalDateTime.now())
        );

       ValueNotAllowed exception = Assertions.assertThrows(ValueNotAllowed.class, ()-> {
            transferService.transfer(1223L, transferRequest);
        });


       String actualMessage = exception.getMessage();

       Assertions.assertEquals(expectedMessage, actualMessage);
    }

    private static Stream<Arguments> testParameters(){
        return Stream.of(
                Arguments.of(0.0, 100.0, "Amount can not be zero"), //valor tranferencia invalida
                Arguments.of(-1.0, 100.0, "Amount can not be zero"), // valor tranferencia invalida
                Arguments.of(100.0, 50.0, "Insufficient funds") //saldo insuficiente
        );
    }
}
