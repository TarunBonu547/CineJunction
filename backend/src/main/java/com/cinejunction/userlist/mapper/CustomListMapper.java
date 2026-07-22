package com.cinejunction.userlist.mapper;

import com.cinejunction.userlist.dto.request.CustomListRequest;
import com.cinejunction.userlist.dto.response.CustomListResponse;
import com.cinejunction.userlist.entity.CustomList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomListMapper {
    CustomListMapper INSTANCE = Mappers.getMapper(CustomListMapper.class);

    CustomList toEntity(CustomListRequest request);

    CustomListResponse toResponse(CustomList list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CustomListRequest request, @MappingTarget CustomList list);
}
