package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.SupplierEntity;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, String> {

    List<SupplierEntity> findAllSupplierBySupplierName(String supplierName);

    @NonNull
    List<SupplierEntity> findAll();

    Optional<SupplierEntity> findSupplierBySupplierId(String supplierId);

    @Query("""
        select s.supplierName
        from SupplierEntity s
        where s.supplierId = :supplierId
    """)
    String findSupplierNameBySupplierId(String supplierId);
}
