package com.razdeep.konsignapi.controller;

import com.google.gson.Gson;
import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.entity.BuyerEntity;
import com.razdeep.konsignapi.model.CollectionVoucher;
import com.razdeep.konsignapi.model.PendingBill;
import com.razdeep.konsignapi.service.BuyerService;
import com.razdeep.konsignapi.service.CollectionVoucherService;
import io.micrometer.core.annotation.Timed;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/collection-vouchers")
public class CollectionVoucherController {

    private final Gson gson;

    private final CollectionVoucherService collectionVoucherService;
    private final BuyerService buyerService;

    @Autowired
    public CollectionVoucherController(
            Gson gson, CollectionVoucherService collectionVoucherService, BuyerService buyerService) {
        this.gson = gson;
        this.collectionVoucherService = collectionVoucherService;
        this.buyerService = buyerService;
    }

    @Timed
    @GetMapping("/")
    public ResponseEntity<String> getCollectionVoucher(@RequestParam("voucherNo") String voucherNo) {
        ResponseEntity<String> response;
        CollectionVoucher collectionVoucher = collectionVoucherService.getVoucherByVoucherNo(voucherNo);
        if (collectionVoucher == null) {
            return new ResponseEntity<>("{}", HttpStatus.NOT_FOUND);
        }

        response = new ResponseEntity<>(gson.toJson(collectionVoucher), HttpStatus.OK);
        return response;
    }

    @Timed
    @PostMapping("/")
    public ResponseEntity<String> addCollectionVoucher(@RequestBody CollectionVoucher collectionVoucher) {
        Map<String, String> body = new HashMap<>();
        ResponseEntity<String> response;
        if (collectionVoucherService.addCollectionVoucher(collectionVoucher)) {
            body.put("message", "Successfully added collection voucher");
            response = new ResponseEntity<>(gson.toJson(body), HttpStatus.OK);
        } else {
            body.put("message", "Saving collection voucher failed");
            response = new ResponseEntity<>(gson.toJson(body), HttpStatus.NOT_ACCEPTABLE);
        }
        return response;
    }

    @Timed
    @DeleteMapping("/{voucherNo}")
    ResponseEntity<String> deleteBuyer(@PathVariable String voucherNo) {
        String message;
        if (collectionVoucherService.deleteVoucher(voucherNo)) {
            message = "Successfully deleted Collection Voucher Id: " + voucherNo;
        } else {
            message = voucherNo + " is already deleted";
        }
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", message);
        return new ResponseEntity<>(gson.toJson(responseMap), HttpStatus.OK);
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
