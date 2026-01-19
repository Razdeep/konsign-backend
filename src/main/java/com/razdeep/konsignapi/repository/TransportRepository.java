package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.TransportEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepository extends JpaRepository<TransportEntity, String> {

    List<TransportEntity> findAllTransportByTransportNameAndTenantId(String transportName, String tenantId);

    List<TransportEntity> findAllByTenantId(String tenantId);

    Optional<TransportEntity> findByTransportIdAndTenantId(String transportId, String tenantId);

    void deleteByTransportIdAndTenantId(String transportId, String tenantId);
}
