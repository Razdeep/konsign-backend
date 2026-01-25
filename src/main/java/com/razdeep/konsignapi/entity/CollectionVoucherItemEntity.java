package com.razdeep.konsignapi.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "collection_voucher_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionVoucherItemEntity extends BaseEntity {

    @Id
    @Column(name = "collection_voucher_item_id")
    private String collectionVoucherItemId;

    @ManyToOne
    @JoinColumn(name = "fk_collection_voucher_id")
    private CollectionVoucherEntity collectionVoucher;

    @OneToOne
    @JoinColumn(name = "bill_bill_no")
    BillEntity bill;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal amountCollected;

    String bank;

    String ddNo;

    LocalDate ddDate;
}
