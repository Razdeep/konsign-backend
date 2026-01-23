package com.razdeep.konsignapi.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectionVoucherItem {

    String billNo;

    String supplierName;

    BigDecimal billAmount;

    BigDecimal pendingBillAmount;

    BigDecimal amountCollected;

    String bank;

    String ddNo;

    //    @JsonFormat(pattern="yyyy-MM-dd")
    String ddDate;
}
