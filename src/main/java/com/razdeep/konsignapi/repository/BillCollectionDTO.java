package com.razdeep.konsignapi.repository;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillCollectionDTO {
    String billNo;
    LocalDate billDate;
    String billAmount;
    String supplierName;
    String voucherNo;
    Double amountCollected;
    String bank;
    String ddNo;
    LocalDate ddDate;
}
