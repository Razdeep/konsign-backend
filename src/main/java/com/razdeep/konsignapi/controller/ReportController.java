package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.service.BuyerService;
import com.razdeep.konsignapi.service.SupplierService;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/report")
public class ReportController {

    private final SupplierService supplierService;
    private final BuyerService buyerService;

    public ReportController(SupplierService supplierService, BuyerService buyerService) {
        this.supplierService = supplierService;
        this.buyerService = buyerService;
    }

    @Timed
    @GetMapping("/supplier")
    public ResponseEntity<byte[]> generateSupplierReport(@RequestParam String supplierId) throws Exception {
        final var bytes = supplierService.generateSupplierLedger(supplierId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @Timed
    @GetMapping("/buyer")
    public ResponseEntity<byte[]> generateBuyerReport(@RequestParam String buyerId) throws Exception {
        final var bytes = buyerService.generateBuyerLedger(buyerId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }
}
