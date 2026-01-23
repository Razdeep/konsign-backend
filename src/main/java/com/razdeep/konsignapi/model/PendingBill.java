package com.razdeep.konsignapi.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingBill {
    String billNo;
    String supplierName;
    String buyerName;
    BigDecimal billAmount;
    BigDecimal pendingAmount;
}
