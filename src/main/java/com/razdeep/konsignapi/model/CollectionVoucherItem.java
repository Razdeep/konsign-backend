package com.razdeep.konsignapi.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CollectionVoucherItem {

    @NotBlank(message = "Bill number must not be blank")
    @Size(max = 10, message = "Bill number must be at most 10 characters")
    private String billNo;

    @NotBlank(message = "Supplier name must not be blank")
    @Size(max = 30, message = "Supplier name must be at most 30 characters")
    private String supplierName;

    @NotNull(message = "Bill amount is required")
    @Positive(message = "Bill amount must be greater than zero")
    private BigDecimal billAmount;

    private BigDecimal pendingBillAmount;

    @NotNull(message = "Collected amount is required")
    @PositiveOrZero(message = "Collected amount cannot be negative")
    private BigDecimal amountCollected;

    @Size(max = 10, message = "Bank name must be at most 10 characters")
    private String bank;

    @Size(max = 10, message = "DD number must be at most 10 characters")
    private String ddNo;

    @NotNull(message = "dd date is required")
    private LocalDate ddDate;
}
