package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.model.Bill;
import com.razdeep.konsignapi.model.ResponseVerdict;
import com.razdeep.konsignapi.service.BillService;
import io.micrometer.core.annotation.Timed;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    @Timed
    @PostMapping(value = "/")
    public ResponseEntity<ResponseVerdict> addBillEntry(@RequestBody Bill bill) {
        ResponseEntity<ResponseVerdict> response;
        ResponseVerdict responseVerdict = new ResponseVerdict();

        if (bill.anyFieldEmpty()) {
            responseVerdict.setMessage("Saving bill failed because all fields are not properly filled");
            response = new ResponseEntity<>(responseVerdict, HttpStatus.NOT_ACCEPTABLE);
        } else if (billService.enterBill(bill)) {
            responseVerdict.setMessage("Successfully saved bill");
            response = new ResponseEntity<>(responseVerdict, HttpStatus.OK);
        } else {
            responseVerdict.setMessage("Saving bill failed");
            response = new ResponseEntity<>(responseVerdict, HttpStatus.NOT_ACCEPTABLE);
        }
        return response;
    }

    @Timed
    @GetMapping(value = "/")
    public ResponseEntity<ResponseVerdict> getBill(@RequestParam(name = "billNo") String billNo) {
        val bill = billService.getBill(billNo);
        ResponseVerdict responseVerdict = new ResponseVerdict();
        if (bill == null) {
            responseVerdict.setMessage("Bill not found");
            return new ResponseEntity<>(responseVerdict, HttpStatus.NOT_FOUND);
        }
        responseVerdict.setData(bill);
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @GetMapping(value = "/{offset}/{pageSize}")
    public ResponseEntity<ResponseVerdict> getAllBills(@PathVariable int offset, @PathVariable int pageSize) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        val bills = billService.getAllBills(offset, pageSize);
        stopWatch.stop();
        LOG.info("billEntryService.getAllBills() took " + stopWatch.getLastTaskTimeMillis() + " ms");
        ResponseVerdict responseVerdict = new ResponseVerdict();
        if (bills == null) {
            responseVerdict.setMessage("Bill not found");
            return new ResponseEntity<>(responseVerdict, HttpStatus.NOT_FOUND);
        }
        responseVerdict.setData(bills);
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @Timed
    @DeleteMapping(value = "/")
    public ResponseEntity<ResponseVerdict> deleteBill(@RequestParam(name = "billNo") String billNo) {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        if (billService.deleteBill(billNo)) {
            responseVerdict.setMessage("Successfully deleted bill " + billNo);
        } else {
            responseVerdict.setMessage("Bill " + billNo + " is already deleted.");
        }
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }
}
