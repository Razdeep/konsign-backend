package com.razdeep.konsignapi.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "bill")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillEntity extends BaseTimestamp {
    @Id
    private String billNo;

    @ManyToOne
    @JoinColumn(name = "fk_supplier_id", nullable = false)
    private SupplierEntity supplierEntity;

    @ManyToOne
    @JoinColumn(name = "fk_buyer_id", nullable = false)
    private BuyerEntity buyerEntity;

    private LocalDate billDate;

    @ManyToOne
    @JoinColumn(name = "fk_transport_id", nullable = false)
    private TransportEntity transportEntity;

    private LocalDate lrDate;
    private Double billAmount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "billEntry")
    private List<LrPmEntity> lrPmEntityList;
}
