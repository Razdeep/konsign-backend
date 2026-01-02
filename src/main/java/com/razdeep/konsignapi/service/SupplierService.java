package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.SupplierEntity;
import com.razdeep.konsignapi.model.Supplier;
import com.razdeep.konsignapi.repository.SupplierRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    @Autowired
    private SupplierService self;

    private final SupplierRepository supplierRepository;

    private final CommonService commonService;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, CommonService commonService) {
        this.supplierRepository = supplierRepository;
        this.commonService = commonService;
    }

    public boolean isSupplierIdTaken(String supplierId) {
        return supplierRepository.findById(supplierId).isPresent();
    }

    public List<Supplier> getSuppliers() {
        String agencyId = commonService.getAgencyId();
        return self.getSupplierByAgencyId(agencyId);
    }

    @Cacheable(value = "getSuppliers", key = "#agencyId")
    public List<Supplier> getSupplierByAgencyId(String agencyId) {
        List<Supplier> result = new ArrayList<>();
        List<SupplierEntity> supplierEntityList = supplierRepository.findAllByAgencyId(agencyId);
        if (supplierEntityList == null) {
            return result;
        }
        supplierEntityList.forEach((supplierEntity) -> result.add(new Supplier(supplierEntity)));
        return result;
    }

    @CacheEvict(value = "getSuppliers", allEntries = true)
    public boolean addSupplier(Supplier supplier) {
        String agencyId = commonService.getAgencyId();
        if (!supplierRepository
                .findAllSupplierBySupplierNameAndAgencyId(supplier.getSupplierName(), agencyId)
                .isEmpty()) {
            return false;
        }
        if (supplier.getSupplierId().isEmpty()) {
            if (supplier.getSupplierName().isEmpty()) {
                return false;
            }
            val baseCandidateSupplierId = commonService.generateInitials(supplier.getSupplierName());
            String candidateSupplierId = baseCandidateSupplierId;
            int attempt = 2;
            while (isSupplierIdTaken(candidateSupplierId)) {
                candidateSupplierId = baseCandidateSupplierId + attempt++;
            }
            supplier.setSupplierId(candidateSupplierId);
        }
        SupplierEntity supplierEntity = new SupplierEntity(supplier);
        supplierEntity.setAgencyId(agencyId);
        supplierRepository.save(supplierEntity);
        return true;
    }

    @CacheEvict(value = "getSuppliers", allEntries = true)
    public boolean deleteSupplier(String supplierId) {
        String agencyId = commonService.getAgencyId();
        boolean wasPresent = supplierRepository
                .findSupplierBySupplierIdAndAgencyId(supplierId, agencyId)
                .isPresent();
        if (wasPresent) {
            supplierRepository.deleteById(supplierId);
        }
        return wasPresent;
    }

    public SupplierEntity getSupplierBySupplierName(String supplierName) {
        String agencyId = commonService.getAgencyId();
        val resultList = supplierRepository.findAllSupplierBySupplierNameAndAgencyId(supplierName, agencyId);
        return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }
}
