package com.razdeep.konsignapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CollectionVoucherItem {

    String billNo;

    String supplierName;

    BigDecimal billAmount;

    BigDecimal pendingBillAmount;

    BigDecimal amountCollected;

    String bank;

    String ddNo;

    LocalDate ddDate;
}
