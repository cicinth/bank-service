package com.transfer.transferservice.controller;

import com.transfer.transferservice.controller.dto.TransferDTO.TransferRequest;
import com.transfer.transferservice.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    TransferService transferService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@RequestHeader Long accountAuthor, @RequestBody  TransferRequest transferRequest) {
        transferService.transfer(accountAuthor, transferRequest);
    }
}
