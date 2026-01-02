package com.razdeep.konsignapi.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
