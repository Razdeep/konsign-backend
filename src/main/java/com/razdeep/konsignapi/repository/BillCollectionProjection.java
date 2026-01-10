package com.razdeep.konsignapi.repository;

import java.time.LocalDate;

public interface BillCollectionProjection {

    String getBillNo();

    LocalDate getBillDate();

    Double getBillAmount();

    String getSupplierName();

    String getVoucherNo();

    Double getAmountCollected();

    String getBank();

    String getDdNo();

    String getDdDate();
}
