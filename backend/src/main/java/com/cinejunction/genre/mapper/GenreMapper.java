package com.cinejunction.genre.mapper;

import com.cinejunction.genre.dto.GenreRequest;
import com.cinejunction.genre.dto.GenreResponse;
import com.cinejunction.genre.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting between {@link Genre} entities and genre DTOs.
 */
@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    /**
     * Converts a {@link GenreRequest} to a new {@link Genre} entity.
     *
     * @param request the genre request DTO
     * @return a new Genre entity
     */
    Genre toEntity(GenreRequest request);

    /**
     * Converts a {@link Genre} entity to a {@link GenreResponse} DTO.
     *
     * @param genre the genre entity
     * @return the genre response DTO
     */
    GenreResponse toResponse(Genre genre);
}
