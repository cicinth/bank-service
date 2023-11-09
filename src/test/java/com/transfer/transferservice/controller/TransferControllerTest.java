package com.transfer.transferservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.transferservice.controller.dto.TransferDTO.DestinationAccountRequest;
import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.exceptions.NotFoundException;
import com.transfer.transferservice.exceptions.ValueNotAllowed;
import com.transfer.transferservice.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(TransferController.class)
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;


    @Test
    public void shouldReturn200_whenTransferIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .header("accountAuthor", "1")
                .content(asJsonString(buildTransferRequest()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400_whenTransferIsInvalid() throws Exception {
        when(transferService.transfer(1L, buildTransferRequest())).thenThrow(
                new ValueNotAllowed("Insufficient funds")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .header("accountAuthor", "1")
                .content(asJsonString(buildTransferRequest()))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    private TransferRequest buildTransferRequest(){
        return new TransferRequest(100.0, new DestinationAccountRequest(123L, 1343L));
    }

    private String asJsonString(final Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

}
