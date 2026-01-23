package com.razdeep.konsignapi.mapper;

import com.razdeep.konsignapi.entity.BillEntity;
import com.razdeep.konsignapi.model.Bill;
import com.razdeep.konsignapi.tenant.TenantContext;
import java.util.concurrent.atomic.AtomicInteger;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = {LrPmMapper.class})
public interface BillMapper {

    @Mapping(source = "supplierEntity.supplierName", target = "supplierName")
    @Mapping(source = "buyerEntity.buyerName", target = "buyerName")
    @Mapping(source = "transportEntity.transportName", target = "transportName")
    @Mapping(source = "lrPmEntityList", target = "lrPmList")
    Bill toModel(BillEntity entity);

    @Mapping(source = "lrPmList", target = "lrPmEntityList")
    BillEntity toEntity(Bill bill);

    @AfterMapping
    default void linkChildren(@MappingTarget BillEntity billEntity) {

        // TODO make sure the tenant id is set using an EntityListener
        billEntity.setTenantId(TenantContext.getTenantId());

        if (billEntity.getLrPmEntityList() == null) return;

        AtomicInteger lr_pm_index = new AtomicInteger();
        billEntity.getLrPmEntityList().forEach(lrpm -> {
            lrpm.setBillEntry(billEntity);
            lrpm.setLrPmId(billEntity.getBillNo() + "_" + lr_pm_index.getAndIncrement());
            lrpm.setTenantId(TenantContext.getTenantId());
        });
    }
}
