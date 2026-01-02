package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.BillEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BillEntryRepository extends JpaRepository<BillEntity, String> {

    @Query(value = "select billEntity from BillEntity billEntity where billEntity.buyerEntity.buyerId = ?1")
    List<BillEntity> findAllBillsByBuyerId(String buyerId);

    Optional<BillEntity> findByBillNoAndAgencyId(String billNo, String agencyId);

    Page<BillEntity> findByAgencyId(String agencyId, Pageable pageable);
}
