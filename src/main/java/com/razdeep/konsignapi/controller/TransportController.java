package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.ResponseVerdict;
import com.razdeep.konsignapi.model.Transport;
import com.razdeep.konsignapi.service.TransportService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/transports")
public class TransportController {

    private final TransportService transportService;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @Timed
    @PostMapping
    ResponseEntity<ResponseVerdict> addTransport(@RequestBody Transport transport) {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        if (transportService.addTransport(transport)) {
            responseVerdict.setMessage("Successfully added transport");
            return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
        } else {
            responseVerdict.setMessage("Failed to add transport");
            return new ResponseEntity<>(responseVerdict, HttpStatus.BAD_REQUEST);
        }
    }

    @Timed
    @GetMapping
    ResponseEntity<ResponseVerdict> getTransports() {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setData(transportService.getTransports());
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @DeleteMapping("/{transportId}")
    ResponseEntity<ResponseVerdict> deleteTransport(@PathVariable String transportId) {
        String message;
        if (transportService.deleteTransport(transportId)) {
            message = "Successfully deleted transport Id: " + transportId;
        } else {
            message = transportId + " is already deleted";
        }
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setMessage(message);
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }
}
