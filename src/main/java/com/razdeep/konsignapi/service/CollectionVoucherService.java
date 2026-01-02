package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.CollectionVoucherEntity;
import com.razdeep.konsignapi.entity.CollectionVoucherItemEntity;
import com.razdeep.konsignapi.model.Bill;
import com.razdeep.konsignapi.model.CollectionVoucher;
import com.razdeep.konsignapi.model.CollectionVoucherItem;
import com.razdeep.konsignapi.model.PendingBill;
import com.razdeep.konsignapi.repository.CollectionVoucherRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class CollectionVoucherService {

    private final CollectionVoucherRepository collectionVoucherRepository;
    private final BuyerService buyerService;
    private final BillService billService;

    @Autowired
    public CollectionVoucherService(
            CollectionVoucherRepository collectionVoucherRepository,
            BuyerService buyerService,
            BillService billService) {
        this.collectionVoucherRepository = collectionVoucherRepository;
        this.buyerService = buyerService;
        this.billService = billService;
    }

    public boolean addCollectionVoucher(CollectionVoucher collectionVoucher) {
        if (collectionVoucher.getCollectionVoucherItemList() == null) {
            return false;
        }

        CollectionVoucherEntity collectionVoucherEntity = CollectionVoucherEntity.builder()
                .voucherNo(collectionVoucher.getVoucherNo())
                .voucherDate(LocalDate.parse(collectionVoucher.getVoucherDate()))
                .buyer(buyerService.getBuyerByBuyerName(collectionVoucher.getBuyerName()))
                .build();

        //
        // collectionVoucherEntity.setCreationTimestamp(getVoucherByVoucherNo(collectionVoucher.getVoucherNo()));

        List<CollectionVoucherItemEntity> collectionVoucherItemEntityList;
        AtomicInteger collectionVoucherItemIndex = new AtomicInteger();

        collectionVoucherItemEntityList = collectionVoucher.getCollectionVoucherItemList().stream()
                .map(collectionVoucherItem -> {
                    val targetBill = billService.getBill(collectionVoucherItem.getBillNo());
                    val targetBilEntity = billService.convertBillIntoBillEntity(targetBill);
                    val collectionVoucherItemId =
                            collectionVoucher.getVoucherNo() + "_" + collectionVoucherItemIndex.getAndIncrement();
                    return CollectionVoucherItemEntity.builder()
                            .collectionVoucherItemId(collectionVoucherItemId)
                            .collectionVoucher(collectionVoucherEntity)
                            .bill(targetBilEntity)
                            .amountCollected(collectionVoucherItem.getAmountCollected())
                            .bank(collectionVoucherItem.getBank())
                            .ddNo(collectionVoucherItem.getDdNo())
                            .ddDate(LocalDate.parse(collectionVoucherItem.getDdDate()))
                            .build();
                })
                .collect(Collectors.toList());

        collectionVoucherEntity.setCollectionVoucherItemEntityList(collectionVoucherItemEntityList);
        collectionVoucherRepository.save(collectionVoucherEntity);
        return true;
    }

    public boolean deleteVoucher(String voucherNo) {
        boolean wasPresent = collectionVoucherRepository.findById(voucherNo).isPresent();
        collectionVoucherRepository.deleteById(voucherNo);
        return wasPresent;
    }

    public List<PendingBill> getPendingBillsToBeCollected(String buyerId) {
        List<Bill> billsByBuyerId = billService.getBillsByBuyerId(buyerId);
        val collectedAmountSoFar = this.getCollectedAmountInfoForBuyerId(buyerId);
        List<PendingBill> res = new ArrayList<>();
        for (val billByBuyerId : billsByBuyerId) {
            if (collectedAmountSoFar.containsKey(billByBuyerId.getBillNo())) {
                if (billByBuyerId.getBillAmount() > collectedAmountSoFar.get(billByBuyerId.getBillNo())) {
                    val pendingBillAmount =
                            billByBuyerId.getBillAmount() - collectedAmountSoFar.get(billByBuyerId.getBillNo());
                    val pendingBill = PendingBill.builder()
                            .billNo(billByBuyerId.getBillNo())
                            .billAmount(billByBuyerId.getBillAmount())
                            .buyerName(billByBuyerId.getBuyerName())
                            .supplierName(billByBuyerId.getSupplierName())
                            .pendingAmount(pendingBillAmount)
                            .build();
                    res.add(pendingBill);
                }
            } else {
                val pendingBill = PendingBill.builder()
                        .billNo(billByBuyerId.getBillNo())
                        .billAmount(billByBuyerId.getBillAmount())
                        .buyerName(billByBuyerId.getBuyerName())
                        .supplierName(billByBuyerId.getSupplierName())
                        .pendingAmount(billByBuyerId.getBillAmount())
                        .build();
                res.add(pendingBill);
            }
        }
        return res;
    }

    private Map<String, Double> getCollectedAmountInfoForBuyerId(String buyerId) {
        val collectionVouchers = collectionVoucherRepository.getCollectedAmountInfoForBuyerId(buyerId);
        Map<String, Double> res = new HashMap<>();

        for (val collectionVoucher : collectionVouchers) {
            for (val collectionVoucherItem : collectionVoucher.getCollectionVoucherItemEntityList()) {
                if (res.containsKey(collectionVoucherItem.getBill().getBillNo())) {
                    val newValue = res.get(collectionVoucherItem.getBill().getBillNo())
                            + collectionVoucherItem.getAmountCollected();
                    res.put(collectionVoucherItem.getBill().getBillNo(), newValue);
                } else {
                    res.put(collectionVoucherItem.getBill().getBillNo(), collectionVoucherItem.getAmountCollected());
                }
            }
        }

        return res;
    }

    private Double getCollectedAmountForBillNo(String billNo) {
        return collectionVoucherRepository.getCollectedAmountForBillNo(billNo);
    }

    @Nullable
    public CollectionVoucher getVoucherByVoucherNo(String voucherNo) {
        CollectionVoucherEntity collectionVoucherEntity =
                collectionVoucherRepository.getCollectionVoucherByVoucherNo(voucherNo);

        if (collectionVoucherEntity == null) {
            return null;
        }

        val collectionVoucherItemList = collectionVoucherEntity.getCollectionVoucherItemEntityList().stream()
                .map(collectionVoucherItemEntity -> {
                    val billNo = collectionVoucherItemEntity.getBill().getBillNo();
                    Bill bill = billService.getBill(billNo);
                    val supplierName = bill.getSupplierName();
                    val billAmount = bill.getBillAmount();
                    val pendingBillAmount = billAmount - getCollectedAmountForBillNo(billNo);
                    return CollectionVoucherItem.builder()
                            .billNo(billNo)
                            .supplierName(supplierName)
                            .billAmount(billAmount)
                            .pendingBillAmount(pendingBillAmount)
                            .amountCollected(collectionVoucherItemEntity.getAmountCollected())
                            .bank(collectionVoucherItemEntity.getBank())
                            .ddNo(collectionVoucherItemEntity.getDdNo())
                            .ddDate(String.valueOf(collectionVoucherItemEntity.getDdDate()))
                            .build();
                })
                .collect(Collectors.toList());

        return CollectionVoucher.builder()
                .voucherNo(collectionVoucherEntity.getVoucherNo())
                .voucherDate(String.valueOf(collectionVoucherEntity.getVoucherDate()))
                .buyerName(collectionVoucherEntity.getBuyer().getBuyerName())
                .collectionVoucherItemList(collectionVoucherItemList)
                .build();
    }
}
