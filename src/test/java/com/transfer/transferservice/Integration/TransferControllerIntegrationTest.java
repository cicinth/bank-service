package com.transfer.transferservice.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.transferservice.controller.dto.TransferDTO.DestinationAccountRequest;
import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.model.Account;
import com.transfer.transferservice.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void shouldReturn200_whenTransferIsValid() throws Exception {
        Account account = new Account();
        account.setName("Maria");
        account.setCpf(4354L);
        Account accountSaved = accountRepository.save(account);

        accountSaved.setBalance(500.0);

        accountRepository.save(accountSaved);

        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .header("accountAuthor", account.getNumber())
                .content(asJsonString(buildTransferRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Double balance = accountRepository.findById(account.getNumber()).get().getBalance();
        Assertions.assertEquals(398.01, balance, 0.1);

    }

    private TransferRequest buildTransferRequest(){
        return new TransferRequest(100.0, new DestinationAccountRequest(454L, 6565L));
    }

    private String asJsonString(final Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
