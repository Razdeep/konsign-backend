package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.TransportEntity;
import com.razdeep.konsignapi.model.Transport;
import com.razdeep.konsignapi.repository.TransportRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransportService {

    private final TransportRepository transportRepository;

    private final CommonService commonService;

    public TransportService(TransportRepository transportRepository, CommonService commonService) {
        this.transportRepository = transportRepository;
        this.commonService = commonService;
    }

    public boolean isTransportIdTaken(String transportId) {
        return transportRepository.findById(transportId).isPresent();
    }

    //    @CacheEvict(value = "getTransports", allEntries = true)
    public boolean addTransport(Transport transport) {
        String agencyId = commonService.getTenantId();

        if (!transportRepository
                .findAllTransportByTransportNameAndTenantId(transport.getTransportName(), agencyId)
                .isEmpty()) {
            return false;
        }

        if (transport.getTransportId().isEmpty()) {
            if (transport.getTransportName().isEmpty()) {
                return false;
            }
            final var baseCandidateTransportId = commonService.generateInitials(transport.getTransportName());
            String candidateTransportId = baseCandidateTransportId;
            int attempt = 2;
            while (isTransportIdTaken(candidateTransportId)) {
                candidateTransportId = baseCandidateTransportId + attempt++;
            }
            transport.setTransportId(candidateTransportId);
        }

        TransportEntity transportEntity = TransportEntity.builder()
                .transportId(transport.getTransportId())
                .transportName(transport.getTransportName())
                .build();

        transportEntity.setTenantId(agencyId);

        transportRepository.save(transportEntity);
        return true;
    }

    public TransportEntity getTransportByTransportName(String transportName) {
        String agencyId = commonService.getTenantId();
        final var resultList = transportRepository.findAllTransportByTransportNameAndTenantId(transportName, agencyId);
        return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<Transport> getTransports() {
        String agencyId = commonService.getTenantId();
        return getTransports(agencyId);
    }

    //    @Cacheable(value = "getTransports", key = "#agencyId")
    public List<Transport> getTransports(String agencyId) {
        List<Transport> result = new ArrayList<>();
        transportRepository
                .findAllByTenantId(agencyId)
                .forEach((transportEntity) -> result.add(
                        new Transport(transportEntity.getTransportId(), transportEntity.getTransportName())));
        return result;
    }

    //    @CacheEvict(value = "getTransports", allEntries = true)
    public boolean deleteTransport(String transportId) {
        String agencyId = commonService.getTenantId();
        boolean wasPresent = transportRepository
                .findByTransportIdAndTenantId(transportId, agencyId)
                .isPresent();
        if (wasPresent) {
            transportRepository.deleteByTransportIdAndTenantId(transportId, agencyId);
        }
        return wasPresent;
    }
}
