package com.razdeep.konsignapi.mapper;

import com.razdeep.konsignapi.entity.LrPmEntity;
import com.razdeep.konsignapi.model.LrPm;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LrPmMapper {
    LrPm toModel(LrPmEntity entity);

    List<LrPm> toModelList(List<LrPmEntity> entities);
}
