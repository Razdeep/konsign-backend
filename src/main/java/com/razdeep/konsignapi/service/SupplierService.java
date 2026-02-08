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
        List<Supplier> result = new ArrayList<>();
        List<SupplierEntity> supplierEntityList = supplierRepository.findAll();
        supplierEntityList.forEach((supplierEntity) -> result.add(new Supplier(supplierEntity)));
        return result;
    }

    public boolean addSupplier(Supplier supplier) {
        String agencyId = commonService.getTenantId();
        if (!supplierRepository
                .findAllSupplierBySupplierName(supplier.getSupplierName())
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

    public boolean deleteSupplier(String supplierId) {
        boolean wasPresent =
                supplierRepository.findSupplierBySupplierId(supplierId).isPresent();
        if (wasPresent) {
            supplierRepository.deleteById(supplierId);
        }
        return wasPresent;
    }

    public SupplierEntity getSupplierBySupplierName(String supplierName) {
        final var resultList = supplierRepository.findAllSupplierBySupplierName(supplierName);
        return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }

    public byte[] generateSupplierLedger(String supplierId) throws Exception {
        String supplierName = supplierRepository.findSupplierNameBySupplierId(supplierId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("supplierName", supplierName);

        return commonService.generatePdf("supplier.ftl", payload);
    }
}
