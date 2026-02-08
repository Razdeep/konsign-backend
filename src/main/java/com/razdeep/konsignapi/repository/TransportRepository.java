package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.TransportEntity;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepository extends JpaRepository<TransportEntity, String> {

    List<TransportEntity> findAllTransportByTransportName(String transportName);

    @NonNull
    List<TransportEntity> findAll();

    Optional<TransportEntity> findByTransportId(String transportId);

    void deleteByTransportId(String transportId);
}
