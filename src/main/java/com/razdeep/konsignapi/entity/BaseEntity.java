package com.razdeep.konsignapi.entity;

import com.razdeep.konsignapi.listener.EntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(EntityListener.class)
public class BaseEntity {

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "tenant_id", updatable = false, nullable = false)
    private String tenantId;
}
