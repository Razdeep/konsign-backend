package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.*;
import com.razdeep.konsignapi.exception.ResourceNotFoundException;
import com.razdeep.konsignapi.exception.SaveResourceException;
import com.razdeep.konsignapi.mapper.BillMapper;
import com.razdeep.konsignapi.model.Bill;
import com.razdeep.konsignapi.model.CustomPageImpl;
import com.razdeep.konsignapi.repository.BillEntryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
public class BillService {

    private final Logger LOG = LoggerFactory.getLogger(BillService.class.getName());

    private final BillMapper billMapper;

    private final BuyerService buyerService;
    private final SupplierService supplierService;
    private final TransportService transportService;

    private final CommonService commonService;
    private final BillEntryRepository billEntryRepository;

    public BillService(
            BuyerService buyerService,
            SupplierService supplierService,
            TransportService transportService,
            CommonService commonService,
            BillEntryRepository billEntryRepository,
            BillMapper billMapper) {
        this.buyerService = buyerService;
        this.supplierService = supplierService;
        this.transportService = transportService;
        this.commonService = commonService;
        this.billEntryRepository = billEntryRepository;
        this.billMapper = billMapper;
    }

    public void addBill(Bill bill) {

        BuyerEntity buyerEntity = buyerService.getBuyerByBuyerName(bill.buyerName());
        SupplierEntity supplierEntity = supplierService.getSupplierBySupplierName(bill.supplierName());
        TransportEntity transportEntity = transportService.getTransportByTransportName(bill.transportName());

        if (buyerEntity == null || supplierEntity == null || transportEntity == null || bill.lrPmList() == null) {
            throw new SaveResourceException(bill.billNo() + " could not be saved");
        }

        BillEntity billEntity = billMapper.toEntity(bill);
        billEntity.setBuyerEntity(buyerEntity);
        billEntity.setSupplierEntity(supplierEntity);
        billEntity.setTransportEntity(transportEntity);

        billEntryRepository.save(billEntity);
    }

    public Bill getBill(String billNo) {
        String tenantId = commonService.getTenantId();
        return getBill(billNo, tenantId);
    }

    public Bill getBill(String billNo, String tenantId) throws ResourceNotFoundException {

        final var billEntryOptional = billEntryRepository.findByBillNoAndTenantId(billNo, tenantId);
        if (billEntryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Bill " + billNo + " not found");
        }
        final var billEntry = billEntryOptional.get();

        return billMapper.toModel(billEntry);
    }

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
        return billEntityList.stream().map(billMapper::toModel).collect(Collectors.toList());
    }

    public BillEntity convertBillIntoBillEntity(Bill bill) {
        final var targetSupplierEntity = supplierService.getSupplierBySupplierName(bill.supplierName());
        final var targetBuyerEntity = buyerService.getBuyerByBuyerName(bill.buyerName());
        final var targetTransportEntity = transportService.getTransportByTransportName(bill.transportName());
        List<LrPmEntity> targetLrPmEntityList = new ArrayList<>();
        if (bill.lrPmList() != null) {
            targetLrPmEntityList = bill.lrPmList().stream().map(LrPmEntity::new).collect(Collectors.toList());
        }
        return BillEntity.builder()
                .billNo(bill.billNo())
                .supplierEntity(targetSupplierEntity)
                .buyerEntity(targetBuyerEntity)
                .billDate(bill.billDate())
                .transportEntity(targetTransportEntity)
                .lrDate(bill.lrDate())
                .billAmount(bill.billAmount())
                .lrPmEntityList(targetLrPmEntityList)
                .build();
    }

    public CustomPageImpl<Bill> getAllBills(int offset, int size) {
        String agencyId = commonService.getTenantId();
        return getAllBills(offset, size, agencyId);
    }

    public CustomPageImpl<Bill> getAllBills(int offset, int size, String agencyId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Pageable pageable = PageRequest.of(offset, size, Sort.by("billNo").descending());
        final var billEntityPages = billEntryRepository.findByTenantId(agencyId, pageable);
        stopWatch.stop();
        LOG.info("repository call took {} ms", stopWatch.getLastTaskTimeMillis());

        stopWatch.start();
        final var billList = billEntityPages.stream().map(billMapper::toModel).collect(Collectors.toList());
        stopWatch.stop();
        LOG.info("repository stream api conversion took {} ms", stopWatch.getLastTaskTimeMillis());

        final var pageNumber = billEntityPages.getPageable().getPageNumber();
        final var pageSize = billEntityPages.getPageable().getPageSize();

        return new CustomPageImpl<>(billList, pageNumber, pageSize, billEntityPages.getTotalElements());
    }
}
