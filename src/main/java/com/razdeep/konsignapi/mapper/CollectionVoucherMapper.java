package com.razdeep.konsignapi.mapper;

import com.razdeep.konsignapi.entity.CollectionVoucherEntity;
import com.razdeep.konsignapi.model.CollectionVoucher;
import java.util.concurrent.atomic.AtomicInteger;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = {CollectionVoucherItemMapper.class})
public interface CollectionVoucherMapper {

    @Mapping(source = "buyer.buyerName", target = "buyerName")
    @Mapping(source = "collectionVoucherItemEntityList", target = "collectionVoucherItemList")
    CollectionVoucher toModel(CollectionVoucherEntity entity);

    @Mapping(source = "collectionVoucherItemList", target = "collectionVoucherItemEntityList")
    CollectionVoucherEntity toEntity(CollectionVoucher model);

    @AfterMapping
    default void linkChildren(@MappingTarget CollectionVoucherEntity collectionVoucherEntity) {

        if (collectionVoucherEntity.getCollectionVoucherItemEntityList() == null) return;

        AtomicInteger collectionVoucherItemIndex = new AtomicInteger();
        collectionVoucherEntity.getCollectionVoucherItemEntityList().forEach(item -> {
            item.setCollectionVoucher(collectionVoucherEntity);
            item.setCollectionVoucherItemId(
                    collectionVoucherEntity.getVoucherNo() + "_" + collectionVoucherItemIndex.getAndIncrement());
        });
    }
}
