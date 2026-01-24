package com.razdeep.konsignapi.mapper;

import com.razdeep.konsignapi.entity.CollectionVoucherEntity;
import com.razdeep.konsignapi.model.CollectionVoucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {CollectionVoucherItemMapper.class})
public interface CollectionVoucherMapper {

    @Mapping(source = "buyer.buyerName", target = "buyerName")
    @Mapping(source = "collectionVoucherItemEntityList", target = "collectionVoucherItemList")
    CollectionVoucher toModel(CollectionVoucherEntity entity);

    @Mapping(source = "collectionVoucherItemList", target = "collectionVoucherItemEntityList")
    CollectionVoucherEntity toEntity(CollectionVoucher model);
}
