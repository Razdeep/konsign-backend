package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.Bill;
import com.razdeep.konsignapi.model.KonsignApiResponse;
import com.razdeep.konsignapi.service.BillService;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/bills")
public class BillController {

    private static final Logger LOG = LoggerFactory.getLogger(BillController.class.getName());

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @Timed
    @PostMapping
    public ResponseEntity<KonsignApiResponse> addBillEntry(@RequestBody Bill bill) {

        billService.addBill(bill);
        KonsignApiResponse konsignApiResponse = KonsignApiResponse.builder()
                .message("Successfully saved bill")
                .success(true)
                .build();

        return ResponseEntity.ok(konsignApiResponse);
    }

    @Timed
    @GetMapping("/{billNo}")
    public ResponseEntity<KonsignApiResponse> getBill(@PathVariable String billNo) {
        final var bill = billService.getBill(billNo);
        KonsignApiResponse konsignApiResponse =
                KonsignApiResponse.builder().success(true).data(bill).build();
        return ResponseEntity.ok(konsignApiResponse);
    }

    @Timed
    @GetMapping
    public ResponseEntity<KonsignApiResponse> getAllBills(
            @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "5") int pageSize) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final var bills = billService.getAllBills(offset, pageSize);
        stopWatch.stop();
        LOG.info("billEntryService.getAllBills() took {} ms", stopWatch.getLastTaskTimeMillis());
        return new ResponseEntity<>(
                KonsignApiResponse.builder().success(true).data(bills).build(), HttpStatus.OK);
    }

    @Timed
    @DeleteMapping("/{billNo}")
    public ResponseEntity<KonsignApiResponse> deleteBill(@PathVariable String billNo) {
        KonsignApiResponse konsignApiResponse = new KonsignApiResponse();
        if (billService.deleteBill(billNo)) {
            konsignApiResponse.setMessage("Successfully deleted bill " + billNo);
        } else {
            konsignApiResponse.setMessage("Bill " + billNo + " is already deleted.");
        }
        return new ResponseEntity<>(konsignApiResponse, HttpStatus.OK);
    }
}
