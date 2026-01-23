package com.razdeep.konsignapi.mapper;

import com.razdeep.konsignapi.entity.CollectionVoucherItemEntity;
import com.razdeep.konsignapi.model.CollectionVoucherItem;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {LrPmMapper.class})
public interface CollectionVoucherItemMapper {

    CollectionVoucherItem toModel(CollectionVoucherItemEntity entity);

    List<CollectionVoucherItem> toEntityList(List<CollectionVoucherItemEntity> entities);
}
