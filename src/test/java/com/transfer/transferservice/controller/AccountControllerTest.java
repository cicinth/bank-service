package com.transfer.transferservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.transferservice.controller.dto.AccountDTO.AccountRequest;
import com.transfer.transferservice.controller.dto.AccountDTO.AccountResponse;
import com.transfer.transferservice.controller.dto.TransferDTO.DestinationAccountRequest;
import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.controller.dto.mapper.AccountMapper;
import com.transfer.transferservice.service.AccountService;
import com.transfer.transferservice.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountMapper accountMapper;

    @BeforeEach
    public void setup(){
        when(accountMapper.toResponse(any())).thenReturn(new AccountResponse(2334L, "Jualiana", 3454L));
    }

    @Test
    public void shouldReturn201_whenAccountIsCreated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .content(asJsonString(buildAccountRequest()))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("2334"))
                .andExpect(jsonPath("$.name").value("Jualiana"));
    }

    private AccountRequest buildAccountRequest(){
        return new AccountRequest("Juliana", 3242L);
    }

    private String asJsonString(final Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }
}
