package com.transfer.transferservice.client;

import com.transfer.transferservice.client.model.TransferRequestBacen;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bacen-service", url = "${bacen.service.url}")
public interface BacenClient {

    @PostMapping("/transfer")
    String transfer(@RequestBody TransferRequestBacen transferRequest);
}
