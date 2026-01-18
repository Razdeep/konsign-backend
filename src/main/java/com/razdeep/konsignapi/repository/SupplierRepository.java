package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.SupplierEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, String> {

    List<SupplierEntity> findAllSupplierBySupplierNameAndTenantId(String supplierName, String tenantId);

    List<SupplierEntity> findAllByTenantId(String supplierName);

    Optional<SupplierEntity> findSupplierBySupplierIdAndTenantId(String supplierId, String tenantId);

    @Query("""
        select s.supplierName
        from SupplierEntity s
        where s.supplierId = :supplierId
    """)
    String findSupplierNameBySupplierId(String supplierId);
}
