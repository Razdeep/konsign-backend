package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.CollectionVoucher;
import com.razdeep.konsignapi.model.KonsignApiResponse;
import com.razdeep.konsignapi.model.PendingBill;
import com.razdeep.konsignapi.service.CollectionVoucherService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/collection-vouchers")
public class CollectionVoucherController {

    private final CollectionVoucherService collectionVoucherService;

    public CollectionVoucherController(CollectionVoucherService collectionVoucherService) {
        this.collectionVoucherService = collectionVoucherService;
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
    @GetMapping(value = "/pending-bills")
    ResponseEntity<KonsignApiResponse> getPendingBillsToBeCollected(
            @RequestParam(required = false) String buyerId, @RequestParam(required = false) String buyerName)
            throws BadRequestException {
        List<PendingBill> pendingBills = collectionVoucherService.getPendingBillsToBeCollected(buyerId, buyerName);

        return ResponseEntity.ok(
                KonsignApiResponse.builder().success(true).data(pendingBills).build());
    }
}
