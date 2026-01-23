package com.razdeep.konsignapi.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectionVoucher {

    private String voucherNo;

    private LocalDate voucherDate;

    private String buyerName;

    private List<CollectionVoucherItem> collectionVoucherItemList;
}
