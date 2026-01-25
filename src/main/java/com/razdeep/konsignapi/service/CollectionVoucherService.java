package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.BuyerEntity;
import com.razdeep.konsignapi.entity.CollectionVoucherEntity;
import com.razdeep.konsignapi.exception.ResourceNotFoundException;
import com.razdeep.konsignapi.mapper.CollectionVoucherMapper;
import com.razdeep.konsignapi.model.Bill;
import com.razdeep.konsignapi.model.CollectionVoucher;
import com.razdeep.konsignapi.model.CollectionVoucherItem;
import com.razdeep.konsignapi.model.PendingBill;
import com.razdeep.konsignapi.repository.CollectionVoucherRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.BadRequestException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class CollectionVoucherService {

    private final CollectionVoucherRepository collectionVoucherRepository;
    private final BuyerService buyerService;
    private final BillService billService;
    private final CollectionVoucherMapper collectionVoucherMapper;

    public CollectionVoucherService(
            CollectionVoucherRepository collectionVoucherRepository,
            BuyerService buyerService,
            BillService billService,
            CollectionVoucherMapper collectionVoucherMapper) {
        this.collectionVoucherRepository = collectionVoucherRepository;
        this.buyerService = buyerService;
        this.billService = billService;
        this.collectionVoucherMapper = collectionVoucherMapper;
    }

    public void addCollectionVoucher(CollectionVoucher collectionVoucher) {

        BuyerEntity buyerEntity = buyerService.getBuyerByBuyerName(collectionVoucher.getBuyerName());

        CollectionVoucherEntity collectionVoucherEntity = collectionVoucherMapper.toEntity(collectionVoucher);
        collectionVoucherEntity.setBuyer(buyerEntity);

        final var collectionVoucherItemEntityList = collectionVoucherEntity.getCollectionVoucherItemEntityList();
        final var collectionVoucherItemList = collectionVoucher.getCollectionVoucherItemList();
        for (int i = 0; i < collectionVoucherItemList.size(); ++i) {
            final var targetBill =
                    billService.getBill(collectionVoucherItemList.get(i).getBillNo());
            final var targetBillEntity = billService.convertBillIntoBillEntity(targetBill);
            collectionVoucherItemEntityList.get(i).setBill(targetBillEntity);
        }

        collectionVoucherRepository.save(collectionVoucherEntity);
    }

    public boolean deleteVoucher(String voucherNo) {
        boolean wasPresent = collectionVoucherRepository.findById(voucherNo).isPresent();
        collectionVoucherRepository.deleteById(voucherNo);
        return wasPresent;
    }

    public List<PendingBill> getPendingBillsToBeCollected(String buyerId, String buyerName) throws BadRequestException {

        if ((buyerName == null || buyerName.isEmpty()) && (buyerId == null || buyerId.isEmpty())) {
            throw new BadRequestException("buyerName and buyerId is empty");
        }

        if (buyerId == null || buyerId.isEmpty()) {
            buyerId = buyerService.getBuyerByBuyerName(buyerName).getBuyerId();
        }

        List<Bill> billsByBuyerId = billService.getBillsByBuyerId(buyerId);

        final var collectedAmountSoFar = this.getCollectedAmountInfoForBuyerId(buyerId);
        List<PendingBill> res = new ArrayList<>();
        for (final var billByBuyerId : billsByBuyerId) {
            if (collectedAmountSoFar.containsKey(billByBuyerId.billNo())) {
                if (billByBuyerId.billAmount().compareTo(collectedAmountSoFar.get(billByBuyerId.billNo())) > 0) {
                    final var pendingBillAmount =
                            billByBuyerId.billAmount().subtract(collectedAmountSoFar.get(billByBuyerId.billNo()));
                    final var pendingBill = PendingBill.builder()
                            .billNo(billByBuyerId.billNo())
                            .billAmount(billByBuyerId.billAmount())
                            .buyerName(billByBuyerId.buyerName())
                            .supplierName(billByBuyerId.supplierName())
                            .pendingAmount(pendingBillAmount)
                            .build();
                    res.add(pendingBill);
                }
            } else {
                final var pendingBill = PendingBill.builder()
                        .billNo(billByBuyerId.billNo())
                        .billAmount(billByBuyerId.billAmount())
                        .buyerName(billByBuyerId.buyerName())
                        .supplierName(billByBuyerId.supplierName())
                        .pendingAmount(billByBuyerId.billAmount())
                        .build();
                res.add(pendingBill);
            }
        }
        return res;
    }

    private Map<String, BigDecimal> getCollectedAmountInfoForBuyerId(String buyerId) {
        final var collectionVouchers = collectionVoucherRepository.getCollectedAmountInfoForBuyerId(buyerId);
        Map<String, BigDecimal> res = new HashMap<>();

        for (final var collectionVoucher : collectionVouchers) {
            for (final var collectionVoucherItem : collectionVoucher.getCollectionVoucherItemEntityList()) {
                if (res.containsKey(collectionVoucherItem.getBill().getBillNo())) {
                    final var newValue = res.get(collectionVoucherItem.getBill().getBillNo())
                            .add(collectionVoucherItem.getAmountCollected());
                    res.put(collectionVoucherItem.getBill().getBillNo(), newValue);
                } else {
                    res.put(collectionVoucherItem.getBill().getBillNo(), collectionVoucherItem.getAmountCollected());
                }
            }
        }

        return res;
    }

    private BigDecimal getCollectedAmountForBillNo(String billNo) {
        return collectionVoucherRepository.getCollectedAmountForBillNo(billNo);
    }

    private CollectionVoucherItem enrichWithPendingAmount(CollectionVoucherItem collectionVoucherItem) {
        String billNo = collectionVoucherItem.getBillNo();
        BigDecimal billAmount = collectionVoucherItem.getBillAmount();
        BigDecimal getCollectedAmount = getCollectedAmountForBillNo(billNo);
        BigDecimal pendingAmount = billAmount.subtract(getCollectedAmount);
        collectionVoucherItem.setPendingBillAmount(pendingAmount);
        return collectionVoucherItem;
    }

    @Nullable
    public CollectionVoucher getVoucherByVoucherNo(String voucherNo) {
        CollectionVoucherEntity collectionVoucherEntity =
                collectionVoucherRepository.getCollectionVoucherByVoucherNo(voucherNo);

        if (collectionVoucherEntity == null) {
            throw new ResourceNotFoundException("Collection Voucher " + voucherNo + " Not Found");
        }

        CollectionVoucher collectionVoucher = collectionVoucherMapper.toModel(collectionVoucherEntity);
        collectionVoucher.setCollectionVoucherItemList(collectionVoucher.getCollectionVoucherItemList().stream()
                .map(this::enrichWithPendingAmount)
                .toList());

        return collectionVoucher;
    }
}
