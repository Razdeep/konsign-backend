package com.razdeep.konsignapi.entity;

import com.razdeep.konsignapi.model.Supplier;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "supplier")
@Getter
@Setter
public class SupplierEntity extends BaseTimestamp {
    @Id
    private String supplierId;

    @NonNull
    private String supplierName;

    @OneToMany(mappedBy = "supplierEntity")
    private List<BillEntity> billEntities;

    public SupplierEntity(Supplier supplier) {
        supplierId = supplier.getSupplierId();
        supplierName = supplier.getSupplierName();
    }

    public SupplierEntity() {}
}
