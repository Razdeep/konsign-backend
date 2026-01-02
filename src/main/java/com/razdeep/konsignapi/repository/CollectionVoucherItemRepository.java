package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.CollectionVoucherItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionVoucherItemRepository extends JpaRepository<CollectionVoucherItemEntity, String> {}
