package com.razdeep.konsignapi.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CollectionVoucher {

    @NotBlank(message = "Voucher number must not be blank")
    @Size(max = 10, message = "Voucher number must be at most 10 characters")
    private String voucherNo;

    @NotNull(message = "Voucher date is required")
    private LocalDate voucherDate;

    @NotBlank(message = "Buyer name must not be blank")
    @Size(max = 30, message = "Buyer name must be at most 30 characters")
    private String buyerName;

    @NotEmpty(message = "At least one voucher item is required")
    @Valid
    private List<CollectionVoucherItem> collectionVoucherItemList;
}
