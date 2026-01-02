package com.razdeep.konsignapi.entity;

import java.time.LocalDate;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "collection_voucher_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionVoucherItemEntity extends BaseTimestamp {

    @Id
    @Column(name = "collection_voucher_item_id")
    private String collectionVoucherItemId;

    @ManyToOne
    @JoinColumn(name = "fk_collection_voucher_id")
    private CollectionVoucherEntity collectionVoucher;

    @OneToOne
    @JoinColumn(name = "bill_bill_no")
    BillEntity bill;

    Double amountCollected;

    String bank;

    String ddNo;

    LocalDate ddDate;
}
