package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.service.SupplierService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX)
public class ReportController {

    private final SupplierService supplierService;

    public ReportController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Timed
    @GetMapping("/supplierReport")
    public ResponseEntity<byte[]> generateSupplierReport(@RequestParam String supplierId) throws Exception {
        final var bytes = supplierService.generateSupplierLedger(supplierId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }
}
