package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.ResponseVerdict;
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
    ResponseEntity<ResponseVerdict> getSuppliers() {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        responseVerdict.setData(supplierService.getSuppliers());
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @PostMapping
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
    @DeleteMapping("/{supplierId}")
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
}
