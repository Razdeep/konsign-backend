package com.razdeep.konsignapi.repository.projection;

import java.time.LocalDate;

public interface BillCollectionProjection {

    String getBillNo();

    LocalDate getBillDate();

    String getBillAmount();

    String getSupplierName();

    String getVoucherNo();

    Double getAmountCollected();

    String getBank();

    String getDdNo();

    LocalDate getDdDate();
}
