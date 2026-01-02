package com.razdeep.konsignapi.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "collection_vouchers")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionVoucherEntity extends BaseTimestamp {

    @Id
    private String voucherNo;

    private LocalDate voucherDate;

    @OneToOne
    @JoinColumn(name = "buyer_buyer_id")
    private BuyerEntity buyer;

    @OneToMany(mappedBy = "collectionVoucher", cascade = CascadeType.ALL)
    private List<CollectionVoucherItemEntity> collectionVoucherItemEntityList;
}
