package com.razdeep.konsignapi.listener;

import com.razdeep.konsignapi.entity.BaseEntity;
import com.razdeep.konsignapi.tenant.TenantContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;

public class EntityListener {

    @PrePersist
    public void prePersist(BaseEntity entity) {
        Instant now = Instant.now();

        entity.setTenantId(TenantContext.getTenantId());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setUpdatedAt(Instant.now());
    }
}
