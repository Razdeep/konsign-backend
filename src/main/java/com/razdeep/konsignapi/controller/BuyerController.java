package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.Buyer;
import com.razdeep.konsignapi.model.ResponseVerdict;
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
    ResponseEntity<ResponseVerdict> getBuyers() {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setData(buyerService.getBuyers());
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @PostMapping
    ResponseEntity<ResponseVerdict> addBuyer(@RequestBody Buyer buyer) {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        if (buyerService.addBuyer(buyer)) {
            responseVerdict.setMessage("Successfully added buyer");
            return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
        } else {
            responseVerdict.setMessage("Failed to add supplier");
            return new ResponseEntity<>(responseVerdict, HttpStatus.BAD_REQUEST);
        }
    }

    @Timed
    @DeleteMapping("/{buyerId}")
    ResponseEntity<ResponseVerdict> deleteBuyer(@PathVariable String buyerId) {
        String message;
        if (buyerService.deleteBuyer(buyerId)) {
            message = "Successfully deleted buyer Id: " + buyerId;
        } else {
            message = buyerId + " is already deleted";
        }
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setMessage(message);
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }
}
