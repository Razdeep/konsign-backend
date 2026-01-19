package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.KonsignApiResponse;
import com.razdeep.konsignapi.model.Supplier;
import com.razdeep.konsignapi.service.SupplierService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Timed
    @GetMapping
    ResponseEntity<KonsignApiResponse> getSuppliers() {
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        konsignApiResponse.setData(supplierService.getSuppliers());
        return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
    }

    @Timed
    @PostMapping
    ResponseEntity<KonsignApiResponse> addSupplier(@RequestBody Supplier supplier) {
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        if (supplierService.addSupplier(supplier)) {
            konsignApiResponse.setMessage("Successfully added supplier");
            return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
        } else {
            konsignApiResponse.setMessage("Failed to add supplier. Most probably because it already exists");
            return new ResponseEntity<>(konsignApiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Timed
    @DeleteMapping("/{supplierId}")
    ResponseEntity<KonsignApiResponse> deleteSupplier(@PathVariable String supplierId) {
        String message;
        if (supplierService.deleteSupplier(supplierId)) {
            message = "Successfully deleted Supplier Id: " + supplierId;
        } else {
            message = supplierId + " is already deleted";
        }
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        konsignApiResponse.setMessage(message);
        return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
    }
}
