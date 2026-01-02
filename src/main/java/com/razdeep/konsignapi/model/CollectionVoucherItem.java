package com.razdeep.konsignapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectionVoucherItem {

    String billNo;

    String supplierName;

    Double billAmount;

    Double pendingBillAmount;

    Double amountCollected;

    String bank;

    String ddNo;

    //    @JsonFormat(pattern="yyyy-MM-dd")
    String ddDate;
}
