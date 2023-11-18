package com.transfer.transferservice.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.transfer.transferservice.client.BacenClient;
import com.transfer.transferservice.controller.dto.TransferDTO.DestinationAccountRequest;
import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.model.Account;
import com.transfer.transferservice.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(WireMockExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TransferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private BacenClient bacenClient;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup(){
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterEach
    public void teardown(){
        wireMockServer.stop();
    }

    @Test
    public void shouldReturn200_whenTransferIsValid() throws Exception {
        Account account = new Account();
        account.setName("Maria");
        account.setCpf(434L);
        Account accountSaved = accountRepository.save(account);

        accountSaved.setBalance(500.0);

        accountRepository.save(accountSaved);

        Account accountDestination = new Account();
        accountDestination.setName("João");
        accountDestination.setCpf(111L);
        accountRepository.save(accountDestination);

        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .header("accountAuthor", account.getNumber())
                .content(asJsonString(buildTransferRequest(123L, accountDestination.getNumber())))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Double balance = accountRepository.findById(account.getNumber()).get().getBalance();
        Assertions.assertEquals(400, balance, 0.1);

    }

    @Test
    public void shouldReturn200_whenTransferIsValidAndBetweenDifferentBank() throws Exception {
        Account account = new Account();
        account.setName("Maria");
        account.setCpf(54545L);
        Account accountSaved = accountRepository.save(account);

        accountSaved.setBalance(500.0);

        accountRepository.save(accountSaved);

        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .header("accountAuthor", account.getNumber())
                        .content(asJsonString(buildTransferRequest(3434L, 43543L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Double balance = accountRepository.findById(account.getNumber()).get().getBalance();
        Assertions.assertEquals(398.01, balance, 0.1);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/transfer")));
    }

    @Test
    public void shouldReturn400_whenBacenReturn400ForOneTransfer() throws Exception {
        Account account = new Account();
        account.setName("Maria");
        account.setCpf(5556478788L);
        Account accountSaved = accountRepository.save(account);
        accountSaved.setBalance(500.0);

        accountRepository.save(accountSaved);
        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                        .header("accountAuthor", account.getNumber())
                        .content(asJsonString(buildTransferRequest(0L, 43543L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Banco do destinatario não encontrado"));


        wireMockServer.verify(postRequestedFor(urlEqualTo("/transfer")));

    }

    private TransferRequest buildTransferRequest(Long bank, Long accountNumber){
        return new TransferRequest(100.0, new DestinationAccountRequest(bank, accountNumber));
    }

    private String asJsonString(final Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
