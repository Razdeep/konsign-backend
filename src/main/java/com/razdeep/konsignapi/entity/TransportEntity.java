package com.razdeep.konsignapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "transport")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransportEntity extends BaseTimestamp {
    @Id
    private String transportId;

    @NonNull
    private String transportName;

    @OneToMany(mappedBy = "transportEntity")
    private List<BillEntity> billEntities;
}
