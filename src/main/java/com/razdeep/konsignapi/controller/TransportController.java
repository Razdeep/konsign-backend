package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.KonsignApiResponse;
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
    ResponseEntity<KonsignApiResponse> addTransport(@RequestBody Transport transport) {
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        if (transportService.addTransport(transport)) {
            konsignApiResponse.setMessage("Successfully added transport");
            return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
        } else {
            konsignApiResponse.setMessage("Failed to add transport");
            return new ResponseEntity<>(konsignApiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Timed
    @GetMapping
    ResponseEntity<KonsignApiResponse> getTransports() {
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        konsignApiResponse.setData(transportService.getTransports());
        return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
    }

    @Timed
    @DeleteMapping("/{transportId}")
    ResponseEntity<KonsignApiResponse> deleteTransport(@PathVariable String transportId) {
        String message;
        if (transportService.deleteTransport(transportId)) {
            message = "Successfully deleted transport Id: " + transportId;
        } else {
            message = transportId + " is already deleted";
        }
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        konsignApiResponse.setMessage(message);
        return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
    }
}
