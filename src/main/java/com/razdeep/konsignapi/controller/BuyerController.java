package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.Buyer;
import com.razdeep.konsignapi.model.KonsignApiResponse;
import com.razdeep.konsignapi.service.BuyerService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/buyers")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @Timed
    @GetMapping
    ResponseEntity<KonsignApiResponse> getBuyers() {
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        konsignApiResponse.setData(buyerService.getBuyers());
        return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
    }

    @Timed
    @PostMapping
    ResponseEntity<KonsignApiResponse> addBuyer(@RequestBody Buyer buyer) {
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        if (buyerService.addBuyer(buyer)) {
            konsignApiResponse.setMessage("Successfully added buyer");
            return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
        } else {
            konsignApiResponse.setMessage("Failed to add supplier");
            return new ResponseEntity<>(konsignApiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Timed
    @DeleteMapping("/{buyerId}")
    ResponseEntity<KonsignApiResponse> deleteBuyer(@PathVariable String buyerId) {
        String message;
        if (buyerService.deleteBuyer(buyerId)) {
            message = "Successfully deleted buyer Id: " + buyerId;
        } else {
            message = buyerId + " is already deleted";
        }
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        konsignApiResponse.setMessage(message);
        return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
    }
}
