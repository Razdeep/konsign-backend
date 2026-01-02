package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.TransportEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepository extends JpaRepository<TransportEntity, String> {

    List<TransportEntity> findAllTransportByTransportNameAndAgencyId(String transportName, String agencyId);

    List<TransportEntity> findAllByAgencyId(String agencyId);

    Optional<TransportEntity> findByTransportIdAndAgencyId(String transportId, String agencyId);

    void deleteByTransportIdAndAgencyId(String transportId, String agencyId);
}
