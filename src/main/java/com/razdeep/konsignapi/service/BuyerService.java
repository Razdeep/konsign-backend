package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.BuyerEntity;
import com.razdeep.konsignapi.model.Buyer;
import com.razdeep.konsignapi.repository.BuyerRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class BuyerService {

    private final BuyerRepository buyerRepository;

    private final CommonService commonService;

    public BuyerService(BuyerRepository buyerRepository, CommonService commonService) {
        this.buyerRepository = buyerRepository;
        this.commonService = commonService;
    }

    public List<Buyer> getBuyers() {
        String agencyId = commonService.getAgencyId();
        return getBuyersByAgencyId(agencyId);
    }

    //    @Cacheable(value = "getBuyers", key = "#agencyId")
    public List<Buyer> getBuyersByAgencyId(String agencyId) {
        List<Buyer> result = new ArrayList<>();
        buyerRepository.findAllByAgencyId(agencyId).forEach((buyerEntity) -> result.add(new Buyer(buyerEntity)));
        return result;
    }

    private boolean isBuyerIdTaken(String buyerId) {
        return buyerRepository.findById(buyerId).isPresent();
    }

    //    @CacheEvict(value = "getBuyers", allEntries = true)
    public boolean addBuyer(Buyer buyer) {
        String agencyId = commonService.getAgencyId();
        if (!buyerRepository
                .findAllBuyerByBuyerNameAndAgencyId(buyer.getBuyerName(), agencyId)
                .isEmpty()) {
            return false;
        }

        if (buyer.getBuyerId().isEmpty()) {
            if (buyer.getBuyerName().isEmpty()) {
                return false;
            }
            final var baseCandidateBuyerId = commonService.generateInitials(buyer.getBuyerName());
            String candidateBuyerId = baseCandidateBuyerId;
            int attempt = 2;
            while (isBuyerIdTaken(candidateBuyerId)) {
                candidateBuyerId = baseCandidateBuyerId + attempt++;
            }
            buyer.setBuyerId(candidateBuyerId);
        }

        BuyerEntity buyerEntity = new BuyerEntity(buyer);
        buyerEntity.setAgencyId(agencyId);
        buyerRepository.save(buyerEntity);
        return true;
    }

    //    @CacheEvict(value = "getBuyers", allEntries = true)
    public boolean deleteBuyer(String buyerId) {
        String agencyId = commonService.getAgencyId();
        boolean wasPresent =
                buyerRepository.findByBuyerIdAndAgencyId(buyerId, agencyId).isPresent();
        if (wasPresent) {
            buyerRepository.deleteById(buyerId);
        }
        return wasPresent;
    }

    public BuyerEntity getBuyerByBuyerName(String buyerName) {
        String agencyId = commonService.getAgencyId();
        final var resultList = buyerRepository.findAllBuyerByBuyerNameAndAgencyId(buyerName, agencyId);
        return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
    }

    public byte[] generateBuyerLedger(String buyerId) throws Exception {
        String agencyId = commonService.getAgencyId();
        String buyerName = buyerRepository.findBuyerNameByBuyerId(buyerId, agencyId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("buyerName", buyerName);
        return commonService.generatePdf("buyer.ftl", payload);
    }
}
