package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.Buyer;
import com.razdeep.konsignapi.model.ResponseVerdict;
import com.razdeep.konsignapi.model.Supplier;
import com.razdeep.konsignapi.model.Transport;
import com.razdeep.konsignapi.service.BuyerService;
import com.razdeep.konsignapi.service.SupplierService;
import com.razdeep.konsignapi.service.TransportService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController(KonsignConstant.CONTROLLER_API_PREFIX)
public class MasterController {

    private final SupplierService supplierService;
    private final BuyerService buyerService;
    private final TransportService transportService;

    @Autowired
    public MasterController(
            SupplierService supplierService, BuyerService buyerService, TransportService transportService) {
        this.supplierService = supplierService;
        this.buyerService = buyerService;
        this.transportService = transportService;
    }

    @Timed
    @GetMapping("/suppliers")
    ResponseEntity<ResponseVerdict> getSuppliers() {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setData(supplierService.getSuppliers());
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @PostMapping("/suppliers")
    ResponseEntity<ResponseVerdict> addSupplier(@RequestBody Supplier supplier) {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        if (supplierService.addSupplier(supplier)) {
            responseVerdict.setMessage("Successfully added supplier");
            return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
        } else {
            responseVerdict.setMessage("Failed to add supplier. Most probably because it already exists");
            return new ResponseEntity<>(responseVerdict, HttpStatus.BAD_REQUEST);
        }
    }

    @Timed
    @DeleteMapping("/suppliers/{supplierId}")
    ResponseEntity<ResponseVerdict> deleteSupplier(@PathVariable String supplierId) {
        String message;
        if (supplierService.deleteSupplier(supplierId)) {
            message = "Successfully deleted Supplier Id: " + supplierId;
        } else {
            message = supplierId + " is already deleted";
        }
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setMessage(message);
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @GetMapping("/buyers")
    ResponseEntity<ResponseVerdict> getBuyers() {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setData(buyerService.getBuyers());
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @PostMapping("/buyers")
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
    @DeleteMapping("/buyers/{buyerId}")
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

    @Timed
    @PostMapping("/transports")
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
    @GetMapping("/transports")
    ResponseEntity<ResponseVerdict> getTransports() {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setData(transportService.getTransports());
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @DeleteMapping("/transports/{transportId}")
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
