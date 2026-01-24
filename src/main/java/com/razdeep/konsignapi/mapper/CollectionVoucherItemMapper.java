package com.razdeep.konsignapi.mapper;

import com.razdeep.konsignapi.entity.CollectionVoucherItemEntity;
import com.razdeep.konsignapi.model.CollectionVoucherItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {LrPmMapper.class})
public interface CollectionVoucherItemMapper {

    @Mapping(source = "bill.billNo", target = "billNo")
    @Mapping(source = "bill.supplierEntity.supplierName", target = "supplierName")
    @Mapping(source = "bill.billAmount", target = "billAmount")
    CollectionVoucherItem toModel(CollectionVoucherItemEntity entity);

    List<CollectionVoucherItem> toModelList(List<CollectionVoucherItemEntity> entities);

    CollectionVoucherItemEntity toEntity(CollectionVoucherItem model);

    List<CollectionVoucherItemEntity> toEntityList(List<CollectionVoucherItem> model);
}
