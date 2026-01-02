package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.TransportEntity;
import com.razdeep.konsignapi.model.Transport;
import com.razdeep.konsignapi.repository.TransportRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TransportService {

    @Autowired
    private TransportService self;

    private final TransportRepository transportRepository;

    private final CommonService commonService;

    @Autowired
    public TransportService(TransportRepository transportRepository, CommonService commonService) {
        this.transportRepository = transportRepository;
        this.commonService = commonService;
    }

    public boolean isTransportIdTaken(String transportId) {
        return transportRepository.findById(transportId).isPresent();
    }

    @CacheEvict(value = "getTransports", allEntries = true)
    public boolean addTransport(Transport transport) {
        String agencyId = commonService.getAgencyId();

        if (!transportRepository
                .findAllTransportByTransportNameAndAgencyId(transport.getTransportName(), agencyId)
                .isEmpty()) {
            return false;
        }

        if (transport.getTransportId().isEmpty()) {
            if (transport.getTransportName().isEmpty()) {
                return false;
            }
            val baseCandidateTransportId = commonService.generateInitials(transport.getTransportName());
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

        transportEntity.setAgencyId(agencyId);

        transportRepository.save(transportEntity);
        return true;
    }

    public TransportEntity getTransportByTransportName(String transportName) {
        String agencyId = commonService.getAgencyId();
        val resultList = transportRepository.findAllTransportByTransportNameAndAgencyId(transportName, agencyId);
        return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<Transport> getTransports() {
        String agencyId = commonService.getAgencyId();
        return self.getTransports(agencyId);
    }

    @Cacheable(value = "getTransports", key = "#agencyId")
    public List<Transport> getTransports(String agencyId) {
        List<Transport> result = new ArrayList<>();
        transportRepository
                .findAllByAgencyId(agencyId)
                .forEach((transportEntity) -> result.add(
                        new Transport(transportEntity.getTransportId(), transportEntity.getTransportName())));
        return result;
    }

    @CacheEvict(value = "getTransports", allEntries = true)
    public boolean deleteTransport(String transportId) {
        String agencyId = commonService.getAgencyId();
        boolean wasPresent = transportRepository
                .findByTransportIdAndAgencyId(transportId, agencyId)
                .isPresent();
        if (wasPresent) {
            transportRepository.deleteByTransportIdAndAgencyId(transportId, agencyId);
        }
        return wasPresent;
    }
}
