package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.*;
import com.razdeep.konsignapi.mapper.BillMapper;
import com.razdeep.konsignapi.model.Bill;
import com.razdeep.konsignapi.model.CustomPageImpl;
import com.razdeep.konsignapi.model.LrPm;
import com.razdeep.konsignapi.repository.BillEntryRepository;
import lombok.val;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BillService {

    private final Logger LOG = LoggerFactory.getLogger(BillService.class.getName());

    private final BillMapper billMapper;

    private final BuyerService buyerService;
    private final SupplierService supplierService;
    private final TransportService transportService;

    private final CommonService commonService;
    private final BillEntryRepository billEntryRepository;

    @Autowired
    public BillService(BuyerService buyerService, SupplierService supplierService,
                       TransportService transportService, CommonService commonService,
                       BillEntryRepository billEntryRepository) {
        this.buyerService = buyerService;
        this.supplierService = supplierService;
        this.transportService = transportService;
        this.commonService = commonService;
        this.billEntryRepository = billEntryRepository;
        this.billMapper = Mappers.getMapper(BillMapper.class);
    }

    @Caching(evict = {
            @CacheEvict(value = "getAllBills", allEntries = true),
            @CacheEvict(value = "getBill", key = "#bill.billNo")
    })
    public boolean enterBill(Bill bill) {

        BuyerEntity buyerEntity = buyerService.getBuyerByBuyerName(bill.getBuyerName());
        SupplierEntity supplierEntity = supplierService.getSupplierBySupplierName(bill.getSupplierName());
        TransportEntity transportEntity = transportService.getTransportByTransportName(bill.getTransportName());

        if (buyerEntity == null || supplierEntity == null || transportEntity == null || bill.getLrPmList() == null) {
            return false;
        }

        BillEntity billEntity = BillEntity.builder()
                .buyerEntity(buyerEntity)
                .billNo(bill.getBillNo())
                .billAmount(bill.getBillAmount())
                .billDate(bill.getBillDate())
                .lrDate(bill.getLrDate())
                .supplierEntity(supplierEntity)
                .transportEntity(transportEntity)
                .build();

        AtomicInteger lr_pm_index = new AtomicInteger();

        List<LrPmEntity> lrPmEntityList = bill.getLrPmList().stream()
                .map(lrPm -> {
                    LrPmEntity lrPmEntity = new LrPmEntity(lrPm);
                    lrPmEntity.setLrPmId(bill.getBillNo() + "_" + lr_pm_index.getAndIncrement());
                    lrPmEntity.setBillEntry(billEntity);
                    return lrPmEntity;
                })
                .collect(Collectors.toList());

        billEntity.setLrPmEntityList(lrPmEntityList);

        billEntryRepository.save(billEntity);
        return true;
    }

    public Bill getBill(String billNo) {
        String agencyId = commonService.getAgencyId();
        return getBill(billNo, agencyId);
    }

    @Cacheable(value = "getBill", key = "#billNo.concat(#agencyId)")
    public Bill getBill(String billNo, String agencyId) {

        val billEntryOptional = billEntryRepository.findByBillNoAndAgencyId(billNo, agencyId);
        if (billEntryOptional.isEmpty()) {
            return null;
        }
        val billEntry = billEntryOptional.get();

        List<LrPm> lrPmList = billEntry.getLrPmEntityList().stream()
                .map((lrPmEntity -> new LrPm(lrPmEntity.getLr(), lrPmEntity.getPm())))
                .collect(Collectors.toList());

        return Bill.builder()
                .billNo(billEntry.getBillNo())
                .billAmount(billEntry.getBillAmount())
                .billDate(billEntry.getBillDate())
                .buyerName(billEntry.getBuyerEntity().getBuyerName())
                .supplierName(billEntry.getSupplierEntity().getSupplierName())
                .transportName(billEntry.getTransportEntity().getTransportName())
                .lrPmList(lrPmList)
                .lrDate(billEntry.getLrDate())
                .build();

//        return billMapper.billEntityToBill(billEntry);
    }

    @Caching(evict = {
            @CacheEvict(value = "getAllBills", allEntries = true),
            @CacheEvict(value = "getBill", key = "#bill.billNo")
    })
    public boolean deleteBill(String billNo) {
        boolean wasPresent = false;
        if (billEntryRepository.findById(billNo).isPresent()) {
            wasPresent = true;
            billEntryRepository.deleteById(billNo);
        }
        return wasPresent;
    }

    public List<Bill> getBillsByBuyerId(String buyerId) {
        List<BillEntity> billEntityList = billEntryRepository.findAllBillsByBuyerId(buyerId);
        return billEntityList.stream().map(Bill::new).collect(Collectors.toList());
    }

    public BillEntity convertBillIntoBillEntity(Bill bill) {
        val targetSupplierEntity = supplierService.getSupplierBySupplierName(bill.getSupplierName());
        val targetBuyerEntity = buyerService.getBuyerByBuyerName(bill.getBuyerName());
        val targetTransportEntity = transportService.getTransportByTransportName(bill.getTransportName());
        List<LrPmEntity> targetLrPmEntityList = new ArrayList<>();
        if (bill.getLrPmList() != null) {
            targetLrPmEntityList = bill.getLrPmList().stream().map(LrPmEntity::new)
                    .collect(Collectors.toList());
        }
        return BillEntity.builder()
                .billNo(bill.getBillNo())
                .supplierEntity(targetSupplierEntity)
                .buyerEntity(targetBuyerEntity)
                .billDate(bill.getBillDate())
                .transportEntity(targetTransportEntity)
                .lrDate(bill.getLrDate())
                .billAmount(bill.getBillAmount())
                .lrPmEntityList(targetLrPmEntityList)
                .build();
    }

    public CustomPageImpl<Bill> getAllBills(int offset, int size) {
        String agencyId = commonService.getAgencyId();
        return getAllBills(offset, size, agencyId);
    }

    @Cacheable(value = "getAllBills", key = "{#offset,#size,#agencyId}")
    public CustomPageImpl<Bill> getAllBills(int offset, int size, String agencyId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Pageable pageable = PageRequest.of(offset, size, Sort.by("billNo").descending());
        val billEntityPages = billEntryRepository.findByAgencyId(agencyId, pageable);
        stopWatch.stop();
        LOG.info("repository call took {} ms", stopWatch.getLastTaskTimeMillis());

        stopWatch.start();
        val billList = billEntityPages.stream()
                .map(Bill::new)
                .collect(Collectors.toList());
        stopWatch.stop();
        LOG.info("repository stream api conversion took {} ms", stopWatch.getLastTaskTimeMillis());

        val pageNumber = billEntityPages.getPageable().getPageNumber();
        val pageSize = billEntityPages.getPageable().getPageSize();

        return new CustomPageImpl<Bill>(billList, pageNumber, pageSize, billEntityPages.getTotalElements());
    }
}
