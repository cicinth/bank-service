package com.transfer.transferservice.controller.dto.mapper;

import com.transfer.transferservice.controller.dto.AccountDTO.AccountResponse;
import com.transfer.transferservice.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class AccountMapperTest {

    @Spy
    private AccountMapper accountMapperSpy;

    @Test
    public void testToResponseWithSpy(){
        Account account = new Account(123L, "Maria", 343L, 500.0, LocalDateTime.now());

        AccountResponse response = accountMapperSpy.toResponse(account);

        Assertions.assertEquals(account.getNumber(), response.number());
        Assertions.assertNotNull(response);

    }
}
