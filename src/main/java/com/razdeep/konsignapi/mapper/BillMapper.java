package com.razdeep.konsignapi.mapper;

import com.razdeep.konsignapi.entity.BillEntity;
import com.razdeep.konsignapi.model.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {LrPmMapper.class})
public interface BillMapper {

    @Mapping(source = "supplierEntity.supplierName", target = "supplierName")
    @Mapping(source = "buyerEntity.buyerName", target = "buyerName")
    @Mapping(source = "transportEntity.transportName", target = "transportName")
    @Mapping(source = "lrPmEntityList", target = "lrPmList")
    Bill toModel(BillEntity entity);
}
