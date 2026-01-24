package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.entity.BuyerEntity;
import com.razdeep.konsignapi.model.CollectionVoucher;
import com.razdeep.konsignapi.model.KonsignApiResponse;
import com.razdeep.konsignapi.model.PendingBill;
import com.razdeep.konsignapi.service.BuyerService;
import com.razdeep.konsignapi.service.CollectionVoucherService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/collection-vouchers")
public class CollectionVoucherController {

    private final CollectionVoucherService collectionVoucherService;
    private final BuyerService buyerService;

    public CollectionVoucherController(CollectionVoucherService collectionVoucherService, BuyerService buyerService) {
        this.collectionVoucherService = collectionVoucherService;
        this.buyerService = buyerService;
    }

    @Timed
    @GetMapping("/{voucherNo}")
    public ResponseEntity<KonsignApiResponse> getCollectionVoucher(@PathVariable String voucherNo) {
        CollectionVoucher collectionVoucher = collectionVoucherService.getVoucherByVoucherNo(voucherNo);
        return ResponseEntity.ok(KonsignApiResponse.builder()
                .data(collectionVoucher)
                .success(true)
                .build());
    }

    @Timed
    @PostMapping
    public ResponseEntity<KonsignApiResponse> addCollectionVoucher(
            @Valid @RequestBody CollectionVoucher collectionVoucher) {
        collectionVoucherService.addCollectionVoucher(collectionVoucher);
        return ResponseEntity.ok(KonsignApiResponse.builder()
                .success(true)
                .message("Successfully added collection voucher")
                .build());
    }

    @Timed
    @DeleteMapping("/{voucherNo}")
    ResponseEntity<KonsignApiResponse> deleteBuyer(@PathVariable String voucherNo) {
        String message = collectionVoucherService.deleteVoucher(voucherNo)
                ? "Successfully deleted Collection Voucher Id: " + voucherNo
                : voucherNo + " is already deleted";

        return ResponseEntity.ok(
                KonsignApiResponse.builder().message(message).success(true).build());
    }

    @Timed
    @GetMapping(value = "/pending-bills", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, Object>> getPendingBillsToBeCollected(
            @RequestParam(required = false) String buyerId, @RequestParam(required = false) String buyerName) {
        List<PendingBill> pendingBills;
        if (buyerId != null && !buyerId.isEmpty()) {
            pendingBills = collectionVoucherService.getPendingBillsToBeCollected(buyerId);
        } else if (buyerName != null && !buyerName.isEmpty()) {
            BuyerEntity retrievedBuyerEntity = buyerService.getBuyerByBuyerName(buyerName);
            if (retrievedBuyerEntity == null) {
                String message = "Buyer name not found in database";
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", message);
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }
            String retriedBuyerId = retrievedBuyerEntity.getBuyerId();
            if (retriedBuyerId == null) {
                String message = "Buyer name not found in database";
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", message);
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }
            pendingBills = collectionVoucherService.getPendingBillsToBeCollected(retriedBuyerId);
        } else {
            String message = "Either buyerId or buyerName must be present the request param";
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", message);
            return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("pendingBills", pendingBills);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }
}
