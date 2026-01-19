package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.SupplierEntity;
import com.razdeep.konsignapi.model.Supplier;
import com.razdeep.konsignapi.repository.SupplierRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    private final CommonService commonService;

    public SupplierService(SupplierRepository supplierRepository, CommonService commonService) {
        this.supplierRepository = supplierRepository;
        this.commonService = commonService;
    }

    public boolean isSupplierIdTaken(String supplierId) {
        return supplierRepository.findById(supplierId).isPresent();
    }

    public List<Supplier> getSuppliers() {
        String agencyId = commonService.getTenantId();
        return getSupplierByAgencyId(agencyId);
    }

    //    @Cacheable(value = "getSuppliers", key = "#agencyId")
    public List<Supplier> getSupplierByAgencyId(String agencyId) {
        List<Supplier> result = new ArrayList<>();
        List<SupplierEntity> supplierEntityList = supplierRepository.findAllByTenantId(agencyId);
        if (supplierEntityList == null) {
            return result;
        }
        supplierEntityList.forEach((supplierEntity) -> result.add(new Supplier(supplierEntity)));
        return result;
    }

    //    @CacheEvict(value = "getSuppliers", allEntries = true)
    public boolean addSupplier(Supplier supplier) {
        String agencyId = commonService.getTenantId();
        if (!supplierRepository
                .findAllSupplierBySupplierNameAndTenantId(supplier.getSupplierName(), agencyId)
                .isEmpty()) {
            return false;
        }
        if (supplier.getSupplierId().isEmpty()) {
            if (supplier.getSupplierName().isEmpty()) {
                return false;
            }
            final var baseCandidateSupplierId = commonService.generateInitials(supplier.getSupplierName());
            String candidateSupplierId = baseCandidateSupplierId;
            int attempt = 2;
            while (isSupplierIdTaken(candidateSupplierId)) {
                candidateSupplierId = baseCandidateSupplierId + attempt++;
            }
            supplier.setSupplierId(candidateSupplierId);
        }
        SupplierEntity supplierEntity = new SupplierEntity(supplier);
        supplierEntity.setTenantId(agencyId);
        supplierRepository.save(supplierEntity);
        return true;
    }

    //    @CacheEvict(value = "getSuppliers", allEntries = true)
    public boolean deleteSupplier(String supplierId) {
        String agencyId = commonService.getTenantId();
        boolean wasPresent = supplierRepository
                .findSupplierBySupplierIdAndTenantId(supplierId, agencyId)
                .isPresent();
        if (wasPresent) {
            supplierRepository.deleteById(supplierId);
        }
        return wasPresent;
    }

    public SupplierEntity getSupplierBySupplierName(String supplierName) {
        String agencyId = commonService.getTenantId();
        final var resultList = supplierRepository.findAllSupplierBySupplierNameAndTenantId(supplierName, agencyId);
        return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }

    // TODO: implement properly later
    public byte[] generateSupplierLedger(String supplierId) throws Exception {
        String supplierName = supplierRepository.findSupplierNameBySupplierId(supplierId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("supplierName", supplierName);

        return commonService.generatePdf("supplier.ftl", payload);
    }
}
