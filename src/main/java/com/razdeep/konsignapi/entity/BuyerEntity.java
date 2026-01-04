package com.razdeep.konsignapi.entity;

import com.razdeep.konsignapi.model.Buyer;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "buyer")
@Getter
@Setter
public class BuyerEntity extends BaseTimestamp {
    @Id
    private String buyerId;

    @NonNull
    private String buyerName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "buyerEntity")
    private List<BillEntity> billEntities;

    public BuyerEntity(Buyer buyer) {
        buyerId = buyer.getBuyerId();
        buyerName = buyer.getBuyerName();
    }

    public BuyerEntity() {}
}
